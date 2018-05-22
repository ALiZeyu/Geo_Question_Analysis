package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;

import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class BijiaoWordTongshi {
	
	public static String findFirst(ArrayList<String> sentence, Tree word) {
		String s = "";
		String cueWord = word.content + "_" + word.parent.content;
		int index = sentence.indexOf(cueWord) - 1;
		while (index >= 0 && !sentence.get(index).split("_")[1].equals("CC")) index--;
		int i = index - 1;
		while (sentence.get(i).split("_")[1].startsWith("N") || sentence.get(i).split("_")[1].startsWith("D")) {
			s = sentence.get(i).split("_")[0] + s;
			i--;
		}
		return s;
	}
	
	public static String findSecond(ArrayList<String> sentence, Tree word) {
		String s = "";
		String cueWord = word.content + "_" + word.parent.content;
		int index = sentence.indexOf(cueWord) - 1;
		while (index >= 0 && !sentence.get(index).split("_")[1].equals("CC")) index--;
		int i = index + 1;
		while (sentence.get(i).split("_")[1].startsWith("N") || sentence.get(i).split("_")[1].startsWith("D")) {
			s += sentence.get(i).split("_")[0];
			i++;
		}
		return s;
	}
	
	public static String findThird(Tree word) {
		return Print.print(word.parent.parent.parent.child.get(1));
	}
	
	public static String findLast(Tree word) {
		return word.content;
	}
	
	public static String print(ArrayList<String> sentence, Tree node) {
		return (findFirst(sentence, node) + "@" + findSecond(sentence, node) + "@" + findThird(node) + "@@" + findLast(node));
	}
}
