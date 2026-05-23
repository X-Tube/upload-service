package com.microservice.uploadservice.controller.dtos.requests;

import java.util.List;

public record CompleteUploadRequest(
        String uploadId,
        List<PartRequest> parts
) {}