package com.microservice.uploadservice.controller.dtos.resposes;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UploadResponse(
        UUID videoId,
        MultiPartUploadResponse videoMultiPartURL,
        String thumbnailURL
) {
}
