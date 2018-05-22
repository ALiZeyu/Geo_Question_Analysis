package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author wang
 * 为(为了)...，...
 */
public class CuoShiSolution1 {
	
	//为了。。。。。，
	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no;
		Pattern pattern = Pattern.compile("，|应|应该");
		//向后找【，|应|应该】
		while (k < sentence.size() && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k++;
		//记录第一个词的位置，根据其是不是动词，填第二个槽
		int index=-1;
		if (word.no == 1) {
			index = word.no;
			//为了怎么怎么样
			for (int i = word.no; i < k; i++)
				s += sentence.get(i).split("_")[0];
		} else {
			for (int i = 0; (i < word.no - 1 && !sentence.get(i).split("_")[0].equals("，")); i++)
				s += sentence.get(i).split("_")[0];
			index=s.equals("")?word.no:0;
			for (int i = word.no; i < k; i++)
				s += sentence.get(i).split("_")[0];
		}
		String str=sentence.get(index);
		return str.split("_")[1].startsWith("V")?s.substring(str.split("_")[0].length())+"@"+str.split("_")[0]:s+"@";
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence, List<Integer> senIndex) {
		String s = "";
		int k1=word.no;
		Pattern pattern1 = Pattern.compile("，|应|应该|可以");
		//先找【，|应|应该|可以】
		while (k1 < sentence.size() && !pattern1.matcher(sentence.get(k1).split("_")[0]).matches()) k1++;
		if(k1 == sentence.size())
			k1 = word.no;
		//在向后找【是|为|包括】
		int k = k1;
		Pattern pattern = Pattern.compile("是|为|包括");
		while (k < sentence.size() && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k++;
		if (k == sentence.size()) {
			k = k1;
			while (k < sentence.size() && !sentence.get(k).split("_")[0].equals("措施")) k++;
				if (k == sentence.size()) k = k1;
		}
		for (int i = k + 1; i < sentence.size(); i++){
			s += sentence.get(i).split("_")[0];
			senIndex.add(i);
		}
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence, List<Integer> senIndex) {
		return findFirst(node, sentence) + "@" + findSecond(node, sentence, senIndex);
	}
	
}
