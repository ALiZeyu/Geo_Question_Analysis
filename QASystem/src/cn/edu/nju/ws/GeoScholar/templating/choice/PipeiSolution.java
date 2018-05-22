package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;

import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
import cn.edu.nju.ws.GeoScholar.templating.common.findCueWord;

public class PipeiSolution {

	public static String findFirst(Tree word, ArrayList<String> sentence) {
		Tree t = word.parent.parent;
		while (t.parent != null && !((t.parent.content.equals("IP") || t.parent.content.equals("NP") ||
				(t.parent.content.equals("VP") && t.parent.parent.content.equals("ROOT") && !t.parent.child.get(0).equals("ADVP"))) && t.parent.child.size() > 1 && t.parent.child.indexOf(t) > 0)) t = t.parent;
		//if (t.parent.content.equals("IP")) t = t.parent;
		String s = "";
		Tree temp = null;
		if (t.parent == null) {
			while (t.child.size() == 1) t = t.child.get(0);
			s = Print.print(findCueWord.findFirstNP(t.child.get(0), sentence));
		} else
		if (t.parent.child.get(t.parent.child.indexOf(t) - 1).content.equals("PP")) {
			int i = t.parent.child.indexOf(t) - 1;
			while (t.parent.child.get(i).content.equals("PP")) {
				s = Print.print(t.parent.child.get(i)) + s;
				i--;
			}
			s = Print.print(findCueWord.findFirstNP(t.parent.child.get(i), sentence)) + s;
			return s;
		} else {
			for (int i = t.parent.child.indexOf(t) - 1; i >= 0; i--) {
				if (t.parent.child.get(i).equals("NP")) {
					temp = t.parent.child.get(i);
					break;
				}
			}
			for (int i = t.parent.child.indexOf(t) - 1; i >= 0; i--) {
				temp = findCueWord.findFirstNP(t.parent.child.get(i), sentence);
				if (temp != null) break;
			}
			if (temp == null)
				s = Print.print(t.parent.child.get(t.parent.child.indexOf(t) - 1));
			else
				s = Print.print(temp);
		}
		int i = 0;
		while (i < sentence.size() - 1 && !s.startsWith(sentence.get(i).split("_")[0])) i++;
		if (i > 0 && sentence.get(i - 1).startsWith("、")) {
			i--;
			s = "、" + s;
			for (int j = i - 1; j >=0 && sentence.get(j).split("_")[1].startsWith("N"); j--)
				s = sentence.get(j).split("_")[0] + s;
		}
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		if (word.no == sentence.size()) return "";
		Tree t = word.parent.parent;
		while (!t.parent.content.equals("ROOT") && !t.content.equals("VP")) t = t.parent;
		return t.child.size() > 1 ? Print.print(t.child.get(1)) : sentence.get(word.no).split("_")[0];
	}
	
	public static String print(Tree node, ArrayList<String> sentence) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence);
	}
}
