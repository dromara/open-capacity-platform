package com.open.capacity.gateway.error;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

/**
 * 异常抽象
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@SuppressWarnings("serial")
public final class CustomerThrowableProblem extends AbstractThrowableProblem {

	private String msg;

	protected CustomerThrowableProblem(@Nullable final URI type, @Nullable final String title,
			@Nullable final StatusType status) {
		super(type, title, status, null);
	}

	public CustomerThrowableProblem(@Nullable final URI type, @Nullable final String title, @Nullable final StatusType status,
			@Nullable final String detail, @Nullable final URI instance, @Nullable final ThrowableProblem cause) {
		super(type, title, status, detail, instance, cause);
	}

	public CustomerThrowableProblem(@Nullable final URI type, @Nullable final String title, @Nullable final StatusType status,
			@Nullable final String detail, @Nullable final URI instance, @Nullable final ThrowableProblem cause,
			@Nullable final Map<String, Object> parameters) {
		super(type, title, status, detail, instance, cause, parameters);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
