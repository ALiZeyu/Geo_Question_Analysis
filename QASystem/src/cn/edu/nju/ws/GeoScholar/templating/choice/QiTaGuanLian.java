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

public class QiTaGuanLian {
	//显示触发词【说明|表明|表现为】
	//由观察得出在地理试题中”说明“不太可能不是动词
	public static String getTemplate(TimuInfo topic, List<String> senList) throws IOException {
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.split(" ").length;
		String first="",second="",third="";
		String secStr="";
		if(sentence.contains("说明_VV")||sentence.contains("表明_VV")||sentence.contains("表现为_VV")){
			int index = sentence.contains("说明_VV")?sentence.indexOf("说明_VV"):sentence.indexOf("表明_VV");
			index = index==-1?sentence.indexOf("表现为_VV"):index;
			//这三个词如果位于句尾直接返回
			if(index==sentence.size()-1) return "";
			second=sentence.get(index).split("_")[0];
			int i=index+1;
			if(i<sentence.size() && sentence.get(i).equals("，_PU")) i++;
			String str="";
			for(int j=i;j<sentence.size();j++)
				str+=sentence.get(j)+" ";
			str=str.trim();
			if(MyUtil.isIP(str)){
				for(int j=i;j<sentence.size();j++){
					third+=sentence.get(j).split("_")[0];
					secStr+=sentence.get(j)+" ";
				}
				i=index-1;
				while(i>0 && (sentence.get(index).endsWith("PU")||sentence.get(index).endsWith("AD")))
					i--;
				for(int j=0 ; j<=i ;j++)
					first+=sentence.get(j).split("_")[0];
				//这后处理要尽量避免
				if(first.endsWith("，这"))
					first.substring(0, first.length()-"，这".length());
			}
		}
		else {
			if(topic.xxType.equals("IP")){
				if(bound-1>0&&sentence.get(bound-1).endsWith("_VV")){
					Set<String> set = Input.getCueword(5).keySet();
					int index = bound-1;
					while(index>0 && sentence.get(index).endsWith("VV")){
						//这个动词不能是指示的触发词
						if(set.contains(sentence.get(index).split("_")[0])){
							second="";
							break;
						}
						second=sentence.get(index).split("_")[0]+second;
						index--;
					}
					third=topic.xxText;
					for(int j=bound;j<sentence.size();j++)
						secStr+=sentence.get(j)+" ";
					while(index>0 && (sentence.get(index).endsWith("PU")||sentence.get(index).endsWith("AD")))
						index--;
					for(int j=0 ; j<=index ;j++)
						first+=sentence.get(j).split("_")[0];
				}
			}
		}
//		Set<String> words = new HashSet<String>();
//		for (String s : sentence)
//			words.add(s.split("_")[0]);
		String template = (first.isEmpty()||second.isEmpty()||third.isEmpty())?"":first+"@"+second+"@"+third;
		if (!template.isEmpty()) senList.add(secStr.trim());
		return template.isEmpty()?"":"-1+" + template;
	}
}
