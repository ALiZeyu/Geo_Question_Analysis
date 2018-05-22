package Segmentation;


public class ParamList {
	double avgValue;
	double timestamp;
	double value;
	
	public ParamList() {
		this.avgValue=0.0;
		this.timestamp=0.0;
		this.value=0.0;
	}
	
	public ParamList(double avgValue,double now,double value) {
		this.avgValue=avgValue;
		this.timestamp=now;
		this.value=value;
	}
	
	public ParamList(double avgValue) {
		this.avgValue=avgValue;
	}
	
	public void setValue(double avgValue,double now,double value){
		this.avgValue=avgValue;
		this.timestamp=now;
		this.value=value;
	}
}
