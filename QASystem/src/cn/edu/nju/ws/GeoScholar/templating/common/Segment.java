package cn.edu.nju.ws.GeoScholar.templating.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Joint.Joint;
/**
 * 本类提供词法分析的接口
 * @author 李泽宇
 * @author 王韶杰
 * */
public class Segment {

	public static Joint j = null;
	
	public static void init() throws IOException {
		j=new Joint("./data/templating/Data/");
		j.loadModel();
	}
	
	/**
	 * @param	question
	 * 			待分词文本
	 * @param	split
	 * 			题干与选项的边界位置
	 * @return	分词与词性标注结果
	 * */
	public static List<String> segmentQuestion(String question, int split) throws IOException {
		if (j == null) {
			init();
		}
		List<String> l = j.seg_postag(question, split);
		//l.set(l.indexOf("的_DEG"), "的_DEC");
//		if (l.contains("也_AD"))
//			l.remove("也_AD");
//		if (l.size() > 1 && l.get(0).startsWith("图") && l.get(1).startsWith("中")) {
//			l.remove(1);
//			l.remove(0);
//			if (l.get(0).startsWith("为"))
//				l.remove(0);
//			if (l.get(0).startsWith("所示"))
//				l.remove(0);
//			if (l.get(0).startsWith("，"))
//				l.remove(0);
//		}
		//将风小雾大等分开的四个字合在一起
//		for (int i = 0; i < l.size() - 3; i++) {
//			if (l.get(i).endsWith("NN") && l.get(i + 1).endsWith("VA") && l.get(i + 2).endsWith("NN") && l.get(i + 3).endsWith("VA")) {
//				String s = "";
//				for (int j = i; j < i + 4; j++)
//					s += l.get(j).split("_")[0];
//				s += "_VA";
//				for (int j = i + 3; j > i; j--)
//					l.remove(j);
//				l.set(i, s);
//			}
//		}
		//Excute.excute(l);
		return l;
	}
	
	/**
	 * @param	question
	 * 			待分词文本
	 * @param	words
	 * 			地名词典
	 * @return	分词与词性标注结果*/
	public static List<String> segmentQuestion(String question, List<String> words) throws IOException {
		if (j == null) {
			init();
		}
		List<String> l = j.seg_postag(question, 0, words);
		//Excute.excute(l);
		return l;
	}
	
	public static List<String> segmentQuestion(String question, int split, List<String> words) throws IOException {
		if (j == null) {
			init();
		}
		List<String> l = j.seg_postag(question, split, words);
		//Excute.excute(l);
		return l;
	}
	
	public static List<String> segmentQuestion(String question) throws IOException {
		if (j == null) {
			init();
		}
		List<String> l = j.seg_postag(question, 0);
		//Excute.excute(l);
		return l;
	}
	
	/*输入一句话，返回所有名词*/
	public static List<String> getAllNN(String question){
		if (j == null) {
			try {
				init();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<String> l = j.seg_postag(question, 0);
		List<String> result=new ArrayList<>();
		for(String str:l){
			String[] array=str.split("_");
			if(array[1].startsWith("N"))
				result.add(array[0]);
		}
		return result;
	}
	
	/*输入句子，获取纯分词结果，输出词与词之间用空格间隔的字符串，*/
	public static String getSegment(String question, List<String> loclist){
		if (j == null) {
			try {
				init();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<String> l = j.seg_postag(question, 0, loclist);
		StringBuffer sb=new StringBuffer();
		for(String str : l)
			sb.append(str.split("_")[0]+" ");
		return sb.toString().trim();
	}
	
	/*输入句子，获取纯分词结果，输出词与词之间用空格间隔的字符串*/
	public static String getSegment(String question){
		if (j == null) {
			try {
				init();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<String> l = j.seg_postag(question, 0, null);
		StringBuffer sb=new StringBuffer();
		for(String str : l)
			sb.append(str.split("_")[0]+" ");
		return sb.toString().trim();
	}
	
	public static void segment(String question, int split) throws IOException {
		if (j == null) {
			init();
		}
		List<String> l = j.seg_postag(question, split);
		//Excute.excute(l);
	}
	
	public static void segment(String question) throws IOException {
		if (j == null) {
			init();
		}
		List<String> l = j.seg_postag(question, 0);
		//Excute.excute(l);
	}
	
	public static List<String> getTerm(String question){
		if (j == null) {
			try {
				init();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return j.getTerm(question);
	}

}
