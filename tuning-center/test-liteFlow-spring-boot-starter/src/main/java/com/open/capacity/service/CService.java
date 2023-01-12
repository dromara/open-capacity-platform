package com.open.capacity.service;

import com.open.capacity.dto.Order;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@LiteflowComponent("c")
public class CService extends NodeComponent {
	@Override
	public void process() {
		Order context = this.getRequestData();
		System.out.println(context.getPrice());
		System.out.println("C executed!");
	}
	
}
