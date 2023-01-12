package com.open.capacity.common.encryptbody.bean;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.InputStream;

/**
 * <p>解密信息输入流</p>
 * @author licoy.cn
 * @version 2018/9/7
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@NoArgsConstructor
@AllArgsConstructor
public class DecryptHttpInputMessage implements HttpInputMessage {

    private InputStream body;

    private HttpHeaders headers;

	@Override
	public InputStream getBody() {
		return body;
	}

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
