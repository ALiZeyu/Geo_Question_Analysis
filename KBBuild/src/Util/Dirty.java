package Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dirty {

	public static void main(String[] args) {
		//TextBookClean("F://地理相关/毕设v/Textbooks/教材");
//		String s = "1.在哪些时段，全球气温有明显的上升？";
//		Pattern p = Pattern.compile("图[\\d]+");
//		Matcher m = p.matcher(s);
//		System.out.println(s.matches("[\\d|\\.]+.*"));
//		String path = "F://地理相关/毕设v/Data_and_tool/zhwiki-latest-pages-articles.xml/zhwiki-latest-pages-articles.xml";
//		Util.cut(path, 102400000);attri_type
		//attri_type_count("E://workspace/StanfordDP/data/query");
		//get_sample_query("E://workspace/StanfordDP/data/query");
		//num_extract("E://workspace/StanfordDP/data/query");
		city_split();
	}
	//删除空行以及“图2”有规律的题目
	public static void TextBookClean(String pre){
		List<String> dirs = Util.getFileList(pre);
		List<String> all = new ArrayList<>();
		//Pattern p = Pattern.compile("图[\\d|\\.]+.*");
		for(String dir:dirs){
			List<String> files = Util.getFileList(dir);
			for(String file:files){
				List<String> data = Util.read_file(file);
				List<String> del = new ArrayList<>();
				for(int i=0;i<data.size();i++){
					if(data.get(i).length()==0)
						continue;
					if(data.get(i).matches("图[\\d|\\.]+.*"))
						continue;
					if(data.get(i).startsWith("思考")||data.get(i).startsWith("读图思考")){
						if(data.get(i+1).matches("[\\d|\\.]+.*")||data.get(i+1).length()==0){
							while(i+1<data.size() && (data.get(i+1).matches("[\\d|\\.]+.*")||data.get(i+1).length()==0))
								i++;
						}
					}
					if(data.get(i).length()!=0)
						del.add(data.get(i));
				}
				all.addAll(del);
				all.add("");
			}
		}
		Util.writeFile(all, "data/rawData/Textbook.txt");
	}
	//统计每种属性类型分别有多少个
	public static void attri_type_count(String path){
		List<String> content_list = Util.getAllFileContentList(path);
//		Map<String, Integer> map_count = new HashMap<>();
//		for(String sen : content_list){
//			String[] temp = sen.split("\t", 2);
//			if(temp[1].length()>8){
//				System.out.println(sen);
//				continue;
//			}
//			map_count.put(temp[1], map_count.getOrDefault(temp[1], 0)+1);
//		}
//		for(Map.Entry<String, Integer> entry : map_count.entrySet()){
//			System.out.println(entry.getKey()+"         "+entry.getValue());
//		}
		Map<String, Set<String>> map_type = new HashMap<>();
		for(String sen : content_list){
			//System.out.println(sen);
			String[] temp = sen.split("\t", 2);
			if(temp[1].length()>8){
				System.out.println(sen);
				continue;
			}
			//
			if(!map_type.containsKey(temp[1])){
				Set<String> temp_list = new HashSet<>();
				temp_list.add(temp[0].split(" ")[1]);
				map_type.put(temp[1], temp_list);
			}else
				map_type.get(temp[1]).add(temp[0].split(" ")[1]);
		}
		for(Map.Entry<String, Set<String>> entry : map_type.entrySet()){
			if(entry.getKey().startsWith("百科")){
				Set<String> attri_set = new HashSet<>(entry.getValue());
				System.out.println(entry.getKey()+"         "+attri_set);
			}
		}
	}
	
	//统计每种属性类型分别有多少个
		public static void get_sample_query(String path){
			List<String> content_list = Util.getAllFileContentList(path);
			List<String> other_list = new ArrayList<>();
			List<String> entity_list = new ArrayList<>();
			Map<String, Set<String>> map_type = new HashMap<>();
			
			for(String sen : content_list){
				//System.out.println(sen);
				String[] temp = sen.split("\t", 2);
				if(temp[1].length()>8){
					System.out.println(sen);
					continue;
				}
				String key = temp[1].startsWith("其他")?"other":"entity";
				if(!map_type.containsKey(key)){
					Set<String> temp_list = new HashSet<>();
					temp_list.add(sen);
					map_type.put(key, temp_list);
				}else
					map_type.get(key).add(sen);
			}
			for(Map.Entry<String, Set<String>> entry : map_type.entrySet()){
				if(entry.getKey().equals("other")){
					int i = 0;
					for(String sen : entry.getValue()){
						if(i>=100)
							break;
						other_list.add(sen);
						i++;
					}
				}else{
					int i = 0;
					for(String sen : entry.getValue()){
						if(i>=100)
							break;
						entity_list.add(sen);
						i++;
					}
				}
			}
			List<String> result = new ArrayList<>(entity_list);
			result.addAll(other_list);
			Util.writeFile(result, "data/200query.txt");
		}
	
	public static void num_extract(String path){
		List<String> query_list = Util.getAllFileContentList(path);
		List<String> result = new ArrayList<>();
		//Set<String> attri_set = new HashSet<>(); && !attri_set.contains(sp[0].split(" ")[1])attri_set.add(sp[0].split(" ")[1]);
		Collections.shuffle(query_list);
		String[] type = {"百科实体	单个", "百科实体	多个"};
		Map<String, Integer> map = new HashMap<>();
		for(String t : type){
			map.put(t, 0);
		}
		for(String sen : query_list){
			String[] sp = sen.split("\t", 2);
			if(sp.length == 2 && map.containsKey(sp[1]) && map.get(sp[1])<5){
				result.add(sen);
				
				map.put(sp[1], map.get(sp[1])+1);
			}
		}
		Util.writeFile(result, "data/baike_query.txt");
	}
	
	public static void city_split(){
		String query_path = "E://workspace/StanfordDP/data/200query.txt";
		String answer_path = "E://workspace/StanfordDP/data/value_result.txt";
		List<String> query_list = Util.read_file(query_path);
		List<String> answer_list = Util.read_file(answer_path);
		List<String> result = new ArrayList<>();
		for(String sen : query_list){
			String q = sen.split("\t")[0];
			boolean flag = true;
			for(String answer : answer_list)
				if(answer.startsWith(q)){
					result.add(answer);
					flag = false;
					break;
				}
			if(flag == true)
				result.add(q);
		}
		Util.writeFile(result, "data/200result.txt");
	}

}
