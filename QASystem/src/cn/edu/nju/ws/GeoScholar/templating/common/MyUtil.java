package cn.edu.nju.ws.GeoScholar.templating.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyUtil {
	/**读文件*/
	public static List<String> readListFromFile(String path){
		List<String> result=new ArrayList<String>();
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf8"));
			String line=br.readLine();
			if(line.startsWith("\uFEFF"))
				line=line.substring(1);
			while(line!=null){
//				if(line.trim().length()<2){
//					line=br.readLine();
//					continue;
//				}else {
				result.add(line.trim().replace("__", ""));
				line=br.readLine();
				//}
				
			}
			br.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**把分词、词性标注的结果list转成string，中间用“ ”分隔。*/
	public static String listToString(List<String> tag){
		StringBuffer result=new StringBuffer();
		for(String str : tag)
			result.append(str+" ");
		return result.toString().trim();
	}
	
	/**把分词、词性标注的结果字符串转成原句，即试题文本*/
	public static String tagstrToOriStr(String s){
		StringBuffer result=new StringBuffer();
		String[] tag=s.split(" ");
		for(String str : tag)
			result.append(str.split("_")[0]);
		return result.toString().trim();
	}
	
	/** 把一部分分词、词性标注的结果list转成string，中间用“ ”分隔。*/
	public static String senListToString(List<String> tag, List<Integer> senIndex) {
		Collections.sort(senIndex);
		StringBuffer result = new StringBuffer();
		for(Integer i : senIndex){
			result.append(tag.get(i) + " ");
		}
		return result.toString().trim();
	}
	
	/**判断一句话是不是完整的句子*/
	public static boolean isIP(String tagStr) throws IOException{
		//特殊处理经纬度之内的逗号，是没有词性的，句法树会报错
		if(tagStr==null||tagStr.length()==0) return false;
		String t=(tagStr.indexOf("，")==-1||tagStr.charAt(tagStr.indexOf("，")+1)!='_')?tagStr:tagStr.substring(0, tagStr.indexOf("，"));
		Tree xxTree = TimuInfo.getParseingTree(t.trim());
		String xxType = TimuInfo.getXuanXiangType(xxTree);
		if(xxType.equals("IP"))
			return true;
		return false;
	}
	
	/**把分词、词性标注的结果list转成分词string，用空格分开用于识别地点*/
	public static String listToSegStr(List<String> tag){
		StringBuffer sb=new StringBuffer();
		for(String str : tag)
			sb.append(str.split("_")[0]+" ");
		return sb.toString().trim();
	}
	
	//比较两个文件，输出不一致的句子。
	public static void diff(String mine , String gold){
		List<String> candidates=readListFromFile(mine);
		List<String> references=readListFromFile(gold);
		if(candidates.size()!=references.size()){
			System.out.println("the reference and the candidate consists of different number of lines!");
			return;
		}
		for(int i=0;i<candidates.size();i++){
			if(!candidates.get(i).equals(references.get(i))){
				System.out.println(references.get(i-1));
				System.out.println("mine:  "+candidates.get(i));
				System.out.println("gold:  "+references.get(i));
			}
		}
	}
	
	/**用于判断在某个词之后，另一个词是否出现过
	 * @param p:当前词的文职
	 * @param list:词法分析结果，含有词性
	 * @param str:判断存在的词*/
	public static int following(int p, List<String> list, String str){
		for(int i=p+1;i<list.size();i++){
			if(list.get(i).contains(str))
				return i;
		}
		return -1;
	}
	
	public static int orfollowing(int p, List<String> list, String str){
		List<String> words = new ArrayList<>(Arrays.asList(str.split("\\|")));
		for(int i=p+1;i<list.size();i++){
			if(words.contains(list.get(i).split("_")[0]))
				return i;
		}
		return -1;
	}
	
	/**用标注好的数据生成一份答案
	 * 1、删除无用的词语
	 * 2、将本类型的填槽结果留下，其他的删除*/
	public static void modifyTagMuban(String input, String output, String type){
		List<String> candidates=readListFromFile(input);
		for(int i=0;i<candidates.size();i++){
			// del word
			String[] del = {"最有可能","有可能","最可能","可能","主要"};
			String q = candidates.get(i);
			for(String s:del){
				while (q.contains(s))
					q = q.substring(0, q.indexOf(s) ) + q.substring(q.indexOf(s) + s.length(), q.length());
			}
			candidates.set(i, q);
			//将本类型的填槽结果留下，其他的删除
			if(i%2==1){
				String str = candidates.get(i);
				str=str.split("\\) ")[0];
				if(!str.endsWith(")")) str+=")";
				str=str.replaceAll("_[0|1|2|3|4|5]\\(", "\\(");
				
				if(str.lastIndexOf(")")!=str.indexOf(")")){
					StringBuffer sb = new StringBuffer();
					String[] temp = str.split("\\)");
					for(String s : temp){
						if(s.contains(type+"(")){
							if(!s.endsWith(")")) s+=")";
							sb.append(s+" ");
						}
					}
					str=sb.toString().trim();
				}
				candidates.set(i, str);
			}
		}
		writeFile(candidates, output);
	}
	
	public static void writeFile(List<String> data,String file){
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			for(String str:data)
				writer.write(str+"\n");
			writer.flush();
			writer.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**判断逗号是否有效，即是否包含在（）中*/
	public static boolean isValidPU(List<String> sentence, int index){
		boolean b=false,e=false;
		int bb=index-10>0?(index-10):0;
		int ee=index+10<sentence.size()?(index+10):sentence.size()-1;
		for(int i=bb;i<index;i++){
			if(sentence.get(i).equals("（_PU")){
				b=true;
				break;
			}
		}
		for(int i=ee;i>index;i--){
			if(sentence.get(i).equals("）_PU")){
				e=true;
				break;
			}
		}
		return !(b&&e);
	}
	
	/**判断一个逗号是否在经纬度之中
	 * @param
	 * cotent:句子
	 * position:，在原句子中的index*/
	public static boolean isInJWD(String content,int position){
		Pattern p=Pattern.compile("（?(\\d|\\.)+°[ewsnEWSN]?((\\d|\\.)+[′|’][ewsnEWSN]?)?，(\\d|\\.)+°[ewsnEWSN]?((\\d|\\.)+[′|’][ewsnEWSN]?)?）?");
		Matcher m = p.matcher(content);
		while (m.find()) {
			int start = m.start();
			int end = m.end();
			if(position>start&&position<end)
				return true;
		}
		return false;
	}
	
	//判断某个词是否在几个词当中
	public static boolean strEquals(String ori, String words){
		String[] array = words.split("\\|");
		for(String s:array)
			if(ori.equals(s))
				return true;
		return false;
	}
	
	//判断某个词是否在set当中
	public static boolean setContains(Set<String> set, String str){
		String[] array = str.split("\\|");
		for(String s:array)
			if(set.contains(s))
				return true;
		return false;
	}
	
	public static boolean arrayContains(String[] array, String word){
		for(String w : array)
			if(w.equals(word))
				return true;
		return false;
	}
	
	//句尾：在句子末尾或者是下一个字符是逗号，注意传入的是当前词的索引+1
	public static boolean isAtEnd(int index, List<String> tags){
		if(index==tags.size() || (index<tags.size() && tags.get(index).equals("，_PU")))
			return true;
		return false;
	}
	
	//句首：在句子开头或者是前一个字符是逗号，注意传入当前词索引
	public static boolean isAtbegin(int index, List<String> tags){
		if(index==0 || (index-1>0 && tags.get(index-1).equals("，_PU")))
			return true;
		return false;
	}
	
	/**返回树的最左孩子，可以获得print最左边界的index*/
	public static Tree getLeftNode(Tree t){
		while(t.child.size()>0)
			t=t.child.get(0);
		return t;
	}
	
	//有些数字比如经纬度词性是NN但是第一个字符肯定是数字
	public static boolean startWithNum(String s){
		if(Character.isDigit(s.charAt(0)))
			return true;
		return false;
	}
	
	/**每个模板都有必填的槽，如果没填信息就是不合理的*/
	public static boolean isLegal(int type,String template){
		return false;
	}
	
	/**输出模板的最右边界*/
	public static int getRightBound(QuestionTemplateFromNLP qt){
		int end = -1;
		for (SlotStructureFromNLP ss : qt.slots) 
			if (ss != null) {
				if (ss.endOffset > end)
					end = ss.endOffset;
			}
		return end;
	}
	
	/**判断字符串是否以某几个词语作为结尾*/
	public static boolean endWithStr(String str, String words){
		String[] array = words.split("\\|");
		for(String s:array)
			if(str.endsWith(s))
				return true;
		return false;
	}
	
	public static int getIndexFromList(List<String> list, String words){
		String[] array = words.split("\\|");
		for(String s:array)
			if(list.indexOf(s)!=-1)
				return list.indexOf(s);
		return -1;
	}
	/**获取模板在原始句子中的范围，思路：从句子开头开始找第一个词，找到之后一直找到模板末尾的词。
	 * 但有时候句子终会有相同的词，那么最先发现的肯定是靠前的那个，处理思路是如果开头这个词语在句中出现了多次，取index和lastindex，比较哪一个离末尾较近，就取那一个*/
	public static List<Integer> getRange(String str, List<String> sentence){
		int b=99,e=-1;
		if(str.contains("+")) str=str.split("\\+")[1];
		String[] slots = str.split("@");
		//[图4_NN, 为_VC, 某地_NN, 地质剖面图_NN, ，_PU, 该_DT, 地区_NN, ①_NN, 的_DEG, 岩石_NN, 形成时间_NN, 早于_VV, ②_NN, ，_PU, ②_NN, 的_DEG, 岩石_NN, 形成时间_NN, 晚于_VV, ③_NN]
		//上面这句话后面那个模板岩石是第一个，因此假设end一定是正确的，在所有的begin中找最小的，因此需要用bs存起来。
		List<Integer> bs=new ArrayList<>();
		for (String slot : slots) {
			if (slot.isEmpty()) {
				continue;
			} else {
				int start = -1, end = -1;
				for (int i = 0; i < sentence.size(); i++)
					if (slot.startsWith(sentence.get(i).split("_")[0])) {
						start = i;
						int j = i;
						while (j < sentence.size() && !slot.endsWith(sentence.get(j).split("_")[0])) {
							while (j < sentence.size() && slot.contains(sentence.get(j).split("_")[0]))
								j++;
							j--;
							if (slot.endsWith(sentence.get(j).split("_")[0]))
								end = j;
							else
								j += 2;
						}
						if (j == sentence.size())
							end = j - 1;
						else
							end = j;
						break;
					}
				if(start!=-1) bs.add(start);
				if(end>e) e=end;
			}
		}
		List<Integer> list=new ArrayList<>();
		for(Integer beginid : bs){
			String begin=sentence.get(beginid);
			int tempb=beginid;
			if(sentence.indexOf(begin)!=sentence.lastIndexOf(begin)){
				//System.out.println("重复词语"+sentence);
				int f=sentence.indexOf(begin),s=sentence.lastIndexOf(begin);
				if(f<e && s<e){
					if(e-f>e-s)
						tempb=s;
					else
						tempb=f;
				}
			}
			if(b>tempb) b=tempb;
		}
		list.add(b);list.add(e);
		return list;
	}
	
	public static Tree getTreeFromOri(String str) throws IOException{
		List<String> tagresult = Segment.segmentQuestion(str,0,null);
		String t = MyUtil.listToString(tagresult);
		return TimuInfo.getParseingTree(t.trim());
	}
}
