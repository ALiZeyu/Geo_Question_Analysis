package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.edu.nju.ws.GeoScholar.common.NLPResult;
import cn.edu.nju.ws.GeoScholar.common.NLPTemplate;
import cn.edu.nju.ws.GeoScholar.templating.common.DepTree;
import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.NerRecognition;
import cn.edu.nju.ws.GeoScholar.templating.common.QuestionTemplateFromNLP;
import cn.edu.nju.ws.GeoScholar.templating.common.Segment;
import cn.edu.nju.ws.GeoScholar.templating.common.SlotStructureFromNLP;
import cn.edu.nju.ws.GeoScholar.templating.common.SyntaxParser;
import cn.edu.nju.ws.GeoScholar.templating.common.TimuInfo;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/**
 * 本类提供了一系列接口，用于获取自然语言处理后的，包括词法分析、句法分析、语义模板分析在内的结果
 * @author 王韶杰
 * @author 李泽宇
 * */

public class Generate {
	private static final Logger LOGGER = Logger.getLogger(Generate.class);
	private static final boolean DEBUG = false;

	static class Template {
		int type;
		String template = "";

		public Template(int type, String template, String sentence) throws IOException {
			this.type = type;
			//如果是实体信息陈述，需要做一些后处理
			if (type == 13) {
				String[] s = template.split("@");
				if (s.length > 3)
					if (sentence.contains(s[2] + s[1]))
						this.template = s[0] + "@" + s[2] + s[1] + "@" + s[3];
					else
						this.template = s[0] + "@" + s[1] + "@" + s[2] + s[3];
				else
					this.template = template;
			} else {
				this.template = template;
			}
		}

	}
	
	/**
	 * 加载词法分析、句法分析的模型
	 * */
	public static void init() throws IOException {
		Segment.init();
		SyntaxParser.init();
		NerRecognition.init();
	}
	
//	public static void main(String[] args) throws IOException {
//		//init();
//		//successiveProcess();
//		//Process();
//		//getParsingTree();
//	}
	
	/**对于选项中有逗号的问题，判断需不需要拆分成两个子句
	 * @param question
	 * 			试题问句
	 * @param locWords
	 * 			地名词典
	 * */
//	public static boolean splitOrNot(String question,List<String> locWords) throws IOException {
//		question = question.replace("\t", "@");
//		if (question.indexOf("@") == -1) question = "@" + question;
//		//除去句子中的空字符
//		question=question.replaceAll(" |\t", "");
//		List<String> result=new ArrayList<>();
//		
//		TimuInfo topic = new TimuInfo(question,locWords);
//		
//		List<List<String>> allList = new ArrayList<>();
//		List<NLPTemplate> tst1=GenerateFirstTemplate(topic, allList);
//		if(tst1.size()>0) return true;
//		return false;
//	}
	
	public static List<NLPResult> GenerateTemplate(List<String> list, String question,List<String> locWords) throws IOException {
		if (DEBUG) System.out.println(question);
		
		question = question.replace("\t", "@");
		if (question.indexOf("@") == -1) question = "@" + question;
		//除去句子中的空字符
		question=question.replaceAll(" |\t", "");
		List<NLPResult> result=new ArrayList<>();
		//处理题干中的否定词，题干中如果含有nwords中的词，那么否定设为true，并替换为rwords中对应的词
		boolean negative=false;
		String[] array=question.split("@");
		String[] nwords={"不符合","不相关","不正确","不够合理","不合理","不包括","不可能","不是"};
		String[] rwords={"符合","相关","正确","合理","合理","包括","","是"};
		for(int i=0;i<nwords.length;i++){
			if(array[0].contains(nwords[i])){
				if(i!=1||(i==1 && array[0].contains("的") && array[0].contains("是"))){
					negative=true;
					array[0]=array[0].replace(nwords[i], rwords[i]);
					question=array[0]+"@"+array[1];
				}
			}
		}
		
		TimuInfo topic = new TimuInfo(question,locWords);
		list.add(topic.allTag);
		list.add(topic.allTree.toString());
		
		List<List<String>> allList = new ArrayList<>();
		List<NLPTemplate> tst1=GenerateFirstTemplate(topic, allList);
		if(tst1.size()==0){
			//如果不存在一级模板或者存在模板但是没有填槽
			NLPResult nlpResult=new NLPResult(negative, topic.thText, topic.sxwText);
			nlpResult.superTemplates = null;
			nlpResult.targetSentence = question.replace("@", "");
			nlpResult.subTemplates = GenerateSecondTemplate(question,null,locWords);
			nlpResult.targetTree = nlpResult.subTemplates.getSyntaxTree();
			if(nlpResult.subTemplates.templates.size()>1){
				result.addAll(Candidate.chooseCandidate(nlpResult));
			}else{
				result.add(nlpResult);
			}
		}
		else{
			//存在一级模板，看是否存在选项问句。TODO 处理二级模板多个的情况
			//topic.preSplit = false;//如果存在一级模板，那么不拆分
			for(int i=0;i<tst1.size();i++){
				NLPTemplate template=tst1.get(i);
				List<String> seclist=i<allList.size()?allList.get(i):new ArrayList<>();
				NLPResult nlpResult=new NLPResult(negative, topic.thText, topic.sxwText);
				nlpResult.superTemplates=template;
				if(seclist.size()!=0){
					String str=seclist.get(0);
					TimuInfo timu = new TimuInfo(str, topic.preSplit);
					if(timu.xxType.equals("IP")){
						nlpResult.subTemplates=GenerateSecondTemplate("", timu,locWords);
						nlpResult.targetTree=TimuInfo.getParseingTree(str);
						nlpResult.targetSentence=MyUtil.tagstrToOriStr(str);
					}
					else{
						nlpResult.subTemplates=null;
						nlpResult.targetSentence=question.replace("@", "");
					}
				}else{
					nlpResult.subTemplates=null;
					nlpResult.targetSentence=question.replace("@", "");
				}
				result.add(nlpResult);
			}
		}
		return result;
	}
	
	/**获取nlp处理结果，先看一级模板有没有结果，没有的话直接获取二级模板，否则获取选项问句的nlp结果
	 * @param	question
	 * 			问题文本
	 * @param	locWords
	 * 			地名词典
	 * @return	试题文本的nlp处理结果
	 * */
	public static List<NLPResult> GenerateTemplate(String question,List<String> locWords) throws IOException {
		if (DEBUG) System.out.println(question);
		
		question = question.replace("\t", "@");
		if (question.indexOf("@") == -1) question = "@" + question;
		//除去句子中的空字符
		question=question.replaceAll(" |\t", "");
		List<NLPResult> result=new ArrayList<>();
		//处理题干中的否定词，题干中如果含有nwords中的词，那么否定设为true，并替换为rwords中对应的词
		boolean negative=false;
		String[] array=question.split("@");
		String[] nwords={"不符合","不相关","不正确","不够合理","不合理","不包括","不可能","不是"};
		String[] rwords={"符合","相关","正确","合理","合理","包括","","是"};
		for(int i=0;i<nwords.length;i++){
			if(array[0].contains(nwords[i])){
				if(i!=1||(i==1 && array[0].contains("的") && array[0].contains("是"))){
					negative=true;
					array[0]=array[0].replace(nwords[i], rwords[i]);
					question=array[0]+"@"+array[1];
				}
			}
		}
		
		TimuInfo topic = new TimuInfo(question,locWords);
		
		List<List<String>> allList = new ArrayList<>();
		List<NLPTemplate> tst1=GenerateFirstTemplate(topic, allList);
		if(tst1.size()==0){
			//如果不存在一级模板或者存在模板但是没有填槽
			NLPResult nlpResult=new NLPResult(negative, topic.thText, topic.sxwText);
			nlpResult.superTemplates = null;
			nlpResult.targetSentence = question.replace("@", "");
			nlpResult.subTemplates = GenerateSecondTemplate(question,null,locWords);
			nlpResult.targetTree = nlpResult.subTemplates.getSyntaxTree();
			if(nlpResult.subTemplates.templates.size()>1){
				result.addAll(Candidate.chooseCandidate(nlpResult));
			}else{
				result.add(nlpResult);
			}
		}
		else{
			//存在一级模板，看是否存在选项问句。TODO 处理二级模板多个的情况
			//topic.preSplit = false;//如果存在一级模板，那么不拆分
			for(int i=0;i<tst1.size();i++){
				NLPTemplate template=tst1.get(i);
				List<String> seclist=i<allList.size()?allList.get(i):new ArrayList<>();
				NLPResult nlpResult=new NLPResult(negative, topic.thText, topic.sxwText);
				nlpResult.superTemplates=template;
				if(seclist.size()!=0){
					String str=seclist.get(0);
					TimuInfo timu = new TimuInfo(str, topic.preSplit);
					if(timu.xxType.equals("IP")){
						nlpResult.subTemplates=GenerateSecondTemplate("", timu,locWords);
						nlpResult.targetTree=TimuInfo.getParseingTree(str);
						nlpResult.targetSentence=MyUtil.tagstrToOriStr(str);
					}
					else{
						nlpResult.subTemplates=null;
						nlpResult.targetSentence=question.replace("@", "");
					}
				}else{
					nlpResult.subTemplates=null;
					nlpResult.targetSentence=question.replace("@", "");
				}
				result.add(nlpResult);
			}
		}
		return result;
	}
	
	/**获取试题文本的一级模板的结果
	 * @param	topic
	 * 			问题文本及其词法分析、句法分析结果
	 * @param	alllist
	 * 			初始为空，用于返回选项问句
	 * @return	试题文本的一级模板
	 * */
	public static List<NLPTemplate> GenerateFirstTemplate(TimuInfo topic, List<List<String>> alllist) throws IOException {
		//ArrayList<NlpOutput> l = new ArrayList<NlpOutput>();
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList) topic.allTagList;
		// 为了确保触发词在题干或选项中
		int bound = topic.tgTag.length()==0?-1:topic.tgTag.split(" ").length;
		String q = topic.question;
		Set<Integer> type = TemplateClassify.Firstclassify(t, sentence, bound);
		List<Template> templates = new ArrayList<Template>();
		for (int i : type) {
			try {
				String s = "";
				List<String> seclist=new ArrayList<>();
				switch (i) {
				case 1:
					s = YuanYinTemplate.getTemplate(topic,seclist);
					if (!s.isEmpty()) {
						templates.add(new Template(i, s, q));
						alllist.add(seclist);
					}
					break;
				case 2:
					s = HouGuoTemplate.getTemplate(topic,seclist);
					if (!s.isEmpty()) {
						templates.add(new Template(i, s, q));
						alllist.add(seclist);
					}
					break;
				case 3:
					s = YingXiangTemplate.getTemplate(topic,seclist);
					if (!s.isEmpty()){
						templates.add(new Template(i, s, q));
						alllist.add(seclist);
					}
					break;
				case 4:
					s = CuoShiTemplate.getTemplate(topic,seclist);
					if (!s.isEmpty()){
						templates.add(new Template(i, s, q));
						alllist.add(seclist);
					}
					break;
				default:
					s = QiTaGuanLian.getTemplate(topic,seclist);
					if (!s.isEmpty()){
						templates.add(new Template(-1, s, q));
						alllist.add(seclist);
					}
					break;
				}
			} catch (Exception e) {
				System.out.println("generate160"+topic.origQuestion);
				LOGGER.warn(String.format("templating error: %s", e.getMessage()));
			}
		}
		
		
		List<NLPTemplate> result=new ArrayList<>();
		// 什么模板都不对那就一般陈述
		if (templates.isEmpty()) {
			return result;
		} else{
			//TODO 目前没有处理一句话有两个相同类型的一级模板的情况，如果有两个模板类型不一样，那么就分开
			for (Template template : templates) {
				
				NLPTemplate nl = new NLPTemplate();
				String segStr=MyUtil.listToSegStr(topic.allTagList);
				nl.OriginalText=topic.tgText+topic.xxText;
				nl.pos = new ArrayList<String>();
				nl.pos.addAll(sentence);
				nl.syntaxTree = t;
				nl.times=NerRecognition.getTime(nl.OriginalText);
				nl.geoTerms=Segment.getTerm(nl.OriginalText);
				nl.locations=NerRecognition.getLocation(segStr);
				nl.templates = new ArrayList<QuestionTemplateFromNLP>();
				
				String[] temp = template.template.split("\t");
				//如果含有多个同一类型的高阶模板，只要一个就行了
				int count=0;
				for (String s : temp) {
					if (s.contains("+@")) continue;
					if (count==1) break;
					QuestionTemplateFromNLP geo = new QuestionTemplateFromNLP();
					geo.oriText = topic.tgText + "@" + topic.xxText;
					geo.syntaxTreeLeaves = Tree.getLeaves(t);
					switch (template.type) {
						case 1:
							geo.templateType = "原因";
							geo.slotCount = 2;
							break;
						case 2:
							geo.templateType = "后果";
							geo.slotCount = 2;
							break;
						case 4:
							geo.templateType = "对策";
							geo.slotCount = 3;
							break;
						case 3:
							geo.templateType = "影响";
							geo.slotCount = 4;
							break;
						default:
							geo.templateType = "其他关联";
							geo.slotCount = 3;
							break;
					}
					if(s.split("\\+").length!=2)
						continue;
					geo.cueWord = Integer.parseInt(s.split("\\+")[0]);
					s = s.split("\\+")[1];
					geo.slots = new ArrayList<SlotStructureFromNLP>();
					String[] slots = s.split("@");
					for (String slot : slots) {
						if (slot.isEmpty()) {
							geo.slots.add(null);
						} else {
							SlotStructureFromNLP aslot = new SlotStructureFromNLP();
							aslot.content = slot;
							int start = -1, end = -1;
							for (int i = 0; i < sentence.size(); i++)
								if (slot.startsWith(sentence.get(i).split("_")[0])) {
									start = i;
									int j = i;
									while (j < sentence.size() && !slot.endsWith(sentence.get(j).split("_")[0])) {
										while (j < sentence.size() && slot.contains(sentence.get(j).split("_")[0]))
											j++;
										j--;
										if (slot.endsWith(sentence.get(j).split("_")[0]))
											end = j;
										else
											j += 2;
									}
									if (j == sentence.size())
										end = j - 1;
									else
										end = j;
									break;
								}
							aslot.startOffset = start;
							aslot.endOffset = end;
							aslot.syntaxNodes = new ArrayList<Tree>();
							aslot.depNodes = new ArrayList<DepTree>();
							for (int i = start + 1; i <= end + 1; i++) {
								aslot.syntaxNodes.add(Tree.findNodeByNo(t, i));
							}
							geo.slots.add(aslot);
							// System.out.print(slot + " ");
						}
					}
					if (slots.length < geo.slotCount)
						for (int i = 0; i < geo.slotCount - slots.length; i++)
							geo.slots.add(null);
					nl.templates.add(geo);
					count++;
				}
				if(nl.templates.size()!=0)
					result.add(nl);
			}
		}
		return result;
	}
	
	/**获取试题文本的二级模板的结果
	 * @param	question
	 * 			问题文本
	 * @param	timu
	 * 			选项问句的词法分析、句法分析结果
	 * @param	locWords
	 * 			地名词典
	 * @return	试题文本的二级模板
	 * */
	public static NLPTemplate GenerateSecondTemplate(String question, TimuInfo timu, List<String> locWords) throws IOException {
		if (DEBUG) System.out.println(question);
		TimuInfo topic;
		if(timu!=null){
			question="@" + timu.question;
			topic=timu;
			//如果存在一级模板，那么就不管选项拆分的问题了
			topic.preSplit=false;
		}else {
			question = question.replace("\t", "@");
			if (question.indexOf("@") == -1) {
				question = "@" + question;
			}
			topic = new TimuInfo(question,locWords);
		}
		 
		String[] te = question.split("@");
		Tree t = topic.allTree;
		ArrayList<String> sentence = (ArrayList) topic.allTagList;
		// 为了确保触发词在题干或选项中
		int bound = topic.tgTag.length()==0?-1:topic.tgTag.split(" ").length;
		String q = topic.question;
		Set<Integer> type = TemplateClassify.Secondclassify(t, sentence, bound);
		List<Template> templates = new ArrayList<Template>();
		for (int i : type) {
			try {
				String s = "";
				switch (i) {
				case 5:
					s = ZhiShiTemplate.getTemplate(topic);
					if (!s.isEmpty())
						templates.add(new Template(i, s, q));
					break;
//					s = PaiXuTemplate.getTemplate(t, sentence);
//					if (!s.isEmpty())
//						templates.add(new Template(i, s, q));
//					break;
				case 6:
					s = BijiaoTemplate.getTemplate(topic);
					if (!s.isEmpty()){
						templates.add(new Template(i, s, q));
						//System.out.println(topic.origQuestion);
					}
					break;
				case 7:
					s = BianhuaTemplate.getTemplate(topic);
					if (!s.isEmpty())
						templates.add(new Template(i, s, q));
					break;
				case 8:
					s = YinsuTemplate.getTemplate(topic);
					if (!s.isEmpty())
						templates.add(new Template(i, s, q));
					break;
				case 9:
					s = YundongTemplate.getTemplate(topic);
					if (!s.isEmpty())
						templates.add(new Template(i, s, q));
					break;
				case 10:
					//构成
					s = GouChengTemplate.getTemplate(topic);
					if (!s.isEmpty())
						templates.add(new Template(i, s, q));
					break;
				case 11:
					s = FenbuTemplate.getTemplate(topic);
					if (!s.isEmpty())
						templates.add(new Template(i, s, q));
					break;
//					s = WeiyuTemplate.getTemplate(t, sentence);
//					if (!s.isEmpty())
//						templates.add(new Template(i, s, q));
//					break;
				case 12:
					s = YingXiangTemplate2.getTemplate(topic, null);
					if (!s.isEmpty())
						templates.add(new Template(i, s, q));
					break;
				default:
					break;
				}
			} catch (Exception e) {
				System.out.println("generate345"+topic.origQuestion);
				LOGGER.warn(String.format("templating error: %s", e.getMessage()));
			}
		}
		//丁为中心商务区，能耗昼夜差异大，像这种句子有了指示(丁，中心商务区)，后面的部分就没有了，因此需要处理并列句的问题
		if(templates.size()==1){
			String str = templates.get(0).template;
			if(!str.contains("\t")){
				List<Integer> cover=MyUtil.getRange(str, sentence);
				int b = cover.get(0),e=cover.get(1);
				if(b>bound&&b-1>0&&sentence.get(b-1).equals("，_PU")){
				//	System.out.println("缺少陈述"+sentence);
					String s = ChenShuTemplate.getTemplate(topic);
					String[] temp = s.split("\t");
					if(temp.length==2){
						for(String tt : temp){
							int end=MyUtil.getRange(tt, sentence).get(1);
							if(end==b-2){
								templates.add(new Template(13, tt, q));
								break;
							}
						}
					}
				}else if(e>bound && e+1<sentence.size() && sentence.get(e+1).equals("，_PU")){
				//	System.out.println("缺少陈述"+sentence);
					String s = ChenShuTemplate.getTemplate(topic);
					String[] temp = s.split("\t");
					//与里约热内卢同纬度的大陆西岸@地处板块内部，大陆西岸地壳稳定:这句话其他陈述只有【-1+大陆西岸@地壳@稳定】但正是需要的
					//if(temp.length==2){
						for(String tt : temp){
							int begin=MyUtil.getRange(tt, sentence).get(0);
							if(begin==e+2){
								templates.add(new Template(13, tt, q));
								break;
							}
						}
					//}
				}
			}
		}
		//暂时定为如果没有以上类型的模板，就尝试实体信息陈述，再不行就其他陈述
		if(templates.isEmpty()){
			String s = ChenShuTemplate.getTemplate(topic);
			if (!s.isEmpty()) {
				String[] st = s.split("\t");
				for (String st1 : st)
					templates.add(new Template(13, st1, q));
			}
		}
		//纯分词结果，用于分析依存句法树和地点
		String segStr=MyUtil.listToSegStr(topic.allTagList);
		NLPTemplate nl = new NLPTemplate();
		nl.OriginalText=topic.tgText+topic.xxText;
		nl.pos = new ArrayList<String>();
		nl.pos.addAll(sentence);
		nl.syntaxTree = t;
		//nl.dependencyTree = Depparser.depparse(segStr);
		nl.times=NerRecognition.getTime(nl.OriginalText);
		nl.geoTerms=Segment.getTerm(nl.OriginalText);
		nl.locations=NerRecognition.getLocation(segStr);
		nl.templates = new ArrayList<QuestionTemplateFromNLP>();
		// 什么模板都不对那就一般陈述
		for (Template template : templates) {
			String[] temp = template.template.split("\t");
			for (String s : temp) {
				if (s.contains("+@"))
					continue;
				QuestionTemplateFromNLP geo = new QuestionTemplateFromNLP();
				geo.oriText = te[0] + "@" + (te.length > 1 ? te[1] : "");
				geo.syntaxTreeLeaves = Tree.getLeaves(t);
				switch (template.type) {
				case 5:
					geo.templateType = "指示";
					geo.slotCount = 3;
					break;
				case 6:
					geo.templateType = "比较";
					geo.slotCount = 5;
					break;
				case 7:
					geo.templateType = "变化";
					geo.slotCount = 3;
					break;
				case 8:
					geo.templateType = "因果关联";
					geo.slotCount = 2;
					break;
				case 9:
					geo.templateType = "运动";
					geo.slotCount = 3;
					break;
				case 10:
					geo.templateType = "构成";
					geo.slotCount = 3;
					break;
				case 11:
					geo.templateType = "分布";
					geo.slotCount = 2;
					break;
				case 12:
					geo.templateType = "影响";
					geo.slotCount = 4;
					break;
				default:
					geo.templateType = "其他陈述";
					geo.slotCount = 3;
					break;
				}
				if(s.split("\\+").length!=2)
					continue;
				geo.cueWord = Integer.parseInt(s.split("\\+")[0]);
				s = s.split("\\+")[1];
				geo.slots = new ArrayList<SlotStructureFromNLP>();
				String[] slots = s.split("@");
				List<Integer> range=MyUtil.getRange(s, sentence);
				for (String slot : slots) {
					if (slot.isEmpty()) {
						geo.slots.add(null);
					} else {
						SlotStructureFromNLP aslot = new SlotStructureFromNLP();
						aslot.content = slot;
						int start = -1, end = -1;
						for (int i = 0; i < sentence.size(); i++)
							if (slot.startsWith(sentence.get(i).split("_")[0]) && i>=range.get(0)) {
								start = i;
								int j = i;
								while (j < sentence.size() && !slot.endsWith(sentence.get(j).split("_")[0])) {
									while (j < sentence.size() && slot.contains(sentence.get(j).split("_")[0]))
										j++;
									j--;
									if (slot.endsWith(sentence.get(j).split("_")[0]))
										end = j;
									else
										j += 2;
								}
								if (j == sentence.size())
									end = j - 1;
								else
									end = j;
								break;
							}
						aslot.startOffset = start;
						aslot.endOffset = end;
						aslot.syntaxNodes = new ArrayList<Tree>();
						aslot.depNodes = new ArrayList<DepTree>();
						for (int i = start + 1; i <= end + 1; i++) {
							aslot.syntaxNodes.add(Tree.findNodeByNo(t, i));
							//aslot.depNodes.add(DepTree.findNodeByNo(nl.dependencyTree,i));
						}
						geo.slots.add(aslot);
						// System.out.print(slot + " ");
					}
				}
				if (slots.length < geo.slotCount)
					for (int i = 0; i < geo.slotCount - slots.length; i++)
						geo.slots.add(null);
				nl.templates.add(geo);
			}
		}
		//除去模板范围只在题干中的，比如“该企业负责人说“在北京呆了十多年，成本越来越高”，此成本主要指	劳动力和交通”的“变化(成本,null,越来越高)”
		if(nl.templates.size()>1){
			Iterator<QuestionTemplateFromNLP> it=nl.templates.iterator();
			while(it.hasNext()){
				QuestionTemplateFromNLP qt=it.next();
				int end=MyUtil.getRightBound(qt);
				if(end<bound)
					it.remove();
			}
		}
		
		if (nl.templates.size()==0) {
			QuestionTemplateFromNLP geo = new QuestionTemplateFromNLP();
			geo.oriText = te[0] + "@" + (te.length > 1 ? te[1] : "");
			geo.syntaxTreeLeaves = Tree.getLeaves(t);
			geo.templateType = "其他陈述";
			geo.slotCount = 1;
			geo.cueWord = -3;
			geo.slots = new ArrayList<SlotStructureFromNLP>();

			SlotStructureFromNLP aslot = new SlotStructureFromNLP();
			aslot.content = geo.oriText;
			aslot.startOffset = 0;
			aslot.endOffset = sentence.size() - 1;
			aslot.syntaxNodes = new ArrayList<Tree>();
			aslot.depNodes = new ArrayList<DepTree>();
			for (int i = aslot.startOffset + 1; i <= aslot.endOffset + 1; i++) {
				aslot.syntaxNodes.add(Tree.findNodeByNo(t, i));
				//aslot.depNodes.add(DepTree.findNodeByNo(nl.dependencyTree, i));
			}
			geo.slots.add(aslot);
			nl.templates.add(geo);
		}
		
		
		return nl;
	}
}
