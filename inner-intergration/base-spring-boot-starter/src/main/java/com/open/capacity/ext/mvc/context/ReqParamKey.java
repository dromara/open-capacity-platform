/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc.context;

import java.util.Objects;

public class ReqParamKey {
    private ReqParamType paramType;
    private String name;
    private static ReqParamKey REQ_BODY_KEY = new ReqParamKey(ReqParamType.BODY,"");
    private ReqParamKey(ReqParamType paramType, String name) {
        this.paramType = paramType;
        this.name = name;
    }
    public static ReqParamKey buildReqBodyKey(){
        return REQ_BODY_KEY;
    }
    public static ReqParamKey buildReqQueryKey(String name){
        return new ReqParamKey(ReqParamType.QUERY,name);
    }
    public static ReqParamKey buildReqExtKey(String name){
        return new ReqParamKey(ReqParamType.DES_EXT,name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReqParamKey that = (ReqParamKey) o;
        return paramType == that.paramType && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paramType, name);
    }
}
