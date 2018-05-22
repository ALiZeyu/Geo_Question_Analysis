package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.riot.system.StreamRDF;

import DP.DepTree;

public class Util {
	
	public static void main(String[] args) {
		//nonsense("E://workspace/StanfordDP/data/baike_page/"); 
	}
	
	public static void nonsense(String path){
		List<String> file_names = getFileList(path);
		List<String> attribute = new ArrayList<>();
		Set<String> attri_set = new HashSet<>();
		for(String name : file_names){
			List<String> data = read_file(name);
			for(String str : data){
				String[] array = str.split("\t\t", 2);
				if(array.length!=2) continue;
				String[] attri = str.split("\t\t");
				//当前实体的属性集合
				for(int i=1; i<attri.length; i++){
					String temp = attri[i].split(":::")[0];
					if(temp.endsWith(":")||temp.endsWith("：")) temp = temp.substring(0, temp.length()-1);
					attri_set.add(temp);
				}
			}
		}
		attribute.addAll(attri_set);
		writeFile(attribute, "data/all_attribute.txt");
	}
	
	public static List<String> read_file(String path){
		List<String> result = new ArrayList<>();
		try {
			BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));
			String line = br1.readLine();
			if (line!=null && line.startsWith("\uFEFF")) {
				line = line.substring(1);
			}
			while (line != null) {
				result.add(line.trim());
				line = br1.readLine();
			}
			br1.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	/*匹配到第一个字符串就返回**/
	public static String regex_match(String regex, String sen){
		String result = "";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(sen);
		while(m.find()&&result.equals("")){
			int start = m.start();
			int end = m.end();
			result = sen.substring(start, end);
		}
		return result;
	}
	//暂时要求每个词的长度都要大于2，后期可以修改
	public static Set<String> read_dic(String path){
		Set<String> result = new HashSet<>();
		try {
			BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));
			String line = br1.readLine();
			if (line.startsWith("\uFEFF")) {
				line = line.substring(1);
			}
			while (line != null) {
				if(line.trim().length()>2)
					result.add(line.trim());
				line = br1.readLine();
			}
			br1.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void writeFile(List<String> data,String file){
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			for(String str:data)
				writer.write(str+"\n");
			writer.flush();
			writer.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**获取当前路径下所有的文件名*/
	public static List<String> getFileList(String path){
		File file = new File(path);
		File[] list = file.listFiles();
		List<String> result = new ArrayList<>();
		for(File f:list){
			result.add(f.getAbsolutePath());
		}
		return result;
	}
	
	/**获取当前文件夹下所有文件的内容List。*/
	public static List<String> getAllFileContentList(String path){
		List<String> final_result=new ArrayList<>();
		List<String> fileList=Util.getFileList(path);
		for(String str : fileList){
			List<String> temp = Util.read_file(str);
			final_result.addAll(temp);
		}
		return final_result;
	}
	
	public static void cut(String path, long lon) {
        File file = new File(path);
        //int num = 10;//分割文件的数量
 
        //long lon = file.length() / 10L + 1L;//使文件字节数+1，保证取到所有的字节
        try {
            RandomAccessFile raf1 = new RandomAccessFile(file, "r");
 
            byte[] bytes = new byte[1024];//值设置越小，则各个文件的字节数越接近平均值，但效率会降低，这里折中，取1024
            int len = -1;
            String name = "data/small_wiki.txt";
            File file2 = new File(name);
            RandomAccessFile raf2 = new RandomAccessFile(file2, "rw");
            while ((len = raf1.read(bytes)) != -1){//读到文件末尾时，len返回-1，结束循环
	            raf2.write(bytes, 0, len);
	            if (raf2.length() > lon)//当生成的新文件字节数大于lon时，结束循环
	                break;
            }
            raf2.close();
            raf1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }
	
	public static void command_exec(String[] p_args){
		try {
//			String[] p_args = new String[] { "python", "E:\\workspace\\StanfordDP\\data\\cmdTest.py", "word.txt" };
            Process pr = Runtime.getRuntime().exec(p_args);
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            pr.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static String list_to_str(List<String> seg){
		StringBuffer sb = new StringBuffer();
		for(String word : seg)
			sb.append(word.split("_")[0]+" ");
		return sb.toString().trim();
	}
	/**利用。；！切分句子*/
	public static List<String> sen_split(String sen){
		Set<Character> punctuation = new HashSet<>();
		punctuation.add('。');
		punctuation.add('；');
		punctuation.add('！');
		punctuation.add('，');
		punctuation.add(',');
		List<String> result = new ArrayList<>();
		sen = sen.replace(" ", "");
		int index = 0, b=0, e=0;
		while(index<sen.length()){
			if(punctuation.contains(sen.charAt(index))){
				if(index-b > 6)
					result.add(sen.substring(b,index + 1));
				index++;
				b=index;
			}else{
				index++;
			}
		}
		if(index == sen.length() && index-b > 6)
			result.add(sen.substring(b,index));
		return result;
	}
	
	//判断句子中是否含有特定的词语
	public static String array_contain_split(String sen, String[] array){
		int max=1;
		String sp_str = null;
		for(String str : array)
			if(sen.contains(str)){
				int num = sen.split(str).length;
				if(num>max){
					max = num;
					sp_str = str;
				}
			}
		return sp_str;
	}
	
	public static String array_contain(String sen, String[] array){
		String sp_str = null;
		for(String str : array)
			if(sen.contains(str)){
				return str;
			}
		return sp_str;
	}
	
	public static boolean array_count(List<String> infobox, String[] array){
		int num = 0;
		for(String trigger : array){
			for(String info : infobox)
				if(info.replaceAll(" ", "").contains(trigger)){
					num++;
					break;
				}
		}
		int bound = array.length/2>1?array.length/2:1;
		return bound<=num?true:false;
	}
	/**
	 * 获取数组最大值所在的位置下标
	 * */
	public static int array_max(int[] count){
		int max = 0;
		for(int i=1;i<count.length;i++)
			if(count[i]>count[max])
				max = i;
		return max;
	}
	
	/**
	 * 获取数组范围内最大值所在的位置下标
	 * */
	public static int array_range_max(int[] count, int b, int e){
		int max = b;
		for(int i=b;i<e;i++)
			if(count[i]>count[max])
				max = i;
		return max;
	}
	
	//用于寻找A是key的value以及key的value是A,获取那个“是”
	/**
	 * 输入：分词结果。属性词的index。关注的前后范围
	 * */
	public static int get_is_index(List<String> seg, int index){
		int range = 4;
		String[] trigger = {"是_", "为_", "作为_", "担任_", "：_", ":_", "有_"};
		for(int i=index-2;i<=index+2;i++){
			if(i>=0 && i<seg.size()){
				for(String word : trigger)
					if(seg.get(i).startsWith(word)){
						if(i<index && word.matches("[：|:].*"))
							continue;
						return i;
					}
			}
		}
		return -1;
	}
	
	//截取到当前句子末尾，即找到；，。等
	public static int get_next_split(List<String> seg, int index, int direction){
		for(int i = index+direction; i<seg.size()&& i>=0; i+=direction)
			if(seg.get(i).matches("[，|,|。|；|;].*"))
				return i;
		return direction == 1?seg.size():0;
	}
	
}
