package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import cn.edu.nju.ws.GeoScholar.common.NLPTemplate;
import cn.edu.nju.ws.GeoScholar.templating.choice.Generate.Template;
import cn.edu.nju.ws.GeoScholar.templating.common.DepTree;
import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.NerRecognition;
import cn.edu.nju.ws.GeoScholar.templating.common.QuestionTemplateFromNLP;
import cn.edu.nju.ws.GeoScholar.templating.common.Segment;
import cn.edu.nju.ws.GeoScholar.templating.common.SlotStructureFromNLP;
import cn.edu.nju.ws.GeoScholar.templating.common.TimuInfo;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class GetTemplate {
	private static final Logger LOGGER = Logger.getLogger(GetTemplate.class);
	private static final boolean DEBUG = false;
	
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
		
		List<Template> templates = new ArrayList<Template>();
		//如果不拆分直接来，如果拆分，那么拆成两个句子分别获得模板并合起来，注意要替换触发词的下标。
		if(topic.preSplit==false){
			templates = getSecTemplate(topic, locWords);
		}else{
			List<TimuInfo> timus = getSplitSen(topic);
			//拆分过后句子的触发词和原来位置不一样，需要保持一致
			for(TimuInfo tm : timus){
				List<Template> temp = getSecTemplate(tm, locWords);
				for(Template sep_template : temp){
					int position =-1;
					try {
						position = Integer.valueOf(sep_template.template.split("+")[0]);
					} catch (Exception e) {
						System.out.println("the position of keyword is not a number");
					}
					if(position-1>0 && position-1<tm.allTagList.size()){
						String word = tm.allTagList.get(position);
						position = getIndex(topic.allTagList, word);
						String new_template = position+"+"+sep_template.template.split("+")[1];
						sep_template.template = new_template;
					}
				}
				templates.addAll(temp);
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
		//什么模板都配不上，其他陈述，拆分后的句子如果某一句话没有模板，全句话都是其他陈述吧2017-6-16.
		if (nl.templates.size()==0 || (topic.preSplit == true && nl.templates.size()<2)) {
			nl.templates = new ArrayList<>();
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
	
	public static List<Template> getSecTemplate(TimuInfo topic, List<String> locWords) throws IOException{
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
				case 6:
					s = BijiaoTemplate.getTemplate(topic);
					if (!s.isEmpty()){
						templates.add(new Template(i, s, q));
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
		return templates;
	}
	
	//将句子拆分成两句，分别得到一个TimuInfo对象，注意 TimuInfo(question,locWords)的输入是：题干@选项
	//中间的@很重要，因为要保证触发词的位置是在选项里面
	public static List<TimuInfo> getSplitSen(TimuInfo topic){
		List<TimuInfo> tis = new ArrayList<>();
		//if (question.indexOf("@") == -1) {
//			question = "@" + question;
//		}
//		topic = new TimuInfo(question,locWords);
		//注意 TimuInfo(question,locWords)的输入是：题干@选项
		return tis;
	}
	
	public static int getIndex(List<String> allTagList, String word){
		int p = -1;
		for(int i=0;i<allTagList.size();i++){
			if(word.equals(allTagList.get(i))){
				p=i;
				return p;
			}
		}
		return p;
	}
}
