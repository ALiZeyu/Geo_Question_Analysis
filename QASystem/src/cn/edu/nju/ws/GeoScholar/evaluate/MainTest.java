package cn.edu.nju.ws.GeoScholar.evaluate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.nju.ws.GeoScholar.common.NLPResult;
import cn.edu.nju.ws.GeoScholar.templating.choice.Generate;
import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.QuestionTemplateFromNLP;

public class MainTest {

	public static void main(String[] args) {
//		if(args.length != 2){
//			System.out.println("the number of input paramter is three");
//			return;
//		}
//		String input_path = args[0];
//		String output_path = args[1];
//		String eva_path = args[2];
//		String input_path = "test_data/sample_test_data.txt";
//		String output_path = "test_data/sample_result_data.txt";
//		String eva_path = "test_data/sample_golden_data.txt";
		String input_path = "test_data/final_test_data.txt";
		String output_path = "test_data/final_result_data.txt";
		String eva_path = "test_data/final_golden_data.txt";
		try {
			Generate.init();
			successiveProcess(input_path,output_path);
		} catch (IOException e) {
			System.out.println("no output");
			e.printStackTrace();
		}
		evaluate(output_path, eva_path);
	}
	
	public static void successiveProcess(String testPath, String resultPath) throws IOException{
		//E:/geo_data/测试软件/test_paper.txt
		List<String> result = MyUtil.readListFromFile(testPath);
		List<String> mine = new ArrayList<>();
		Set<String> del = new HashSet<>();
		Map<String, Integer> count = new HashMap<>();
		for (int j = 0 ; j < result.size(); j++) {
			//全部试题测试
			//System.out.println(result.get(j));
			mine.add(result.get(j));
			try {
				List<String> list=new ArrayList<>();
				List<NLPResult> allresult = Generate.GenerateTemplate(result.get(j),list);
				for(NLPResult tst : allresult){
					if((tst.superTemplates==null||tst.superTemplates.templates.size()==0)&&(tst.subTemplates==null||tst.subTemplates.templates.size()==0))
						System.out.println("模板缺失"+result.get(j));
				}
				for(NLPResult tst : allresult){
					if(tst.superTemplates!=null){
						for(QuestionTemplateFromNLP nlp:tst.superTemplates.templates){
							String name = nlp.templateType;
							count.put(name, count.getOrDefault(name, 0)+1);
							if(nlp.toString()!=null)
								mine.add(nlp.toString());
						}
					}
				}
				for(NLPResult tst : allresult){
					if(tst.subTemplates!=null)
						for(QuestionTemplateFromNLP nlp:tst.subTemplates.templates){
							String name = nlp.templateType;
//							if(tst.subTemplates.templates.size()>1 && name.equals("其他陈述") && tst.superTemplates==null){
//								del.add(result.get(j));
//								continue;
//							}
							count.put(name, count.getOrDefault(name, 0)+1);
							if(nlp.toString()!=null)
								mine.add(nlp.toString());
						}
				}
			} catch (Exception e) {
				System.out.println(result.get(j)+e.getMessage());
			}
			mine.add("\n");
		}
//		int sum = 0;
//		for(Map.Entry<String, Integer> entry : count.entrySet())
//			sum+=entry.getValue();
//		System.out.println(sum);
//		for(Map.Entry<String, Integer> entry : count.entrySet()){
//			System.out.println(entry.getKey()+"\t"+(double)entry.getValue()/sum);
//		}
//		Iterator<String> it = result.iterator();
//		while(it.hasNext()){
//			String temp = it.next();
//			if(del.contains(temp))
//				it.remove();
//		}
//		MyUtil.writeFile(result, "test_data/testData.txt");
		MyUtil.writeFile(mine, resultPath);
	}
	
	public static void evaluate(String result_path, String golden_path){
		List<String> result = MyUtil.readListFromFile(result_path);
		List<String> golden = MyUtil.readListFromFile(golden_path);
		int r=0, g=0, correct=0, mine_num=0, gold_num=0;
		Set<String> mine = new HashSet<>();
		Set<String> gold = new HashSet<>();
		while(r<result.size() && g<golden.size()){
			int er=r+1, eg=g+1;
			while(er<result.size() && !result.get(er).trim().equals(""))
				er++;
			for(int i = r+1;i<er;i++)
				mine.add(result.get(i));
			while(er<result.size() && result.get(er).trim().equals(""))
				er++;
			r = er;
			
			while(eg<golden.size() && !golden.get(eg).trim().equals(""))
				eg++;
			for(int i = g+1;i<eg;i++)
				gold.add(golden.get(i));
			while(eg<golden.size() && golden.get(eg).trim().equals(""))
				eg++;
			g = eg;
			mine_num += mine.size();
			gold_num += gold.size();
			for(String template:mine)
				if(gold.contains(template)){
					correct++;
					//System.out.println(template);
				}
			mine.clear();
			gold.clear();
		}
		System.out.println("result_num: "+mine_num+"\t"+"gold_num: "+gold_num+"\tmine_num"+correct);
		double precision = (double)correct/mine_num;
		double recall = (double)correct/gold_num;
		double Fscore = 2*precision*recall/(precision+recall);
		System.out.println("precision:\t"+precision);
		System.out.println("recall:\t"+recall);
		System.out.println("f1-score:\t"+Fscore);
	}

}
