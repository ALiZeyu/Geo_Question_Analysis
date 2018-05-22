package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.HashSet;

import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class BijiaoWordXiangtong {
	//向前找到[与|和]，取之前的名词和DT， 
	public static String findFirst(ArrayList<String> sentence, Tree word) {
		String s = "";
		String cueWord = word.content + "_" + word.parent.content;
		int index = sentence.indexOf(cueWord) - 1;
		while (index >= 0 && (!sentence.get(index).split("_")[0].equals("与") && !sentence.get(index).split("_")[0].equals("和"))) index--;
		if (index < 0) {
			Tree t = word.parent.parent;
			while (!t.parent.content.equals("IP")) t = t.parent;
			/*s = Print.print(t.child.get(0).child.get(0));
			Tree node = word.parent.parent;
			t = t.child.get(t.child.indexOf(node) - 1);
			if (t.child.get(0).content.equals("DNP") && t.child.size() == 2)
				s = Print.print(t.child.get(0).child.get(0));
			else*/
			s = Print.print(t.parent.child.get(t.parent.child.indexOf(t) - 1));
		} else {
			int i = index - 1;
			while (i > 0 && !(sentence.get(i).split("_")[1].startsWith("N") || sentence.get(i).split("_")[1].startsWith("D"))) i--;
			while (i >=0 && (sentence.get(i).split("_")[1].startsWith("N") || sentence.get(i).split("_")[1].startsWith("D")) && !sentence.get(i).split("_")[0].startsWith("图")) {
				s = sentence.get(i).split("_")[0];
				i--;
				if (i < 0) break;
			}
			i += 2;
			HashSet<String> set = new HashSet<String>();
			set.add("①");set.add("②");set.add("④");	set.add("③");
			if (set.contains(sentence.get(i).split("_")[0])) s += sentence.get(i).split("_")[0];
		}
		return s;
	}
	//向前找到[与|和]，就取之后的一个词6的一笔
	public static String findSecond(ArrayList<String> sentence, Tree word) {
		String s = "";
		String cueWord = word.content + "_" + word.parent.content;
		int index = sentence.indexOf(cueWord) - 1;
		while (index >= 0 && (!sentence.get(index).split("_")[0].equals("与") && !sentence.get(index).split("_")[0].equals("和"))) index--;
		if (index >= 0) {
			int i = index + 1;
			if (sentence.get(i).split("_")[1].startsWith("N")) {
				s = sentence.get(i).split("_")[0];
				i++;
			}
		}
		return s;
	}
	//从触发词向前找[与|和]，找到之后再向前移动两个位置，
	public static String findThird(ArrayList<String> sentence, Tree word) {
		String s = "";
		String cueWord = word.content + "_" + word.parent.content;
		int index = sentence.indexOf(cueWord) - 1;
		while (index >= 0 && (!sentence.get(index).split("_")[0].equals("与") && !sentence.get(index).split("_")[0].equals("和"))) index--;
		if (index < 0) {
			Tree t = word.parent.parent.parent;
			Tree node = word.parent.parent;
			t = t.child.get(t.child.indexOf(node) - 1);
			s = Print.print(t.child.get(t.child.size() - 1));
		} else {
			int i = index - 2;
			HashSet<String> set = new HashSet<String>();
			set.add("①");
			set.add("②");
			set.add("④");
			set.add("③");
			while (i >=0 && (sentence.get(i).split("_")[1].startsWith("N") || sentence.get(i).split("_")[1].startsWith("D")) && !sentence.get(i).split("_")[0].startsWith("图")) {
				if (set.contains(sentence.get(i + 1).split("_")[0])) break;
				s = sentence.get(i + 1).split("_")[0] + s;
				i--;
				if (i < 0) break;
			}
		}
		return s;
	}
	//向前找到[和|与]，取之后的第二个词在向后找名词
	public static String findFourth(ArrayList<String> sentence, Tree word) {
		String s = "";
		String cueWord = word.content + "_" + word.parent.content;
		int index = sentence.indexOf(cueWord) - 1;
		while (index >= 0 && (!sentence.get(index).split("_")[0].equals("与") && !sentence.get(index).split("_")[0].equals("和"))) index--;
		if (index >= 0) {
			int i = index + 2;
			while (i < word.no - 1 && !sentence.get(i).split("_")[1].startsWith("N")) i++;
			while (i < word.no - 1) {
				if(!sentence.get(i).split("_")[1].equals("AD"))
					s += sentence.get(i).split("_")[0];
				i++;
			}
		}
		return s;
	}
	
	//就是触发词
	public static String findLast(Tree word) {
		return word.content;
	}
	
	public static String print(ArrayList<String> sentence, Tree node) {
		return findFirst(sentence, node) + "@" + findSecond(sentence, node) + "@" + findThird(sentence, node) + "@" + findFourth(sentence, node) + "@" +findLast(node);
	}
}
