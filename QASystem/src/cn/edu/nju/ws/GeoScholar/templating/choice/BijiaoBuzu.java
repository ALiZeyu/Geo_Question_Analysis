package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;

import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * @author Lizy
 * 这个类用于处理A接近|不足接一个数字。
 * 流程是触发词填最后一个槽，触发词之后的数字填第二个槽，向上找含有NP的IP，取触发词之前的部分填入第一个槽
 * */
public class BijiaoBuzu {
	public static String print(Tree node, ArrayList<String> sentence) {
		String first="",second=node.content,third="";
		for(int i=node.no;i<sentence.size()&&!sentence.get(i).endsWith("_PU");i++)
			third+=sentence.get(i).split("_")[0];
		Tree t = node.parent.parent;
		//向上找含有NP+VP的IP，触发词就在VP中
		while (!t.content.equals("ROOT") && !((t.content.equals("IP")||t.content.equals("FRAG")) && t.child.size()>1)) 
			t = t.parent;
		if(t.content.equals("ROOT")){
			//很有可能是句法树错到了不能忍
			int index=node.no-2;
			while(index>=0 && !sentence.get(index).equals("，_PU")){
				first=sentence.get(index).split("_")[0]+first;
				index--;
			}
			return first+"@###@"+third+"@###@"+second;
		}
			
		int left=MyUtil.getLeftNode(t).no-1;
		int right=node.no-2;
		while(right>0 && (sentence.get(right).endsWith("AD")||sentence.get(right).endsWith("VV")))
			right--;
		for(int j=left;j<=right;j++)
			first+=sentence.get(j).split("_")[0];
		return first+"@###@"+third+"@###@"+second;
	}
}
