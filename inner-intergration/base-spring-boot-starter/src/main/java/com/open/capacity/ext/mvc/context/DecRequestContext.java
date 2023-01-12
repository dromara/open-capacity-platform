/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc.context;

import java.util.HashMap;
import java.util.Map;

public class DecRequestContext {
    private Object[] desArgs;
    private Object[] reqArgs;
    private Map<ReqParamKey,String> midVal = new HashMap<>();
    private boolean bodyReset;
    private boolean queryReset;
    private boolean allQueryDes;
    private Object encResult;
    private Object reqResult;
    private LoginUser loginUser;
    private String reqIp;
    private String url;
    private Throwable reqException;

    public Object[] getDesArgs() {
        return desArgs;
    }

    public void setDesArgs(Object[] desArgs) {
        this.desArgs = desArgs;
    }

    public Object[] getReqArgs() {
        return reqArgs;
    }

    public void setReqArgs(Object[] reqArgs) {
        this.reqArgs = reqArgs;
    }

    public Map<ReqParamKey, String> getMidVal() {
        return midVal;
    }

    public boolean isBodyReset() {
        return bodyReset;
    }

    public void setBodyReset(boolean bodyReset) {
        this.bodyReset = bodyReset;
    }

    public boolean isQueryReset() {
        return queryReset;
    }

    public void setQueryReset(boolean queryReset) {
        this.queryReset = queryReset;
    }

    public boolean isAllQueryDes() {
        return allQueryDes;
    }

    public void setAllQueryDes(boolean allQueryDes) {
        this.allQueryDes = allQueryDes;
    }

    public Object getReqResult() {
        return reqResult;
    }

    public void setReqResult(Object reqResult) {
        this.reqResult = reqResult;
    }

    public LoginUser getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(LoginUser loginUser) {
        this.loginUser = loginUser;
    }

    public String getReqIp() {
        return reqIp;
    }

    public void setReqIp(String reqIp) {
        this.reqIp = reqIp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Throwable getReqException() {
        return reqException;
    }

    public void setReqException(Throwable reqException) {
        this.reqException = reqException;
    }

    public Object getEncResult() {
        return encResult;
    }

    public void setEncResult(Object encResult) {
        this.encResult = encResult;
    }

    public boolean hasResetQuery(String name){
        return getMidVal().containsKey(ReqParamKey.buildReqQueryKey(name));
    }
    public String getResetQuery(String name){
        return getMidVal().get(ReqParamKey.buildReqQueryKey(name));
    }
}
