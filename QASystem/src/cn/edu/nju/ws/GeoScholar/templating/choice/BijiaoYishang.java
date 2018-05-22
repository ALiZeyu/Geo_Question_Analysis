package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;

import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class BijiaoYishang {
	public static String print(Tree node, ArrayList<String> sentence) {
		String first="",second="",third=node.content;
		int right=node.no-2;
		//这边之所以限制为三，是因为这句话前面没有介词或者动词
		//( (IP (IP (NP (LCP (NP (NN 资料)) (LC 中)) (NP (PU “) (NN 九九歌) (PU ”))) (VP (MSP 所) (VP (VV 描述)))) (IP (NP (NN 区域) (NN 河流) (NN 结冰期)) (VP (LCP (NP (QP (CD 六) (CLP (M 个))) (NP (NN 月))) (LC 以上))))) )
		for(int i=0;i<3;i++){
			if(right>=0&&MyUtil.strEquals(sentence.get(right).split("_")[1], "CD|M|NN")){
				second=sentence.get(right).split("_")[0]+second;
				right--;
			}
			else break;
		}
		Tree t = node.parent.parent;
		//向上找含有NP+VP的IP，触发词就在VP中
		while (!t.content.equals("ROOT") && !((t.content.equals("IP")||t.content.equals("FRAG")) && t.child.size()>1)) 
			t = t.parent;
		if(t.content.equals("ROOT"))
			return "";
		int left=MyUtil.getLeftNode(t).no-1;
		while(right>0 && MyUtil.strEquals(sentence.get(right).split("_")[1], "AD|P|VV"))
			right--;
		for(int j=left;j<=right;j++)
			first+=sentence.get(j).split("_")[0];
		if(Tree.getLeftBro(t).equals("IP")||Tree.getLeftBro(t).equals("NP")){
			int index=t.parent.child.indexOf(t);
			Tree bro=t.parent.child.get(index-1);
			first=Print.print(bro)+first;
		}
		return first+"@###@"+second+"@###@"+third;
	}
}
