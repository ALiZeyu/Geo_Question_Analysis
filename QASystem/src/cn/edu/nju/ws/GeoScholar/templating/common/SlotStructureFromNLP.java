package cn.edu.nju.ws.GeoScholar.templating.common;

import java.util.ArrayList;
import java.util.List;

//import cn.edu.nju.ws.GeoScholar.templating.Generate.Template;

/**
 * NLP的槽结构
 * @author lfshi
 *
 */
public class SlotStructureFromNLP {
	/**
	 * 槽的内容, 纯文本
	 */
	public String content = null;
	
	/**
	 * 槽的起始词位置，第一个词为0
	 */
	public int startOffset;
	/**
	 * 槽的结束词位置
	 */
	public int endOffset;
	/**
	 * 槽的词对应句法树节点列表
	 */
	public List<Tree> syntaxNodes;
	/**
	 * 槽的词对应依存树节点列表
	 */
	public List<DepTree> depNodes;
	
	/**
	 * toString
	 */
	@SuppressWarnings("unchecked")
	public String toString() {
		return content;
	}
}
