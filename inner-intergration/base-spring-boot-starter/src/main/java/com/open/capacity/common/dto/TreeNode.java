package com.open.capacity.common.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 树实体类
 */
@Data
@AllArgsConstructor
@ToString(callSuper = true)
@NoArgsConstructor
@Builder
public class TreeNode {
	private Long id;
	private Long parentId;
	private List<TreeNode> children;
}