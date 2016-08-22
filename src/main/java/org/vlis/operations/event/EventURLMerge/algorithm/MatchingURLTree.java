package org.vlis.operations.event.EventURLMerge.algorithm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MatchingURLTree {
	private static ConcurrentHashMap<String, URLTree> urlTree = new ConcurrentHashMap<String, URLTree>();
	private static BuildTreeInterface buildTreeInterface = null;
	private static MatchingTreeInterface matchingTreeInterface = null;
	private static URLTree currentTree = null;

	public static Map<String, String> matchingAll(String node, Set<String> urls) throws Exception {
		if (node == null || node.length() <= 0) {
			throw new Exception("输入参数为空");
		}
		Map<String, String> resultMap = new ConcurrentHashMap<String, String>();
		try {
			if (urlTree.containsKey(node)) {
				currentTree = urlTree.get(node);
			} else {
				URLTreeNode templeUrlTreeNode = new URLTreeNode(node, null);
				URLTree templeUrlTree = new URLTree(templeUrlTreeNode);
				urlTree.put(node, templeUrlTree);
				currentTree = templeUrlTree;
			}
			if (currentTree != null) {
				buildTreeInterface = new BuildTreeImp(currentTree);
				matchingTreeInterface = new MatchingTreeImp(currentTree);
				for (String str : urls) {
					buildTreeInterface.buildTree(str);
				}
				for (String str : urls) {
					resultMap.put(str, matchingTreeInterface.matchingURLTree(str));
				}
			} else {
				throw new Exception("不存在树！即没找到，又没新建");
			}

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			throw new Exception("matching all urls exception!");
		}
		return resultMap;
	}

	public static String matchingSingle(String node, String url) throws Exception {
		if (node == null || node.length() <= 0) {
			throw new Exception("输入参数为空");
		}
		String result = null;
		try {
			if (urlTree.containsKey(node)) {
				currentTree = urlTree.get(node);
			} else {
				return null;
			}
			if (currentTree != null) {
				buildTreeInterface = new BuildTreeImp(currentTree);
				matchingTreeInterface = new MatchingTreeImp(currentTree);
				buildTreeInterface.buildTree(url);
				result = matchingTreeInterface.matchingURLTree(url);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			throw new Exception("matching all urls exception!");
		}
		return result;
	}

	public static ConcurrentHashMap<String, URLTree> getUrlTree() {
		return urlTree;
	}

	public static void setUrlTree(ConcurrentHashMap<String, URLTree> urlTree) {
		MatchingURLTree.urlTree = urlTree;
	}

	public static void main(String[] args) throws Exception {

		Set<String> urls = new HashSet<String>();
		//成功
		for (int i = 0; i < 10; i++) {
			String url = "www.baidu.com/inchina/" + i;
			urls.add(url);
		}
		
		//失败
		for (int i = 0; i < 10; i++) {
			String url = "www.baidu.com/inchina/" + i+"/dgl";
			urls.add(url);
		}
		String url = "www. .com/inchina/" + "dgl"+"/qwqe";
		urls.add(url);
		Map<String, String> rst = MatchingURLTree.matchingAll("node0001", urls);
		
		for(String key:rst.keySet()){
			System.out.print("（"+key+",");
			System.out.println(rst.get(key)+")");
		}
	}
}
