/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.common.core.crypto;

public class SimpleReqEncVo implements ReqEncVo{

    private Long reqTime;
    private String reqId;
    private String reqSource;
    private String encDataKey;
    private String encReqData;
    private String reqSign;

    public Long getReqTime() {
        return reqTime;
    }

    public void setReqTime(Long reqTime) {
        this.reqTime = reqTime;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getReqSource() {
        return reqSource;
    }

    public void setReqSource(String reqSource) {
        this.reqSource = reqSource;
    }

    public String getEncDataKey() {
        return encDataKey;
    }

    public void setEncDataKey(String encDataKey) {
        this.encDataKey = encDataKey;
    }

    public String getEncReqData() {
        return encReqData;
    }

    public void setEncReqData(String encReqData) {
        this.encReqData = encReqData;
    }

    public String getReqSign() {
        return reqSign;
    }

    public void setReqSign(String reqSign) {
        this.reqSign = reqSign;
    }

    @Override
    public Long findReqTime() {
        return getReqTime();
    }

    @Override
    public void putReqTime(Long reqTime) {
        setReqTime(reqTime);
    }

    @Override
    public String findReqId() {
        return getReqId();
    }

    @Override
    public void putReqId(String reqId) {
        setReqId(reqId);
    }

    @Override
    public String findReqSource() {
        return getReqSource();
    }

    @Override
    public void putReqSource(String reqSource) {
        setReqSource(reqSource);
    }

    @Override
    public String findEncDataKey() {
        return getEncDataKey();
    }

    @Override
    public void putEncDataKey(String encDataKey) {
        setEncDataKey(encDataKey);
    }

    @Override
    public String findEncReqData() {
        return getEncReqData();
    }

    @Override
    public void putEncReqData(String encReqData) {
        setEncReqData(encReqData);
    }

    @Override
    public String findReqSign() {
        return getReqSign();
    }

    @Override
    public void putReqSign(String reqSign) {
        setReqSign(reqSign);
    }
}
