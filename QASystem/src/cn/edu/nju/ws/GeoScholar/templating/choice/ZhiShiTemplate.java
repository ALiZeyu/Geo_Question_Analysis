package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Input;
import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.TimuInfo;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
import cn.edu.nju.ws.GeoScholar.templating.common.findCueWord;

public class ZhiShiTemplate {
	/*如果触发词是题干的最后一个，根据观察，直接取之前和之后的部分
	 * 如果触发词在其他位置，那么就需要借助于句法树
	 * 以上两步完成后有一个后处理，即把含有顿号、分别这些能分的句子给分开*/

	public static String getTemplate(TimuInfo topic) throws IOException {
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.split(" ").length;
		Map<String, String> cueword = Input.getCueword(5);
		List<Tree> word = findCueWord.getCueWords(cueword, t, 5, bound, sentence);
		String template = "";
		Set<String> words = new HashSet<String>();
		for (String s : sentence) words.add(s.split("_")[0]);
		//当有触发词位于题干末尾时，前面题干中的触发词就不要了
		if(word.size()>1 && isBound(word, bound)!=-1){
			int temp = isBound(word, bound);
			Iterator<Tree> it = word.iterator();
			while(it.hasNext()){
				Tree tnode=it.next();
				if(tnode.no<temp)
					it.remove();
			}
		}
		for (Tree w : word) {
			Pattern pattern = Pattern.compile("原因|影响因素|影响|所|不|措施|目的");
			String s = "";
			//&& !(!w.parent.content.startsWith("V") && w.content.equals("为"))&& !sentence.get(w.no - 2).endsWith("LC") 
			//如果同时出现“是指”，那么只留下指,出现“表示为”，只留下为
			if (w.no > 1 && w.no < sentence.size() 
					&& !sentence.get(w.no - 2).endsWith("PU") && !(sentence.get(w.no - 2).startsWith("图") && sentence.get(w.no - 1).startsWith("示"))
					&& !(pattern.matcher(sentence.get(w.no - 2).split("_")[0]).matches()&&!MyUtil.setContains(words, "分别|及|依次")) 
					&& !sentence.get(w.no).startsWith("的") && !sentence.get(w.no).startsWith("计划") 
					&& !sentence.get(w.no).split("_")[0].equals("主")
					&& !(w.content.equals("是") && sentence.get(w.no).split("_")[0].equals("指"))
					&& !(w.content.equals("表示") && sentence.get(w.no).split("_")[0].equals("为"))) {
				if(w.no==bound||w.no==bound+1)
					s = ZhiShiSolution1.print(topic.allTree, w, sentence);
				else{
//					System.out.println(topic.origQuestion);
					s = ZhiShiSolution.print(w, sentence);
				}
				//处理多个指示的拆分
				if(s.contains("&&&")){
					String[] sep=s.split("&&&");
					for(String sp:sep)
						template += w.no + "+" + sp + "\t";
				}
				else
					template += w.no + "+" + s + "\t";
			}
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		return template;
	}
	
	public static int isBound(List<Tree> word, int bound){
		for(Tree t : word){
			if(Math.abs(t.no-bound)<=1)
				return t.no;
		}
		return -1;
	}
	
//	public static void separate(String s, List<String> first, List<String> second){
//		if (!s.isEmpty() && s.contains("、")) {
//			if (s.split("@")[1].contains("、")) {
//				String[] temp = s.split("@")[1].split("、");
//				String s1 = s.split("@")[0];
//				if (s1.contains("和") || s1.contains("及") || s1.contains("、") || s1.contains("①") || s1.contains("②") || s1.contains("④") || s1.contains("③"))
//					for (int i = 0; i < temp.length; i++) {
//						template += w.no + "+";
//						if (s1.contains("、")) {
//							template += s1.split("、")[i];
//							s1 = s1.substring(s1.indexOf("、") + 1);
//						}
//						else if (s1.contains("和")) {
//							template += s1.split("和")[i];
//							s1 = s1.substring(s1.indexOf("和") + 1);
//						}
//						else if (s1.contains("及")) {
//							template += s1.split("及")[i];
//							s1 = s1.substring(s1.indexOf("及") + 1);
//							//该大陆及沿岸洋流性质为非洲大陆、暖流
//						}
//						else
//							template += s1.charAt(i) + "";
//						template += "@" + temp[i] + "\t";
//					}
//				else
//					template += w.no + "+" + s + "\t";
//			} else {
//				String[] temp = s.split("@")[0].split("、");
//				String s1 = s.split("@")[1];
//				if (s1.contains("和") || s1.contains("及") || s1.contains("、") || s1.contains("①") || s1.contains("②") || s1.contains("④") || s1.contains("③"))
//					for (int i = 0; i < temp.length; i++) {
//						template += w.no + "+" + temp[i] + "@";
//						if (s1.contains("、")) {
//							template += s1.split("、")[i];
//							s1 = s1.substring(s1.indexOf("、") + 1);
//						}
//						else if (s1.contains("和")) {
//							template += s1.split("和")[i];
//							s1 = s1.substring(s1.indexOf("和") + 1);
//						}
//						else if (s1.contains("及")) {
//							template += s1.split("及")[i];
//							s1 = s1.substring(s1.indexOf("及") + 1);
//							//该大陆及沿岸洋流性质为非洲大陆、暖流
//						}
//						else
//							template += s1.charAt(i) + "";
//						template += "\t";
//					}
//				else
//					template += w.no + "+" + s + "\t";
//			}
//		} else
//	}
}
