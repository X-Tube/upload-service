package com.microservice.uploadservice.infrastructure.mappers;

import com.microservice.uploadservice.controller.dtos.resposes.MultiPartUploadResponse;
import com.microservice.uploadservice.domain.MultiPartUpload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MultiPartMapper {

    MultiPartUploadResponse domainToResponse(MultiPartUpload multiPartUpload);
}
