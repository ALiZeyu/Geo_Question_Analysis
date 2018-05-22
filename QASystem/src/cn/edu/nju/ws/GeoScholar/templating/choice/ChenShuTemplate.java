package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.TimuInfo;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class ChenShuTemplate {

	//IP->[NP|IP]+[VP|IP]
	private static boolean checkIP(Tree t) {
		for (int i = 0; i < t.child.size() - 1; i++)
			if (!t.parent.content.equals("CP") && (t.child.get(i).content.equals("NP") || t.child.get(i).content.equals("IP")) && (t.child.get(i + 1).content.equals("VP") || t.child.get(i + 1).content.equals("IP")))
				return true;
		return false;
	}
	
	private static List<Tree> getIP(Tree t) {
		List<Tree> l = new ArrayList<Tree>();
		if (!t.child.isEmpty()) {
			if (t.content.equals("IP") && t.child.size() > 1 && checkIP(t) && (!Print.print(t).contains("比") || !Print.print(t).contains("比例")))
				l.add(t);
			else
				if (t.child.size() != 0)
					for (int i = 0; i < t.child.size(); i++)
						l.addAll(getIP(t.child.get(i)));
		}
		return l;
	}
	
	public static String getTemplate(TimuInfo topic) throws IOException {
		Tree w = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		String template = "";
		//先找到所有符合要求的IP
		List<Tree> l = getIP(w);
		Set<String> set = new HashSet<String>();
		set.add("推断");
		set.add("所示");
		set.add("@比");
		for (Tree t : l) {
			Tree tvp = null, tnp = null;
			for (int i = 0; i < t.child.size() - 1; i++)
				if ((t.child.get(i).content.equals("NP") || t.child.get(i).content.equals("IP")) && (t.child.get(i + 1).content.equals("VP") || t.child.get(i + 1).content.equals("IP"))) {
					tvp = t.child.get(i + 1);
					tnp = t.child.get(i);
				}
			String te = Print.print(tvp);
			List<Tree> tl = new ArrayList<Tree>();
			if (te.contains("，")) {
				boolean b = true;
				while (tvp.child.size() <= 2 && tvp.child.size()>0)
					tvp = tvp.child.get(tvp.child.size() - 1);
				for (Tree tt : tvp.child)
					if (!tt.content.equals("PU") && !tt.content.equals("ADVP"))
						tl.add(tt);
					else
						b = false;
				if (b) {
					tl = new ArrayList<Tree>();
					tl.add(tvp);
				}
			}
			else
				tl.add(tvp);
			for (int index = 0; index < tl.size(); index++) {
				String s = "";
				tvp = tl.get(index);
				String middle = "";
				if (tvp.child.size() >= 2) {
					if (tvp.child.get(tvp.child.size() - 2).content.equals("PP") || tvp.child.get(tvp.child.size() - 2).child.get(0).content.equals("PP")) {
						for (int i = tvp.child.size() - 2; i >= 0 && (tvp.child.get(i).content.startsWith("P") || tvp.child.get(i).content.startsWith("V")); i--)
							middle = Print.print(tvp.child.get(i)) + middle;
						tvp = tvp.child.get(tvp.child.size() - 1);
					} else if (tvp.child.get(0).content.equals("PP") || tvp.child.get(0).child.get(0).content.equals("PP")) {
						for (int i = 0; i < tvp.child.size() - 1; i++)
							middle += Print.print(tvp.child.get(i));
						tvp = tvp.child.get(tvp.child.size() - 1);
					}
				}
				if (middle.isEmpty())
					middle = "@";
				else
					middle = "@" + middle + "@";
				while (tvp.child.size() == 2 && tvp.child.get(0).content.startsWith("AD") && (tvp.child.get(1).child.get(0).content.startsWith("V") || tvp.child.get(1).child.get(0).content.startsWith("P"))) {
					if (middle.equals("@"))
						middle = "@" + Print.print(tvp.child.get(0)) +"@";
					tvp = tvp.child.get(1);
				} 
				if (tvp.child.size() >= 2 && (tvp.child.get(tvp.child.size() - 2).content.startsWith("V") || (tvp.child.get(tvp.child.size() - 2).content.startsWith("P")&&!tvp.child.get(tvp.child.size() - 2).content.equals("PU")))) {
					s = Print.print(tnp) + "@" + Print.print(tvp.child.get(tvp.child.size() - 2));
					if (tvp.child.get(tvp.child.size() - 1).content.equals("PP"))
						s += Print.print(tvp.child.get(tvp.child.size() - 1).child.get(0)) + middle + Print.print(tvp.child.get(tvp.child.size() - 1).child.get(1))+ "\t";
					else
						s += middle + Print.print(tvp.child.get(tvp.child.size() - 1))+ "\t";
				}
				else if (tvp.child.size() >= 2 && (tvp.child.get(0).content.startsWith("V") || tvp.child.get(0).content.startsWith("P"))) {
					s = Print.print(tnp) + "@" + Print.print(tvp.child.get(0));
					if (tvp.child.get(tvp.child.size() - 1).content.equals("PP"))
						s += Print.print(tvp.child.get(tvp.child.size() - 1).child.get(0)) + middle + Print.print(tvp.child.get(tvp.child.size() - 1).child.get(1))+ "\t";
					else {
						s += middle;
						for (int i = 1; i <= tvp.child.size() - 1; i++)
							s += Print.print(tvp.child.get(i));
						s += "\t";
					}
				}
				else {
					Tree temp = tnp;
					while (temp.child.size() == 1 || temp.child.get(0).content.equals("ADJP") || temp.child.get(0).content.equals("LCP")) {
						while (temp.child.size() == 1) temp = temp.child.get(0);
						if (temp.child.size() == 0) {
							break;
						}
						while (temp.child.get(0).content.equals("LCP")) temp = temp.child.get(temp.child.size() - 1);
						while (temp.child.get(0).content.equals("ADJP")) temp = temp.child.get(temp.child.size() - 1);
					}
					//2016-11-28这句话报错了少加了一个边界条件
					//if (temp.child.size() == 0 && (t.child.size() == 2 || !t.child.get(t.child.indexOf(tnp) - 1).content.startsWith("N"))) {
					  if (temp.child.size() == 0 && (t.child.size() == 2 || (t.child.indexOf(tnp) - 1>-1 && !t.child.get(t.child.indexOf(tnp) - 1).content.startsWith("N")))) {
						for (int i = temp.no - 2; i >= 0 && (sentence.get(i).split("_")[1].startsWith("N") ||sentence.get(i).split("_")[1].startsWith("PN") || sentence.get(i).split("_")[1].startsWith("CC") || sentence.get(i).split("_")[1].startsWith("DT")); i--)
							s = sentence.get(i).split("_")[0] + s;
						if (s.isEmpty())
							s += Print.print(temp) + "@" + middle + Print.print(tvp)+ "\t";
						else
							s += "@" + Print.print(temp) + middle + Print.print(tvp)+ "\t";
					}
					//2016-11-28这句话报错了少加了一个边界条件
					else if (temp.child.size() == 0 && t.child.size() > 2 && (t.child.indexOf(tnp) - 1>-1 && t.child.get(t.child.indexOf(tnp) - 1).content.startsWith("N")))
						s = Print.print(t.child.get(t.child.indexOf(tnp) - 1)) + "@" + Print.print(temp) + middle + Print.print(tvp)+ "\t";
					else {
						s = temp.child.size() == 0?Print.print(temp):Print.print(temp.child.get(0));
						if (s.endsWith("的"))
							s = s.substring(0, s.length() - 1);
						s += "@";
						for (int i = 1; i < temp.child.size(); i++)
							s += Print.print(temp.child.get(i));
						s += middle;
						if (tvp.child.size() > 1 && tvp.child.get(0).content.equals("MSP"))
							s += Print.print(tvp.child.get(1))+ "\t";
						else
							s += Print.print(tvp)+ "\t";
					}
				}
				for (String st : set)
					if (s.contains(st)) {
						s = "";
						break;
					}
				if (s.startsWith("该@")) {
					s = "该" + s.substring(2,s.length());
					s = s.substring(0,s.indexOf("@")) + "@" + s.substring(s.indexOf("@"), s.length()); 
				}
				while (s.contains("@了"))
					 s = s.substring(0, s.indexOf("@了")) + "了@" + s.substring(s.indexOf("@了") + 2, s.length());
				//Pattern py = Pattern.compile("@(①|②|③|④)@");
				/*if (s.contains("@(①|②|③|④)@")) {
					int i = s.indexOf("@(①|②|③|④)@");
					s = s.substring(0, i) + s.substring(i + 1, i + 3) + "@" + s.substring(i + 3);
				}*/
				if (!s.isEmpty()) template += "-1+" + s;
			}
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1); else {
			if (w.child.get(0).content.equals("NP")) {
				Tree t = w.child.get(0);
				for (int i = 0; i < t.child.size() - 1; i++)
					if (t.child.get(i).content.equals("NP") && (t.child.get(i + 1).content.equals("NP") || t.child.get(i + 1).content.equals("ADJP"))) {
						if (t.child.get(i + 1).content.equals("NP"))
							template = "-1+" + Print.print(t.child.get(i)) + "@@" + Print.print(t.child.get(i + 1));
						else
							template = "-1+" + Print.print(t.child.get(i)) + "@" + Print.print(t.child.get(i + 1)) +"@" + Print.print(t.child.get(i + 2));
						break;
					}
			}
		}
		return template;
	}
}
