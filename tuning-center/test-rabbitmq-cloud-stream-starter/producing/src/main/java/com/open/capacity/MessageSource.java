package com.open.capacity;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MessageSource {

    @Output("packetUplinkOutput")
    MessageChannel packetUplinkOutput();

}
