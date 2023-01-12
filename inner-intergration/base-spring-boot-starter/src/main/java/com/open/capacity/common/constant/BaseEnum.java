package com.open.capacity.common.constant;

/**
 * 响应结构体定义
 * @Author: someday
 * @Date 2019-09-12
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public interface BaseEnum<T extends Enum<T> & BaseEnum<T>> {

  /**
   * 获取code码存入数据库
   *
   * @return 获取编码
   */
  Integer getStatusCodeValue();

  /**
   * 获取编码名称，便于维护
   *
   * @return 获取编码名称
   */
  String getName();

  /**
   * 根据code码获取枚举
   *
   * @param cls enum class
   * @param code enum code
   * @return get enum
   */
  static <T extends Enum<T> & BaseEnum<T>> T parseByCode(Class<T> cls, Integer code) {
    for (T t : cls.getEnumConstants()) {
      if (t.getStatusCodeValue().intValue() == code.intValue()) {
        return t;
      }
    }
    return null;
  }

}