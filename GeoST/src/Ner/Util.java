package Ner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Util 
{
	// 加载文件
	List<String> loadFile(String filename)
	{
		File file = new File(filename);
		List<String> data = new ArrayList<>();
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			String line = br.readLine();
			if(line.startsWith("\uFEFF"))
			{
				line = line.substring(1);
			}
			while(line != null)
			{
				data.add(line);
				line = br.readLine();
			}		
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				br.close();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return data;
	}
	
	//加载参数
	Map<String, double[]> loadParams(String paramsFile)
	{
		File file = new File(paramsFile);
		Map<String, double[]> params = new HashMap<>();
		
		BufferedReader br = null;
		try 
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			String s = null;
			while((s = br.readLine()) != null)
			{
				String[] str = s.split("\t");
				double[] tempDouble = {Double.parseDouble(str[1]), 0.0, 0.0};
				params.put(str[0], tempDouble);
			}
		} 
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				br.close();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return params;
	}
	
	/** 对一句话生成(词|词性|标签)序列
	 * @param mode 0表示训练，1表示测试
	 */
	List<String[]> genWordPosLabel(String sentence, int mode)
	{
		List<String[]> wordList = new ArrayList<>();
		String[] wordLabel = sentence.split(" ");
		for(String temp : wordLabel)
		{
			if(mode == 0)
			{
				wordList.add(temp.split("/"));
			}
			else if(mode == 1)
			{//设置选择是否添加词性
//				String[] t = {temp.split("/")[0], temp.split("/")[1], null};
//周浩修改，删除词性				
				String[] t = {temp.split("/")[0], null, null};
				wordList.add(t);
			}
		}
		return wordList;
	}
	
	/** Term长度为sentence中词的个数+1
	 * @param wordList
	 * @return
	 */
	List<Term> genTerm(List<String[]> wordList)
	{
		List<Term> termList = new ArrayList<>();
		termList.add(new Term(0.0, -1, null, null));
		for(int i = 0; i < wordList.size(); ++i)
		{
			Term temp = new Term(0.0, i, wordList.get(i)[2], termList.get(termList.size() - 1));
			termList.add(temp);
		}
		return termList;
	}
	
	//简化标签
	List<String[]> simplifyLabel(List<String[]> wordLabel)
	{
		for(int i = wordLabel.size() - 1 ; i >= 0 ; --i)
		{
			if(wordLabel.get(i)[2].equals("other"))
			{
				wordLabel.get(i)[2] = "OTHER";
			}
			if(wordLabel.get(i)[2].equals("b_nr") || wordLabel.get(i)[2].equals("i_nr"))
			{
				wordLabel.get(i)[2] = "PER";
			}
			if(wordLabel.get(i)[2].equals("b_ns") || wordLabel.get(i)[2].equals("i_ns"))
			{
				wordLabel.get(i)[2] = "LOC";
			}
			if(wordLabel.get(i)[2].equals("b_nt") || wordLabel.get(i)[2].equals("i_nt"))
			{
				wordLabel.get(i)[2] = "ORG";
			}
			if(wordLabel.get(i)[2].equals("b_t") || wordLabel.get(i)[2].equals("i_t"))
			{
				wordLabel.get(i)[2] = "TIME";
			}
		}
		return wordLabel;
	}
	
	Map<String, double[]> evaluation(String testfile, String resultfile)
	{
		Map<String, double[]> evaluation = new HashMap<>();
		String[] Label = {"OTHER", "PER", "LOC", "ORG", "TIME"};
		for(String temp : Label)
		{
			double[] array = {0.0, 0.0, 0.0};
			evaluation.put(temp, array);
		}
		Util u = new Util();
		List<String> sentenceTest = u.loadFile(testfile);
		List<String> sentenceResult = u.loadFile(resultfile);
		for(int i = 0; i < sentenceTest.size(); ++i)
		{
			List<String[]> tempTest = u.simplifyLabel(u.genWordPosLabel(sentenceTest.get(i), 0));
			List<String[]> tempResult = u.genWordPosLabel(sentenceResult.get(i),0);
			for(int j = 0; j < tempTest.size(); ++j)
			{
				if(tempTest.get(j)[tempTest.get(j).length-1].equals(tempResult.get(j)[tempResult.get(j).length-1]))
				{
					evaluation.get(tempTest.get(j)[tempTest.get(j).length-1])[0] += 1.0;
				}
				
				if(evaluation.containsKey(tempResult.get(j)[tempResult.get(j).length-1]))
				{
					evaluation.get(tempResult.get(j)[tempResult.get(j).length-1])[1] += 1.0;
				}
				
				if(evaluation.containsKey(tempTest.get(j)[tempTest.get(j).length-1]))
				{
					evaluation.get(tempTest.get(j)[tempTest.get(j).length-1])[2] += 1.0;
				}
			}
		}
		
		return evaluation;
	}
}
