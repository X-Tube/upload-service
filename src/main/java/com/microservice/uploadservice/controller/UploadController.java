package com.microservice.uploadservice.controller;

import com.microservice.uploadservice.application.usecases.UploadUseCase;
import com.microservice.uploadservice.controller.dtos.requests.UploadRequest;
import com.microservice.uploadservice.controller.dtos.resposes.UploadResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadUseCase uploadUseCase;

    @PostMapping("/new")
    public ResponseEntity<UploadResponse> uploadVideo(
            @RequestBody UploadRequest request,
            HttpServletRequest http
    ) {
        Long userId = Long.parseLong(http.getHeader("user-id"));

        return new ResponseEntity<>(uploadUseCase.startUpload(request, userId), HttpStatus.CREATED);
    }
}
