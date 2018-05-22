package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 与。。。相比句型，但与之前还有主语
 * 1月份，②地与③地相比	白昼时间长
 * 思路就是从“相比”向前找[与|和]，之前的是槽一，之间的是槽二。
 * 然后看相比，之后的部分，取名词作为公共部分(方面)，没有的话就直接填入最后一个槽，
 * */
public class BijiaoWordXiangbi2 {
	
	public static String findFirst(ArrayList<String> sentence) {
		int index = sentence.indexOf("相比_VV");
		String s = "";
		int id=index-1;
		while(id>=0 && !MyUtil.strEquals(sentence.get(id).split("_")[0], "和|与")) id--;
		if(id-1>0 && sentence.get(id-1).split("_")[0].equals("，")) id--;
		for (int i = id - 1; i >= 0 && !sentence.get(i).split("_")[0].equals("，"); i--) {
			s = sentence.get(i).split("_")[0] + s;
		}
		return s;
	}
	
	public static String findSecond(ArrayList<String> sentence) {
		int index = sentence.indexOf("相比_VV");
		String s = "";
		int id=index-1;
		while(id>=0 && !MyUtil.strEquals(sentence.get(id).split("_")[0], "和|与")) id--;
		for (int i = index-1; i > id; i--) {
			s = sentence.get(i).split("_")[0] + s;
		}
		return s;
	}
	
	
	public static String findLast(Tree node, ArrayList<String> sentence) {
		int index = sentence.indexOf("相比_VV")+1;
		int id=index+1;
		while(id<sentence.size() && !MyUtil.strEquals(sentence.get(id).split("_")[1], "VV|AD|VA")) id++;
		String common="",last="";
		for(int i=index+1;i<id;i++) common+=sentence.get(i).split("_")[0];
		for(int i=id;i<sentence.size();i++) last+=sentence.get(i).split("_")[0];
		return common+"\t"+last;
	}
	
	public static String print(ArrayList<String> sentence, Tree node) {
		String str=findLast(node, sentence);
		String[] te = str.split("\t");
		if(te.length!=2) return "";
		if(te[0].equals(""))
			return findFirst(sentence) + "@" + findSecond(sentence) + "@@@" + te[1];
		else
			return findFirst(sentence) + "@" + findSecond(sentence) + "@" + te[0] + "@"+ te[0] +"@" + te[1];
	}
}
