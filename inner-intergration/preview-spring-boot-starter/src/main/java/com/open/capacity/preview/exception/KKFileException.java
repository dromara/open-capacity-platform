package com.open.capacity.preview.exception;

/**
 * <p>
 * 基本异常
 * <p/>
 *
 * @author luowj
 * @version 1.0
 * @since 2022/7/13 18:18
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 */
public class KKFileException extends RuntimeException {

    /**
     * 异常信息
     */
    private final String message;

    public KKFileException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
