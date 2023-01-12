package com.open.capacity.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.open.capacity.TreeNode;
import com.open.capacity.TreeUtil;

import lombok.SneakyThrows;

/**
 * JMH Visual Chart：http://deepoove.com/jmh-visual-chart/ JMH
 * Visualizer：https://jmh.morethan.io/
 */
@BenchmarkMode(Mode.All)
@Warmup(iterations = 100, time = 1)
@Measurement(iterations = 100, time = 1)
@Fork(1)
@Threads(4)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class TreeBenchmark {

	private static List<TreeNode> list = Lists.newArrayList();

	static {
		for (int i = 1; i <= 5; i++) {
			TreeNode parent = TreeNode.builder().id("" + i).parentId("0").build();
			list.add(parent);
			for (int j = 1; j <= 10; j++) {
				TreeNode child1 = TreeNode.builder().id(i + "" + j).parentId(parent.getId()).build();
				list.add(child1);
				for (int k = 1; k <= 100; k++) {
					TreeNode child2 = TreeNode.builder().id(i + "" + j + "" + k).parentId(child1.getId()).build();
					list.add(child2);
					for (int m = 1; m <= 10; m++) {
						TreeNode child3 = TreeNode.builder().id(i + "" + j + "" + k + "" + m).parentId(child2.getId())
								.build();
						list.add(child3);
						for (int n = 1; n <= 100; n++) {
							TreeNode child4 = TreeNode.builder().id(i + "" + j + "" + k + "" + m + "" + n)
									.parentId(child3.getId()).build();
							list.add(child4);
						}
					}
				}
			}
		}
		System.out.println("init");
	}

	@Benchmark
	@SneakyThrows
	public void testGroupToTree() {

		List<TreeNode> lists1 = TreeUtil.build(list, "0");
	}

	@Benchmark
	@SneakyThrows
	public void testListToTree() {

		List<TreeNode> lists2 = TreeUtil.listToTree(list, TreeNode::setChildren, null, TreeNode::getId,
				TreeNode::getParentId, (node) -> "0".equals(node.getParentId()));
	}

	public static void main(String[] args) throws RunnerException {

		Options options = new OptionsBuilder().include(TreeBenchmark.class.getSimpleName())
				.resultFormat(ResultFormatType.JSON).build();
		new Runner(options).run();

	}
}