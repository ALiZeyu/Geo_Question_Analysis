package Segmentation;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Evaluate.Evaluate;
import common.MyUtil;

public class WordSegment {
	String trainFile;
	String testFile;
	String modelFile;
	String outputFile;
	String featuredic;
	String evaFile;
	int iteration;
	int dicmode;//dicmode已经不用了，现在segmode指的是要不要前处理时间地点。
	
	// 下面四个变量分别对应句子的开头、结尾、字符类型序列的开头、结尾
	public static final char bos = 'α';
	public static final char eos = 'β';
	public static final char bot = 'γ';
	public static final char eot = 'δ';
	public static final char letters[] = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
            'w', 'x', 'y', 'z', 
            'Ａ', 'Ｂ', 'Ｃ', 'Ｄ', 'Ｅ', 'Ｆ', 'Ｇ', 'Ｈ', 'Ｉ', 
            'Ｊ', 'Ｋ', 'Ｌ', 'Ｍ', 'Ｎ', 'Ｏ', 'Ｐ', 'Ｑ', 'Ｒ', 
            'Ｓ', 'Ｔ', 'Ｕ', 'Ｖ', 'Ｗ', 'Ｘ', 'Ｙ', 'Ｚ', 'ａ', 
            'ｂ', 'ｃ', 'ｄ', 'ｅ', 'ｆ', 'ｇ', 'ｈ', 'ｉ', 'ｊ', 
            'ｋ', 'ｌ', 'ｍ', 'ｎ', 'ｏ', 'ｐ', 'ｑ', 'ｒ', 'ｓ', 
            'ｔ', 'ｕ', 'ｖ', 'ｗ', 'ｘ', 'ｙ', 'ｚ'};

	public static final char numbers[] = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
			'０', '１', '２', '３', '４', '５', '６', '７', '８', '９',
			'一','二','三','四','五','六','七','八','九','零','十','百','千','万','亿','〇','两'}; 
	
	public static final char[] pnctations={'　','！','＂','＃','＄','％','＆','＇','（','）',
			'＊','＋','，','－','．','／','：','；','＜','＝',
			'＞','？','＠','［','＼','］','＾','＿','｀','｛',
			'｜','｝','～','。','、','“','”','﹃','﹄','‘','’','﹁','﹂',
			'…','【','】','《','》','〈','〉','·','[',']','(',')','.','．',','};
	public static final List<Character> letter_list=new ArrayList<Character>();
	public static final List<Character> num_list=new ArrayList<Character>();
	public static final List<Character> pn_list=new ArrayList<Character>();
	
	static{
		for(char ch:letters)
			letter_list.add(ch);
		for(char ch:numbers)
			num_list.add(ch);
		for(char ch:pnctations)
			pn_list.add(ch);
	}
	
	public WordSegment(String str){
		
	}
	
	public WordSegment() {
		Properties props = new Properties();
		try {
			InputStream in=new BufferedInputStream(new FileInputStream("seg.properties"));
			props.load(in);
			trainFile=props.getProperty("trainFile");
			testFile=props.getProperty("testFile");
			modelFile=props.getProperty("modelFile");
			outputFile=props.getProperty("outputFile");
			featuredic=props.getProperty("featuredic");
			evaFile=props.getProperty("evaFile");
			String it=props.getProperty("iteration");
			if(it!=null)
				iteration=Integer.valueOf(it);
			else
				iteration=0;
			String di=props.getProperty("dicmode");
			if(di!=null)
				dicmode=Integer.valueOf(di);
			else{
				System.out.println("can't find dicmode from seg.properties file ");
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setTestFile(String testfile){
		this.testFile=testfile;
	}

	char charType(char ch) {
		if (num_list.contains(ch))
			return 'D';
		if (letter_list.contains(ch))
			return 'L';
		if (pn_list.contains(ch))
			return 'P';
		return 'H';
	}
	
//	boolean Isadd(String str){
//		String p="[0-9\\.\\+\\-]*[0-9][年|月|日|时|万|亿|%]*";
//		if(str.matches(p))
//			return false;
//		return true;
//	}

	//从训练语料中抽出词典，作为特征之一
//	Set<String> get_lexicon(String path) {
//		Set<String> lexicon = new HashSet<String>();
//		Map<String, Integer> lexicon_map=new HashMap<>();
//		String[] temp;
//		File text = new File(path);
//		try {
//			if (path.equals(trainFile)) {
//				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(text), "utf-8"));
//				String line = reader.readLine();
//				if (line.startsWith("\uFEFF")) {
//					line = line.substring(1);
//				}
//				while (line != null) {
//					temp = line.split(" ");
//					for (String str : temp) {
//						//lexicon.add(str);
//						if(str.length()>1&&str.length()<7&&Isadd(str)){
//							if(lexicon_map.containsKey(str))
//								lexicon_map.put(str, lexicon_map.get(str)+1);
//							else
//								lexicon_map.put(str, 1);
//						}
//					}
//					line = reader.readLine();
//				}
//				reader.close();
//				
////				File outdic=new File("data/train_dic.txt");
////				if(!outdic.exists())
////					outdic.createNewFile();
////				BufferedWriter writer = new BufferedWriter(
////						new OutputStreamWriter(new FileOutputStream(outdic), "utf-8"));
//				for(Map.Entry<String, Integer> entry:lexicon_map.entrySet()){
//					//System.out.println(entry.getKey()+"  "+entry.getValue());
//					if(entry.getValue()>2)
////						writer.write(entry.getKey()+"\t"+entry.getValue()+"\n");
//						lexicon.add(entry.getKey());
//				}
////				writer.flush();
////				writer.close();
//			}else {
//				if(!text.exists()){
//					System.out.println("can't find featuredic");
//					return null;
//				}
//				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(text), "utf-8"));
//				String line = reader.readLine();
//				if (line.startsWith("\uFEFF")) {
//					line = line.substring(1);
//				}
//				while (line != null) {
//					line=line.trim();
//					if(!line.equals(""))
//						lexicon.add(line);
//					line = reader.readLine();
//				}
//				reader.close();
//			}
//			//System.out.println(lexicon.size());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return lexicon;
//	}

	List<Character> get_gold_actions(String str) {
		// 根据分好词的句子构造action序列
		List<Character> action = new ArrayList<Character>();
		String[] sentence = str.split(" ");
		int temp;
		for (int i = 0; i < sentence.length; i++) {
			temp = sentence[i].length() - 1;
			action.add('S');
			for (int j = 0; j < temp; j++)
				action.add('A');
		}
		return action;
	}

	List<String> action_to_sentence(List<Character> action, String sentence) {
		// 根据action给句子分词
		String str = "" + sentence.charAt(0);
		List<String> result = new ArrayList<String>();
		int i;
		for (i = 1; i < sentence.length() - 1; i++) {
			if (action.get(i) == 'S') {
				result.add(str);
				str = "" + sentence.charAt(i);
			} else
				str += sentence.charAt(i);
		}
		if (action.get(i) == 'S') {
			result.add(str);
			result.add("" + sentence.charAt(i));
		} else {
			str += sentence.charAt(i);
			result.add(str);
		}
		return result;
	}
	
//	//hash第二次缩减
	List<String> extract_append_features(String sentence, char[] charts, int i, State state) {
		/* 当字符对应的动作是A时所抽取的特征 */
		List<String> features = new ArrayList<String>();
		int L = sentence.length();
		char prev_ch;
		char curr_ch = sentence.charAt(i);
		char next_ch;
		char prev_cht;
		char curr_cht = charts[i];
		char next_cht;
		prev_ch = i - 1 >= 0?sentence.charAt(i - 1):bos;
		next_ch = i + 1 < L?sentence.charAt(i + 1):eos;
		prev_cht = i - 1 >= 0?charts[i - 1]:bot;
		next_cht = i + 1 < L?charts[i + 1]: eot;
		features.add("01" + prev_ch);
		features.add("02" + curr_ch);
		features.add("03" + next_ch);
		features.add("04" + prev_cht);
		features.add("05" + curr_cht);
		features.add("06" + next_cht);
		features.add("07" + prev_ch + curr_ch);
		features.add("08" + curr_ch + next_ch);
		features.add("09" + prev_cht + curr_cht);
		features.add("10" + curr_cht + next_cht);
		features.add("11" + prev_ch + curr_ch + next_ch);
		features.add("12" + prev_cht + curr_cht + next_cht);
		return features;
	}
	
	//第二次缩减
	List<String> extract_seprate_features(String sentence, char[] charts, int i, State state,Set<String> lexicon) {
		/* 当字符对应的动作是S时所抽取的特征 */
		List<String> features = new ArrayList<String>();
		int L = sentence.length();
		char prev_ch;
		char curr_ch=sentence.charAt(i);
		char next_ch;
		char prev_cht;
		char curr_cht=charts[i];
		char next_cht;
		prev_ch = i - 1 >= 0?sentence.charAt(i - 1):bos;
		next_ch = i + 1 < L?sentence.charAt(i + 1):eos;
		prev_cht = i - 1 >= 0?charts[i - 1]:bot;
		next_cht = i + 1 < L?charts[i + 1]: eot;
		
		String curr_w = sentence.substring(state.curr.index, i);
		int curr_w_len = curr_w.length();

		features.add("01" + prev_ch);
		features.add("02" + curr_ch);
		features.add("03" + next_ch);
		features.add("04" + prev_cht);
		features.add("05" + curr_cht);
		features.add("06" + next_cht);
		features.add("07" + prev_ch + curr_ch);
		features.add("08" + curr_ch + next_ch);
		features.add("09" + prev_cht + curr_cht);
		features.add("10" + curr_cht + next_cht);
		features.add("11" + prev_ch + curr_ch + next_ch);
		features.add("12" + prev_cht + curr_cht + next_cht);
		features.add("13" + curr_w);
		if (curr_w_len == 1)
			features.add("14");
		else {
			features.add("15" + sentence.charAt(state.curr.index) + " " + sentence.charAt(i - 1));
			features.add("16" + sentence.charAt(state.curr.index) + " " + curr_w_len);
			features.add("17" + sentence.charAt(i - 1) + " " + curr_w_len);
		}
		if (!state.prev.empty()) {
			String prev_w = sentence.substring(state.prev.index, state.curr.index);
			int prev_w_len = prev_w.length();
			features.add("18" + prev_w + " " + curr_w);
			features.add("19" + sentence.charAt(state.curr.index - 1) + " " + curr_w);
			features.add("20" + prev_w + " " + sentence.charAt(state.curr.index - 1));
			features.add("21" + prev_w + " " + curr_w_len);
			features.add("22" + curr_w + " " + prev_w_len);
		}
		if (i < L) {
			features.add("23" + prev_ch + curr_ch);
			features.add("24" + curr_w + " " + curr_ch);
			features.add("25" + sentence.charAt(state.curr.index) + " " + curr_ch);
		}
		//第三种词典处理模式，如果在分词过程中切出了词典中的词，那么加一条特征
		if(lexicon!=null){
			if(lexicon.contains(curr_w))
				features.add("26=curr_w_len="+curr_w_len);
//			DoubleArrayTrie adt = DoubleArrayTrie.getInstance();
//			int maxSize = adt.getMax();
//			if(curr_w.length()<=maxSize){
//				if (adt.exactMatchSearch(curr_w) >= 0) {
//					features.add("26=curr_w_len="+curr_w_len);
//				}
//			}
		}
		return features;
	}

	
	List<String> extract_features(String sentence, char[] charts, int i, State state,Set<String> lexicon) {
		/* 抽取的特征 */
		List<String> features = new ArrayList<String>();
		int L = sentence.length();
		char prev_ch;
		char curr_ch = sentence.charAt(i);
		char next_ch;
		char prev_cht;
		char curr_cht = charts[i];
		char next_cht;
		if (i == 0) {
			prev_ch = bos;
			prev_cht = bot;
		} else {
			prev_ch = sentence.charAt(i - 1);
			prev_cht = charts[i - 1];
		}
		if (i == L - 1) {
			next_ch = eos;
			next_cht = eot;
		} else {
			next_ch = sentence.charAt(i + 1);
			next_cht = charts[i + 1];
		}
		String curr_w = sentence.substring(state.curr.index, i);
		int curr_w_len = curr_w.length();

		features.add("01" + prev_ch);
		features.add("02" + curr_ch);
		features.add("03" + next_ch);
		features.add("04" + prev_cht);
		features.add("05" + curr_cht);
		features.add("06" + next_cht);
		features.add("07" + prev_ch + curr_ch);
		features.add("08" + curr_ch + next_ch);
		features.add("09" + prev_cht + curr_cht);
		features.add("10" + curr_cht + next_cht);
		features.add("11" + prev_ch + curr_ch + next_ch);
		features.add("12" + prev_cht + curr_cht + next_cht);
		features.add("13" + curr_w);
		if (curr_w_len == 1)
			features.add("14");
		else {
			features.add("15" + sentence.charAt(state.curr.index) + " " + sentence.charAt(i - 1));
			features.add("16" + sentence.charAt(state.curr.index) + " " + curr_w_len);
			features.add("17" + sentence.charAt(i - 1) + " " + curr_w_len);
		}
		if (!state.prev.empty()) {
			String prev_w = sentence.substring(state.prev.index, state.curr.index);
			int prev_w_len = prev_w.length();
			features.add("18" + prev_w + " " + curr_w);
			features.add("19" + sentence.charAt(state.curr.index - 1) + " " + curr_w);
			features.add("20" + prev_w + " " + sentence.charAt(state.curr.index - 1));
			features.add("21" + prev_w + " " + curr_w_len);
			features.add("22" + curr_w + " " + prev_w_len);
		}
		if (i < L) {
			features.add("23" + prev_ch + curr_ch);
			features.add("24" + curr_w + " " + curr_ch);
			features.add("25" + sentence.charAt(state.curr.index) + " " + curr_ch);
		}
		//第三种词典处理模式，如果在分词过程中切出了词典中的词，那么加一条特征
		if(lexicon!=null){
			if(lexicon.contains(curr_w))
				features.add("26=curr_w_len="+curr_w_len);
//			DoubleArrayTrie adt = DoubleArrayTrie.getInstance();
//			int maxSize = adt.getMax();
//			if(curr_w.length()<=maxSize){
//				if (adt.exactMatchSearch(curr_w) >= 0) {
//					features.add("26=curr_w_len="+curr_w_len);
//				}
//			}
		}
		return features;
	}

	List<String> extract_features(char action, String sentence, char[] charts, int i, State state,Set<String> lexicon) {
		if (action == 'A')
			return extract_append_features(sentence, charts, i, state);
		else
			return extract_seprate_features(sentence, charts, i, state,lexicon);
	}

	//用于老模型的得分计算，先留着吧
	double transition_score(char action, String sentence, char[] charts, int i, State state,
			Map<String, ParamList[]> params, int mode,Set<String> lexicon) {
		//TODO计算句子的得分 
		double result = 0.0;
		List<String> features = new ArrayList<String>();
		if (state.empty())
			return result;
		if (action == 'A') {
			features = extract_append_features(sentence, charts, i, state);
			for (String temp : features) {
				if (params.containsKey(temp)) {
					if (mode == 0)
						result += params.get(temp)[1].value;
					else
						result += params.get(temp)[1].avgValue;
				}
			}
		} else if (action == 'S') {
			features = extract_seprate_features(sentence, charts, i, state,lexicon);
			for (String temp : features) {
				if (params.containsKey(temp)) {
					if (mode == 0)
						result += params.get(temp)[0].value;
					else
						result += params.get(temp)[0].avgValue;
				}
			}
		}
		return result;
	}
	
	double array_transition_score(char action, String sentence, char[] charts, int i, State state,
			Map<String, ParamList[]> params,Set<String> lexicon) {
		// 计算句子的得分
		double result = 0;
		List<String> features = new ArrayList<String>();
		if (state.empty())
			return result;
		ParamList[] temp_params=null;
		if (action == 'A') {
			features = extract_append_features(sentence, charts, i, state);
			for (String temp : features) {
				temp_params=params.get(temp);
				if(temp_params!=null)
					result+=temp_params[1].avgValue;
			}
		} else if (action == 'S') {
			features = extract_seprate_features(sentence, charts, i, state,lexicon);
			for (String temp : features) {
				temp_params=params.get(temp);
				if(temp_params!=null)
					result+=temp_params[0].avgValue;
			}
		}
		return result;
	}
	
	//calculate score for arraymodel
	double[] cal_score(String sentence, char[] charts, int i, State state,Map<String, ParamList[]> params,Set<String> lexicon){
		double[] result = new double[2];
		if (state.empty())
			return result;
		double ssc=0,asc=0;
		ParamList[] temp=null;
		List<String> features = extract_features(sentence, charts, i, state,lexicon);
		for(int index=0;index<12;index++){
			temp=params.get(features.get(index));
			if(temp!=null){
				ssc+=temp[0].avgValue;
				asc+=temp[1].avgValue;
			}
		}
		for(int index=12;index<features.size();index++){
			temp=params.get(features.get(index));
			if(temp!=null)
				ssc+=temp[0].avgValue;
		}
		result[0]=ssc;
		result[1]=asc;
		return result;
	}

	List<State> backtrace_and_get_state(State state) {
		List<State> result = new ArrayList<State>();
		while (state.index > -1) {
			result.add(state);
			state = state.link;
		}
		Collections.reverse(result);
		return result;
	}

	void lazy_update(char action,Map<String, ParamList[]> params, List<String> features, double now, double scale) {
		// 更新参数，其中0：参数的总和、1：上次更新的时间、2：参数的值
		if (action == 'S') {
			for (String feature : features) {
				if (!params.containsKey(feature)) {
					ParamList[] temp=new ParamList[2];
					temp[0]=new ParamList(scale, now, scale);
					temp[1]=new ParamList();
					params.put(feature, temp);
				} else {
					double elapsed = now - params.get(feature)[0].timestamp;
					double curr_val = params.get(feature)[0].value;
					double curr_sum = params.get(feature)[0].avgValue;
					params.get(feature)[0].setValue(curr_sum + elapsed * curr_val + scale, now,curr_val + scale);
//					avgValue=;
//					params.get(feature)[0].timestamp=now;
//					params.get(feature)[0].value=;
				}
			}
		}else {
			for (String feature : features) {
				if (!params.containsKey(feature)) {
					ParamList[] temp=new ParamList[2];
					temp[1]=new ParamList(scale, now, scale);
					temp[0]=new ParamList();
					params.put(feature, temp);
				} else {
					double elapsed = now - params.get(feature)[1].timestamp;
					double curr_val = params.get(feature)[1].value;
					double curr_sum = params.get(feature)[1].avgValue;
					params.get(feature)[1].setValue(curr_sum + elapsed * curr_val + scale, now, curr_val + scale);
//					avgValue=;
//					params.get(feature)[1].timestamp=now;
//					params.get(feature)[1].value=;
				}
			}
		}
		
	}
	
	//每次迭代后更新参数
	void flush(Map<String, ParamList[]> params, double now) {
		Iterator<Map.Entry<String, ParamList[]>> it=params.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, ParamList[]> entry=it.next();
			ParamList[] value=entry.getValue();
			for(ParamList p:value){
				double elapsed = now - p.timestamp;
				double curr_val = p.value;
				double curr_sum = p.avgValue;
				p.avgValue=curr_sum + elapsed * curr_val;
				p.timestamp=now;
			}
			
		}
	}
	
	void insert_agenda(List<State> agenda, State state){
		int i,size=agenda.size();
		for(i=0;i<size;i++)
			if(state.score < agenda.get(i).score){
				agenda.add(i, state);
				break;
			}
		if(i==size){
			agenda.add(size, state);
		}
	}
	//插入排序修改
	void update_agenda(List<State> agenda, State state, int size) {
		// 更新列表，仅存储前size条记录
		if(agenda.size()==size&&state.score<agenda.get(0).score)
			return;
		insert_agenda(agenda, state);
		if(agenda.size()>size)
			agenda.remove(0);
	}
	
	/**
	 * @params sentence 句子 
	 * size agenda容量
	 * params 模型
	 * actions 分词label
	 * now lazyupdate使用
	 * mode 0训练 1测试
	 * 2016-8-31 modify
	  */
	List<String> beam_search(String sentence, int size, Map<String,ParamList[]> params,
			List<Character> actions, double now, int mode, Set<String> lexicon) {
		
		List<String> result = new ArrayList<String>();
		List<String> correct_features = new ArrayList<String>();
		List<String> predict_features = new ArrayList<String>();
		List<State> correct_path = new ArrayList<State>();
		List<State> predict_state_path = new ArrayList<State>();
		char[] str = sentence.toCharArray();
		int L = sentence.length();
		//初始化类型序列
		char[] charts = new char[L];
		for (int i = 0; i < L; i++) {
			charts[i] = charType(str[i]);
		}
		
		State head = new State(0, -1, null, 'n');
		State correct_state=head;
		State predict_state = new State();
		
		Rule rule = new Rule(sentence,"",0,0,null);

		List<State> agenda = new ArrayList<State>();
		List<State> temp_agenda = new ArrayList<State>();
		temp_agenda.add(head);

		for (int i = 0; i < L; i++) {
			agenda.clear();
			//生成新的候选集
			for (State state : temp_agenda) {
				if (i > 0 && rule.can_append(i)) {
					double score = transition_score('A', sentence, charts, i, state, params, mode,lexicon);
					update_agenda(agenda, new State(state.score + score, i, state, 'A'), size);
				}
				if (rule.can_sepreate(i)) {
					double score = transition_score('S', sentence, charts, i, state, params, mode,lexicon);
					update_agenda(agenda, new State(state.score + score, i, state, 'S'), size);
				}
			}
						
			if (mode == 0) {
				boolean in_agenda = false;
				for (State state : agenda) {
					if(state.link==correct_state&&state.action==actions.get(i)){
						in_agenda = true;
						correct_state=state;
						correct_path=backtrace_and_get_state(state);
						break;
					}
				}
				if (!in_agenda) {
					predict_state = agenda.get(agenda.size() - 1);
					predict_state_path = backtrace_and_get_state(predict_state);
					State c=new State(0,i,correct_state,actions.get(i));
					agenda.add(c);
					correct_path=backtrace_and_get_state(c);
					if (predict_state_path.size() != correct_path.size()) {
						System.out.println("len wrong at" + sentence.charAt(i));
					}
					for (int j = 0; j < predict_state_path.size() - 1; j++) {
						correct_features = extract_features(correct_path.get(j + 1).action, sentence, charts, j + 1,
								correct_path.get(j),lexicon);
						predict_features = extract_features(predict_state_path.get(j + 1).action, sentence, charts,
								j + 1, predict_state_path.get(j),lexicon);
						lazy_update(correct_path.get(j + 1).action,params, correct_features, now, 1.0);
						lazy_update(predict_state_path.get(j + 1).action,params, predict_features, now, -1.0);
					}
					return null;
				}
			}
			temp_agenda.clear();
			temp_agenda.addAll(agenda);
		}
		
		if (mode == 0) {
			predict_state = agenda.get(agenda.size() - 1);
			predict_state_path = backtrace_and_get_state(predict_state);

			List<Character> predict_action = new ArrayList<Character>();
			for (int i = 0; i < predict_state_path.size(); i++)
				predict_action.add(predict_state_path.get(i).action);
			result = action_to_sentence(predict_action, sentence);

			correct_path=backtrace_and_get_state(correct_state);
			assert(predict_state_path.size() == correct_path.size());
			for (int j = 0; j < predict_state_path.size() - 1; j++) {
				correct_features = extract_features(correct_path.get(j + 1).action, sentence, charts, j + 1,
						correct_path.get(j),lexicon);
				predict_features = extract_features(predict_state_path.get(j + 1).action, sentence, charts, j + 1,
						predict_state_path.get(j),lexicon);
				lazy_update(correct_path.get(j + 1).action,params, correct_features, now, 1.0);
				lazy_update(predict_state_path.get(j + 1).action,params, predict_features, now, -1.0);
			}
		} else {
			predict_state = agenda.get(agenda.size() - 1);
			predict_state_path = backtrace_and_get_state(predict_state);
			List<Character> predict_action = new ArrayList<Character>();
			for (int i = 0; i < predict_state_path.size(); i++)
				predict_action.add(predict_state_path.get(i).action);
			result=action_to_sentence(predict_action, sentence);
			// 用户词典后处理
//			if (dicmode == 1) {
//				List<String> temp=new ArrayList<>();
//				temp.addAll(result);
//				result.clear();
//				String word;
//				DoubleArrayTrie adt = DoubleArrayTrie.getInstance();
//				int maxSize = adt.getMax();
//				//List<String> temp = action_to_sentence(predict_action, sentence);
//
//				for (int i = 0; i < temp.size(); i++) {
//					word = temp.get(i);
//					int j = i;
//					while (j + 1 < temp.size() && word.length() + temp.get(j + 1).length() <= maxSize)
//						word += temp.get(++j);
//					while (true) {
//						if (adt.exactMatchSearch(word) >= 0 || word.equals(temp.get(i))) {
//							result.add(word);
//							i = j;
//							break;
//						} else {
//							word = word.substring(0, word.length() - temp.get(j).length());
//							j--;
//						}
//					}
//				}
//			}
		}		
		return result;
	}
	/**
	 * prefix 词典前缀
	 * sentence 待分词的句子
	 * size agenda容量
	 * params 模型
	 * segmode 0训练 1前处理时考虑时间地点
	 * 2016-8-31 modify
	 * split 题干和选项分开的位置
	 * words 图标注中的地点
	 * */
	List<String> array_beam_search(String prefix,String sentence, int size,  Map<String, ParamList[]> params,
			Set<String> lexicon,int segmode,int split, List<String> words) {
		
		List<String> result = new ArrayList<String>();
		List<State> predict_state_path = new ArrayList<State>();
		char[] str = sentence.toCharArray();
		int L = sentence.length();
		//初始化类型序列
		char[] charts = new char[L];
		for (int i = 0; i < L; i++) {
			charts[i] = charType(str[i]);
		}
		
		State head = new State(0, -1, null, 'n');
		State predict_state = new State();
		
		Rule rule = new Rule(sentence,prefix,segmode,split,words);

		List<State> agenda = new ArrayList<State>();
		List<State> temp_agenda = new ArrayList<State>();
		temp_agenda.add(head);

		for (int i = 0; i < L; i++) {
			agenda.clear();
			//生成新的候选集
			for (State state : temp_agenda) {
				if(rule.can_append(i)&&rule.can_sepreate(i)){
					double[] fea_score=cal_score(sentence, charts, i, state, params,lexicon);
					update_agenda(agenda, new State(state.score + fea_score[0], i, state, 'S'), size);
					update_agenda(agenda, new State(state.score + fea_score[1], i, state, 'A'), size);
				}else{
					if (rule.can_append(i)) {
					double score = array_transition_score('A', sentence, charts, i, state, params,lexicon);
					update_agenda(agenda, new State(state.score + score, i, state, 'A'), size);
					}
					if (rule.can_sepreate(i)) {
						double score = array_transition_score('S', sentence, charts, i, state, params,lexicon);
						update_agenda(agenda, new State(state.score + score, i, state, 'S'), size);
	
					}
				}
				
			}
			temp_agenda.clear();
			temp_agenda.addAll(agenda);
		}
		
		predict_state = agenda.get(agenda.size() - 1);
		predict_state_path = backtrace_and_get_state(predict_state);
		List<Character> predict_action = new ArrayList<Character>();
		for (int i = 0; i < predict_state_path.size(); i++)
			predict_action.add(predict_state_path.get(i).action);
		//利用规则后处理分词结果
		String[] pattern_str = { 
				"[东|西|南|北][距|邻|临|迁|起|至|达|濒]"
				};
		for (int j = 0; j < pattern_str.length; j++) {
			Pattern p = Pattern.compile(pattern_str[j]);
			Matcher m = p.matcher(sentence);
			while (m.find()) {
				int start = m.start();
				int end = m.end();
				if(end-start==2)
					if(predict_action.get(start)=='S'&&predict_action.get(start+1)=='S'&&(end>sentence.length()-1||predict_action.get(end)=='S'))
						predict_action.set(start+1, 'A');
			}
		}
		result = action_to_sentence(predict_action, sentence);
		
		// 用户词典后处理
		List<String> temp = new ArrayList<>();
		temp.addAll(result);
		result.clear();
		String word;
		DoubleArrayTrie adt = DoubleArrayTrie.getInstance(prefix+"dic/post.dic");
		int maxSize = adt.getMax();
		// List<String> temp = action_to_sentence(predict_action, sentence);
		int len=0;
		for (int i = 0; i < temp.size(); i++) {
			word = temp.get(i);
			int j = i;
			while (j + 1 < temp.size() && word.length() + temp.get(j + 1).length() <= maxSize)
				word += temp.get(++j);
			while (true) {
				if (adt.exactMatchSearch(word) >= 0 || word.equals(temp.get(i))) {
					if(len<split && len+word.length()>split && !word.equals(temp.get(i))){
						word = word.substring(0, word.length() - temp.get(j).length());
						j--;
					}else{
						len+=word.length();
					result.add(word);
					i = j;
					break;
					}
					
				} else {
					word = word.substring(0, word.length() - temp.get(j).length());
					j--;
				}
			}
		}
		
		if(segmode == 1){
			for(int i=0;i<result.size();i++){
				if(rule.timeList.contains(result.get(i))){
					String tempw=result.get(i)+"_time";
					result.set(i, tempw);
				}
				else if(rule.locList.contains(result.get(i))){
					String tempw=result.get(i)+"_loc";
					result.set(i, tempw);
				}
			}
		}
		return result;
	}

	/**
	 * sentence 待分词的句子
	 * size agenda容量
	 * params 模型*/
	List<String> array_beam_search(String sentence, int size,  Map<String, ParamList[]> params,
			Set<String> lexicon) {
		
		List<String> result = new ArrayList<String>();
		List<State> predict_state_path = new ArrayList<State>();
		char[] str = sentence.toCharArray();
		int L = sentence.length();
		//初始化类型序列
		char[] charts = new char[L];
		for (int i = 0; i < L; i++) {
			charts[i] = charType(str[i]);
		}
		
		State head = new State(0, -1, null, 'n');
		State predict_state = new State();
		
		Rule rule = new Rule(sentence,"",0,0,null);

		List<State> agenda = new ArrayList<State>();
		List<State> temp_agenda = new ArrayList<State>();
		temp_agenda.add(head);

		for (int i = 0; i < L; i++) {
			agenda.clear();
			//生成新的候选集
			for (State state : temp_agenda) {
				if(rule.can_append(i)&&rule.can_sepreate(i)){
					double[] fea_score=cal_score(sentence, charts, i, state, params,lexicon);
					update_agenda(agenda, new State(state.score + fea_score[0], i, state, 'S'), size);
					update_agenda(agenda, new State(state.score + fea_score[1], i, state, 'A'), size);
				}else{
					if (rule.can_append(i)) {
					double score = array_transition_score('A', sentence, charts, i, state, params,lexicon);
					update_agenda(agenda, new State(state.score + score, i, state, 'A'), size);
					}
					if (rule.can_sepreate(i)) {
						double score = array_transition_score('S', sentence, charts, i, state, params,lexicon);
						update_agenda(agenda, new State(state.score + score, i, state, 'S'), size);
	
					}
				}
				
			}
			temp_agenda.clear();
			temp_agenda.addAll(agenda);
		}
		
		predict_state = agenda.get(agenda.size() - 1);
		predict_state_path = backtrace_and_get_state(predict_state);
		List<Character> predict_action = new ArrayList<Character>();
		for (int i = 0; i < predict_state_path.size(); i++)
			predict_action.add(predict_state_path.get(i).action);
		result = action_to_sentence(predict_action, sentence);
		// 用户词典后处理
	//	if (dicmode == 1) {
			List<String> temp = new ArrayList<>();
			temp.addAll(result);
			result.clear();
			String word;
			DoubleArrayTrie adt = DoubleArrayTrie.getInstance("dic/post.dic");
			int maxSize = adt.getMax();
			// List<String> temp = action_to_sentence(predict_action, sentence);

			for (int i = 0; i < temp.size(); i++) {
				word = temp.get(i);
				int j = i;
				while (j + 1 < temp.size() && word.length() + temp.get(j + 1).length() <= maxSize)
					word += temp.get(++j);
				while (true) {
					if (adt.exactMatchSearch(word) >= 0 || word.equals(temp.get(i))) {
						result.add(word);
						i = j;
						break;
					} else {
						word = word.substring(0, word.length() - temp.get(j).length());
						j--;
					}
				}
			}
		//}
		return result;
	}

	//2016-8-31
	public void train() {
		int agenda_size = 16, mode = 0;
		Map<String, ParamList[]> params = new HashMap<String, ParamList[]>();
		String sentence;
		int now=0;
		List<Character> actions = new ArrayList<Character>();

		if(trainFile==null){
			System.out.println("can't find traning data from seg.properties file");
			return;
		}
		File file = new File(trainFile);
		if(!file.exists()){
			System.out.println("can't find segment traning data");
			return;
		}
		//从语料中添加词典特征
		//Set<String> lexicon=get_lexicon(trainFile);
		Set<String> lexicon=null;
//		if(dicmode==2){
//			lexicon=get_lexicon(trainFile);
//		}
		List<String> data = MyUtil.read_file(trainFile);
		int size = data.size();
		String temp,time;
		DateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
		
		if(iteration==0){
			System.out.println("the value of itration is 0,or can't find it from seg.properties file ");
			return;
		}
		
		for (int j = 0; j < iteration; j++) {
			if(j>0)
				Collections.shuffle(data);
			time = format.format(new Date());
			System.out.println(time+" segment training itration "+(j+1)+" begin");
			//now = j * size + 1;
			for (int i = 0; i < size; i++) {
				temp = data.get(i);
				String[] ttt=temp.split(" ");
				if(ttt.length>80)
					continue;
				sentence = temp.replace(" ", "");
				//只有一个字符无法抽取三元特征，直接跳过。
				if(sentence.length()<2)
					continue;
				actions = get_gold_actions(temp);
				now = j * size + i;
				beam_search(sentence, agenda_size, params, actions, now, mode,lexicon);
			}
			flush(params, now);
			time = format.format(new Date());
			System.out.println(time+" segment training itration "+(j+1)+" end");
		}
		
		double s_value=0.0,a_value=0.0;
		try {
			if(modelFile==null){
				System.out.println("can't find model file from seg.properties file");
				return;
			}
			File model_file=new File(modelFile);
			if(!model_file.exists())
				model_file.createNewFile();
			
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(model_file), "utf-8"));
			for (Map.Entry<String, ParamList[]> entry : params.entrySet()) {
				s_value=entry.getValue()[0].avgValue/(data.size() * iteration);
				a_value=entry.getValue()[1].avgValue/(data.size() * iteration);
				//final_value=entry.getValue().get(0);
				if(a_value==0 && s_value==0)
					continue;
				writer.write(entry.getKey() + '\t' + s_value + '\t'+a_value+'\n');
			}
			writer.flush();
			writer.close();
			
			time = format.format(new Date());
			System.out.println(time+" the model has been written into segment model file");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void seg() {
		int agenda_size = 16,mode=1;
		List<String> test_data = new ArrayList<String>();
		DateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
		String time;
		
		//Set<String> lexicon=null;
		//if(dicmode==2){
		if(featuredic==null){
			System.out.println("can't find featuredic from seg.properties file");
			return;
		}
//		time = format.format(new Date());
//		System.out.println(time+" 加载特征词典");
		
		//Set<String> lexicon=get_lexicon(featuredic);
		Set<String> lexicon=null;
		
//		time = format.format(new Date());
//		System.out.println(time+" 特征词典加载完毕");
		//}

		if(modelFile==null){
			System.out.println("can't find model file from seg.properties file");
			return;
		}
		if(testFile==null){
			System.out.println("can't find test file from seg.properties file");
			return;
		}
		if(outputFile==null){
			System.out.println("can't find output file from seg.properties file");
			return;
		}
		File file = new File(modelFile);
		File test_path = new File(testFile);
		File output = new File(outputFile);
		
		try {
			if(!file.exists()){
				System.out.println("can't find segment model file");
				return;
			}
			if(!test_path.exists()){
				System.out.println("can't find segment test file");
				return;
			}
			if(!output.exists())
				output.createNewFile();
			
			time = format.format(new Date());
			System.out.println(time+" loading segment model file.........");

//array model begin
			Map<String, ParamList[]> params = new HashMap<String, ParamList[]>();
			String[] temp;
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			String line = reader.readLine();
			if (line.startsWith("\uFEFF")) {
				line = line.substring(1);
			}
			while (line != null) {
				temp = line.split("\t");
				ParamList[] value=new ParamList[2];
				value[0]=new ParamList(Double.parseDouble(temp[1]));
				value[1]=new ParamList(Double.parseDouble(temp[2]));
//				value[0].avgValue=);
//				value[1].avgValue=Double.parseDouble(temp[2]);
				params.put(temp[0], value);
				line = reader.readLine();
			}
			reader.close();
//array model end
			
			time = format.format(new Date());
			System.out.println(time+" segment model file has been loaded");
			
			BufferedReader test_reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(test_path), "utf-8"));
			line = test_reader.readLine();
			if (line.startsWith("\uFEFF")) {
				line = line.substring(1);
			}
			while (line != null) {
			//	line=BCConvert.qj2bj(line);
				line=line.trim();
				if(line.equals("")){
					line=test_reader.readLine();
					continue;
				}
				test_data.add(line);
				line = test_reader.readLine();
			}
			test_reader.close();

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "utf-8"));
			List<String> result = new ArrayList<String>();
			for (int i = 0; i < test_data.size(); i++) {
				//如果只有一个字符，直接输出即可
				if(test_data.get(i).length()<2){
					result.clear();
					result.add(test_data.get(i));
				}
				else
					result=array_beam_search(test_data.get(i), agenda_size, params, lexicon);
				for (int j = 0; j < result.size() - 1; j++){
					writer.write(result.get(j) + " ");
//					//System.out.print(result.get(j) + " ");
				}
				writer.write(result.get(result.size() - 1));
				writer.write("\n");
				
//				System.out.print(result.get(result.size() - 1));
//				System.out.println();
			}
			writer.flush();
			writer.close();
			time = format.format(new Date());
			System.out.println(time+" the result has been written into segment output file");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Evaluate.calculate(outputFile, evaFile);
	}
	
	/**模型之前已经加载好了，每次对一句话进行分词
	 * split 题干与选项分开*/
	public List<String> geo_seg(String prefix, Map<String, ParamList[]> params, String str, int segmode, int split, List<String> words) {
		int agenda_size = 16,mode=1;
		
//		Set<String> lexicon=get_lexicon(featuredic);
		Set<String> lexicon=null;
		List<String> final_result=new ArrayList<>();

		if (str.length() < 2) {
			final_result.add(str);
		} else
			final_result=array_beam_search(prefix, str, agenda_size, params, lexicon, segmode, split, words); 
		return final_result;
	}
}
