package common;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyUtil {
	public static List<String> read_file(String path){
		List<String> data=new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));
			String line = reader.readLine();
			if (line.startsWith("\uFEFF")) {
				line = line.substring(1);
			}
			while (line != null) {
				line=line.trim();
				if(line.equals("")){
					line=reader.readLine();
					continue;
				}
				data.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	public static Set<String> read_set(String path){
		Set<String> data=new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));
			String line = reader.readLine();
			if (line.startsWith("\uFEFF")) {
				line = line.substring(1);
			}
			while (line != null) {
				line=line.trim();
				if(line.equals("")){
					line=reader.readLine();
					continue;
				}
				data.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static void writeFile(List<List<String>> data,String file){
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			for(List<String> list:data){
				for(String str:list)
					writer.write(str+" ");
				writer.write("\n");
			}
			writer.flush();
			writer.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String array2String(String[] array){
		StringBuffer sb = new StringBuffer();
		for(String str : array)
			sb.append(str+" ");
		return sb.toString().trim();
	}
	
	public static void writeList(List<String> data,String file){
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			for(String list:data){
				writer.write(list.trim()+"\n");
			}
			writer.flush();
			writer.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int Contains(List<String> result,String str){
		String[] temp=str.split("\\|");
		for(String s : temp){
			if(result.contains(s))
				return result.indexOf(s);
		}
		return -1;
	}
	
	public static void EA_extraction(String path){
		List<String> data = read_file(path);
		Set<String> eSet = new HashSet<>();
		Set<String> aSet = new HashSet<>();
		for(String str : data){
			String[] sen = str.split("\t")[str.split("\t").length-1].split(" ");
			StringBuffer esb = new StringBuffer();
			StringBuffer asb = new StringBuffer();
			int index = 0;
			while(index < sen.length){
				System.out.println(sen[index]);
				if(sen[index].split("_")[2].startsWith("E")){
					esb.append(sen[index].split("_")[0]+" ");
				}
				else if (sen[index].split("_")[2].startsWith("A")) {
					asb.append(sen[index].split("_")[0]+" ");
				}
				index++;
			}
			eSet.add(esb.toString());
			aSet.add(asb.toString());
			Set<String> result = new HashSet<>();
			result.addAll(eSet);
	        result.retainAll(aSet);
	        System.out.println("交集：" + result);
		}
		List<String> elist = new ArrayList<>(eSet);
		writeList(new ArrayList<>(eSet), "data/all_entity.txt");
		writeList(new ArrayList<>(aSet), "data/all_attri.txt");
	}
	
}
