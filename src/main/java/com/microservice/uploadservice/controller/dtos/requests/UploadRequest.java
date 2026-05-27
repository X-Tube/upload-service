package com.microservice.uploadservice.controller.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record UploadRequest(
        @NotNull String title,
        @NotNull String description,
        @NotNull Long duration,
        @NotNull Long size,
        @NotNull int totalParts
) {
}
