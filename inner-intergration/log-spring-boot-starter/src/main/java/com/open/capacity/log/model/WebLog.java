package com.open.capacity.log.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.Data;

@Data
@Component
@RequestScope
@ConditionalOnClass({HttpServletRequest.class})
public class WebLog {

	private String path;
	private Map<String, String[]> params;
	private Object req;
	private Object resp;

}
