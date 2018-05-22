package cn.edu.nju.ws.GeoScholar.common;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

/**
 * Nlp产生的输出
 * @author Lizy
 *
 */
public class NLPResult extends NlpOutput{ 
	
	public boolean getNegative(){
		return negative;
	}

	public List<String> getConventionalVerbalExchanges() {
		return conventionalVerbalExchanges;
	}

	public List<String> getContexts() {
		return contexts;
	}

	public NLPTemplate getSuperTemplates() {
		return superTemplates;
	}

	public String getTargetSentence() {
		return targetSentence;
	}
	
	public Tree getTargetTree(){
		return targetTree;
	}

	public NLPTemplate getSubTemplates() {
		return subTemplates;
	}
	
	public NLPResult(boolean negative, String thText, List<String> sxwText){
		this.negative=negative;
		this.conventionalVerbalExchanges=new ArrayList<>();
		this.conventionalVerbalExchanges.add(thText);
		this.contexts=new ArrayList<>();
		this.contexts.addAll(sxwText);
	}
	
	public NLPResult(NLPResult clone){
		this.negative=clone.negative;
		this.contexts=clone.contexts;
		this.conventionalVerbalExchanges=clone.conventionalVerbalExchanges;
		this.targetSentence=clone.targetSentence;
		this.targetTree=clone.targetTree;
		this.superTemplates=clone.superTemplates;
		this.subTemplates=new NLPTemplate(clone.subTemplates);
	}
	
	public NLPResult(){
		
	}
	
	/**
	 * 否定标志
	 */
	public boolean negative;
	
	/**
	 * 套话
	 */
	public List<String> conventionalVerbalExchanges;

	/**
	 * 上下文信息
	 */
	public List<String> contexts;

	/**
	 * 高阶模板
	 */
	public NLPTemplate superTemplates;

	/**
	 * 选项问句
	 */
	public String targetSentence;
	
	/**
	 * 选项问句的句法树
	 */
	public Tree targetTree;

	/**
	 * 二级模板
	 */
	public NLPTemplate subTemplates;
	
	public String toString() {
		return  "否定标志:"+negative
				+"\n套话:"+(conventionalVerbalExchanges==null?"null":conventionalVerbalExchanges.toString())
				+"\n上下文信息:"+(contexts==null?"null":contexts.toString())
				+"\n高阶模板:"+(superTemplates==null?"null":superTemplates.toString())
				+"\n选项问句:"+(targetSentence==null?"null":targetSentence)
				+"\n选项问句句法树:"+(targetTree==null?"null":targetTree.toString())
				+"\n二级模板:"+(subTemplates == null?"null":subTemplates.toString());
	}

}
