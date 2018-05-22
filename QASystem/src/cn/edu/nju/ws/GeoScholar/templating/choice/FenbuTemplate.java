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

public class FenbuTemplate {

	public static String getTemplate(TimuInfo topic) throws IOException {
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.split(" ").length;
		Map<String, String> cueword = Input.getCueword(11);
		List<Tree> word = findCueWord.getCueWords(cueword, t, 11, bound,sentence);
		String template = "";
		Set<String> words=new HashSet<>();
		for (String s : sentence)
			words.add(s.split("_")[0]);
		for (Tree w : word) {
			if (w.parent.content.startsWith("V") && w.no < sentence.size() && !MyUtil.strEquals(sentence.get(w.no).split("_")[0], "，|上限|的")) {
				if (MyUtil.strEquals(w.content, "出现|分布")
						&& !MyUtil.strEquals(sentence.get(w.no).split("_")[0], "上限|范围|特点|特征")) {
					if ((w.content.equals("出现") && !sentence.get(w.no).startsWith("在"))||(w.content.equals("出现") && w.no-2>=0 && sentence.get(w.no-2).endsWith("天气_NN")))
						continue;
					String s = FenbuSolution1.print(w, sentence);
					if (!s.startsWith("@"))
						template += w.no + "+" + s + "\t";
				} else if (isLegal(w.content, sentence.get(w.no).split("_")[0])) {
					String s = FenbuSolution2.print(w, sentence);
					if(s.contains("&&&")){
						String[] sep=s.split("&&&");
						for(String sp:sep)
							template += w.no + "+" + sp + "\t";
					}
					else
						template += w.no + "+" + s + "\t";
				}
			}else if(MyUtil.strEquals(w.content, "延伸|分布|走向")&&MyUtil.isAtEnd(w.no, sentence)&&MyUtil.setContains(words, "呈|沿")){
				String s = FenbuSolution3.print(w, sentence);
				template += w.no + "+" + s + "\t";
			}
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		return template;
	}
	
	public static boolean isLegal(String str1, String str2){
		if(str1.equals("位置") && !str2.equals("是"))
			return false;
		else if (str1.equals("出现") && !str2.equals("在"))
			return false;
//		else if (str1.equals("形成") && !str2.equals("于"))
//			return false;
		return true;
	}
}
