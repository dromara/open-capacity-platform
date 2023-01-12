package com.open.capacity.common.utils;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.dto.ResponseEntity;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author someday
 * @date 2018/12/20
 */
@Slf4j
@UtilityClass
public class ResponseUtil {

	/**
	 * 通过流写到前端
	 *
	 * @param objectMapper 对象序列化
	 * @param response
	 * @param msg          返回信息
	 * @param httpStatus   返回状态码
	 * @throws IOException
	 */
	public  void responseWriter(ObjectMapper objectMapper, HttpServletResponse response, String msg,
			int httpStatus) throws IOException {
		ResponseEntity result = ResponseEntity.of( httpStatus, msg,null);
		responseWrite(objectMapper, response, result);
	}

	/**
	 * 通过流写到前端
	 * 
	 * @param objectMapper 对象序列化
	 * @param response
	 * @param obj
	 */
	public  void responseSucceed(ObjectMapper objectMapper, HttpServletResponse response, Object obj)
			throws IOException {
		ResponseEntity result = ResponseEntity.succeed(obj);
		responseWrite(objectMapper, response, result);
	}

	/**
	 * 通过流写到前端
	 * 
	 * @param objectMapper
	 * @param response
	 * @param msg
	 * @throws IOException
	 */
	public  void responseFailed(ObjectMapper objectMapper, HttpServletResponse response, String msg)
			throws IOException {
		ResponseEntity result = ResponseEntity.failed(msg);
		responseWrite(objectMapper, response, result);
	}

	private  void responseWrite(ObjectMapper objectMapper, HttpServletResponse response, ResponseEntity result)
			throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		try (Writer writer = response.getWriter()) {
			writer.write(objectMapper.writeValueAsString(result));
			writer.flush();
		}
	}

	/**
	 * 发送文本。使用UTF-8编码。
	 * 
	 * @param response HttpServletResponse
	 * @param text     发送的字符串
	 */
	public  void renderText(HttpServletResponse response, String text) {
		render(response, "text/plain;charset=UTF-8", text);
	}

	/**
	 * 发送json。使用UTF-8编码。
	 * 
	 * @param response HttpServletResponse
	 * @param text     发送的字符串
	 */
	public  void renderJson(HttpServletResponse response, Object obj) {
		try {
			ObjectMapper objectMapper = SpringUtil.getBean(ObjectMapper.class);
			render(response, "application/json;charset=UTF-8", objectMapper.writeValueAsString(obj));
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
		}
	}

	public  void renderJsonError(HttpServletResponse response, Object obj, int httpStatus) {
		try {
			response.setStatus(httpStatus);
			ObjectMapper objectMapper = SpringUtil.getBean(ObjectMapper.class);
			render(response, "application/json;charset=UTF-8", objectMapper.writeValueAsString(obj));
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 发送xml。使用UTF-8编码。
	 * 
	 * @param response HttpServletResponse
	 * @param text     发送的字符串
	 */
	public  void renderXml(HttpServletResponse response, String text) {
		render(response, "text/xml;charset=UTF-8", text);
	}

	/**
	 * 发送内容。使用UTF-8编码。
	 * 
	 * @param response
	 * @param contentType
	 * @param text
	 */
	public  void render(HttpServletResponse response, String contentType, String text) {
		response.setContentType(contentType);
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		try (PrintWriter out = response.getWriter()) {
			out.write(text);
			out.flush();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
