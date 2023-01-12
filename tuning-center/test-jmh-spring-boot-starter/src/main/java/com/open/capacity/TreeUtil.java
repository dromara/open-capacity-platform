package com.open.capacity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import lombok.experimental.UtilityClass;

/**
 * @author wangwen
 * @date 2022年10月22日16:34:11
 */
@UtilityClass
public class TreeUtil {

	/**
	 * 递归 listToTree 模型 平铺转树
	 * 
	 * @param source      源数据
	 * @param setChildFun 设置递归的方法
	 * @@param setFlag 设置标记
	 * @param idFn   获取id的方法
	 * @param pidFn  获取父id的方法
	 * @param isRoot 获取根节点的方法
	 */
	public <F, T> List<F> listToTree(List<F> source, BiConsumer<F, List<F>> setChildFun, BiConsumer<F, Integer> setFlag,
			Function<F, T> idFn, Function<F, T> pidFn, Predicate<F> isRoot) {
		List<F> tree = new ArrayList<>();
		Map<T, List<F>> map = new HashMap<>(source.size());
		for (F f : source) {
			if (isRoot.test(f)) {
				tree.add(f);
			} else {
				List<F> tempList = map.getOrDefault(pidFn.apply(f), new ArrayList<>());
				tempList.add(f);
				map.put(pidFn.apply(f), tempList);
			}
		}
		tree.forEach(l -> assembleTree(l, map, setChildFun, idFn, setFlag, 0));
		return tree;
	}

	/**
	 * 组装树
	 * 
	 * @param <F>
	 * @param <T>
	 * @param current
	 * @param map
	 * @param setChildListFn
	 * @param idFn
	 * @param setFlag
	 * @param flag
	 */
	private <F, T> void assembleTree(F current, Map<T, List<F>> map, BiConsumer<F, List<F>> setChildListFn,
			Function<F, T> idFn, BiConsumer<F, Integer> setFlag, Integer flag) {
		List<F> fs = map.get(idFn.apply(current));
		setChildListFn.accept(current, fs);
		if (!CollectionUtils.isEmpty(fs)) {
			fs.forEach(l -> assembleTree(l, map, setChildListFn, idFn, setFlag, flag));
		}
		if (setFlag != null) {
			setFlag.accept(current, flag);
		}
	}

	/**
	 * 循环 loopToTree 模型 平铺转树
	 * 
	 * @param <T>
	 * @param source
	 * @param idFun
	 * @param pidFun
	 * @param getChildFun
	 * @param setChildFun
	 * @param isRoot
	 * @return
	 */
	public static <T> List<T> loopToTree(List<T> source, BiConsumer<T, List<T>> setChildFun, Function<T, ?> idFun,
			Function<T, ?> pidFun, Function<T, List<T>> getChildFun, Predicate<T> isRoot) {

		final List<T> ret = new ArrayList<>();
		final Map<Object, T> map = new HashMap<>();

		source.forEach(t -> {
			Optional.ofNullable(isRoot).map(r -> {
				if (isRoot.test(t)) {
					ret.add(t);
				}
				return r;
			}).orElseGet(() -> {
				Optional.ofNullable(pidFun.apply(t)).orElseGet(() -> {
					ret.add(t);
					return null;
				});
				return null;
			});
			map.put(idFun.apply(t), t);
		});

		source.forEach(t -> {
			map.computeIfPresent(pidFun.apply(t), (k, v) -> {
				Optional.ofNullable(getChildFun.apply(v)).orElseGet(() -> {
					final List<T> list = new ArrayList<>();
					setChildFun.accept(v, list);
					return list;
				}).add(t);
				return v;
			});
		});

		return ret;
	}

	/**
	 * 列表数据构建树
	 *
	 * @param listNodes 传入的树节点列表
	 * @return
	 */
	public <T extends TreeNode> List<T> build(List<T> listNodes, Object root) {
		List<T> treeNodes = transToTree(listNodes);
		return findRootNode(treeNodes, root);
	}

	public <F, T> List<F> groupToTree(List<F> source, BiConsumer<F, List<F>> setChildFun,
			BiConsumer<F, Integer> setFlag, Function<F, T> idFn, Function<F, T> pidFn, Predicate<F> isRoot) {
		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}
		Map<T, F> sourceMap = source.stream()
				.collect(Collectors.toMap(idFn, Function.identity(), (key1, key2) -> key2));
		Map<T, List<F>> pIdToChildrenListMap = source.stream()
				.collect(Collectors.groupingBy(node -> pidFn.apply(node)));
		for (Map.Entry<T, List<F>> entry : pIdToChildrenListMap.entrySet()) {
			F treeNode = sourceMap.get(entry.getKey());
			if (treeNode == null) {
				continue;
			}
			setChildFun.accept(treeNode, entry.getValue().stream().collect(Collectors.toList()));
		}
		List<F> treeNodes = pIdToChildrenListMap.values().stream().flatMap(Collection::stream)
				.collect(Collectors.toList());
		List<F> rootNodes = new ArrayList<>();
		for (F treeNode : treeNodes) {
			if (isRoot.test(treeNode)) {
				rootNodes.add(treeNode);
			}
		}
		return rootNodes;
	}

	private <T extends TreeNode> List<T> findRootNode(List<T> treeNodes, Object root) {
		List<T> rootNodes = new ArrayList<>();
		for (T treeNode : treeNodes) {
			if (isRootNode(root, treeNode)) {
				rootNodes.add(treeNode);
			}
		}
		return rootNodes;
	}

	private static <T extends TreeNode> boolean isRootNode(Object root, T treeNode) {
		String parentId = treeNode.getParentId();
		return (root == null && (parentId == null || "null".equals(parentId)))
				|| (root != null && root.equals(parentId));
	}

	public <T extends TreeNode> List<T> transToTree(List<T> treeNodes) {
		if (CollectionUtils.isEmpty(treeNodes)) {
			return Collections.emptyList();
		}
		Map<String, T> sourceMap = treeNodes.stream()
				.collect(Collectors.toMap(T::getId, Function.identity(), (key1, key2) -> key2));
		Map<String, List<T>> pIdToChildrenListMap = treeNodes.stream()
				.collect(Collectors.groupingBy(node -> node.getParentId() == null ? "null" : node.getParentId()));
		for (Map.Entry<String, List<T>> entry : pIdToChildrenListMap.entrySet()) {
			T treeNode = sourceMap.get(entry.getKey());
			if (treeNode == null) {
				continue;
			}
			treeNode.setChildren(entry.getValue().stream().collect(Collectors.toList()));
		}
		return pIdToChildrenListMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
	}

	/**
	 * 树转平铺 treeToList
	 * 
	 * @param source             源数据
	 * @param target             目标容器
	 * @param childListFn        递归调用方法
	 * @param addTargetCondition 添加到容器的判断方法
	 */
	public static <F> void treeToList(List<F> source, List<F> target, Function<F, List<F>> getChildFun,
			Predicate<F> isAdd) {
		treeLoop(source, getChildFun, (l) -> {
			if (isAdd.test(l)) {
				target.add(l);
			}
		});
	}

	/**
	 * 树的简易递归,listen会回调当前对象
	 * 
	 * @param source      源数据
	 * @param childListFn get方法
	 * @param listen      回调函数
	 */
	public static <F> void treeLoop(List<F> source, Function<F, List<F>> getChildFun, Consumer<F> listen) {
		treeLoop(source, getChildFun, listen, null);
	}

	public static <F> void treeLoop(List<F> source, Function<F, List<F>> getChildFun, Consumer<F> preListen,
			Consumer<F> postFun) {
		if (CollectionUtils.isEmpty(source)) {
			return;
		}
		source.forEach(l -> {
			Optional.ofNullable(preListen).ifPresent(s -> s.accept(l));
			treeLoop(getChildFun.apply(l), getChildFun, preListen, postFun);
			Optional.ofNullable(postFun).ifPresent(s -> s.accept(l));
		});
	}

}
