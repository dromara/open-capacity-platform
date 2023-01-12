package com.xxl.job.core.endpoint;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.util.DiscoveryUtil;


/**
 * executor EndPoint
 * @author someday
 */
@RestController
public class ExecutorBizEndPoint {

    private static ExecutorBiz executorBiz = new ExecutorBizImpl();

    /**
     * 用于任务的执行
     * @param triggerParam execution param
     * @return success | fail
     */
    @PostMapping("/v1/executor/api")
    public ReturnT<String> run(@RequestBody TriggerParam triggerParam, HttpServletRequest httpServletRequest) {
        String remoteHost = httpServletRequest.getRemoteHost();
        return !DiscoveryUtil.hostExist(remoteHost) ? ReturnT.FAIL : executorBiz.run(triggerParam);
    }

    /**
     * 用于job-admon的登记
     * @param adminName console name
     */
    @PostMapping("/v1/executor/list")
    public void addServices(@RequestParam String adminName){
        DiscoveryUtil.addList(adminName);
    }

}
