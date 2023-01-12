package com.open.capacity.batch.dao;

import java.util.List;

import com.open.capacity.batch.entity.DeliverPost;

public interface DeliverPostDao {

	public void batchInsert(List<? extends DeliverPost> list)  ;

}
