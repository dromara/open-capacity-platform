package com.open.capacity.common.agora.interactive.req;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 *  @author: <a href="https://github.com/hiwepy">hiwepy</a>
 * 声网请求参数
 * 详情参考文档： https://docs.agora.io/cn/cloud-recording/cloud_recording_api_rest?platform=RESTful#acquire-%E8%AF%B7%E6%B1%82%E7%A4%BA%E4%BE%8B
 */
@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class AgoraRequest<T>{

    /**
     * 频道号
     */
    private String cname;

    /**
     * 录制用户号
     */
    private String uid;

    public AgoraRequest(String cname, String uid) {
        this.cname = cname;
        this.uid = uid;
    }

    private T clientRequest;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
