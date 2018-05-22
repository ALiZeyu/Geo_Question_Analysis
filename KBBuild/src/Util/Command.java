package Util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Command {
	public static void exeCmd(String commandStr,String flag) {
		BufferedReader br = null;
		try {
			Process p = Runtime.getRuntime().exec(commandStr);
			br = new BufferedReader(new InputStreamReader(p.getInputStream(),"utf-8"));
			String line = null;
			List<String> result=new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if(flag.equalsIgnoreCase("test"))
					result.add(line + "\n");
				else
					System.out.println(line);
			}
			if(flag.equalsIgnoreCase("test")){
				FileWriter fw=new FileWriter("output.txt");
				for(String str:result)
					fw.write(str);
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		finally
		{
			if (br != null)
			{
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
