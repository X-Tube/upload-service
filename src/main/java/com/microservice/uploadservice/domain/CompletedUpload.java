package com.microservice.uploadservice.domain;

import com.microservice.uploadservice.controller.dtos.requests.PartRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompletedUpload {
    private String uploadId;

    private List<PartUpload> parts;
}
