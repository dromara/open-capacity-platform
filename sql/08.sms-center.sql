##导出 sms-center 的数据库结构
CREATE DATABASE IF NOT EXISTS `sms-center` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `sms-center`;
SET FOREIGN_KEY_CHECKS=0;
#
# Structure for table "sys_sms"
#
DROP TABLE IF EXISTS `sys_sms`;
CREATE TABLE `sys_sms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `phone` varchar(16) NOT NULL COMMENT '手机号码',
  `sign_name` varchar(128) DEFAULT NULL COMMENT '短信签名',
  `template_code` varchar(128) DEFAULT NULL COMMENT '短信模板代码',
  `params` varchar(500) DEFAULT NULL COMMENT '参数',
  `biz_id` varchar(128) DEFAULT NULL COMMENT '阿里云返回的',
  `code` varchar(64) DEFAULT NULL COMMENT '阿里云返回的code',
  `message` varchar(128) DEFAULT NULL COMMENT '阿里云返回的',
  `day` date NOT NULL COMMENT '日期',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `tenant_id` varchar(32) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  KEY `phone` (`phone`),
  KEY `day` (`day`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COMMENT='发短信记录';

#
# Data for table "sys_sms"
# 



drop table if exists worker_node;
create table worker_node
(
id bigint not null auto_increment comment 'auto increment id',
host_name varchar(64) not null comment 'host name',
port varchar(64) not null comment 'port',
type int not null comment 'node type: actual or container',
launch_date date not null comment 'launch date',
modified timestamp   null comment 'modified time',
created timestamp   null comment 'created time',
primary key(id)
)
 comment='db workerid assigner for uid generator',engine = innodb;