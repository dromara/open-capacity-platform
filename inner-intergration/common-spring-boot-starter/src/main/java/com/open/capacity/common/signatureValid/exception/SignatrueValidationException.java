package com.open.capacity.common.signatureValid.exception;

/**
 * @author lzw
 * @description
 * @date 2023/2/22 8:18
 */
public class SignatrueValidationException extends RuntimeException {

    public SignatrueValidationException() {
        super();
    }

    public SignatrueValidationException(String message) {
        super(message);
    }

    public SignatrueValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatrueValidationException(Throwable cause) {
        super(cause);
    }

    protected SignatrueValidationException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
