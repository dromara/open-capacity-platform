/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.common.core.crypto;

public interface ReqSourceVo {
    /**
     * 请求参数
     * @return
     */
    Long findReqTime();
    void putReqTime(Long reqTime);

    /**
     * 请求序列号
     * @return
     */
    String findReqId();
    void putReqId(String reqId);

    /**
     * 请求来源，可以为发送请求平台
     * @return
     */
    String findReqSource();
    void putReqSource(String reqSource);

    /**
     * 对称加密数据密钥
     * @return
     */
    String findDataKey();
    void putDataKey(String dataKey);

    /**
     * 请求数据
     * @return
     */
    String findReqData();
    void putReqData(String reqData);
}
