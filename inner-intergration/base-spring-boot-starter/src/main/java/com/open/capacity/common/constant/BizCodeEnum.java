package com.open.capacity.common.constant;

import java.util.Optional;

/**
 * 业务响应码定义
 * @Author: someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */

public enum BizCodeEnum implements BaseEnum<BizCodeEnum> {
	/**
	 * 整个系统通用编码 xx_xx_xxxx (服务标识_业务_错误编号，便于错误快速定位
	 */
	Success(0, "操作成功"), Fail(1, "操作失败"), NotFindError(10001, "未查询到信息"), SaveError(10002, "保存信息失败"),
	UpdateError(10003, "更新信息失败"), ValidateError(10004, "数据检验失败"), SystemError(10007, "系统异常"),
	BusinessError(10008, "业务异常"), TransferStatusError(10010, "当前状态不正确，请勿重复提交");

	private Integer statusCodeValue;
	private String msg;

	BizCodeEnum(Integer statusCodeValue, String msg) {
		this.statusCodeValue = statusCodeValue;
		this.msg = msg;
	}

	@Override
	public Integer getStatusCodeValue() {
		return this.statusCodeValue;
	}

	@Override
	public String getName() {
		return this.msg;
	}

	public static Optional<BizCodeEnum> of(Integer statusCodeValue) {
		return Optional.ofNullable(BaseEnum.parseByCode(BizCodeEnum.class, statusCodeValue));
	}
}
