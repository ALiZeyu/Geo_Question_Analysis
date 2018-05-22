package cn.edu.nju.nlp.swing;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;  

public class Initial extends JFrame implements ActionListener{  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//定义组件  
    JPanel jp1,jp2,jp3;//面板  
    JLabel title_lb,des_lb;
    JButton jb;//按钮  
    
    public static void main(String[] args) {  
    	Initial win=new Initial();
    }  
      
    //构造函数  setText("<html>abc <br>def </html>");
    public Initial(){
        //创建面板  
        jp1=new JPanel();  
        jp2=new JPanel();
        jp3 = new JPanel();
        //标题
        title_lb=new JLabel("地理试题语义分析系统");
        title_lb.setFont(new Font("仿宋", Font.PLAIN, 28));
        //说明
        des_lb=new JLabel("<html>系统功能：对地理试题进行语义解析，包括分词、词性标注、句法分析、模板填槽。<br>"
        		+ "输入：选取文本文件，以utf-8格式编码，每行是一句试题文本。<br>"+"作者：MF1533027李泽宇</html>",JLabel.CENTER);
        des_lb.setFont(new Font("仿宋", Font.PLAIN, 20));
        //des_lb.setPreferredSize(new Dimension(200, 200));
        //创建按钮  
        jb=new JButton("输入文件选择");
        jb.addActionListener(this);
        jb.setFont(new Font("仿宋",Font.BOLD,20));
          
        //设置布局管理  
        this.setLayout(new GridLayout(3, 1));//网格式布局
        this.setLocationRelativeTo(null);
          
        //加入各个组件  
        jp1.add(title_lb); 
        jp2.add(des_lb);
        jp3.add(jb);
          
        //加入到JFrame  
        this.add(jp1);  
        this.add(jp2);
        this.add(jp3);
          
        //设置窗体  
        this.setTitle("地理试题语义分析系统");//窗体标签  
        this.setSize(800, 400);//窗体大小  
        this.setLocationRelativeTo(null);//在屏幕中间显示(居中显示)  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//退出关闭JFrame  
        this.setVisible(true);//显示窗体
          
        //锁定窗体  
        this.setResizable(false);  
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser jfc=new JFileChooser();  
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );  
        jfc.showDialog(new JLabel(), "选择");
        File file=jfc.getSelectedFile();  
        if(null != file){
        	if(!file.isFile()){
        		JOptionPane.showMessageDialog(null, "请选择文本文件", "标题",JOptionPane.PLAIN_MESSAGE);
        	}else{
        		this.dispose();
        		Process p = new Process(file.getAbsolutePath());
        		p.load();
        		//System.out.println("等等我");
        	}
	        //System.out.println(jfc.getSelectedFile().getName());
        }
	
		
	}

}  