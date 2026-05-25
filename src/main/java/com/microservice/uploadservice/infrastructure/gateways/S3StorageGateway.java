package com.microservice.uploadservice.infrastructure.gateways;

import com.microservice.uploadservice.application.exceptions.S3ServerException;
import com.microservice.uploadservice.application.gateways.StorageGateway;
import com.microservice.uploadservice.domain.MultiPartUpload;
import com.microservice.uploadservice.domain.PartUpload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3StorageGateway implements StorageGateway {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${aws.s3.buckets.video-input}")
    private String videoBucket;

    @Value("${aws.s3.buckets.thumbnail-input}")
    private String thumbnailBucket;

    @Value("${aws.s3.buckets.duration}")
    private int duration;

    @Override
    public String generateThumbnailUrl(UUID videoId) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest
                    .builder()
                    .bucket(thumbnailBucket)
                    .key(videoId.toString())
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest
                    .builder()
                    .signatureDuration(Duration.ofMinutes(duration))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedPutRequest = s3Presigner.presignPutObject(presignRequest);

            return presignedPutRequest.url().toString();
        } catch (S3Exception ex) {
            throw throwS3Exceptions(ex);
        }
    }

    @Override
    public MultiPartUpload initiateMultipartUpload(UUID videoId, int totalParts) {
        try {
            CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                    .bucket(videoBucket)
                    .key(videoId.toString())
                    .build();

            String uploadId = s3Client.createMultipartUpload(createRequest).uploadId();

            List<String> partUrls = new ArrayList<>();
            for (int i = 1; i <= totalParts; i++) {
                partUrls.add(generatePartUrl(videoId.toString(), uploadId, i));
            }

            return MultiPartUpload.builder()
                    .uploadId(uploadId)
                    .partUrls(partUrls)
                    .build();
        } catch (S3Exception ex) {
            throw throwS3Exceptions(ex);
        }
    }

    @Override
    public void completeMultipartUpload(String videoId, String uploadId, List<PartUpload> parts) {
        try {
            List<CompletedPart> completedParts = parts.stream()
                    .map(part -> CompletedPart.builder()
                            .partNumber(part.getPartNumber())
                            .eTag(part.getETag())
                            .build())
                    .toList();

            CompleteMultipartUploadRequest request = CompleteMultipartUploadRequest.builder()
                    .bucket(videoBucket)
                    .key(videoId)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                    .build();

            s3Client.completeMultipartUpload(request);
        } catch (S3Exception ex) {
            throw throwS3Exceptions(ex);
        }
    }

    private String generatePartUrl(String key, String uploadId, int partNumber) {
        UploadPartRequest partRequest = UploadPartRequest.builder()
                .bucket(videoBucket)
                .key(key)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();

        UploadPartPresignRequest presignRequest = UploadPartPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .uploadPartRequest(partRequest)
                .build();

        return s3Presigner.presignUploadPart(presignRequest).url().toString();
    }

    private S3ServerException throwS3Exceptions(S3Exception ex) {
        return new S3ServerException("Error while connecting to S3: " + ex.getMessage());
    }
}
