package common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.print.DocFlavor.STRING;


public class PrePro {

	public static void main(String[] args) {
		//del2_count3("data/chenshu_attri.txt");
		//build_chenshu_corpus("data/chenshu_.txt");
		//比较类：E://geo_data/com_entity_3_result.txt
//		rule_count("E://workspace/AtrributeExtraction/data/chenshu_full_result.txt");
//		putlabel("E://geo_data/com_entity_1.txt", "E://geo_data/com_entity_3.txt");
		//putlabel_chenshu("E://workspace/AtrributeExtraction/data/chenshu_full.txt");
		shuffleCorpus("data/type_EA.txt");
		//trigger_extract("E://workspace/GeoScholar/data/templating/cuewords_new.txt");
	}
	//比较类句子实体和属性分离，原始文件是"E://geo_data/com_entity_1.txt", "E://geo_data/com_entity_3.txt"	
	public static void putlabel(String p1, String p2){
		List<String> pos=MyUtil.read_file(p1);
		List<String> label=MyUtil.read_file(p2);
		List<String> result=new ArrayList<>();
		int i=0,j=0;
		while(j<label.size()){
			String pos_sen = labelToSen(pos.get(i));
			String label_sen = labelToSen(label.get(j));
			if(pos_sen.contains(label_sen)){
				List<String> list=new ArrayList<>(Arrays.asList(pos.get(i).split(" ")));
				int begin = Integer.parseInt(label.get(j).split(" ")[0].split("_")[1]);
				if(begin!=0){
					for(int pp=0;pp<begin;pp++) list.remove(0);
				}
				//int e1b=-1,e1e=-1,a1b=-1,a1e=-1,e2b=-1,e2e=-1,a2b=-1,a2e=-1;
				int[] index=new int[8];
				for(int pp=0;pp<8;pp++) index[pp]=-1;
				String[] nums=label.get(j+1).split(";");
				if(nums[2].contains("、")){
					if(nums[0].contains("-")){
						index[0]=Integer.parseInt(nums[0].split("-")[0])-begin;
						index[1]=Integer.parseInt(nums[0].split("-")[1])-begin;
					}else
						index[0]=Integer.parseInt(nums[0].split("-")[0])-begin;
					
					if(nums[1].contains("-")){
						index[4]=Integer.parseInt(nums[1].split("-")[0])-begin;
						index[5]=Integer.parseInt(nums[1].split("-")[1])-begin;
					}else
						index[4]=Integer.parseInt(nums[1].split("-")[0])-begin;
					
					String[] numss=nums[2].split("、");
					if(numss[0].contains("-")){
						index[2]=Integer.parseInt(numss[0].split("-")[0])-begin;
						index[3]=Integer.parseInt(numss[0].split("-")[1])-begin;
					}else
						index[2]=Integer.parseInt(numss[0].split("-")[0])-begin;
					
					if(numss[1].contains("-")){
						index[6]=Integer.parseInt(numss[1].split("-")[0])-begin;
						index[7]=Integer.parseInt(numss[1].split("-")[1])-begin;
					}else
						index[6]=Integer.parseInt(numss[1].split("-")[0])-begin;
					
				}else{
					if(nums[0].contains("-")){
						index[0]=Integer.parseInt(nums[0].split("-")[0])-begin;
						index[1]=Integer.parseInt(nums[0].split("-")[1])-begin;
					}else
						index[0]=Integer.parseInt(nums[0].split("-")[0])-begin;
					
					if(nums[2].contains("-")){
						index[2]=Integer.parseInt(nums[2].split("-")[0])-begin;
						index[3]=Integer.parseInt(nums[2].split("-")[1])-begin;
					}else
						index[2]=Integer.parseInt(nums[2].split("-")[0])-begin;
				}
				list.set(index[0], list.get(index[0])+"_EB");
				for(int t=index[0]+1;t<index[1];t++)
					list.set(t, list.get(t)+"_EI");
				if(index[1]>0) list.set(index[1], list.get(index[1])+"_EE");
				
				list.set(index[2], list.get(index[2])+"_AB");
				for(int t=index[2]+1;t<index[3];t++)
					list.set(t, list.get(t)+"_AI");
				if(index[3]>0) list.set(index[3], list.get(index[3])+"_AE");
				
				if(index[4]>0) list.set(index[4], list.get(index[4])+"_EB");
				if(index[4]>0) 
					for(int t=index[4]+1;t<index[5];t++)
						list.set(t, list.get(t)+"_EI");
				if(index[5]>0) list.set(index[5], list.get(index[5])+"_EE");
				
				if(index[6]>0) list.set(index[6], list.get(index[6])+"_AB");
				if(index[6]>0) 
					for(int t=index[6]+1;t<index[7];t++)
						list.set(t, list.get(t)+"_AI");
				if(index[7]>0) list.set(index[7], list.get(index[7])+"_AE");
				
				for(int t=0;t<list.size();t++)
					if(list.get(t).indexOf("_")==list.get(t).lastIndexOf("_"))
						list.set(t, list.get(t)+"_O");
				StringBuilder sb=new StringBuilder();
				for(String ss:list) sb.append(ss+" ");
				result.add(sb.toString().trim());
				i+=2;j+=2;
			}else{
				i+=2;
			}
		}
		MyUtil.writeList(result, "E://geo_data/labelData.txt");
	}
	
	//陈述类句子打标签，原始文件是data/chenshu_full.txt
	public static void putlabel_chenshu(String p2){
		List<String> data = MyUtil.read_file(p2);
		List<String> result = new ArrayList<>();
		int i=0;
		while(i<data.size()){
			String[] array = extract(data.get(i).trim());
			String[] index = data.get(i+1).split(";",4);
			String[][] tag = {{"_EB","_EI","_EE"},{"_EB","_EI","_EE"},{"_AB","_AI","_AE"}};
			for(int j=0;j<3;j++)
				if(index[j].length()>0)
					put_single_label(index[j], array, tag[j]);
			for(int j=0;j<array.length;j++)
				if(!(array[j].endsWith("_EB")||array[j].endsWith("_EI")||array[j].endsWith("_EE")||
						array[j].endsWith("_AB")||array[j].endsWith("_AI")||array[j].endsWith("_AE")))
					array[j]+="_O";
			result.add(MyUtil.array2String(array));
			i+=2;
		}
		MyUtil.writeList(result, "data/chenshu_labelData.txt");
	}
	
	//打上某一类标签：实体或者属性
	public static void put_single_label(String i, String[] array, String[] label){
		String[] index = i.split("、");
		for(String str : index){
			String[] l = str.split("-");
			if(l.length==1)
				array[Integer.parseInt(l[0])]+=label[0];
			else{
				int b = Integer.parseInt(l[0]);
				int e = Integer.parseInt(l[1]);
				array[b] += label[0];
				array[e] += label[2];
				for(int t=b+1;t<e;t++)
					array[t] += label[1];
			}
		}
	}
	
	//从带有序号的句子中抽出分词、词性结果
	public static String[] extract(String sen){
		String[] temp = sen.split("\t");
		int len = temp.length;
		String[] array = new String[len];
		for(int i=0;i<len;i++){
			array[i] = temp[i].substring(0, temp[i].lastIndexOf("_"));
		}
		return array;
	}
	
	//令比较和陈述类交叉排列
	public static void shuffleCorpus(String path){
		List<String> data = MyUtil.read_file(path);
		//Collections.shuffle(data);
		
		List<String> result = new ArrayList<>();
		int i1=0, i2=525, gap=4;
		while(i1<525||i2<data.size()){
			if(i1<525){
				int top = Math.min(525, i1+gap);
				for(int i=i1;i<top;i++)
					result.add(data.get(i));
				i1=top;
			}
			if(i2 < data.size()){
				int top = Math.min(data.size(), i2+gap);
				for(int i=i2;i<top;i++)
					result.add(data.get(i));
				i2=top;
			}
		}
		MyUtil.writeList(result, "data/all_data/EA_shuffle.txt");
	}
	
	public static String get_origin(String sen){
		String[] array = sen.split("\t");
		return array[array.length-1];
	}
	
	/***/
	public static String labelToSen(String str){
		StringBuilder sb = new StringBuilder();
		for(String word:str.split(" ")){
			sb.append(word.split("_")[0]);
		}
		return sb.toString();
	}
	
	public static void del2_count3(String path){
		List<String> list = MyUtil.read_file(path);
		Iterator<String> it = list.iterator();
		int triple = 0;
		while(it.hasNext()){
		    String x = it.next().trim();
		    if(!x.equals(";;;")){
		    	if(x.split("\t").length == 2){
		    		it.remove();
		    		it.next();
		    		it.remove();
		    	}
		    	else if(x.split("\t").length == 3)
		    		triple++;
		    }
		}
		MyUtil.writeList(list, "data/chenshu_.txt");
		System.out.println(triple);
	}
	//补全标注数据：1、是否和上一句话长度一致；2、长度3、4默认为0;;1;
	public static void build_chenshu_corpus(String path){
		List<String> list = MyUtil.read_file(path);
		int i=0;
		while(i<list.size()){
			//System.out.println(list.get(i));
			if(list.get(i+1).trim().equals(";;;")){
				int l1 = list.get(i).split("\t").length;
				int l2 = list.get(i-2).split("\t").length;
				if(l1==l2)
					list.set(i+1, list.get(i-1));
				else if(l1==3||l1==4)
					list.set(i+1, "0;;1");
				else 
					System.out.println(list.get(i));
			}else{
				String[] a = list.get(i+1).trim().split(";",4);
				if(a[1].length()!=0&&(a[0].length()==0||a[2].length()==0))
					System.out.println(list.get(i));
			}
			i+=2;
		}
		MyUtil.writeList(list, "data/chenshu_full.txt");
	}
	//统计根据规则方法得到的实体及属性的正确率和召回率。
	//正确率：正确的/自己分出来的。召回率：正确的、答案里面的
	public static void rule_count(String path){
		List<String> data = MyUtil.read_file(path);
		int i=1,right=0,precision=0,recall = 0;
		while(i<data.size()){
			int b=i;
			i++;
			while(data.get(i).startsWith("slot"))
				i++;
			int max = 0;
			List<Integer> count = new ArrayList<>();
			for(int t=b;t<i;t++){
				List<Integer> temp = single_count(data.get(t), data.get(i));
				if(temp.get(0)>=max){
					count=temp;
					max = temp.get(0);
				}
			}
			i+=2;
			if(count.size()!=0){
				right+=count.get(0);
				precision+=count.get(1);
				recall+=count.get(2);
			}else{
				System.out.println(data.get(i-1));
			}
			
		}
		System.out.println(right+"\t"+precision+"\t"+recall);
		System.out.println((double)right/precision);
		System.out.println((double)right/recall);
	}
	
	public static List<Integer> single_count(String s, String standard){
		List<Integer> r = new ArrayList<>();
		String[] slot = s.substring(s.indexOf("(") + 1, s.length()-1).split(",");
		String[] answer = standard.split(";");
		int right = 0, precision = 0;
		System.out.println(s);
		for(int i=0;i<4 && slot.length>=4;i++){
			if(!slot[i].equals("null") && !slot[i].equals("###")){
				precision++;
				if((i&1)==0){
					if(slot[i].equals(answer[0]) || slot[i].equals(answer[1]))
						right++;
				}else{
					int c = answer.length==2?1:2;
					if(answer[c].contains(slot[i]))
						right++;
				}
			}
		}
		for(int i=0;i<2 && slot.length==3;i++){
			if(!slot[i].equals("null") && !slot[i].equals("###")){
				precision++;
				if(i==0){
					if(slot[i].equals(answer[0]))
						right++;
				}else if(answer.length==2){
					if(answer[1].contains(slot[i]))
						right++;
				}
			}
		}
		r.add(right);
		r.add(precision);
		r.add(answer.length);
		return r;
	}
	
	//抽取句子中的触发词
	public static void trigger_extract(String path){
		List<String> list = MyUtil.read_file(path);
		Set<String> triggers = new HashSet<>();
		for(String line : list){
			if(line.split("\t")[1].equals("6")){
				String word = line.split("\t")[0].split("_")[0];
				triggers.add(word);
			}
		}
		
		List<String> data = MyUtil.read_file("data/EA_Data.txt");
		List<String> result = new ArrayList<>();
		for(int i=0; i<data.size(); i++){
			if(i<525)
				result.add("-1\t"+data.get(i));
			else{
				String[] array = data.get(i).split(" ");
				List<Integer> index = new ArrayList<>();
				for(int j=0;j<array.length;j++)
					if(triggers.contains(array[j].split("_")[0]) && !array[j].contains("较_AD"))
						index.add(j);
				String temp = "";
				if(index.size()==0){
					temp = "\t"+data.get(i);
					System.out.println(temp);
				}else if(index.size()==1){
					temp = index.get(0)+"\t"+data.get(i);
				}else{
					for(Integer ii : index)
						temp += ii+"\t";
					temp += data.get(i);
					System.out.println(temp);
				}
				result.add(temp);
			}
		}
		MyUtil.writeList(result, "data/type_EA.txt");
	}

}
