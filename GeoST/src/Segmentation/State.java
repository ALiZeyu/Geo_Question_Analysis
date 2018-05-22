package Segmentation;

public class State {
	/*
	 * score:到当前汉字时总的得分
	 * index:当前汉字在句子中的位置
	 * link:上一个汉字的State对象
	 * action：当前对应的操作
	 * prev:上一个词首字State
	 * curr:当前词首字State
	 */
	double score;
	int index;
	State link;
	char action;
	State prev;
	State curr;
	
	State(double score,	int index,	State link,	char action){
		this.score=score;
		this.index=index;
		this.link=link;
		this.action=action;
		
		if(action=='A'){
			this.prev=link.prev;
			this.curr=link.curr;
		}
		else if(action=='S'){
			this.prev=link.curr;
			this.curr=this;
		}
		else{
			this.prev=null;
			this.curr=this;
		}
	}
	public State() {
	}
	
	boolean empty(){
		if(this.index==-1)
			return true;
		return false;
	}

}
