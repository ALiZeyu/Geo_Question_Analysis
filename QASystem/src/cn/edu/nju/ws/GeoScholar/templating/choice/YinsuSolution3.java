package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;

public class YinsuSolution3 {
	//A越大B越小
	public static String print(ArrayList<String> sentence) {
		String first="",second="";
		int k=sentence.indexOf("越_AD");
		for(int i=0;i<=k+1;i++)
			first+=sentence.get(i).split("_")[0];
		for(int i=k+2;i<sentence.size();i++)
			if(!sentence.get(i).startsWith("，"))
				second+=sentence.get(i).split("_")[0];
		return  k + "+" +first + "@" + second;
	}
}
