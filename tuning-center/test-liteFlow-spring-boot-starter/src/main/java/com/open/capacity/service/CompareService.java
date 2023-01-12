package com.open.capacity.service;

import com.open.capacity.dto.Order;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeIfComponent;
import org.springframework.stereotype.Component;

@LiteflowComponent("x")
public class CompareService extends NodeIfComponent {
    @Override
    public boolean processIf() throws Exception {
        Order context = this.getRequestData();
        if(context.getType().equals(1)){
            return true;
        }else {
            return false;
        }
    }
}
