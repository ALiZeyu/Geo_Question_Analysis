package TermRecog;

import java.util.ArrayList;
import java.util.List;

public class TermRecognition {
	
	public static List<String> getAllTerm(String prefix, String str){
		String temp;
		List<String> result=new ArrayList<>();
		DoubleArrayTrieTerm adt = DoubleArrayTrieTerm.getInstance(prefix+"dic/Term.txt");
		int maxSize = adt.getMax();
		while (str.length() > 0) {
			if (str.length() < maxSize)
				temp = str;
			else
				temp = str.substring(0, maxSize);
			while (temp.length() > 1)
				if (adt.exactMatchSearch(temp) >= 0) {
					result.add(temp);
					break;
				} else
					temp = temp.substring(0, temp.length() - 1);
			str = str.substring(temp.length());
		}
		return result;
	}
}
