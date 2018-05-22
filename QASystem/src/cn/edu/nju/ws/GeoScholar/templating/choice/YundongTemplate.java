package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.edu.nju.ws.GeoScholar.templating.common.Input;
import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.TimuInfo;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
import cn.edu.nju.ws.GeoScholar.templating.common.findCueWord;

public class YundongTemplate {
	public static String getTemplate(TimuInfo topic) throws IOException {
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.split(" ").length;
		Map<String, String> cueword = Input.getCueword(9);
		List<Tree> word = findCueWord.getCueWords(cueword, t, 9, bound,sentence);
//		Set<String> words = new HashSet<String>();
//		for (String s : sentence)
//			words.add(s.split("_")[0]);
		String template = "";
		for (Tree w : word) {
			//经过不位于句首和句尾，后面不跟的，不跟CD，NT(从该日起大约经过4天)
			if(sentence.get(w.no-1).split("_")[1].startsWith("VV")){
				if(w.content.equals("经过")&&!(!MyUtil.isAtbegin(w.no-1, sentence) &&
						!MyUtil.isAtEnd(w.no, sentence)&&
						!sentence.get(w.no).split("_")[0].equals("的")&&
						!(sentence.get(w.no).split("_")[1].equals("CD")||sentence.get(w.no).split("_")[1].equals("NT"))))
					return "";
				//流动前面不是”的“
				if(w.content.equals("流动")&&(w.no-2>0 && sentence.get(w.no-2).split("_")[0].equals("的")))
					return "";
				String s = YundongSolution.print(w, sentence);
				if (s.length()!=0) template +=  w.no + "+" + s + "\t";
			}
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		return template;
	}
}
