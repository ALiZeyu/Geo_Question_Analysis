package Segmentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Ner.TimeRecognition;
import common.MyUtil;

public class Rule {
	public static final int NOAction = 0;
	public static final int Seperate = 1;
	public static final int Append = 2;
	int[] Rules;//前处理后的结果
	List<String> timeList;
	List<String> locList;
	
	/**
	 * sentence 待分词的句子
	 * dicmode 这里面前处理后处理都会做所以去掉这个参数
	 * prefix 路径前缀
	 * segmode 0是正常分词，1需要前匹配时间地点
	 * */
	Rule(String sentence, String prefix, int segmode,int split,List<String> words) {
		int i, start, end;
		int[] Rules = new int[sentence.length()];
		timeList=new ArrayList<String>();
		locList=new ArrayList<String>();
		
		Rules[0]=Seperate;
		
		//处理特殊字符
		char[] sep_character={'[',']','【','】',';','；','@','#','—','-','①','②','③','④','⑤','⑥','⑦','⑧','⑨','⑩','甲','乙','丙','丁','→','。'};
		Set<Character> sep=new HashSet<>();
		for(Character c:sep_character)
			sep.add(c);
		for(int j=0;j<sentence.length();j++){
			if(sentence.charAt(j)==' ')
				Rules[j]=Seperate;
			else if(sep.contains(sentence.charAt(j))){
				Rules[j]=Seperate;
				if(j+1<sentence.length())
					Rules[j+1]=Seperate;
			}
		}
		//前处理字典
		String str = sentence, temp;
		DoubleArrayTriePre adt = DoubleArrayTriePre.getInstance(prefix+"dic/pre.txt");
		int maxSize = adt.getMax();
		int k = 0;
		while (str.length() > 0) {
			if (str.length() < maxSize)
				temp = str;
			else
				temp = str.substring(0, maxSize);
			while (temp.length() > 1)
				if (adt.exactMatchSearch(temp) >= 0) {
					if(k<split && k + temp.length()>split)
						temp = temp.substring(0, temp.length() - 1);
					else{
						Rules[k] = Seperate;
						for (i = k + 1; i < k + temp.length(); i++)
							Rules[i] = Append;
						if (i < sentence.length())
							Rules[i] = Seperate;
						break;
					}
					
				} else
					temp = temp.substring(0, temp.length() - 1);
			k += temp.length();
			str = str.substring(temp.length());
		}
		
		// "[A-Za-z0-9\\.\\+\\-]*[A-za-z0-9][年|月|日|时|万|亿|%]*","(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]*[-A-Za-z0-9+&@#/%=~_|]",
		//,"(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]*[-A-Za-z0-9+&@#/%=~_|]""[A-Za-z0-9\\.\\+\\-\\_]*[A-za-z0-9][年|月|日|时|万|亿|%|％]*",
		
		//处理正则表达式匹配
		String[] pattern_str = { 
//				"[A-Za-z0-9\\.\\+\\_\\/]*[A-Za-z0-9](分钟|分|秒|世纪|年代|年|月份|月|日|时刻|时|万|亿|%|％|℃){0,1}",
//				"([图|表])([\\d|甲|乙|丙|丁])+([\\（|\\(][a|b|c|d|A|B|C|D][\\）|\\)])*",
//				"(\\d|\\.)+°[ewsnEWSN]?((\\d|\\.)+[′|’][ewsnEWSN]?)?",
//				"（?(\\d|\\.)+°[ewsnEWSN]?((\\d|\\.)+[′|’][ewsnEWSN]?)?，(\\d|\\.)+°[ewsnEWSN]?((\\d|\\.)+[′|’][ewsnEWSN]?)?）?",
//				"[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]]{1,4}(类|图|国|河流|河|市|县|镇|村|点|线|地|区域|区|阶段|河段)",
//				"[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]][城省处]",
//				"(洋流|气流|环节)[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]]",
//				"1[:|︰|：]\\d{1,10}",
//				"([0|1|2|3|4|5|6|7|8|9|０|１|２|３|４|５|６|７|８|９|一|二|三|四|五|六|七|八|九|零|十|百|千|万|亿|〇|两]{0,3})[:|：]([0|1|2|3|4|5|6|7|8|9|０|１|２|３|４|５|６|７|８|９|一|二|三|四|五|六|七|八|九|零|十|百|千|万|亿|〇|两]{0,3})"
				};
		for (int j = 0; j < pattern_str.length; j++) {
			Pattern p = Pattern.compile(pattern_str[j]);
			Matcher m = p.matcher(sentence);
			while (m.find()) {
				start = m.start();
				end = m.end();
				if(start < split && end > split)
					continue;
				Rules[start] = Seperate;
				for (i = start + 1; i < end; i++)
					Rules[i] = Append;
				if(end<sentence.length())
					Rules[end]=Seperate;
			}
		}
		//处理昼长夜短、自东向西短语
		String[] pattern_4 = { 
				"([自|从|由])([长|短|高|低|东|西|南|北|中|上|下|左|右][部|东|西|南|北]*)([到|至|向])([长|短|高|低|东|西|南|北|中|上|下|左|右][部|东|西|南|北]*)"
				};
		for (int j = 0; j < pattern_4.length; j++) {
			Pattern p = Pattern.compile(pattern_4[j]);
			Matcher m = p.matcher(sentence);
			while (m.find()) {
				start = m.start();
				end = m.end();
				if(end-start==4){
					//由 高 到 低
					Rules[start] = Seperate;
					Rules[start+1] = Seperate;
					Rules[start+2] = Seperate;
					Rules[start+3] = Seperate;
					if(end<sentence.length())
						Rules[end]=Seperate;
				}else if (end-start==6) {
					//自 东南 到 西北
					Rules[start] = Seperate;
					Rules[start+1] = Seperate;
					Rules[start+2] = Append;
					Rules[start+3] = Seperate;
					Rules[start+4] = Seperate;
					Rules[start+5] = Append;
					if(end<sentence.length())
						Rules[end]=Seperate;
				}
				
			}
		}
		
		String[] pattern_5 = { 
				"([昼|夜|东|西|南|北|中|上|下|左|右|风|雨|雾|雪|人|浪|春|夏|秋|冬][间|部|东|西|南|北]*)([冷|热|长|短|高|低|多|少|大|小])([昼|夜|东|西|南|北|中|上|下|左|右|风|雨|雾|雪|浪|春|夏|秋|冬][部|东|西|南|北|间]*)([冷|热|长|短|高|低|多|少|大|小|急])"
				};
		for (int j = 0; j < pattern_5.length; j++) {
			Pattern p = Pattern.compile(pattern_5[j]);
			Matcher m = p.matcher(sentence);
			while (m.find()) {
				start = m.start();
				end = m.end();
				if(end-start==4){
					//风小雾大，合在一起
					Rules[start] = Seperate;
					Rules[start+1] = Append;
					Rules[start+2] = Append;
					Rules[start+3] = Append;
					if(end<sentence.length())
						Rules[end]=Seperate;
				}else if (end-start==6) {
					//东南 高 西北 低
					Rules[start] = Seperate;
					Rules[start+1] = Append;
					Rules[start+2] = Seperate;
					Rules[start+3] = Seperate;
					Rules[start+4] = Append;
					Rules[start+5] = Seperate;
					if(end<sentence.length())
						Rules[end]=Seperate;
				}
				
			}
		}
		//处理正*方向
		String[] pattern_6 = {"正[东|西|南|北]方[向|位]"};
		for (int j = 0; j < pattern_6.length; j++) {
			Pattern p = Pattern.compile(pattern_6[j]);
			Matcher m = p.matcher(sentence);
			while (m.find()) {
				start = m.start();
				end = m.end();
				Rules[start] = Seperate;
				Rules[start+1] = Append;
				Rules[start+2] = Seperate;
				Rules[start+3] = Append;
				if(end<sentence.length())
					Rules[end]=Seperate;
			}
		}
		//处理空格，由于会把句中空格过滤，这里不用
//		for(i=0;i<sentence.length();i++)
//			if(sentence.charAt(i)==' ')
//				Rules[i]=Seperate;
//		 处理用户词典
//		if (dicmode == 0) {
//		}
		
		//处理时间和地点
		if(segmode==1){
			//时间词典
			String tf=prefix+"dic/timeLib.dic";
			timeList=MyUtil.read_file(tf);
			TimeRecognition timeRecognition=new TimeRecognition();
			timeList.addAll(timeRecognition.findAllTime(sentence));
			//地名词典
			String lf=prefix+"dic/simple_place.txt";
			locList=MyUtil.read_file(lf);
			
			for(String s:timeList){
				int j=sentence.indexOf(s);
				if(j>=0){
					Rules[j]=Seperate;
					for(i=j+1;i<j+s.length();i++)
						Rules[i]=Append;
				}
			}
			for(String s:locList){
				int j=sentence.indexOf(s);
				if(j>=0){
					Rules[j]=Seperate;
					for(i=j+1;i<j+s.length();i++)
						Rules[i]=Append;
				}
			}
		}
		//传入的地点处理
		if(words!=null && words.size()>0){
			Collections.sort(words);
			for(String s:words){
				int j=sentence.indexOf(s);
				if(j>=0){
					Rules[j]=Seperate;
					for(i=j+1;i<j+s.length();i++)
						Rules[i]=Append;
					if (i < sentence.length())
						Rules[i] = Seperate;
				}
			}
		}
		//利用题干和选项分离信息
		if(split>0 && split<sentence.length())
			Rules[split]=Seperate;
		this.Rules = Rules;
	}

	int[] getRules() {
		return this.Rules;
	}

	boolean can_sepreate(int i) {
		if (this.Rules[i] == Append)
			return false;
		return true;
	}

	boolean can_append(int i) {
		if (this.Rules[i] == Seperate)
			return false;
		return true;
	}
}
