package com.microservice.uploadservice;

import com.microservice.uploadservice.application.gateways.StorageGateway;
import com.microservice.uploadservice.domain.MultiPartUpload;
import com.microservice.uploadservice.domain.PartUpload;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class StorageGatewayIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private StorageGateway storageGateway;

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.buckets.video-input}")
    private String videoBucket;

    @Value("${aws.s3.buckets.thumbnail-input}")
    private String thumbnailBucket;

    @Test
    void shouldGeneratePresignedThumbnailUrlAndLog() {
        UUID videoId = UUID.randomUUID();

        String presignedUrl = storageGateway.generateThumbnailUrl(videoId);

        assertNotNull(presignedUrl);
        log.info("[TEST] Generated Presigned Thumbnail URL: {}", presignedUrl);
    }

    @Test
    void shouldInitiateMultipartUploadAndLog() {
        UUID videoId = UUID.randomUUID();
        int totalParts = 3;

        MultiPartUpload multiPartUpload = storageGateway.initiateMultipartUpload(videoId, totalParts);

        assertNotNull(multiPartUpload);
        log.info("[TEST] Initiated Multipart Upload. ID: {}", multiPartUpload.getUploadId());

        multiPartUpload.getPartUrls().forEach(url ->
                log.info("[TEST] Generated Part URL: {}", url)
        );

        assertEquals(totalParts, multiPartUpload.getPartUrls().size());
    }

    @Test
    void shouldCompleteMultipartUploadLocally() {
        UUID videoId = UUID.randomUUID();
        String key = videoId.toString();
        int totalParts = 3;

        MultiPartUpload multiPartUpload = storageGateway.initiateMultipartUpload(videoId, totalParts);
        String uploadId = multiPartUpload.getUploadId();
        log.info("[TEST] Starting manual upload flow for ID: {}", uploadId);

        List<PartUpload> completedParts = new ArrayList<>();
        byte[] fiveMegabytesOfData = new byte[5 * 1024 * 1024];

        for (int i = 1; i <= totalParts; i++) {
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(videoBucket)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumber(i)
                    .build();

            UploadPartResponse partResponse = s3Client.uploadPart(
                    uploadPartRequest,
                    RequestBody.fromBytes(fiveMegabytesOfData)
            );

            String eTag = partResponse.eTag();
            log.info("[TEST] Part {} (5MB) uploaded directly. Captured S3 ETag: {}", i, eTag);

            completedParts.add(new PartUpload(i, eTag));
        }

        log.info("[TEST] Requesting final assembly stitching from gateway...");
        assertDoesNotThrow(() ->
                storageGateway.completeMultipartUpload(key, uploadId, completedParts)
        );

        HeadObjectResponse headResponse = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(videoBucket)
                .key(key)
                .build());

        assertNotNull(headResponse);
        log.info("[TEST] Multipart flow complete! Final object verified in bucket size: {} bytes", headResponse.contentLength());
    }
}