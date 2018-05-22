package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.GeoScholar.templating.common.DepTree;
import cn.edu.nju.ws.GeoScholar.templating.common.Input;
import cn.edu.nju.ws.GeoScholar.templating.common.QuestionTemplateFromNLP;
import cn.edu.nju.ws.GeoScholar.templating.common.SlotStructureFromNLP;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class Nesting {

	@SuppressWarnings("unchecked")
	protected static List<QuestionTemplateFromNLP> nest(List<QuestionTemplateFromNLP> list, Tree t, Input input) {
		List<QuestionTemplateFromNLP> parents = new ArrayList<QuestionTemplateFromNLP>();
		List<QuestionTemplateFromNLP> result =  new ArrayList<QuestionTemplateFromNLP>();
		List<QuestionTemplateFromNLP> remove =  new ArrayList<QuestionTemplateFromNLP>();
		/*for (QuestionTemplateFromNLP qt : list)
			if (qt.templateType.equals("因果") || qt.templateType.equals("影响"))
				parents.add(qt);
			else
				result.add(qt);*/
		result.addAll(list);
		/*for (QuestionTemplateFromNLP qt : list) {
			if (qt.templateType.equals("排序") && qt.slots.get(2) != null && qt.slots.get(2).text.contains("、")) {
				String[] ls = qt.slots.get(2).text.split("、");
				QuestionTemplateFromNLP geo = new QuestionTemplateFromNLP();
				geo.oriText = qt.oriText;
				geo.syntaxTreeLeaves = Tree.getLeaves(t);
				geo.templateType = "列表";
				geo.slotCount = ls.length;
				geo.slots = new ArrayList<SlotStructureFromNLP>();
				int j = 0;
				for (String s : ls) {
					SlotStructureFromNLP aslot = new SlotStructureFromNLP();
					aslot.isTemplate = false;
					aslot.text = s;
					aslot.content = s;
					aslot.startOffset = aslot.endOffset = qt.slots.get(2).startOffset + j;
					aslot.syntaxNodes = new ArrayList<Tree>();
					aslot.depNodes = new ArrayList<DepTree>();
					aslot.syntaxNodes.add(Tree.findNodeByNo(t, aslot.startOffset));
					aslot.depNodes.add(DepTree.findNodeByNo(input.depRoots, aslot.startOffset));
					j += 2;
					geo.slots.add(aslot);
				}
				List<QuestionTemplateFromNLP> l =  new ArrayList<QuestionTemplateFromNLP>();
				l.add(geo);
				qt.slots.get(2).isTemplate = true;
				qt.slots.get(2).content = l;
			} else if (qt.templateType.equals("最值") && qt.slots.get(0) != null && (qt.slots.get(0).text.contains("和") || qt.slots.get(0).text.contains("、"))) {
				String[] ls;
				if (qt.slots.get(0).text.contains("、"))
					ls = qt.slots.get(0).text.split("、");
				else
					ls = qt.slots.get(0).text.split("和");
				QuestionTemplateFromNLP geo = new QuestionTemplateFromNLP();
				geo.oriText = qt.oriText;
				geo.syntaxTreeLeaves = Tree.getLeaves(t);
				geo.templateType = "集合";
				geo.slotCount = ls.length;
				geo.slots = new ArrayList<SlotStructureFromNLP>();
				int j = 0;
				for (String s : ls) {
					SlotStructureFromNLP aslot = new SlotStructureFromNLP();
					aslot.isTemplate = false;
					aslot.text = s;
					aslot.content = s;
					aslot.startOffset = aslot.endOffset = qt.slots.get(2).startOffset + j;
					aslot.syntaxNodes = new ArrayList<Tree>();
					aslot.depNodes = new ArrayList<DepTree>();
					aslot.syntaxNodes.add(Tree.findNodeByNo(t, aslot.startOffset));
					aslot.depNodes.add(DepTree.findNodeByNo(input.depRoots, aslot.startOffset));
					j += 2;
					geo.slots.add(aslot);
				}
				List<QuestionTemplateFromNLP> l =  new ArrayList<QuestionTemplateFromNLP>();
				l.add(geo);
				qt.slots.get(0).isTemplate = true;
				qt.slots.get(0).content = l;
			} else if (qt.templateType.equals("匹配") && qt.slots.get(1) != null && (qt.slots.get(1).text.contains("和") || qt.slots.get(1).text.contains("、"))) {
				String[] ls;
				if (qt.slots.get(1).text.contains("、"))
					ls = qt.slots.get(1).text.split("、");
				else
					ls = qt.slots.get(1).text.split("和");
				QuestionTemplateFromNLP geo = new QuestionTemplateFromNLP();
				geo.oriText = qt.oriText;
				geo.syntaxTreeLeaves = Tree.getLeaves(t);
				geo.templateType = "集合";
				geo.slotCount = ls.length;
				geo.slots = new ArrayList<SlotStructureFromNLP>();
				int j = 0;
				for (String s : ls) {
					SlotStructureFromNLP aslot = new SlotStructureFromNLP();
					aslot.isTemplate = false;
					aslot.text = s;
					aslot.content = s;
					aslot.startOffset = aslot.endOffset = qt.slots.get(1).startOffset + j;
					aslot.syntaxNodes = new ArrayList<Tree>();
					aslot.depNodes = new ArrayList<DepTree>();
					aslot.syntaxNodes.add(Tree.findNodeByNo(t, aslot.startOffset));
					aslot.depNodes.add(DepTree.findNodeByNo(input.depRoots, aslot.startOffset));
					j += 2;
					geo.slots.add(aslot);
				}
				List<QuestionTemplateFromNLP> l =  new ArrayList<QuestionTemplateFromNLP>();
				l.add(geo);
				qt.slots.get(1).isTemplate = true;
				qt.slots.get(1).content = l;
			}
		}*/
		parents.addAll(result);
		//if (parents.isEmpty()) return list;
		for (QuestionTemplateFromNLP qt : parents)
				for (SlotStructureFromNLP ss : qt.slots)
					if (ss != null)
						for (QuestionTemplateFromNLP qtf : result) 
							if (qt != qtf) {
								boolean b = true;
								for (SlotStructureFromNLP ssf : qtf.slots){
									if (ssf != null) {
										String s1 = ss.content;
										String s2 = ssf.content;
										if (!s1.contains(s2)) {
											b = false;
											break;
										}
									}
								}
								if (b) {
									if (qtf != null && 
											(!qtf.templateType.equals("时间限定") 
											|| (qtf.slots == null || qtf.slots.get(0) == null || ss.content.equals(qtf.slots.get(0).content)))) {
										if (qt.templateType.equals("因果") || qt.templateType.equals("影响") || qt.templateType.equals("实体信息陈述")) {
											/*if (ss.isTemplate) {
												if (!((ArrayList<QuestionTemplateFromNLP>)ss.content).contains(qtf))
													((ArrayList<QuestionTemplateFromNLP>)ss.content).add(qtf);
											}
											else {
												ss.isTemplate = true;
												ss.content = new ArrayList<QuestionTemplateFromNLP>();
												((ArrayList<QuestionTemplateFromNLP>)ss.content).add(qtf);
											}*/
											remove.add(qtf);
										} else
											if (qtf.templateType.equals("实体信息陈述"))
												remove.add(qtf);
									}
								}
							}
		//result.addAll(parents);
		for (QuestionTemplateFromNLP qt : remove)
			result.remove(qt);
		/*for (QuestionTemplateFromNLP qt : result)
			for (int i = 0; i < qt.slots.size(); i++)
				qt.slots.set(i, checkRepeat(qt.slots, i));*/
		/*for (QuestionTemplateFromNLP qt : parents)
			for (SlotStructureFromNLP ss : qt.slots)
				if (ss != null)
					ss.content = ss.text;*/
		List<String> bj = new ArrayList<String>();
		for (QuestionTemplateFromNLP qt : result)
			if (qt.templateType.equals("比较") && qt.slots.get(2) != null && qt.slots.get(4) != null)
				bj.add(qt.slots.get(2).content 
						+ qt.slots.get(4).content);
		parents = new ArrayList<QuestionTemplateFromNLP>();
		parents.addAll(result);
		for (QuestionTemplateFromNLP qt : parents)
			if (qt.templateType.equals("实体信息陈述")) {
				String s = qt.slots.get(0).content + qt.slots.get(2).content;
				if (bj.contains(s))
					result.remove(qt);
			}
		return result;
	}
	
	/*@SuppressWarnings("unchecked")
	private static SlotStructureFromNLP checkRepeat(ArrayList<SlotStructureFromNLP> slots, int index) {
		SlotStructureFromNLP ss = slots.get(index);
		if (ss != null && ss.isTemplate && ((ArrayList<QuestionTemplateFromNLP>)ss.content).size() > 1) {
			for (int i = 0; i < ((ArrayList<QuestionTemplateFromNLP>)ss.content).size(); i++) {
				QuestionTemplateFromNLP qtf = ((ArrayList<QuestionTemplateFromNLP>)ss.content).get(i);
				List<QuestionTemplateFromNLP> l = new ArrayList<QuestionTemplateFromNLP>();
				for (int j = 0; j < ((ArrayList<QuestionTemplateFromNLP>)ss.content).size(); j++) {
					if (i != j) {
						for (SlotStructureFromNLP ssf : ((ArrayList<QuestionTemplateFromNLP>)ss.content).get(j).slots) {
							if (ssf != null && ssf.isTemplate)
								l.addAll(((ArrayList<QuestionTemplateFromNLP>)ssf.content));
						}
					}		
				}
				if (l.contains(qtf))
					((ArrayList<QuestionTemplateFromNLP>)ss.content).remove(qtf);
			}
		} 
		return ss;
	}*/
}
