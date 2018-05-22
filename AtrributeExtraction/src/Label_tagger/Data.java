package Label_tagger;

import java.util.ArrayList;
import java.util.List;

public class Data {
	
	public static Instance read_instance(String line,String mod){
		Instance instance=new Instance();
//		if(mod.equals("train")){
//			
//		}else if(mod.equals("test")){
//			List<String> words=new ArrayList<>();
//			List<String> postags=new ArrayList<String>();
//			String[] w=line.split(" ");
//			for(int i=0;i<w.length;i++){
//				words.add(w[i].split("_")[0]);
//				postags.add(w[i].split("_")[1]);
//			}
//			instance.setWords(words);
//			instance.setPostags(postags);
//		}
		List<String> words=new ArrayList<String>();
		List<String> postags=new ArrayList<String>();
		List<String> tags=new ArrayList<String>();
		List<Integer> index=new ArrayList<>();
		String[] array = line.split("\t");
		for(int i=0;i<array.length-1;i++) index.add(Integer.parseInt(array[i]));
		line = array[array.length-1];
		String[] w=line.split(" ");
		for(int i=0;i<w.length;i++){
			String[] t=w[i].split("_");
			if(t.length<3){
				System.out.println("training data format wrong!the sentence is："+line);
				return null;
			}else if(t.length==3){
				words.add(t[0]);
				postags.add(t[1]);
				tags.add(t[2]);
			}else{
				int len=t.length;
				String t0="";
				for(int j=0;j<len-2;j++)
					t0+=t[j];
				words.add(t0);
				postags.add(t[len-2]);
				tags.add(t[len-1]);
			}
		}
		instance.setIndex(index);
		instance.setWords(words);
		instance.setPostags(postags);
		instance.setLabels(tags);
		return instance;
	}
}
