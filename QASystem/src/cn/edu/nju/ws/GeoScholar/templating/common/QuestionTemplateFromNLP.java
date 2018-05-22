package cn.edu.nju.ws.GeoScholar.templating.common;

import java.util.ArrayList;
import java.util.List;


/**
 * NLP输出结果的存储结构
 * @author lfshi
 *
 */

public class QuestionTemplateFromNLP {
	/**
	 * 原始文本, 题干+选项, 中间用'$'分隔
	 */
	public String oriText ;							
	/**
	 * 模板类型
	 */
	public String templateType;				
	/**
	 * 模板槽的个数
	 */
	public int slotCount ;						
	/**
	 * 槽信息列表
	 */
	public ArrayList<SlotStructureFromNLP> slots ;	
	/**
	 * 句法树
	 */
	public List<Tree> syntaxTreeLeaves;
	/**
	 * 线索词ID
	 */
	public int cueWord;
	/**
	 * toString
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(templateType + "(");
		boolean first = false;
		for (SlotStructureFromNLP slot : slots) {
			if (!first) {
				result.append(slot);
				first = true;
			} else 
				result.append("," + slot);
		}
		result.append(")");
		return result.toString();
	}
	//影响类关系、比较类、因果、指示、分布、构成
	//原因、后果、影响、对策、其他关联、 \t 指示、比较、变化、因果关联、运动、构成、分布、其他陈述
//	public String toString() {
//		String[] other = {"对策","其他关联","变化","运动","其他陈述"};
//		String[] normal = {"原因","因果关联","指示","分布","构成","后果"};
//		StringBuilder result = new StringBuilder();
//		if(MyUtil.arrayContains(other, templateType))
//			return null;
//		if(MyUtil.arrayContains(normal, templateType)){
//			String name = (templateType.equals("原因")||templateType.equals("因果关联"))?"因果":templateType;
//			String sec = slots.get(1).toString();
//			String first = slots.get(0).toString();
//			result.append(name+"("+first+","+sec+")");
////			result.append(name + "(");
////			boolean first = false;
////			for (SlotStructureFromNLP slot : slots) {
////				if (!first) {
////					result.append(slot);
////					first = true;
////				} else 
////					result.append("," + slot);
////			}
////			result.append(")");
//		}else if(templateType.equals("比较")){
//			if(slots.size()==5){
//				String first = slots.get(0)==null?"null":slots.get(0).toString();
//				String sec = slots.get(1)==null?"null":slots.get(1).toString();
//				String third = slots.get(2)==null?"null":slots.get(2).toString();
//				String fourth = slots.get(3)==null?"null":slots.get(3).toString();
//				String fifth = slots.get(4)==null?"null":slots.get(4).toString();
//				if(sec.equals("null")){
//					sec = fourth.equals("null")?"":fourth;
//				}
//				if(fourth.equals("null"))
//					fourth = sec.equals("null")?"":sec;
//				if(third.equals("###") && fourth.equals("###"))
//					result.append("比较_"+fifth+"("+first+","+sec+")");
//				else
//				result.append("比较_"+fifth+"("+first+sec+","+third+fourth+")");
//			}else {
//				return null;
//			}
//		}else if(templateType.equals("影响")){
//			if(slots.size()==4){
//				String first = slots.get(0)==null?"null":slots.get(0).toString();
//				String sec = slots.get(1)==null?"null":slots.get(1).toString();
//				String third = slots.get(3)==null?"null":slots.get(3).toString();
//				third = third.equals("null")?"":third;
//				result.append("影响_"+third+"("+first+","+sec+")");
//			}else {
//				return null;
//			}
//		}
////		else{
////			if(slots.size()==2){
////				String sec = slots.get(0)==null?"null":slots.get(0).toString();
////				String first = slots.get(1)==null?"null":slots.get(1).toString();
////				result.append("因果("+first+","+sec+")");
////			}else {
////				return null;
////			}
////		}
//		
//		return result.toString();
//	}
//	
}
