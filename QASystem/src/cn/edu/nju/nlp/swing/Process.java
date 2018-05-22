package cn.edu.nju.nlp.swing;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import cn.edu.nju.ws.GeoScholar.common.NLPResult;
import cn.edu.nju.ws.GeoScholar.templating.choice.Generate;
import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.QuestionTemplateFromNLP;

public class Process extends JFrame implements ActionListener{
	String input_file;
	JPanel jp1, jp2;
	JButton jb;
	JTextArea jt;
	//二维数组，分别是题目、分词结果、句法树、模板4个list
	static List<List<String>> result = new ArrayList<>();
	public Process(String file){
		this.input_file = file;
		jp1 = new JPanel();
		jp2 = new JPanel();
		//文本显示
		jt = new JTextArea("开始加载模型.....\r\n");
		jt.setLineWrap(true);
		jt.setSize(750, 200);
		jt.setFont(new Font("仿宋", Font.PLAIN, 20));
		//按钮显示
		jb = new JButton("查看结果");
		jb.addActionListener(this);
		jb.setFont(new Font("仿宋",Font.BOLD,20));
		
		jp1.add(jt);
		jp2.add(jb);
		jp2.setVisible(false);
		
		this.setLayout(new GridLayout(2, 1));
		this.add(jp1);
		this.add(jp2);
		
		this.setTitle("正在处理....");//窗体标签  
        this.setSize(800, 400);//窗体大小  
        this.setLocationRelativeTo(null);//在屏幕中间显示(居中显示)  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//退出关闭JFrame  
        this.setVisible(true);//显示窗体
	}
	
	
	public void load(){
		String filename = this.input_file;
		
		Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
            	try {
					Generate.init();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
                jt.append("模型加载完毕，开始处理文本\r\n");
                try {
					result = successiveProcess(filename);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//                for (int i = 0; i < 3; i++) {
//                	jt.append((i+1)+"\r\n");
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//                jt.append("action end");
                jp2.setVisible(true);
            }
        });
		t.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.dispose();
		if(result.size() == 0){
			List<String> temp = new ArrayList<>();
			temp.add("未输出结果");
			result.add(0, temp);
		}
		new Show(input_file,result);
	}
	
	public static List<List<String>> successiveProcess(String testPath) throws IOException{
		//E:/geo_data/测试软件/test_paper.txt
		List<String> result = MyUtil.readListFromFile(testPath);
		List<List<String>> mine = new ArrayList<>();
		List<String> seg_list = new ArrayList<>();
		List<String> tree_list = new ArrayList<>();
		List<String> tem_list = new ArrayList<>();
		Set<String> del = new HashSet<>();
		Map<String, Integer> count = new HashMap<>();
		for (int j = 0 ; j < result.size(); j++) {
			//全部试题测试
			System.out.println(result.get(j));
			StringBuffer tem_sb = new StringBuffer();
			List<String> temp = new ArrayList<>();
			try {
				List<String> list=new ArrayList<>();
				List<NLPResult> allresult = Generate.GenerateTemplate(temp, result.get(j),list);
				if(temp.size()==2){
					seg_list.add(temp.get(0));
					tree_list.add(temp.get(1));
				}
//				for(NLPResult tst : allresult){
//					if((tst.superTemplates==null||tst.superTemplates.templates.size()==0)&&(tst.subTemplates==null||tst.subTemplates.templates.size()==0))
//						System.out.println("模板缺失"+result.get(j));
//				}
				for(NLPResult tst : allresult){
					if(tst.superTemplates!=null){
						for(QuestionTemplateFromNLP nlp:tst.superTemplates.templates){
							String name = nlp.templateType;
							count.put(name, count.getOrDefault(name, 0)+1);
							if(nlp.toString()!=null)
								tem_sb.append(nlp.toString()+"\t");
								//mine.add(nlp.toString());
						}
					}
				}
				for(NLPResult tst : allresult){
					if(tst.subTemplates!=null)
						for(QuestionTemplateFromNLP nlp:tst.subTemplates.templates){
							String name = nlp.templateType;
							if(name.equals("其他陈述") && tst.superTemplates==null){
								del.add(result.get(j));
								continue;
							}
							count.put(name, count.getOrDefault(name, 0)+1);
							if(nlp.toString()!=null)
								tem_sb.append(nlp.toString()+"\t");
								//mine.add(nlp.toString());
						}
				}
			} catch (Exception e) {
				System.out.println(result.get(j)+e.getMessage());
			}
			tem_list.add(tem_sb.substring(0, tem_sb.length()-1));
		}
		mine.add(result);
		mine.add(seg_list);
		mine.add(tree_list);
		mine.add(tem_list);
		return mine;
		//MyUtil.writeFile(mine, resultPath);
	}
}
