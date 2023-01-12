package com.open.capacity.service;

import com.open.capacity.dto.Order;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@LiteflowComponent("b")
public class BService extends NodeComponent {
	@Override
	public void process() {
		Order context = this.getContextBean(Order.class);
		context.setPrice(context.getPrice().multiply(BigDecimal.valueOf(0.7)));
		System.out.println("B executed!");
	}
	
}
