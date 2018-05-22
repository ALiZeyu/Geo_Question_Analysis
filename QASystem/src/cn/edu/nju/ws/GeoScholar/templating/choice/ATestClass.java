package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.nju.ws.GeoScholar.common.NLPResult;
import cn.edu.nju.ws.GeoScholar.common.NLPTemplate;
import cn.edu.nju.ws.GeoScholar.templating.common.Input;
import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.QuestionTemplateFromNLP;
import cn.edu.nju.ws.GeoScholar.templating.common.Segment;
import cn.edu.nju.ws.GeoScholar.templating.common.TimuInfo;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
import cn.edu.nju.ws.GeoScholar.templating.common.Util;

public class ATestClass {
	
	public static void main(String[] args) throws IOException {
		Generate.init();
		//successiveSplitLabel();
		//seg_tag_file("E://geo_data/所有试题.txt", "E://geo_data/all_processed.txt");
		successiveProcess();
		//rule_enattri("E://workspace/AtrributeExtraction/data/chenshu_full");
		//getChioce();
		//Process();
		//getParsingTree();
		//eva("E://workspace/CSV2TXT/data/paper/科大讯飞-地理测试01.data");
		//splitProcess();
		//getSplitLabel();
		//getSplitSens();
	}
	
	//读取文件分词和词性标注之后写入文件
	public static void seg_tag_file(String inpath, String outpath) throws IOException{
		List<String> data = MyUtil.readListFromFile(inpath);
		List<String> result = new ArrayList<>();
		for(String sen : data){
			List<String> temp = Segment.segmentQuestion(sen);
			String temp_str = MyUtil.listToString(temp);
			result.add(temp_str);
		}
		MyUtil.writeFile(result, outpath);
	}
	
//	public static void splitProcess() throws IOException{
//		String[] paths={"F://地理相关/试题拆分/Qsplit.txt","F://地理相关/试题拆分/QNsplit.txt"};
//		List<String> list=new ArrayList<>();
//		for(String path:paths){
//			List<String> strs = MyUtil.readListFromFile(path);
//			for(String str:strs){
//				if(Generate.splitOrNot(str, null))
//					list.add(str);
//			}
//		}
//		MyUtil.writeFile(list, "F://地理相关/试题拆分/FirstTemplate.txt");
//	}
	
	public static void successiveProcess() throws IOException{
		//E:/geo_data/测试软件/test_paper.txt
		List<String> result = MyUtil.readListFromFile("test_data/test_paper.txt");		
		List<String> mine = new ArrayList<>();
		Set<String> del = new HashSet<>();
		Map<String, Integer> count = new HashMap<>();
		for (int j = 0 ; j < result.size(); j++) {
			//全部试题测试
			System.out.println(result.get(j));
			mine.add(result.get(j));
			try {
				List<String> list=new ArrayList<>();
				List<NLPResult> allresult = Generate.GenerateTemplate(result.get(j),list);
				for(NLPResult tst : allresult){
					if(tst.superTemplates!=null){
						for(QuestionTemplateFromNLP nlp:tst.superTemplates.templates){
							String name = nlp.templateType;
							count.put(name, count.getOrDefault(name, 0)+1);
							if(nlp.toString()!=null)
								mine.add(nlp.toString());
						}
					}
				}
				for(NLPResult tst : allresult){
					if(tst.subTemplates!=null)
						for(QuestionTemplateFromNLP nlp:tst.subTemplates.templates){
							String name = nlp.templateType;
							if(name.equals("其他陈述") && tst.superTemplates==null){
								del.add(result.get(j));
								continue;
							}
							count.put(name, count.getOrDefault(name, 0)+1);
							if(nlp.toString()!=null)
								mine.add(nlp.toString());
						}
				}
				for(NLPResult tst : allresult){
					if((tst.superTemplates==null||tst.superTemplates.templates.size()==0)&&(tst.subTemplates==null||tst.subTemplates.templates.size()==0))
						System.out.println("模板缺失"+result.get(j));
					else if(!isLegal(tst)){
						System.out.println("含有空格"+result.get(j));
					}
				}
			} catch (Exception e) {
				System.out.println(result.get(j)+e.getMessage());
			}
			mine.add("\n");
		}
		int sum = 0;
		for(Map.Entry<String, Integer> entry : count.entrySet())
			sum+=entry.getValue();
		System.out.println(sum);
		for(Map.Entry<String, Integer> entry : count.entrySet()){
			System.out.println(entry.getKey()+"\t"+(double)entry.getValue()/sum);
		}
//		Iterator<String> it = result.iterator();
//		while(it.hasNext()){
//			String temp = it.next();
//			if(del.contains(temp))
//				it.remove();
//		}
//		MyUtil.writeFile(result, "test_data/testData.txt");
		MyUtil.writeFile(mine, "test_data/result.txt");
	}
	
	public static void rule_enattri(String p){
		String path = p+".txt";
		List<String> data = MyUtil.readListFromFile(path);
		List<String> result = new ArrayList<>();
		int index=0;
		while(index < data.size()){
			System.out.println(data.get(index));
			String sentence = RecoverSen(data.get(index));
			result.add(sentence);
			try {
				List<NLPResult> allresult = Generate.GenerateTemplate(sentence,null);
				for(NLPResult tst : allresult){
					if(tst.subTemplates!=null)
						for(QuestionTemplateFromNLP nlp:tst.subTemplates.templates)
							result.add("slot"+nlp.toString());
				}
			} catch (Exception e) {
				System.out.println(sentence+e.getMessage());
			}
			result.add(RecoverSlot(data.get(index), data.get(index+1)));
			index+=2;
		}
		MyUtil.writeFile(result, p+"_result.txt");
	}
	//从词法分析结果中得到原句
	public static String RecoverSen(String tag){
		StringBuffer sb = new StringBuffer();
		String[] list = tag.split("\t| ");
		for(String str :list)
			sb.append(str.split("_")[0]);
		return sb.toString();
	}
	//得到标注结果
	public static String RecoverSlot(String tag,String index_str){
		StringBuffer sb = new StringBuffer();
		String[] list = tag.split("\t| ");
		String[] index_array = index_str.split(";", 4);
		for(String index : index_array){
			if(index.length()>0){
				String[] sub = index.split("、");
				for(String str : sub){
					sb.append(RecoverStr(list, str));
					sb.append("、");
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(";");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	//得到实体或者属性词
	public static String RecoverStr(String[] list, String index){
		String[] be = index.split("-");
		StringBuffer sb = new StringBuffer();
		for(int i=Integer.parseInt(be[0]) ; i<=Integer.parseInt(be[be.length-1]) ; i++){
			sb.append(list[i].split("_")[0]);
		}
		return sb.toString();
	}
	
	//标准：1、只有陈述类型。2、第三个槽里面有VA
	public static ArrayList<String> isAttribution(NLPTemplate subTemplates){
		if(subTemplates.templates.size()==1 && subTemplates.templates.get(0).templateType.equals("其他陈述")){
			int b = subTemplates.templates.get(0).slots.get(2).startOffset;
			int e = subTemplates.templates.get(0).slots.get(2).endOffset;
			ArrayList<String> sen_list = subTemplates.pos;
			for(int i=b;i<=e;i++)
				if(sen_list.get(i).endsWith("VA"))
					return sen_list;
		}
		return null;
	}
	
	public static void getParsingTree() throws IOException{
		String type = "二阶_修改";
		List<String> result = MyUtil.readListFromFile("F://地理相关/500题审核/final/data/muban/" + type + "_.txt");
		//List<String> result = MyUtil.readListFromFile("E://workspace/NewTemplate/data/分别.txt");
		//List<String> result = MyUtil.readListFromFile("E://workspace/NewTemplate/data/debug.txt");
		for (int j = 0; j < result.size(); j++) {
//			if (j % 2 == 1) {
//				continue;
//			}
			String question=result.get(j);
			Tree t1=Input.parsing(question);
			question = question.replace("\t", "@");
			if (question.indexOf("@") == -1) {
				question = "@" + question;
			}
			String[] te = question.split("@");
			TimuInfo topic = new TimuInfo(question,null);
//			ArrayList<NlpOutput> l = new ArrayList<NlpOutput>();
			Tree t = topic.allTree;
			if(!t.toString().equals(t1.toString())){
				System.out.println(t.toString());
				System.out.println(t1.toString());
				System.out.println(question);
			}
			//ArrayList<String> sentence = (ArrayList) topic.allTagList;
		}
	}
	
//	public static void eva(String path) throws IOException{
//		List<String> result = MyUtil.readListFromFile(path);
//		List<String> re=new ArrayList<>();
//		for (int j = 0 ; j < result.size(); j++) {
//			System.out.println(result.get(j));
//			NLPResult tst = Generate.GenerateTemplate(result.get(j));
//			String type="";
//			String template="";
//			re.add(result.get(j));
//			if(tst.superTemplates!=null){
//				for(QuestionTemplateFromNLP q:tst.superTemplates.templates){
//					type+=q.templateType+" ";
//					template+=q.toString()+" ";
//				}
//			}
////				System.out.println(tst.superTemplates.templates.toString());
//			if(tst.subTemplates!=null){
//				for(QuestionTemplateFromNLP q:tst.subTemplates.templates){
//					type+=q.templateType+" ";
//					template+=q.toString()+" ";
//				}
//			}
//			//System.out.println(tst.subTemplates.templates.toString());
//			re.add(type.trim());
//			re.add(template.trim());
//			
////			if(sb.toString().trim().equals(""))
////				System.out.println(result.get(j));
////			else if(!sb.toString().trim().equals(result.get(j+1))){
////				mine.add(result.get(j));
////				mine.add("mine："+sb.toString().trim());
////				mine.add("gold："+result.get(j+1));
////			}
//		}
//		MyUtil.writeFile(re, path+".type");
//	
//	}
	
	public static boolean isLegal(NLPResult tst){
		if(tst.superTemplates!=null && tst.superTemplates.templates.size()>0){
			for(QuestionTemplateFromNLP q:tst.superTemplates.templates)
				if(q.toString().contains(" ")||q.toString().contains("\t"))
					return false;
		}
		if(tst.subTemplates!=null && tst.subTemplates.templates.size()>0){
			for(QuestionTemplateFromNLP q:tst.subTemplates.templates)
				if(q.toString().contains(" ")||q.toString().contains("\t"))
					return false;
		}
		return true;
	}
	/**提取比较类模板，标注实体和属性*/
	public static void getChioce() throws IOException{
		List<String> result = MyUtil.readListFromFile("E://geo_data/entity.txt");
		List<String> all=new ArrayList<>();
		for (String sentence:result) {
			StringBuilder sb=new StringBuilder();
			for(String str : sentence.split(" ")) sb.append(str.split("_")[0]);
			List<NLPResult> allresult = Generate.GenerateTemplate(sb.toString(),null);
			for(NLPResult tst : allresult){
				if(!(tst.subTemplates==null)){
					for(QuestionTemplateFromNLP tmplate : tst.subTemplates.templates)
						if(tmplate.templateType=="比较"){
							all.add(sentence);
							break;
						}
				}
			}
		}
		MyUtil.writeFile(all, "E://geo_data/com_entity.txt");
	}
	
	public static void put_label(){
		List<String> txt = MyUtil.readListFromFile("E://geo_data/com_entity.txt");
		for(String str:txt){
			String[] sentence=str.split(" ");
			
		}
	}
	/**对选项中带有逗号的句子进行拆分判断并输出待拆分的句子(原句还是选项问句)
	 * @throws IOException */
	public static void getSplitLabel() throws IOException{
//		List<String> sens = MyUtil.readListFromFile("F://地理相关/试题拆分/QNsplit.txt");
//		List<String> all=new ArrayList<>();
//		for (String sentence:sens) {
//			sentence=sentence.replace("\t", "@");
//			TimuInfo tm=new TimuInfo(sentence, null);
//			if(tm.preSplit==true)
//				all.add(sentence);
//		}
//		MyUtil.writeFile(all, "F://地理相关/试题拆分/wrong_split.txt");
//		List<String> sens = MyUtil.readListFromFile("F://地理相关/试题拆分/Qsplit.txt");
//		List<String> all=new ArrayList<>();
//		for (String sentence:sens) {
//			sentence=sentence.replace("\t", "@");
//			TimuInfo tm=new TimuInfo(sentence, null);
//			if(tm.preSplit==false)
//				all.add(sentence);
//		}
//		MyUtil.writeFile(all, "F://地理相关/试题拆分/wrong_split_true.txt");
		List<String> data=MyUtil.readListFromFile("F://MyQQFile/374489786/FileRecv/splitSample.txt");
		int tt=0,tf=0,ft=0,ff=0;
		List<String> ttf=new ArrayList<>();
		List<String> tff=new ArrayList<>();
		List<String> ftf=new ArrayList<>();
		List<String> fff=new ArrayList<>();
		for(String sen:data){
			String[] question=sen.split("\t");
			int flag=question.length==2?Integer.parseInt(question[1]):1;
			TimuInfo tm=new TimuInfo(question[0], null);
			if(tm.preSplit==true){
				if(flag==1){
					tt++;
					ttf.add(question[0]);
				}else{
					tf++;
					tff.add(question[0]);
				}
			}else{
				if(flag==1){
					ft++;
					ftf.add(question[0]);
				}
				else{
					ff++;
					fff.add(question[0]);
				}
			}
		}
		System.out.println(tt+" "+tf+" "+ft+" "+ff);
		MyUtil.writeFile(ttf, "F://地理相关/试题拆分/tt.txt");
		MyUtil.writeFile(tff, "F://地理相关/试题拆分/tf.txt");
		MyUtil.writeFile(ftf, "F://地理相关/试题拆分/ft.txt");
		MyUtil.writeFile(fff, "F://地理相关/试题拆分/ff.txt");
	}
	//取出需要拆分的句子(去掉套话这些或者是选项问句)
	public static void getSplitSens() throws IOException{
		List<String> sens = MyUtil.readListFromFile("F://地理相关/试题拆分/Qsplit.txt");
		List<String> result=new ArrayList<>();
		for(String sen:sens){
			List<String> temp=GenerateTemplate(sen, null);
			if(temp!=null)
				result.addAll(temp);
		}
		MyUtil.writeFile(result, "F://地理相关/试题拆分/Allsplit.txt");
	}
	
	public static List<String> GenerateTemplate(String question,List<String> locWords) throws IOException {
		//if (question.indexOf("@") == -1) question = "@" + question;
		question=question.replace("\t", "@");
		//除去句子中的空字符
		question=question.replaceAll(" |\t", "");
		List<String> result=new ArrayList<>();
		TimuInfo topic = new TimuInfo(question,locWords);
		
		List<List<String>> allList = new ArrayList<>();
		List<NLPTemplate> tst1=Generate.GenerateFirstTemplate(topic, allList);
		if(tst1.size()==0){
			result.add(topic.question);
		}else{
			for(int i=0;i<tst1.size();i++){
				NLPTemplate template=tst1.get(i);
				List<String> seclist=i<allList.size()?allList.get(i):new ArrayList<>();
				if(seclist.size()!=0){
					String str=MyUtil.tagstrToOriStr(seclist.get(0));
					result.add(str);
				}
			}
		}
		return result.size()>0?result:null;
	}
	
	
	public static void print_split(List<String> qlist) throws IOException{
		for(String question : qlist){
			question=question.replace("\t", "@");
			//除去句子中的空字符
			question=question.replaceAll(" |\t", "");
			List<String> result=new ArrayList<>();
			TimuInfo topic = new TimuInfo(question,null);
			System.out.println(topic.preSplit);
		}
	}
	//获取多句话是否要拆分的结果
	public static void successiveSplitLabel() throws IOException{
		String[] array = {
				"上海比北京@去年气温高，降水量大",
				"图中河流@a为内流河，b为外流河",
				"为防止艾比湖继续萎缩，在该湖流域应采取的措施是@修建水库，调节径流",
				"据图推断@甲区多公共服务设施，靠近住宅区", 
				"据图推断@乙是位于郊区的高新技术产业园区", 
				"据图推断@丙区商业网点等级低，服务半径小", 
				"据图推断@丁能耗昼夜差异大，为中心商务区", 
				"图示区域内@地形复杂多样，风能资源丰富", 
				"图示区域内@水田农业为主，水稻播种面积大", 
				"图示区域内@国道以西公路建设难度小，路网密度大",
				"图示区域内@南部水资源丰富，可跨流域向北部调水"
				};
		for(String sen : array){
			TimuInfo t = new TimuInfo(sen, null);
			System.out.println(t.preSplit);
		}
	}
}
