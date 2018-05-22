package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TimeRecognition
{
	private boolean containNumber(String str)
	{
		for(int i = 0; i < str.length(); ++i)
		{
			if(Character.isDigit(str.charAt(i)))
			{
				return true;
			}
		}
		return  false;
	}
	private String matchShi(String dateStr)
	{
		int index = dateStr.indexOf("，");
		String time = "";
		if(index>0 && dateStr.charAt(index-1)=='时' && (containNumber(dateStr.substring(0, index))==false))
		{
			time = dateStr.substring(0, index);
		}
		return time;
	}
	
	private String matchYear(String dateStr) 
	{
		try 
		{
	        List matchYear = null;  
	        
	        // 时间中必须包含"年"  
			Pattern py = Pattern.compile("((北京时间)?(\\d{1,4}(世纪|至|~|～|－|-|——|—))?(\\d{1,4}|每|今)(年|周年|万年前|~|～|－|-|\\/|—)(\\d{1,2}(月|月份|年代|－|-|\\/|—)(上旬|中旬|下旬)?(\\d{1,2}(日)?)?)?(\\s)*(到|至|～|~|-|－|——|—)?(\\d{1,2}(点|时|月|日)?((:)?\\d{1,2}(年|分|日)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)");
	        Matcher matcher = py.matcher(dateStr);
  
	        if (matcher.find() && matcher.groupCount() >= 1) 
	        {  
	            matchYear = new ArrayList();  
	            for (int i = 1; i <= matcher.groupCount(); i++) 
	            {  
	                String temp = matcher.group(i);  
	                matchYear.add(temp);  
	            }  
	        } 
	        else 
	        {  
	            matchYear = Collections.EMPTY_LIST;  
	        }             
	        if (matchYear.size() > 0) 
	        {  
	            return ((String) matchYear.get(0)).trim();  
	        } 
	        else 
	        {
	        		return "";
	        }  
	    } 
		catch (Exception e) 
		{  
	        e.printStackTrace(); 
	        return "";
	    }  
	      
}  
	
	private String realYear(String s)
	{
		String ry = matchYear(s);
		if("".equals(ry)|| ry.matches("[0-9]+"))
		{
			return "";
		} 
		else if (ry.matches("\\d{1,2}:\\d{1,2}:\\d{1,2}"))
		{
			if (ry.charAt(0) > '2' || ry.charAt(3) > '5' || ry.charAt(6) > '5')
			{
				return "";
			}
		} 
		else if (ry.matches("\\d{1,2}:\\d{1,2}"))
		{
			if (ry.charAt(0)> '2' || ry.charAt(3) > '5')
			{
				return "";
			}
		} 
		else if (ry.matches("\\d{1,2}(-|－|——)\\d{1,2}"))
		{
			return "";
		}
		else
		{
			return ry;
		}
		return ry;
	}
	
	private String matchMonth(String dateStr) 
	{
		try 
		{
	        List<String> matchMonth = null;  
        
	        // 时间中必须包含"月"
	        Pattern p = Pattern.compile("((北京时间)?(\\d{1,2}|一|二|三|四|五|六|七|八|九|十|十一|十二)(\\/|月)(到|至|～|~|-|－|——)?(\\d{1,2}([日|月])?)?(\\s)*(到|至|～|~|-|－|——)?(\\d{1,2}([点|时|月])?((:)?\\d{1,2}(分|日)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)");

			Matcher matcher = p.matcher(dateStr);
  
	        if (matcher.find() && matcher.groupCount() >= 1) 
	        {  
	            matchMonth = new ArrayList();  
	            for (int i = 1; i <= matcher.groupCount(); i++) 
	            {  
	                String temp = matcher.group(i);  
	                matchMonth.add(temp);  
	            }  
	        }
	        else
	        {  
	            matchMonth = Collections.EMPTY_LIST;  
	        }             
	        if (matchMonth.size() > 0)
	        {  
	            return ((String) matchMonth.get(0)).trim();  
	        }
	        else 
	        {
	        		return "";
	        }  
	    } 
		catch (Exception e) 
		{  
	        e.printStackTrace(); 
	        return "";
	    }  
 
}  
	 
	private String realMonth(String s)
	{
		String rm = matchMonth(s);
		if("".equals(rm)|| rm.matches("[0-9]+"))
		{
			return "";
		}
		else if (rm.matches("\\d{1,2}:\\d{1,2}:\\d{1,2}"))
		{
			if (rm.charAt(0) > '2' || rm.charAt(3) > '5' || rm.charAt(6) > '5')
			{
				return "";
			}
		} 
		else if (rm.matches("\\d{1,2}:\\d{1,2}"))
		{
			if (rm.charAt(0)> '2' || rm.charAt(3) > '5')
			{
				return "";
			}
		}
		else if (rm.matches("\\d{1,2}(-|－|——)\\d{1,2}"))
		{
			return "";
		} 
		else 
		{
			return rm;
		}
		return rm;
	}
	
	private String matchDay(String dateStr) 
	{
		try 
		{
	        List matchDay = null;  
	    
	        // 时间中必须包含"日"
	        Pattern pd = Pattern.compile("(\\d{1,2}(日)?(\\s)*(到|至|～|~|-|－|——)?(\\d{1,2}([月|点|时|日])?((:)?\\d{1,2}(分|日)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)");
	        
	        Matcher matcher = pd.matcher(dateStr);

	        if (matcher.find() && matcher.groupCount() >= 1) 
	        {  
	            matchDay = new ArrayList();  
	            for (int i = 1; i <= matcher.groupCount(); i++) 
	            {  
	                String temp = matcher.group(i);  
	                matchDay.add(temp);  
	            }  
	        } 
	        else 
	        {  
	            matchDay = Collections.EMPTY_LIST;  
	        }             
	        if (matchDay.size() > 0) 
	        {  
	            return ((String) matchDay.get(0)).trim();  
	        } 
	        else 
	        {
	        		return "";
	        }  
	    } 
		catch (Exception e) 
		{  
	        e.printStackTrace(); 
	        return "";
	    }  

	}  
 
	private String realDay(String s)
	{
		String rd = matchDay(s);
		if("".equals(rd)|| rd.matches("[0-9]+"))
		{
			return "";
		}
		else if (rd.matches("\\d{1,2}:\\d{1,2}:\\d{1,2}"))
		{
			if (rd.charAt(0) > '2' || rd.charAt(3) > '5' || rd.charAt(6) > '5')
			{
				return "";
			}
		} 
		else if (rd.matches("\\d{1,2}:\\d{1,2}"))
		{
			if (rd.charAt(0)> '2' || rd.charAt(3) > '5')
			{
				return "";
			}
		} 
		else if (rd.matches("\\d{1,2}(-|－|——)\\d{1,2}"))
		{
			return "";
		} 
		else 
		{
			return rd;
		}
		return rd;
	}

	private String matchHour(String dateStr)
	{
		try
		{
	        List matchHour = null;  
	    
	        // 时间中必须包含"时或分或秒，20世纪，6～9月"
	        Pattern ph = Pattern.compile("(\\d{1,2}(点|时|世纪|~|～|-|－|——)((:|、|到|至)?\\d{1,2}(分|时|月|年代)?(份)?((:)?\\d{1,2}(秒)?)?)?(\\s)*(PM|AM)?)");

			Matcher matcher = ph.matcher(dateStr);
	        
	        if (matcher.find() && matcher.groupCount() >= 1) 
	        {  
	            matchHour = new ArrayList();  
	            for (int i = 1; i <= matcher.groupCount(); i++)
	            {  
	                String temp = matcher.group(i);  
	                matchHour.add(temp);  
	            }  
	        } 
	        else 
	        {  
	            matchHour = Collections.EMPTY_LIST;  
	        }             
	        if (matchHour.size() > 0) 
	        {  
	            return ((String) matchHour.get(0)).trim();  
	        } 
	        else 
	        {
	        		return "";
	        }  
	    }
		catch (Exception e) 
		{  
	        e.printStackTrace(); 
	        return "";
	    }  
	      
	}  
 
	private String realHour(String s)
	{
		String rh = matchHour(s);
		if("".equals(rh)|| rh.matches("[0-9]+"))
		{
			return "";
		} 
		else if (rh.matches("\\d{1,2}:\\d{1,2}:\\d{1,2}"))
		{
			if (rh.charAt(0) > '2' || rh.charAt(3) > '5' || rh.charAt(6) > '5')
			{
				return "";
			} 
			else
				return rh;
		}
		else if (rh.matches("\\d{1,2}:\\d{1,2}"))
		{
			if (rh.charAt(0)> '2' || rh.charAt(3) > '5')
			{
				return "";
			}
			else
				return rh;
		} 
		else if (rh.matches("\\d{1,2}(-|－|——)\\d{1,2}"))
		{
			return "";
		} 
		else
			return rh;	
	}

	private String realFullMonth(String s)
	{
		try 
		{
	        List matchHour = null;  
	    
	        // 时间中必须包含"12月份"
	        Pattern ph = Pattern.compile("((\\d{1,2}[、|-])?\\d{1,2}月份)");
	        
	        Matcher matcher = ph.matcher(s);

	        if (matcher.find() && matcher.groupCount() >= 1) 
	        {  
	            matchHour = new ArrayList();  
	            for (int i = 1; i <= matcher.groupCount(); i++) 
	            {  
	                String temp = matcher.group(i);  
	                matchHour.add(temp);  
	            }  
	        } 
	        else 
	        {  
	            matchHour = Collections.EMPTY_LIST;  
	        }             
	        if (matchHour.size() > 0) 
	        {  
	            return ((String) matchHour.get(0)).trim();  
	        } 
	        else 
	        {
	        		return "";
	        }  
	    } 
		catch (Exception e) 
		{  
	        e.printStackTrace(); 
	        return "";
	    }  
	}	
	
	//删除时间词之前的图1、题4之类标签
	private String deleteLabel(String s)
	{
		try 
		{
	        List matchLabel = null;  
	      
	        Pattern pl = Pattern.compile("([图|题]\\d{1,2}(-|－|——|、)?(\\d{1,2})?)");
	        
	        Matcher matcher = pl.matcher(s);

	        if (matcher.find() && matcher.groupCount() >= 1) 
	        {  
	            matchLabel = new ArrayList();  
	            for (int i = 1; i <= matcher.groupCount(); i++) 
	            {  
	                String temp = matcher.group(i);  
	                matchLabel.add(temp);  
	            }  
	        }
	        else 
	        {  
	            matchLabel = Collections.EMPTY_LIST;  
	        }             
	        if (matchLabel.size() > 0) 
	        {  
	            return ((String) matchLabel.get(0)).trim();  
	        } 
	        else 
	        {
	        		return "";
	        }  
	    } 
		catch (Exception e) 
		{  
	        e.printStackTrace(); 
	        return "";
	    }
	}
	
	private String realTime(String s)
	{
		String sp = new String(s);
		String sub = "";
		sub = sp.replaceFirst("((图|题)\\d{1,2}((-|－|——|、)\\d{1,2})?)", "");
		sub = sub.replaceAll("\\d{1,2}[至|-|——|～]\\d{1,2}[页|题]", "");
		
		String []time = {realYear(sub),realMonth(sub),realDay(sub),realHour(sub),realFullMonth(sub),matchShi(sub)};
//		for(String t : time)
//		{
//			System.out.println(t);
//		}
		int maxLen = realYear(sub).length();
		String maxStr = realYear(sub);
		
		for(String a : time)
		{
			if(a.length() > maxLen)
			{
				maxLen = a.length();
				maxStr = a;
			}
		}
	
		int index = maxStr.length();
		for(int i = maxStr.length()-1; i >= 0; --i)
		{
			if(!Character.isDigit(maxStr.charAt(i))&& maxStr.charAt(i)!='－')
			{
				index = i+1;
				break;
			}
		}
		return maxStr.substring(0,index);
	}
	
	public boolean isTime(String s)
	{
		if(s.contains("期间") || s.contains("之日") || s.contains("当日")) return true;
		else if(!"".equals(realTime(s))) return true;
		return false;	
		
	}
	
	public ArrayList<String> findAllTime(String s)
	{
		ArrayList<String> timeList = new ArrayList<>();
		String at = realTime(s);
		while(!"".equals(realTime(s)))
		{
			timeList.add(realTime(s));
			s = s.replaceFirst(realTime(s), "");
		}
		String[] time = {"春季", "夏季", "秋季", "冬季", "早晨", "中午","上午", "下午", "傍晚", "晚上","夜间"
				, "立春" , "雨水" , "惊蛰" , "春分" , "清明" , "谷雨" , "立夏" , "小满" , "芒种" , "夏至" , "小暑" , "大暑"
				, "立秋" , "处暑" , "白露" , "秋分" , "寒露" , "霜降" , "立冬" , "小雪" , "大雪" , "冬至" , "小寒" , "大寒"
		};
		
		for(int i = 0; i < time.length; ++i)
		{
			for(int j = 0; j < time.length; ++j)
			{
				if(s.contains("从"+time[i]+"到"+time[j]))
				{
					timeList.add("从"+time[i]+"到"+time[j]);
					s = s.replace("从"+time[i]+"到"+time[j], "");
				}
				if(s.contains(time[i]+"到"+time[j]))
				{
					timeList.add(time[i]+"到"+time[j]);
					s = s.replace(time[i]+"到"+time[j], "");
				}
			}
		}
		for(int i = 0; i < time.length; ++i)
		{
			if(s.contains(time[i]))
			{
				String temp = time[i];
				if(s.indexOf(time[i]) + 2 < s.length() && s.charAt(s.indexOf(time[i])+2) == '日')
					temp += "日";
				timeList.add(temp);
			}
		}
		
		
		return timeList;
	}
	
	public static void main(String[] args)
	{  	   
		TimeRecognition t = new TimeRecognition();
		String s = "卫星发射当日";
		System.out.println(t.isTime(s));
//		List<String> timeList = t.findAllTime("由08时到20时，在6月到8月期间，在7、8月份，放假期间，“彩虹”登录广州之日，本届大会期间，该日20时，卫星发射当日");
//		for(int i = 0; i < timeList.size(); ++i)
//		{
//			System.out.println(timeList.get(i));
//		}
	}
	
}
