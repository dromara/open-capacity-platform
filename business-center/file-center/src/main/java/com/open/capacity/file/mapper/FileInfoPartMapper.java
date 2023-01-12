package com.open.capacity.file.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.open.capacity.db.mapper.BaseMapper;
import com.open.capacity.file.entity.FileInfoPart;

/**
 * 文件分片表 Mapper 接口
 * @author owen
 */
@Mapper
public interface FileInfoPartMapper extends BaseMapper<FileInfoPart> {

}
