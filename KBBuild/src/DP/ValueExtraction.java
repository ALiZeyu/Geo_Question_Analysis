package DP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.DocFlavor.STRING;

import Util.Util;

public class ValueExtraction {
	static String[] value_in_set = {"高校","高等学府@@高校","高等院校@@高校"};
	
	/**
	 * 总流程，读入cansen文本文件、按照keyword依次抽取属性值
	 * @param path 候选抽取句子文件
	 * 文件格式：1、query+属性类型。2、KBanswer。3、side_entity+++行数。4、若干行实体。5、若干行待抽取句子
	 * 抽取流程：
	 * 1、如果
	 * 2、如果是"人名	多个"且sideEntity中含有标题为“人物”的实体，那么就取人物实体列表下面
	 * */
	public static void extraction_pipeline(String path) throws IOException{
		List<String> data = Util.read_file(path);
		List<String> result = new ArrayList<>();
		int index=0;
		while(index<data.size()){
			while(index<data.size() && data.get(index).indexOf(":::")==-1) index++;
			if(index>=data.size())
				break;
			//下面多少句是待抽取文本
			int sen_num = Integer.parseInt(data.get(index).split(":::")[1]);
			String attri_type = data.get(index).split(":::")[2];
			String query_word = data.get(index).split(":::")[0];
			//抽取KBanswer与百度百科实体标题
			if(++index>=data.size()) break;
			String KBanswer = "";
			if(query_word.contains("中文名"))
				KBanswer = query_word.split(" ")[0];
			String[] exact_answer = data.get(index).split("\t\t",2);
			KBanswer = exact_answer[0].endsWith("+++")?KBanswer:exact_answer[0].split("\\+\\+\\+")[1];
			String baikeTitle = exact_answer[1].endsWith("@@@")?"":exact_answer[1].split("@@@")[1];
			//抽取side_entity
			if(++index>=data.size()) break;
			Map<String, List<String>> side_entity_map = new HashMap<>();
			if(data.get(index).startsWith("side_entity")){
				int entity_num = Integer.parseInt(data.get(index).split("\\+\\+\\+")[1]);
				String entity_type = "";
				List<String> entity_list = new ArrayList<>();
				for(int i = index+1;i<=index+entity_num && i<data.size();i++){
					if(data.get(i).endsWith("+++")){
						if(entity_type.equals(""))
							entity_type = data.get(i).substring(0, data.get(i).length() - 3);
						else{
							side_entity_map.put(entity_type, entity_list);
							entity_type = "";
							i--;
							entity_list = new ArrayList<>();
						}
					}
					else {
						entity_list.add(data.get(i));
					}
				}
				side_entity_map.put(entity_type, entity_list);
				index+=entity_num;
			}
			//抽取待处理句子，KBanswer, side_entity, Baike_title都存好了
			List<String> temp = new ArrayList<>();
			for(int i=index+1;i<=index+sen_num && i<data.size();i++){
				temp.add(data.get(i));
			}
			String attri_value = "";
			if(KBanswer.equals("")){
				//特殊处理“人名	多个”抽取侧边实体
				if(attri_type.equals("人名	多个") && (query_word.endsWith("人")||query_word.endsWith("人物"))){
					//找sideEntity里面的实体
					for(Map.Entry<String, List<String>> entry : side_entity_map.entrySet()){
						if(!attri_value.equals("")) break;
						if(entry.getKey().endsWith("人物")){
							for(int i=0;i<entry.getValue().size()&&i<5;i++)
								attri_value += entry.getValue().get(i)+"@@@";
							if(!attri_value.equals(""))
								attri_value = attri_value.substring(0,attri_value.length()-3);
						}
					}
				}
				else if (attri_type.equals("人名	单个") && !baikeTitle.equals("")) {
					//找百度百科实体标题
					attri_value = baikeTitle;
				}
				else if(attri_type.equals("百科实体	单个") && baikeTitle.endsWith(query_word.split(" ")[1]))
					//主要处理某某机场
					attri_value = baikeTitle;
				else if(attri_type.equals("百科实体	多个") && query_word.endsWith("景点")){
					//找sideEntity里面的实体,优先找景点，没有的话就找地点
					List<String> backup = new ArrayList<>();
					for(Map.Entry<String, List<String>> entry : side_entity_map.entrySet()){
						if(!attri_value.equals("")) break;
						if(entry.getKey().endsWith("景点")){
							for(int i=0;i<entry.getValue().size()&&i<3;i++)
								attri_value += entry.getValue().get(i)+"@@@";
							if(!attri_value.equals(""))
								attri_value = attri_value.substring(0,attri_value.length()-3);
						}
						else if(entry.getKey().endsWith("地点")||entry.getKey().endsWith("地名"))
							backup = entry.getValue();
					}
					if(attri_value.equals("") && backup.size()>0){
						for(int i=0;i<backup.size()&&i<3;i++)
							attri_value += backup.get(i)+"@@@";
						if(!attri_value.equals(""))
							attri_value = attri_value.substring(0,attri_value.length()-3);
					}
					
				}
				if(attri_value.equals(""))
					attri_value = value_extract(query_word, attri_type, temp);
			}else
				attri_value = KBanswer;
			result.add(query_word+":::"+attri_value);
			index += sen_num;
		}
		Util.writeFile(result, "data/value_result.txt");
	}
	
	//是、为、作为、担任
	//属性先验：是否数值、是否含有顿号(不含有冒号)、集合
	/**
	 * 输入：query：搜索的词条；data：当前属性\t句子List；
	 * value_type：当前属性的类型，side_entity_map：页面侧面实体
	 * */
	public static String value_extract(String query, String value_type, List<String> data) throws IOException{
		List<String> candidate = new ArrayList<>();
		String entity = query.split(" ")[0];
		String attribute = query.split(" ")[1];
		if(data.size() == 0)
			return "";
		//单个还是多个，通过属性类型来判断
		boolean single = (value_type.endsWith("多个")&&!value_type.startsWith("数字"))?false:true;
		//属性值限定在一个集合内
		if(null != is_in_array(attribute, value_in_set)){
			String attri = is_in_array(attribute, value_in_set);
			candidate = extract_by_set(attri, data);
		}
		else{
			for(String sentence : data){
				//当前这句话的关键词， attribute是全局query
				if(sentence.split("\t", 2).length!=2){
					//System.out.println(sentence);
					continue;
				}
				String trigger = sentence.split("\t")[0];
				sentence = sentence.split("\t")[1];
				//将属性词放入前处理词典防止分词没分出来
				List<String> temp_dic = new ArrayList<>();
				temp_dic.add(trigger);
				temp_dic.add(attribute);
				List<String> split_sen = Util.sen_split(sentence);
				for(String sen : split_sen){
					List<String> seg = Segment.segmentQuestion(sen, 0, temp_dic);
					//System.out.println(seg);
					List<Integer> indexes = get_key_index(trigger, attribute, seg);
					for(int index : indexes){
						if(value_type.startsWith("数字")){
							//除去“面积”的2.5倍这些
							if(index+1<seg.size() && seg.get(index+1).startsWith("的"))
								continue;
							String num_value = num_extract(seg, index, attribute, value_type);
							if(!num_value.equals("") && num_value.length() > 1)
								candidate.add(num_value);
						}else if(value_type.startsWith("人名")){
							//人名单个和多个
							List<String> temp = NRExtract(index, seg, ".*NR");
							candidate.addAll(temp);
						}else if(value_type.startsWith("地名")){
							//地名单个、多个
							List<String> temp = NRExtract(index, seg, ".*NR");
							candidate.addAll(temp);
						}else{
							//百科实体、其他实体单个、多个、句
							List<String> temp = new ArrayList<>();
							if(value_type.endsWith("单个"))
								temp = NRExtract(index, seg, ".*[NR|NN]");
							else{
								String answer_type = value_type.endsWith("句子")?"sen":"multi";
								temp = multiExtract(index, seg, ".*[NR|NN]", answer_type);
							}
							candidate.addAll(temp);
						}
					}
				}
			}
		}
		System.out.println(candidate);
		return post_count(candidate, entity, single);
	}
	/*抽取数字类型的属性值，先找数字，再找单位**/
	public static String num_extract(List<String> seg, int index, String attribute, String value_type){
		int is_index = Util.get_is_index(seg, index);
		String[] num_pattern = {"\\d{2,4}年(\\d{1,2}(月|月份)(\\d{1,2}日)?)?",
				"[0|1|2|3|4|5|6|7|8|9|０|１|２|３|４|５|６|７|８|９|一|二|三|四|五|六|七|八|九|零|十|百|千|万|亿|〇|两]+[\\.|\\+|\\_|．]{0,1}[0|1|2|3|4|5|6|7|8|9|０|１|２|３|４|５|６|７|８|９|一|二|三|四|五|六|七|八|九|零|十|百|千|万|亿|〇|两]{0,}[%|％]{0,1}"
				};
		String unit_pattern = "(毫米|mm|厘米|cm|分米|dm|千米|km|米|m|平方米|m2|平方公里|km2|元|人|度|℃|立方米|升|毫升|分|分钟|秒|小时|个).*";
		String unit_regex = "(毫米|mm|厘米|cm|分米|dm|千米|km|米|m|平方米|m2|平方公里|平方千米|km2|元|人|度|℃|立方米|升|毫升|分|分钟|秒|小时|个)";
		String result = "";
		//找到数字之后，需要找单位，如果纯数字找到单位则去掉，如果数字+单位需要识别出单位
		String unit_sub = "";
		if(is_index!=-1 && is_index>=index){
			//根据词性寻找名词，需要继续修改。
			int step = is_index<index?-1:1,temp = index-1;
			while(temp>=0 && temp<seg.size() && result.equals("")){
				String word = seg.get(temp).split("_")[0];
				for(String regex : num_pattern)
					if(word.matches(regex)){
						result = word;
					}
				temp += step;
			}
			if(!result.equals("")){
				for(int i = temp+1;i<seg.size();i++)
					unit_sub += seg.get(i).split("_")[0];
			}
		}
		if(result.equals("")){
			StringBuffer sb = new StringBuffer();
			for(int i = index+1;i<seg.size();i++)
				sb.append(seg.get(i).split("_")[0]);
			String sub_sen = sb.toString();
			for(String regex : num_pattern){
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(sub_sen);
				while(m.find()&&result.equals("")){
					int start = m.start();
					int end = m.end();
					result = sub_sen.substring(start, end);
					if(end<sub_sen.length())
						unit_sub = sub_sen.substring(end);
				}
				if(!result.equals(""))
					break;
			}
		}
		//2.5倍这种是噪声，特殊处理
		if(unit_sub.startsWith("倍")) return "";
		//抽取单位
		String unit = "";
		if(!result.equals("")){
			Pattern p = Pattern.compile(unit_regex);
			Matcher m = p.matcher(unit_sub);
			while(m.find()&&unit.equals("")){
				int start = m.start();
				int end = m.end();
				unit = unit_sub.substring(start, end);
			}
		}
		//特殊处理“2009年9月经国务院批准晋升为国家级湿地自然保护区”
		if(result.equals("") && attribute.endsWith("时间")){
			StringBuffer sb = new StringBuffer();
			for(int i = 0;i<seg.size();i++)
				sb.append(seg.get(i).split("_")[0]);
			String sub_sen = sb.toString();
			result = Util.regex_match(num_pattern[0], sub_sen);
		}
		if(value_type.startsWith("数字+单位"))
			if(!unit.equals(""))
				return result+unit;
			else 
				return "";
		return result;
	}
	
	
	/**最一般的抽取，抽取单个名词
	 * index:属性词所在位置
	 * word_regex:是要NR还是NN|NR*/
	public static List<String> NRExtract(int index, List<String> seg, String word_regex){
		//处理：福建省会福州
		Set<String> temp_candidate = new HashSet<>();
		if(index+1 < seg.size() && seg.get(index+1).endsWith("_NR")){
			temp_candidate.add(seg.get(index+1).split("_")[0]);
		}
		int is_index = Util.get_is_index(seg, index);
		if(is_index!=-1){
			//根据词性寻找名词，需要继续修改。
			int step = is_index<index?-1:1,temp = index-1;
			while(temp>=0 && temp<seg.size() && !seg.get(temp).matches(word_regex))
				temp += step;
			if(temp>=0 && temp<seg.size())
				temp_candidate.add(seg.get(temp).split("_")[0]);
			//根据依存句法树
			temp_candidate.addAll(extract_by_dep(seg, is_index, step==-1?true:false));
		}
		return new ArrayList<>(temp_candidate);
	}
	
	
	/**
	 * 最一般的抽取，抽取多个词(multi)或者句子(sen)
	 * index:属性词所在位置
	 * word_regex:是要NR还是NN|NR
	 * */
	public static List<String> multiExtract(int index, List<String> seg, String word_regex, String answer_type){
		//处理：福建省会福州
		Set<String> temp_candidate = new HashSet<>();
		
		if(answer_type.equals("multi") && index+1 < seg.size() && seg.get(index+1).endsWith("_NR")){
			int sp = Util.get_next_split(seg, index, 1);
			for(int j = index+1;j<sp;j++)
				if(seg.get(j).matches(word_regex))
					temp_candidate.add(seg.get(j).split("_")[0]);
		}
		int is_index = Util.get_is_index(seg, index);
		if(is_index!=-1){
			//根据词性寻找名词，需要继续修改。
			int step = is_index<index?-1:1,temp = is_index+step;
			int sp = Util.get_next_split(seg, index, step);
			if(answer_type.equals("sen")){
				//直接截取到句尾或者句首
				StringBuffer sb = new StringBuffer();
				while(temp>=0 && temp<seg.size()){
					if(step==1)
						sb.append(seg.get(temp).split("_")[0]);
					else
						sb.insert(0, seg.get(temp).split("_")[0]);
					temp+=step;
				}
			}else{
				while(temp>=0 && temp<seg.size()){
					if(seg.get(temp).matches(word_regex))
						temp_candidate.add(seg.get(temp).split("_")[0]);
					temp += step;
				}
				//根据依存句法树
				temp_candidate.addAll(extract_by_dep(seg, is_index, step==-1?true:false));
			}
			
		}
		return new ArrayList<>(temp_candidate);
	}
	
	/**根据统计抽取最终的结果。
	 * 输入：候选list，实体(要求属性值不包含在实体中)，single（单个实体还是多个，如著名景点就有好多个）
	 * */
	public static String post_count(List<String> candidate, String entity, boolean single){
		Map<String, Integer> map = new HashMap<>();
		for(String c : candidate){
			if(entity.indexOf(c)!=-1 || c.indexOf(entity)!=-1)
				continue;
			map.put(c, map.getOrDefault(c, 0)+1);
		}
		List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue()-o1.getValue();
			}
		});
		if(single == true){
			if(list.size()>0)
				return list.get(0).getKey();
		}else if(list.size()>0){
			StringBuffer sb = new StringBuffer();
			double sum=0;
			for(int i=0;i<list.size();i++)
				sum += list.get(i).getValue();
			int avg = (int)(sum/list.size());
			int count=0;//只要前三个实体
			for(Entry<String, Integer> entry : list)
				if(entry.getValue() >= avg&&count<3){
					sb.append(entry.getKey()+"@@@");
					count++;
				}
			return sb.substring(0, sb.length()-3);
		}
		if(list.size()>0)
			return list.get(0).getKey();
		return "";
	}
	
	//作为主语：SUB VMOD NMOD(作为depNMOD(福州_1, 作为_2))
	//作为宾语：VMOD OBJ PRD
	/**输入：分词结果、谓词下标、flag==true那么就找主语，否则就是找宾语
	 * 输出：候选list词，不加词性
	 * */
	public static List<String> extract_by_dep(List<String> seg, int is_index, boolean flag){
		List<String> candidate = new ArrayList<>();
		String seg_str = Util.list_to_str(seg);
		DepTree dependencyTree = Depparser.depparse(seg_str);
		//result的String[]长度为3，分别是"head/tail"、关系、另一个节点词的索引word_tag[1].startsWith("N")
		List<String[]> result = new ArrayList<>();
		Depparser.get_node_index(dependencyTree, is_index+1, result);
		if(flag == true){
			for(String[] array : result){
				if((array[1].equals("SUB")||array[1].equals("VMOD")) && array[0].equals("head")){
					String[] word_tag = seg.get(Integer.parseInt(array[2])-1).split("_");
					if(word_tag[1].equals("NN")||word_tag[1].equals("NR"))
						candidate.add(word_tag[0]);
				}else if(array[1].equals("NMOD") && array[0].equals("tail")){
					if(Integer.parseInt(array[2])-1 < seg.size()){
						String[] word_tag = seg.get(Integer.parseInt(array[2])-1).split("_");
						if(word_tag[1].equals("NN")||word_tag[1].equals("NR"))
							candidate.add(word_tag[0]);
					}
					
				}
			}
		}else{
			for(String[] array : result){
				if((array[1].equals("OBJ")||array[1].equals("VMOD")||array[1].equals("PRD")) && array[0].equals("head")){
					String[] word_tag = seg.get(Integer.parseInt(array[2])-1).split("_");
					if(word_tag[1].equals("NN")||word_tag[1].equals("NR"))
						candidate.add(word_tag[0]);
				}
			}
		}
		return candidate;
	}
	
	//获取属性词的索引，如果词性不是名词就不要
	/**输入：分词结果、两个属性词；输出属性下标*/
	public static List<Integer> get_key_index(String trigger, String attribute, List<String> seg){
		List<Integer> indexes = new ArrayList<>();
		for(int i=0;i<seg.size();i++){
			if(seg.get(i).startsWith(trigger+"_N") || seg.get(i).startsWith(attribute+"_N"))
				indexes.add(i);
		}
		return indexes;
	}
	
	//判断当前属性值是否在一个集合(数组)当中
	public static String is_in_array(String word, String[] array){
		for(String str :array)
			if(str.contains(word)){
				return str.contains("@@")?str.split("@@")[1]:str;
			}
				
		return null;
	}
	
	//从属性值词典中抽出属性值
	public static List<String> extract_by_set(String attri, List<String> data){
		List<String> candidate = new ArrayList<>();
		String path = "data/attribute_set/"+attri+".txt";
		Set<String> value_dic = Util.read_dic(path);
		for(String sen : data){
			if(sen.split("\t", 2).length!=2){
				System.out.println(sen);
				continue;
			}
			sen = sen.split("\t", 2)[1];
			//List<String> seg = Arrays.asList(Segment.getSegment(sen).split(" "));
			for(String word : value_dic)
				if(sen.contains(word))
					candidate.add(word);
		}
		return candidate;
	}

}
