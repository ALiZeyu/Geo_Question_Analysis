package RDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import Util.Util;

public class Extraction {

	public static void main(String[] args) {
		//F://地理相关/毕设v/Clinga_data/Clinga_data/Clinga_info/Infobox
//		List<String> paths = Util.getFileList("data/dic_extraction");
//		List<String> infolist = new ArrayList<>();
//		for(String path:paths){
//			readRDF(path);
//		}
		//dic_extraction("data/dic_extraction/11-30");
		
	}
	//根据Clinga rdf文件构建词典，参数是地点词典的文件夹路径data/dic_extraction"
	public static void dic_extraction(String dic_path){
		List<String> paths = Util.getFileList(dic_path);
		List<String> dic_list = new ArrayList<>();
		for(String path:paths){
			dic(path, dic_list);
		}
		Util.writeFile(dic_list, "data/Geo_dic.txt");
	}
	
	//抽取实体及其对应的infobox，路径F:\地理相关\毕设v\Clinga_data\Clinga_data\Clinga_info\Infobox
	public static void infobox_extraction(String infobox_path){
		List<String> paths = Util.getFileList(infobox_path);
		List<String> infobox_list = new ArrayList<>();
		for(String path:paths){
			readRDF(path, infobox_list);
		}
		Util.writeFile(infobox_list, "data/Infobox_dic.txt");
	}
	
	public static void dic(String fileName, List<String> dic_list){
		List<String> list = new ArrayList<>();
		SAXReader reader = new SAXReader();              
        try {
			Document document = reader.read(new File(fileName));
			Element root = document.getRootElement();
			listNodes(root,list);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
        int b = fileName.lastIndexOf("\\"),e = fileName.indexOf(".");
        dic_list.add(fileName.substring(b+1, e)+":");
        dic_list.addAll(list);
        System.out.println(fileName.substring(b+1, e));
        System.out.println(list.size());
	}
	 
	public static void listNodes(Element node, List<String> list) {
		if(node.getName().matches("zhongWenMing|zhongWenMingCheng")){
			if(node.getTextTrim().length()!=0)
				list.add(node.getTextTrim());
		}
  
        // 当前节点下面子节点迭代器  
        Iterator<Element> it = node.elementIterator();  
        // 遍历  
        while (it.hasNext()) {  
            // 获取某个子节点对象  
            Element e = it.next();
            // 对子节点进行遍历  
            listNodes(e, list);
        }  
    } 
	
	public static void readRDF(String fileName, List<String> infobox_list) {
        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(fileName);
        if (in == null) {  
            throw new IllegalArgumentException("File: " + fileName  
                    + " not found");  
        }
        //key:Clinga内部编号，value：infoboxkey:value的list
        Map<String, List<String>> map = new HashMap<>();
        model.read(in, null);
        //解析RDF成XML的形式，一个resource下面多个infobox
        try {
			OutputStream os = new FileOutputStream("data/xml.txt");
			model.write(os);  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        SAXReader reader = new SAXReader();              
        try {
			Document document = reader.read(new File("data/xml.txt"));
			Element root = document.getRootElement();
			listNodes(root,map);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
        
        for(Map.Entry<String, List<String>> entry:map.entrySet()){
        	List<String> temp = entry.getValue();
        	StringBuffer sb = new StringBuffer();
        	String name = "";
        	for(String item:temp){
        		if(item.startsWith("中文名"))
        			name = item.split("@@@")[1];
        		else {
					sb.append(item+"\t");
				}
        	}
        	if(!name.equals(""))
        		infobox_list.add(name+"\t"+sb.toString());
        }
    }  
	
	public static void listNodes(Element node, Map<String, List<String>> map) {
		if(node.getName().equals("InfoboxItem") && node.attributes().size()==1){
			String key = keyExtraction(node.attributes().get(0).getValue()); 
			Iterator<Element> it = node.elementIterator();
			String infokey="",infovalue="";
	        while (it.hasNext()) {  
	            // 获取某个子节点对象  
	            Element e = it.next();
	            if(e.getName().equals("infoboxValue"))
	            	infovalue = e.getText();
	            else if(e.getName().equals("infoboxKey"))
	            	infokey = e.getText();  
	        }
	        if(!infokey.equals("")&&!infovalue.equals("")){
	        	if(map.containsKey(key))
		        	map.get(key).add(infokey+"@@@"+infovalue);
		        else{
		        	List<String> l = new ArrayList<>();
		        	l.add(infokey+"@@@"+infovalue);
		        	map.put(key, l);
		        }
	        }
	        return;
		}
        System.out.println("当前节点的名称：：" + node.getName()+" ");  
        // 获取当前节点的所有属性节点  
        List<Attribute> list = node.attributes();  
        // 遍历属性节点  
        for (Attribute attr : list) {  
            System.out.println(attr.getText() + "-----" + attr.getName() + "---" + attr.getValue());  
        }  
  
        if (!(node.getTextTrim().equals(""))) {  
            System.out.println("文本内容：：：：" + node.getText());  
        }  
  
        // 当前节点下面子节点迭代器  
        Iterator<Element> it = node.elementIterator(); 
        // 遍历  
        while (it.hasNext()) {  
            // 获取某个子节点对象  
            Element e = it.next();
            // 对子节点进行遍历  
            listNodes(e,map);  
        }  
    } 
	
	public static String keyExtraction(String str){
		int len = "http://ws.nju.edu.cn/clinga/".length();
		str = str.substring(len);
		int tail = str.indexOf('.');
		return str.substring(0, tail);
	} 

}
