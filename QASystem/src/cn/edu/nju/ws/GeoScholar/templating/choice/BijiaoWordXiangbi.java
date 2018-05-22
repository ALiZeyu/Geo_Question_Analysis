package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/*
 * 与。。。相比
 * slot1：从“相比”向前截取，截到【，|与|和】*/
public class BijiaoWordXiangbi {
	
	//从触发词向前截取到[，|与|和]
	public static String findFirst(ArrayList<String> sentence) {
		int index = sentence.indexOf("相比_VV");
		String s = "";
		for (int i = index - 1; i >= 0 && !MyUtil.strEquals(sentence.get(i).split("_")[0], "，|与|和"); i--) {
			s = sentence.get(i).split("_")[0] + s;
		}
		return s;
	}
	//从相比之后的[，]开始，找和First词性，词的个数相同的序列，赋给second
	public static String findSecond(ArrayList<String> sentence) {
		int index = sentence.indexOf("相比_VV");
		String s = "";
		int in = index - 1;
		if (sentence.get(index + 1).startsWith("，")) {
			while (in >= 0 && !MyUtil.strEquals(sentence.get(in).split("_")[0], "，|与|和")) in--;
			in++;
			for (int i = index + 2; i < sentence.size() - index + in; i++) {
				boolean b = true;
				for (int j = 0; j < index - in; j++)
					if (!sentence.get(i + j).split("_")[1].equals(sentence.get(in + j).split("_")[1])) {
						b = false;
						break;
					}
				if (b) {
					for (int j = index + 2; j < i + index - in; j++)
						s += sentence.get(j).split("_")[0];
					return s;
				}
			}
		}
		else {
			for (int i = index + 1; i < sentence.size() && !sentence.get(i).split("_")[0].equals("，"); i++)
				s += sentence.get(i).split("_")[0];
		}
		return s;
	}
	//找子树多于一个的IP
	private static Tree findFirstIP(Tree t) {
		List<Tree> l = new ArrayList<Tree>();
		if (!t.child.isEmpty()) {
			if (t.content.equals("IP") && t.child.size() > 1)
				return t;
			if (t.child.size() != 0)
				for (int i = 0; i < t.child.size(); i++) {
					Tree w = findFirstIP(t.child.get(i));
					if (w != null) return w;
				}
		}
		return null;
	}
	//从触发词向上取父父父父父节点temp，然后找左兄弟是PU的temp子节点t，找t及t孩子的IP。
	//如果没找到IP，从当前节点开始向后找NP，连起来，删除second槽部分后赋给Third
	//如果找到IPt，如果t就在逗号之后且t的最左孩子只有一个子节点，t=t的第二个孩子，将t的最右孩子赋给Third
	//如果找到IPt，如果t只有一个孩子，取这个孩子的第二个孩子并赋给Third
	//如果找到IPt，且不满足上面两行，那么把t的所有但除了最右连起来，删除second槽部分后赋给Third
	public static String findThird(Tree node, ArrayList<String> sentence) {
		String s = "";
		Tree temp = node.parent.parent.parent.parent.parent;
		int k = 2;
		do {
			while (k < temp.child.size() && !temp.child.get(k - 1).content.equals("PU")) k++;
			if (k >= temp.child.size()) break;
			Tree t = temp.child.get(k);
			t = findFirstIP(t);
			/*while (!t.content.equals("IP") && t.child.size() > 0)
				t = t.child.get(0);*/
			if (t == null || t.child.size() == 0) {
				for (int i = k; i < temp.child.size() && temp.child.get(i).content.equals("NP"); i++)
					s += Print.print(temp.child.get(i));
				s += " ";
				if (k == 2 && s.length() > findSecond(sentence).length()) s = s.substring(findSecond(sentence).length());
			} else {
				if (k == 2 && t.child.get(0).child.size() == 1) {
					t = t.child.get(1);
					s += Print.print(t.child.get(t.child.size() - 1)) + " ";
				} else if (t.child.size() == 1) {
					s += Print.print(t.child.get(0).child.get(1)) + " ";
				} else {
					for (int i = 0; i < t.child.size() - 1; i++)
						s += Print.print(t.child.get(i));
					s += " ";
					if (k == 2 && s.contains(findSecond(sentence))) s = s.substring(findSecond(sentence).length());
				}
			}
			k++;
		} while (k < temp.child.size());
		if (!s.isEmpty()) s = s.substring(0, s.length() - 1);
		return s;
	}
	//从触发词向上取父父父父父节点temp，然后找左兄弟是PU的temp子节点t，找t及t孩子的IP。
	//如果没找到IP，把temp的最右孩子赋给Third
	//如果找到IPt，如果t就在逗号之后且t的最左孩子只有一个子节点，将t的第二个孩子最左孩子赋给Last（区别于Third的右）
	//如果找到IPt，如果t只有一个孩子，取这个孩子的第1个孩子并赋给Last（区别于Third的右）
	//如果找到IPt，且不满足上面两行，那么把t的最赋给Last（区别于Third的除了最右）
	public static String findLast(Tree node, ArrayList<String> sentence) {
		String s = "";
		Tree temp = node.parent.parent.parent.parent.parent;
		int k = 2;
		do {
			while (k < temp.child.size() && !temp.child.get(k - 1).content.equals("PU")) k++;
			if (k >= temp.child.size()) break;
			Tree t = temp.child.get(k);
			t = findFirstIP(t);
			/*while (!t.content.equals("IP") && t.child.size() > 0)
				t = t.child.get(0);*/
			if (t == null || t.child.size() == 0) {
				s += Print.print(temp.child.get(temp.child.size() - 1)) + " ";
			} else {
				if (k == 2 && t.child.get(0).child.size() == 1) {
					s += Print.print(t.child.get(1).child.get(0)) + " ";
				} else if (t.child.size() == 1) {
					s += Print.print(t.child.get(0).child.get(0)) + " ";
				} else {
					s += Print.print(t.child.get(t.child.size() - 1)) + " ";
				}
			}
			k++;
		} while (k < temp.child.size());
		if (!s.isEmpty()) s = s.substring(0, s.length() - 1);
		return s;
	}
	
	public static String print(ArrayList<String> sentence, Tree node) {
		return (findFirst(sentence) + "@" + findSecond(sentence) + "@" + findThird(node, sentence) + "@@" + findLast(node, sentence));
	}
}
