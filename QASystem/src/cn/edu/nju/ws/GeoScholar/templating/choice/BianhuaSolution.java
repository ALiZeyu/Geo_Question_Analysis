package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.List;


import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class BianhuaSolution {
	public static String print(Tree node, ArrayList<String> sentence) {
		String first="",second="",third="";
		boolean flag=node.content.equals("越来越")?true:false;
		Tree t = node.parent.parent;
		//向上找含有NP+VP的IP，触发词就在VP中
		while (!t.content.equals("ROOT") && !((t.content.equals("IP")||t.content.equals("FRAG")) && t.child.size()>1 && hasNP(t))) 
			t = t.parent;
		if(t.content.equals("ROOT"))
			return "";
		first=getAllNP(t)+"@";
		Tree tree=t.child.get(t.child.size()-1);
		//第二三个槽需要从VP中切分，目前是最右VP填第三个，后期要修改
		if(tree.child.size()==1){
			second="@";
			third=Print.print(tree);
		}else{
			for(int i=0;i<tree.child.size()-2;i++)
				second+=Print.print(tree.child.get(i));
			if(tree.child.size()-2>=0 && tree.child.get(tree.child.size()-2).content.equals("ADVP"))
				third+=Print.print(tree.child.get(tree.child.size()-2));
			else
				second+=Print.print(tree.child.get(tree.child.size()-2));
			second+="@";
			third += getVP(tree.child.get(tree.child.size()-1));
		}
		//防止“越来越”被填入第二个槽
		if(flag==true && second.endsWith("越来越@")){
			second=second.substring(0, second.length()-4)+"@";
			third="越来越"+third;
		}
		//删除第二个槽前面的
		String[] del={"随着", "随","因"};
		for(String str:del)
			if(second.startsWith(str))
				second=second.substring(str.length());
		return first+second+third;
	}
	
	//判断当前节点是不是含有
	public static boolean hasNP(Tree t){
		if(t.child.size()-2>=0 && t.child.get(t.child.size()-2).content.equals("NP"))
			return true;
		return false;
	}
	//获取当前IP节点的所有NP子节点。
	public static String getAllNP(Tree t){
		List<Tree> list = new ArrayList<>();
		int i=t.child.size()-2;
		while(i>=0 && t.child.get(i).content.equals("NP")){
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
			if(temp.content.equals("MSP")||((temp.content.equals("ADVP")||temp.content.equals("AD")) && !isYue(temp)))
				continue;
			list.add(temp);
		}
		String s="";
		for(int i=0;i<list.size();i++)
			s+=Print.print(list.get(i));
		return s;
	}
	//判断是不是“越来越”
	public static boolean isYue(Tree t) {
		while(t.child.size()>0)
			t=t.child.get(0);
		return t.content.equals("越来越")?true:false;
	}
}
