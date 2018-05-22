package cn.edu.nju.ws.GeoScholar.templating.choice;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.nju.ws.GeoScholar.templating.common.Input;
import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.TimuInfo;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
import cn.edu.nju.ws.GeoScholar.templating.common.findCueWord;

public class YingXiangTemplate {

	public static String getTemplate(TimuInfo topic, List<String> senList) throws IOException {
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.length()==0?-1:topic.tgTag.split(" ").length;
		Map<String, String> cueword = Input.getCueword(3);
		List<Tree> word = findCueWord.getCueWords(cueword, t, 3, bound, sentence);
		String template = "";
		Set<String> words = new HashSet<String>();
		for (String s : sentence) words.add(s.split("_")[0]);
		if(topic.tgTag.contains("措施_NN") && topic.tgTag.contains("是_VC")) return "";
		for (Tree w : word) {
			List<Integer> senIndex = new ArrayList<>();
			if (w.content.equals("影响")) {
				if ((w.no == 1 || (w.no - 2>=0 && sentence.get(w.no - 2).startsWith("，"))) && (words.contains("是")||words.contains("有")) )
					template += w.no + "+" + modifySlot(YingXiangSolution8.print(w, sentence), words) + "\t";
				else if (words.contains("受") || words.contains("受到")){
					template += w.no + "+" + modifySlot(YingXiangSolution1.print(w, sentence,topic.sxwText), words) + "\t";
				}
				else if (words.contains("对") && sentence.indexOf("对_P")<w.no-1)
					template += w.no + "+" + modifySlot(YingXiangSolution4.print(w, sentence), words) + "\t";
				else if (words.contains("给") && words.contains("带来"))
					template += w.no + "+" + modifySlot(YingXiangSolution2.print(w, sentence), words) + "\t";
			} 
//			else if ((w.no == 1 || !sentence.get(w.no - 2).startsWith("应")) && w.content.equals("控制") || w.content.equals("侵扰")){
//				template += w.no + "+" + modifySlot(YingXiangSolution1.print(w, sentence, topic.sxwText), words) + "\t";
//			}
			else if (w.content.equals("带来") && !words.contains("影响") && words.contains("给"))
				template += w.no + "+" + modifySlot(YingXiangSolution2.print(w, sentence), words) + "\t";
			else if (w.no < sentence.size() 
					&& !sentence.get(w.no).startsWith("供") && !sentence.get(w.no).startsWith("至") && !sentence.get(w.no).startsWith("达") 
					&& (w.no == 1 || !sentence.get(w.no - 2).startsWith("图")) 
					&& (w.no > 1 && (w.content.equals("可") || w.content.equals("使") || w.content.contains("利于") || w.content.equals("有助于"))))
				template += w.no + "+" + modifySlot(YingXiangSolution3.print(w, sentence), words) + "\t";
			else if (words.contains("对") && w.content.equals("重要"))
				template += w.no + "+" + modifySlot(YingXiangSolution4.print(w, sentence), words) + "\t";
			if (template.endsWith("形成@\t"))
				template = template.substring(0, template.length() - 4) + "@形成\t";
			if(senIndex.size()>0)
				senList.add(MyUtil.senListToString(topic.allTagList, senIndex));
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		return template;
	}
	
	public static String get3thSlot(Set<String> words){
		if(MyUtil.setContains(words, "有利于|利于|促进|改善|有助于"))
			return "有利";
		if(MyUtil.setContains(words, "不利于"))
			return "不利";
		return "";	
	}
	
	public static String modifySlot(String s, Set<String> words){
		String[] temp=s.split("@");
		if(temp.length<2){
			System.out.println(s);
			return s;
		}
		return temp.length==2?temp[0]+"@"+temp[1]+"@"+get3thSlot(words)+"@":temp[0]+"@"+temp[1]+"@"+get3thSlot(words)+"@"+temp[2];
	}
}
