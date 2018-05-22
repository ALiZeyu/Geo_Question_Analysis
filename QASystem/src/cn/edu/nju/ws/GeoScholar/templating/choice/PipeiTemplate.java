package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Input;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
import cn.edu.nju.ws.GeoScholar.templating.common.findCueWord;

public class PipeiTemplate {

	public static String getTemplate(Tree t, ArrayList<String> sentence) throws IOException {
//		Set<String> cueword = Input.getWord(6);
//		List<Tree> word = findCueWord.getCueWords(cueword, t);
		Set<String> cueword = new HashSet<>();
		List<Tree> word = new ArrayList<>();
		String template = "";
		Set<String> words = new HashSet<String>();
		for (String s : sentence)
			words.add(s.split("_")[0]);
		for (Tree w : word) {
			Pattern pattern = Pattern.compile("原因|因素|影响|所|不|措施");
			String s = "";
			if (w.no > 1 && w.no < sentence.size() && !(!w.parent.content.startsWith("V") && w.content.equals("为"))
					&& !sentence.get(w.no - 2).endsWith("PU") && !sentence.get(w.no - 2).startsWith("图") && !pattern.matcher(sentence.get(w.no - 2).split("_")[0]).matches() && !sentence.get(w.no).startsWith("的")  && !sentence.get(w.no).startsWith("计划") 
					&& !sentence.get(w.no - 2).endsWith("LC") && !sentence.get(w.no).split("_")[0].equals("主") && !((words.contains("排序") || words.contains("依次")) && (words.contains("由") || words.contains("从")) && words.contains("到"))) {
				s = PipeiSolution.print(w, sentence);
				if (!s.isEmpty() && s.contains("、")) {
					if (s.split("@")[1].contains("、")) {
						String[] temp = s.split("@")[1].split("、");
						String s1 = s.split("@")[0];
						if (s1.contains("和") || s1.contains("及") || s1.contains("、") || s1.contains("①") || s1.contains("②") || s1.contains("④") || s1.contains("③"))
							for (int i = 0; i < temp.length; i++) {
								template += w.no + "+";
								if (s1.contains("、")) {
									template += s1.split("、")[i];
									s1 = s1.substring(s1.indexOf("、") + 1);
								}
								else if (s1.contains("和")) {
									template += s1.split("和")[i];
									s1 = s1.substring(s1.indexOf("和") + 1);
								}
								else if (s1.contains("及")) {
									template += s1.split("及")[i];
									s1 = s1.substring(s1.indexOf("及") + 1);
									//该大陆及沿岸洋流性质为非洲大陆、暖流
								}
								else
									template += s1.charAt(i) + "";
								template += "@" + temp[i] + "\t";
							}
						else
							template += w.no + "+" + s + "\t";
					} else {
						String[] temp = s.split("@")[0].split("、");
						String s1 = s.split("@")[1];
						if (s1.contains("和") || s1.contains("及") || s1.contains("、") || s1.contains("①") || s1.contains("②") || s1.contains("④") || s1.contains("③"))
						for (int i = 0; i < temp.length; i++) {
							template += w.no + "+" + temp[i] + "@";
							if (s1.contains("、")) {
								template += s1.split("、")[i];
								s1 = s1.substring(s1.indexOf("、") + 1);
							}
							else if (s1.contains("和")) {
								template += s1.split("和")[i];
								s1 = s1.substring(s1.indexOf("和") + 1);
							}
							else if (s1.contains("及")) {
								template += s1.split("及")[i];
								s1 = s1.substring(s1.indexOf("及") + 1);
								//该大陆及沿岸洋流性质为非洲大陆、暖流
							}
							else
								template += s1.charAt(i) + "";
							template += "\t";
						}
					}
				} else
					template += w.no + "+" + s + "\t";
			}
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		return template;
	}
}
