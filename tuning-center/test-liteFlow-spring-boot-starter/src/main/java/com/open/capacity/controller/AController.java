package com.open.capacity.controller;

import com.open.capacity.dto.Order;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.slot.DefaultContext;
import com.yomahub.liteflow.util.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;


@RestController
@Log4j2
public class AController {

	@Resource
	private FlowExecutor flowExecutor;
	
	@GetMapping("/exec")
	public String exec() {
		Order test = new Order();
		test.setId(123L);
		test.setType(1);
		test.setPrice(BigDecimal.valueOf(100));
		LiteflowResponse response = flowExecutor.execute2Resp("chain1", test);
		DefaultContext context = response.getFirstContextBean();
		System.out.println(JsonUtil.toJsonString(context.getData("student")));
		if (response.isSuccess()){
			log.info("执行成功");
		}else{
			log.info("执行失败");
		}
		return "hello";
	}

	@GetMapping("/reload")
	public void reload() {
		flowExecutor.reloadRule();
	}

}
