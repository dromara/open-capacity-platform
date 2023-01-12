package com.open.capacity.common.exception;

import java.util.List;

import com.open.capacity.common.dto.ValidateResult;

import lombok.Getter;

/**
 * @author zlt
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class ValidationException extends RuntimeException {
  @Getter
  private List<ValidateResult> result;
  public ValidationException(List<ValidateResult> list){
    super();
    this.result = list;
  }
}
