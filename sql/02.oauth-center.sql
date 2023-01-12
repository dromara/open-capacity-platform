CREATE DATABASE IF NOT EXISTS `oauth-center` DEFAULT CHARACTER SET = utf8;
Use `oauth-center`;

-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `client_id` varchar(32) NOT NULL COMMENT '应用标识',
  `resource_ids` varchar(256) DEFAULT NULL COMMENT '资源限定串(逗号分割)',
  `client_secret` varchar(256) DEFAULT NULL COMMENT '应用密钥(bcyt) 加密',
  `client_secret_str` varchar(256) DEFAULT NULL COMMENT '应用密钥(明文)',
  `scope` varchar(256) DEFAULT NULL COMMENT '范围',
  `authorized_grant_types` varchar(256) DEFAULT NULL COMMENT '5种oauth授权方式(authorization_code,password,refresh_token,client_credentials)',
  `web_server_redirect_uri` varchar(256) DEFAULT NULL COMMENT '回调地址 ',
  `authorities` varchar(256) DEFAULT NULL COMMENT '权限',
  `access_token_validity` int(11) DEFAULT NULL COMMENT 'access_token有效期',
  `refresh_token_validity` int(11) DEFAULT NULL COMMENT 'refresh_token有效期',
  `additional_information` varchar(4096) DEFAULT '{}' COMMENT '{}',
  `autoapprove` char(5) NOT NULL DEFAULT 'true' COMMENT '是否自动授权 是-true',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `client_name` varchar(128) DEFAULT '' COMMENT '应用名称',
  `support_id_token` tinyint(1) DEFAULT '1' COMMENT '是否支持id_token',
  `id_token_validity` int(11) DEFAULT '60' COMMENT 'id_token有效期',
  `if_limit` int(11) DEFAULT NULL COMMENT '是否应用限流',
  `limit_count` bigint(20) DEFAULT NULL COMMENT '限流阈值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------

INSERT INTO `oauth_client_details`
(id, client_id, resource_ids, client_secret, client_secret_str, `scope`, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, create_time, update_time, client_name, support_id_token, id_token_validity, if_limit, limit_count)
VALUES(1, 'webApp', NULL, '$2a$10$06msMGYRH8nrm4iVnKFNKOoddB8wOwymVhbUzw/d3ZixD7Nq8ot72', 'webApp', 'app', 'authorization_code,password,refresh_token,client_credentials,implicit,password_code,openId,mobile_password,cas_ticket,faceId,password_sso,password_goole,password_smkey', NULL, NULL, 3600, NULL, '{"LOGOUT_NOTIFY_URL_LIST":"http://127.0.0.1:8082/logoutNotify"}', 'true', NULL, NULL, 'pc端', 1, 60, 0, 10000);
INSERT INTO `oauth_client_details`
(id, client_id, resource_ids, client_secret, client_secret_str, `scope`, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, create_time, update_time, client_name, support_id_token, id_token_validity, if_limit, limit_count)
VALUES(2, 'app', NULL, '$2a$10$i3F515wEDiB4Gvj9ym9Prui0dasRttEUQ9ink4Wpgb4zEDCAlV8zO', 'app', 'app', 'authorization_code,password,refresh_token', 'http://127.0.0.1:8081/callback.html', NULL, 3600, NULL, '{"LOGOUT_NOTIFY_URL_LIST":"http://127.0.0.1:8081/logoutNotify"}', 'true', NULL, NULL, '移动端', 1, 60, 0, 10000);
INSERT INTO `oauth_client_details`
(id, client_id, resource_ids, client_secret, client_secret_str, `scope`, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, create_time, update_time, client_name, support_id_token, id_token_validity, if_limit, limit_count)
VALUES(3, 'owen', NULL, '$2a$10$a1ZEXiZQr604LN.wVxet.etPm6RvDs.HIaXP48J2HKRaEnZORTVwe', 'owen', 'app', 'authorization_code,password,refresh_token,client_credentials', 'http://127.0.0.1:9997/clientOne/login', NULL, 3600, 28800, '{}', 'true', '2018-12-27 00:50:30.0', '2018-12-27 00:50:30.0', '第三方应用', 1, 60, 0, 10000);




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
