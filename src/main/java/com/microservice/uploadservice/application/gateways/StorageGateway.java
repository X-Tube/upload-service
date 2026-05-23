package com.microservice.uploadservice.application.gateways;

import com.microservice.uploadservice.domain.MultiPartUpload;
import com.microservice.uploadservice.domain.PartUpload;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StorageGateway {
     String generateThumbnailUrl(UUID videoId);

     MultiPartUpload initiateMultipartUpload(UUID videoId, int totalParts);

     void completeMultipartUpload(String videoId, String uploadId, List<PartUpload> parts);
}
