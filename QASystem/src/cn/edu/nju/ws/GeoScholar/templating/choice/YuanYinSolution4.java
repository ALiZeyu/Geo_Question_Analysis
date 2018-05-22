package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
/*
 * 
 * @author Lizy
 * 【导致|引起|造成】。。。的是。。。
 */
public class YuanYinSolution4 {
	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no;
		while (k < sentence.size() && !(sentence.get(k).split("_")[0].equals("的"))) k++;
		if (k == sentence.size()) {
			k = word.no;
			Pattern pattern = Pattern.compile("是|为|有");
			while (k < sentence.size() && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k++;
			if (k == sentence.size()) 
				return sentence.get(word.no).split("_")[0];
		}
		for (int i = word.no; i < k; i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence, List<Integer> senIndex) {
		String s = "";
		int k = word.no;
		Pattern pattern = Pattern.compile("是|为|有");
		while (k < sentence.size() && !pattern.matcher(sentence.get(k).split("_")[0]).matches())
			k++;
		for (int i = k+1; i < sentence.size(); i++){
			s += sentence.get(i).split("_")[0];
			senIndex.add(i);
		}
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence, List<Integer> senIndex) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence, senIndex);
	}
}
