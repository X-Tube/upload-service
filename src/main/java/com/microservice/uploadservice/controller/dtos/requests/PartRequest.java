package com.microservice.uploadservice.controller.dtos.requests;

public record PartRequest(
        Integer partNumber,
        String eTag
) {
}
