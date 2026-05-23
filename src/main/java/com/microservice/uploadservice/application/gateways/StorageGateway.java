package com.microservice.uploadservice.application.gateways;

import com.microservice.uploadservice.domain.MultiPartUpload;

import java.util.Map;
import java.util.UUID;

public interface StorageGateway {
     String generateThumbnailUrl(UUID videoId);

     MultiPartUpload initiateMultipartUpload(UUID videoId, int totalParts);
}
