package com.open.capacity.common.agora.interactive.req;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * 开始参数
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class AgoraStartParam {

    private RecordingConfig recordingConfig;

    private RecordingFileConfig recordingFileConfig;

    private RecordingStorageConfig storageConfig;
}
