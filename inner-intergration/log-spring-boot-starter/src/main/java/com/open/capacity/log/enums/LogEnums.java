package com.open.capacity.log.enums;

/**
 * 日志tag标记
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public enum LogEnums {

	REQUEST_LOG("1","biz", "请求日志") ,
	BIGRESULT_LOG("2" ,"sql","数据库查询结果"),
	SLOWRESULT_LOG("3" ,"sql","SQL执行耗时"),
	AUDIT_LOG("4","audit" ,"审计日志");
	
	private String id ;
	private String tag ;
	private String name ;
	private LogEnums(String id, String tag , String name) {
		this.id = id;
		this.tag = tag ;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}
