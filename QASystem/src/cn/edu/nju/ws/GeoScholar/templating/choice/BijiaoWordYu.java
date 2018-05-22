package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.HashSet;

import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
import cn.edu.nju.ws.GeoScholar.templating.common.findCueWord;

public class BijiaoWordYu {
	//如果触发词的父父父节点是VP，且VP的最左孩子是PP，那么就取这个PP的最左孩子
	//否则看触发词的前一个词，删除[远|略]，在向前找词性以[N|D]开头的词，如果是DT就取当前词和下一个词，如果是NN就取当前词，向左找到第一个
	public static String findFirst(ArrayList<String> sentence, Tree word) {
		String s = "";
		if (word.parent.parent.parent.content.equals("VP") && word.parent.parent.parent.child.get(0).content.equals("PP")) {
			Tree t = word.parent.parent.parent.parent.child.get(0);
			s = Print.print(findCueWord.findFirstNP(t, sentence));
		} else {
			int i = word.no - 2;
			if (i>=0&&sentence.get(i).split("_")[0].equals("远")) i--;
			if (i>=0&&sentence.get(i).split("_")[0].equals("略")) i--;
			while (i>=0&&(sentence.get(i).split("_")[1].startsWith("N") || sentence.get(i).split("_")[1].startsWith("D"))) {
				if (sentence.get(i).split("_")[1].equals("DT"))
					s = sentence.get(i).split("_")[0] + sentence.get(i + 1).split("_")[0];
				else if (!sentence.get(i).split("_")[1].startsWith("D"))
					s = sentence.get(i).split("_")[0];
				i--;
				if (i < 0) break;
			}
			i += 2;
			HashSet<String> set = new HashSet<String>();
			set.add("①");
			set.add("②");
			set.add("④");
			set.add("③");
			if (set.contains(sentence.get(i).split("_")[0])) s += sentence.get(i).split("_")[0];
			//该地区@①的岩石形成时间早于②
			if (i+1<sentence.size()&&set.contains(sentence.get(i+1).split("_")[0])) s += sentence.get(i+1).split("_")[0];
		}
		return s;
	}
	//如果触发词节点的父父节点第二个孩子是NP，那么返回这个NP
	//否则从触发词向后找词性以[N|D]开头的词直到句尾，连起来返回
	public static String findSecond(ArrayList<String> sentence, Tree word) {
		String s = "";
		if (word.parent.parent.child.get(1).content.equals("NP")) {
			s = Print.print(word.parent.parent.child.get(1));
		} else {
			String cueWord = word.content + "_" + word.parent.content;
			int index = sentence.indexOf(cueWord);
			int i = index + 1;
			while (sentence.get(i).split("_")[1].startsWith("N") || sentence.get(i).split("_")[1].startsWith("D")) {
				s += sentence.get(i).split("_")[0];
				i++;
				if (i >= sentence.size()) break;
			}
		}
		return s;
	}
	//差不多就是看触发词的前一个词，删除[远|略]，在取First到触发词之间的部分
	public static String findThird(ArrayList<String> sentence, Tree word) {
		String s = "";
		int i = word.no - 2;
		if (sentence.get(i).split("_")[0].equals("远")) i--;
		if (sentence.get(i).split("_")[0].equals("略")) i--;
		HashSet<String> set = new HashSet<String>();
		set.add("①");
		set.add("②");
		set.add("④");
		set.add("③");
		while ((i>=1 && sentence.get(i - 1).split("_")[1].startsWith("N")) || (i >= 2 && sentence.get(i - 1).split("_")[1].startsWith("D") && sentence.get(i - 2).split("_")[1].startsWith("N"))) {
			if (set.contains(sentence.get(i).split("_")[0])) break;
			s = sentence.get(i).split("_")[0] + s;
			i--;
			if (i <= 0) break;
		}
		if(s.startsWith("的")) s=s.substring(1);
		return s;
	}
	//删除触发词最后的于
	public static String findLast(Tree word) {
		return word.content.substring(0, word.content.length() - 1);
	}
	
	public static String print(ArrayList<String> sentence, Tree node) {
		//如果下一个词是数字，调用其他模板识别
		if(node.no<sentence.size()&&(sentence.get(node.no).endsWith("CD")||(sentence.get(node.no).endsWith("_NN")&&MyUtil.startWithNum(sentence.get(node.no)))))
			return BijiaoBuzu.print(node, sentence);
		return (findFirst(sentence, node) + "@" + findSecond(sentence, node) + "@" + findThird(sentence, node) + "@@" + findLast(node));
	}
}
