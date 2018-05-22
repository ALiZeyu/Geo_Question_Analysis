package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.List;


import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author lizy
 * [因为|由于]A,B
 */
public class HouGuoSolution2 {
	
	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no;
		while (k < sentence.size() && !sentence.get(k).startsWith("，"))
			k++;
		for (int i = word.no; i < k; i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence, List<Integer> senIndex) {
		String s = "";
		int k = word.no;
		while (k < sentence.size() && !sentence.get(k).startsWith("，")) k++;
		if (sentence.get(k).split("_")[0].equals("，")) k++;
		if (sentence.get(k).split("_")[0].equals("所以")) k++;
		for (int i = k; i < sentence.size(); i++){
			s += sentence.get(i).split("_")[0];
			senIndex.add(i);
		}
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence, List<Integer> senIndex) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence, senIndex);
	}
}
