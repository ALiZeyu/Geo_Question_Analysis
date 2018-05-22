package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author wang
 * 由...形成
 * 现在已改成构造类
 */
public class YuanYinSolution3 {

	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int t = sentence.get(word.no - 2).startsWith("而") ? word.no - 2: word.no - 1;
		int k = t - 1;
		while (k >= 0 && !sentence.get(k).split("_")[0].equals("由")) k--;
		if (k == -1) {
			k = t - 1;
			while (k >= 0 && !sentence.get(k).split("_")[0].equals("是")) k--;
			if (k == -1) {
				for (int i = t - 1; i >= 0 && (sentence.get(i).split("_")[1].startsWith("N") || sentence.get(i).split("_")[1].startsWith("D") || sentence.get(i).split("_")[1].startsWith("JJ")); i--)
					s = sentence.get(i).split("_")[0] + s;
				return s;
			}
		}
		for (int i = k + 1; i < t; i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		int t = sentence.get(word.no - 2).startsWith("而") ? word.no - 2: word.no - 1;
		int k = t - 1;
		while (k >= 0 && !sentence.get(k).split("_")[0].equals("由")) k--;
		if (k == -1) {
			k = t - 1;
			while (k >= 0 && !sentence.get(k).split("_")[0].equals("是")) k--;
			if (k == -1) {
				for (int i = word.no; i < sentence.size() && !sentence.get(i).startsWith("，"); i++)
					s += sentence.get(i).split("_")[0];
				return s;
			}
		}
		for (int i = k - 1; i >= 0 && (sentence.get(i).split("_")[1].startsWith("N") || sentence.get(i).split("_")[1].startsWith("D") || sentence.get(i).split("_")[1].startsWith("JJ")); i--)
			s = sentence.get(i).split("_")[0] + s;
		if (s.isEmpty()) s = sentence.get(k - 1).split("_")[0];
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence) + "@" + node.content;
	}
}

