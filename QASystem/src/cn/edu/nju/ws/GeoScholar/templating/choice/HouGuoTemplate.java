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

public class HouGuoTemplate {
	public static String getTemplate(TimuInfo topic, List<String> senList) throws IOException {
		//if (sentence.get(0).startsWith("影响")) return "";
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.length()==0?-1:topic.tgTag.split(" ").length;
		Map<String, String> cueword = Input.getCueword(2);
		List<Tree> word = findCueWord.getCueWords(cueword, t, 2, bound, sentence);
		Set<String> words = new HashSet<String>();
		for (String s : sentence) words.add(s.split("_")[0]);
		String template = "";
		for (Tree w : word) {
			List<Integer> senIndex = new ArrayList<>();
			//因为...,...
			//if ((w.content.equals("因") || w.content.equals("因为")) && (w.no < 2 || !(sentence.get(w.no - 2).startsWith("是") || sentence.get(w.no - 2).startsWith("主要")))){
			if ((w.content.equals("因") || w.content.equals("因为")) && w.no < 2 ){
				template += w.no + "+" + HouGuoSolution1.print(w, sentence, senIndex) + "\t";
			}
			//
			else if (MyUtil.strEquals(w.content, "由于|因为|随着") && words.contains("，")){
				if(w.no<=1){
					template += w.no + "+" + HouGuoSolution2.print(w, sentence, senIndex) + "\t";
				}
			}
			//A导致，能够，造成B
			else if (w.no > 1 && w.parent.content.startsWith("V") && !(w.content.equals("形成") || w.content.equals("成")) && (w.no < sentence.size() && !sentence.get(w.no).startsWith("的"))){
				template += w.no + "+" + HouGuoSolution3.print(w, sentence, senIndex) + "\t";
			}
			//如果....，将会。。。
			//如果K企业将纺纱厂建在越南.巴基斯坦等国，利润比建在美国高，最主要的原因是越南、巴基斯坦等国	离原料产地较近所以不能含有原因
			else if (MyUtil.strEquals(w.content, "如果|若") && (!MyUtil.setContains(words, "导致|造成|原因")||MyUtil.getIndexFromList(sentence, "导致_VV|造成_VV|原因_NN")>=bound) && words.contains("，")){
				template += w.no + "+" + HouGuoSolution4.print(w, sentence, senIndex, bound) + "\t";
			}
			if(senIndex.size()>0)
				senList.add(MyUtil.senListToString(topic.allTagList, senIndex));
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		return template;
	}
}
