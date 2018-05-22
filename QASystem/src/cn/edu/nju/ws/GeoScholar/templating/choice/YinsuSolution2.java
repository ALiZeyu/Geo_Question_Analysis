package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;

import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
/*
 * 因。。而。。*/
public class YinsuSolution2 {
	public static String print(Tree node, ArrayList<String> sentence) {
		String first="",second="";
		int k=node.no;
		while(k<sentence.size()&&!(MyUtil.strEquals(sentence.get(k).split("_")[0], "，|而")))
			k++;
		if(k==sentence.size()){
			//句中不含逗号和而，就看句法树
			String all="";
			for(int i=0;i<sentence.size();i++)
				all+=sentence.get(i).split("_")[0];
			Tree temp=node.parent;
			while(!temp.content.equals("ROOT") && !temp.content.equals("PP"))
				temp=temp.parent;
			String pp=Print.print(temp);
			for(int i=0;i<node.no-1;i++)
				first+=sentence.get(i).split("_")[0];
			first+=pp;
			second=all.substring(first.length(), all.length());
		}else {
			//句中含有逗号和而，就借此分开；
			for(int i=0;i<k;i++)
				first+=sentence.get(i).split("_")[0];
			for(int i=k+1;i<sentence.size();i++)
				second+=sentence.get(i).split("_")[0];
		}
		return first + "@" + second;
	}
}
