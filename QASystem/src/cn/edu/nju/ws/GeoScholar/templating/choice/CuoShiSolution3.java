package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
/*
 * B是为了A*/
public class CuoShiSolution3 {
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 3;
		if(k>=0 && sentence.get(k).startsWith("主要")) k--;
		if(k>=0 && sentence.get(k).startsWith("，")) k--;
		for(int i=0;i<=k;i++) s += sentence.get(i).split("_")[0];
		return s;
	}
	
	//是为了之后的
	public static String findFirst(Tree word, ArrayList<String> sentence, List<Integer> senIndex) {
		String s = "";
		for (int i=word.no; i < sentence.size(); i++){
			s += sentence.get(i).split("_")[0];
			senIndex.add(i);
		}
		return s+"@";
	}
	
	public static String print(Tree node, ArrayList<String> sentence, List<Integer> senIndex) {
			return findFirst(node, sentence, senIndex) + "@" + findSecond(node, sentence);
	}
}
