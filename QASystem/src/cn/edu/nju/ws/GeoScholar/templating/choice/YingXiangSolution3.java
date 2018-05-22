package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author wang
 * ...可(利于，有助于，使)...
 */
public class YingXiangSolution3 {

	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 2, k1 = word.no - 2;
		if (sentence.get(k1).split("_")[0].equals("可")) k1--;
		if (sentence.get(k1).split("_")[0].equals("会")) k1--;
		if (sentence.get(k1).split("_")[0].equals("能")) k1--;
		if (sentence.get(k1).split("_")[0].equals("能够")) k1--;
		if (sentence.get(k1).split("_")[0].equals("将")) k1--;
		Pattern pattern = Pattern.compile("，|则|那么|是");
		while (k >= 0 && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k--;
		if (k != k1) {
			for (int i = k1; i > k && !pattern.matcher(sentence.get(i).split("_")[0]).matches(); i--)
				s = sentence.get(i).split("_")[0] + s;
		} else {
			for (int i = 0; i < k; i++)
				s += sentence.get(i).split("_")[0];
		}
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		Tree t = word.parent.parent;
		//如果触发词是[利于|不利于]这样，后面紧更一个动词比如“上海会展业的发展	有利于优化产业结构、带动工业发展”，暂认为就没有受影响体，直接填入结果槽
		if(word.content.endsWith("利于") &&sentence.get(word.no).split("_")[1].startsWith("V")){
			s+="@";
			for (int i = word.no; i < sentence.size(); i++)
				s += sentence.get(i).split("_")[0];
			return s;
		}
		while (t.child.size() == 1 && t.parent != null) t = t.parent;
		if (t.child.size() == 2) {
			t = t.child.get(1);
			while (t.child.size() == 1) t = t.child.get(0);
			if (t.child.size() == 0) {
				s = t.content;
			} else {
				if (t.child.get(0).content.equals("VV")) {
					for (int i = 1; i < t.child.size(); i++)
						s += Print.print(t.child.get(i));
					s += "@" + Print.print(t.child.get(0));
				} else {
					for (int i = 0; i < t.child.size() - 1; i++)
						s += Print.print(t.child.get(i));
					s += "@" + Print.print(t.child.get(t.child.size() - 1));
				}
			}
		} else {
			s = Print.print(t.child.get(1)) + "@";
			for (int i = 2; i < t.child.size(); i++)
				s += Print.print(t.child.get(i));
		}
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence);
	}
}
