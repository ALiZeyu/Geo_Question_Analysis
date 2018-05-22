package cn.edu.nju.ws.GeoScholar.templating.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cn.edu.nju.nlp.main.TimeAndLocMain;
/**
 * 时间地点识别,封装自Louc程序
 * @author Lizy
 * */
public class NerRecognition {

	public static TimeAndLocMain talm=null;
	
	public static void init() throws IOException {
		talm=new TimeAndLocMain();
		talm.load_NERModel("./data/templating/Data/");
	}
	
	/**输入一句话，返回所有时间*/
	public static List<String> getTime(String s){
		return talm.timeList(s);
	}
	
	/**输入一句话，判断是不是时间*/
	public static boolean isTime(String s){
		return talm.isTime(s);
	}
	
	/**输入一句分词结果，返回所有地点*/
	public static List<String> getLocation(String s){
		//地点识别中不能出现/
		s=s.replaceAll(" \\/ | \\/", " ");
		return talm.locList(s);
	}
	
	/**输入一句话，返回所有地点，需要传入地点list*/
	public static List<String> getLocationFromStr(String s, List<String> loclist){
		String seg=Segment.getSegment(s,loclist);
		seg=seg.replaceAll(" \\/ | \\/", " ");
		List<String> seglist=new ArrayList<>(Arrays.asList(seg.split(" ")));
		List<String> locCopy=new ArrayList<>(loclist);
		Iterator<String> locIt=locCopy.iterator();
		//删除loclist(locCopy)中和本句话无关的词语
		while(locIt.hasNext()){
			String str=locIt.next();
			if(!seglist.contains(str))
				locIt.remove();
		}
		List<String> locs=talm.locList(seg);
		Iterator<String> it=locs.iterator();
		//删除大别山区识别出来的大别山
		while(it.hasNext()){
			String str=it.next();
			if(locCopy.contains(str))
				it.remove();
			else{
				for(String ss : locCopy){
					if(ss.contains(str) || str.contains(ss)){
						it.remove();
						break;
					}
				}
			}
		}
		locs.addAll(locCopy);
		return locs;
	}
	
	/**输入一句话，返回所有地点*/
	public static List<String> getLocationFromStr(String s){
		String seg=Segment.getSegment(s,null);
		seg=seg.replaceAll(" \\/ | \\/", " ");
		return talm.locList(seg);
	}
	
}
