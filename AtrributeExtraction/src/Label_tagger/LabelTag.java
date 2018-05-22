package Label_tagger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.print.DocFlavor.STRING;

import Evaluate.Evaluate;
import common.MyUtil;

public class LabelTag {
	String trainFile;
	String testFile;
	String modelFile;
	String outputFile;
	String evaFile;
	Set<String> entity_dic;
	Set<String> attri_dic;
	int iteration;
	
	//构造函数就是加载property文件
	public LabelTag(){
//		Properties props = new Properties();
//		try {
//			InputStream in=new BufferedInputStream(new FileInputStream("tag.properties"));
//			props.load(in);
//			trainFile=props.getProperty("trainFile");
//			testFile=props.getProperty("testFile");
//			evaFile=props.getProperty("evaFile");
//			modelFile=props.getProperty("modelFile");
//			outputFile=props.getProperty("outputFile");
//			String it=props.getProperty("iteration");
//			if(it!=null)
//				iteration=Integer.valueOf(it);
//			else
//				iteration=0;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		this.trainFile = "data/all_data/train.txt";
		this.testFile = "data/all_data/test.txt";
		this.evaFile = "data/all_data/test.txt";
		this.modelFile = "data/all_data/model.txt";
		this.outputFile = "data/all_data/output.txt";
		this.attri_dic = MyUtil.read_set("data/attri_dic.txt");
		//this.attri_dic = new HashSet<>();
		this.entity_dic = new HashSet<>();
		//this.entity_dic = MyUtil.read_set("data/entity_dic.txt");
		this.iteration = 20;
	}
	
	
	public LabelTag(String str){
		
	}
	
	public void setFile(String testfile,String outputfile){
		this.testFile=testfile;
		this.outputFile=outputfile;
	}
	
	public void train(){
		if(trainFile==null){
			System.out.println("can't find training data from tag.properties file");
			return;
		}
		File train_file=new File(trainFile);
		if(!train_file.exists()){
			System.out.println("can't find postag training data");
			return;
		}
		//读取训练数据
		List<Instance> instances=new ArrayList<Instance>();
		List<String> training_data=MyUtil.read_file(trainFile);
		for(String line:training_data){
			Instance instance=Data.read_instance(line, "train");
			instances.add(instance);
		}

		//提取特征和词性表
		Map<String, Integer> feature_alphabet=new HashMap<String, Integer>();
		List<String> tags=new ArrayList<String>();
		//抽取所有的特征并构造特征表
		build_feature_space(instances, feature_alphabet, tags);
		Map<String, Integer> tags_alphabet=new HashMap<String, Integer>();
		for(int i=0;i<tags.size();i++)
			tags_alphabet.put(tags.get(i), i);
		
		//System.out.println(tags);
//		for(Map.Entry<String, Integer> entry:tags_alphabet.entrySet())
//			System.out.print(entry.getKey()+", ");
		int T=tags_alphabet.size(),L=feature_alphabet.size();
		double[] W=new double[(T+L)*T];
		double[] Wsum=new double[(T+L)*T];
		int[] Wtime=new int[(T+L)*T];
		
		Model model=new Model(tags_alphabet,tags, feature_alphabet, W, Wsum);
		
		int N=instances.size();
		DateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
		String time;
		if(iteration==0){
			System.out.println("the value of itration is 0,or can't find it from tag.properties file ");
			return;
		}
		for(int it=0;it<iteration;it++){
			time= format.format(new Date());
			System.out.println(time+" postag training itration "+(it+1)+" begin");
			//迭代次数
			for(int i=0;i<instances.size();i++){
				Instance instance=instances.get(i);
				List<String> predict=viterbi(instance, model, false);
				List<String> reference=instance.labels;
				int now=it*N+i+1;
				//更新特征参数
				for(int j=0;j<predict.size();j++){
					if(!predict.get(j).equals(reference.get(j))){
						int predict_tag_index=tags_alphabet.get(predict.get(j));
						int reference_tag_index=tags_alphabet.get(reference.get(j));
						List<Integer> p_indices=new ArrayList<Integer>();
						List<Integer> r_indices=new ArrayList<Integer>();
						for(Integer index:instance.indices.get(j)){
							p_indices.add(T*T+T*index+predict_tag_index);
							r_indices.add(T*T+T*index+reference_tag_index);
						}
						lazy_update(W, Wsum, Wtime, p_indices, now, -1.0);
						lazy_update(W, Wsum, Wtime, r_indices, now, 1.0);
					}
				}
				//更新tag转换矩阵
				for(int j=0;j<predict.size()-1;j++){
					if(!predict.get(j).equals(reference.get(j))||!predict.get(j+1).equals(reference.get(j+1))){
						List<Integer> p_indices=new ArrayList<>();
						List<Integer> r_indices=new ArrayList<>();
						int p_index=tags_alphabet.get(predict.get(j))*T+tags_alphabet.get(predict.get(j+1));
						int r_index=tags_alphabet.get(reference.get(j))*T+tags_alphabet.get(reference.get(j+1));
						p_indices.add(p_index);
						r_indices.add(r_index);
						lazy_update(W, Wsum, Wtime, p_indices, now, -1.0);
						lazy_update(W, Wsum, Wtime, r_indices, now, 1.0);
					}
				}
			}
			time = format.format(new Date());
			System.out.println(time+" postag training itration "+(it+1)+" end");
			//evaluate(model);
		}
		save_model(model);
		time= format.format(new Date());
		System.out.println("the postag model has been written into model file");
	}
	
	void lazy_update(double[] W,double[] Wsum,int[] Wtime,List<Integer> indices,int now,double scale){
		for(Integer index:indices){
			int elapsed=now-Wtime[index];
			double val=W[index];
			W[index]=val+scale;
			Wsum[index]+=elapsed*val+scale;
			Wtime[index]=now;
		}
	}
	/**
	 * @param feature_alphabet	所有的特征及其对应的编号
	 * @param tags	从中抽出的词性*/
	void build_feature_space(List<Instance> instances,Map<String, Integer> feature_alphabet,List<String> tags){
		Set<String> tags_alphabet=new HashSet<String>();
		for(Instance instance:instances){
			List<List<Integer>> indices=new ArrayList<List<Integer>>();//特征在特征表中的位置
			int len=instance.words.size();
			for(int i=0;i<len;i++){
				List<Integer> indice=new ArrayList<Integer>();
				List<String> local_feature=extract_local_features(instance, i);
				for(String str:local_feature){
					if(!feature_alphabet.containsKey(str))
						feature_alphabet.put(str, feature_alphabet.size());
					indice.add(feature_alphabet.get(str));
				}
				indices.add(indice);
			}
			instance.setIndices(indices);
			tags_alphabet.addAll(instance.labels);
		}
		tags.addAll(tags_alphabet);
	}
	
	//特征构造函数
	List<String> extract_local_features(Instance instance, int index) {
		List<String> features = new ArrayList<String>();
		List<String> words = instance.words;
		List<String> postags = instance.postags;
		String w_2, w_1, w0, w1, w2;
		String p_2, p_1, p0, p1, p2;
		int length;
		w_2 = index - 2 >= 0 ? words.get(index - 2) : "bos";
		w_1 = index - 1 >= 0 ? words.get(index - 1) : "bos";
		w0 = words.get(index);
		w1 = index + 1 < words.size() ? words.get(index + 1) : "eos";
		w2 = index + 2 < words.size() ? words.get(index + 2) : "eos";
		length = w0.length() > 5 ? 5 : w0.length();
		
		p_2 = index - 2 >= 0 ? postags.get(index - 2) : "bos";
		p_1 = index - 1 >= 0 ? postags.get(index - 1) : "bos";
		p0 = postags.get(index);
		p1 = index + 1 < postags.size() ? postags.get(index + 1) : "eos";
		p2 = index + 2 < postags.size() ? postags.get(index + 2) : "eos";
		
		//if(is_word_match(w0)) features.add("match");
		if(attri_dic.contains(w0)) features.add("attri");
		if(entity_dic.contains(w0)) features.add("entity");
		features.add("length=" + length);
		features.add("w[-2]=" + w_2);
		features.add("w[-1]=" + w_1);
		features.add("w[0]=" + w0);
		features.add("w[1]=" + w1);
		features.add("w[2]=" + w2);
		features.add("w[-1][0]=" + w_1 + "," + w0);
		features.add("w[0][1]=" + w0 + "," + w1);
		features.add("w[-1][1]=" + w_1 + "," + w1);
		
		features.add("p[-2]=" + p_2);
		features.add("p[-1]=" + p_1);
		features.add("p[0]=" + p0);
		features.add("p[1]=" + p1);
		features.add("p[2]=" + p2);
		features.add("p[-1][0]=" + p_1 + "," + p0);
		features.add("p[0][1]=" + p0 + "," + p1);
		features.add("p[-1][1]=" + p_1 + "," + p1);
		
		features.add("wordtype="+getWordType(w0));
		List<Integer> trigger = instance.index;
		if(trigger.get(0)==-1)
			features.add("dis_cs" + index);
		else {
			if(trigger.size()==1)
				features.add("dis_bj" + (index-trigger.get(0)));
			else{
				int d1 = index-trigger.get(0);
				int d2 = index-trigger.get(1);
				int d = Math.abs(d1) < Math.abs(d2)?d1:d2;
				features.add("dis_bj" + d);
			}
		}

//		int l = length > 3 ? 3 : length;
//		for (int j = 1; j < l + 1; j++) {
//			features.add("pre[" + j + "]=" + w0.substring(0, j));
//			features.add("suf[" + j + "]=" + w0.substring(j-1, w0.length()));
//		}
		return features;
	}
	boolean is_word_match(String word){
		String[] pattern_str = {
				"①|②|③|④|⑤",
				"[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]]{1,2}(类|图|国|国家|河流|河|市|县|镇|村|点|线|地|区域|区|阶段)",
				"[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]][城省处]",
				"(洋流|气流|环节)[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]]",
				};
		for(String pattern : pattern_str)
			if(word.matches(pattern))
				return true;
		return false;
	}
	
	char getWordType(String word){
		if(word.length()==1&&CharType.punctSet.contains(word.charAt(0)))
			return 'p';
		if(IsLetter(word))
			return 'l';
		if(IsNum(word))
			return 'n';
		if(word.endsWith("年")||word.endsWith("月")||word.endsWith("日")){
			word=word.substring(0,word.length()-1);
			if(IsNum(word))
				return 't';
		}
		return 'o';
	}
	
	boolean IsNum(String str){
		for(int i=0;i<str.length();i++){
			if(!CharType.digitSet.contains(str.charAt(i)))
				return false;
		}
		return true;
	}
	
	boolean IsLetter(String str){
		for(int i=0;i<str.length();i++){
			if(!CharType.letterSet.contains(str.charAt(i)))
				return false;
		}
		return true;
	}
	/***/
	public void tagging(List<Double> eva_result){
		if(testFile==null){
				System.out.println("can't find testing data from tag.properties file");
				return;
			}
		File test_file=new File(testFile);
		if(!test_file.exists()){
			System.out.println("can't find postag testing data");
			return;
		}
		List<Instance> instances=new ArrayList<Instance>();
		List<String> test_data = MyUtil.read_file(testFile);
		for(String line : test_data){
			Instance instance=Data.read_instance(line, "test");
			instances.add(instance);
		}
		
		DateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
		String time=format.format(new Date());
		System.out.println(time+" loading postag model......");
		
		Model model=read_model();
		
		time=format.format(new Date());
		System.out.println(time+" postag model has been loaded");
		if(model==null){
			System.out.println("the postag model file is empty");
			return;
		}
		
		for(Instance instance:instances)
			build_instance(instance, model);
		for(Instance instance:instances){
			List<String> p=viterbi(instance, model, false);
			instance.predict_tags=p;
		}
		
		//evaluate(model);
		
		String URL_pattern="(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]*[-A-Za-z0-9+&@#/%=~_|]";
		
		try {
			if(outputFile==null){
				System.out.println("can't find output file from tag.properties file");
				return;
			}
			File dest = new File(outputFile);  
			if(!dest.exists()){
			   dest.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(outputFile); 
	        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			for(Instance instance:instances){
				for(int i=0;i<instance.words.size();i++){
//					if(get_post_tag("",instance.words.get(i),null)!=null)
//						osw.write(instance.words.get(i)+get_post_tag("", instance.words.get(i),null)+" ");
//					else
						osw.write(instance.words.get(i)+"_"+instance.postags.get(i)+"_"+instance.predict_tags.get(i)+" ");
				}
				osw.write("\n");
			}
			osw.flush();
			osw.close();
			time = format.format(new Date());
			System.out.println(time+" the result has been written into segment output file");
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Double> tem = Evaluate.eva_en_ar(testFile, outputFile);
		eva_result.addAll(tem);
	}
	/**设定词的词性后处理*/
	String get_post_tag(String prefix, String str, List<String> words){
		String[] patternNN={
				"[A-Za-z0-9\\.\\+\\_\\/]*[A-Za-z0-9](分钟)",
				"([图|表])([\\d+|甲|乙|丙|丁])([\\（|\\(][a|b|c|d|A|B|C|D][\\）|\\)])*",
				"[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]]{1,2}(类|图|国|河流|河|市|县|镇|村|点|线|地|区域|区|阶段)",
				"[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]][城省处]",
				"(洋流|气流|环节)[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]]",
				"(\\d|\\.)+°[ewsnEWSN]?((\\d|\\.)+′[ewsnEWSN]?)?",
				"1[:|︰|：]\\d{3,10}",
				"[A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|Ａ|Ｂ|Ｃ|Ｄ|Ｅ|Ｆ|Ｇ|Ｈ|Ｉ|Ｊ|Ｋ|Ｌ|Ｍ|Ｎ|Ｏ|Ｐ|Ｑ|Ｒ|Ｓ|Ｔ|Ｕ|Ｖ|Ｗ|Ｘ|Ｙ|Ｚ|ａ|ｂ|ｃ|ｄ|ｅ|ｆ|ｇ|ｈ|ｉ|ｊ|ｋ|ｌ|ｍ|ｎ|ｏ|ｐ|ｑ|ｒ|ｓ|ｔ|ｕ|ｖ|ｗ|ｘ|ｙ|ｚ]*"};
		String[] patternNT={"([0-9]{0,3})[:|：]([0-9]{0,3})",
				"[0|1|2|3|4|5|6|7|8|9|０|１|２|３|４|５|６|７|８|９|一|二|三|四|五|六|七|八|九|零|十|百|千|万|亿|〇|两]+[世|年|点|月|日|时|分|秒][纪|份|代]{0,1}"};
		String[] patternCD={"[0|1|2|3|4|5|6|7|8|9|０|１|２|３|４|５|６|７|８|９|一|二|三|四|五|六|七|八|九|零|十|百|千|万|亿|〇|两]+[万|亿|%|％|℃]{0,1}"};
		String[] patternVV={"[东|西|南|北][距|邻|临|迁|起|至|达|濒]"};
		String[] sep_PU={"[","]","【","】",";","；","@","#","~","(",")"};
		String[] patternVA={"([昼|夜|东|西|南|北|中|上|下|左|右|风|雨|雾|雪|人|浪|春|夏|秋|冬])([冷|热|长|短|高|低|多|少|大|小])([昼|夜|东|西|南|北|中|上|下|左|右|风|雨|雾|雪|浪|春|夏|秋|冬])([冷|热|长|短|高|低|多|少|大|小|急])"};
		Set<String> sep_pu=new HashSet<>();
		for(String c:sep_PU)
			sep_pu.add(c);
		String[] sep_NN={"甲","乙","丙","丁","戊","①","②","③","④","⑤"};
		Set<String> sep_nn=new HashSet<>();
		for(String c:sep_NN)
			sep_nn.add(c);
		DoubleArrayTriePos adt = DoubleArrayTriePos.getInstance(prefix);
		if(words!=null&&words.size()>0){
			if(words.contains(str))
				return "_NR";
		}
		
		if(adt.exactMatchSearch(str) >= 0) 
			return adt.getDicPos(str);
		
		for(String p:patternNN){
			if(str.matches(p))
				return "_NN";
		}
		
		for(String p:patternCD){
			if(str.matches(p))
				return "_CD";
		}
		
		for(String p:patternNT){
			if(str.matches(p))
				return "_NT";
		}
		for(String p:patternVV){
			if(str.matches(p))
				return "_VV";
		}
		for(String p:patternVA){
			if(str.matches(p))
				return "_VA";
		}
		
		if(sep_nn.contains(str))
			return "_NN";
		
		if(sep_pu.contains(str))
			return "_PU";
		
		return null;
	}
	
	List<String> viterbi(Instance instance,Model model,boolean avg){
		List<String> tags=model.tags;
		int T=tags.size();
		int L=instance.words.size();
		double[][] trans_matrix=new double[T][T];
		double[][] emit_matrix=new double[L][T];
		build_score_matrix(instance,model,trans_matrix,emit_matrix,avg);
		
		List<List<State>> lattices=new ArrayList<>();
		for(int i=0;i<L;i++){
			List<State> s=new ArrayList<>();
			lattices.add(s);
		}
		
		for(int i=0;i<L;i++){
			if(i==0){
				for(int j=0;j<tags.size();j++){
					double score=emit_matrix[i][j];
					State state=new State(i, score, tags.get(j), null, j);
					lattices.get(i).add(state);
				}
			}else {
				for(int j=0;j<tags.size();j++){
					State prev=get_max_prev(lattices.get(i-1), trans_matrix, emit_matrix, i, j);
					double score=trans_matrix[prev.tag_index][j]+emit_matrix[i][j]+prev.score;
					State state=new State(i, score, tags.get(j), prev, j);
					lattices.get(i).add(state);
				}
			}
		}
		
		int index=-1;
		double score=-9999;
		for(int i=0;i<lattices.get(L-1).size();i++)
			if(score<lattices.get(L-1).get(i).score){
				score=lattices.get(L-1).get(i).score;
				index=i;
			}
		return backtrace(lattices.get(L-1).get(index));
	}
	
	State get_max_prev(List<State> s,double[][] trans_matrix,double[][] emit_matrix,int i,int j){
		int index=-1;
		double score=-9999;
		for(int r=0;r<s.size();r++){
			double t=trans_matrix[s.get(r).tag_index][j]+emit_matrix[i][j]+s.get(r).score;
			if(t>score){
				score=t;
				index=r;
			}
		}
		return s.get(index);
	}
	
	List<String> backtrace(State best){
		List<String> predict_tags=new ArrayList<>();
		while(best!=null){
			predict_tags.add(best.tag);
			best=best.prev_state;
		}
		Collections.reverse(predict_tags);
		return predict_tags;
	}
	
	void build_score_matrix(Instance instance,Model model,double[][] trans_matrix,double[][] emit_matrix,boolean avg){
		int T=model.tags.size();
		int L=instance.words.size();
		//System.out.println(instance.words);
		for(int i=0;i<T;i++)
			for(int j=0;j<T;j++){
				if(avg)
					trans_matrix[i][j]=model.Wsum[i*T+j];
				else
					trans_matrix[i][j]=model.W[i*T+j];
			}
		List<Integer> nums=new ArrayList<>();
		for(int i=0;i<L;i++)
			for(int j=0;j<T;j++){
				List<Integer> temp=instance.indices.get(i);
				for(int t=0;t<temp.size();t++)
					nums.add(temp.get(t)*T+T*T+j);
				if(avg)
					emit_matrix[i][j]=cal_score(model.Wsum, nums);
				else
					emit_matrix[i][j]=cal_score(model.W, nums);
				nums.clear();
			}
	}
	
	double cal_score(double[] w,List<Integer> nums){
		double result=0;
		for(int i=0;i<nums.size();i++)
			result+=w[nums.get(i)];
		return result;
	}
	/**从模型中抽出所含特征*/
	void build_instance(Instance instance,Model model){
		instance.indices=new ArrayList<>();
		for(int i=0;i<instance.words.size();i++){
			List<Integer> temp=new ArrayList<>();
			List<String> features=extract_local_features(instance, i);
			for(String str:features)
				if(model.feature_alphabet.containsKey(str))
					temp.add(model.feature_alphabet.get(str));
			instance.indices.add(temp);
		}
	}
	
	void evaluate(Model model){
		List<Instance> instances=new ArrayList<>();
		try {
			FileInputStream fis = new FileInputStream(trainFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line=br.readLine();
			if(line.startsWith("\uFEFF"))
				line=line.substring(1);
			while(line!=null){
				Instance instance=Data.read_instance(line, "train");
				instances.add(instance);
				line=br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(Instance instance:instances)
			build_instance(instance, model);
		
		int correct=0,total=0;
		for(Instance instance:instances){
			List<String> predict=viterbi(instance, model, false);
			List<String> reference=instance.labels;
			if(predict.size()!=reference.size())
				System.out.println("wrong length");
			total+=reference.size();
			for(int j=0;j<reference.size();j++)
				if(predict.get(j).equals(reference.get(j)))
					correct++;
		}
		System.out.println("training accuracy: "+1.0*correct/total);
	}
	
//	String eva_result(String num,List<String> diff,Map<String, Integer> map){
//		//读取要对比的两组数据到instances和gold_instances
//		List<Instance> instances=new ArrayList<Instance>();
//		List<Instance> gold_instances=new ArrayList<Instance>();
//		try {
//			if(outputFile==null){
//				System.out.println("can't find output file from tag.properties file");
//				return null;
//			}
//			File output_file=new File(outputFile);
//			if(!output_file.exists()){
//				System.out.println("can't find output file while evaluating");
//				return null;
//			}
//			FileInputStream fis = new FileInputStream(outputFile+"_"+num);
//			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
//			BufferedReader br = new BufferedReader(isr);
//			String line=br.readLine();
//			if(line.startsWith("\uFEFF"))
//				line=line.substring(1);
//			while(line!=null){
//				Instance instance=Data.read_instance(line, "train");
//				instances.add(instance);
//				line=br.readLine();
//			}
//			br.close();
//			
////			if(evaFile==null){
////				System.out.println("can't find evaluation file from tag.properties file");
////				return;
////			}
////			File eva_file=new File(evaFile);
////			if(!eva_file.exists()){
////				System.out.println("can't find evaluation file while evaluating");
////				return;
////			}
//			FileInputStream fis1 = new FileInputStream(evaFile+"_"+num);
//			InputStreamReader isr1 = new InputStreamReader(fis1, "UTF-8");
//			BufferedReader br1 = new BufferedReader(isr1);
//			line=br1.readLine();
//			if(line.startsWith("\uFEFF"))
//				line=line.substring(1);
//			while(line!=null){
//				Instance instance=Data.read_instance(line, "train");
//				gold_instances.add(instance);
//				line=br1.readLine();
//			}
//			br1.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		//开始对比计数	
//		int correct=0,total=0;
//		for(int i=0;i<instances.size();i++){
//			List<String> words=instances.get(i).words;
//			List<String> predict=gold_instances.get(i).tags;
//			List<String> reference=instances.get(i).tags;
//			if(predict.size()!=reference.size()){
//				System.out.println(gold_instances.get(i).words);
//				System.out.println("wrong length");}
//			total+=reference.size();
//			for(int j=0;j<reference.size();j++){
//				if(predict.get(j).equals(reference.get(j)))
//					correct++;
//				else{
//					String str=reference.get(j)+"_"+predict.get(j);
//					if(map.containsKey(str))
//						map.put(str, map.get(str)+1);
//					else 
//						map.put(str, 1);
//					String str1="",str2="";
//					for(int index=-2;index<=2;index++){
//						if(j+index<0||j+index>reference.size()-1)
//							continue;
//						else{
//							str1+=words.get(j+index)+"_"+predict.get(j+index)+" ";
//							str2+=words.get(j+index)+"_"+reference.get(j+index)+" ";
//						}
//					}
//					diff.add(str1);
//					diff.add(str2);
//					diff.add("\n");
//				}
//			}
//		}
//	
//		return String.valueOf(1.0*correct/total);
//	}
	void eva_result(List<String> diff){
		//读取要对比的两组数据到instances和gold_instances
		List<Instance> instances=new ArrayList<Instance>();
		List<Instance> gold_instances=new ArrayList<Instance>();
		try {
			if(outputFile==null){
				System.out.println("can't find output file from tag.properties file");
				return;
			}
			File output_file=new File(outputFile);
			if(!output_file.exists()){
				System.out.println("can't find output file while evaluating");
				return;
			}
			FileInputStream fis = new FileInputStream(outputFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line=br.readLine();
			if(line.startsWith("\uFEFF"))
				line=line.substring(1);
			while(line!=null){
				Instance instance=Data.read_instance(line, "train");
				instances.add(instance);
				line=br.readLine();
			}
			br.close();
			
			if(evaFile==null){
				System.out.println("can't find evaluation file from tag.properties file");
				return;
			}
			File eva_file=new File(evaFile);
			if(!eva_file.exists()){
				System.out.println("can't find evaluation file while evaluating");
				return;
			}
			FileInputStream fis1 = new FileInputStream(evaFile);
			InputStreamReader isr1 = new InputStreamReader(fis1, "UTF-8");
			BufferedReader br1 = new BufferedReader(isr1);
			line=br1.readLine();
			if(line.startsWith("\uFEFF"))
				line=line.substring(1);
			while(line!=null){
				Instance instance=Data.read_instance(line, "train");
				gold_instances.add(instance);
				line=br1.readLine();
			}
			br1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//开始对比计数	
		int correct=0,total=0;
		for(int i=0;i<instances.size();i++){
			List<String> words=instances.get(i).words;
			List<String> predict=gold_instances.get(i).labels;
			List<String> reference=instances.get(i).labels;
			if(predict.size()!=reference.size()){
				System.out.println(gold_instances.get(i).words);
				System.out.println("wrong length");}
			total+=reference.size();
			for(int j=0;j<reference.size();j++){
				if(predict.get(j).equals(reference.get(j))||(predict.get(j).equals("NN")&&reference.get(j).equals("NR"))||(predict.get(j).equals("NR")&&reference.get(j).equals("NN")))
					correct++;
				else{
					String str=reference.get(j)+"_"+predict.get(j);
					String str1="",str2="";
					for(int index=-2;index<=2;index++){
						if(j+index<0||j+index>reference.size()-1)
							continue;
						else{
							str1+=words.get(j+index)+"_"+predict.get(j+index)+" ";
							str2+=words.get(j+index)+"_"+reference.get(j+index)+" ";
						}
					}
					diff.add(str1);
					diff.add(str2);
					diff.add("\n");
				}
			}
		}
		System.out.println(1.0*correct/total);
		//Evaluate.diff(outputFile, evaFile, "data/pos_diff.txt");
	}
	
	void save_model(Model model){
		if(modelFile==null){
			System.out.println("can't find model file from tag.properties file while writing model");
			return;
		}
		
		try {
			File model_file=new File(modelFile);
			if(!model_file.exists()){
				model_file.createNewFile();
			}
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(model_file));
			os.writeObject(model);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void save_model(Model model,String num){
		if(modelFile==null){
			System.out.println("can't find model file from tag.properties file while writing model");
			return;
		}
		
		try {
			File model_file=new File(modelFile+"_"+num);
			if(!model_file.exists()){
				model_file.createNewFile();
			}
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(model_file));
			os.writeObject(model);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	Model read_model(){
		Model model=new Model();
		if(modelFile==null){
			System.out.println("can't find model file from tag.properties file while reading model");
			return null;
		}
		File model_file=new File(modelFile);
		if(!model_file.exists()){
			System.out.println("can't find postag model file while reading model");
			return null;
		}
		try {
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(model_file));
			model=(Model)is.readObject();
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return model;
	}
	
//	Model read_model(String num){
//		Model model=new Model();
//		if(modelFile==null){
//			System.out.println("can't find model file from tag.properties file while reading model");
//			return null;
//		}
//		File model_file=new File(modelFile+"_"+num);
//		if(!model_file.exists()){
//			System.out.println("can't find postag model file while reading model");
//			return null;
//		}
//		try {
//			ObjectInputStream is = new ObjectInputStream(new FileInputStream(model_file));
//			model=(Model)is.readObject();
//			is.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		return model;
//	}
//	
//	public void tag_by_list(List<String> seg_result,String num){
//		List<Instance> instances=new ArrayList<>();
//		for(String line:seg_result){
//			line=line.trim();
//			if(line.equals("")){
//				continue;
//			}else {
//				Instance instance=Data.read_instance(line, "test");
//				instances.add(instance);
//			}
//		}	
//		DateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
//		String time=format.format(new Date());
//		System.out.println(time+" loading postag model......");
//		
//		Model model=read_model(num);
//		
//		time=format.format(new Date());
//		System.out.println(time+" postag model has been loaded");
//		if(model==null){
//			System.out.println("the postag model file is empty");
//			return;
//		}
//		
//		for(Instance instance:instances)
//			build_instance(instance, model);
//		for(Instance instance:instances){
//			List<String> p=viterbi(instance, model, false);
//			instance.predict_tags=p;
//		}
//		
//		//evaluate(model);
//		String[] sep_character={"[","]","【","】",";","；","@","#"};
//		Set<String> sep=new HashSet<>();
//		for(String c:sep_character)
//			sep.add(c);
//	//	String URL_pattern="(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]*[-A-Za-z0-9+&@#/%=~_|]";
//		
//		try {
//			if(outputFile==null){
//				System.out.println("can't find output file from joint.properties file");
//				return;
//			}
//			File dest = new File(outputFile);  
//			if(!dest.exists()){
//			   dest.createNewFile();
//			}
//			FileOutputStream fos = new FileOutputStream(outputFile+"_"+num); 
//	        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
//			for(Instance instance:instances){
//				for(int i=0;i<instance.words.size();i++){
//					if(sep.contains(instance.words.get(i)))
//						osw.write(instance.words.get(i)+"_PU ");
////					else if(instance.words.get(i).matches(URL_pattern))
////						osw.write(instance.words.get(i)+"/URL ");
//					else
//						osw.write(instance.words.get(i)+"_"+instance.predict_tags.get(i)+" ");
//				}
//				osw.write("\n");
//			}
//			osw.flush();
//			osw.close();
//			time = format.format(new Date());
//			System.out.println(time+" the result has been written into output file");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	List<String> read_trainFile(){
		List<String> result=new ArrayList<>();
		try {
			if(trainFile==null){
				System.out.println("can't find training data from tag.properties file");
				return null;
			}
			File train_file=new File(trainFile);
			if(!train_file.exists()){
				System.out.println("can't find postag training data");
				return null;
			}
			FileInputStream fis = new FileInputStream(train_file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line=br.readLine();
			if(line.startsWith("\uFEFF"))
				line=line.substring(1);
			while(line!=null){
				line=line.trim();
				result.add(line);
				line=br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(result.size());
		return result;
	}
	/**
	 * @param prefix 路径前缀
	 * @param model 模型文件
	 * @param result 分词结果
	 * @param words 传入的地点list*/
	public List<String> geo_tag(String prefix, Model model, List<String> result, List<String> words){
		List<String> final_result=new ArrayList<>();
//		List<Instance> instances=new ArrayList<>();
		
//		for(List<String> temp_list:result){
//			String line="";
//			for(String str:temp_list)
//				line+=str+" ";
//			line=line.trim();
//			Instance instance=Data.read_instance(line, "test");
//			build_instance(instance, model);
//			List<String> p=viterbi(instance, model, false);
//			instance.predict_tags=p;
//			instances.add(instance);
//		}
		List<String> timeList=new ArrayList<>();
		List<String> locList=new ArrayList<>();
		
		String line="";
		for(String str:result){
			if(str.endsWith("_time")){
				timeList.add(str.split("_")[0]);
				line+=str.split("_")[0]+" ";
			}else if (str.endsWith("_loc")) {
				locList.add(str.split("_")[0]);
				line+=str.split("_")[0]+" ";
			}else {
				line+=str+" ";
			}
		}
		line=line.trim();
		Instance instance=Data.read_instance(line, "test");
		build_instance(instance, model);
		List<String> p=viterbi(instance, model, false);
		instance.predict_tags=p;
		
		for(int i=0;i<instance.words.size();i++){
			if(timeList.contains(instance.words.get(i)))
				final_result.add(instance.words.get(i)+"_time");
			else if (locList.contains(instance.words.get(i))) 
				final_result.add(instance.words.get(i)+"_loc");
			else if(get_post_tag(prefix,instance.words.get(i),words)!=null)
				final_result.add(instance.words.get(i)+get_post_tag(prefix,instance.words.get(i),words));
			else
				final_result.add(instance.words.get(i)+"_"+instance.predict_tags.get(i));
//		final_result.add(instance.words.get(i)+"_"+instance.predict_tags.get(i));
		}
		//幅度前面必须是名词而不是动词
		if(final_result.indexOf("幅度_NN")!=-1){
			int index=final_result.indexOf("幅度_NN");
			if(index>=1 && final_result.get(index-1).endsWith("VV")){
				String str = final_result.get(index-1).split("_")[0]+"_NN";
				final_result.set(index-1, str);
			}
		}
		//后处理 0-3月分开的情况
		while(MyUtil.Contains(final_result, "~_PU|～_PU|－_PU|-_PU|——_PU|—_PU")!=-1){
			int index=MyUtil.Contains(final_result, "~_PU|～_PU|－_PU|-_PU|——_PU|—_PU");
			if(index>0&&index<final_result.size()-1){
				int b=index-1;
				int e=index+1;
				if((final_result.get(b).endsWith("NT") && final_result.get(e).endsWith("NT"))||
					(final_result.get(b).endsWith("CD") && (final_result.get(e).endsWith("NT")||final_result.get(e).endsWith("CD")))){
					String join="";
					for(int i=b;i<e;i++) join+=final_result.get(i).split("_")[0];
					join+=final_result.get(e);
					final_result.set(b, join);
					final_result.remove(index);
					final_result.remove(index);
				}else {
					break;
				}
			}
			else {
				break;
			}
		//	System.out.println(final_result);
		}
		
//		String ofile=outputFile;
//		try {
//			File dest = new File(ofile);  
//			if(!dest.exists()){
//			   dest.createNewFile();
//			}
//			FileOutputStream fos = new FileOutputStream(ofile); 
//	        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
//	        for(Instance instance:instances){
//	        	 for(int i=0;i<instance.words.size();i++){
//	        		 if(instance.words.equals(" "))
//	        			 continue;
//				if(Is(instance.words.get(i)))
//					osw.write(instance.words.get(i)+" ");
//				else
//					osw.write(instance.words.get(i)+"_"+instance.predict_tags.get(i)+" ");
//			}
//			osw.write("\n");
//	        }
//	       
//			osw.flush();
//			osw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		//System.out.println("done");
		return final_result;
	}
	
//	void get_evaFile(List<String> list){
//		String[] suffix={"0","1","2","3","4","5","6","7","8","9","10"};
//		int size=list.size()/10;
//		for(int i=1;i<=10;i++){
//			int begin=size*Integer.parseInt(suffix[i-1]);
//			int end=size*Integer.parseInt(suffix[i]);
//			FileOutputStream fos;
//			try {
//				fos = new FileOutputStream(evaFile+"_"+suffix[i]);
//				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
//			for(int t=begin;t<end;t++)
//				osw.write(list.get(t)+"\n");
//			osw.flush();
//			osw.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//	       
//		}
//	}
	
//	void run_10cv(List<String> list){
//		Map<String, Integer> map = new HashMap<>();
//		List<String> result = new ArrayList<>();
//		try {
//			BufferedWriter bw = new BufferedWriter(
//					new OutputStreamWriter(new FileOutputStream("postagdata/EvatypeResult.txt"), "UTF-8"));
//			for (int i = 1; i <= 10; i++) {
//				List<String> train_list = new ArrayList<>();
//				List<String> test_list = new ArrayList<>();
//				build(list, train_list, test_list, i);
//				//System.out.println(train_list.size() + " " + test_list.size());
//				trainByList(train_list, String.valueOf(i));
//				tag_by_list(test_list, String.valueOf(i));
//				List<String> diff = new ArrayList<>();
//				result.add(eva_result(String.valueOf(i), diff, map));
//				for (String str : diff)
//					bw.write(str + "\n");
//			}
//			Map<String, Integer> fi=sortMap(map);
//			for(Map.Entry<String, Integer> en:fi.entrySet())
//				bw.write(en.getKey()+"\t"+en.getValue()+"\n");
//			for(String str:result)
//				bw.write(str + "\n");
//			bw.flush();
//			bw.close();
//		} catch (UnsupportedEncodingException | FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		
//	}
//	
//	void build(List<String> list,List<String> train_list,List<String> test_list,int index){
//		List<String> temp=new ArrayList<>();
//		String[] suffix={"0","1","2","3","4","5","6","7","8","9","10"};
//		int size=list.size()/10;
//		int begin=size*Integer.parseInt(suffix[index-1]);
//		int end=size*Integer.parseInt(suffix[index]);
//		for(int i=0;i<begin;i++)
//			train_list.add(list.get(i));
//		for(int i=begin;i<end;i++)
//			temp.add(list.get(i));
//		for(int i=end;i<10*size;i++)
//			train_list.add(list.get(i));
//		
//		for(String line:temp){
//			String test="";
//			String[] w=line.split(" ");
//			for(int i=0;i<w.length;i++){
//				String[] t=w[i].split("_");
//				if(t.length==2){
//					test+=t[0]+" ";
//				}else{
//					int len=t.length;
//					String t0="";
//					for(int j=0;j<len-1;j++)
//						t0+=t[j];
//					test+=t0+" ";
//				}
//			}
//			test_list.add(test.trim());
//		}
//	}
//	
//	Map<String, Integer> sortMap(Map<String, Integer> oldmap){
//		List<Map.Entry<String, Integer>> list=new ArrayList<Map.Entry<String,Integer>>(oldmap.entrySet());
//		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
//
//			@Override
//			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
//				return o2.getValue()-o1.getValue();
//			}
//			
//		});
//		Map<String, Integer> newmap = new LinkedHashMap();
//		for(int i=0;i<list.size();i++)
//			newmap.put(list.get(i).getKey(), list.get(i).getValue());
//		return newmap;
//	}
	
	
}
