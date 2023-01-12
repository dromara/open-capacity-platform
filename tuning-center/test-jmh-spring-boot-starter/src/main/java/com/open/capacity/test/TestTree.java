package com.open.capacity.test;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.open.capacity.TreeNode;
import com.open.capacity.TreeUtil;

public class TestTree {

	private static List<TreeNode> list = Lists.newArrayList();

	static {

		for (int i = 1; i <= 50; i++) {
			TreeNode parent = TreeNode.builder().id("" + i).parentId("0").build();
			list.add(parent);
			for (int j = 1; j <= 50; j++) {
				TreeNode child1 = TreeNode.builder().id(j + "" + i).parentId(parent.getId()).build();
				list.add(child1);
				for (int k = 1; k <= 100; k++) {
					TreeNode child2 = TreeNode.builder().id(k + "" + j + "" + i).parentId(child1.getId()).build();
					list.add(child2);
				}
			}
		}
		System.out.println("init");
	}
 
	public static void main(String[] args) throws JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		List<TreeNode> lists2 = TreeUtil.listToTree(list, TreeNode::setChildren, null, TreeNode::getId,
				TreeNode::getParentId, (node) -> "0".equals(node.getParentId()));
		
		
		List<TreeNode> lists4 =Lists.newArrayList();
		
		TreeUtil.treeToList(lists2, lists4, TreeNode::getChildren,l ->  true);
		
		System.out.println(lists4.size());
		
	}
}
