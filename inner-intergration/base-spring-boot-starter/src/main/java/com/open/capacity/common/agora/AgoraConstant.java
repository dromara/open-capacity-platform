package com.open.capacity.common.agora;
/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 * 声网相关的常量
 */
public interface AgoraConstant {

    // 获取频道内的所有用户
    // https://api.agora.io/dev/v1/channel/user/{appid}/{channelName}
    String URL_CHANNEL_USER = "https://api.agora.io/dev/v1/channel/user/{0}/{1}";

    // 用户禁封
    // https://api.agora.io/dev/v1/kicking-rule
    String URL_RULE = "https://api.agora.io/dev/v1/kicking-rule";

    // 录制请求的uid
    String RECORDING_UID = "10";

    // 存储位置 /video/{{yyyy-MM-dd}}
    String VEIDO_PAHT = "video";
}
