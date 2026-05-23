package com.microservice.uploadservice.controller.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardError {
    private LocalDate timestamp;
    private Integer status;
    private String error;
    private String path;

}
