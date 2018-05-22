package Joint;

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
import java.util.List;

import pos_tagger.PosTag;

public class Process {
	/**
	 * 调用分词词性标注接口的一些操作*/
	public static void main(String[] args) {
		//put_label();
		//delPosTag();
		Joint j=new Joint("E://workspace/GeoScholar/data/templating/Data/");
		j.loadModel();
		String sen = "101巴拉圭Paaguay0.761";
		List<String> words = new ArrayList<>();
		words.add("Paaguay");
		words.add("人类发展指数");
		System.out.println(j.seg_postag(sen, 0, words));
	}
	
	public static void put_label(){
		Joint j=new Joint("E://workspace/GeoScholar/data/templating/Data/");
		j.loadModel();
		List<String> txt = readListFromFile("E://geo_data/com_entity.txt");
		List<String> result=new ArrayList<>();
		for(String str:txt){
			String[] sentence=str.split(" ");
			StringBuilder sb=new StringBuilder();
			if(sentence[0].contains("_")){
				for(int i=0;i<sentence.length;i++){
					sb.append(sentence[i]+"_"+i+" ");
				}
			}else{
				List<String> list = Arrays.asList(sentence);
				List<String> temp = j.postagfromlist("", list);
				for(int i=0;i<temp.size();i++){
					sb.append(temp.get(i)+"_"+i+" ");
				}
			}
			result.add(sb.toString().trim());
			result.add("e1:;e2:;a:");
		}
		writeFile(result, "E://geo_data/com_entity_1.txt");
	}
	
	public static void delPosTag(){
		List<String> txt = readListFromFile("E://geo_data/com_entity_1.txt");
		List<String> result=new ArrayList<>();
		for(String str:txt){
			if(str.contains("_")){
				String[] array = str.split(" ");
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<array.length;i++){
					sb.append(array[i].split("_")[0]+"_"+i+" ");
				}
//				for(String word : array){
//					sb.append(word.replaceAll("_.*_", "_")+" ");
//				}
				result.add(sb.toString().trim());
			}else{
				result.add(str);
			}
		}
		writeFile(result, "E://geo_data/com_entity_3.txt");
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
	
	public static List<String> readListFromFile(String path){
		List<String> result=new ArrayList<String>();
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf8"));
			String line=br.readLine();
			if(line.startsWith("\uFEFF"))
				line=line.substring(1);
			while(line!=null){
				if(line.trim().length()<2){
					line=br.readLine();
					continue;
				}else {
					result.add(line.trim());
				line=br.readLine();
				}
				
			}
			br.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
