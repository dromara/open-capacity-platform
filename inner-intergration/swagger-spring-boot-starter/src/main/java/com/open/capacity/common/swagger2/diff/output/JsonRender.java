package com.open.capacity.common.swagger2.diff.output;

import com.alibaba.fastjson.JSON;
import com.open.capacity.common.swagger2.diff.SwaggerDiff;

public class JsonRender implements Render {

    @Override
    public String render(SwaggerDiff diff) {
        return JSON.toJSONString(diff);
    }
}
