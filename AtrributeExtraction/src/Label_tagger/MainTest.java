package Label_tagger;

import java.util.ArrayList;
import java.util.List;

import Evaluate.Evaluate;
import common.MyUtil;

public class MainTest {

	public static void main(String[] args) {
//		LabelTag tag=new LabelTag();
//		List<String> a=new ArrayList<>();
//      tag.train();
//      tag.tagging();
//      tag.eva_result(a);
//		List<String> train=tag.read_trainFile();
//		tag.get_evaFile(train);
//		tag.run_10cv(train);
		five_fold("data/all_data/EA_shuffle.txt");
		//MyUtil.EA_extraction("data/all_data/EA_shuffle.txt");
		//Evaluate.rule_count("E://workspace/AtrributeExtraction/data/chenshu_full_result.txt");
		//Evaluate.rule_count_bijiao("E:/geo_data/com_entity_3_result.txt");
	}
	
	public static void five_fold(String path){
		List<String> data = MyUtil.read_file(path);
		List<List<String>> split = new ArrayList<>();
		int k=5;
		//构造交叉验证数据集
		for(int i=0;i<k;i++) split.add(new ArrayList<>());
		int t = data.size() / k, i = 0;
		while(i < data.size()){
			split.get(i/t).add(data.get(i));
			i++;
		}
		double precision = 0, recall = 0, f_score = 0, seg_acu = 0;
		for(i=0;i<k;i++){
			List<String> s_train = new ArrayList<>();
			for(int j=0;j<k;j++)
				if(j!=i)
					s_train.addAll(split.get(j));
			MyUtil.writeList(s_train, "data/all_data/train.txt");
			MyUtil.writeList(split.get(i), "data/all_data/test.txt");
			List<Double> result = new ArrayList<>();
			LabelTag tag=new LabelTag();
			List<String> a=new ArrayList<>();
			tag.train();
			tag.tagging(result);
			precision+=result.get(0);
			recall+=result.get(1);
			f_score+=result.get(2);
			seg_acu+=result.get(3);
			System.out.println(result);
		}
		System.out.println("precision: "+(precision/k));
		System.out.println("recall: "+(recall/k));
		System.out.println("f_score: "+(f_score/k));
		System.out.println("seg_accuracy: "+(seg_acu/k));
	}
}
