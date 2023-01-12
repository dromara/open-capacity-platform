

# [![Fork me on Gitee](https://gitee.com/owenwangwen/open-capacity-platform/widgets/widget_5.svg)](https://gitee.com/owenwangwen/open-capacity-platform)open-capacity-platform 微服务能力开放平台 

<p align="center">
 <img src="https://img.shields.io/badge/buildi%20-success-green.svg" alt="Build Status"/>
 <img src="https://img.shields.io/badge/easyweb%20-green.svg" alt="es"/>
 <img src="https://img.shields.io/badge/elasticsearch%20-6.5.4-green.svg" alt="es"/>
 <img src="https://img.shields.io/badge/Spring%20Boot-2.6.3-blue.svg" alt="sb">
 <img src="https://img.shields.io/badge/Spring%20Cloud-2021.0.5-blue.svg" alt="sc">
 <img src="https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2021.0.4.0-blue.svg" alt="sc">
</p>

[![star](https://gitee.com/owenwangwen/open-capacity-platform/badge/star.svg?theme=white)](https://gitee.com/owenwangwen/open-capacity-platform/stargazers)
[![Fork me on Gitee](https://gitee.com/owenwangwen/open-capacity-platform/widgets/widget_6.svg)](https://gitee.com/owenwangwen/open-capacity-platform)
[![fork](https://gitee.com/owenwangwen/open-capacity-platform/badge/fork.svg?theme=white)](https://gitee.com/owenwangwen/open-capacity-platform/members)
 
简称ocp是基于layui+springcloud的企业级微服务框架(用户权限管理，配置中心管理，应用管理，....),其核心的设计目标是分离前后端，快速开发部署，学习简单，功能强大，提供快速接入核心接口能力，其目标是帮助企业搭建一套类似百度能力开放平台的框架；  
- 基于layui前后端分离的企业级微服务架构  
- 兼容spring cloud netflix & spring cloud alibaba  
- 优化Spring Security内部实现，实现API调用的统一出口和权限认证授权中心  
- 完善Spring安全扩展,解决OWASP高级安全弱点
- 提供完善的企业微服务流量监控，日志监控能力   
- 通用的微服务架构应用非功能性(NFR)需求,更容易地在不同的项目中复用    
- 提供完善的压力测试方案  
- 提供完善的灰度发布方案  
- 提供完善的微服务部署方案  

### master分支启动文档

[https://www.kancloud.cn/owenwangwen/newocp/content](https://www.kancloud.cn/owenwangwen/newocp/content)


### 2.0.1和alibaba分支开发手册  
[https://www.kancloud.cn/owenwangwen/open-capacity-platform/content](https://www.kancloud.cn/owenwangwen/open-capacity-platform/content)

### **与驰骋jflow工作流-表单集成声明** #

-  该软件与JFlow 驰骋工作流、表单引擎中间件集成.
-  JFlow是一款100%开源的、存粹国产的流程、表单引擎系统, 支持多个.net java多个版本.
-  驰骋官网 http://ccflow.org/?frm=capacity 


### 云原生高薪架构师课程推荐

腾讯课堂： https://ke.qq.com/course/2738602

51CTO：https://edu.51cto.com/course/23845.html

###  ISTIO

CSDN学院： https://download.csdn.net/course/detail/35483



### 欢迎进群（大佬云集）

<a target="_blank" href="//shang.qq.com/wpa/qunwpa?idkey=4eb3e891564dba87ff097d2ca403bf8c2ceb9244f198692feda5d0dd424b9457"><img border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="ocp&amp;cp微服务" title="ocp&amp;cp微服务"></a>  

<table>
	<tr>
            <td><img src=https://images.gitee.com/uploads/images/2019/1209/121109_59a8822c_869801.png "屏幕截图.png"" />
			<td><img src=https://images.gitee.com/uploads/images/2019/1209/121142_1f491d9b_869801.png "屏幕截图.png"" />
			<td><img src=https://images.gitee.com/uploads/images/2020/0401/194112_aa2542bd_869801.png "屏幕截图.png" />	
			<td><img src=https://images.gitee.com/uploads/images/2020/0518/105108_51d550de_869801.png "屏幕截图.png"" />					
	</tr>
</table>


# 技术介绍
<table>
	<tr>
		<td><img src="https://images.gitee.com/uploads/images/2020/0716/173815_cc75fc64_869801.png "屏幕截图.png"></td>
		<td><img src="https://images.gitee.com/uploads/images/2020/0531/225148_0dff4506_1441068.png "屏幕截图.png"></td>
    </tr>
	
</table>

# **功能介绍**
- 统一安全认证中心多因子融合认证(SAK)
	- 支持oauth的四种模式登录
	- 支持用户名密码加图形验证码登录
	- 手机密码手机短信登录
	- 支持谷歌动态令牌登录
	- 支持百度人脸识别登录
	- 支持openId登录
	- 支持第三方系统单点sso登录
- 微服务架构基础支撑
	- 服务注册发现、路由与负载均衡
	- 服务熔断与限流
	- 统一配置中心
	- 统一日志中心
	- 统一异常处理
	- 分布式锁
	- 分布式任务调度器
	- 统一水印组件
	- 支持s3分片上传等异步文件中心
- 系统服务监控中心
	- 服务调用链监控
	- 应用吞吐量监控
	- 服务降级、熔断监控
	- 微服务服务监控
- 能力开放平台业务支撑
	- 网关基于应用方式API接口隔离
	- 网关基于应用限制调用次数
	- 网关SRABC统一权限管理(SIPM)权限自助平台(SIAG)
	- 网关聚合服务内部Swagger接口文档
	- 网关统一跨域处理
	- 网关服务动态路由注册
- docker容器化部署
	- 基于rancher的容器化部署
	- 基于docker的elk日志监控
	- 基于docker的服务动态扩容
   
   

## 能力开放管理平台   

<table>
	<tr>
        <td><img src="https://user-images.githubusercontent.com/16487298/210167333-13444fb6-9311-41c7-8373-27d532025fcb.png"/></td>
        <td><img src="https://user-images.githubusercontent.com/16487298/210167365-af3951fa-416a-4696-9ba7-1ea1afc719ae.png"/></td>
        <td><img src="https://user-images.githubusercontent.com/16487298/210167383-0829b495-5ee7-4ce8-9639-7c6d7199cd09.png"/></td>
        <td><img src="https://user-images.githubusercontent.com/16487298/210167390-f3c2a613-ff6e-41e6-8f19-c5fbddd2ab5f.png"/></td>
    </tr>
    <tr>
        <td><img src="https://user-images.githubusercontent.com/16487298/210167417-a9de414d-e8fe-4e37-942c-82d946ec8f84.png"/></td>
        <td><img src="https://user-images.githubusercontent.com/16487298/210167436-11852ca0-75e0-477a-981c-3ae3a5c06cae.png"/></td>
        <td><img src="https://user-images.githubusercontent.com/16487298/210167442-4c0f9a81-d296-43f6-b3c1-47f6356b570b.png"/></td>
        <td><img src="https://user-images.githubusercontent.com/16487298/210167456-3e3c2c35-1376-4a29-b965-9e8c89d935dd.png"/></td>
    </tr>
    <tr>
        <td><img src="https://user-images.githubusercontent.com/16487298/210168606-35b6b31e-9cd4-44e3-8642-407900f5b63d.png"/></td>
        <td><img src="https://user-images.githubusercontent.com/16487298/210167484-1adc3977-b250-4904-9a37-1ab0f3212e21.png"/></td>
	<td><img src="https://user-images.githubusercontent.com/16487298/210168474-0331ad08-57fc-4540-82a9-acb0831d7529.png" /></td>
        <td><img src="https://user-images.githubusercontent.com/16487298/210171031-d0efa28c-cd5d-4ce5-88e8-a113e2f4bd6f.png"/></td>
    </tr>
 
 
   <tr>
	<td><img src="https://user-images.githubusercontent.com/16487298/210171068-37b79eaa-c775-487e-a019-2e6f7fb65207.png"/></td>
	<td><img src="https://user-images.githubusercontent.com/16487298/210171094-8e263865-c088-4c9e-bd8d-3d191414b21c.png"/></td>
	<td><img src="https://user-images.githubusercontent.com/16487298/210171170-d0d24787-57b0-4475-827a-f8e965fc3fd1.png"/></td>
        <td><img src=https://images.gitee.com/uploads/images/2019/1021/180056_5df984ec_869801.png "屏幕截图.png"/></td>
    </tr>
	<tr>
        <td><img src=https://images.gitee.com/uploads/images/2019/1021/180342_fbfa0c95_869801.png "屏幕截图.png"/></td>
        <td><img src=https://images.gitee.com/uploads/images/2019/1021/180402_d345fc8c_869801.png "屏幕截图.png"/></td>
        <td><img src=https://images.gitee.com/uploads/images/2019/1021/180422_dec2b5c4_869801.png "屏幕截图.png"/></td>
        <td><img src=https://images.gitee.com/uploads/images/2019/1021/180439_d42f2d32_869801.png "屏幕截图.png"/></td>
    </tr>
</table>

# 容器化部署     
<table>
	<tr>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125453_6682dba8_1147840.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125453_3831567a_1147840.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125454_b04fbc0d_1147840.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125454_1f9ce4e8_1147840.png"/></td>
    </tr>
	<tr>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125454_272e0e79_1147840.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125455_0f0278dd_1147840.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125455_05a5b463_1147840.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125455_4827ecff_1147840.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125456_7cf25a83_1147840.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125456_bbac1fb9_1147840.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125456_5c697b5f_1147840.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125457_397161e8_1147840.png"/></td>
    </tr>
</table>
 
# APM监控 #
<table>
	<tr>
        <td><img src="https://images.gitee.com/uploads/images/2019/0330/105610_52def254_869801.png "屏幕截图.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0330/105638_5c7ab9ac_869801.png "屏幕截图.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0330/105713_c9c94365_869801.png "屏幕截图.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0330/105736_ac478159_869801.png "屏幕截图.png"/></td>
	</tr>
	<tr>
		<td><img src="https://images.gitee.com/uploads/images/2020/0703/151910_2bf8f7cf_869801.png "屏幕截图.png"/></td>
		<td><img src="https://images.gitee.com/uploads/images/2020/0703/151518_a64fb77c_869801.png "屏幕截图.png"/></td>
		<td><img src="https://images.gitee.com/uploads/images/2020/0703/151713_216d7010_869801.png "屏幕截图.png"/></td>
		<td><img src="https://images.gitee.com/uploads/images/2020/0703/151810_74106796_869801.png "屏幕截图.png"/></td>
    </tr>
     
</table>

# 系统监控 #
<table>
	<tr>
		<td><img src="https://images.gitee.com/uploads/images/2019/0523/085501_ee047496_869801.png "屏幕截图.png""/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0401/230332_f777ea8d_869801.png "屏幕截图.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0401/230430_3eb6b5e0_869801.png "屏幕截图.png"/></td>
    </tr>
	<tr>
		<td><img src="https://images.gitee.com/uploads/images/2019/0722/164150_6c0ce093_869801.png "屏幕截图.png"/></td>
		<td><img src="https://images.gitee.com/uploads/images/2019/0722/163241_9b29852f_869801.png "屏幕截图.png""/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0722/163356_08ec244d_869801.png "屏幕截图.png"/></td>
    </tr>
</table>

#  灰度发布功能演示   
 
ocp灰度发布功能(参考dev分支) 
a.先启动 register-center 注册中心的 eureka-server 注册服务  
b.在启动 api-gateway 网关服务 
c.再启动 oauth-center 认证中心 oauth-server 认证服务 
d.在启动 business-center 业务中心的 对应服务 user-center 
d.启动gray-center的discovery-console  
e.启动gray-center的discovery-console-desktop    
 
灰度管理UI  
用户名:admin      
密码  :admin  

<table>
	<tr>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125451_c3b6224d_1147840.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125450_b42073c5_1147840.png"/></td>
    </tr>
	<tr>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125450_66e3a8db_1147840.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0126/125451_28b1bc41_1147840.png"/></td>
    </tr>
     
</table>

请参考
[https://github.com/Nepxion/Docs/blob/master/discovery-doc/README_QUICK_START.md](https://github.com/Nepxion/Docs/blob/master/discovery-doc/README_QUICK_START.md)，感谢军哥分享  


# Spring Cloud NETFLIX 版本
https://gitee.com/owenwangwen/open-capacity-platform/tree/2.0.1/


# oracle 版本
https://gitee.com/owenwangwen/open-capacity-platform/tree/oracle/

# 用户权益 #
- 允许免费用于学习、毕设、公司项目、私活等。

# 禁止事项 #
- 代码50%以上相似度的二次开源。
- 注意：若禁止条款被发现有权追讨9999的授权费。


