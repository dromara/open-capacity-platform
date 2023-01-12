package com.open.capacity;

import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding({MessageSink.class})
public class StreamsConfig {
}