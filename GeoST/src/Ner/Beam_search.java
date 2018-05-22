package Ner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class Beam_search 
{
	
	/**
	 * 运行beam search
	 * @param wordListence 原始句子
	 * @param beam_size beam大小
	 * @param params double数组中[0：参数总和，1：上次更新的时间，2：参数的值]
	 * @param actions 由Term的label生成 ???
	 * @param now 第i次迭代的第j个句子序号
	 * @param mode 0表示训练，1表示测试
	 * @return 
	 */
	List<Term> runBeamSearch(String sentence, int beamSize, Map<String,double[]> params, double now, int mode)
	{

//		String[] Label = {"other", "b_nr", "i_nr", "b_ns", "i_ns", "b_nt", "i_nt", "b_t", "i_t"};
		//周浩修改，标签只要时间地点相关的
		String[] Label = {"other", "b_ns", "i_ns", "b_t", "i_t"};
		Util u = new Util();
		List<String[]> wordList = u.genWordPosLabel(sentence, mode);

		//beam search
		ArrayList<Term> agenda = new ArrayList<Term>();
		ArrayList<Term> tempAgenda = new ArrayList<Term>();
		Term first = new Term(0.0, -1, null, null);
		tempAgenda.add(first);
		
		for(int i = 0; i < wordList.size(); ++i)
		{
			agenda.clear();
			for(Term term : tempAgenda)
			{
				for(String label : Label)
				{
					//对每个单词标记label，循环计算哪种标记的得分最高
					double score = computScore(wordList, i, new Term(0.0, i, label, term), params, mode);
					updateAgenda(agenda, new Term(term.score + score, i, label, term), beamSize);
				}
				
			}
			tempAgenda.clear();
			tempAgenda.addAll(agenda);
		}
		
		List<Term> predictTerm = new ArrayList<>();
		if(mode == 0)
		{
			/*使用正确划分与预测划分相比较的方式更新paras map，用来computeScore*/
			List<Term> correctTerm = u.genTerm(wordList);
			String[] correctLabels = extractLabels(correctTerm.get(correctTerm.size() - 1));
			
			Term best = agenda.get(agenda.size() - 1);
			predictTerm = best.backTrace(best);
			assert(predictTerm.size()==correctTerm.size());
			String[] predictLabels = extractLabels(agenda.get(agenda.size() - 1));

			for(int i = 0; i < wordList.size(); ++i)
			{
				List<String> correctFeatures = extractFeature(wordList, correctLabels, i);
				List<String> predictFeatures = extractFeature(wordList, predictLabels, i);
				double correctScale = 1.0;
				double predictScale = -1.0;

				lazy_update(params, correctFeatures, now, correctScale);
				lazy_update(params, predictFeatures, now, predictScale);
			}
		}
		else
		{
			Term best = agenda.get(agenda.size() - 1);
			predictTerm = best.backTrace(best);
			
		}
		
		return predictTerm;
	}
	
	
	/**
	 * 根据term的标签生成action，从后往前生成
	 * @param term 大小为sen.size
	 */
	String[] extractLabels(Term t)
	{		List<Term> term = t.backTrace(t);
		String[] labels = new String[term.size()];
		for(int i = 0; i < term.size(); ++i)
		{
			labels[i] = term.get(i).label;
		}
		return labels;
	}
	
	/**
	 * 更新beam search中的项，保证其中个数最大为beam_size
	 * @param agenda
	 * @param term
	 * @param beam_size
	 */
	//更新dstAgenda，保证term个数小于等于beam_size
	void updateAgenda(List<Term> agenda, Term term, int beam_size)
	{
		Comparator<Term> cmp = new Comparator<Term>() 
		{
			public int compare(Term t1, Term t2)
			{
				if(t1.score >= t2.score)
					return 1;
				else 
					return -1;
			}
		};
		
		if(agenda.size() < beam_size)
		{
			agenda.add(term);
			Collections.sort(agenda, cmp);
		}
		else if(term.score > agenda.get(0).score)
		{
			agenda.set(0, term);
			Collections.sort(agenda, cmp);
		}
	}
	
	/** 计算生成的句子片段得分
	 * @param wordList
	 * @param i 生成sent中到i为止句子的标签
	 * @param term
	 * @param params double数组中[0：参数总和，1：上次更新的时间，2：参数的值]
	 * @param mode 0表示训练，1表示测试
	 * @return
	 */
	//计算wordList[0...i]片段的得分
	double computScore(List<String[]> wordList, int i, Term term, Map<String, double[]> params, int mode)
	{
		double result = 0.0;
		
		if(term.isEmpty())
		{
			return result;
		}
		
		String[] labels = extractLabels(term);
		List<String> features = extractFeature(wordList, labels, i);
		
		for(String feature : features)
		{
			if(params.containsKey(feature))
			{
				if(mode == 0)
					result += params.get(feature)[2];
				else
					result += params.get(feature)[0];
			}
		}
		return result;
	}
	
	void lazy_update(Map<String, double[]> params, List<String> features, double now, double scale)
	{
		for(String feature:features)
		{
			if(!params.containsKey(feature))
			{
				double[] temp = {scale, now, scale};
				params.put(feature, temp);
			}
			else
			{
				double elapsed = now - params.get(feature)[1];
				double currVal = params.get(feature)[2];
				double currSum = params.get(feature)[0];
				
				/*意义???*/
				params.get(feature)[0] = currSum + elapsed * currVal+scale;
				params.get(feature)[1] = now;
				params.get(feature)[2] = currVal + scale;
			}
		}
	}
	
	/** 判断字符的类型，分为数字(D)、字母(L)、汉字(H)和其他符号(O) */
	String charType(String word)
	{
		String chr = "";
		
		for(int i = 0; i < word.length(); ++i)
		{
			char ch = word.charAt(i);
			
			if(	ch=='\u96f6' || ch=='\u4e00'|| ch=='\u4e8c' || ch=='\u4e09' || ch=='\u56db' || ch=='\u4e94' || ch=='\u516d' ||ch=='\u4e03' 
					|| ch=='\u516b' || ch=='\u4e5d' || ch=='\u5341' || ch=='\u767e' || ch=='\u5343' || ch=='\u4e07' || ch=='\u4ebf' )
				chr += 'D';
			else if(ch >= '\u0030' && ch <= '\u0039')
				chr += 'd';
			else if((ch >= '\u0041' && ch <= '\u005a')||(ch >= '\u0061' && ch<= '\u007a'))
				chr += 'l';
			else if(ch >= '\u4e00'&& ch<= '\u9fa5')
				chr += 'h';
			else
				chr += 'o';
		}
		return chr;
	}
	
	
	/**
	 * 对某个词生成相应的特征(词性标注已加)
	 * @return
	 */
	List<String> extractFeature(List<String[]> wordList, String[] labels, int i)
	{
//周浩修改，去除了关于词性的特征		
		
		List<String> features = new ArrayList<>();
		
		String prev_word, curr_word, next_word;
//		String prev_pos, curr_pos, next_pos;
		curr_word = wordList.get(i)[0];
//		curr_pos = wordList.get(i)[1];
		if(i == 0)
		{//start sign
			prev_word = "####";
//			prev_pos = "γγγγ";
		}
		else
		{
			prev_word = wordList.get(i-1)[0];
//			prev_pos = wordList.get(i-1)[1];
		}
		if(i == wordList.size()-1)
		{//end sign
			next_word = "$$$$";
//			next_pos = "δδδδ";
		}
		else
		{
			next_word = wordList.get(i+1)[0];
//			next_pos = wordList.get(i+1)[1];
		}
		String prev_cht = charType(prev_word);
		String curr_cht = charType(curr_word);
		String next_cht = charType(next_word);

		features.add("1=word[-1]=" + prev_word + "Label=" + labels[i]);
		features.add("2=word[0]=" + curr_word + "Label="+ labels[i]);
		features.add("3=word[+1]=" + next_word +"Label=" + labels[i]);
		features.add("4=word[-1]word[0]="+ prev_word + curr_word + "Label=" + labels[i]);
		features.add("5=word[0]word[+1]="+ curr_word + next_word + "Label=" + labels[i]);
		features.add("6=word[-1]word[0]word[+1]="+ prev_word + curr_word + next_word + "Label=" + labels[i]);
		features.add("7=cht[-1]=" + prev_cht + "Label=" + labels[i]);
		features.add("8=cht[0]=" + curr_cht + "Label=" + labels[i]);
		features.add("9=cht[+1]=" + next_cht + "Label=" + labels[i]);
		features.add("10=cht[-1]cht[0]=" + prev_cht + curr_cht + "Label=" + labels[i]);
		features.add("11=cht[0]cht[+1]=" + curr_cht + next_cht + "Label=" + labels[i]);
		features.add("12=cht[-1]cht[0]cht[+1]=" + prev_cht + curr_cht + next_cht + "Label=" + labels[i]);
//周浩修改
/*
		features.add("13=pos[-1]=" + prev_pos + "Label=" + labels[i]);
		features.add("14=pos[0]=" + curr_pos + "Label=" + labels[i]);
		features.add("15=pos[+1]=" + next_pos + "Label=" + labels[i]);
		features.add("16=pos[-1]pos[0]=" + prev_pos + curr_pos + "Label=" + labels[i]);
		features.add("17=pos[0]pos[+1]=" + curr_pos + next_pos + "Label=" + labels[i]);
		features.add("18=pos[-1]pos[0]pos[+1]=" + prev_pos + curr_pos + next_pos + "Label=" + labels[i]);
*/		
		return features;
	}
	
}
