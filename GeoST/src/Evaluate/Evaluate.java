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
import java.util.List;

import pos_tagger.Data;
import pos_tagger.Instance;

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
}
