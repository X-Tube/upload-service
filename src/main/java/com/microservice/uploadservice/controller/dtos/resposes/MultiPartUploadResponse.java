package com.microservice.uploadservice.controller.dtos.resposes;

import lombok.Builder;

import java.util.List;

@Builder
public record MultiPartUploadResponse(
        String uploadId,
        List<String> partUrls
) {
}
