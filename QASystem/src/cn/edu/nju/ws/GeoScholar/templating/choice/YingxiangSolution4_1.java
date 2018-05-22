package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
/*
 * 对该城市环境质量影响较大的是	钢铁厂
 * */
public class YingxiangSolution4_1 {
	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no;
		Pattern pattern = Pattern.compile("是|为");
		while (k <sentence.size() && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k++;
		if (k == sentence.size()) k = word.no-1;
		//对河床形态影响不大的是	岩石性质
		for (int i = k+1; i <sentence.size(); i++)
			s = sentence.get(i).split("_")[0] + s;
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no-1;
		Pattern pattern = Pattern.compile("无|没有");
		//对B没有影响的是：
		if(k-1>0 && pattern.matcher(sentence.get(k-1).split("_")[0]).matches()) k--;
		for (int i = 1; i < k; i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String findLast(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no;
		Pattern pattern = Pattern.compile("是|的");
		if (k < sentence.size() && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k++;
		for (int i = word.no; i < sentence.size() && i <= k; i++)
			s += sentence.get(i).split("_")[0];
		Pattern pattern1 = Pattern.compile("无|没有|不");
		if (s.isEmpty() && (pattern1.matcher(sentence.get(k - 2).split("_")[0]).matches() || pattern.matcher(sentence.get(k - 3).split("_")[0]).matches()))
			s = "没有";
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence) + "@" + findLast(node, sentence);
	}
}
