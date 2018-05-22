package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.List;


import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class YundongSolution {

	public static String print(Tree node, ArrayList<String> sentence) {
		String first="",second="",third="";
		//boolean flag=node.content.equals("越来越")?true:false;
		Tree t = node.parent.parent;
		//向上找含有NP+VP的IP，触发词就在VP中
		while (!t.content.equals("ROOT") && !((t.content.equals("IP")||t.content.equals("FRAG")) && t.child.size()>1 && hasNP(t))) 
			t = t.parent;
		if(t.content.equals("ROOT"))
			return "";
		first=getAll(t, "NP")+"@";
		Tree tree=t.child.get(t.child.size()-1);
		//第二三个槽需要从VP中切分，目前是最右VP填第三个，后期要修改
		if(tree.child.size()==1){
			second="@";
			third=getVP(tree);
		}else if(tree.child.size()>=2){
			//VP->PP+VP
			if(tree.child.get(tree.child.size()-1).content.equals("VP") && tree.child.get(tree.child.size()-2).content.equals("PP")){
				second=getAll(tree, "PP")+"@";
				third=getVP(tree.child.get(tree.child.size()-1));
			}else if(tree.child.get(tree.child.size()-1).content.equals("VP") && tree.child.get(tree.child.size()-2).content.equals("VP")){
				//VP->VP+VP
				second=getAll(tree, "VP")+"@";
				third=getVP(tree.child.get(tree.child.size()-1));
			}else if(tree.child.get(tree.child.size()-1).content.equals("VP") && tree.child.get(tree.child.size()-2).content.equals("VV")){
				//VP->VV+VP
				second="@";
				third=getVP(tree.child.get(tree.child.size()-1));
			}else{
				second=Print.print(tree);
				Tree temp = node;
				while(!temp.content.equals("VP") && temp!=tree){
					temp=temp.parent;
				}
				second=second.substring(0, second.length()-Print.print(temp).length())+"@";
				third=getVP(temp);
			}
			
//			for(int i=0;i<tree.child.size()-1;i++)
//				second+=Print.print(tree.child.get(i));
//			second+="@";
//			third = getVP(tree.child.get(tree.child.size()-1));
		}
		//删除第二个槽前面的
		String[] del={"将", "会","正在"};
		for(String str:del)
			if(second.startsWith(str))
				second=second.substring(str.length());
		if(second.endsWith("而")) second=second.substring(0, second.length()-1);
		return first+second+third;
	}
	
	//判断当前节点是不是含有
	public static boolean hasNP(Tree t){
		if(t.child.get(t.child.size()-2).content.equals("NP"))
			return true;
		return false;
	}
	//获取当前IP节点的所有type类型子节点。
	public static String getAll(Tree t, String type){
		List<Tree> list = new ArrayList<>();
		int i=t.child.size()-2;
		while(i>=0 && t.child.get(i).content.equals(type)){
			list.add(t.child.get(i));
			i--;
		}
		String s="";
		for(i=list.size()-1;i>=0;i--)
			s+=Print.print(list.get(i));
		return s;
	}
	//删除VP节点中的副词，“而”这些
	public static String getVP(Tree t){
		if(t.child.size()==1)
			return Print.print(t);
		List<Tree> list = new ArrayList<>();
		for(int i=0;i<t.child.size();i++){
			Tree temp=t.child.get(i);
			if(temp.content.equals("MSP")||((temp.content.equals("ADVP")||temp.content.equals("AD"))))
				continue;
			list.add(temp);
		}
		String s="";
		for(int i=0;i<list.size();i++)
			s+=Print.print(list.get(i));
		return s;
	}

}
