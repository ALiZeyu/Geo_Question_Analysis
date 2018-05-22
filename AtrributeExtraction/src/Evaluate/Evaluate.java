package Evaluate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

import common.MyUtil;

public class Evaluate {
	
	public static void calculate(String out , String ref){
		List<List<String>> candidates=get_sentences(out);
		List<List<String>> references=get_sentences(ref);
		if(candidates.size()!=references.size()){
			System.out.println("the reference and the candidate consists of different number of lines!");
			return;
		}
		int nTotalCorrectWords=0;
		int nCandidateWords=0;
		int nReferenceWords = 0;
		// float nTotalCorrectTags=0;
		// float nCandidateTags=0;
		// float nReferenceTags=0;
		for (int index = 0; index < candidates.size(); index++) {
			List<String> candidate = candidates.get(index);
			List<String> reference = references.get(index);
			nCandidateWords += candidate.size();
			nReferenceWords += reference.size();
			int indexCandidate = 0;
			int indexReference = 0;
			int i = 0, j = 0;
			while (i < candidate.size() && j < reference.size()) {
				if (candidate.get(i).equals(reference.get(j))) {
					nTotalCorrectWords++;
					indexCandidate += candidate.get(i).length();
					indexReference += reference.get(j).length();
					i++;
					j++;
				} else {
					if (indexCandidate == indexReference) {
						indexCandidate += candidate.get(i).length();
						indexReference += reference.get(j).length();
						i++;
						j++;
					} else if (indexCandidate < indexReference) {
						indexCandidate += candidate.get(i).length();
						i++;
					} else {
						indexReference += reference.get(j).length();
						j++;
					}
				}
			}
		}
		float word_precision = (float) (nTotalCorrectWords) / (float) (nCandidateWords);
		float word_recall = (float) (nTotalCorrectWords) / (float) (nReferenceWords);
		float word_fmeasure = (2 * word_precision * word_recall) / (word_precision + word_recall);
		System.out.println(nTotalCorrectWords);
		System.out.println(nCandidateWords);
		System.out.println(nReferenceWords);
		System.out.println("Word precision:" + word_precision);
		System.out.println("Word recall:"+word_recall);
		System.out.println("Word F-measure:"+word_fmeasure);
		diff(out, ref, "data/diff.txt");
	}
	
	
	public static void diff(String out , String ref , String diff){
		List<String> candidates=get_string_list(out);
		List<String> references=get_string_list(ref);
		List<String> result=new ArrayList<>();
		if(candidates.size()!=references.size()){
			System.out.println("the reference and the candidate consists of different number of lines!");
			return;
		}
		for(int i=0;i<candidates.size();i++){
			if(!candidates.get(i).equals(references.get(i))){
				result.add(candidates.get(i));
				result.add(references.get(i));
			}
		}
		writeFile(result, diff);
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
	
	public static List<List<String>> get_sentences(String file){
		List<List<String>> result=new ArrayList<List<String>>();
		String[] temp;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
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
				List<String> list=new ArrayList<String>();
				temp=line.split(" ");
				for(int i=0;i<temp.length;i++)
					list.add(temp[i]);
				result.add(list);
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<String> get_string_list(String file){
		List<String> result=new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
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
				result.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<Double> eva_en_ar(String test_file, String out_file){
		List<String> test_data = MyUtil.read_file(test_file);
		List<String> out_data = MyUtil.read_file(out_file);
		int right = 0, precision=0, recall=0, split = 0;
		for(int i=0;i<test_data.size();i++){
			int temp=0;
			List<String> predict = name_extract(out_data.get(i));
			precision += predict.size();
			List<String> reference = name_extract(test_data.get(i));
			recall += reference.size();
			for(String en : predict)
				if(reference.contains(en))
					temp++;
			right += temp;
			if(reference.size() == 2){
				if(temp>0)
					split++;
			}else{
				if(temp>1)
					split++;
			}
		}
		List<Double> result = new ArrayList<>();
		double pp = (double)right/precision;
		double rr = (double)right/recall;
		double ff = 2*pp*rr/(pp+rr);
		double seg = (double)split/out_data.size();
		result.add(pp);
		result.add(rr);
		result.add(ff);
		result.add(seg);
		return result;
	}
	
	public static List<String> name_extract(String sen){
		List<String> en = new ArrayList<>();
		if(sen.indexOf("\t")!=-1) sen = sen.split("\t")[sen.split("\t").length-1];
		String[] tags = sen.split(" ");
		int i=0;
		while(i < tags.length){
			if(tags[i].endsWith("EB")||tags[i].endsWith("AB")){
				String[] labels = new String[2];
				String[] le = {"EI","EE"};
				String[] la = {"AI","AE"};
				labels = tags[i].endsWith("EB")?le:la;
				
				int b=i;
				i++;
				while(i < tags.length && (tags[i].endsWith(labels[0])||tags[i].endsWith(labels[1])))
					i++;
				int e=i;
				String temp="";
				for(int j=b;j<e;j++)
					temp+=tags[j].split("_")[0];
				en.add(temp);				
			}else
				i++;
		}
		return en;
	}
	
	public static void rule_count(String path){
		List<String> data =MyUtil.read_file(path);
		double right = 0;
		int mine = 0,all = 0;
		int index = 0;
		while(index<data.size()){
			String sen = data.get(index);
			if(sen.startsWith("slot")){
				all+=2;
				sen = sen.substring(9);
				System.out.println(sen);
				String[] slot = sen.split(",");
				String[] answer = data.get(++index).split(";");
				for(int i=0;i<2;i++){
					if(i<slot.length && slot[i].startsWith("null"))
						continue;
					mine++;
					if(i==0 && slot[i].endsWith(answer[i]))
						right+=1;
					else if(i<slot.length && i<answer.length && slot[i].startsWith(answer[i]))
						right+=1;
				}
			}else{
				index++;
			}
		}
		System.out.println(right+"\t"+mine+"\t"+all);
	}
	
	public static void rule_count_bijiao(String path){
		List<String> data =MyUtil.read_file(path);
		double right = 0;
		int mine = 0,all = 0,sum=0;
		int index = 0;
		while(index<data.size()){
			String sen = data.get(index);
			if(sen.startsWith("slot")){
				Set<String> m_list = new HashSet<>();
				Set<String> g_list = new HashSet<>();
				while(data.get(index).startsWith("slot")){
					sen = data.get(index);
					if(sen.startsWith("slot比较")){
						mine+=2;
						sen = sen.substring(7);
						String[] slot = sen.split(",");
						m_list.add(slot[0]);
						m_list.add(slot[1].equals("null")?slot[3]:slot[1]);
					}
					index++;
				}
				String[] answer = data.get(index).split(";");
				sum++;
				for(String a : answer){
					if(a.indexOf("、")>0){
						g_list.add(a.split("、")[0]);
						g_list.add(a.split("、")[1]);
					}
					else
						g_list.add(a);
				}
				all+=g_list.size()-1;
				System.out.println(sen);
				for(String a : m_list)
					if(g_list.contains(a))
						right++;
			}else{
				index++;
			}
		}
		System.out.println(sum);
		System.out.println(right+"\t"+mine+"\t"+all);
	}
	
	
}
