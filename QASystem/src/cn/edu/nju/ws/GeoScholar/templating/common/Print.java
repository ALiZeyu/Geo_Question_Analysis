package cn.edu.nju.ws.GeoScholar.templating.common;

public class Print {

	public static String print(Tree p) {
		String s = "";
		if (p == null)
			return "";
		if (p.child.isEmpty())
			return p.content;
		else
			for (Tree t : p.child)
				s += print(t);
		return s;
	}

}
