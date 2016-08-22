package org.vlis.operations.event.EventURLMerge.algorithm;

import java.util.Map;

public class URLTree {
	private URLTreeNode rootNode;

	public URLTree(URLTreeNode rootNode) {
		// TODO Auto-generated constructor stub
		this.rootNode = rootNode;
	}

	/**
	 * 新增节点只在新增的时候维持与父节点和祖父节点的关系，如果需要维持与孩子节点或者孙子节点的关系，请自行处理
	 * 1、new一个新节点 
	 * 2、指明其父节点
	 * 3、父节点的孩子节点集合中添加此节点 
	 * 4、祖父节点的孙子节点集合中添加此节点
	 * @return
	 */
	public URLTreeNode addNode(String nodeName, URLTreeNode templeParent) {
		if (templeParent == null) {
			return null;
		}
		URLTreeNode newNode = null;
		try {
			newNode = new URLTreeNode(nodeName, templeParent);
			templeParent.getChild().put(nodeName, newNode);
			if (templeParent.getParent() != null) {
				templeParent.getParent().getGrandson().put(nodeName, newNode);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return newNode;
	}

	/**
	 * 暂时不用
	 * 
	 * 停止，不要写删除，因为删除要维护父节点中孙子节点这一层关系，又不能删除 维护五级之间的引用关系 只对单个的节点起作用，其他的关乎到
	 * 很多复杂节点问题的，暂且放一边
	 * 1、删除祖父节点对自己的引用
	 * 2、删除父节点对自己和对孩子的引用
	 * 3、删除子集对父节点，对孩子的引用，以及对孙子节点的引用 
	 * 4、删除孩子对父节点的引用
	 * @param templeParent
	 *            父节点
	 * @param templeCurrent
	 *            需要删除的节点
	 * @param templeChild
	 *            需要删除节点的子节点
	 * @return
	 */
	@Deprecated
	private boolean deleteNode(URLTreeNode templeParent, URLTreeNode templeCurrent, URLTreeNode templeChild) {
		// 对祖父节点的操作
		if (templeParent.getParent() != null) {
			templeParent.getParent().getGrandson().remove(templeCurrent.getName());
		}
		// 对父节点的操作
		templeParent.getChild().remove(templeCurrent.getName());
		templeParent.getGrandson().remove(templeChild.getName());

		return true;
	}

	/**
	 * 写合并 这里是合并的过程，至于要满足什么条件时候才合并，需要在service层控制逻辑里面写代码
	 * 检查关系重复添加或者删除！！！！----------------------------------------------------------------------------------
	 * 1、操作祖父节点
	 * 2、操作父节点,子集中删除源节点，添加目标节点；孙子集不用改变
	 * 3、操作源节点,父节点清空，子节点集合清空，孙子节点集合清空
	 * 4、操作目标节点
	 * 5、操作儿子节点
	 * 6、操作孙子节点,儿子中的每个孙子节点都在目标节点中注册
	 * @return 合并成功返回true，否则返回false
	 */
	public boolean combineNode(URLTreeNode templeParent, URLTreeNode templeCurrentSourceNode, URLTreeNode templeChild,
			URLTreeNode templeCurrentDestinationNode) {
		try {
			// 操作祖父节点
			if (templeParent.getParent() != null) {
				templeParent.getParent().getGrandson().remove(templeCurrentSourceNode.getName());// ------1
				templeParent.getParent().getGrandson().put(templeCurrentDestinationNode.getName(), // +++++1
						templeCurrentDestinationNode);
			}
			// 操作父节点,子集中删除源节点，添加目标节点；孙子集不用改变
			templeParent.getChild().remove(templeCurrentSourceNode.getName());// -----2
			templeParent.getChild().put(templeCurrentDestinationNode.getName(), templeCurrentDestinationNode);// ++++2
			templeParent.getGrandson().remove(templeChild.getName());// -----3
			// 操作源节点,父节点清空，子节点集合清空，孙子节点集合清空
			templeCurrentSourceNode.setParent(null);// ----4
			templeCurrentSourceNode.getChild().clear();// ----5
			templeCurrentSourceNode.getGrandson().clear();// ----6
			// 操作目标节点，
			templeCurrentDestinationNode.setParent(templeParent);// ++++3同时----7
			templeCurrentDestinationNode.getChild().put(templeChild.getName(), templeChild);// +++++4
			// 操作儿子节点
			templeChild.setParent(templeCurrentDestinationNode);// +++++++5
			templeChild.getParent().getParent().getGrandson().put(templeChild.getName(), templeChild);// ++++6
			// 操作孙子节点,儿子中的每个孙子节点都在目标节点中注册
			for (Map.Entry<String, URLTreeNode> entry : templeChild.getChild().entrySet()) {
				if (!templeCurrentDestinationNode.getChild().containsKey(entry.getKey())) {
					templeCurrentDestinationNode.getChild().put(entry.getKey(), entry.getValue());// +++++7
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean isNeedDelete() {
		return true;
	}

	public URLTreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(URLTreeNode rootNode) {
		this.rootNode = rootNode;
	}
}
