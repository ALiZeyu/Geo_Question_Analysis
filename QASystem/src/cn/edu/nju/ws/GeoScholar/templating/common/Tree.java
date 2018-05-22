package cn.edu.nju.ws.GeoScholar.templating.common;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class Tree {
	public String content;
	public Tree parent;
	public int no;
	public List<Tree> child = new ArrayList<Tree>();
	
	public static List<Tree> getLeaves(Tree t) {
		List<Tree> l = new ArrayList<Tree>();
		if (t.child.size() > 0) {
			for (int i = 0; i < t.child.size(); i++)
				l.addAll(getLeaves(t.child.get(i)));
		} else
			l.add(t);
		return l;
	}

	// 插入右子树
	public void addRightChild(Tree c) {
		c.parent = this;
		this.child.add(c);
	}

	// 插入左子树
	public void addLeftChild(Tree c) {
		c.parent = this;
		this.child.add(0, c);
	}

	// 从节点向下找相应label的子节点
	public static Tree getDownByLabel(Tree node, String label) {
		while (node.child.size() != 0 && !node.content.equals(label))
			node = node.child.get(0);
		return node.child.size()== 0?null:node;
	}

	// 从节点向上找相应label的父节点
	public static Tree getUpByLabel(Tree node, String label) {
		while (node!=null && !(node.content.equals(label) && node.parent != null && !node.parent.content.equals(label)))
			node = node.parent;
		return node;
	}
	//返回左兄弟的content,如果没有左兄弟那么就返回"null"
	public static String getLeftBro(Tree node){
		if(node.parent==null || node.parent.child.indexOf(node)<=0)
			return "null";
		int index=node.parent.child.indexOf(node);
		Tree bro=node.parent.child.get(index-1);
		return bro.content;
	}
	
	public static List<Tree> getRightBrosList(Tree node){
		//如果没有父节点或者没有右兄弟，返回null
		if(node.parent==null && node.parent.child.indexOf(node)==node.parent.child.size()-1)
			return null;
		List<Tree> list=new ArrayList<>();
		int id=node.parent.child.indexOf(node);
		for(int i=id+1;i<node.parent.child.size();i++) 
			list.add(node.parent.child.get(i));
		return list;
	}

	private static String traversal(Tree t) {
		String s = "";
		if (t.child.size() > 0) {
			s += "(" + t.content + " ";
			for (int i = 0; i < t.child.size(); i++)
				s += traversal(t.child.get(i));
			s += ")";
		} else
			s += t.content;
		return s;
	}
	
	public static Tree findNodeByNo(Tree t, int no) {
		if (t.no == no) return t;
		else {
			for (int i = 0; i < t.child.size(); i++) {
				Tree temp = findNodeByNo(t.child.get(i), no);
				if (temp != null)
					return temp;
			}
		}
		return null;
	}
	
	public String toString() {
		String s1 = "";
		if (this.child.size() > 0) {
			String s = "(";
			for (int i = 0; i < this.child.size(); i++)
				s += traversal(this.child.get(i));
			s += " )";
			for (int i = 0; i < s.length() - 1; i++) {
				s1 += s.charAt(i);
				if (s.charAt(i) == '(' && s.charAt(i + 1) == '(')
					s1 += " ";
				if (s.charAt(i) == ')' && s.charAt(i + 1) == '(')
					s1 += " ";
			}
			s1 += s.charAt(s.length() - 1);
		}
		else
			s1 = "( )";
		try {
			s1 = new String(s1.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s1;
	}
	
}
