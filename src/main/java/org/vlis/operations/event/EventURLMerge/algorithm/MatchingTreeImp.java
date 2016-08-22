package org.vlis.operations.event.EventURLMerge.algorithm;


public class MatchingTreeImp implements MatchingTreeInterface {
	private URLTree urlTree;
	private int rightRatio = 1;

	public MatchingTreeImp(URLTree urlTree) {
		// TODO Auto-generated constructor stub
		this.urlTree = urlTree;
	}

	public String matchingURLTree(String param) {
		// TODO Auto-generated method stub
		String[] arrayString = null;
		int i = -1;
		if (param != null) {
			arrayString = param.split("/");
		}
		if (arrayString != null && rightRatio < arrayString.length) {
			URLTreeNode templeParent = urlTree.getRootNode();
			URLTreeNode templeCurrent = urlTree.getRootNode();
			URLTreeNode templeChild = urlTree.getRootNode();
			URLTreeNode templeGrandson = urlTree.getRootNode();
			StringBuffer result = new StringBuffer();
			for (i = rightRatio; i < arrayString.length; i++) {
				// 没有到结尾
				if (i < arrayString.length - 1) {
					if (templeParent.getChild().containsKey(arrayString[i])) {
						templeCurrent = templeParent.getChild().get(arrayString[i]);
						// System.out.println("++"+arrayString[i]);
						result.append("/" + arrayString[i]);
					} else if (templeParent.getChild().containsKey("*")) {
						// System.out.println("++*");
						templeCurrent = templeParent.getChild().get("*");
						result.append("/*");
					} else if (templeParent.getChild().containsKey("*" + arrayString[i + 1])) {
						// System.out.println("++*"+arrayString[i+1]);
						templeCurrent = templeParent.getChild().get("*" + arrayString[i + 1]);
						result.append("/*" + arrayString[i + 1]);
					} else {
						System.out.println("不符合树");
						break;
					}
				} else {
					if (templeParent.getChild().containsKey(arrayString[i])) {
						// System.out.println("++"+arrayString[i]);
						templeCurrent = templeParent.getChild().get(arrayString[i]);
						result.append("/" + arrayString[i]);
					} else if (templeParent.getChild().containsKey("*")) {
						// System.out.println("++*");
						templeCurrent = templeParent.getChild().get("*");
						result.append("/*");
					} else {
						System.out.println("不符合树");
						break;
					}
				}
				templeParent = templeCurrent;
			}
			if (i == arrayString.length) {
				System.out.println("输入 : " + param);
				System.out.println("匹配树，最终结果为 : " + result.toString());
				return result.toString();
			} else {
				System.out.println("输入 : " + param);
				System.out.println("不符合树，最终匹配结果为本身 : " + param);
				System.out.println("匹配进度 : " + result.toString());
				return param;
			}
		}
		return "";
	}

}
