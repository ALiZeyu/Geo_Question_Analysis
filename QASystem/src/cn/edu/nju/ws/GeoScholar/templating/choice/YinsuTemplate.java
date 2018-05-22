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

public class YinsuTemplate {
	public static String getTemplate(TimuInfo topic) throws IOException {
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.length()==0?-1:topic.tgTag.split(" ").length;
		Map<String, String> cueword = Input.getCueword(8);
		List<Tree> word = findCueWord.getCueWords(cueword, t, 8, bound, sentence);
		Set<String> words = new HashSet<String>();
		for (String s : sentence) words.add(s.split("_")[0]);
		String template = "";
		for (Tree w : word) {
			List<Integer> senIndex = new ArrayList<>();
			//原因是
			if (MyUtil.strEquals(w.content,"原因|影响因素|目的|成因")&&!MyUtil.setContains(words, "及|分别")&&w.no<sentence.size()&&w.no>bound){
				template += w.no + "+" + switchSlot(YuanYinSolution1.print(w, sentence, senIndex)) + "\t";
			}
			else if(MyUtil.strEquals(w.content,"结果")){
				if(w.no==sentence.size() && MyUtil.setContains(words, "是|为|有"))
					template += w.no + "+" + YinsuSolution1.print(w, sentence) + "\t";
			}
			else if(MyUtil.strEquals(w.content,"因") && w.no > 1 &&sentence.get(w.no - 2).endsWith("NN")&& !(sentence.get(w.no - 2).startsWith("是") || sentence.get(w.no - 2).startsWith("主要"))){
				//不能含有【变化|运动】的触发词
				Map<String, String> cueword_t = Input.getCueword(7);
				List<Tree> word_t = findCueWord.getCueWords(cueword_t, t, 7, bound, sentence);
				cueword_t = Input.getCueword(9);
				word_t.addAll(findCueWord.getCueWords(cueword_t, t, 9, bound, sentence));
				if(word_t.size()==0)
					template += w.no + "+" + YinsuSolution2.print(w, sentence) + "\t";
			}
			//因为...,...和....是因为...
			else if (MyUtil.strEquals(w.content,"因|由于|因为|得益于|取决于|源于|受控于|受制于")){
				//触发词是[因]的话前面不是[是]
				if(w.no > bound && !(w.content.equals("因")&&!(w.no - 2>=0 && sentence.get(w.no - 2).startsWith("是"))))
					template += w.no + "+" + switchSlot(YuanYinSolution2.print(w, sentence, senIndex))+ "\t";
				//else if (MyUtil.strEquals(w.content,"因|由于|因为") && (w.no == 1||!(sentence.get(w.no - 2).startsWith("是") || sentence.get(w.no - 2).startsWith("主要"))))
				else if (MyUtil.strEquals(w.content,"因|由于|因为") && w.no == 1)	
					template += w.no + "+" + HouGuoSolution1.print(w, sentence, senIndex) + "\t";
			}
			//导致，能够，造成B的是A，要不这仨位于句首，要不前面是一个逗号，后面有(是|为|有)
			else if ((MyUtil.strEquals(w.content,"导致|造成|引起|引发")) && !(words.contains("原因")||words.contains("影响因素")||words.contains("目的"))){
				if((w.no==bound || w.no==1 || sentence.get(w.no - 2).startsWith("，")) && (words.contains("是")||words.contains("为")||words.contains("有")))
					template += w.no + "+" + switchSlot(YuanYinSolution4.print(w, sentence, senIndex)) + "\t";
				//A导致，能够，造成B!(w.content.equals("形成") || w.content.equals("成")) &&
				else if (w.no > bound && w.parent.content.startsWith("V") && (w.no < sentence.size() && !sentence.get(w.no).startsWith("的"))){
					template += w.no + "+" + HouGuoSolution3.print(w, sentence, senIndex) + "\t";
				} 
			}
			else if(MyUtil.strEquals(w.content,"随着") && (w.no==bound||w.no==1) ){
					template += w.no + "+" + HouGuoSolution2.print(w, sentence, senIndex) + "\t";
			}
			//如果....，将会。。。
			else if ((w.content.equals("如果") || w.content.equals("若")) && !words.contains("导致") && !words.contains("造成") && words.contains("，")){
				template += w.no + "+" + HouGuoSolution4.print(w, sentence, senIndex, bound) + "\t";
			}
		}
		if (yueYue(sentence)&&template.isEmpty()) {
			template +=YinsuSolution3.print(sentence) + "\t";
		}
		
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		return template;
	}
	//因素关联的槽是先原因后结果，是原因模板反过来的
	public static String switchSlot(String str){
		String[] array = str.split("@");
		if(array.length==2)
			return array[1]+"@"+array[0];
		return str;
	}
	
	public static boolean yueYue(List<String> sentence){
		if(sentence.indexOf("越_AD")!=-1){
			int b=sentence.indexOf("越_AD");
			int e=sentence.lastIndexOf("越_AD");
			if(b!=e){
				if(e+1<sentence.size()&&sentence.get(b+1).endsWith("VA")&&sentence.get(e+1).endsWith("VA"))
					return true;
			}
		}
		return false;
	}
}
