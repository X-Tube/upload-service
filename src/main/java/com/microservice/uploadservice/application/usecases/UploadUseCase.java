package com.microservice.uploadservice.application.usecases;

import com.microservice.uploadservice.application.gateways.MessageProducer;
import com.microservice.uploadservice.application.gateways.StorageGateway;
import com.microservice.uploadservice.controller.dtos.requests.UploadRequest;
import com.microservice.uploadservice.controller.dtos.resposes.UploadResponse;
import com.microservice.uploadservice.domain.Video;
import com.microservice.uploadservice.domain.enums.VideoStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadUseCase {

    private final StorageGateway storageGateway;
    private final MessageProducer messageProducer;

    public UploadResponse startUpload(UploadRequest request, Long senderId) {
        UUID videoId = UUID.randomUUID();
        Map<String, String> presignedUrls = storageGateway.generateUploadPresignedUrls(videoId);

        Video video = Video.builder()
                .id(videoId)
                .author(senderId)
                .title(request.title())
                .description(request.description())
                .videoStatus(VideoStatus.UPLOADING)
                .duration(request.duration())
                .size(request.size())
                .build();

        messageProducer.sendEvent(video);

        return UploadResponse.builder()
                .videoId(videoId)
                .videoURL(presignedUrls.get("VIDEO_KEY"))
                .thumbnailURL(presignedUrls.get("THUMBNAIL_KEY"))
                .build();
    }
}
