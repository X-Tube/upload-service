package com.microservice.uploadservice.domain;

import com.microservice.uploadservice.domain.enums.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Video {

    private UUID id;

    private Long author;

    private String title;

    private String description;

    private VideoStatus videoStatus;

    private Long duration;

    private Long size;
}
