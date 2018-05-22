package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;
import java.util.List;


import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author wang
 * 如果...，将会...
 */
public class HouGuoSolution4 {
	static int split;
	public static String findSecond(Tree word, ArrayList<String> sentence, List<Integer> senIndex) {
		String s = "";
//		int k = word.no;
//		while (k < sentence.size() && !sentence.get(k).split("_")[0].equals("，"))
//			k++;
//		k++;
		int k=split+1;
		if (k>=0 && k < sentence.size() && sentence.get(k).split("_")[0].equals("将")) k++;
		if (k>=0 && k < sentence.size() && sentence.get(k).split("_")[0].equals("会")) k++;
		if (k>=0 && k < sentence.size() && sentence.get(k).split("_")[0].equals("是")) k++;
		for (int i = k; i < sentence.size(); i++){
			s += sentence.get(i).split("_")[0];
			senIndex.add(i);
		}
		return s;
	}
	
	public static String findFirst(Tree word, ArrayList<String> sentence, int bound){
		String s = "";
		//如果含有“则”、“那么”,就从此分开，否则在题干中找最后一个有效的逗号。
		if(sentence.contains("则_AD")||sentence.contains("那么_AD")){
			split=sentence.lastIndexOf("则_AD")==-1?sentence.lastIndexOf("那么_AD"):sentence.lastIndexOf("则_AD");
		}
		else{
			for (int i = bound-1; i > word.no-1 ; i--)
				if(sentence.get(i).split("_")[0].equals("，") && MyUtil.isValidPU(sentence, i)){
					split=i;
					break;
				}
		}
		//按照常理来说，是找题干中最后一个逗号，但出现了这道题“图中@如果某地区雾霾天气越严重，④越少”如果在bound之后
		if(split==-1 && word.no>bound){
			for (int i = sentence.size()-1 ; i > word.no; i--)
				if(sentence.get(i).split("_")[0].equals("，") && MyUtil.isValidPU(sentence, i)){
					split=i;
					break;
				}
		}
		if(split==-1) split=bound-1;
		for(int i=0;i<word.no-1;i++)
			s += sentence.get(i).split("_")[0];
		for (int i = word.no; i < split; i++)
			s += sentence.get(i).split("_")[0];
		if(s.endsWith("，")) s=s.substring(0, s.length()-1);
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence, List<Integer> senIndex, int bound) {
		split=-1;
		return findFirst(node, sentence, bound) + "@" + findSecond(node, sentence, senIndex);
	}
}

