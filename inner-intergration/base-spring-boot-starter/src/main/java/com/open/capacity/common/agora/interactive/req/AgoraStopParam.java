package com.open.capacity.common.agora.interactive.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 开始参数
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class AgoraStopParam {

    /**
     * 设置 stop 方法是否为异步调用。
     * true：异步。调用 stop 后立即收到响应。
     * （默认）false：同步。调用 stop 后，需等待所有录制文件上传至第三方云存储方可收到响应。
     */
    private Boolean async_stop = true;

}
