package pos_tagger;

import java.util.ArrayList;
import java.util.List;

public class Data {
	
	public static Instance read_instance(String line,String mod){
		Instance instance=new Instance();
		if(mod.equals("train")){
			List<String> words=new ArrayList<String>();
			List<String> tags=new ArrayList<String>();
			String[] w=line.split(" ");
			for(int i=0;i<w.length;i++){
				String[] t=w[i].split("_");
				if(t.length<2){
					System.out.println("training data format wrong!the sentence is："+line);
					return null;
				}else if(t.length==2){
					words.add(t[0]);
					tags.add(t[1]);
				}else{
					int len=t.length;
					String t0="";
					for(int j=0;j<len-1;j++)
						t0+=t[j];
					words.add(t0);
					tags.add(t[len-1]);
				}
			}
			instance.setWords(words);
			instance.setTags(tags);
		}else if(mod.equals("test")){
			List<String> words=new ArrayList<>();
			String[] w=line.split(" ");
			for(int i=0;i<w.length;i++){
				words.add(w[i]);
			}
			instance.setWords(words);
		}
		return instance;
	}
	
//	public static List<String> read_training_file(String file){
//		
//	}
}
