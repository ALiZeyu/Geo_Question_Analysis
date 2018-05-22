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

public class CuoShiTemplate {

	public static String getTemplate(TimuInfo topic, List<String> senList) throws IOException {
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.length()==0?-1:topic.tgTag.split(" ").length;
		Map<String, String> cueword = Input.getCueword(4);
		List<Tree> word = findCueWord.getCueWords(cueword, t, 4, bound, sentence);
		String template = "";
		Set<String> words = new HashSet<String>();
		for (String s : sentence) words.add(s.split("_")[0]);
		
		for (Tree w : word) {
			List<Integer> senIndex = new ArrayList<>();
			if(w.content.startsWith("为")){
				if( w.no == 1 || (w.no - 1 > 0 && sentence.get(w.no - 2).split("_")[0].equals("，")) )
					template += w.no + "+" + CuoShiSolution1.print(w, sentence, senIndex) + "\t";
				else if(w.content.equals("为了")&&(w.no-1>0 && sentence.get(w.no - 2).split("_")[0].equals("是")))
					template += w.no + "+" + CuoShiSolution3.print(w, sentence, senIndex) + "\t";
			}
				
			else if (!words.contains("为") && !words.contains("为了")){
				//城市小区建设采取屋顶绿化、下凹式绿地、透水铺地等措施，主要作用是	增强雨水的滞留和下渗(措施后面不跟作用)
				int id=MyUtil.following(w.no-1, sentence, "，");
				if(id==-1||id>bound-1)
					template += w.no + "+" + CuoShiSolution2.print(w, sentence, senIndex) + "\t";
			}
			if(senIndex.size()>0)
				senList.add(MyUtil.senListToString(topic.allTagList, senIndex));
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		return template;
	}
}
