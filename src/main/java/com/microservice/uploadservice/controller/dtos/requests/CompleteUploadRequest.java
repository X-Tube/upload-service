package com.microservice.uploadservice.controller.dtos.requests;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CompleteUploadRequest(
        @NotNull String uploadId,
        @NotNull List<PartRequest> parts
) {}