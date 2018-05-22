package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import cn.edu.nju.ws.GeoScholar.templating.common.Input;
import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Segment;
import cn.edu.nju.ws.GeoScholar.templating.common.TimuInfo;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
import cn.edu.nju.ws.GeoScholar.templating.common.findCueWord;

public class BijiaoTemplate {

	public static String getTemplate(TimuInfo topic) throws IOException {
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList)topic.allTagList;
		int bound = topic.tgTag.split(" ").length;
		Map<String, String> cueword = Input.getCueword(6);
		List<Tree> word = findCueWord.getCueWords(cueword, t, 6, bound, sentence);
		String template = "";
		Set<String> words = new HashSet<String>();
		for (String s : sentence) words.add(s.split("_")[0]);
		for (Tree w : word) {
			//之上、之下必须位于句尾，前面必跟一个单位或者CD或者数字开头的NN
			if(MyUtil.strEquals(w.content, "以上|以下") && zhishangSatisfy(w.no-2, sentence)){
				template += w.no + "+" + BijiaoYishang.print(w, sentence) + "\t";
			}
			//触发词之后的词一定是数字或者NN以数字开头
			else if(MyUtil.strEquals(w.content, "不足|达|达到|约|接近|等于")
					&&!MyUtil.setContains(words, "以上|以下")
					&&(w.no<sentence.size()&&(sentence.get(w.no).endsWith("CD")||(sentence.get(w.no).endsWith("_NN")&&MyUtil.startWithNum(sentence.get(w.no)))))){
				template += w.no + "+" + BijiaoBuzu.print(w, sentence) + "\t";
			}
			//相比不位于句首，而且后面没有“比”：与地球相比，火星	自转角速度比地球大得多
			else if (w.content.equals("相比") && w.no>1 && !(sentence.contains("比_P")&&sentence.indexOf("比_P")>w.no)) {
				String s = "";
				if(sentence.get(0).split("_")[0].equals("与") || sentence.get(0).split("_")[0].equals("和"))
					//与前面没有主语
					s = BijiaoWordXiangbi.print(sentence, w);
				else 
					s = BijiaoWordXiangbi2.print(sentence, w);
				//并列结构的拆分，是为了处理并列结构
				if (s.contains(" ")) {
					String[] temp = s.split("@");
					String[] temp1 = temp[2].split(" ");
					String[] temp2 = temp[4].split(" ");
					int max = temp1.length > temp2.length ? temp1.length : temp2.length;
					for (int i = 0; i < max; i++) {
						template += w.no + "+";
						template += temp[0] + "@" + temp[1] + "@";
						template += i < temp1.length ? temp1[i] : temp1[temp1.length - 1];
						template += "@@";
						template += i < temp2.length ? temp2[i] : temp2[temp2.length - 1];
						template += "\t";
					}
				}
				else
					template += template += w.no + "+" + s + "\t";
			}
			else
			if ((words.contains("与")||words.contains("和")) && MyUtil.strEquals(w.content, "类似|一样|相当|一致|相同|不同|相反"))
				template += w.no + "+" + BijiaoWordXiangtong.print(sentence, w) + "\t";
//			else
//			if (w.content.equals("同时"))
//				template += w.no + "+" + BijiaoWordTongshi.print(sentence, w) + "\t";
			else
			if (w.content.endsWith("于"))
				template += w.no + "+" + BijiaoWordYu.print(sentence, w) + "\t";
//			else
//			if (word.contains("在") && (w.no == sentence.size() || sentence.get(w.no).endsWith("PU"))&& w.parent.content.equals("LC")){
//				//System.out.println(sentence);
//				template += w.no + "+" + BijiaoPosLC.print(w) + "\t";}
			else
			if (w.parent.content.equals("P") && w.no>1) {
				//System.out.println("介词比较类"+topic.tgText+"\t"+topic.xxText);
				String s = BijiaoPosPP.print(w, sentence);
				if (s.contains(" ")) {
					String[] temp = s.split("@");
					String[] temp1 = temp[2].split(" ");
					String[] temp2 = temp[4].split(" ");
					int max = temp1.length > temp2.length ? temp1.length : temp2.length;
					for (int i = 0; i < max; i++) {
						template += w.no + "+"; 
						template += temp[0] + "@" + temp[1] + "@";
						template += i < temp1.length ? temp1[i] : temp1[temp1.length - 1];
						template += "@@";
						template += i < temp2.length ? temp2[i] : temp2[temp2.length - 1];
						template += "\t";
					}
				}
				else
					template += w.no + "+" + s + "\t";
			}
		}
		if (!template.isEmpty()) template = template.substring(0, template.length() - 1);
		if(template.length()==0) return template;
		//用于将实体和它的方面分离
		String[] list = template.split("\t");
		template = "";
		for (String s : list){
			if (s.contains("@@@")) {
				String no = s.split("\\+")[0];
				s = s.substring(s.indexOf("+") + 1);
				String new_s="";
				List<String> l1 = Segment.j.seg_postag(s.split("@")[0],0);
				List<String> l2 = Segment.j.seg_postag(s.split("@")[1],0);
				if (l1.size() > l2.size()) {
				//	如果l1比l2长，那么根据l2去l1中找对应的词，
					new_s += no + "+";
					List<String> split_result=splitTemplate(l1, l2);
//					for (int i = l1.size() - l2.size(); i < l1.size(); i++)
//						new_s += l1.get(i).split("_")[0];
					new_s +=split_result.get(0)+"@";
					new_s += s.split("@")[1] + "@";
//					for (int i = 0; i < l1.size() - l2.size(); i++)
//						new_s += l1.get(i).split("_")[0];
					new_s +=split_result.get(1);
					new_s += "@@" + s.substring(s.lastIndexOf("@") + 1);
				} else if (l1.size() < l2.size()) {
					//如果l1较短，l2较长，那么将l2切分成和l1长度一样的部分和后面的。前面的算槽二，之后的算槽三
					new_s += no + "+";
					new_s += s.split("@")[0] + "@";
					for (int i = 0; i < l1.size(); i++)
						new_s += l2.get(i).split("_")[0];
					new_s += "@";
					for (int i = l1.size(); i < l2.size(); i++)
						new_s += l2.get(i).split("_")[0];
					new_s += "@@" + s.substring(s.lastIndexOf("@") + 1);
				} else {
					//如果l1，l2一样长，找二者中一样的词或者词组，如果k1到达最后一个词(不是溢出)，那么将l2拆分成（0-k2）(槽二)和（k2到尾部）(槽三)；
					//否则那么将l1拆分成（0-k1）(槽一)和（k1到尾部）(槽三)
					int k1 = 0, k2 = 0;
					for (int i = 0; i < l1.size(); i++)
						for (int j = 0; j < l2.size(); j++)
							if (l1.get(i).equals(l2.get(j))) {
								k1 = i;
								k2 = j;
								break;
							}
					while (k1<l1.size()-1 && k2<l2.size()-1 && l1.get(k1).equals(l2.get(k2))) {
						k1++;
						k2++;
					}
					if (k1 == l1.size() - 1) {
						new_s += no + "+";
						new_s += s.split("@")[0] + "@";
						for (int i = 0; i <= k2; i++)
							new_s += l2.get(i).split("_")[0];
						new_s += "@";
						for (int i = k2 + 1; i < l2.size(); i++)
							new_s += l2.get(i).split("_")[0];
						new_s += "@@" + s.substring(s.lastIndexOf("@") + 1);
					} else {
						new_s += no + "+";
						for (int i = 0; i <= k1; i++)
							new_s += l1.get(i).split("_")[0];
						new_s += "@";
						new_s += s.split("@")[1] + "@";
						for (int i = k1 + 1; i < l1.size(); i++)
							new_s += l1.get(i).split("_")[0];
						new_s += "@@" + s.substring(s.lastIndexOf("@") + 1);
					}
				}
				template += modifySlot(new_s) + "\t";
			} else
				template += modifySlot(s) + "\t";
			}
		return template;
	}
	//新模板和旧模板填槽的位置变了，因此特殊处理一下
	public static String modifySlot(String s){
		if(s.equals(""))
			return "";
		String[] temp=s.split("@");
		if(temp.length<4){
			//System.out.println("比较"+s);
			return s;
		}
		//处理方面前面还有的
		if(temp[2].startsWith("的"))
			temp[2]=temp[2].substring(1);
		if(temp[3].startsWith("的"))
			temp[3]=temp[3].substring(1);
		return temp.length==5?temp[0]+"@"+temp[2]+"@"+temp[1]+"@"+temp[3]+"@"+temp[4] : temp[0]+"@"+temp[2]+"@"+temp[1]+"@"+temp[3]+"@";
	}
	/*
	 * 用于处理“该线路北京游客的规模较宁夏的小”这样的句子中提出北京和宁夏这样对应的
	 * 1、如果槽二NR：“北极村自转线速度比大连大”，去槽一找对应的NR，并从NR开始截取l2.size个词赋给first,槽一其余赋给second
	 * 3、槽二含有：可以利用规则匹配：“图中甲地1月份降水量比7月份的多”
	 * 4、利用词的相似性，按照按照完全相同，前后缀两个字相同、前后缀一个字相同的顺序，
	 * 如果以上都不奏效，那么把槽一后l2.size个词赋给first，前面的给second
	 * 局限性：甲地人口迁出比例直辖市比省级行政中心高
	 * excuse me？直辖市省级中心怎么匹配
	 * 2、如果含有的“①的水温变化比②大”按“的分开”
	 */
	public static List<String> splitTemplate(List<String> l1, List<String> l2){
		//为了特殊处理：则E处气温比H处高，多了一个则
		if(l1.size()>1 && l1.get(0).split("_")[1].equals("AD"))
			l1.remove(0);
		
		List<String> result = new ArrayList<String>();
		String first="",second="";
		int de_index = -1;
		List<String> l2rule=new ArrayList<String>();
		List<Integer> indexes= new ArrayList<Integer>();
		int l2nr=isNR(l2);
		
		if (l2nr!=-1) {
			int i;
			for(i = 0; i < l1.size() ; i++){
				if(l1.get(i).split("_")[1].equals("NR"))
					break;
			}
			if(i < l1.size()){
				int gap = i - l2nr;
				for (int j = 0; j < l2.size(); j++) {
					if (gap + j >= 0 && gap + j < l1.size())
						first += l1.get(j + gap).split("_")[0];
				}

				String s = listPrint(l1);
				second = s.replace(first, "");
				if(second.startsWith("的"))
					second=second.substring(1, second.length());
				result.add(first);
				result.add(second);
			}
		}
		else if ((l2rule=isRule(l2))!=null) {
			String re=l2rule.get(0);
			Pattern p=Pattern.compile(re);
			int index=Integer.parseInt(l2rule.get(1));
			int i;
			for(i = 0; i < l1.size() ; i++){
				if(p.matcher(l1.get(i).split("_")[0]).matches())
					break;
			}
			if(i < l1.size()){
				int gap = i - index;
				for (int j = 0; j < l2.size(); j++) {
					if (gap + j >= 0 && gap + j < l1.size())
						first += l1.get(j + gap).split("_")[0];
				}

				String s = listPrint(l1);
				second = s.replace(first, "");
				result.add(first);
				if(second.startsWith("的"))
					second=second.substring(1, second.length());
				result.add(second);
			}
		}else if((indexes = get_similar_words(l1, l2))!=null){
			int w2 = indexes.get(0);
			int w1 = indexes.get(1);
			int gap = w1 - w2;
			for (int j = 0; j < l2.size(); j++) {
				if (gap + j >= 0 && gap + j < l1.size())
					first += l1.get(j + gap).split("_")[0];
			}

			String s = listPrint(l1);
			second = s.replace(first, "");
			if(second.startsWith("的"))
				second=second.substring(1, second.length());
			result.add(first);
			result.add(second);
		}
		else if ((l1.contains("的_DEG") || l1.contains("的_DEC")) && (de_index = get_de_index(l1, l2)) != -1) {
			// else if((l1.contains("的_DEG")||l1.contains("的_DEC"))){
			if (de_index < l1.size() - 1) {
				for (int i = 0; i < de_index; i++)
					first += l1.get(i).split("_")[0];
				for (int i = de_index + 1; i < l1.size(); i++)
					second += l1.get(i).split("_")[0];
				result.add(first);
				result.add(second);
			}
		}
		if(result.size()==0){
			for (int i = l1.size() - l2.size(); i < l1.size(); i++)
				first += l1.get(i).split("_")[0];
			for (int i = 0; i < l1.size() - l2.size(); i++)
				second += l1.get(i).split("_")[0];
			result.add(first);
			result.add(second);
		}
		return result;
	}
	
	public static int get_de_index(List<String> l1, List<String> l2){
		int index=l1.contains("的_DEG")?l1.indexOf("的_DEG"):l1.indexOf("的_DEC");
		//不记得注释为啥这样些了，为了转述先改掉
//		if(index!=l2.size())
//			return -1;
		return index;
	}
	/**找句子中的NR并返回所在下标，如果NR下一个词是[]*/
	public static int isNR(List<String> l2){
		int flag=-1;
		int i;
		for(i=0;i<l2.size();i++){
			if(l2.get(i).split("_")[1].equals("NR")){
				flag=i;
				break;
			}
		}
		if(i<l2.size()-1 && l2.get(i+1).split("_")[0].equals("的"))
			l2.remove(i+1);
		return flag;
	}
	/**找句子中符合正则表达式的词语，并记下是符合的哪一条表达式及当前词的下标*/
	public static List<String> isRule(List<String> l2){
		List<String> result=new ArrayList<String>();
		String[] pattern_str = { 
				"①|②|③|④|⑤",
				"([图|表])([\\d|甲|乙|丙|丁])+([\\（|\\(][a|b|c|d|A|B|C|D][\\）|\\)])*",
				"[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]]{1,2}(类|图|国|河流|河|市|县|镇|村|点|线|地|区域|区|阶段)",
				"[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]][城省处]",
				"(洋流|气流|环节)[甲|乙|丙|丁|戊|①|②|③|④|⑤|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|[A-Z]|[a-z]]",
				"[A-Za-z0-9\\.\\+\\_\\/]*[A-Za-z0-9](分钟|分|秒|世纪|年代|年|月份|月|日|时刻|时|万|亿|%|％|℃)",
				"(\\d|\\.)+°[ewsnEWSN]?((\\d|\\.)+′[ewsnEWSN]?)?"
				};
		int i;
		
		for(i=0;i<l2.size();i++){
			for(String str:pattern_str){
				Pattern p=Pattern.compile(str);
				if(p.matcher(l2.get(i).split("_")[0]).matches()){
					result.add(str);
					result.add(String.valueOf(i));
					if(i<l2.size()-1 && l2.get(i+1).split("_")[0].equals("的"))
						l2.remove(i+1);
					return result;
				}
			}
		}
		return null;
	}
	
	public static String listPrint(List<String> l){
		String result="";
		for(int i=0;i<l.size();i++)
			result+=l.get(i).split("_")[0];
		return result;
	}
	//衡量两个词语的相似程度，按照完全相同，前后缀两个字相同、前后缀一个字相同的顺序
	public static List<Integer> get_similar_words(List<String> l1, List<String> l2){
		List<Integer> result=new ArrayList<Integer>();
		for(int i=0;i<l2.size();i++)
			for(int j=0;j<l1.size();j++){
				if(l2.get(i).split("_")[0].equals(l1.get(j).split("_")[0])){
					result.add(i);
					if(i<l2.size()-1 && l2.get(i+1).split("_")[0].equals("的"))
						l2.remove(i+1);
					result.add(j);
					return result;
				}
			}
		for(int i=0;i<l2.size();i++)
			for(int j=0;j<l1.size();j++)
				for(int len=2;len>0;len--){
					if(word_similarity(len, l2.get(i).split("_")[0], l1.get(j).split("_")[0])){
						result.add(i);
						if(i<l2.size()-1 && l2.get(i+1).split("_")[0].equals("的"))
							l2.remove(i+1);
						result.add(j);
						return result;
					}
			}
		return null;
	}
	public static boolean word_similarity(int len,String word1, String word2){
		if(!(word1.length()>=len)||!(word2.length()>=len))
			return false;
		String w1=word1.substring(0, len);
		String w2=word2.substring(0, len);
		if(w1.equals(w2))
			return true;
		w1=word1.substring(word1.length()-len, word1.length());
		w2=word2.substring(word2.length()-len, word2.length());
		if(w1.equals(w2))
			return true;
		return false;
	}
	
	//前面紧跟数字、单位或者上上一个词是数字、单位以及第一个字符是数字的NN
	public static boolean zhishangSatisfy(int no, List<String> sentence){
		if(sentence.get(no).endsWith("_M")||sentence.get(no).endsWith("_CD"))
			return true;
		if(no-1>0 && (sentence.get(no-1).endsWith("_M") || sentence.get(no-1).endsWith("_CD") || (sentence.get(no-1).endsWith("_NN")&&MyUtil.startWithNum(sentence.get(no-1)))))
			return true;
		return false;
	}
//	//用于判断与。。相比之前是否有主语，true是有主语
//	public static boolean XiangbiMode(Tree node, ArrayList<String> sentence){
//		int index=node.no - 1;
//		
//	}
	
}
