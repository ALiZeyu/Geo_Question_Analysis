package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.nju.ws.GeoScholar.common.NLPResult;
import cn.edu.nju.ws.GeoScholar.templating.common.QuestionTemplateFromNLP;
import cn.edu.nju.ws.GeoScholar.templating.common.SlotStructureFromNLP;
/*
 * 目前的想法是两个模板有覆盖那么就是候选，没有覆盖就是组合。
 * 但是如果类型相同有可能是分别是、分别位于的情况，如果类型相同，不是完全覆盖，那么归为组合比较合适，如果后期加入拆分，那么就必须重新思考
 * */
public class Candidate {
	//用于看模板覆盖的范围
	private static class Check {
		int start;
		int end;
		
		public Check(QuestionTemplateFromNLP qt) {
			start = 99;
			end = -1;
			for (SlotStructureFromNLP ss : qt.slots) 
				if (ss != null) {
					if (ss.startOffset!=-1 && ss.startOffset < start)
						start = ss.startOffset;
					if (ss.endOffset > end)
						end = ss.endOffset;
				}
		}
		public String toString(){
			return start+"-->"+end;
		}
	}
	
	//根据模板的类型、第一个词、线索词判重
	private static class Type {
		String type;
		String first;
		int cueword;
		
		public Type(String t, String f, int c) {
			type = t;
			first = f;
			cueword = c;
		}
		
		public boolean equals(Type t) {
			return type.equals(t.type) && (first.equals(t.first) || (cueword > 0 && cueword == t.cueword));
		}
	}
	
	public static List<NLPResult> chooseCandidate(NLPResult nl) {
		List<NLPResult> result=new ArrayList<>();
		List<Check> l = new ArrayList<Check>();
		for (QuestionTemplateFromNLP qt : nl.subTemplates.templates)
			l.add(new Check(qt));
		
		sortByCover(nl, l);
		remove(nl, l);
		//删除后只剩一个模板，直接返回
		if(l.size()==1){
			result.add(nl);
			return result;
		}
		//满足特殊情况，返回
		if(special(nl)){
			result.add(nl);
			return result;
		}
		
		//此时nl、l排序完毕，list是一个个模板范围重合的模板集合
		List<QuestionTemplateFromNLP> subtemplates=nl.subTemplates.templates;
		List<List<QuestionTemplateFromNLP>> list = new ArrayList<List<QuestionTemplateFromNLP>>();
		for (int i = 0; i < l.size() - 1; i++)  {
			Check ci = l.get(i);
			for (int j = i + 1; j < l.size(); j++) {
				Check cj = l.get(j);
				if (!(cj.start > ci.end || ci.start > cj.end) && subtemplates.get(i).cueWord != subtemplates.get(j).cueWord) {
					//1、二者触发词不同 2、二者覆盖重合；那么这二者应该放到一个list里面
					boolean b = true;
					for (int k = 0; k < list.size(); k++) {
						//pair里面已经有了其中一个a，那么再填进去另一个，如果两个都有了就跳过
						if (list.get(k).contains(subtemplates.get(i)) && !list.get(k).contains(subtemplates.get(j))) { 
							list.get(k).add(subtemplates.get(j));
							b = false;
						} else if (!list.get(k).contains(subtemplates.get(i)) && list.get(k).contains(subtemplates.get(j))) { 
							list.get(k).add(subtemplates.get(i));
							b = false;
						} else if (list.get(k).contains(subtemplates.get(i)) && list.get(k).contains(subtemplates.get(j))) {
							b = false;
						}
					}
					if (b) {
						List<QuestionTemplateFromNLP> temp = new ArrayList<QuestionTemplateFromNLP>();
						temp.add(subtemplates.get(i));
						temp.add(subtemplates.get(j));
						list.add(temp);
					}
				}
			}
		}
		//ArrayList<NLPResult> nllist = new ArrayList<NlpOutput>();
		if (list.isEmpty())
			//如果模板没什么重合，不作处理
			result.add(nl);
		else {
			List<QuestionTemplateFromNLP> common = new ArrayList<QuestionTemplateFromNLP>();
			Set<QuestionTemplateFromNLP> same = new HashSet<QuestionTemplateFromNLP>();
			//list中含有的，即相互之间有覆盖的叫做same.除此之外的叫做common即每个候选里面都要有的
			for (List<QuestionTemplateFromNLP> temp : list)
				for (QuestionTemplateFromNLP qt : subtemplates)
					if (temp.contains(qt))
						same.add(qt);
			for (QuestionTemplateFromNLP qt : subtemplates)
				if (!same.contains(qt))
					common.add(qt);
			//对有覆盖的两两组合，即{1,2}{3,4}->{1,3}{1,4}{2,3}{2,4}
			Map<Integer, Type> typelist = new HashMap<Integer, Type>();
			for (QuestionTemplateFromNLP qt : list.get(0)) {
				//没有模板第一个槽为空，为空应该是出错了
				if (qt.slots.get(0) == null) {
					list.get(0).remove(qt);
					continue;
				}
				//这个map是为了处理拆分之后的句子，“北京比伦敦风小，雾大”生成的两个模板类型，第一个槽、触发词都一样，因此应该作为并列而不是两个候选。
				Type type = new Type(qt.templateType, qt.slots.get(0).content, qt.cueWord);
				boolean b = true;
				for (int i : typelist.keySet())
					if (typelist.get(i).equals(type)) {
						result.get(i).subTemplates.templates.add(qt);
						b = false;
						break;
					}
				if (b) {
					NLPResult nlr=new NLPResult(nl);
					nlr.subTemplates.templates.add(qt);
					result.add(nlr);
					typelist.put(result.indexOf(nlr), new Type(qt.templateType, qt.slots.get(0).content, qt.cueWord));
				}
			}
			
			for (int i = 1; i < list.size(); i++) {
				System.out.println("多个候选"+nl.targetSentence);
				List<NLPResult> tempnllist = new ArrayList<>();
				typelist = new HashMap<Integer, Type>();
				for (QuestionTemplateFromNLP qt : list.get(i)) {
					if (qt.slots.get(i) == null) {
						list.get(i).remove(qt);
						continue;
					}
					Type type = new Type(qt.templateType, qt.slots.get(0).content, qt.cueWord);
					boolean b = true;
					for (int j : typelist.keySet())
						if (typelist.get(j).equals(type)) {
							tempnllist.get(j).subTemplates.templates.add(qt);
							b = false;
							break;
						}
					if (b) {
						NLPResult nlt = new NLPResult();
						nlt.subTemplates.templates = new ArrayList<QuestionTemplateFromNLP>();
						nlt.subTemplates.templates.add(qt);
						tempnllist.add(nlt);
						typelist.put(tempnllist.indexOf(nlt), new Type(qt.templateType, qt.slots.get(0).content, qt.cueWord));
					}
				}
				int size = result.size();
				for (int j = 0; j < tempnllist.size() - 1; j++)
					for (int k = 0; k < size; k++) {
						NLPResult no = result.get(k);
						NLPResult nlt = new NLPResult(no);
						nlt.subTemplates.templates.addAll(no.subTemplates.templates);
						result.add(nlt);
					}
				for (int j = 0; j < result.size(); j += size)
					for (int k = j; k < j + size; k++)
						result.get(k).subTemplates.templates.addAll(tempnllist.get(k / size).subTemplates.templates);
			}
			//最后处理，把common加到每个候选里面
			for (NLPResult no : result)
				no.subTemplates.templates.addAll(common);
		}
		
		
		
		//如果实体信息陈述第二个槽为空，那么取上一个填槽结果的第一个槽填上去，没找到例子先不管
//		for (NlpOutput no : nllist) {
//			for (int i = 1; i < no.templates.size(); i++) {
//				QuestionTemplateFromNLP qt  = no.templates.get(i);
//				if (qt.templateType.equals("实体信息陈述") && qt.slots.get(1) == null && !no.templates.get(i - 1).templateType.equals("时间限定")) {
//					qt.slots.set(1, qt.slots.get(0));
//					qt.slots.set(0, no.templates.get(i - 1).slots.get(0));
//				}
//			}
//		}
		/*for (NlpOutput no : nllist) {
			for (QuestionTemplateFromNLP qt : no.templates)
				for (SlotStructureFromNLP ss : qt.slots)
					checkRepeat(ss);
		}*/
		//如果第一个候选是匹配，那么放到最后一个，不知道是干啥的
//		if (nllist.get(0).templates.get(0).templateType.equals("匹配")) {
//			NlpOutput no = nllist.get(0);
//			for (int i = 0; i < nllist.size() - 1; i++)
//				nllist.set(i, nllist.get(i + 1));
//			nllist.set(nllist.size() - 1, no);
//		}
		//如果第一个候选是实体信息陈述，那么放到最后一个，不知道是干啥的
//		if (nllist.get(0).templates.get(0).templateType.equals("实体信息陈述")) {
//			NlpOutput no = nllist.get(0);
//			for (int i = 0; i < nllist.size() - 1; i++)
//				nllist.set(i, nllist.get(i + 1));
//			nllist.set(nllist.size() - 1, no);
//		}
		
		//如果有因果就排在第一位，不知道是干啥的
//		for (NlpOutput no : nllist) {
//			if (no.templates.get(0).templateType.equals("因果")) {
//				NlpOutput nlp = nllist.get(0);
//				int i = nllist.indexOf(no);
//				nllist.set(0, no);
//				nllist.set(i, nlp);
//				
//			}
//		}
		//时间限定模板已经去掉了，因此这个循环不用了
//		for (NlpOutput no : nllist)
//			for (int j = 1; j < no.templates.size(); j++) {
//			//for (QuestionTemplateFromNLP qt : no.templates) {
//				QuestionTemplateFromNLP qt = no.templates.get(j);
//				if (qt.templateType.equals("时间限定")) {
//					int i = 0;
//					while (no.templates.get(i).templateType.equals("时间限定")) i++;
//					if (i < j) {
//						QuestionTemplateFromNLP qt1 = no.templates.get(i);
//						no.templates.set(i, qt);
//						no.templates.set(j, qt1);
//					}
//				}
//		}
		return result;
	}
	
	/*有一些特殊情况，虽然模板之间有重叠，但仍然互为组合关系，目前有以下几种情况
	 * 1、所有模板都是指示，最后一个槽都是###.
	 * 2、所有模板都是一般陈述.
	 * 3、所有模板都是位于且句中含有
	 * 4、北京比伦敦风小，雾大，这类师兄特殊处理过，有需要的话再添加
	 * */
	public static boolean special(NLPResult nl){
		boolean flag=false;
		List<QuestionTemplateFromNLP> templates=nl.subTemplates.templates;
		String type=templates.get(0).templateType;
		if(type.equals("其他陈述")){
			for(QuestionTemplateFromNLP qt:templates){
				if(!qt.templateType.equals("其他陈述"))
					return false;
			}
			return true;
		}else if (type.equals("指示")) {
			for(QuestionTemplateFromNLP qt:templates){
				if(!(qt.templateType.equals("指示")&&qt.slots.size()==3&&qt.slots.get(2)!=null))
					return false;
			}
			return true;
		}else if (type.equals("分布")) {
			for(QuestionTemplateFromNLP qt:templates){
				if(!qt.templateType.equals("分布"))
					return false;
			}
			if(nl.targetSentence.contains("、"))
				return true;
		}else if (type.equals("比较")) {
			for(QuestionTemplateFromNLP qt:templates){
				if(!qt.templateType.equals("比较"))
					return false;
			}
			//除了A比B大，比C小，还有[该地区@①的岩石形成时间早于②，②的岩石形成时间晚于③]比较特殊的话
			//if(nl.targetSentence.contains("比"))
				return true;
		}
		return flag;
	}
	/**如果某个模板覆盖的范围是另一个模板的一部分，并且类型不同，那么就可以直接删除*/
	public static void remove(NLPResult nl,List<Check> l){
		List<QuestionTemplateFromNLP> templates=nl.subTemplates.templates;
		int size=templates.size();
		for(int i=size-1;i>=1;i--){
			int b=l.get(i).start;
			int e=l.get(i).end;
			for(int j=0;j<i;j++){
				if((b>=l.get(j).start&&e<=l.get(j).end)&&!(b==l.get(j).start&&e==l.get(j).end) && !templates.get(i).templateType.equals(templates.get(j).templateType)){
					//包含在其他模板中，把start，end设为-1，最后统一删除；
					l.get(i).start=-2;
					l.get(i).end=-2;
				}
			}
			
		}
		Iterator<Check> it=l.iterator();
		int count=0;
		while(it.hasNext()){
			Check temp=it.next();
			if(temp.start==-2 && temp.end==-2){
				it.remove();
				templates.remove(count);
				count--;
			}
			count++;
		}
		
	}
	
	public static void sortByCover(NLPResult nl,List<Check> l){
		//按模板的覆盖范围的开头start从小到大进行排序
		for (int i = 0; i < nl.subTemplates.templates.size() - 1; i++)
			for (int j = i; j < nl.subTemplates.templates.size(); j++)
				if (l.get(i).start > l.get(j).start) {
					Check ci = l.get(i);
					QuestionTemplateFromNLP qt = nl.subTemplates.templates.get(i);
					l.set(i, l.get(j));
					nl.subTemplates.templates.set(i, nl.subTemplates.templates.get(j));
					l.set(j, ci);
					nl.subTemplates.templates.set(j, qt);
				}
		//保证第一个模板开头最靠前，结尾最靠后，不会被删除
		if(l.get(0).start==l.get(1).start && l.get(0).end<l.get(1).end){
			Check ci = l.get(0);
			QuestionTemplateFromNLP qt = nl.subTemplates.templates.get(0);
			l.set(0, l.get(1));
			nl.subTemplates.templates.set(0, nl.subTemplates.templates.get(1));
			l.set(1, ci);
			nl.subTemplates.templates.set(1, qt);
		}
	}
	
	/*@SuppressWarnings("unchecked")
	private static void checkRepeat(SlotStructureFromNLP ss) {
		if (ss == null || !ss.isTemplate)
			return;
		if (((ArrayList<QuestionTemplateFromNLP>)ss.content).size() > 1) {
			List<QuestionTemplateFromNLP> l = new ArrayList<QuestionTemplateFromNLP>();
			for (QuestionTemplateFromNLP qt : ((ArrayList<QuestionTemplateFromNLP>)ss.content))
				if (qt.templateType.equals("实体信息陈述"))
					l.add(qt);
			if (l.size() < ((ArrayList<QuestionTemplateFromNLP>)ss.content).size())
				((ArrayList<QuestionTemplateFromNLP>)ss.content).removeAll(l);
			for (QuestionTemplateFromNLP qt : ((ArrayList<QuestionTemplateFromNLP>)ss.content))
				for (SlotStructureFromNLP ssf : qt.slots)
					checkRepeat(ssf);
		}
	}*/
}
