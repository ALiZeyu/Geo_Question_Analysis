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
import java.util.List;

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
	
	public static int Contains(List<String> result,String str){
		String[] temp=str.split("\\|");
		for(String s : temp){
			if(result.contains(s))
				return result.indexOf(s);
		}
		return -1;
	}
	
}
