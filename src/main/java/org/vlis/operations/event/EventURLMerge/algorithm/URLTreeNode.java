package org.vlis.operations.event.EventURLMerge.algorithm;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class URLTreeNode {
	//节点名字
		private String name;
		//父亲节点
		private URLTreeNode parent;
		//子节点集合
		private ConcurrentHashMap <String, URLTreeNode> child;
		//孙子节点集合
		private ConcurrentHashMap <String, URLTreeNode> grandson;
		//跳跃重复次数，用于合并子集专用
		private int skipRepeatCount;
		private boolean childsHadCombined=false;

		public URLTreeNode(String name, URLTreeNode parent) {
			// TODO Auto-generated constructor stub
			this.name = name;
			this.parent=parent;
			this.skipRepeatCount=0;
			this.child=new ConcurrentHashMap<String, URLTreeNode>();
			this.grandson=new ConcurrentHashMap<String, URLTreeNode>();
			}
		public void skipRepeatCountAdd() {
			skipRepeatCount++;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "Name : " + name + " Parent : " + parent + " child : " + child.size()+" grandson : "+grandson.size();
		}


		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}


		public URLTreeNode getParent() {
			return parent;
		}


		public void setParent(URLTreeNode parent) {
			this.parent = parent;
		}

		public int getSkipRepeatCount() {
			return skipRepeatCount;
		}


		public void setSkipRepeatCount(int skipRepeatCount) {
			this.skipRepeatCount = skipRepeatCount;
		}
		public boolean isChildsHadCombined() {
			return childsHadCombined;
		}
		public void setChildsHadCombined(boolean childsHadCombined) {
			this.childsHadCombined = childsHadCombined;
		}
		public ConcurrentHashMap<String, URLTreeNode> getChild() {
			return child;
		}
		public void setChild(ConcurrentHashMap<String, URLTreeNode> child) {
			this.child = child;
		}
		public ConcurrentHashMap<String, URLTreeNode> getGrandson() {
			return grandson;
		}
		public void setGrandson(ConcurrentHashMap<String, URLTreeNode> grandson) {
			this.grandson = grandson;
		}
}
