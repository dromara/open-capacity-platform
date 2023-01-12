package com.open.capacity.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "ocp.oss")
public class OssProperties {

	private boolean enable = true;

	/**
	 * endpoint 配置格式为
	 * 通过外网访问OSS服务时，以URL的形式表示访问的OSS资源，详情请参见OSS访问域名使用规则。OSS的URL结构为[$Schema]://[$Bucket].[$Endpoint]/[$Object]
	 * 。例如，您的Region为华东1（杭州），Bucket名称为examplebucket，Object访问路径为destfolder/example.txt，
	 * 则外网访问地址为https://examplebucket.oss-cn-hangzhou.aliyuncs.com/destfolder/example.txt
	 * https://help.aliyun.com/document_detail/375241.html
	 */
	private String endpoint;
	/**
	 * refer com.amazonaws.regions.Regions; 阿里云region 对应表
	 * https://help.aliyun.com/document_detail/31837.htm?spm=a2c4g.11186623.0.0.695178eb0nD6jp
	 */
	private String region;

	/**
	 * true path-style nginx 反向代理和S3默认支持 pathStyle {http://endpoint/bucketname}
	 * false supports virtual-hosted-style 阿里云等需要配置为 virtual-hosted-style
	 * 模式{http://bucketname.endpoint}
	 */
	private boolean pathStyleAccess = true;

	/**
	 * 自定义域名 用于替换 endpoint 配置格式为
	 */
	private String domain;

	/**
	 * 应用ID
	 */
	private String accessKey;

	/**
	 * Secret key是你账户的密码
	 */
	private String accessSecret;

	/**
	 * 默认的存储桶名称
	 */
	private String bucketName = "payerp";

	/**
	 * 最大线程数，默认： 100
	 */
	private Integer maxConnections = 100;

}
