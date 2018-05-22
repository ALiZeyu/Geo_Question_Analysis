package cn.edu.nju.ws.GeoScholar.templating.choice;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 
 * @author wang
 * 受（受到）...控制（影响，侵扰），...
 * 有四种情况
 * 1、受|受到不位于句首，那么正常运行
 * 2、受|受到位于句首，且题目有上下文，例如“关于阿拉伯联合酋长国的叙述，正确的是	受旅游客源市场影响，游客主要来自西亚和非洲国家”，如果阿联酋被识别为上下文，那么就补成主语
 * 3、受|受到位于句首，受。。影响的是。。。，这时就两个槽分别是影响体和被影响体
 * 4、受|受到位于句首，受A影响，B怎么怎么样此时就是另一种情况了
 */

public class YingXiangSolution1 {

	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 2;
		Pattern pattern = Pattern.compile("受到|受");
		while (k >= 0 && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k--;
		if (k == -1) k = word.no - 1;
		pattern = Pattern.compile("是|，");
		if (k > 0) {
			if (pattern.matcher(sentence.get(k - 1).split("_")[0]).matches()) {
				Tree t = word.parent;
				while (t.parent != null) t = t.parent;
				while (t.child.size() == 1) t = t.child.get(0);
				s = Print.print(t.child.get(0));
			} else {
				if (k > 0 && sentence.get(k - 1).split("_")[0].equals("易")) k--;
				if (k > 0 && sentence.get(k - 1).split("_")[0].equals("少")) k--;
				if (k > 0 && sentence.get(k - 1).split("_")[0].equals("较")) k--;
				if (k > 0 && sentence.get(k - 1).split("_")[0].equals("主要")) k--;
				for (int i = k - 1; i >= 0 && !sentence.get(i).split("_")[0].equals("，") && !sentence.get(i).split("_")[1].equals("LC"); i--)
					s = sentence.get(i).split("_")[0] + s;
			}
			s += "@" + findLast(word, sentence);
		} else {
			Tree t = word.parent;
			while (!t.parent.content.equals("ROOT")) t = t.parent;
			if (t.child.get(t.child.size() - 1).content.equals("IP")) {
				t = t.child.get(t.child.size() - 1);
				s = Print.print(t.child.get(0)) + "@" + Print.print(t.child.get(t.child.size() - 1)); 
			}
		}
		return s;
	}
	
	public static String findFirst(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 2;
		Pattern pattern = Pattern.compile("受到|受");
		while (k >= 0 && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k--;
		if (k == -1) k = Math.max(word.no - 3, 0);
		for (int i = k + 1; i < word.no - 1 && !(i > 0 && i == word.no - 2 && sentence.get(i).split("_")[0].equals("的")); i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String findLast(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no - 2;
		while (k < sentence.size() && !(sentence.get(k).split("_")[0].equals("，"))) k++;
		if (k == sentence.size()) k = word.no - 1;
		for (int i = k + 1; i < sentence.size(); i++)
			s += sentence.get(i).split("_")[0];
		Pattern pattern = Pattern.compile("受到|受");
		while (k >= 0 && !pattern.matcher(sentence.get(k).split("_")[0]).matches()) k--;
		pattern = Pattern.compile("无|没有|少");
		if (s.isEmpty() && k-1>=0 && pattern.matcher(sentence.get(k - 1).split("_")[0]).matches())
			s = sentence.get(k - 1).split("_")[0];
		return s;
	}
	
	public static String print(Tree node, ArrayList<String> sentence, List<String> sxw) {
		if(MyUtil.strEquals(sentence.get(0).split("_")[0], "受|受到")){
			//本意是利用上下文补全主语，但现在有多条，暂取第一条吧
			String first=sxw.size()==0?"":sxw.get(0);
			if(first.contains("与")) first="";
			if(!first.equals("")){
				return findFirst(node, sentence) + "@" + first + "@" +findLast(node, sentence);
			}else{
				//受图d中风影响的气候可能有	热带雨林气候、亚热带季风气候、热带草原气候
				if(MyUtil.orfollowing(node.no-1, sentence, "是|为|有")!=-1
					&&(!sentence.contains("，_PU")
							||(sentence.contains("，_PU")&&MyUtil.orfollowing(node.no-1, sentence, "是|为|有")<sentence.indexOf("，_PU"))))
					return getSlot(node, sentence);
				else
					return writeSlot(node, sentence);
			}
			
		}
		return findFirst(node, sentence) + "@" + findSecond(node, sentence);
	}
	//受first影响的是second
	public static String getSlot(Tree node, ArrayList<String> sentence){
		String first="",second="";
		for(int i=1;i<node.no-1;i++)
			first+=sentence.get(i).split("_")[0];
		int i=node.no;
		while(i<sentence.size() && !MyUtil.strEquals(sentence.get(i).split("_")[0], "是|为|有")) i++;
		for(int j=i+1;j<sentence.size();j++)
			second+=sentence.get(j).split("_")[0];
		return first+"@"+second+"@";
	}
	//受B的影响，A怎么怎么样
	public static String writeSlot(Tree node, ArrayList<String> sentence){
		String second="",last="";
		int position=node.no;
		Tree t=node;
		//找到整棵句法树
		while(!t.parent.content.equals("ROOT")) 
			t=t.parent;
		//从逗号向上找
		int index=MyUtil.following(position-1, sentence, "，_PU")>-1?sentence.indexOf("，_PU")+1:position+1;
		Tree tree=Tree.findNodeByNo(t, index);
		tree=tree.parent;
		if(tree.parent!=null){
			int id=tree.parent.child.indexOf(tree);
			int size=tree.parent.child.size();
			if(id<size-1){
				List<Tree> rBros = Tree.getRightBrosList(tree);
				if(rBros.get(0).content.equals("IP")){
					//如果是子句，第一个孩子和之后的孩子分开
					second=Print.print(rBros.get(0).child.get(0));
					String all=Print.print(rBros.get(0));
					last=all.replace(second, "");
				}else if(!rBros.get(0).content.equals("VP")){
					//如果不是子句，那么将VP之前和之后分开
					//下面这句话没有VP，那么就把最后一棵子树给last
					//( (IP (IP (VP (VV 受) (NP (NN 地转偏向力) (NN 影响)))) (PU ，) (NP (CP (IP (NP (NR 长江)) (VP (PP (PP (P 自) (NP (NN 西))) (PP (P 向) (NP (NN 东)))) (VP (VV 流)))) (DEC 的)) (NP (NN 河段))) (PU ，) (IP (NP (NN 南岸)) (VP (VP (VV 受) (NP (NN 河水) (NN 冲刷) (NN 作用))) (VP (ADVP (AD 较)) (VP (VA 强)))))) )]
					int i=0;
					while(!rBros.get(i).content.equals("VP") && i<rBros.size()-1){
						second+=Print.print(rBros.get(i));
						i++;
					}
					for(int j=i;j<rBros.size();j++)
						last+=Print.print(rBros.get(j));
				}
			}
		}
		return findFirst(node, sentence)+"@"+(second.endsWith("，")?second.substring(0, second.length()-1):second)+"@"+last;
	}
}
