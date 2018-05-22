package Util;

import java.util.List;

public class Count {
	
	//用于统计知识图谱相关信息
	public static void main(String[] args) {
		count_origin_triple("E://workspace/StanfordDP/data/baike_page");
	}
	
	public static void count_origin_triple(String path){
		int sum = 0;
		List<String> file_list = Util.getFileList(path);
		for(String file : file_list){
			List<String> content = Util.read_file(file);
			int temp = 0;
			for(String sen : content){
				String[] array = sen.split("\t\t");
				temp+=array.length-1;
			}
			sum+=temp;
		}
		System.out.println(sum);
	}

}
