package com.open.capacity.common.agora.recording.common;

import lombok.Builder;
import lombok.Data;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@Builder
public class RecordingResult {

    /**
     * 当前对象录制的频道名称
     */
    private String channelId;
    private boolean leaveState;
    private int width = 0;
    private int height = 0;
    private int fps = 0;
    private int kbps = 0;
    private int count = 0;
    private long firstReceiveAudioTime = 0;
    private long firstReceiveAudioElapsed = 0;
    private long firstReceiveVideoTime = 0;
    private long firstReceiveVideoElapsed = 0;
    private String storageDir;

}
