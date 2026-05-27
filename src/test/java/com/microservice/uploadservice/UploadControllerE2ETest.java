package com.microservice.uploadservice;

import com.microservice.uploadservice.controller.dtos.requests.CompleteUploadRequest;
import com.microservice.uploadservice.controller.dtos.requests.PartRequest;
import com.microservice.uploadservice.controller.dtos.requests.UploadRequest;
import com.microservice.uploadservice.controller.dtos.resposes.UploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UploadControllerE2ETest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private final RestTemplate externalRestTemplate = new RestTemplate();

    @Test
    void shouldExecuteFullMultipartUploadLifecycle() {
        log.info("[E2E TEST] Starting full multipart upload orchestration test...");

        UploadRequest initRequest = new UploadRequest(
                "Test Video Title",
                "Test Description",
                120L,
                5242880L,
                5
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("user-Id", "1");

        HttpEntity<UploadRequest> apiRequestEntity = new HttpEntity<>(initRequest, headers);

        log.info("[E2E TEST] Sending initialization request to /uploads/new...");
        ResponseEntity<UploadResponse> initResponse = testRestTemplate.exchange(
                "/uploads/new",
                HttpMethod.POST,
                apiRequestEntity,
                UploadResponse.class
        );

        assertEquals(201, initResponse.getStatusCodeValue(), "Expected HTTP 201 Created");
        UploadResponse payload = initResponse.getBody();

        assertNotNull(payload);
        assertNotNull(payload.videoId());
        assertNotNull(payload.thumbnailURL());

        String uploadId = payload.videoMultiPartURL().uploadId();
        List<String> presignedUrls = payload.videoMultiPartURL().partUrls();

        log.info("[E2E TEST] Initialization successful! Video ID: {}, Upload ID: {}", payload.videoId(), uploadId);
        assertEquals(5, presignedUrls.size(), "Should return exactly 5 presigned URLs");

        byte[] fiveMegabytesOfData = new byte[5 * 1024 * 1024];
        List<PartRequest> harvestedETags = new ArrayList<>();

        log.info("[E2E TEST] Beginning chunk uploads to LocalStack S3...");
        for (int i = 0; i < presignedUrls.size(); i++) {
            String presignedUrl = presignedUrls.get(i);

            URI s3Uri = URI.create(presignedUrl);

            HttpEntity<byte[]> chunkEntity = new HttpEntity<>(fiveMegabytesOfData);

            log.info("[E2E TEST] Uploading Part {} to S3...", (i + 1));
            ResponseEntity<Void> s3Response = externalRestTemplate.exchange(
                    s3Uri,
                    HttpMethod.PUT,
                    chunkEntity,
                    Void.class
            );

            String eTag = s3Response.getHeaders().getETag();
            assertNotNull(eTag, "S3 should return an ETag for part " + (i + 1));
            log.info("[E2E TEST] Part {} uploaded successfully! ETag: {}", (i + 1), eTag);

            harvestedETags.add(new PartRequest(i + 1, eTag));
        }

        CompleteUploadRequest completeReq = new CompleteUploadRequest(
                uploadId,
                harvestedETags
        );

        HttpEntity<CompleteUploadRequest> completeApiRequestEntity = new HttpEntity<>(completeReq, headers);

        log.info("[E2E TEST] All parts uploaded. Sending completion request to API...");

        ResponseEntity<Void> completeResponse = testRestTemplate.exchange(
                "/uploads/complete/" + payload.videoId() + "/video",
                HttpMethod.POST,
                completeApiRequestEntity,
                Void.class
        );

        assertEquals(200, completeResponse.getStatusCodeValue(), "The orchestration should succeed!");
        log.info("[E2E TEST] Multipart upload orchestration perfectly completed!");
    }
}