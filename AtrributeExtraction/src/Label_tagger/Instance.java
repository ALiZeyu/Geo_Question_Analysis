package Label_tagger;

import java.util.List;
/**
 * 每句话是一个instance
 * */
public class Instance {
	
	/**
	 *words	这句话
	 *tags	每个词对应的词性，训练时有用
	 *indices	这个instance所抽取出来的特征，存的是特征编号
	 *predict_tags	预测的词性
	 **/
	List<String> words;
	List<Integer> index;
	List<String> postags;
	List<String> labels;
	List<List<Integer>> indices;
	List<String> predict_tags;
	int trigger = -1;
	
	public List<Integer> getIndex() {
		return index;
	}
	public void setIndex(List<Integer> index) {
		this.index = index;
	}
	
	public int getTrigger() {
		return trigger;
	}
	public void setTrigger(int trigger) {
		this.trigger = trigger;
	}
	public List<String> getWords() {
		return words;
	}
	public void setWords(List<String> words) {
		this.words = words;
	}
	public List<String> getLabels() {
		return labels;
	}
	public void setLabels(List<String> tags) {
		this.labels = tags;
	}
	public void setPostags(List<String> postags){
		this.postags=postags;
	}
	public List<List<Integer>> getIndices() {
		return indices;
	}
	public void setIndices(List<List<Integer>> indices) {
		this.indices = indices;
	}
}