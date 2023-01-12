/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserve.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.open.capacity.uid.worker.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.open.capacity.uid.worker.entity.WorkerNodeEntity;

/**
 * DAO for M_WORKER_NODE
 *
 * @author yutianbao
 */
@Mapper
public interface WorkerNodeMapper {

	/**
	 * Get {@link WorkerNodeEntity} by node host
	 * 
	 * @param host
	 * @param port
	 * @return
	 */
	@Select("select " + " id," + " host_name," + " port," + " type," + " launch_date," + " modified," + " created"
			+ " from" + " worker_node" + " where"
			+ " host_name = #{host,jdbctype=varchar} and port = #{port,jdbctype=varchar} limit 1")
	WorkerNodeEntity getWorkerNodeByHostPort(@Param("host") String host, @Param("port") String port);

	/**
	 * Add {@link WorkerNodeEntity}
	 * 
	 * @param workerNodeEntity
	 */
	@Insert("insert into worker_node" + "(host_name," + "port," + "type," + "launch_date," + "modified," + "created)"
			+ "values (" + "#{hostname}," + "#{port}," + "#{type}," + "#{launchdate}," + "now()," + "now())")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	void addWorkerNode(WorkerNodeEntity workerNodeEntity);

}
