package cn.edu.nju.ws.GeoScholar.templating.common;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class Excute {

	public static void excute(List<String> l) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/templating/seg.txt")));
		/*String[] temp = l.split(" ");
		for (int i = 0; i < temp.length; i++) 
		if ( i < temp.length - 1) bw.write(temp[i].split("@")[0]+" "); else bw.write(temp[i].split("@")[0]);*/
		for (String s : l) {
			String[] temp = s.split("_");
			bw.write(temp[0] + "\t" + temp[1] + "\n");
			bw.flush();
		}
		bw.write("\n");
		bw.flush();
		bw.close();
	}
	
}
