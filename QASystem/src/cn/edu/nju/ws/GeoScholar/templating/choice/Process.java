package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.TimuInfo;
import cn.edu.nju.ws.GeoScholar.templating.common.Util;

public class Process {

	public static void main(String[] args) {
		//compare("F://地理相关/myAMR/1北京高考");
		splitSenExtraction("F://地理相关/AllQuestion");
	}
	
	public static void compare(String path){
		List<String> fileList=getFileList(path);
		List<String> result=new ArrayList<>();
		for(String name:fileList){
			List<String> sens=Util.read_file(name);
			sens.remove(0);
			for(String sen:sens){
				String txt=sen.split("\t")[0];
				txt=txt.replace("/", "@");
				String post=del(txt).replace("@@@", "@");
				if(!txt.equals(post)){
					result.add(txt);
					result.add(post);
				}
			}
		} 
		MyUtil.writeFile(result, "data/对比.txt");
	}
	
	public static void splitSenExtraction(String path){
		List<String> fileList=getFileList(path);
		List<String> sp_result=new ArrayList<>();
		List<String> result=new ArrayList<>();
		for(String name:fileList){
			List<String> sens=Util.read_file(name);
			sens.remove(0);
			for(int i=0;i<sens.size();i++){
				String sen=sens.get(i);
				String xx=sen.split("\t")[1];
				if(xx.contains("，")){
					if(i+1<sens.size()&&xx.contains(sens.get(i+1).split("\t")[1])){
						sp_result.add(sen);
//						i++;
//						while(i<sens.size()&&xx.contains(sens.get(i).split("\t")[1])){
//							sp_result.add(sens.get(i++));
//						}
					}else{
						result.add(sen);
					}
				}
			}
		}
		MyUtil.writeFile(sp_result, "F://地理相关/试题拆分/Qsplit.txt");
		MyUtil.writeFile(result, "F://地理相关/试题拆分/QNsplit.txt");
	}
	
	/**删除套话以及无用的词语，返回题干@@@选项*/
	public static String del(String sen){
		// 移除无用词
		String question=sen;
		String[] del = {"最有可能", "有可能", "最可能", "可能", "最主要", "主要","图中反映"};
		for (String s : del) {
			while (question.contains(s))
				question = question.substring(0, question.indexOf(s)) + question.substring(question.indexOf(s) + s.length(), question.length());
		}
		//据图	蓝水和绿水根本来源相同|	图示	N为低纬信风带
		if(question.startsWith("据图@")||question.startsWith("图示@")) question=question.substring(2);
		//某天文爱好者拍摄“超级月亮”的照片时（图1）	中国正值一年的立秋节气
		String specialRegex="（.*图.*）";
		Pattern p = Pattern.compile(specialRegex);
		Matcher m = p.matcher(question);
		int start=0,end=0;
		while (m.find()) {
			start = m.start();
			end = m.end();
			if(end-start<=5){
				String ss=question.substring(start, end);
				question=question.replace(ss, "");
			}
			
		}

		String[] te = question.split("@");
		//记录并删除题干中的背景部分，指根据句号
//		if(te[0].contains("。")){
//			int i1=te[0].lastIndexOf("。");
//			te[0]=te[0].substring(i1+1, te[0].length());
//		}
		//读图这一类套话，只存在于题干第一个逗号之前，是的话直接删掉。
		Pattern p1=Pattern.compile("读.*[图|表]([\\d|甲|乙|丙|丁])*([\\（|\\(][a|b|c|d|A|B|C|D][\\）|\\)])*.*");
		if(te[0].contains("，")){
			String st=te[0].substring(0, te[0].indexOf("，"));
			if(p1.matcher(st).matches())
				te[0]=te[0].replace(st+"，", "");
		}
		//"当日","时"
		String[] douHao={"相比"};
		for(String s:douHao)
			if (te[0].endsWith(s)) te[0] += "，";
		String[] tt = te[0].split("，");
		if(tt[tt.length-1].startsWith("随着")||tt[tt.length-1].startsWith("随")) te[0] += "，";
		delTaohua(te);
		String q=te.length==2?q=te[0]+"@@@"+te[1]:"@@@"+te[0];
		if(q.startsWith("推断")||q.startsWith("推测"))
			q=q.substring(2);
		
		while (q.startsWith("，"))
			q = q.substring(1);
		if (q.endsWith("。"))
			q = q.substring(0, q.length() - 1);
		return q;
	}
	
	public static List<String> getFileList(String path){
		File file = new File(path);
		File[] list = file.listFiles();
		List<String> result = new ArrayList<>();
		for(File f:list){
			if(f.isFile())
				result.add(f.getAbsolutePath());
		}
		return result;
	}
	
	/**提取套话 
	 * TODO 1、判断套话后面是不是一个句子，不是的话特殊处理。
	 * 2、没识别为套话但含有”正确的是“，如何处理*/
	public static void delTaohua(String[] te){
		//boundary用来判断套话是否出现在选项中
		int boundary=te.length>1?te[0].length():0;
		String sentence = te.length>1?te[0]+te[1]:te[0];
		String[] regex={"(根据|据|由|结合|从).*(分析|推测|推断|判断|可知)(，|出){0,1}(下列|关于|有关).*(正确|符合实际|可信).(是)",
				"(根据|据|由|结合|从).*(分析|推测|推断|判断|可知)(，|出|是){0,1}",
				"(下列|关于|有关).*(正确|符合实际|可信).{1,3}(是)"};
		//有一些不符合上述表达式的套话，特别处理一下
		String[] special={"一般情况下","一般情况下，","图中四地符合实际的是","依据图11与图12，","图中显示","按此理解，","根据图示信息，","图7中","图2中①②③三地自然条件相比较","图3中①、②、③、④四地自然条件相比","文中描述的现象可以解释为","根据所学知识，你认为","结合图4，"};
		Set<String> spSet=new HashSet<>();
		for(String str : special) spSet.add(str);
		List<String> match=TimuInfo.matchStr(regex, sentence);
		String lenstr="";
 		if(match.size()==0){
			for(String ss:spSet)
				if(sentence.contains(ss)){
					int ssindex=sentence.indexOf(ss);
					if(ssindex==0||sentence.charAt(ssindex-1)=='，'){
						lenstr=ss;
						break;
					}
				}
		}
		else if(match.size()==1){
			lenstr=match.get(0);
		}
		else{ 
			lenstr=match.get(0);
			//tminfo.sxwText.add(match.get(1));
		}
		//如果套话的范围直接到了选项，那么题干就为”“
		int len=lenstr.length();
		if(len>0){
			//将题干中套话之前的部分作为上下文。
			int position=sentence.indexOf(lenstr);
//			if(position!=0){
//				String tt=sentence.substring(0,position+1);
//				if(tt.endsWith("，")) tt=tt.substring(0, tt.length()-1);
//				if(tt.length()>0)
//					tminfo.sxwText.add(tt);
//			}
			//删除套话
			int b=sentence.indexOf(lenstr)+lenstr.length();
			if(boundary==0){
				if(te[0].length()>b)
					te[0]=te[0].substring(b);
			}else{
				if(b>=boundary){
					te[0]="";
					te[1]=sentence.substring(b);
				}else
					te[0]=te[0].substring(b);
			}
		}
		
		//处理一下“图中所示、显示，反应，图1中”
		String[] p={"图中显示","图中所示","图中反映","图中"};
		for(String ps:p){
			//图中为，图中是这种不行
			if(te[0].startsWith(ps)&&!(te[0].startsWith(ps+"为")||te[0].startsWith(ps+"的"))){
				te[0]=te[0].substring(ps.length());
				if(lenstr.equals(""))
					lenstr=ps;
			}
		}
		Pattern pp = Pattern.compile("图.{1,3}中");
		Matcher m = pp.matcher(te[0]);
		int start=-1,end=-1;
		if (m.find()) {
			start = m.start();
			end = m.end();
			if(start==0 &&!(end<te[0].length()&&(te[0].charAt(end)=='为'||te[0].charAt(end)=='是'))){
				//图X中为，图X中是这种不行
				if(lenstr.equals(""))
					lenstr=te[0].substring(start, end);
				te[0]=te[0].substring(end);
			}
		}
	}

}
