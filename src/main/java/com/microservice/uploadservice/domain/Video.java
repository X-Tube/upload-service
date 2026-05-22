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

    UUID id;

    Long author;

    String title;

    String description;

    VideoStatus videoStatus;

    Long duration;

    Long size;
}
