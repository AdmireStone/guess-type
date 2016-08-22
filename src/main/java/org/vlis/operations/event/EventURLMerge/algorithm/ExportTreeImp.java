package org.vlis.operations.event.EventURLMerge.algorithm;

import java.util.Map;

public class ExportTreeImp implements ExportTreeInterface {
	private URLTree urlTree;

	public ExportTreeImp(URLTree urlTree) {
		// TODO Auto-generated constructor stub
		this.urlTree = urlTree;
	}

	public boolean exportURLTree() {
		// TODO Auto-generated method stub
		System.out.println(urlTree.getRootNode().getName());
		Traversal(urlTree.getRootNode());
		return false;
	}

	public void Traversal(URLTreeNode node) {
//		System.out.println("-----------------------------------------------------------------------------");
//		if (!node.getName().equals("root")) {
//			System.out.println(" node : " + node.getName() + " parent :" + node.getParent().getName() + " count : "
//					+ node.getSkipRepeatCount());
//		} else {
//			System.out.println(
//					" node : " + node.getName() + " parent : root node!" + "count : " + node.getSkipRepeatCount());
//		}

		if (node.getChild().size() > 0) {
//			System.out.println("the child length is :" + node.getChild().size());
			for (Map.Entry<String, URLTreeNode> entry : node.getChild().entrySet()) {
//				System.out.println("the child set : " + entry.getKey());
				if (entry.getValue().getGrandson().size() == 0) {
//					System.out.println("node " + entry.getKey() + " grandson is null");
				}
				Traversal(entry.getValue());
			}
		} else {
			StringBuffer aBuffer = new StringBuffer();
			URLTreeNode patten = node;
			while (patten.getParent() != null) {
				aBuffer.append("/" + patten.getName());
				patten = patten.getParent();
			}
			if (aBuffer.toString().contains("*")) {
				System.out.println("\r\n" + aBuffer.toString());
			}

		}

	}
}
