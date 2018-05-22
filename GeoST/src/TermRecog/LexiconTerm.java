package TermRecog;

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
import java.util.List;
import java.util.Properties;

public class LexiconTerm {

	String userdic;//用户词典的路径
	List<String> dic;
	int max;//词典中最长词的长度
	private static LexiconTerm lex=null;
	
//	private LexiconTerm() {
//		dic=new ArrayList<>();
//		max=0;
//		Properties props = new Properties();
//		try {
//			InputStream in=new BufferedInputStream(new FileInputStream("seg.properties"));
//			props.load(in);
//			userdic=props.getProperty("userdic");
//			if(userdic==null){
//				System.out.println("can't find user dictionary in seg.properties");
//				return;
//			}
//			File user_dic=new File(userdic);
//			if(!user_dic.exists()){
//				System.out.println("can't find user dictionary");
//				return;
//			}
//			FileInputStream fis = new FileInputStream(user_dic);
//			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
//			BufferedReader br = new BufferedReader(isr);
//			String word = br.readLine();
//			if(word.startsWith("\uFEFF"))
//				word=word.substring(1);
//			while (word != null){
//				word=word.trim();
//				if(max<word.length())
//					max=word.length();
//				if(!word.equals(""))
//					dic.add(word.trim());
//				word=br.readLine();
//			}
//			br.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Collections.sort(dic);
//	}
//	
	private LexiconTerm(String path) {
		dic=new ArrayList<>();
		max=0;
		try {
			File user_dic=new File(path);
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
	
//	public static LexiconTerm getInstance(){
//		if(lex==null){
//			lex=new LexiconTerm();
//		}
//		return lex;
//	}
	
	public static LexiconTerm getInstance(String path){
		if(lex==null){
			lex=new LexiconTerm(path);
		}
		return lex;
	}
	
	int getMax(){
		return max;
	}
	
	List<String> getDic(){
		return dic;
	}


}
