

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