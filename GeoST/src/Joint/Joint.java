package Joint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Segmentation.ParamList;
import Segmentation.WordSegment;
import TermRecog.TermRecognition;
import common.MyUtil;
import pos_tagger.Model;
import pos_tagger.PosTag;

public class Joint {
	Map<String, ParamList[]> seg_model;
	Model pos_model;
	String prefix;
	
	public Joint(String prefix) {
		seg_model=new HashMap<>();
		pos_model=new Model();
		this.prefix=prefix;
//		this.seg_model=loadSegModel("model/geo_ctb.model");
//		this.pos_model=loadPosModel("model/CTB.model");
	}
	
	public void loadModel(){
		this.seg_model=loadSegModel(prefix+"model/geo_ctb.model");
		this.pos_model=loadPosModel(prefix+"model/CTB.model");
	}
	
	//模式1：文本--->全部时间和地点
	public List<String> seg_time_place_tag(String str){
		WordSegment seg=new WordSegment(str);
		List<String> result=seg.geo_seg(prefix,seg_model, str, 1,0,null);
		System.out.println(result);
		PosTag postag=new PosTag(str);
		return postag.geo_tag(prefix,pos_model, result, null);
	}
	
	//根据分词list标注词性
	public List<String> postagfromlist(String str, List<String> list){
		PosTag postag=new PosTag(str);
		return postag.geo_tag(prefix,pos_model, list, null);
	}
	
	//文本直接分词和词性标注
	public List<String> seg_postag(String str,int split){
		//str=str.replaceAll(" |\t", "");
		WordSegment seg=new WordSegment(str);
		List<String> result=seg.geo_seg(prefix,seg_model, str,0,split,null);
		//System.out.println(result);
		PosTag postag=new PosTag(str);
		return postag.geo_tag(prefix,pos_model, result,null);
	}
	
	/**文本直接分词和词性标注，Geoscholar调用的主要接口*/
	public List<String> seg_postag(String str,int split,List<String> words){
		//除去句子中的空字符，为保持一致性，在输入句子后统一处理
		//str=str.replaceAll(" |\t", "");
		WordSegment seg=new WordSegment(str);
		List<String> result=seg.geo_seg(prefix,seg_model, str,0,split,words);
		//System.out.println(result);
		PosTag postag=new PosTag(str);
		return postag.geo_tag(prefix,pos_model, result, words);
	}
	
	public List<String> seg_postag(String str){
		//str=str.replaceAll(" |\t", "");
		WordSegment seg=new WordSegment(str);
		List<String> result=seg.geo_seg(prefix,seg_model, str,0,0,null);
		//System.out.println(result);
		PosTag postag=new PosTag(str);
		return postag.geo_tag(prefix,pos_model, result,null);
	}
	
	public void get_postag_from_segfile(String path){
		List<String> segList = MyUtil.read_file(path);
		List<List<String>> fianlresult = new ArrayList<>();
		PosTag postag=new PosTag("");
		for(String seg : segList){
			List<String> result = new ArrayList<>();
			for(String word:seg.split(" ")){
				result.add(word);
			}
			fianlresult.add(postag.geo_tag(prefix,pos_model, result,null));
		}
		MyUtil.writeFile(fianlresult, "data/cePOS.txt");
	}
	
	public List<String> getTerm(String str){
		return TermRecognition.getAllTerm(prefix, str);
	}
	
	public static void segtagdiff(){
		Joint j=new Joint("E://workspace/GeoST/");
		j.loadModel();
		List<String> sentences=MyUtil.read_file("data/10to16A.txt");
		List<List<String>> result=new ArrayList<>();
		for(String str:sentences)
			result.add(j.seg_postag(str,0));
		MyUtil.writeFile(result, "data/10to16out.txt");
	//	Evaluate.diff("data/2016out.txt","data/2016ref.txt", "data/diff.txt");
	}
	public static void main(String[] args) {
		//segtagdiff();
//		if(args.length!=2){
//			System.out.println("please input two parameters,the mode and the path prefix");
//			return;
//		}
		
		Joint j=new Joint("E://workspace/GeoScholar/data/templating/Data/");
		j.loadModel();
		//List<String> list = MyUtil.read_file("E://workspace/GeoST/data/ceshi.txt");
//		String[] sentence={"本届大会期间，北京比华盛顿日出时间晚",
//				"放假期间黄山6点前日出东北方向，玻利维亚东邻巴西，西临太平洋","土壤铜含量在居民点大于200mg/kg","气旋②自东南向西北方向移动","东北平原地势平坦，伏旱严重",
//				"20世纪50~70年代，人口大规模迁入东北地区的主要原因是地处边疆，邻国人口迁入","玻利维亚西南地形复杂","锋通过④地的时间可能为傍晚"};
//		for(String str:list)
//			System.out.println(j.seg_postag(str, str.indexOf('@')==-1?0:str.indexOf('@'), null));
		j.get_postag_from_segfile("E://geo_data/testCWS.txt");
		//System.out.println(j.seg_time_place_tag("去年，北京的平均气温比上海低12摄氏度"));
		
//		int mode=1;20世纪50~70年代，人口大规模迁入东印度的主要原因是地处边疆，邻国人口迁入
//		switch (mode) {
//		case 1:
//			j.segAndPostag();
//			break;
//		case 2:
//			j.pureSeg();
//			break;
//		case 3:
//			j.segAndTag();
//			break;
//		case 4:
//			j.pureTag();
//			break;
//		case 5:
//			j.purePostag();
//			break;
//		default:
//			break;
//		}
		
//		Joint j=new Joint(args[1]);
//		List<String> s=new ArrayList<String>();
//		s.add("放假期间黄山6点前日出东北方向");
//		s.add("为什么加了词典以后结果变差了");
//		System.out.println(j.batch_lexical_analysis(s));
		//Joint j=new Joint("E://workspace/GeoScholar/data/templating/Data/");
//		System.out.println(j.getTerm(""));
		//j.loadModel();
//		List<String> locs=new ArrayList<>();
//		locs.add("上海");locs.add("上海市");locs.add("北京");locs.add("北京天安门");
//		List<String> all = MyUtil.read_file("E://workspace/CSV2TXT/data/所有试题");
//		for(String str:all){
//			j.seg_postag(str, 0, null);
//		}
		//j.seg_postag("西临", 0, null);
		//System.out.println();
		
	}
	
	Map<String, double[]> loadNerModel(String paramsFile)
	{
		File file = new File(paramsFile);
		Map<String, double[]> params = new HashMap<>();
		
		DateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
		String time = format.format(new Date());
		System.out.println(time+" loading ner model file.........");
		BufferedReader br = null;
		try 
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			String s = null;
			while((s = br.readLine()) != null)
			{
				String[] str = s.split("\t");
				double[] tempDouble = {Double.parseDouble(str[1]), 0.0, 0.0};
				params.put(str[0], tempDouble);
			}
		} 
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				br.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		time = format.format(new Date());
		System.out.println(time+" ner model file has been loaded");
		
		return params;
	}
	
	Map<String, ParamList[]> loadSegModel(String file){
		Map<String, ParamList[]> params = new HashMap<String, ParamList[]>();
		String[] temp;
		DateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
		String time = format.format(new Date());
		System.out.println(time+" loading seg model file.........");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			String line = reader.readLine();
			if (line.startsWith("\uFEFF")) {
				line = line.substring(1);
			}
			while (line != null) {
				temp = line.split("\t");
				ParamList[] value=new ParamList[2];
				value[0]=new ParamList(Double.parseDouble(temp[1]));
				value[1]=new ParamList(Double.parseDouble(temp[2]));
				params.put(temp[0], value);
				line = reader.readLine();
			}
			reader.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		time = format.format(new Date());
		System.out.println(time+" seg model file has been loaded");
		return params;
	}
	
	Model loadPosModel(String modelFile){
		Model model=new Model();
		String mf=modelFile;
		File model_file=new File(mf);
		if(!model_file.exists()){
			System.out.println("can't find model file while reading model");
			return null;
		}
		DateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
		String time=format.format(new Date());
		System.out.println(time+" loading postag model......");
		
		try {
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(mf));
			model=(Model)is.readObject();
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		time=format.format(new Date());
		System.out.println(time+" postag model has been loaded");
		return model;
	}
	
//	void write_result(List<List<String>> result){
//		try {
//			if(output_file.equals(path)){
//				System.out.println("can't find output file in tag.propertites");
//				return;
//			}
//			File dest = new File(output_file);  
//			if(!dest.exists()){
//			   dest.createNewFile();
//			}
//			FileOutputStream fos = new FileOutputStream(output_file); 
//	        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
//	        for(List<String> temp:result){
//	        	for(String temp_list:temp)
//	        		osw.write(temp_list+" ");
//	        	osw.write("\n");
//	        }
//			osw.flush();
//			osw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
}
