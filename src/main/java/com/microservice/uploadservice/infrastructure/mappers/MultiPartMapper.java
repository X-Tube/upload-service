package com.microservice.uploadservice.infrastructure.mappers;

import com.microservice.uploadservice.controller.dtos.requests.CompleteUploadRequest;
import com.microservice.uploadservice.controller.dtos.requests.PartRequest;
import com.microservice.uploadservice.controller.dtos.resposes.MultiPartUploadResponse;
import com.microservice.uploadservice.domain.CompletedUpload;
import com.microservice.uploadservice.domain.MultiPartUpload;
import com.microservice.uploadservice.domain.PartUpload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MultiPartMapper {

    MultiPartUploadResponse domainToResponse(MultiPartUpload multiPartUpload);

    CompletedUpload requestToDomain(CompleteUploadRequest completeUploadRequest);

    PartUpload requestToDomain(PartRequest partRequest);
}
