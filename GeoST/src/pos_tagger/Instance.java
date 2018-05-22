package pos_tagger;

import java.util.List;
/**
 * 每句话是一个instance
 * */
public class Instance {
	/**
	 *words	这句话
	 *tags	每个词对应的词性，训练时有用
	 *indices	这个instance所抽取出来的特征，存的是特征编号
	 *predict_tags	预测的词性*/
	List<String> words;
	List<String> tags;
	List<List<Integer>> indices;
	List<String> predict_tags;
	
	public List<String> getWords() {
		return words;
	}
	public void setWords(List<String> words) {
		this.words = words;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public List<List<Integer>> getIndices() {
		return indices;
	}
	public void setIndices(List<List<Integer>> indices) {
		this.indices = indices;
	}
}