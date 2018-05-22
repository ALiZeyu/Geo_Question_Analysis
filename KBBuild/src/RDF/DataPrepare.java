package RDF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DP.Segment;
import Util.Util;

public class DataPrepare {

	public static void main(String[] args) throws IOException {
//		Segment.init();
//		List<String> file_list = Util.getFileList("E:/workspace/StanfordDP/data/dic/Entity_dic");
//		int[] sum = new int[1];
//		for(String name : file_list){
//			String file_name = name.substring(name.lastIndexOf("\\")+1);
////			if(file_name.equals("chengshi.txt")||file_name.equals("dayang.txt"))
////				continue;
//			main_extract(file_name,sum);
//		}
		main_extract("dayang.txt");
	}
	
	/**
	 * 输入：整理好的词典的名字
	 * 抽取百科
	 * */
	public static void main_extract(String file_name){
		String dic_path = "E://workspace/StanfordDP/data/dic/Entity_dic/"+file_name;
		String baike_path = "E://workspace/StanfordDP/data/baike_page/" + file_name;
		String type_path = "E://workspace/StanfordDP/data/attri_type/" + file_name;
		String query_path = "E://workspace/StanfordDP/data/query/" + file_name;
		String candidate_path = "E://workspace/StanfordDP/data/canSen/" + file_name;
		//抽取百科网页
		//page_extract(dic_path, baike_path);
		//attribute_classify(baike_path, type_path);
		//统计属性并构造缺失属性query
		//int query = query_build(baike_path, type_path, query_path);
		//爬取网页、抽取生语料
		candidateSen_extract(query_path, candidate_path);
//		System.out.println("done");
	}
	
	//爬取百科infobox网页
	public static void page_extract(String dic_path, String result_path){
		String[] args = new String[] {"python", "E:/PyWorkspace/Crawler/InfoboxCrawler.py", dic_path, result_path};
		Util.command_exec(args);
	}
	
	public static void candidateSen_extract(String query_path, String candidate_path){
		String[] args = new String[] {"python", "E:/PyWorkspace/Crawler/Cralwer.py", query_path, candidate_path};
		Util.command_exec(args);
	}
	
	/**
	 * result_path：爬取的infobox文件每一行一个实体，中间用\t分开，属性及其属性值之间有:::分隔。
		//query_path：构造的query存在这个文件中
		//属性评判标准：从个数角度：单个实体、多个实体、一句话(通过看是否顿号分隔以及)
		//从类型角度：人物、、地名、百科实体、数字、数字 + 单位、其他
		//是否有后缀
	 */
	public static void attribute_classify(String result_path, String attri_type_path){
		List<String> data = Util.read_file(result_path);
		//单个属性以及其所有实体的属性值
		Map<String, Set<String>> attribute_value = new HashMap<>();
		//纯文本，key是实体，value是属性字符串
		Map<String, String> raw_text = new HashMap<>();
		//Set<String> attribute_set = new HashSet<>();
		Map<String, Integer> attribute_map = new HashMap<>();
		int entity_num = 0;
		for(String str : data){
			String[] array = str.split("\t\t", 2);
			if(array.length!=2) continue;
			entity_num++;
			raw_text.put(array[0].split("\\[")[0], array[1]);
			String[] attri = str.split("\t\t");
			//当前实体的属性集合
			Set<String> attri_set = new HashSet<>();
			for(int i=1; i<attri.length; i++){
				String[] temp = attri[i].split(":::");
				String key = attri[i].split(":::")[0];
				if(key.endsWith(":")||key.endsWith("：")) key = key.substring(0, key.length()-1);
				attri_set.add(key);
				if(temp.length>1){
					if(!attribute_value.containsKey(key))
						attribute_value.put(key, new HashSet<>());
					attribute_value.get(key).add(attri[i].split(":::")[1]);
				}
					
			}
			for(String a : attri_set)
				attribute_map.put(a, attribute_map.getOrDefault(a, 0)+1);
		}
        //当前种类实体属性集合
        Map<String, String[]> attri_type = new HashMap<>();
        List<String> type_list = new ArrayList<>();
//        for(Map.Entry<String, Set<String>> entry : attribute_value.entrySet()){
//        	String key = entry.getKey();
//        	List<String> temp_str = new ArrayList<>(entry.getValue());
//        	String type = temp_str.toString();
//        	type_list.add(key+"\t"+type);
//        	//attri_type.put(key, type);
//        }
        //String[] bug = {"人口","地区生产总值","车牌代码", "面积"};
        for(Map.Entry<String, Set<String>> entry : attribute_value.entrySet()){
        	String key = entry.getKey();
        	Set<String> value_set = entry.getValue();
        	String[] type = get_attri_type(value_set);
        	type_list.add(key+"\t"+type[0]+"\t"+type[1]);
        	attri_type.put(key, type);
        }
        
        Util.writeFile(type_list, attri_type_path);
	}
	
	public static int query_build(String result_path, String type_path, String query_path){
		List<String> data = Util.read_file(result_path);
		Map<String, Set<String>> infobox = new HashMap<>();
		//单个属性以及其所有实体的属性值
		Map<String, Set<String>> attribute_value = new HashMap<>();
		Map<String, String> raw_text = new HashMap<>();
		//Set<String> attribute_set = new HashSet<>();
		Map<String, Integer> attribute_map = new HashMap<>();
		int entity_num = 0;
		for(String str : data){
			//System.out.println(str);
			String[] array = str.split("\t\t", 2);
			if(array.length!=2) continue;
			entity_num++;
			raw_text.put(array[0].split("\\[")[0], array[1]);
			String[] attri = str.split("\t\t");
			//当前实体的属性集合
			Set<String> attri_set = new HashSet<>();
			for(int i=1; i<attri.length; i++){
				String[] temp = attri[i].split(":::");
				String key = attri[i].split(":::")[0];
				if(key.endsWith(":")||key.endsWith("：")) key = key.substring(0, key.length()-1);
				attri_set.add(key);
				if(temp.length>1){
					if(!attribute_value.containsKey(key))
						attribute_value.put(key, new HashSet<>());
					attribute_value.get(key).add(attri[i].split(":::")[1]);
				}
					
			}
			infobox.put(attri[0].split("\\[")[0], attri_set);
			for(String a : attri_set)
				attribute_map.put(a, attribute_map.getOrDefault(a, 0)+1);
		}
		int bound = entity_num/5+1;
		//删除出现次数小于20%的属性
		Iterator<Map.Entry<String, Integer>> it = attribute_map.entrySet().iterator();  
        while(it.hasNext()){  
            Map.Entry<String, Integer> entry = it.next();
            int times = entry.getValue();
            if(times < bound){
            	String key = entry.getKey();
            	it.remove();
            	attribute_value.remove(key);
            }
        }
        //当前种类实体属性集合
        Set<String> attribute_set = attribute_map.keySet();
        
//        Map<String, String[]> attri_type = new HashMap<>();
//        for(Map.Entry<String, Set<String>> entry : attribute_value.entrySet()){
//        	String key = entry.getKey();
//        }
        //读取属性类型存入map，在下面查询一并写入文件
        List<String> type_list = Util.read_file(type_path);
        Map<String, String> type_map = new HashMap<>();
        for(String sen : type_list){
        	String[] temp = sen.split("\t", 2);
        	type_map.put(temp[0], temp[1]);
        }
        //构造query
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, Set<String>> entry : infobox.entrySet()){
        	List<String> temp_list = new ArrayList<>();
        	String name = entry.getKey();
        	String attri_str = raw_text.get(name);
        	Set<String> set = entry.getValue();
        	for(String attribute : attribute_set){
        		if(!set.contains(attribute) && attri_str.indexOf(attribute)==-1){
        			String attri_type = type_map.getOrDefault(attribute, "");
        			temp_list.add(name+" "+attribute+"\t"+attri_type);
        		}
        	}
        	post_del(temp_list);
        	list.addAll(temp_list);
        }
        int num = list.size();
        Util.writeFile(list, query_path);
        return num;
	}
	
	/**输入某种属性未缺失的属性值获取
	 * @param value_set 本类属性的未缺失属性值集合*/
	public static String[] get_attri_type(Set<String> value_set){
		//1、百科实体(0)、人名(1)、地名(2)、数字(3)、数字+单位(4)、其他(5)；2、单个(6)、多个(7)、句子(8)；3、后缀
		String[] type_name = {"百科实体", "人名", "地名", "数字", "数字+单位", "其他", "单个", "多个","句子"};
		String[] type = new String[2];
		int[] count = new int[9];
		for(String value : value_set){
			List<Integer> type_list = getValueType(value);
			for(int t : type_list)
				count[t]++;
		}
		int type_num = Util.array_range_max(count, 0, 6);
		int num = Util.array_range_max(count, 6, 9);
		type[0] = type_name[type_num];
		type[1] = type_name[num];
		return type;
	}
	/**
	 * 1、判断属性值中是否含有"、","，","；"," ","	"等分隔符，如果有转2，否则转3
	 * 2、将属性值按照分隔符分成多个子句，对每个子句判断类型并投票，如果投票结果为“其他”，判断每个子句分词结果列表中词语个数是否大于2，符合条件则类型为“句子”，否则是“多个”
	 * 3、判断单个属性值的类型，如果类型为其他且分词结果中词个数大于4个，那么类型判为“句子”，否则判为“单个”
	 * */
	public static List<Integer> getValueType(String value){
		//1、百科实体(0)、人名(1)、地名(2)、数字(3)、数字+单位(4)、其他(5)；2、单个(6)、多个(7)、句子(8)；3、后缀
		List<Integer> result = new ArrayList<>();
		String[] split = {"、","，","；"," ","	"};
		value = value.replaceAll("[ |\t]*[\\(|（].*[\\)|）][ |\t]*", "");
		//判断是否含有逗号句号等
		String sp = Util.array_contain_split(value, split);
		if(sp != null){
			//含有逗号或者句号
			String[] fragment = value.split(sp);
			int[] count = new int[6];
			//判断每一部分的类型
			for(String frag : fragment){
				int type = getSingleValueType(frag);
				count[type]++;
			}
			int max = Util.array_max(count);
			//判断是句子还是实体的办法暂时是根据分词的个数
			if(max == 5){
				int c = 0;
				for(String frag : fragment){
					try {
						List<String> seg_list = Segment.segmentQuestion(frag);
						if(seg_list.size()>1 && !(seg_list.size() == 2 && seg_list.get(1).length()==1))
							c++;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(c>fragment.length/2 && (sp.equals("，")||sp.equals(",")))
					result.add(8);
				else
					result.add(7);
			}else{
				result.add(7);
				result.add(max);
			}
		}else{
			//single 
			int type = getSingleValueType(value);
			if(type == 5){
				try {
					List<String> seg_list = Segment.segmentQuestion(value);
					if(value.length()>4 && seg_list.size()>1 && !(seg_list.size() == 2 && seg_list.get(1).length()==1))
						result.add(8);
					else
						result.add(6);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("value type seg error");
					result.add(6);
				}
			}else
				result.add(6);
			result.add(type);
		}
		return result;
	}
	/**3：数字；4：数字+单位；0：百科实体；1：人名；2：地名；5：其他*/
	/**1.判断属性值中是否含有百科页面超链接，如果存在的话转步骤2，否则转步骤三；
	 * 2.爬取超链接所对应的页面并抽取页面infobox中含有的属性，如果属性集合中含有"民族"、"出生年月"、"国籍"等属性则判断类别是“人名”，否则判为“百科实体”类别。
	 * 3.利用正则表达式匹配判断当前属性值是否是“数字”或者“数字+单位”，否则判为“其他”类别*/
	public static int getSingleValueType(String value){
		List<Integer> result = new ArrayList<>();
		if(isNum(value)==1){
			result.add(3);
		}else if(isNum(value)==2)
			result.add(4);
		else if(value.contains("@@@")){
			int b = value.indexOf("[");
			int e=b+1;
			while(e < value.length() && value.charAt(e)!=']') e++;
			String word = value.substring(b+1, e);
			if(word.indexOf("@@@")==-1)
				result.add(0);
			else{
				if(isNum(word.split("@@@")[0])==0){
					String[] p_args = new String[4];
					p_args[0] = "python";
					p_args[1] = "E:/PyWorkspace/Crawler/single_infobox.py";
					p_args[2] = word.split("@@@")[1];
					p_args[3] = "E:/workspace/StanfordDP/data/temp_info.txt";				
					Util.command_exec(p_args);
					System.out.println(p_args[2]);
					List<String> info_list = Util.read_file(p_args[3]);
					String[] person_array = {"民族","出生","国籍","成就"};
					String[] loc_array = {"地区"};
					if(Util.array_count(info_list, person_array))
						result.add(1);
					else if(Util.array_count(info_list, loc_array))
						result.add(2);
					else {
						result.add(0);
					}
				}else{
					if(isNum(word.split("@@@")[0])==1)
						result.add(3);
					else
						result.add(4);
				}
				
			}
		}else{
			result.add(5); 
		}
		return result.get(0);
	}
	
	//1：单个数字；2：数字+单位；0：不是数字
	public static int isNum(String value){
		//String num_pattern = "^-?([1-9]d*.d*|0.d*[1-9]d*|0?.0+|0)$";
		String num_pattern = "^\\d(\\d|\\+|\\-|\\.|,)*";
		if(value.matches(num_pattern)) return 1;
		Pattern p = Pattern.compile(num_pattern);
		Matcher m = p.matcher(value);
		while(m.find()){
			int start = m.start();
			if(start==0)
				return 2;
		}
		return 0;
	}
	
	public static boolean isInstance(String value){
		return false;
	}
	
	//删除暂时无法处理的属性、有覆盖关系的属性。
	public static void post_del(List<String> query){
		String[] suffix = {"GDP","生产总值","现任领导"};
		Set<String> del_set = new HashSet<>();
		for(String w : suffix) del_set.add(w);
//		Map<String, String> contain_map = new HashMap<>();
//		contain_map.put("", "")
		Iterator<String> it = query.iterator();
		while(it.hasNext()){
			String temp = it.next();
			for(String s : del_set)
				if(temp.endsWith(s)){
					it.remove();
					break;
				}
		}
	}
}
