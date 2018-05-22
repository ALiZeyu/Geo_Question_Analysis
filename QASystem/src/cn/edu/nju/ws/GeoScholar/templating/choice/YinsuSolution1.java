package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
/*A是B的结果*/
public class YinsuSolution1 {

	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no-1;
		Pattern pattern = Pattern.compile("是|为|有");
		while (k >=0 && !pattern.matcher(sentence.get(k).split("_")[0]).matches())
			k--;
		if (k == -1) {
			return sentence.get(0).split("_")[0];
		}
		for (int i = 0; i < k; i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no-1;
		Pattern pattern = Pattern.compile("是|为|有");
		while (k > 0 && !pattern.matcher(sentence.get(k).split("_")[0]).matches())
			k--;
		for (int i = k+1; i < word.no-1; i++){
			s += sentence.get(i).split("_")[0];
		}
		if(s.endsWith("的"))
			s=s.substring(0, s.length()-1);
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence) {
		return findSecond(node, sentence) + "@" + findFirst(node, sentence);
	}

}
