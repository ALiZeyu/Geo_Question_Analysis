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

public class GouChengTemplate {
	
	public static String getTemplate(TimuInfo topic) throws IOException {
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.split(" ").length;
		Map<String, String> cueword = Input.getCueword(10);
		List<Tree> word = findCueWord.getCueWords(cueword, t, 10, bound,sentence);
		Set<String> words = new HashSet<String>();
		for (String s : sentence)
			words.add(s.split("_")[0]);
		String template = "";
		for (Tree w : word) {
			if(MyUtil.isAtEnd(w.no, sentence) && MyUtil.setContains(words, "由|由于|因|是|经过|经")){
				template += w.no + "+" + GouChengSolution1.print(w, sentence) + "\t";
			}
			else if(w.content.equals("形成") && MyUtil.isAtEnd(w.no+1, sentence) && MyUtil.setContains(words, "由|由于|是"))
				template += w.no + "+" + GouChengSolution1.print(w, sentence) + "\t";
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		return template;
	}
	
	//形成必须位于句尾，或者是“形成的”位于句尾或后面是PU
	public static boolean isLegal(Tree t, List<String> sentence){
		if(!t.content.equals("形成"))
			return true;
		if(t.no == sentence.size() 
			|| sentence.get(t.no).endsWith("PU")
			|| (sentence.get(t.no).startsWith("的") && (t.no==sentence.size()-1 || sentence.get(t.no+1).endsWith("PU"))))
			return true;
		return false;
	}
}
