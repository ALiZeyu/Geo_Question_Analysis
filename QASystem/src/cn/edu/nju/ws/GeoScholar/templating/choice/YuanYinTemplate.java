package cn.edu.nju.ws.GeoScholar.templating.choice;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.nju.ws.GeoScholar.templating.common.Input;
import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.TimuInfo;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
import cn.edu.nju.ws.GeoScholar.templating.common.findCueWord;

public class YuanYinTemplate {

	public static String getTemplate(TimuInfo topic, List<String> senList) throws IOException {
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.length()==0?-1:topic.tgTag.split(" ").length;
		Map<String, String> cueword = Input.getCueword(1);
		List<Tree> word = findCueWord.getCueWords(cueword, t, 1, bound, sentence);
		Set<String> words = new HashSet<String>();
		for (String s : sentence) words.add(s.split("_")[0]);
		String template = "";
		for (Tree w : word) {
			List<Integer> senIndex = new ArrayList<>();
			//原因是
			//if (w.content.equals("原因") || w.content.equals("优势")){
			if ((w.content.equals("原因") || w.content.equals("影响因素") || w.content.equals("目的"))&&!MyUtil.setContains(words, "分别|及")){
				template += w.no + "+" + YuanYinSolution1.print(w, sentence, senIndex) + "\t";
			}
			//因为...,...和....是因为...从一级原因模板看，触发词不可能位于句首
			else if ((w.content.equals("由于") || w.content.equals("因为") || w.content.equals("得益于")) && w.no>1){
				template += w.no + "+" + YuanYinSolution2.print(w, sentence, senIndex)+ "\t";
			}
			//导致，能够，造成B的是A，要不这仨位于句首，要不前面是一个逗号，后面有(是|为|有)
			else if ((w.content.equals("导致") || w.content.equals("造成") || w.content.equals("引起")) && !(words.contains("原因")||words.contains("影响因素")||words.contains("目的"))){
				if((w.no==1 || sentence.get(w.no - 2).startsWith("，")) && ((words.contains("是")||words.contains("为")||words.contains("有"))))
				template += w.no + "+" + YuanYinSolution4.print(w, sentence, senIndex) + "\t";
			}
			if(senIndex.size()>0)
				senList.add(MyUtil.senListToString(topic.allTagList, senIndex));
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		return template;
	}
}
