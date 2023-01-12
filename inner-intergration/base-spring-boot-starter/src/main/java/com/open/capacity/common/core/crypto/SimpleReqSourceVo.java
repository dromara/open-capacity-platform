/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.common.core.crypto;

public class SimpleReqSourceVo implements ReqSourceVo{
    private Long reqTime;
    private String reqId;
    private String reqSource;
    private String dataKey;
    private String reqData;

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

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public String getReqData() {
        return reqData;
    }

    public void setReqData(String reqData) {
        this.reqData = reqData;
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
    public String findDataKey() {
        return getDataKey();
    }

    @Override
    public void putDataKey(String dataKey) {
        setDataKey(dataKey);
    }

    @Override
    public String findReqData() {
        return getReqData();
    }

    @Override
    public void putReqData(String reqData) {
        setReqData(reqData);
    }
}
