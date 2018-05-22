package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author lizy
 * A导致，能够，造成B
 */
public class HouGuoSolution3 {

	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 2;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("会")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("是")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("将")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("可能")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("或许")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("应该")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("易")) k--;
		while(k > 0 && sentence.get(k).endsWith("AD")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("，")) k--;
		for (int i = k; i >= 0 && !sentence.get(i).split("_")[0].equals("如果"); i--)
			s = sentence.get(i).split("_")[0] + s;
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence, List<Integer> senIndex) {
		String s = "";
		for (int i = word.no; i < sentence.size(); i++){
			s += sentence.get(i).split("_")[0];
			senIndex.add(i);
		}
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence, List<Integer> senIndex) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence, senIndex);
	}
}

