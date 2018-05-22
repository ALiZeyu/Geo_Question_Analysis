package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author wang
 * 措施是
 */
public class CuoShiSolution2 {
	
	//措施之前的
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 2;
//		while (k >= 0 && !(sentence.get(k).split("_")[0].equals("是"))) k--;
//		if (k == -1) {
//			k = word.no - 2;
		while (k >= 0 && !(sentence.get(k).split("_")[0].equals("的")))
			k--;
		if (k == -1)
			k = word.no - 1;
		//}
		//找【，】之后的
		int t = 0;
		while (t < k && !sentence.get(t).split("_")[0].equals("，"))
			t++;
		if (t == k)
			t = -1;
		for (int i = t + 1; i < k; i++)
			s += sentence.get(i).split("_")[0];
		String str = sentence.get(t+1);
		return str.split("_")[1].startsWith("V")?s.substring(str.split("_")[0].length())+"@"+str.split("_")[0]:s+"@";
	}
	
	//措施之后的
	public static String findFirst(Tree word, ArrayList<String> sentence, List<Integer> senIndex) {
		String s = "";
		int k = word.no - 2;
		//貌似是考虑A是B的措施这种已经不用的句式
//		while (k >= 0 && !(sentence.get(k).split("_")[0].equals("的"))) k--;
//		if (k == -1) k = word.no - 1;
//		int k1 = word.no - 2;
//		while (k1 >= 0 && !(sentence.get(k1).split("_")[0].equals("是") || sentence.get(k1).split("_")[0].equals("有"))) k1--;
//		if (k1 == -1) k1 = word.no - 1;
//		for (int i = k1 + 1; i < k; i++){
//			s += sentence.get(i).split("_")[0];
//			senIndex.add(i);
//		}
		if (s.isEmpty()) {
			int i = word.no;
			if (i < sentence.size() && sentence.get(i).split("_")[0].equals("可行")) i++;
			if (i < sentence.size() && sentence.get(i).split("_")[0].equals("的")) i++;
			if (i < sentence.size() && sentence.get(i).split("_")[0].equals("是")) i++;
			if (i < sentence.size() && sentence.get(i).split("_")[0].equals("有")) i++;
			for (; i < sentence.size(); i++){
				s += sentence.get(i).split("_")[0];
				senIndex.add(i);
			}
		}
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence, List<Integer> senIndex) {
//		if (node.no >= sentence.size())
//			return findFirst(node, sentence) + "@" + findSecond(node, sentence);
//		else
			return findSecond(node, sentence) + "@" + findFirst(node, sentence, senIndex);
	}
	
}
