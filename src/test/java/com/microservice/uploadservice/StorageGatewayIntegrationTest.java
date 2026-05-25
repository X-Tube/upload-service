package com.microservice.uploadservice;

import com.microservice.uploadservice.application.exceptions.S3ServerException;
import com.microservice.uploadservice.application.gateways.StorageGateway;
import com.microservice.uploadservice.domain.MultiPartUpload;
import com.microservice.uploadservice.domain.PartUpload;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@Slf4j
public class StorageGatewayIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private StorageGateway storageGateway;

    @MockitoSpyBean
    private S3Client s3Client;

    @MockitoSpyBean
    private S3Presigner s3Presigner;

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

    @Test
    void shouldThrowS3ServerExceptionWhenThumbnailPresignFails() {
        UUID videoId = UUID.randomUUID();

        S3ServerException mockAwsException = new S3ServerException("AWS Presigner Timeout");

        doThrow(mockAwsException).when(s3Presigner).presignPutObject(any(PutObjectPresignRequest.class));

        S3ServerException exception = assertThrows(S3ServerException.class, () ->
                storageGateway.generateThumbnailUrl(videoId)
        );

        assertTrue(exception.getMessage().contains("AWS Presigner Timeout"));
        log.error("[TEST] Successfully caught simulated thumbnail exception: {}", exception.getMessage());
    }

    @Test
    void shouldThrowS3ServerExceptionWhenInitiateMultipartFails() {
        UUID videoId = UUID.randomUUID();
        S3ServerException mockAwsException = new S3ServerException("Access Denied to Bucket");

        doThrow(mockAwsException).when(s3Client).createMultipartUpload(any(CreateMultipartUploadRequest.class));

        S3ServerException exception = assertThrows(S3ServerException.class, () ->
                storageGateway.initiateMultipartUpload(videoId, 3)
        );

        assertTrue(exception.getMessage().contains("Access Denied to Bucket"));
        log.error("[TEST] Successfully caught simulated initiate multipart exception: {}", exception.getMessage());
    }

    @Test
    void shouldThrowS3ServerExceptionWhenCompleteMultipartFails() {
        String videoId = UUID.randomUUID().toString();
        String uploadId = "fake-upload-id";
        List<PartUpload> parts = List.of(new PartUpload(1, "\"fake-etag\""));

        S3ServerException mockAwsException = new S3ServerException("Invalid Part Order");

        doThrow(mockAwsException).when(s3Client).completeMultipartUpload(any(CompleteMultipartUploadRequest.class));

        S3ServerException exception = assertThrows(S3ServerException.class, () ->
                storageGateway.completeMultipartUpload(videoId, uploadId, parts)
        );

        assertTrue(exception.getMessage().contains("Invalid Part Order"));
        log.error("[TEST] Successfully caught simulated complete multipart exception: {}", exception.getMessage());
    }
}