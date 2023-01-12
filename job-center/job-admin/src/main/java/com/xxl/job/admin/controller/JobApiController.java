package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author someday
 */
@Controller
public class JobApiController {

    @Resource
    private AdminBiz adminBiz;

    /**
     * 执行结果回调接口
     * @param callbackParamList 回调列表
     */
    @PostMapping("/api")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> api(@RequestBody List<HandleCallbackParam> callbackParamList) {
        return adminBiz.callback(callbackParamList);
    }


}
