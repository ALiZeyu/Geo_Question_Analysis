package cn.edu.nju.ws.GeoScholar.templating.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 本类用于储存试题文本的词法分析、句法分析结果，这些结果下一步要用来生成语义模板
 * @author 李泽宇
 * */

/*
 * 文本前处理流程
 * 1、输入纯文本，删除题干中的套话,删除无用的词语(“最有可能”这种)，添加逗号(以“相比”结尾)。
 * 2、对上一步得到的句子（题干+选项）进行分词和词性标注。
 * 3、首先判断选项的类型(IP|VP|NP)，注意如果含有逗号，仅依据逗号之前的进行句法分析，但是句法树还是所有的一起。
 * 4、对于题干中没有逗号的题目：
 * 	分析题干核心类型(时间，范围，NP+VP|VP|NP),如果选项是IP，题干符合条件直接返回选项句法树，否则根据规律检查句法树
 *  对于题干中有逗号的题目
 *  分析前导(时间，范围，套话，NP+VP|VP|NP)、核心的类型,如果选项是IP，题干符合条件直接返回选项句法树 否则合在一起，对于存在题干前导的不进行检查
 */
public class TimuInfo {
	/** 原文本用\t或@分隔，主要用于调试 */
	public String origQuestion;
	/** 题干+选项不加分隔符 */
	public String question;
	/** 背景文本 */
	public String bjText;
	/**套话文本*/
	public String thText;
	/** 上下文文本*/
	public List<String> sxwText;
	/** 选项的文本 */
	public String xxText;
	/** 题干的文本 */
	public String tgText;
	/**整句话句法分析结果*/
	public String allTag;
	/** 整句话句法分析结果list */
	public List<String> allTagList;
	/** 选项句法分析结果 */
	public String xxTag;
	/** 题干整句话句法分析结果 */
	public String tgTag;
	/** 整句话句法树 */
	public Tree allTree;
	/** 选项的类型 */
	public String xxType;
	/** 题干核心的类型 */
	public String hxType;
	/**TODO 题干前导的类型 待修改为list */
	public String qdType;
	/** 如果选项中有逗号，提前判断用不用切分，如果用的话在二阶模板里面切分(二阶模板或者选项问句) */
	public boolean preSplit;
	
	/**获取选项问句的词法分析、句法分析结果
	 * @param	strlist:分词词性结果字符串，用空格分隔
	 * @param	flag:选项中有逗号的时候要不要拆分
	 * */
	public TimuInfo(String strlist , boolean flag) throws IOException{
		String q=MyUtil.tagstrToOriStr(strlist); 
		this.origQuestion="\t"+q;
		this.bjText="";
		this.thText="";
		this.sxwText=new ArrayList<>();
		this.question=q;
		this.xxText=q;
		this.tgText="";
		//第二步，分词与词性标注，然后分别得到题干和选项两部分
		this.allTag = strlist;
		this.allTagList = new ArrayList<>(Arrays.asList(strlist.split(" ")));
		this.tgTag="";
		this.xxTag=strlist;
		//第三步：获取题干、选项的类型以及最终的句法树。
		String t=(xxTag.indexOf("，")<1||xxTag.charAt(xxTag.indexOf("，")+1)!='_')?xxTag:xxTag.substring(0, xxTag.indexOf("，"));
		Tree xxTree = getParseingTree(t.trim());
		String xxType = getXuanXiangType(xxTree);
		this.xxType=xxType;
		this.allTree=getParseingTree(xxTag);
		this.allTagList=new ArrayList<>(Arrays.asList(xxTag.split(" ")));
		this.preSplit=flag;
	}
	
	/**
	 * 获取试题文本的词法分析、句法分析结果
	 * @param	question
	 * 			题干@选项字符串
	 * @param	locWords
	 * 			地名词典		
	 * */
	public TimuInfo(String question,List<String> locWords) throws IOException{
		//第一步，删除套话以及“最可能”等词语，同时处理套话及上下文
		this.origQuestion=question;
		this.bjText="";
		this.thText="";
		this.sxwText=new ArrayList<>();
		//判断拆分
		//SplitOrNot(question);
		String q=del(question, this);
		//String q = question.replace("@", "@@@");
		//String q="@@"+question;
		int split=q.indexOf("@@@");
		String[] tm=q.split("@@@");
		q=q.replace("@@@", "");
		this.question=q;
		this.xxText=tm[1];
		this.tgText=tm[0];
		//第二步，分词与词性标注，然后分别得到题干和选项两部分
		List<String> tagresult = Segment.segmentQuestion(q,split,locWords);
		this.allTag = MyUtil.listToString(tagresult);
		this.allTagList = tagresult;
		int tt=0;
		String tgTag="";
		int len=tm[0].length(),tlen=0;
		while(tlen<len){
			tgTag+=tagresult.get(tt)+" ";
			tlen+=tagresult.get(tt).split("_")[0].length();
			tt++;
		}
		this.tgTag=tgTag.trim();
		
		String xxTag="";
		len=tm[1].length();tlen=0;
		while(tlen<len){
			if(tt==tagresult.size()){
				System.out.println("seglist divide error");
				break;
			}
			xxTag+=tagresult.get(tt)+" ";
			tlen+=tagresult.get(tt).split("_")[0].length();
			tt++;
		}
		this.xxTag=xxTag.trim();
		//第三步：获取题干、选项的类型以及最终的句法树。
		String t=(xxTag.indexOf("，")<1||xxTag.charAt(xxTag.indexOf("，")+1)!='_')?xxTag:xxTag.substring(0, xxTag.indexOf("，"));
		Tree xxTree = getParseingTree(t.trim());
		String xxType = getXuanXiangType(xxTree);
		this.xxType=xxType;
		//this.xxTree=xxTree;
		if(tgTag.indexOf("，")==-1 || tgTag.charAt(tgTag.indexOf("，")+1)!='_'){
			//没有题干前导，只有核心
			if(tgTag.isEmpty()){
				//题干全是套话被删完了
				//this.tgTree=null;
				this.allTree=getParseingTree(xxTag);
				this.allTagList=new ArrayList<>(Arrays.asList(xxTag.split(" ")));
				return;
			}
			//只有题干核心
			Tree hxTree= getParseingTree(tgTag);
			String hxType=getHeXinType(tgTag, hxTree);
			this.hxType=hxType;
			
			if(xxType.equals("IP") && (hxType.equals("时间") || hxType.equals("范围"))){
				//删除不必要部分
				this.allTree=getParseingTree(xxTag);
				this.allTagList=new ArrayList<>(Arrays.asList(xxTag.split(" ")));
				if(hxType.equals("范围"))
					this.sxwText.add(MyUtil.tagstrToOriStr(tgTag));
				this.tgTag="";
			}
			else {
				xxTree=getParseingTree(xxTag);
				this.allTree=getParseingTree(allTag);
				String allType=hxType+"+"+xxType;
				isLegalTree(allType, tgTag, hxTree, xxTag, xxTree, allTree);
			//	System.out.println(allType);
//				Tree temp_tree = allTree;
//				this.allTree=isLegalTree(allType, tgTag, hxTree, xxTag, xxTree, allTree);
//				if(!temp_tree.toString().equals(this.allTree.toString())){
//					System.out.println(this.allTree.toString());
//					System.out.println(temp_tree.toString());
//				}
			}
		}else{
			//既有题干前导，又有题干核心
			//TODO：利用逗号分隔前导
			String qdTag=tgTag.substring(0,tgTag.lastIndexOf("，")).trim();
			String hxTag=tgTag.replace(qdTag+" ，_PU", "").trim();
			String qdType=getTGQDType(qdTag);
			if(xxType.equals("IP") && (qdType.equals("时间") || qdType.equals("范围"))){
				if(qdType.equals("范围"))
						this.sxwText.add(MyUtil.tagstrToOriStr(qdTag));
				if(hxTag.equals("")){
					//题干以逗号结尾，只有前导，没有核心
					this.allTree=getParseingTree(xxTag);					
					this.allTagList=new ArrayList<>(Arrays.asList(xxTag.split(" ")));
					this.tgTag="";
				}
				else {
					//既有前导，又有核心
					Tree hxTree= getParseingTree(hxTag);
					String hxType=getHeXinType(hxTag, hxTree);
					//删除不必要部分
					if(xxType.equals("IP") && (hxType.equals("时间") || hxType.equals("范围"))){
						//前导和核心都要去掉
						this.allTree=getParseingTree(xxTag);
						if(hxType.equals("范围"))
							this.sxwText.add(MyUtil.tagstrToOriStr(hxTag));
						this.allTagList=new ArrayList<>(Arrays.asList(xxTag.split(" ")));
						this.tgTag="";
					}
					else {
						//只去掉前导
						xxTree=getParseingTree(xxTag);
						String tempTag=hxTag.trim()+" "+xxTag.trim();
						tempTag=tempTag.trim();
						this.allTree=getParseingTree(tempTag);
						this.allTagList=new ArrayList<>(Arrays.asList(tempTag.split(" ")));
						this.tgTag=hxTag;
						String allType=hxType+"+"+xxType;
						isLegalTree(allType, hxTag, hxTree, xxTag, xxTree, allTree);
//						Tree temp_tree = allTree;
//						this.allTree=isLegalTree(allType, tgTag, hxTree, xxTag, xxTree, allTree);
//						if(!temp_tree.toString().equals(this.allTree.toString())){
//							System.out.println(this.allTree.toString());
//							System.out.println(temp_tree.toString());
//						}
					}
				}
			}else if(qdType.equals("时间") || qdType.equals("范围")){
				//在景区服务设施中，依次布局游客中心、加油站、餐厅、巡防站的合理方案是	①③②④，删除“在景区服务设施中，”				
				String tempTag=hxTag.trim()+" "+xxTag.trim();
				tempTag=tempTag.trim();
				Tree tempTree=getParseingTree(tempTag);
				String tempType = getXuanXiangType(tempTree);
				if(tempType.equals("IP")){
					if(qdType.equals("范围"))
						this.sxwText.add(MyUtil.tagstrToOriStr(qdTag));
					this.allTree=tempTree;					
					this.allTagList=new ArrayList<>(Arrays.asList(tempTag.split(" ")));
					this.tgTag=hxTag;
				}else
					this.allTree=getParseingTree(allTag);
			}else{
				this.allTree=getParseingTree(allTag);
			}
		}
	}
	
	/**删除套话以及无用的词语，返回题干@@@选项
	 * @param	querstion
	 * 			试题文本
	 * @param	tminfo
	 * 			试题词法分析、句法分析结果对象
	 * @return	删除无用信息之后的题干@@@选项
	 * */
	public static String del(String question,TimuInfo tminfo){
		// 移除无用词
		String[] del = {"最有可能", "有可能", "最可能", "可能", "最主要", "主要","图中反映","一般"};
		for (String s : del) {
			while (question.contains(s))
				question = question.substring(0, question.indexOf(s)) + question.substring(question.indexOf(s) + s.length(), question.length());
		}
		//据图	蓝水和绿水根本来源相同|	图示	N为低纬信风带
		if(question.startsWith("据图@")||question.startsWith("图示@")) question=question.substring(2);
		//某天文爱好者拍摄“超级月亮”的照片时（图1）	中国正值一年的立秋节气
		String specialRegex="（.*图.*）";
		Pattern p = Pattern.compile(specialRegex);
		Matcher m = p.matcher(question);
		int start=0,end=0;
		while (m.find()) {
			start = m.start();
			end = m.end();
			if(end-start<=5){
				String ss=question.substring(start, end);
				question=question.replace(ss, "");
			}
			
		}

		String[] te = question.split("@");
		//记录并删除题干中的背景部分，指根据句号
		if(te[0].contains("。")){
			int i1=te[0].lastIndexOf("。");
			//int i2=te[0].indexOf("；");
			//int b=i1>i2?i1:i2;
			tminfo.bjText=te[0].substring(0, i1);
			te[0]=te[0].substring(i1+1, te[0].length());
		}else
			tminfo.bjText="";
		//；之前的作为上下文
		if(te[0].contains("；")){
			int i1=te[0].lastIndexOf("；");
			tminfo.sxwText=new ArrayList<>();
			tminfo.sxwText.add(te[0].substring(0, i1));
			te[0]=te[0].substring(i1+1, te[0].length());
		}
		//读图这一类套话，只存在于题干第一个逗号之前，是的话直接删掉。
		Pattern p1=Pattern.compile("读.*[图|表]([\\d|甲|乙|丙|丁])*([\\（|\\(][a|b|c|d|A|B|C|D][\\）|\\)])*.*");
		if(te[0].contains("，")){
			String st=te[0].substring(0, te[0].indexOf("，"));
			if(p1.matcher(st).matches())
				te[0]=te[0].replace(st+"，", "");
		}
		//删除不是比较的相比“与上海市相比，四川省经济发展的地理优势有交通便利”
		delXb(te, tminfo);
		//"当日","时"
		String[] douHao={"相比"};
		for(String s:douHao)
			if (te[0].endsWith(s)) te[0] += "，";
		String[] tt = te[0].split("，");
		if(tt[tt.length-1].startsWith("随着")||tt[tt.length-1].startsWith("随")) te[0] += "，";
		
//		String q = !te[0].contains("排序") 
//				&& !te[0].contains("原因") 
//				&& !te[0].contains("影响") 
//				&& (te[0].contains("正确的") 
//						|| te[0].contains("错误的") 
//						|| te[0].contains("接近实际的") 
//						|| (te[0].contains("下列") 
//								&& te[0].endsWith("是") 
//								&& !te[0].contains("最"))) ? "" : te[0];
		//删除套话应该是针对题干和选项而不仅是题干,需要进一步更改。
		//te[0]=delTaohua(te[0], tminfo);
		
		delTaohua(te, tminfo);
		String q=te.length==2?q=te[0]+"@@@"+te[1]:"@@@"+te[0];
		if(q.startsWith("推断")||q.startsWith("推测"))
			q=q.substring(2);
		
		while (q.startsWith("，"))
			q = q.substring(1);
		if (q.endsWith("。"))
			q = q.substring(0, q.length() - 1);
		//去掉题干中以句号结尾的句子
//		if (q.contains("。")) 
//			q = q.substring(q.lastIndexOf("。") + 1);
		return q;
	}
	
	/**提取题干中的套话 
	 * @param	te
	 * 			题干和选项数组
	 * @param	tminfo
	 * 			题目文本的词法分析，句法分析结果
	 * */
	public static void delTaohua(String[] te, TimuInfo tminfo){
		//TODO 1、判断套话后面是不是一个句子，不是的话特殊处理。
		// 2、没识别为套话但含有”正确的是“，如何处理
		//boundary用来判断套话是否出现在选项中
		tminfo.thText="";
		if(tminfo.sxwText==null)
			tminfo.sxwText=new ArrayList<>();
		int boundary=te.length>1?te[0].length():0;
		String sentence = te.length>1?te[0]+te[1]:te[0];
		String[] regex={"(根据|据|由|结合|从).*(分析|推测|推断|判断|可知)(，|出){0,1}(下列|关于|有关).*(正确|符合实际|可信).(是)",
				"(根据|据|由|结合|从).*(分析|推测|推断|判断|可知)(，|出|是){0,1}",
				"(下列|关于|有关).*(正确|符合实际|可信|合理).{1,3}(是)"};
		//有一些不符合上述表达式的套话，特别处理一下
		String[] special={"一般情况下","一般情况下，","图中四地符合实际的是","依据图11与图12，","图中显示","按此理解，","根据图示信息，","图7中","图2中①②③三地自然条件相比较","图3中①、②、③、④四地自然条件相比","文中描述的现象可以解释为","根据所学知识，你认为","结合图4，"};
		Set<String> spSet=new HashSet<>();
		for(String str : special) spSet.add(str);
		List<String> match=matchStr(regex, sentence);
		
		if(match.size()==0){
			for(String ss:spSet)
				if(sentence.contains(ss)){
					int ssindex=sentence.indexOf(ss);
					if(ssindex==0||sentence.charAt(ssindex-1)=='，'){
						tminfo.thText=ss;
						break;
					}
				}
		}
		else if(match.size()==1){
			tminfo.thText=match.get(0);
		}
		else{ 
			tminfo.thText=match.get(0);
			tminfo.sxwText.add(match.get(1));
		}
		//如果套话的范围直接到了选项，那么题干就为”“
		int len=tminfo.thText.length();
		if(len>0){
			//将题干中套话之前的部分作为上下文。
			int position=sentence.indexOf(tminfo.thText);
			if(position!=0){
				String tt=sentence.substring(0,position+1);
				if(tt.endsWith("，")) tt=tt.substring(0, tt.length()-1);
				if(tt.length()>0)
					tminfo.sxwText.add(tt);
			}
			//删除套话
			int b=sentence.indexOf(tminfo.thText)+tminfo.thText.length();
			if(boundary==0){
				if(te[0].length()>b)
					te[0]=te[0].substring(b);
			}else{
				if(b>=boundary){
					te[0]="";
					te[1]=sentence.substring(b);
				}else
					te[0]=te[0].substring(b);
			}
		}
		
		//处理一下“图中所示、显示，反应，图1中”
		String[] p={"图中显示","图中所示","图中反映","图中"};
		for(String ps:p){
			//图中为，图中是这种不行
			if(te[0].startsWith(ps)&&!(te[0].startsWith(ps+"为")||te[0].startsWith(ps+"的"))){
				te[0]=te[0].substring(ps.length());
				if(tminfo.thText.equals(""))
					tminfo.thText=ps;
			}
		}
		Pattern pp = Pattern.compile("图.{1,3}中");
		Matcher m = pp.matcher(te[0]);
		int start=-1,end=-1;
		if (m.find()) {
			start = m.start();
			end = m.end();
			if(start==0 &&!(end<te[0].length()&&(te[0].charAt(end)=='为'||te[0].charAt(end)=='是'))){
				//图X中为，图X中是这种不行
				if(tminfo.thText.equals(""))
					tminfo.thText=te[0].substring(start, end);
				te[0]=te[0].substring(end);
			}
		}
	}
	
	//输入一句话，返回list中含有套话和上下文
		public static List<String> matchStr(String[] regs, String str){
			List<String> result = new ArrayList<>();
			int i=0;
			String taohua="";
			String sxw=null;
			for(String reg:regs){
				Pattern p = Pattern.compile(reg);
				Matcher m = p.matcher(str);
				int start=0,end=0;
				while (m.find()) {
					start = m.start();
					end = m.end();
					taohua=str.substring(start, end);
				}
				if(!taohua.equals("")){
					result.add(taohua);
					sxw = getSXW(i + 1, taohua);
					if (!(sxw == null) && sxw.length() > 0)
						result.add(sxw);
					break;
				}
//				if(i==0&&result.size()>0)
//					break;
				//正确的是前面还有文字，看例子处理
//				if(i==2&&result.size()>0){
//					if()
//				}
				i++;
			}
			return result;
		}
		
		//从套话中抽出上下文信息
		public static String getSXW(int type, String taohua){
			String sxw=null;
			String[]regs=new String[2];
			switch (type) {
			case 1:
				regs[0]="(根据|据|由|结合|从).*(分析|推测|推断|判断|可知)(，|出){0,1}(下列|关于|有关)(对|关于|有关){0,1}";
				regs[1]="(正确|符合实际|可信).(是)";
				break;
			case 2:
				regs[0]="(根据|据|由|结合|从)";
				regs[1]="(分析|推测|推断|判断|可知)(，|出){0,1}";
				break;
			case 3:
				regs[0]="(下列|关于|有关)(对|关于|有关){0,1}";
				regs[1]="(正确|符合实际|可信).(是)";
				break;
			default:
				break;
			}
			for(String reg:regs){
				Pattern p = Pattern.compile(reg);
				Matcher m = p.matcher(taohua);
				String temp ="";
				int start=0,end=0;
				while (m.find()) {
					start = m.start();
					end = m.end();
					temp = taohua.substring(start, end);
				}
				taohua=taohua.replace(temp, "");
			}
			sxw=simplifysxw(taohua,type);
			return sxw;
		}
		//进一步处理上下文，去除【，|叙述|描述】之类的
		public static String simplifysxw(String sxw,int type){
			if(type==1||type==3){
				//关于。。正确的是
				if(sxw.indexOf("，")!=-1)
					sxw=sxw.substring(0, sxw.indexOf("，"));
				String[] tail={"的叙述","的描述","的说法","说法","叙述","描述","最"};
				for(String s : tail){
					if(sxw.contains(s))
						sxw=sxw.replace(s, "");
				}
			}
			else if (type==2) {
				//据图可以判断
				if(sxw.indexOf("，")!=-1)
					sxw=sxw.substring(0, sxw.indexOf("，"));
				
				String[] tail={"可以","可"};
				for(String s : tail){
					if(sxw.endsWith(s))
						sxw=sxw.substring(0,sxw.length()-s.length());
				}
				//
				if(sxw.matches("图.{0,2}|此")||((sxw.contains("图")||sxw.contains("表")||sxw.contains("材料"))&&sxw.contains("信息")))
					sxw="";
			}
			return sxw;
		}
	
	public static Tree getParseingTree(String tag) throws IOException {
		tag=tag.trim();
		while(tag.contains("  ")) tag.replace("  ", " ");
		List<String> list=Util.getTreeList(tag);
		String treeStr=SyntaxParser.parsingByList(list);
		return Input.senToNode(treeStr);
	}
	
	/**
	 * 题干前导的类型判断(时间、范围、套话、条件状语、目的状语、陈述句)
	 * @param	tgqdTxt
	 * 			题干前导文本
	 * @return	类型
	 * */
	public static String getTGQDType(String tgqdTxt){
		String[] list = tgqdTxt.split(" ");
		if(list[list.length-1].equals("中_LC"))
			return "范围";
		if(list[0].split("_")[0].equals("为")||list[0].split("_")[0].equals("为了"))
			return "目的状语";
		if (list[0].split("_")[0].equals("若")||list[0].split("_")[0].equals("如果"))
			return "条件状语";
		String str = listToString(list);
		if(NerRecognition.isTime(str))
			return "时间";		
		return "陈述句";
	}
	
	//将词法分析结果转为纯文本
	public static String listToString(String[] list){
		StringBuffer str = new StringBuffer();
		for(String line : list)
			str.append(line.split("_")[0]);
		return str.toString();
	}
	
	/**
	 * 题干核心的四种类型：
	 * 1:NP+VV
	 * 2:NP+NN
	 * 3:NP
	 * 4:VP
	 * */
	public static String getHeXinType(String str, Tree t){
		String[] list = str.split(" ");
		if(list[list.length-1].equals("中_LC"))
			return "范围";
		
		String pure = listToString(list);
		//TimeRecognition time = new TimeRecognition();
		if(NerRecognition.isTime(pure))
			return "时间";
		
		if(t.child!=null)
			t=t.child.get(0);
		if(t.content.equals("NP"))
			return "NP";
		
		if(t.content.equals("VP"))
			return "VP";
		
		if(t.content.equals("IP")){
			if(t.child!=null){
				if(t.child.size()==1 && t.child.get(0).content.equals("VP"))
					return "VP";
				if(t.child.size()>=2 && t.child.get(t.child.size()-2).content.equals("NP") && t.child.get(t.child.size()-1).content.equals("VP")){
					if(str.endsWith("NN"))
						return "NP+NN";
					else
						return "NP+VV";
				}
					
				if(str.endsWith("VV") || str.endsWith("VC") || str.endsWith("VE"))
					return "NP+VV";
				else {
					//System.out.println("题干核心出现未覆盖内容，句法树是："+t);
					return "VP";
				}
			}
		}
		
		//System.out.println("题干核心出现未覆盖内容，句法树是："+t);
		return "VP";
	}
	
	/**
	 * 输入：选项的句法树
	 * 选项的三种类型：
	 * 1:IP
	 * 2:NP
	 * 3:VP
	 * */
	public static String getXuanXiangType(Tree t){
		if(t.child!=null)
			t=t.child.get(0);
		if(t.content.equals("NP")){
			if(t.child.size()>0){
				for(int i=0;i<t.child.size();i++)
					if(t.child.get(i).content.equals("IP"))
						return "IP";
			}
			return "NP";
		}
			
		if(t.content.equals("VP"))
			return "VP";
		if(t.content.equals("IP")){
			if(t.child.size()==1 && t.child.get(0).content.equals("VP"))
				return "VP";
			else 
				return "IP";
		}
		//System.out.println("选项出现未覆盖内容，句法树是："+t);
		return "VP";
	}
	
	public static Tree isLegalTree(String type, String tg, Tree tgTree, String xx, Tree xxTree, Tree t){
		return t;
	}
	
	//选项与题干句法树合并函数
	public static Tree isLegalTree(int no,String type, String tg, Tree tgTree, String xx, Tree xxTree, Tree t){
		String[] xxList=xx.split(" ");
		List<String> xxWords=new ArrayList<>();
		for(String s:xxList)
			xxWords.add(s.split("_")[0]);
		
		String[] tglist=tg.split(" ");
		//题干中最后一个词
		Tree node = Tree.findNodeByNo(t, tglist.length);
		String str="";
		switch (type) {
		//NP+VV+NP
		case "NP+VV+NP":
			while(node!=null && !node.content.equals("VP") && !node.content.equals("PP"))
				node=node.parent;
			str=node==null?"":node.toString();
			if(!cover(str, xxWords)){
				node = Tree.findNodeByNo(tgTree, tglist.length);
				while(!node.content.equals("VP") && !node.content.equals("PP"))
					node=node.parent;
				xxTree=Tree.getDownByLabel(xxTree, "NP");
				if(xxTree==null) break;
				node.addRightChild(xxTree);
			//	System.out.println(false);
				return tgTree;			
			}
			break;
		//NP+VP(NN)+NP
		case "NP+NN+NP":
			node=Tree.getUpByLabel(node, "NP");
			if(node!=null){
				str = node.toString();
				if (!cover(str, xxWords)) {
					node = Tree.findNodeByNo(tgTree, tglist.length);
					node = Tree.getUpByLabel(node, "NP");
					if(node==null) break;
					xxTree = Tree.getDownByLabel(xxTree, "NP");
					if(xxTree==null) break;
					node.addRightChild(xxTree);
					// System.out.println(false);
					return tgTree;
				}
			}
			
			break;
		case "NP+IP":
			if(xxList[0].split("_")[1].startsWith("V") || xxList[0].split("_")[1].startsWith("P") || xxList[0].split("_")[1].startsWith("AD"))
				break;
			node = Tree.findNodeByNo(t, tglist.length+1);
			node = Tree.getUpByLabel(node, "NP");
			if(node!=null){
				str = node.toString();
				if (!cover(str, tglist)) {
					node = Tree.findNodeByNo(xxTree, 1);
					node = Tree.getUpByLabel(node, "NP");
					if(node==null) break;
					tgTree = Tree.getDownByLabel(tgTree, "NP");
					if(tgTree==null) break;
					node.addLeftChild(tgTree);
					// System.out.println(false);
					return xxTree;
				}
			}
			
			break;
		default:
			break;
		}
		//System.out.println(true);
		return t;
	}
	
	public static boolean cover(String sentence, List<String> words){
		for(String w : words){
			if(!sentence.contains(w))
				return false;
		}
		return true;
	}
	
	public static boolean cover(String sentence, String[] words){
		for(String w : words){
			if(!sentence.contains(w.split("_")[0]))
				return false;
		}
		return true;
	}
	//题干中含有[相比，]和[与]且题干末尾是[是|有|原因]，找题干中[与]和[相比]之间的填入上下文并从题干中删除(注：[与]之前的主语不删)
	//[与上海市相比，四川省经济发展的地理优势有交通便利]去掉“与上海市相比”
	public static void delXb(String[] te, TimuInfo timuInfo){
		if(te[0].contains("相比，") && MyUtil.endWithStr(te[0], "是|有|原因")){
			int index=te[0].indexOf("相比，");
			int b=te[0].indexOf("与");
			if(b!=-1 && b<index){
				String sxw=te[0].substring(b, index+3);
				if (timuInfo.sxwText==null) timuInfo.sxwText = new ArrayList<>();
				timuInfo.sxwText.add(sxw);
				te[0]=te[0].replace(sxw, "");
			}
		}
	}
	
	@Override
	public String toString() {
		return "TimuInfo [thText=" + thText + "\n sxwText=" + sxwText + "\n allTagList=" + allTagList + "\n allTree="
				+ allTree.toString() + "]";
	}
	//对原始句子(未经删除套话前处理的)进行拆分判断，给拆分标志赋值。
	public void SplitOrNot(String question) throws IOException{
		question+="。";
		String[] q = question.split("@");
		if(q.length<2 || !q[1].contains("，")){
			this.preSplit=false;
			return;
		}
		int split=question.indexOf("@");
		String txt=question.replace("@", "");
		List<String> result = Segment.segmentQuestion(txt,split,null);
		//保证选项中的逗号没有被分在词里面，比如经纬度。
		int sum=0;
		boolean flag=true;
		for(String s:result){
			if(sum<split)
				sum+=s.split("_")[0].length();
			else{
				if(s.equals("，_PU")){
					flag=false;
					break;
				}
			}
		}
		if(flag){
			this.preSplit=false;
			return;
		}
		String send=getSend(question, result, q[0]);
		try {
			//System.out.println(send);
			this.preSplit=Util.sendPost("http://localhost:8080/simple_split.html", send);
			//this.preSplit=Util.sendPost("http://210.28.132.72:8080/simple_split.html", send);
			//this.preSplit=Util.sendPost("http://114.212.83.126:1487/simple_split.html", send);
		} catch (Exception e) {
			System.out.println(question);
			this.preSplit=false;
		}
	}
	
	public String getSend(String oriQuestion, List<String> result, String tigan){
		StringBuffer seg=new StringBuffer();
		StringBuffer pos=new StringBuffer();
		StringBuffer ner=new StringBuffer();
		StringBuffer temp=new StringBuffer();
		String segStr=MyUtil.listToSegStr(result);
		List<String> times=NerRecognition.getTime(oriQuestion);
		List<String> geoTerms=Segment.getTerm(oriQuestion);
		List<String> locations=NerRecognition.getLocation(segStr);
		for(String str : result){
			String[] strs=str.split("_");
			seg.append(strs[0]);
			temp.append(strs[0]);
			seg.append(temp.toString().equals(tigan)?"\t":" ");
			pos.append(strs[1]+" ");
			String label="O ";
			
			for(String loc:locations){
				if(loc.contains(strs[0])){
					label="loc ";
					break;
				}
			}
			if(label.equals("O ")){
				for(String time:times){
					if(time.contains(strs[0])){
						label="time ";
						break;
					}
				}
			}
			if(label.equals("O ") && geoTerms.contains(str.split("_")[0])) label="term ";
			ner.append(label);
		}
		return "segged_sentence="+seg.toString().trim()+
				"&ner="+ner.toString().trim()+
				"&pos="+pos.toString().trim();
	}
	
}
