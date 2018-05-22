package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author wang
 * 随着...，...
 * 应经改为后果 Lizy
 */

public class YingXiangSolution7 {

	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		s = Print.print(word.parent.parent.child.get(1));
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		Tree t = word.parent.parent.parent;
		if (t.child.size() == 3) {
			t = t.child.get(2);
			s = Print.print(t.child.get(0)) + "@";
			for (int i = 1; i < t.child.size(); i++)
				s += Print.print(t.child.get(i));
		} else {
			s = Print.print(t.child.get(Math.min(2, t.child.size() - 1))) + "@";
			for (int i = 3; i < t.child.size(); i++)
				s += Print.print(t.child.get(i));
		}
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence);
	}
}
