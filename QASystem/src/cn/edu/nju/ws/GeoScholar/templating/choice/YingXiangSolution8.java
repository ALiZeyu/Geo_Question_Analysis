package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author wang
 * 影响...的是
 * 
 */
public class YingXiangSolution8 {

	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		Tree t = word.parent;
		while (!t.parent.content.equals("IP") && !(t.parent.content.equals("VP") && t.parent.child.size() > 1)) t = t.parent;
		s = Print.print(t.parent.child.get(t.parent.child.indexOf(t) + 1)) + "@";
		while (t.parent.child.indexOf(t) == t.parent.child.size() - 1) t = t.parent;
		String temp = Print.print(t.parent.child.get(t.parent.child.indexOf(t) + 1));
		if (!temp.endsWith("的") && !s.contains(temp) && !temp.contains(s)) s += temp;
		return s;
	}
	
	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no;
		while (k < sentence.size() && !sentence.get(k).split("_")[0].equals("是") && !sentence.get(k).split("_")[0].equals("有")) k++;
		if (k == sentence.size()) k = word.no + 1;
		for (int i = k + 1; i < sentence.size(); i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence);
	}
}
