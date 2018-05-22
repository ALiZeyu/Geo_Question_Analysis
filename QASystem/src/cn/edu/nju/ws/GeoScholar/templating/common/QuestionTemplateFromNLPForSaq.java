package cn.edu.nju.ws.GeoScholar.templating.common;

import java.util.ArrayList;
import java.util.List;

public class QuestionTemplateFromNLPForSaq extends QuestionTemplateFromNLP{
	/**
	 * 槽信息列表
	 */
	public ArrayList<SlotStructureFromNLPForSaq> slots ;
	/**
	 * toString
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(templateType + "(");
		boolean first = false;
		for (SlotStructureFromNLPForSaq slot : slots) {
			if (!first) {
				result.append(slot);
				first = true;
			} else 
				result.append("," + slot);
		}
		result.append(")");
		return result.toString();
	}
}
