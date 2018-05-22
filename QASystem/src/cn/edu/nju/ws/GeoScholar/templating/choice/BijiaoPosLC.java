package cn.edu.nju.ws.GeoScholar.templating.choice;

import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class BijiaoPosLC {

	private static String findFirst(Tree word) {
		Tree t = word.parent.parent.parent.parent;
		int index = t.child.indexOf(word.parent.parent.parent);
		String s = "";
		for (int i = 0; i < index; i++)
			s += Print.print(t.child.get(i));
		Tree node = t;
		t = t.parent;
		index = t.child.indexOf(node);
		String s1 = "";
		for (int i = 0; i < index; i++)
			s1 += Print.print(t.child.get(i));
		return s1 + s;
	}
	
	private static String findSecond(Tree word) {
		Tree t = word.parent.parent;
		int index = t.child.indexOf(word.parent);
		String s = "";
		for (int i = 0; i < index; i++)
			s += Print.print(t.child.get(i));
		return s;
	}
	
	private static String findLast(Tree word) {
		return word.content;
	}
	
	public static String print(Tree node) {
		return (findFirst(node) + "@" + findSecond(node) + "@@@" + findLast(node));
	}
}
