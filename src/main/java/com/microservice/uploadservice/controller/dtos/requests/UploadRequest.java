package com.microservice.uploadservice.controller.dtos.requests;

public record UploadRequest(
        String title,
        String description,
        Long duration,
        Long size,
        int totalParts
) {
}
