package com.open.capacity.common.watermark.exception;

/**
 * 水印异常
 * @author owen
 * @date 2022/09/29 14:59:25
 */
public class WatermarkException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7350954869173100377L;

	public WatermarkException(String msg) {
		super(msg);
	}
	
	public WatermarkException(String msg, Exception e) {
		super(msg, e);
	}

	public WatermarkException(String msg, Throwable t) {
		super(msg, t);
	}
}
