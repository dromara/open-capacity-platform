###导出 log-center 的数据库结构
CREATE DATABASE IF NOT EXISTS `log-center` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `log-center`;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
 
CREATE TABLE `sys_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '应用名',
  `class_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类名',
  `method_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '方法名',
  `user_id` int(11) NULL COMMENT '用户id',
  `user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '用户名',
  `client_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '租户id',
  `operation` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作信息',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=archive DEFAULT CHARSET=utf8mb4;

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