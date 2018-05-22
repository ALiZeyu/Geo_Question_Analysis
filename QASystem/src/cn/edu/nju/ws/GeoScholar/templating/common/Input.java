 package cn.edu.nju.ws.GeoScholar.templating.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


public class Input {
	
	
	public Tree getRoots() {
		return roots;
	}

	public void setRoots(Tree roots) {
		this.roots = roots;
	}

	public ArrayList<String> getSentence() {
		return sentence;
	}

	public void setSentence(ArrayList<String> sentence) {
		this.sentence = sentence;
	}

	public Tree roots;
	public DepTree depRoots;
	public ArrayList<String> sentence;
	//老版抽取触发词set
//	public static Set<String> getWord(int type) throws IOException {
//		Set<String> cueword = new HashSet<String>();
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/templating/cuewords_new.txt")));
//		String s;
//	if(s.startsWith("\uFEFF"))
//		s=s.substring(1);
//		while ((s = br.readLine()) != null)	{
//			if (Integer.parseInt(s.split("\t")[1]) == type) cueword.add(s.split("\t")[0]);
//		}
//		br.close();
//		return cueword;
//	}
	
	public static Map<String, String> getCueword(int type) throws IOException {
		Map<String, String> cueword = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/templating/cuewords_new.txt"), "utf8"));
		String s = br.readLine();
		if(s.startsWith("\uFEFF"))
			s=s.substring(1);
		while ( s != null)	{
			if(s.length()==0){
				s = br.readLine();
				break;
			}
			if (Integer.parseInt(s.split("\t")[1]) == type){
				if(s.split("\t")[0].contains("_"))
					cueword.put(s.split("\t")[0].split("_")[0], s.split("\t")[0].split("_")[1]);
				else
					cueword.put(s.split("\t")[0], "");
			} 
			s = br.readLine();
		}
		br.close();
		return cueword;
	}
	//建树
	public static Tree senToNode(String s) {
		Tree node = new Tree();
		node.content = "ROOT";
		node.no = 0;
		String[] token = s.split(" ");
		Stack<Tree> stack = new Stack<Tree>();
		int j = 1;
		for (int i = 1; i < token.length - 1; i++) {
			node.no = -1;
			Tree t = new Tree();
			t.parent = node;
			node.child.add(t);
			stack.push(node);
			node = node.child.get(node.child.size() - 1); 
			if (token[i].startsWith("(") && !token[i].endsWith(")"))		
				node.content = token[i].split("\\(")[1];
			else {
				if (token[i].startsWith(")") && token[i].endsWith(")"))
					node.content = ")";
				else {
					node.content = token[i].split("\\)")[0];
					if (node.content.contains("(") && !node.content.equals("("))
						node.content += ")";
					node.no = j++;
				}
				node = stack.pop();
			}
			while (token[i].endsWith(")") && token[i].length() > 1) {
				token[i] = token[i].substring(0, token[i].length() - 1);
				if (!stack.isEmpty())node = stack.pop();
			}
		}
		return node;
	}

	public static ArrayList<String> treeToSen(String s) {
		String[] token = s.split(" ");
		ArrayList<String> sen = new ArrayList<String>();
		for (int i = 1; i < token.length - 1; i++) {
			if (token[i].startsWith("(") && !token[i].endsWith(")"));
			else {
				if (token[i].startsWith(")") && token[i].endsWith(")"))
					sen.add(")_" + token[i - 1].split("\\(")[1]);
				else 
					sen.add(token[i].split("\\)")[0] + "_" + token[i - 1].split("\\(")[1]);
			}
		}
		return sen;
	}
	
	//从文件中读取词法分析和句法分析的结果构造树和串
	public void input(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf8"));
		String s;
		roots = new Tree();
		depRoots = new DepTree();
		sentence = new ArrayList<String>();
		while ((s = br.readLine()) != null) {
			s = new String(s.getBytes(), "UTF-8");
			roots = senToNode(s);
			sentence = treeToSen(s);
			String text = "";
			for (String word : sentence)
				text += word.split("_")[0] + " ";
			text = text.substring(0, text.length() - 1);
			//depRoots = Depparser.depparse(text);
		}
		br.close();
		File f = new File("data/templating/seg.txt");
		if (f.exists()) 
			f.delete();
		f = new File(filename);
		if (f.exists()) 
			f.delete();
	}
	
	public static Tree parsing(String question){
		try {
			List<String> taglist = Segment.segmentQuestion(question);
			String tagStr="";
			for(String s : taglist) tagStr+=s+" ";
			List<String> list=Util.getTreeList(tagStr.trim());
			String treeStr=SyntaxParser.parsingByList(list);
			return Input.senToNode(treeStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	public void inputForChoice(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String s;
		roots = new Tree();
		depRoots = new DepTree();
		sentence = new ArrayList<String>();
		while ((s = br.readLine()) != null) {
			s = new String(s.getBytes(), "UTF-8");
			roots = senToNode(s);
			sentence = treeToSen(s);
			String text = "";
			for (String word : sentence)
				text += word.split("_")[0] + " ";
			text = text.substring(0, text.length() - 1);
			depRoots = Depparser.depparse(text);
		}
		br.close();
		File f = new File("data/templating/seg.txt");
		if (f.exists()) 
			f.delete();
		f = new File(filename);
		if (f.exists()) 
			f.delete();
	}*/
	
}
