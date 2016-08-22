package org.vlis.operations.event.EventURLMerge.algorithm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BuildTreeImp implements BuildTreeInterface {
	private URLTree tree;
	// 路径纠正参数
	private int rightRatio = 1;
	// 子集合并参数
	private int combineRatio = 4;

	public BuildTreeImp(URLTree tree) {
		// TODO Auto-generated constructor stub
		this.tree = tree;
	}
	
	/**
	 *1、是否已经包含检查节点
	 *	包含
	 *		将节点指针向下移动，继续循环
	 *	不包含
	 * 		检查是否需要合并
	 * 			需要合并
	 * 				检查是否到达合并数量
	 * 					到达合并数量 
	 * 						新建合并目标节点，判断单个节点是否需要合并
	 * 							需要合并
	 * 								删除当前节点
	 * 							不需要合并
	 * 								节点指针向下移动，继续循环
	 * 					未到达合并数量
	 * 						新建节点
	 * 			不需要合并
	 * 				新建节点
	 */
	public boolean buildTree(String url) {
		String[] urlArray = url.split("/");
		URLTreeNode templeParent = tree.getRootNode();
		URLTreeNode templeCurrent = tree.getRootNode();
		URLTreeNode templeChild = tree.getRootNode();
		boolean containsChildKey, containsGrandsonKey;
		HashMap<String, URLTreeNode> templeResultset = null;
		URLTreeNode needDeleteURLTreeNode = null;
		// TODO Auto-generated method stub
		if (urlArray != null && rightRatio < urlArray.length) {
			for (int i = rightRatio; i < urlArray.length; i++) {
				containsChildKey = templeParent.getChild().containsKey(urlArray[i]);
				if (containsChildKey) {
					// 如果包含，則檢查子集是否包含
					// URLTreeNodeList.get(urlArray[i]).getChild().contains(urlArray[i+1]);
					// System.out.println("+++++++++++++++++++contains
					// key!!!"+urlArray[i]);
					templeParent = templeParent.getChild().get(urlArray[i]);
					continue;
				} else {
					// System.out.println("+++++++++++++++++++not contains
					// key!!!"+urlArray[i]);
					if (i < urlArray.length - 1) {
						// new URLTreeNode
						// 如果不存在这个节点，则需要增加这个节点，同时要检查其父节点的孙子节点上是否存在合并项
						containsGrandsonKey = templeParent.getGrandson().containsKey(urlArray[i + 1]);
						if (containsGrandsonKey) {
							// 包含合并项，输出的是孙子节点
							// System.out.println("contain URLTreeNode :
							// "+urlArray[i + 1]);
							templeChild = templeParent.getGrandson().get(urlArray[i + 1]);
							templeChild.skipRepeatCountAdd();
							// System.out.println("11111111111111111111111111111---"+templeChild.getSkipRepeatCount());
							if (templeChild.getSkipRepeatCount() >= combineRatio) {
								if (templeChild.getSkipRepeatCount() == combineRatio) {
									System.out.println("******************************************need to combine!");
									// 如果跳跃重复次数大于合并参数，则需要遍历子集，进行合并，并且要将这个类释放掉；不释放类会影响内存,先删除所有需要合并的节点，再
									// 循环删除子节点
								//	PatternTable.combineTable("root"+url.replace(urlArray[i],"*"),templeParent.getName(), templeChild.getName());
									templeCurrent =tree.addNode("*" + urlArray[i + 1],templeParent);
									// 新建节点，维持关系，新节点其子节点的父亲指向新节点
									for (Map.Entry<String, URLTreeNode> entry :templeParent.getChild().entrySet())
									{
										if (entry.getValue().getChild() != null) {
											if (entry.getValue().getChild().containsKey(templeChild.getName())) {
												tree.combineNode(templeParent, entry.getValue(), templeChild,templeCurrent);
											} else {
												System.out.println("单个孩子同父不同母");
												continue;
											}
										} else {
											System.out.println("子集的子集为null");
											continue;
										}	
									}
								} else {
								//	PatternTable.combineTable("root"+url.replace(urlArray[i],"*"),templeParent.getName(), templeChild.getName());
									templeCurrent = templeChild.getParent();
									System.out.println("已经合并过了，此节点" + templeCurrent.getName() + "满足正则表达式");
								}
							} else {
								
								templeCurrent = tree.addNode(urlArray[i], templeParent);
//										new URLTreeNode(urlArray[i], templeParent);
//								templeParent.getChild().put(templeCurrent.getName(), templeCurrent);
//								if (templeParent.getParent() != null) {
//									templeParent.getParent().getGrandson().put(templeCurrent.getName(), templeCurrent);
//								}
								
								templeCurrent.getChild().put(templeChild.getName(), templeChild);
								System.out.println("需要合并，但没达到合并次数");
							}
						} else {
							templeCurrent = tree.addNode(urlArray[i], templeParent);
							System.out.println("new URLTreeNode : " + templeCurrent.getName() + "parent is : "
									+ templeCurrent.getParent().getName());
						}
					} else {
						if (templeParent.isChildsHadCombined()) {
							 System.out.println("最后一项已经被合并过了");
						} else {
							if (templeParent.getChild().size() >= combineRatio) {
								// System.out.println("正在合并最后一项");
								templeParent.setChildsHadCombined(true);
								Iterator<Map.Entry<String, URLTreeNode>> it = templeParent.getChild().entrySet().iterator();
								while (it.hasNext()) {
									Map.Entry<String, URLTreeNode> entry = it.next();
									if (entry.getValue().getChild().size() == 0) {
										it.remove();
									}
								}
								templeCurrent =tree.addNode("*", templeParent); 
						//		PatternTable.combineLastOne(url,templeParent.getName());
						//		combinedURL=true;
							} else {
								templeCurrent =tree.addNode(urlArray[i], templeParent); 
							}
						}
					}
				}
				templeParent = templeCurrent;
			}
		} else {
			System.out.println("读取字符串为null！");
		}	
		return false;
	}

}
