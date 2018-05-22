package Ner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Term 
{
	double score;
	int index;
	String label;
	Term pre;

	/**
	 * @param score
	 * @param link 指向前一个词
	 */
	Term(double score, int index, String label, Term pre)
	{
		this.score = score;
		this.index = index;
		this.label = label;
		this.pre = pre;
	} 
	
	boolean isEmpty()
	{
		if(this.index == -1)
			return true;
		else
			return false;
	}
	
	// 回溯取回状态
	List<Term> backTrace(Term term)
	{
		List<Term> result = new ArrayList<>();
		while(term.index != -1)
		{
			result.add(term);
			term = term.pre;
		}
		Collections.reverse(result);
		return result;
	}
}
