-- ----------------------------
-- Table structure for file_info
-- ----------------------------
CREATE TABLE `file_info` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `md5` varchar(32) NOT NULL COMMENT '文件md5',
  `name` varchar(128) NOT NULL,
  `is_img` tinyint(1) NOT NULL,
  `content_type` varchar(128) NOT NULL,
  `size` int(11) NOT NULL,
  `path` varchar(255) DEFAULT NULL COMMENT '物理路径',
  `url` varchar(1024) NOT NULL,
  `create_time` datetime NOT NULL,
  `tenant_id` varchar(32) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  KEY `create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



-- ----------------------------
-- Table structure for file_info_extend
-- ----------------------------
-- DROP TABLE IF EXISTS `file_info_extend`;
CREATE TABLE `file_info_extend` (
  `id` varchar(32) NOT NULL COMMENT '文件md5',
  `guid` varchar(32) NOT NULL COMMENT '文件分片id',
  `name` varchar(128) NOT NULL,
  `size` int(11) NOT NULL,
  `path` varchar(255) DEFAULT NULL COMMENT '物理路径',
  `url` varchar(1024) NOT NULL,
  `source` varchar(32) NOT NULL,
  `file_id` varchar(32) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `tenant_id` varchar(32) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件拓展表';




-- drop table if exists worker_node;
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
 comment='db workerid assigner for flyway generator',engine = innodb;
