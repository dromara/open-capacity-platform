package com.open.capacity.service;

import com.open.capacity.dto.Order;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@LiteflowComponent("a")
public class AService extends NodeComponent {

	@Override
	public void process() {
		Order context = this.getRequestData();
		context.setPrice(context.getPrice().multiply(BigDecimal.valueOf(0.9)));
		System.out.println("A executed!");
	}
}
