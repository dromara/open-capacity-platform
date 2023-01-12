CREATE DATABASE IF NOT EXISTS `file-center` DEFAULT CHARACTER SET = utf8;
Use `file-center`;


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file_info
-- ----------------------------
drop table if exists `file_info` ;
CREATE TABLE `file_info` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `md5` varchar(32) NOT NULL COMMENT '文件md5',
  `name` varchar(128) NOT NULL,
  `is_img` tinyint(1) NOT NULL,
  `content_type` varchar(128) NOT NULL,
  `size` int(11) NOT NULL,
  `path` varchar(255) DEFAULT NULL COMMENT '物理路径',
  `url` varchar(1024) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `tenant_id` varchar(32) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  KEY `create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for file_info_extend
-- ----------------------------
drop table if exists  `file_info_part`;
DROP TABLE IF EXISTS `file_info_part`;
 CREATE TABLE `file_info_part` (
  `id` varchar(32) NOT NULL,
  `md5` varchar(100) DEFAULT NULL,
  `guid` varchar(32) NOT NULL COMMENT '文件分片id',
  `upload_id` varchar(100) DEFAULT NULL,
  `name` varchar(128) NOT NULL,
  `is_img` tinyint(1) NOT NULL,
  `content_type` varchar(128) NOT NULL,
  `size` int(11) NOT NULL,
  `path` varchar(255) DEFAULT NULL COMMENT '物理路径',
  `file_id` varchar(32) DEFAULT NULL,
  `e_tag` varchar(100) DEFAULT NULL,
  `part` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件分片表' ;



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
