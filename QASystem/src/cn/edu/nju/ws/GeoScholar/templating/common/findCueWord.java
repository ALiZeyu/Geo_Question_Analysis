package cn.edu.nju.ws.GeoScholar.templating.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 本类用于获取句子中的触发词
 * @author 李泽宇
 * @author 王韶杰
 * */
public class findCueWord {
	
	static List<Tree> word = new ArrayList<Tree>();	
	
//	private static void find(Set<String> s, Tree p) {
//		if (!p.child.isEmpty())
//			for (Tree q : p.child)
//				find(s, q);	
//		else{
//		if (s.contains(p.content))
//			word.add(p);
//		}
//	}
//
//	
//	private static void findCueWords(Set<String> s, Tree p) {
//		word = new ArrayList<Tree>();
//		find(s, p);
//	}
//	
//	public static ArrayList<Tree> getCueWords(Set<String> s, Tree p) {
//		findCueWords(s, p);
//		return (ArrayList<Tree>) word;
//	}
	//TODO 传入整句话分词结果还是题干还是选项
	private static void find(Map<String, String> s, Tree p, List<String> tags) {
		if (!p.child.isEmpty())
			for (Tree q : p.child)
				find(s, q, tags);	
		else{
		if (s.containsKey(p.content))
			if(s.get(p.content).equals(""))
				word.add(p);
			else{
				switch (s.get(p.content)) {
				case "b":
					if(p.no==1 || (p.no>1 && tags.get(p.no-2).equals("，_PU")))
						word.add(p);
					break;
				case "e":
					if(p.no==tags.size() || (p.no<tags.size() && tags.get(p.no).equals("，_PU")))
						word.add(p);
					break;
				case "eVV":
					if(p.no==tags.size() || (p.no<tags.size() && tags.get(p.no).endsWith("VV")))
						word.add(p);

				default:
					break;
				}
			}
		}
	}
	
	private static void findCueWords(Map<String, String> s, Tree p, List<String> tags) {
		word = new ArrayList<Tree>();
		find(s, p, tags);
	}
	
	public static List<Tree> getCueWords(Map<String, String> s, Tree p, List<String> tags) {
		findCueWords(s, p, tags);
		return word;
	}
	/**获取句子中的触发词，返回触发词在句法树中的节点
	 * @param	s
	 * 			触发词列表
	 * @param	p
	 * 			句子的句法树
	 * @param	type
	 * 			模板的编号
	 * @param	index:
	 * 			题干部分tag结果的长度，保证触发词在相应的选项和题干中。
	 * @param	tags
	 * 			词法分析结果
	 * @return	触发词所在节点列表
	 * */
	public static List<Tree> getCueWords(Map<String, String> s, Tree p, int type, int index, List<String> tags) {
		List<Tree> list = getCueWords(s, p, tags);
		if(type<=4)
			return fliter_first(list, index);
		if(type==5)
			return fliter_five(list, index);
		else if(type==8 ||type==12)
			return fliter_sec(list, index);
		return list;
	}

	// 保证一级模板触发词在题干中
	public static List<Tree> fliter_first(List<Tree> list, int index) {
		if (list.size() == 0)
			return list;
		Iterator<Tree> it = list.iterator();
		while (it.hasNext()) {
			Tree node = it.next();
			if (node.no > index)
				it.remove();
		}
		return list;
	}
	//保证触发词”有“必须位于题干末尾
	public static List<Tree> fliter_five(List<Tree> list, int index) {
		if (list.size() == 0)
			return list;
		Iterator<Tree> it = list.iterator(); 
		while (it.hasNext()) {
			Tree node = it.next();
			if (node.content.equals("有") && node.no != index)
				it.remove();
		}
		return list;
	}

	// 保证二级影响、因素关联的触发词在选项中
	public static List<Tree> fliter_sec(List<Tree> list, int index) {
		if (list.size() == 0)
			return list;
		Iterator<Tree> it = list.iterator();
		while (it.hasNext()) {
			Tree node = it.next();
			if (node.no <= index)
				it.remove();
		}
		return list;
	}
	
	public static Tree findFirstNP(Tree p, List<String> sentence) {
		if (p.content.equals("NP") || p.content.equals("DP")) 
			return p;
		else {
			List<Tree> l = new ArrayList<Tree>();
			l.addAll(p.child);
			while (!l.isEmpty()) {
				List<Tree> list = new ArrayList<Tree>();
				for (Tree t : l)
					if (t.content.equals("NP") || t.content.equals("DP")) 
						return t;
					else
						list.addAll(t.child);
				l = list;
			}
		}
		for (int i = p.no - 2; i >= 0; i++)
			if (sentence.get(i).split("_")[1].startsWith("N"))
				return Tree.findNodeByNo(p, i + 1);
		return null;
	}
	
	public static Tree findFirstLCP(Tree p, List<String> sentence) {
		if (p.content.equals("LCP")) 
			return p;
		else {
			List<Tree> l = new ArrayList<Tree>();
			l.addAll(p.child);
			while (!l.isEmpty()) {
				List<Tree> list = new ArrayList<Tree>();
				for (Tree t : l)
					if (t.content.equals("LCP")) 
						return t;
					else
						list.addAll(t.child);
				l = list;
			}
		}
		return null;
	}
}
