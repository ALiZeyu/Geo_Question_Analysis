package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Segment;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
/*
 * A比B怎么怎么样
 * */
public class BijiaoPosPP {
	//看触发词的父父节点是不是NP，是的话返回NP最左孩子
	//否则向上找当前节点不是最左孩子的父节点，从触发词所在节点向左截取[NP|LCP]节点赋给First，如果First依然为空且触发词父父父父节点不为空，将最左孩子赋给First
	public static String findFirst(Tree word) {
		//找NP，先向上看，找不到了就找左兄弟
		Tree t = word.parent;
		if (t.parent.content.equals("NP")) {
			return Print.print(t.parent.child.get(0));
		}
		int index = t.child.indexOf(word);
		while (t.parent.child.indexOf(t) == 0) {
			Tree child = t;
			t = t.parent;
			index = t.child.indexOf(child);
		}
		Tree child = t;
		t = t.parent;
		index = t.child.indexOf(child);
		String s = "";
		//for (int i = index - 1; i >= 0 && t.child.get(i).content.equals("NP"); i--)
		//图中	甲丙间小路比乙丙间小路坡度更缓，添加了LCP
		for (int i = index - 1; i >= 0 && MyUtil.strEquals(t.child.get(i).content, "NP|LCP"); i--)
			s = Print.print(t.child.get(i)) + s;
		if (s.isEmpty() && word.parent.parent.parent.parent != null)
			s = Print.print(word.parent.parent.parent.parent.child.get(0));
		return s;
	}
	//取触发词的父父节点，如果是NP返回最右节点，否则返回第二个
	public static String findSecond(Tree word) {
		Tree t = word.parent.parent;
		if (t.content.equals("NP"))
			return Print.print(t.child.get(t.child.size() - 1));	
		return Print.print(t.child.get(1));
	}
	//从触发词向后找到second之后下一个词
	//如果这个词是VA且位于句子倒数第二个词之前(伦敦比北京	气温高，日较差大)VA压入last,前一个词压入third，i++。
	//然后从位置i向后找前后词是[NN|VV][VA|NN]的组合，找到的话分别压入third、Last。同时也找[AD][VV][N]的组合找到的话AD压入third，之后压入Last,直到句子末尾
	//如果Last为空，那么从触发词找第一个VA并赋给Last。如果连这个VA也没有，那么从second第一个词向后找[VV]，找到VV之前的赋给second，[VV]及之后赋给Last
	public static String findThird(Tree word, List<String> sentence) {
		//sectemp是为了处理句中不含VA，只能向后找VV，此时findsecond结果之后，动词之前的部分需要加入second结果之后，具体的切分在外边处理。
		String s = "",sectemp="";
		//if (t.child.size() > 1 && t.child.get(t.child.size() - 2).content.equals("ADVP"))
			//s = Print.print(t.child.get(t.child.size() - 1));
		int i = word.no + Segment.j.seg_postag(findSecond(word),0).size();
		List<String> third = new ArrayList<String>();
		List<String> last = new ArrayList<String>();
		if (i < sentence.size() - 2 && sentence.get(i).endsWith("VA")) {
			third.add(sentence.get(i - 1).split("_")[0]);
			last.add(sentence.get(i).split("_")[0]);
			i++;
		}
		while (i < sentence.size() - 1) {
			if ((sentence.get(i).endsWith("NN") || sentence.get(i).endsWith("VV")) && (sentence.get(i + 1).endsWith("VA") || sentence.get(i + 1).endsWith("NN"))) {
				third.add(sentence.get(i).split("_")[0]);
				last.add(sentence.get(i + 1).split("_")[0]);
				i++;
			}
			if (i < sentence.size() - 2 && sentence.get(i).endsWith("AD") && sentence.get(i + 1).endsWith("VV") && sentence.get(i + 2).split("_")[1].startsWith("N")) {
				third.add(sentence.get(i).split("_")[0]);
				String temp = sentence.get(i + 1).split("_")[0];
				for (int j = i + 2; j < sentence.size() && sentence.get(j).split("_")[1].startsWith("N"); j++) {
					temp += sentence.get(j).split("_")[0];
					i = j;
				}
				last.add(temp);
				
			}
			i++;
		}
		if (last.isEmpty()) {
			i = word.no + 1;
			while (i < sentence.size()) {
				if (sentence.get(i).endsWith("VA")) {
					last.add(sentence.get(i).split("_")[0]);
					break;
				}
				i++;
			}
			//最后一个槽不填的情况不允许存在，因此如果没有形容词，向后找动词、副词填进去
			if(i==sentence.size()){
				i = word.no + Segment.j.seg_postag(findSecond(word),0).size();
				while(i<sentence.size() && !sentence.get(i).endsWith("VV")){
					if(!sentence.get(i).endsWith("AD")) sectemp+=sentence.get(i).split("_")[0];
					i++;
				}
				if(i<sentence.size()){
					String tlast="";
					for(int temp=i;temp<sentence.size();temp++)
						tlast+=sentence.get(temp).split("_")[0];
					last.add(tlast);
				}else{
					//向后找不到动词，此行肯定是标错了，只能输出错误结果
					sectemp=sectemp.replace(sentence.get(sentence.size()-1).split("_")[0], "");
					last.add(sentence.get(sentence.size()-1).split("_")[0]);
				}
			}
		}
		s += findFirst(word) + "@";
		String temp = findSecond(word);
		for (String s1 : third) {
			if (temp.contains(s1) && !temp.equals(s1)) {
				temp = temp.substring(0, temp.indexOf(s1)) + temp.substring(temp.indexOf(s1) + s1.length(), temp.length()); 
				break;
			}
		}
		s += temp+ sectemp + "@";
		for (String s1 : third)
			if(!s1.equals(findSecond(word)))
				s += s1 + " ";
		if (s.endsWith(" ")) s = s.substring(0, s.length() - 1);
		s += "@@";
		//里约热内卢@区时比北京时间早13小时
		//由于这句话的出现，因此，每一个last的后面的如果是CD，那么就截取到第一个【，|句尾】，从句尾开始找last，如果找不到就算了(有可能是短语)
		for (String s1 : last){
			int index=getIndex(s1, sentence);
			if(index!=-1 && index+1<sentence.size() && sentence.get(index+1).endsWith("CD")){
				for(int ii=index+1;ii<sentence.size() && !sentence.get(ii).startsWith("，");ii++)
					s1+=sentence.get(ii).split("_")[0];
			}
			s += s1 + " ";
			
		}
		s = s.substring(0, s.length() - 1);
		return s;
	}
	
	public static int getIndex(String word, List<String> sentence){
		for(int i=sentence.size()-1;i>=0;i--){
			if(word.equals(sentence.get(i).split("_")[0]))
				return i;
		}
		return -1;
	}
	
	public static String print(Tree node, List<String> sentence) {
		return findThird(node, sentence);// + "@@" + findLast(node));
	}
}
