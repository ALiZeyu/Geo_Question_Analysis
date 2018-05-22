package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author wang
 * 的...影响因素是(有)
 * 现已经改成原因
 */
public class YingXiangSolution6 {

	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 2;
		while (k < sentence.size() && !(sentence.get(k).split("_")[0].equals("是") || sentence.get(k).split("_")[0].equals("有"))) k++;
		if (k == sentence.size()) k = word.no - 1;
		for (int i = k + 1; i < sentence.size(); i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 2;
		while (k >= 0 && !sentence.get(k).split("_")[0].equals("的")) k--;
		if (k == -1) k = word.no - 1;
		if (k > 0 && sentence.get(k - 1).split("_")[0].equals("主要")) k--;
		if (k > 0 && sentence.get(k - 1).split("_")[0].equals("其")) k--;
		if (k > 0 && sentence.get(k - 1).split("_")[0].equals("，")) k--;
		for (int i = k - 1; i >= 0 && !sentence.get(i).split("_")[0].equals("，"); i--)
			s = sentence.get(i).split("_")[0] + s;
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence) + "@";
	}
}
