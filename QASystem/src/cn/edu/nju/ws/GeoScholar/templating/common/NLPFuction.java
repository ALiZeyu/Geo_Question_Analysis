package cn.edu.nju.ws.GeoScholar.templating.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NLPFuction {
	/**利用正则表达式匹配套话并返回*/
	public static String getTaohua(String question){
		String[] regex={"(根据|据|由|结合|从).*(分析|推测|推断|判断|可知)(，|出){0,1}(下列|关于|有关).*(正确|符合实际|可信).(是)",
				"(根据|据|由|结合|从).*(分析|推测|推断|判断|可知)(，|出){0,1}",
				"(下列|关于|有关).*(正确|符合实际|可信).{1,3}(是)"};
		String taohua="";
		for(String reg:regex){
			Pattern p = Pattern.compile(reg);
			Matcher m = p.matcher(question);
			int start=0,end=0;
			while (m.find()) {
				start = m.start();
				end = m.end();
				taohua=question.substring(start, end);
			}
			if(!taohua.equals(""))
				break;
		}
		return taohua;
	}
}
