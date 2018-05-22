package cn.edu.nju.ws.GeoScholar.templating.choice;

import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class BijiaoPosCC {
	
	private static String findNP(Tree t) {
		String s = "";
		for (int i = 0; i < t.child.size(); i++)
			if (t.child.get(i).content.equals("NP"))
				s += Print.print(t.child.get(i)) + " ";
			else if (t.child.get(i).content.equals("IP"))
				s += findNP(t.child.get(i));
			else if (t.child.get(i).content.equals("VP") 
					&& t.child.get(i).child.get(0).content.equals("ADVP") 
					&& t.child.get(i).child.size() == 2
					&& (i == 0 || t.child.get(i - 1).content.equals("PU")))
				s += Print.print(t.child.get(i).child.get(1)) + " ";
		return s;
	}
	
	private static String findVP(Tree t) {
		String s = "";
		for (int i = 0; i < t.child.size(); i++)
			if (t.child.get(i).content.equals("VP")) {
				if (t.child.get(i).child.get(0).content.equals("ADVP")
						&& (i == 0 || t.child.get(i - 1).content.equals("PU")))
					s += Print.print(t.child.get(i).child.get(0)) + " ";
				else
					s += Print.print(t.child.get(i)) + " ";
			} else if (t.child.get(i).content.equals("IP"))
				s += findVP(t.child.get(i));
		return s;
	}
	
	private static String findFirst(Tree node) {
		Tree t = node.parent.parent;
		int index = t.child.indexOf(node.parent);
		String s = "";
		for (int i = 0; i < index; i++)
			s += Print.print(t.child.get(i));
		return s;
	}
	
	private static String findSecond(Tree node) {
		Tree t = node.parent.parent;
		int index = t.child.indexOf(node.parent);
		String s = "";
		for (int i = index + 1; i < t.child.size(); i++)
			s += Print.print(t.child.get(i));
		return s;
	}
	
	private static String findThird(Tree node) {
		Tree t = node.parent.parent.parent;
		int index = t.child.indexOf(node.parent.parent);
		String s = "";
		do  {
			for (int i = index + 1; i < t.child.size(); i++)
				if (t.child.get(i).content.equals("NP"))
					s += Print.print(t.child.get(i)) + " ";
				else if (t.child.get(i).content.equals("IP"))
					s += findNP(t.child.get(i));
				else if (t.child.get(i).content.equals("VP") 
						&& t.child.get(i).child.get(0).content.equals("ADVP") 
						&& t.child.get(i).child.size() == 2
						&& (i == index + 1 || t.child.get(i - 1).content.equals("PU")))
					s += Print.print(t.child.get(i).child.get(1)) + " ";
			if (t.parent != null) index = t.parent.child.indexOf(t);
			t = t.parent;
		} while (t != null);
		if (!s.isEmpty()) s = s.substring(0, s.length() - 1);
		return s;
	}
	
	private static String findLast(Tree node) {
		Tree t = node.parent.parent.parent;
		int index = t.child.indexOf(node.parent.parent);
		String s = "";
		do  {
			for (int i = index + 1; i < t.child.size(); i++)
				if (t.child.get(i).content.equals("VP")) {
					if (t.child.get(i).child.get(0).content.equals("ADVP")
							&& (i == index + 1 || t.child.get(i - 1).content.equals("PU")))
						s += Print.print(t.child.get(i).child.get(0)) + " ";
					else
						s += Print.print(t.child.get(i)) + " ";
				} else if (t.child.get(i).content.equals("IP"))
					s += findVP(t.child.get(i));
			if (t.parent != null) index = t.parent.child.indexOf(t);
			t = t.parent;
		} while (t != null);
		if (!s.isEmpty()) s = s.substring(0, s.length() - 1);
		return s;
	}
	
	public static String print(Tree node) {
		return findFirst(node) + "@" + findSecond(node) + "@" + findThird(node) + "@@" + findLast(node);
	}
}
