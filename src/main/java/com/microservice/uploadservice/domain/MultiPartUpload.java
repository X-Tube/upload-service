package com.microservice.uploadservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultiPartUpload {

    private String uploadId;

    private List<String> partUrls;
}
