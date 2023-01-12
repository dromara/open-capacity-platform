package com.open.capacity.file.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.capacity.db.mapper.BaseMapper;
import com.open.capacity.file.entity.FileInfo;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51
 * oss上传存储db
*/
@Mapper
public interface FileMapper extends BaseMapper<FileInfo> {
	
	List<FileInfo> findList(Page<FileInfo> page, @Param("params") Map<String, Object> params);
}
