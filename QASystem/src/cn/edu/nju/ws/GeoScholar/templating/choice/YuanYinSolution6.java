package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author wang
 * 如果...，将会...
 * 转为后果，已经弃用
 */
public class YuanYinSolution6 {

	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no;
		while (k < sentence.size() && !sentence.get(k).split("_")[0].equals("，"))
			k++;
		k++;
		if (k < sentence.size() && sentence.get(k).split("_")[0].equals("将")) k++;
		if (k < sentence.size() && sentence.get(k).split("_")[0].equals("会")) k++;
		if (k < sentence.size() && sentence.get(k).split("_")[0].equals("是")) k++;
		for (int i = k; i < sentence.size(); i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		for (int i = word.no; i < sentence.size() && !sentence.get(i).split("_")[0].equals("，"); i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence);
	}
}

