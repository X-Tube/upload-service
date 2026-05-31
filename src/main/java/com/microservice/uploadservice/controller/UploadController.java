package com.microservice.uploadservice.controller;

import com.microservice.uploadservice.application.usecases.UploadUseCase;
import com.microservice.uploadservice.controller.dtos.requests.CompleteUploadRequest;
import com.microservice.uploadservice.controller.dtos.requests.UploadRequest;
import com.microservice.uploadservice.controller.dtos.resposes.UploadResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadUseCase uploadUseCase;

    @PostMapping("/new")
    public ResponseEntity<UploadResponse> uploadVideo(
            @RequestBody UploadRequest request,
            @RequestHeader("user-id") Long userId
    ) {
        return new ResponseEntity<>(uploadUseCase.startUpload(request, userId), HttpStatus.CREATED);
    }

    @PostMapping("/complete/{videoId}/video")
    public ResponseEntity<Void> completeUpload(
            @PathVariable String videoId,
            @RequestBody CompleteUploadRequest request,
            @RequestHeader("user-id") Long userId
    ) {
        uploadUseCase.completeUpload(videoId, userId, request);

        return ResponseEntity.ok().build();
    }
}
