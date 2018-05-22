package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.nju.ws.GeoScholar.templating.common.Input;
import cn.edu.nju.ws.GeoScholar.templating.common.TimuInfo;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
import cn.edu.nju.ws.GeoScholar.templating.common.findCueWord;

public class BianhuaTemplate {
	public static String getTemplate(TimuInfo topic) throws IOException {
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.split(" ").length;
		Map<String, String> cueword = Input.getCueword(7);
		List<Tree> word = findCueWord.getCueWords(cueword, t, 7, bound, sentence);
//		Set<String> words = new HashSet<String>();
//		for (String s : sentence)
//			words.add(s.split("_")[0]);
		String template = "";
		//变化触发词除了”越来越“除外，只能位于句末或者逗号之前。
		for (Tree w : word) {
			if(w.no==sentence.size() || (w.no>0 && sentence.get(w.no).split("_")[1].equals("PU")) || w.content.equals("越来越")){
				String s = BianhuaSolution.print(w, sentence);
				if (s.length()!=0) template +=  w.no + "+" + s + "\t";
			}
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		return template;
	}
}
