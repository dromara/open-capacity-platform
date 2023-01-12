package com.open.capacity.gateway.event;

import com.alibaba.fastjson.JSON;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import dnl.utils.text.table.TextTable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestEventListener implements UpdateListener {
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		try {
			if (newEvents != null && newEvents.length > 0) {
				for (EventBean event : newEvents) {
					String method = (String) event.get("method");
					String path = (String) event.get("path");
					long timestamp = (long) event.get("timestamp");
					RequestEvent requestEvent = new RequestEvent(method, path, timestamp);
					String[][] values = { { "高频接口日志", JSON.toJSONString(requestEvent) } };
					TextTable log = new TextTable(new String[] { "name", "msg" }, values);
					log.printTable();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}