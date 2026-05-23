package com.microservice.uploadservice.application.gateways;

import com.microservice.uploadservice.domain.Video;

public interface MessageProducer {
    void sendEvent(Video video);
}
