package Label_tagger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class LexiconPos {
	String userdic;//用户词典的路径
	List<String> dic;
	Map<String, String> posdic;
	int max;//词典中最长词的长度
	private static LexiconPos lex=null;
	
	private LexiconPos() {
		dic=new ArrayList<>();
		max=0;
		Properties props = new Properties();
		try {
//			InputStream in=new BufferedInputStream(new FileInputStream("tag.properties"));
//			props.load(in);
//			userdic=props.getProperty("userdic");
//			if(userdic==null){
//				System.out.println("can't find user dictionary in tag.properties");
//				return;
//			}
			File user_dic=new File("dic/NNTerm.txt");
			if(!user_dic.exists()){
				System.out.println("can't find user dictionary");
				return;
			}
			FileInputStream fis = new FileInputStream(user_dic);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String word = br.readLine();
			if(word.startsWith("\uFEFF"))
				word=word.substring(1);
			while (word != null){
				word=word.trim();
				if(max<word.length())
					max=word.length();
				if(!word.equals(""))
					dic.add(word.trim());
				word=br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort(dic);
	}
	/**2016/5/16改为词加词性格式*/
	private LexiconPos(String path) {
		dic=new ArrayList<>();
		posdic=new HashMap<String, String>();
		max=0;
		try {
			File user_dic=new File(path+"dic/NNTerm.txt");
			if(!user_dic.exists()){
				System.out.println("can't find user dictionary");
				return;
			}
			FileInputStream fis = new FileInputStream(user_dic);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String word = br.readLine();
			if(word.startsWith("\uFEFF"))
				word=word.substring(1);
			while (word != null){
				if(word.length()>0){
					word=word.trim();
					String[] record=word.split("\t");
					if(max<record[0].length())
						max=record[0].length();
					if(!record[0].equals(""))
						dic.add(record[0]);
					posdic.put(record[0], record[1]);
				}
				word=br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort(dic);
	}
	
	public static LexiconPos getInstance(){
		if(lex==null){
			lex=new LexiconPos();
		}
		return lex;
	}
	
	public static LexiconPos getInstance(String path){
		if(lex==null){
			lex=new LexiconPos(path);
		}
		return lex;
	}
	
	int getMax(){
		return max;
	}
	
	List<String> getDic(){
		return dic;
	}
	
	Map<String, String> getPosdic(){return posdic;}
}
