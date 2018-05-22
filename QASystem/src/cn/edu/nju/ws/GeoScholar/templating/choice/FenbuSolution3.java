package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class FenbuSolution3 {
	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 2;
		Pattern pattern = Pattern.compile("呈|沿");
		while (k >= 0 && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k--;
		while (k-1 >= 0 && sentence.get(k-1).endsWith("VC")) k--;
		for(int i=k-1;i>=0 && !sentence.get(i).equals("，_PU");i--)
			s = sentence.get(i).split("_")[0] + s;
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		Pattern pattern = Pattern.compile("呈|沿");
//		int t = word.no - 2;
//		if (t > 0 && sentence.get(t).startsWith("而"))
//			t--;
		int k = word.no - 2;
		while (k >= 0 && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k--;
		if (k == -1) {
			return null;
		} else {
			for (int i = k + 1; i <= word.no - 2; i++)
				s += sentence.get(i).split("_")[0];
		}
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence);
	}
}
