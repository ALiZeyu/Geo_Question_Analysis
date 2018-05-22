package DP;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DepTree {

	
	protected int no;
	protected String content = null;
	protected DepTree parent = null;
	protected List<DepTree> child = new ArrayList<DepTree>();
	protected List<String> rel = new ArrayList<String>();
	
	public DepTree (int no, String content) {
		this.no = no;
		this.content = content;
	}

	public DepTree() {
		
	}
	
	public static DepTree findNodeByNo(DepTree t, int no) {
		if (t.no == no) return t;
		else {
			for (int i = 0; i < t.child.size(); i++) {
				DepTree temp = findNodeByNo(t.child.get(i), no);
				if (temp != null)
					return temp;
			}
		}
		return null;
	}
	
	private String traversal(DepTree t) {
		String s = "";
		if (t.child.size() > 0) {
			for (int i = 0; i < t.child.size(); i++) {
				s += t.rel.get(i) + "(";
				s += t.content + "_" + t.no + ", ";
				s += t.child.get(i).content + "_" + t.child.get(i).no + ")\n";
			}
			for (DepTree dt : t.child) {
				s += traversal(dt);
			}
		}
		return s;
	}
	
	public void get_node_index(DepTree t, int index, List<String[]> result){
		if (t.child.size() > 0) {
			for (int i = 0; i < t.child.size(); i++) {
				if(t.no == index){
					String[] temp = new String[3];
					temp[0] = "head";
					temp[1] = t.rel.get(i);
					temp[2] = String.valueOf(t.child.get(i).no);
					result.add(temp);
				}
				else if (t.child.get(i).no == index) {
					String[] temp = new String[3];
					temp[0] = "tail";
					temp[1] = t.rel.get(i);
					temp[2] = String.valueOf(t.no);
					result.add(temp);
				}
			}
			for (DepTree dt : t.child) {
				get_node_index(dt, index, result);
			}
		}
	}
	
	public String toString() {
		String s1 = traversal(this);
		try {
			s1 = new String(s1.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s1;
	}

}
