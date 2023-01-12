package com.open.capacity.file.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.capacity.file.entity.FileInfoPart;
import com.open.capacity.file.mapper.FileInfoPartMapper;
import com.open.capacity.file.service.FileInfoPartService;

/**
 * 文件分片表 服务实现类
 * @author owen
 */
@Service
public class FileInfoPartServiceImpl extends ServiceImpl<FileInfoPartMapper, FileInfoPart> implements FileInfoPartService {

}
