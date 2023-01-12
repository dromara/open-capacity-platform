package com.open.capacity.gateway.event;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RequestEvent implements Serializable {

    /**
	 *
	 */
	private static final long serialVersionUID = 3191279827674743906L;
	private String method;
    private String path;
    private long timestamp;

}