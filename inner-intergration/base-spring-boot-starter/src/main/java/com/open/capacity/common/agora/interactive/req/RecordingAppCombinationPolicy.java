package com.open.capacity.common.agora.interactive.req;

/**
 * 应用设置
 * https://docs.agora.io/cn/cloud-recording/cloud_recording_api_rest?platform=RESTful#appsCollection
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
public enum RecordingAppCombinationPolicy {

	/**
	 * 合流模式  ：（默认）除延时转码外，均选用此种方式
	 */
	DEFAULT("default", "（默认）除延时转码外，均选用此种方式"),
	/**
	 *如需延时转码，则选用此种方式
	 */
	POSTPONE_TRANSCODING("postpone_transcoding", "如需延时转码，则选用此种方式"),

	;

	private String name;

	private String desc;

	private RecordingAppCombinationPolicy(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}

	public boolean equals(RecordingAppCombinationPolicy region) {
		return this.compareTo(region) == 0;
	}

}
