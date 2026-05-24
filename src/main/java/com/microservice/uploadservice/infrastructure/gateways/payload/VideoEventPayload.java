package com.microservice.uploadservice.infrastructure.gateways.payload;

import com.microservice.uploadservice.domain.enums.VideoStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record VideoEventPayload(
        UUID id,
        Long author,
        String title,
        String description,
        VideoStatus videoStatus,
        Long duration,
        Long size
) {
}
