package com.open.capacity.uaa.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.model.Client;
import com.open.capacity.uaa.common.service.IClientService;
import com.open.capacity.uaa.dto.ClientDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用相关接口
 * @author owen 624191343@qq.com
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@Api(tags = "应用")
@RestController
@RequestMapping("/clients")
public class ClientController {
	@Autowired
	private IClientService clientService;

	@GetMapping("/list")
	@ApiOperation(value = "应用列表")
	public PageResult<Client> list(@RequestParam Map<String, Object> params) {
		return clientService.listClient(params, true);
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "根据id获取应用")
	public Client getById(@PathVariable Long id) {
		return clientService.getById(id);
	}
	
	
	@GetMapping("/client")
	@ApiOperation(value = "根据clientId获取应用")
	public Client getByClientId(@RequestParam String clientId) {
		return clientService.loadClientByClientId(clientId) ;
	}

	@GetMapping("/all")
	@ApiOperation(value = "所有应用")
	public ResponseEntity<List<Client>> allClient() {
		PageResult<Client> page = clientService.listClient(Maps.newHashMap(), false);
		return ResponseEntity.succeed(page.getData());
	}

	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除应用")
	public ResponseEntity delete(@PathVariable Long id) {

		Try.of(() -> clientService.delClient(id)).onFailure(ex -> log.error("client-delete-error", ex))
				.getOrElseThrow(item -> new BusinessException("操作失败"));
		return ResponseEntity.succeed("操作成功");

	}

	@PostMapping("/saveOrUpdate")
	@ApiOperation(value = "保存或者修改应用")
	public ResponseEntity saveOrUpdate(@RequestBody ClientDto clientDto) throws Exception {

		Try.of(() -> clientService.saveClient(clientDto)).onFailure(ex -> log.error("client-saveOrUpdate-error", ex))
				.getOrElseThrow(item -> new BusinessException("操作失败"));
		return ResponseEntity.succeed("操作成功");

	}
}
