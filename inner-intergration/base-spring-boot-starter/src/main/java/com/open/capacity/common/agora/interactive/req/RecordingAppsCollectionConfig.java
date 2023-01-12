package com.open.capacity.common.agora.interactive.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 应用设置
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class RecordingAppsCollectionConfig {

    /**
     *  combinationPolicy：（选填）String 类型，各个云端录制应用的组合方式。
     *  "default"：（默认）除延时转码外，均选用此种方式。
     *  "postpone_transcoding"：如需延时转码，则选用此种方式。
     */
    @JsonProperty("combinationPolicy")
    private String combinationPolicy = "default";

}
