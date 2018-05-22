package Ner;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TimeAndLocMain
{
	private Map<String, double[]> params;
	
	public TimeAndLocMain()
	{
		load_NERModel();
	}
	
	public void load_NERModel()
	{
		Util u = new Util();
		params = u.loadParams("param/params.txt");
	}
	
	/**
	 * 输入一句话 s
	 * @return 返回所有时间列表
	 */
	public ArrayList<String> timeList(String s)
	{
		TimeRecognition tr = new TimeRecognition();
		return tr.findAllTime(s);
	}
	
	/**
	 * 输入一句话 s，判断整句是否为时间
	 * @return 布尔值
	 */
	public boolean isTime(String s)
	{
		TimeRecognition tr = new TimeRecognition();
		List<String> timeList = tr.findAllTime(s);
		for(String time : timeList)
		{
			if(s.equals(time)) return true;
		}
		String time = tr.irRegularTime(s);
		if(time.equals(s)) return true;
		return false;
	}
	
	/**
	 * 输入分词结果 s，词与词之间用空格隔开，如“江苏省 南京市”
	 * @return 返回所地点列表
	 */
	public ArrayList<String> locList(String s)
	{
		int beamSize = 16, mode = 1, now = 0;
		Util u = new Util();
//		Map<String, double[]> params = u.loadParams("param/params.txt");
					
		Beam_search b = new Beam_search();
		List<String[]> nerResult = new ArrayList<>();
		
		List<String[]> wordList = u.genWordPosLabel(s, mode);
		List<Term> predictTerm = b.runBeamSearch(s, beamSize, this.params, now, mode);
		for(int i = 0; i < predictTerm.size(); ++i)
		{
			wordList.get(i)[2] = predictTerm.get(i).label;
		}
		nerResult = u.simplifyLabel(wordList);
		
		params.clear();
		
		ArrayList<String> result = new ArrayList<>();
		
		int index = 0;
		while(index < nerResult.size())
		{
			if(nerResult.get(index)[2].equals("LOC"))
			{
				StringBuilder sb = new StringBuilder();
				int i = index;
				while(i < nerResult.size() && nerResult.get(i)[2].equals("LOC"))
				{
					sb.append(nerResult.get(i)[0]);
					++i;
				}
				result.add(sb.toString());
				index = i;
			}
			else
				index++;
		}

		return result;
	}
	
	public static void main(String[] args)
	{
		TimeAndLocMain talm = new TimeAndLocMain();
		List<String> timeList = talm.timeList("十一月");
		for(String time : timeList)
		{
			System.out.println(time);
		}
		
		boolean isTime = talm.isTime("专家抵达湘时");
		System.out.println(isTime);
		
		List<String> locList = talm.locList("长春 基地 高档 车型 的 合理 市场 定位 是 吉林省 市场");
		for(String loc : locList)
		{
			System.out.println(loc);
		}
	
	}
}
