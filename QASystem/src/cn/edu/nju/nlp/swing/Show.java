package cn.edu.nju.nlp.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.edu.nju.ws.GeoScholar.templating.common.MyUtil;
import cn.edu.nju.ws.GeoScholar.templating.common.Util;

public class Show extends JFrame implements ActionListener{
	String input_file;
	JPanel jp1, jp2;
	JButton jb1;
	JCheckBox jc1,jc2,jc3;
	JTextArea jt;
	static List<List<String>> result = new ArrayList<>();
	
	public static void main(String[] args) { 
//		List<String> result = Util.read_file("E:/workspace/StanfordDP/data/200query.txt");
//    	Show s = new Show("", result);
    }
	
	public Show(String file, List<List<String>> result){
		this.input_file = file;
		this.result = result;
		List<Integer> index = new ArrayList<>();
		index.add(0);index.add(1);index.add(2);index.add(3);
		List<String> temp = build_list(result, index);
		String str = listToHtml(temp);
		this.setLayout(new GridLayout(2, 1));
		jp1 = new JPanel();
		jp2 = new JPanel();
		
		jt = new JTextArea(str);
		//jt.setSize(700, 300);
		jt.setMaximumSize(new Dimension(700, 300));
		//jt.setPreferredSize(new Dimension(700, 300));
		//jt.setBounds(50, 0, 700, 300);
		jt.setFont(new Font("仿宋", Font.PLAIN, 16));
//		jt.setLineWrap(true);        //激活自动换行功能 
//		jt.setWrapStyleWord(true);
		
		JScrollPane scroll = new JScrollPane(jt); 
		scroll.setPreferredSize(new Dimension(700, 300));
		//scroll.setMaximumSize(scroll.getPreferredSize());
		//scroll.setMinimumSize(scroll.getPreferredSize());

		//分别设置水平和垂直滚动条总是出现 
		scroll.setHorizontalScrollBarPolicy( 
		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
		scroll.setVerticalScrollBarPolicy( 
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		jp1.setLayout(new BoxLayout(jp1, BoxLayout.X_AXIS));
		jp1.add(scroll);
		//jp.add(jt);
		jc1 = new JCheckBox("分词");
		jc2 = new JCheckBox("句法结果");
		jc3 = new JCheckBox("模板结果");
		jb1 = new JButton("结果存储");
		jb1.addActionListener(this);
		
		
		jc1.setFont(new Font("仿宋", Font.PLAIN, 16));
		jc2.setFont(new Font("仿宋", Font.PLAIN, 16));
		jc3.setFont(new Font("仿宋", Font.PLAIN, 16));
		jb1.setFont(new Font("仿宋", Font.PLAIN, 16));
		jp2.add(jc1);
		jp2.add(jc2);
		jp2.add(jc3);
		jp2.add(jb1);
		
		this.add(jp1);
		this.add(jp2);
		this.setTitle("分析结果");//窗体标签  
        this.setSize(800, 400);//窗体大小  
        this.setLocationRelativeTo(null);//在屏幕中间显示(居中显示)  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//退出关闭JFrame  
        this.setVisible(true);//显示窗体
	}
	
	public String listToHtml(List<String> result){
		StringBuffer sb = new StringBuffer();
		//sb.append("<html>");<br>
		for(String sen : result)
			sb.append(sen+"\r\n");
		//sb.append("</html>");
		return sb.toString();
	}
	
	public List<String> build_list(List<List<String>> result, List<Integer> index){
		List<String> list = new ArrayList<>();
		int size = result.get(0).size();
		if(size == 1 && result.size() != 4){
			list.add(result.get(0).get(0));
			return list;
		}
		for(int i=0;i<size;i++){
			for(int j:index)
				list.add(result.get(j).get(i));
			
			list.add("\n");
		}
		return list;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		List<Integer> index = new ArrayList<>();
		index.add(0);
		if(jc1.isSelected()) index.add(1);
		if(jc2.isSelected()) index.add(2);
		if(jc3.isSelected()) index.add(3);
		if(index.size() == 1) index.add(3);
		List<String> result = build_list(this.result, index);
		MyUtil.writeFile(result, "data/swing_result.txt");
	}

}
