package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author lizy
 * B是因为A
 */
public class YuanYinSolution2 {
	
	public static String findFirst(Tree word, ArrayList<String> sentence) {
		//(之前)是因为
		String s = "";
		int k = word.no - 2;
		if (sentence.get(k).split("_")[0].equals("是")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("主要")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("或许")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("应该")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("可能")) k--;
		if (k >= 0 && sentence.get(k).split("_")[0].equals("，")) k--;
		for (int i = 0; i <= k; i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String findSecond(Tree word, ArrayList<String> sentence, List<Integer> senIndex) {
		//是因为。。。（之后）
		String s = "";
		for (int i = word.no; i < sentence.size(); i++){
			s += sentence.get(i).split("_")[0];
			senIndex.add(i);
		}
		return s;
	}
	/**一般情况下都是A是因为B，但出现了“由于c处气压高于a处，所以气流由c处流向a处”这句话触发词位于句首，因此特殊处理一下
	 * 方案是找第一个逗号进行两段切分
	 * */
	public static String getSlot(Tree node, ArrayList<String> sentence, List<Integer> senIndex){
		String first="",second="";
		if(sentence.contains("，_PU")){
			int index=sentence.indexOf("，_PU");
			for(int i=1;i<index;i++)
				first += sentence.get(i).split("_")[0];
			if(index+1<sentence.size() && sentence.get(index+1).startsWith("所以")) index++;
			for(int i=index+1;i<sentence.size();i++)
				second += sentence.get(i).split("_")[0];
		}else {
			//一般不会出现这种情况，此处只是防止没出现结果
			for(int i=1;i<3;i++)
				first += sentence.get(i).split("_")[0];
			for(int i=3;i<sentence.size();i++)
				second += sentence.get(i).split("_")[0];
		}
		return first+"@"+second;
	}
	
	public static String print(Tree node, ArrayList<String> sentence, List<Integer> senIndex) {
		if(node.no==1)
			return getSlot(node, sentence, senIndex);
		return findFirst(node, sentence) + "@" + findSecond(node, sentence, senIndex);
	}
}
