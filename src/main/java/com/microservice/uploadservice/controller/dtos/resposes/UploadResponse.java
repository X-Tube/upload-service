package com.microservice.uploadservice.controller.dtos.resposes;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UploadResponse(
        UUID videoId,
        String videoURL,
        String thumbnailURL
) {
}
