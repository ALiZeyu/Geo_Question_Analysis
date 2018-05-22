package Label_tagger;

public class State {
	int index;
	double score;
	String tag;
	State prev_state;
	int tag_index;
	
	public State(int index,double score,String tag,State prev_state,int tag_index) {
		this.index=index;
		this.score=score;
		this.tag=tag;
		this.prev_state=prev_state;
		this.tag_index=tag_index;
	}
}
