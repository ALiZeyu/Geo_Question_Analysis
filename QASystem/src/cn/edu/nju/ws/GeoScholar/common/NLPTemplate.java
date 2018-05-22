package cn.edu.nju.ws.GeoScholar.common;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.GeoScholar.templating.common.DepTree;
import cn.edu.nju.ws.GeoScholar.templating.common.QuestionTemplateFromNLP;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
/**
 * NLPResult中的模板
 * @author Lizy
 *
 */
public class NLPTemplate {

	public String getOriginalText() {
		return OriginalText;
	}
	
	public ArrayList<String> getPos() {
		return pos;
	}

	public Tree getSyntaxTree() {
		return syntaxTree;
	}

//	public DepTree getDependencyTree() {
//		return dependencyTree;
//	}

	public List<String> getGeoTerms() {
		return geoTerms;
	}

	public List<String> getTimes() {
		return times;
	}

	public List<String> getLocations() {
		return locations;
	}

	public ArrayList<QuestionTemplateFromNLP> getTemplates() {
		return templates;
	}
	//除了模板不一致，其他全部复制的构造函数
	public NLPTemplate(NLPTemplate clone){
		this.OriginalText=clone.OriginalText;
		this.pos=clone.pos;
		this.syntaxTree=clone.syntaxTree;
		//this.dependencyTree=clone.dependencyTree;
		this.geoTerms=clone.geoTerms;
		this.times=clone.times;
		this.locations=clone.locations;
		this.templates=new ArrayList<>();
	}
	
	public NLPTemplate(){
		
	}

	/**
	 * 模板所对应的文本
	 */
	public String OriginalText;
	
	/**
	 * 词性标注 + 分词, 形式为 分词_词性
	 */
	public ArrayList<String> pos;
	
	/**
	 * 句法树
	 */
	public Tree syntaxTree ;	
	
	/**
	 * 依存树
	 */
	//public DepTree dependencyTree;

	/**
	 * 概念术语
	 */
	public List<String> geoTerms;

	/**
	 * 时间
	 */
	public List<String> times;

	/**
	 * 地点
	 */
	public List<String> locations;

	/**
	 * 模板化的问句, 来自NLP
	 */
	public ArrayList<QuestionTemplateFromNLP> templates;
	
	public String toString() {
		// TODO Auto-generated method stub
		return 	"原始文本:"+OriginalText
				+"\n词性标注+分词:"+pos.toString()
				+"\n句法树:"+syntaxTree.toString()
				+"概念术语:"+(geoTerms==null?"null":geoTerms.toString())
				+"\n时间:"+(times==null?"null":times.toString())
				+"\n地点:"+(locations==null?"null":locations.toString())
				+"\n模板化问句:"+templates.toString();
	}

}
