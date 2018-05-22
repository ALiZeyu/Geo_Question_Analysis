package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Input;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
import cn.edu.nju.ws.GeoScholar.templating.common.findCueWord;

public class TemplateClassify {
	//切记：句法树中叶子节点的no是分词list中index+1；
	public static Set<Integer> Secondclassify(Tree t, List<String> sentence, int bound) throws IOException {
		Set<Integer> set = new HashSet<Integer>();
		Map<String, String> s5 = Input.getCueword(5);
		Map<String, String> s6 = Input.getCueword(6);
		Map<String, String> s7 = Input.getCueword(7);
		Map<String, String> s8 = Input.getCueword(8);
		Map<String, String> s9 = Input.getCueword(9);
		Map<String, String> s10 = Input.getCueword(10);
		Map<String, String> s11 = Input.getCueword(11);
		Map<String, String> s12 = Input.getCueword(12);
		if (!findCueWord.getCueWords(s5, t, 5, bound, sentence).isEmpty())
			set.add(5);
		if (!findCueWord.getCueWords(s6, t, 6, bound, sentence).isEmpty())
			set.add(6);
		if (!findCueWord.getCueWords(s7, t, 7, bound, sentence).isEmpty())
			set.add(7);
		if (!findCueWord.getCueWords(s8, t, 8, bound, sentence).isEmpty() || YinsuTemplate.yueYue(sentence))
			set.add(8);
		if (!findCueWord.getCueWords(s9, t, 9, bound, sentence).isEmpty())
			set.add(9);
		if (!findCueWord.getCueWords(s10, t, 10, bound, sentence).isEmpty())
			set.add(10);
		if (!findCueWord.getCueWords(s11, t, 11, bound, sentence).isEmpty())
			set.add(11);
		if (!findCueWord.getCueWords(s12, t, 12, bound, sentence).isEmpty())
			set.add(12);
		if(set.isEmpty())
			set.add(-1);
		return set;
	}
	
	
	public static Set<Integer> Firstclassify(Tree t, List<String> sentence, int bound) throws IOException {
		Set<Integer> set = new HashSet<Integer>();
		Map<String, String> s1 = Input.getCueword(1);
		Map<String, String> s2 = Input.getCueword(2);
		Map<String, String> s3 = Input.getCueword(3);
		Map<String, String> s4 = Input.getCueword(4);
		if (!findCueWord.getCueWords(s1, t, 1, bound, sentence).isEmpty())
			set.add(1);
		if (!findCueWord.getCueWords(s2, t, 2, bound, sentence).isEmpty())
			set.add(2);
		if (!findCueWord.getCueWords(s3, t, 3, bound, sentence).isEmpty())
			set.add(3);
		if (!findCueWord.getCueWords(s4, t, 4, bound, sentence).isEmpty())
			set.add(4);
		if(set.isEmpty())
			set.add(-1);
		return set;
	}
	
}
