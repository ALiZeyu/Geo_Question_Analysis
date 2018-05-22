package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author wang
 * ...与...相关（无关）
 */

public class YingXiangSolution5 {

	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 2;
		Pattern pattern = Pattern.compile("与");
		while (k >= 0 && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k--;
		if (k == -1) k = word.no - 1;
		for (int i = k - 1; i >= 0 && !sentence.get(i).split("_")[0].equals("，"); i--)
			s = sentence.get(i).split("_")[0] + s;
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 2;
		Pattern pattern = Pattern.compile("与");
		while (k >= 0 && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k--;
		if (k == -1) k = word.no - 3;
		int k1 = sentence.get(word.no - 2).split("_")[0].equals("密切") ? word.no - 2 : word.no - 1;
		for (int i = k + 1; i < k1; i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String findLast(Tree word, ArrayList<String> sentence) {
		String s = word.content;
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence) + "@" + findLast(node, sentence);
	}
}
