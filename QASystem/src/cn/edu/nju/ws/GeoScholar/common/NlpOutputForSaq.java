package cn.edu.nju.ws.GeoScholar.common;

import java.util.ArrayList;

import cn.edu.nju.ws.GeoScholar.templating.common.DepTree;
import cn.edu.nju.ws.GeoScholar.templating.common.QuestionTemplateFromNLPForSaq;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class NlpOutputForSaq extends NlpOutput{
	
	/**
	 * 模板化的问句, 来自NLP
	 */
	public ArrayList<QuestionTemplateFromNLPForSaq> templates;
	
	public String toString() {
		// TODO Auto-generated method stub
		return "词性标注+分词:"+pos.toString()
				+"\n句法树:"+syntaxTree.toString()
				+"\n依存树:"+dependencyTree.toString()				//依存树toString方法结尾处有回车
				+"模板化问句:"+templates.toString();
	}

}
