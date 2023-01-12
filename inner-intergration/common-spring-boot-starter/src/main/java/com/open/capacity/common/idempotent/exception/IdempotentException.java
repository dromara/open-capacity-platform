package com.open.capacity.common.idempotent.exception;

/**
 * @author someday
 * @date 2018/9/25
 * Idempotent Exception If there is a custom global exception, you need to inherit the
 * custom global exception.
 */
public class IdempotentException extends RuntimeException {

	public IdempotentException() {
		super();
	}

	public IdempotentException(String message) {
		super(message);
	}

	public IdempotentException(String message, Throwable cause) {
		super(message, cause);
	}

	public IdempotentException(Throwable cause) {
		super(cause);
	}

	protected IdempotentException(String message, Throwable cause, boolean enableSuppression,
								  boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
