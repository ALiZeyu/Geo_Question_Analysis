package cn.edu.nju.ws.GeoScholar.templating.common;

import java.util.List;

public class SlotStructureFromNLPForSaq extends SlotStructureFromNLP {
	/**
	 * 槽的内容, 嵌套的模板
	 */
	public QuestionTemplateFromNLPForSaq template = null;
	
	/**
	 * toString
	 */
	@SuppressWarnings("unchecked")
	public String toString() {
		if (content != null)
			return content;
		else
			return template.toString();
	}
}
