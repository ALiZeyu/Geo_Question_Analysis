package cn.edu.nju.ws.GeoScholar.templating.choice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Print;
import cn.edu.nju.ws.GeoScholar.templating.common.Tree;

public class ZhiShiSolution1 {
	static Set<String> spwords;
	public static String findSecond(Tree word, ArrayList<String> sentence) {
		String s = "";
		int k = word.no;
		for (int i = k; i < sentence.size(); i++)
			s += sentence.get(i).split("_")[0];
		return s;
	}
	
	public static String findFirst(Tree word, ArrayList<String> sentence) {
		//从触发词向前句首
		String s = "";
		int k = word.no - 2;
		String[] spArray={"分别","依次","及"};
		List<String> separateList=new ArrayList<>(Arrays.asList(spArray));
		//表示为只留下为
		if (k > 0 && sentence.get(k).split("_")[0].equals("表示")) k--;
		if (k > 0 && sentence.get(k).split("_")[0].equals("一般")) k--;
		if (k > 0 && sentence.get(k).split("_")[0].equals("应该")) k--;
		if (k > 0 && sentence.get(k).split("_")[0].equals("应")) k--;
		if (k > 0 && sentence.get(k).split("_")[0].equals("分别")) {k--;spwords.add("分别");}
		if (k > 0 && sentence.get(k).split("_")[0].equals("依次")) {k--;spwords.add("依次");}
		if (k > 0 && sentence.get(k).split("_")[0].equals("主要")) k--;
		if (k > 0 && sentence.get(k).split("_")[0].equals("约")) k--;
		if (k > 0 && sentence.get(k).split("_")[0].equals("大约")) k--;
		if (k > 0 && sentence.get(k).split("_")[0].equals("，")) k--;
		while(k>=0){
			if(separateList.contains(sentence.get(k).split("_")[0]))
				spwords.add(sentence.get(k).split("_")[0]);
			s=sentence.get(k).split("_")[0]+s;
			k--;
		}
		return s;
	}	
	
	public static String print(Tree all, Tree node, ArrayList<String> sentence) {
		spwords=new HashSet<>();
		String result="";
		String[] array=new String[2];
		array[0]=findFirst(node, sentence);
		array[1]=findSecond(node, sentence);
		//第一种情况：选项中有顿号
		if(array[1].contains("、")){
			String[] sec=array[1].split("、");
			int len=sec.length;
			//题干中有顿号，选项中也有
			if(array[0].contains("、")){
				//选项利用顿号分隔，判断切分数量是否一致
				int id=sentence.indexOf("、_PU");
				if(id<0) return array[0]+"@"+array[1]+"@###";
				Tree dnode=Tree.findNodeByNo(all, id+1);
				if(dnode.parent.parent!=null && dnode.parent.parent.content.equals("NP")){
					String cover = Print.print(dnode.parent.parent);
					if(array[0].contains(cover)){
						String[] sp=cover.split("、");
						if(sp.length==len){
							//青岛、合恩角、好望角三地典型植被景观分别为	温带落叶阔叶林、温带落叶阔叶林、亚热带常绿阔叶林
							for(int i=0;i<len;i++){
								String first=array[0].replace(cover, sp[i]);
								result+=first+ "@" +sec[i]+ "@" +"###&&&";
							}
							return result;
						}else if(sp.length+1==len && sp[sp.length-1].contains("及")){
							//与图示地区山体岩石、地貌类型及其形成的外力作用对应的是	石灰岩、球状风化地貌、风力作用
							String[] js=sp[sp.length-1].split("及");
							if(js.length==2){
								String[] new_sp=new String[len];
								for(int i=0;i<sp.length-1;i++)
									new_sp[i]=sp[i];
								new_sp[len-2]=js[0];
								new_sp[len-1]=js[1];
								for(int i=0;i<len;i++){
									String first=array[0].replace(cover, new_sp[i]);
									result+=first+ "@" +sec[i]+ "@" +"###&&&";
								}
								return result;
							}
						}
					}else {
						System.out.println("指示拆分，选项中各部分的覆盖不在槽一");
					}
				}
			}
			//选项中有顿号，题干中有“及”，题干根据“及”分开，向前找第一个“的”A的B及C是。。-》A的B是+A的C是
			if(len==2 && spwords.contains("及")&&!array[0].contains("及以上")&&!array[0].contains("涉及")){
				//到2034年，我国0—15岁和60岁及以上人口比重将分别达到	58%、24%|由乙转化为甲过程中涉及到的地质作用可能有	重熔再生作用、地壳运动
				String[] sp=array[0].split("及");
				/*及其的处理方式，如果只有及没有其，那么向前找的，找到拼接，找不到直接分开
				 * 如果含有其，那么向前找的，找到拼接，找不到拼接A的B*/
				if(sp.length==2){
					String[] new_sp=processJi(array[0]);				
					for(int i=0;i<len;i++){
						result+=new_sp[i]+ "@" +sec[i]+ "@" +"###&&&";
					}
					return result;
				}
			}
			//题干中有①,②，选项中有顿号
			if(extractNum(array[0]).size()==len){
				List<Character> numList=extractNum(array[0]);
				int b=array[0].indexOf(numList.get(0));
				int e=array[0].lastIndexOf(numList.get(numList.size()-1));
				String cover=array[0].substring(b, e+1);
				for(int i=0;i<len;i++){
					String first=array[0].replace(cover, String.valueOf(numList.get(i)));
					result+=first+ "@" +sec[i]+ "@" +"###&&&";
				}
				return result;
			}
		}
		//选项中没有顿号而题干中有顿号，目前只处理前顿号、后编号的情况
		if(array[0].contains("、")){
			//图中为博物馆、乡（镇）行政机构、集贸市场依次是	⑥①②
			String[] fir=array[0].split("、");
			int len=fir.length;
			if(extractNum(array[1]).size()==len){
				List<Character> numList=extractNum(array[1]);
				int id=sentence.indexOf("、_PU");
				if(id<0)
					return array[0]+"@"+array[1]+"@###";
				Tree dnode=Tree.findNodeByNo(all, id+1);
				if(dnode.parent.parent!=null && dnode.parent.parent.content.equals("NP")){
					String cover = Print.print(dnode.parent.parent);
					if(array[0].contains(cover)){
						if(array[0].endsWith(cover)) array[0]=cover;
						//上面是为了处理“图中为博物馆、乡（镇）行政机构、集贸市场依次是	⑥①②”这句话，没有办法的办法
						String[] sp=cover.split("、");
						if(sp.length==len){
							for(int i=0;i<len;i++){
								String first=array[0].replace(cover, sp[i]);
								result+=first+ "@" +numList.get(i)+ "@" +"###&&&";
							}
							return result;
						}
					}else {
						System.out.println("指示拆分，选项中各部分的覆盖不在槽一");
					}
				}
			}
			
		}
		//题干中含有“及”，目前默认分为两部分，首先尝试切分第二个槽，如果能切成两部分，再去切分前面的
		if(spwords.contains("及")&&!array[0].contains("及以上")&&!array[0].contains("涉及")){
			if(array[1].contains("，")||array[1].contains("——")){
				String seperator=array[1].contains("，")?"，":"——";
				String[] sec=array[1].split(seperator);
				if(sec.length==2){
					String[] new_sp=processJi(array[0]);
					for(int i=0;i<2;i++){
						result+=new_sp[i]+ "@" +sec[i]+ "@" +"###&&&";
					}
				}
			}
		}
		//题干含有分别、和，选项含有“和”，规则限制条件很细，但是真没什么好的方法了
		if(spwords.contains("分别")&&array[0].contains("和")&&array[1].contains("和")){
			String[] sec=array[1].split("和");
			int id=sentence.indexOf("和_CC");
			if(id<0)
				return array[0]+"@"+array[1]+"@###";
			Tree dnode=Tree.findNodeByNo(all, id+1);
			if(dnode.parent.parent!=null && dnode.parent.parent.content.equals("NP")){
				String cover = Print.print(dnode.parent.parent);
				if(array[0].contains(cover)){
					if(array[0].contains("分别")) array[0]=array[0].replace("分别", "");
					if(array[0].endsWith(cover)) array[0]=cover;
					//上面是为了处理“图中为博物馆、乡（镇）行政机构、集贸市场依次是	⑥①②”这句话，没有办法的办法(这注释应该是写错了)
					String[] sp=cover.split("和");
					if(sp.length==2&&sec.length==2){
						for(int i=0;i<2;i++){
							String first=array[0].replace(cover, sp[i]);
							result+=first+ "@" +sec[i]+ "@" +"###&&&";
						}
						return result;
					}
				}else {
					System.out.println(array[1]);
					System.out.println("指示拆分，选项中各部分的覆盖不在槽一");
				}
			}
		}
		if(result.equals("")&&MyUtil.setContains(spwords, "分别|依次"))
			result=array[0]+ "@" +array[1]+ "@" +"###";
		if(result.equals(""))
			result=array[0]+ "@" +array[1];
		return result;
	}
	//抽出句中的①②③④
	public static List<Character> extractNum(String sentence){
		char[] numArray={'①','②','③','④','⑤','⑥','⑦','⑧','⑨','⑩'};
		List<Character> numList=new ArrayList<>();
		for(char c:numArray)
			numList.add(c);
		List<Character> num=new ArrayList<>();
		for(int i=0;i<sentence.length();i++){
			if(numList.contains(sentence.charAt(i))){
				num.add(sentence.charAt(i));
			}
		}
		return num;
	}
	
	public static String[] processJi(String str){
		/*
		 * 1、不是及其、前后都含有的：直接切分
		 * 2、不是及其，不含的：直接切分
		 * 3、不是及其，含有的：sp[0]不变、sp[1]变为..的sp[1]
		 * 4、是极其，含的:sp[0]不变、sp[1]变为..的sp[1]
		 * 5、是极其，不含的:sp[0]不变、sp[1]变为sp[0]的sp[1]
		 * 丑陋的一笔*/
		String[] sp=str.split("及");
		String[] new_sp=new String[2];
		boolean flag=false;
		if(sp[1].startsWith("其")){
			sp[1]=sp[1].substring(1);
			flag=true;
		}
		//找的
		int did=sp[0].length()-1;
		while(did>=0){
			if(sp[0].charAt(did)=='的')
				break;
			did--;
		}
		//重组
		if(did>-1 && flag==false && sp[1].contains("的")){
			//全部含有的
			new_sp[0]=sp[0];
			new_sp[1]=sp[1];
		}	
		else if(did==-1&&flag==false){
			new_sp[0]=sp[0];
			new_sp[1]=sp[1];
		}else if(did>-1&&flag==false){
			new_sp[0]=sp[0];
			new_sp[1]=sp[0].substring(0, did+1)+sp[1];
		}
		else if(did>-1&&flag==true){
			new_sp[0]=sp[0];
			new_sp[1]=sp[0].substring(0, did+1)+sp[1];
		}else{
			new_sp[0]=sp[0];
			new_sp[1]=sp[0]+"的"+sp[1];
		}
		return new_sp;
	}
}
