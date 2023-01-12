package com.open.capacity;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MessageSink {

    @Input("waterLevelInput")
    SubscribableChannel waterLevelInput();

    @Input("temperatureInput")
    SubscribableChannel temperatureInput();
    
    @Input("temperatureInputError")
    SubscribableChannel temperatureInputError();
    

}
