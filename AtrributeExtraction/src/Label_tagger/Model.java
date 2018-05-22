package Label_tagger;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Model implements Serializable{
	/**
	 * tags_alphabet	词性label
	 * tags	
	 * feature_alphabet	所有特征
	 * W	
	 * Wsum	*/
	private static final long serialVersionUID = 1L;
	Map<String, Integer> tags_alphabet;
	List<String> tags;
	Map<String, Integer> feature_alphabet;
	double[] W;  
	double[] Wsum;
	
	public Model(Map<String, Integer> t,List<String> tags,Map<String, Integer> f,double[] W,double[] Wsum) {
		this.tags_alphabet=t;
		this.tags=tags;
		this.feature_alphabet=f;
		this.W=W;
		this.Wsum=Wsum;
	}
	
	public Model(){
		
	}
	
}
