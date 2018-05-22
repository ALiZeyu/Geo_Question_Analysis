package cn.edu.nju.ws.GeoScholar.common;

import java.util.ArrayList;

import cn.edu.nju.ws.GeoScholar.templating.common.DepTree;
import cn.edu.nju.ws.GeoScholar.templating.common.QuestionTemplateFromNLP;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;
/**
 * Nlp产生的输出
 * @author jwding
 *
 */
public class NlpOutput {
	/**
	 * 词性标注 + 分词, 形式为 分词_词性
	 */
	@Deprecated
	public ArrayList<String> pos ;			
	/**
	 * 句法树
	 */
	@Deprecated
	public Tree syntaxTree ;
	/**
	 * 依存树
	 */
	@Deprecated
	public DepTree dependencyTree ;
	
	/**
	 * 模板化的问句, 来自NLP
	 */
	@Deprecated
	public ArrayList<QuestionTemplateFromNLP> templates;
	
	public String toString() {
		// TODO Auto-generated method stub
		return "词性标注+分词:"+pos.toString()
				+"\n句法树:"+syntaxTree.toString()
				+"\n依存树:"+dependencyTree.toString()				//依存树toString方法结尾处有回车
				+"模板化问句:"+templates.toString();
	}
}
