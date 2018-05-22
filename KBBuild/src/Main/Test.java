package Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import DP.DepTree;
import DP.Depparser;
import DP.Segment;
import DP.ValueExtraction;
import Segmentation.State;
import Util.Util;


public class Test {

	public static void main(String[] args) throws IOException {
		Depparser.init();
		Segment.init();
		
//		String[] array = {"福州 作为 福建 省会", "福建 省会 是 福州", "江大桥 担任 南京 市长", "北京 市长 为 江大桥"};
//		String[] sen={"福州作为福建省会","福建省会是福州","江大桥担任南京市长","北京市长为刘琦"};
//		int[] trigger = {1,2,1,2};
//		boolean[] flag = {true, false, true, false};
//		for(int i=0;i<array.length;i++){
//			List<String> seg_list = Segment.segmentQuestion(sen[i]);
//			System.out.println(seg_list);
//			List<String> list = ValueExtraction.extract_by_dep(seg_list, trigger[i], flag[i]);
//			System.out.println(list);"data/num_sample.txt"
//		}E://workspace/StanfordDP/data/canSen/short.txtE://workspace/StanfordDP/data/canSen/sample.txt
		ValueExtraction.extraction_pipeline("E://workspace/StanfordDP/data/canSen/200query.txt");
//		String[] array = {"福建省", "厦门", "福州", "福建省", "福州", "福建省", "福建", "地理", "城市", "福建省", "福建省", "厦门", "福州", "福建省", "福建", "城市", "福建", "福建", "城市", "福建", "厦门", "福州", "福建"};
//		List<String> list = Arrays.asList(array);
//		System.out.println(ValueExtraction.post_count(list, "福建省", true));
		
		
//		String segStr = "北海 已经 成为 中国 对 外 开放 中 升起 的 一 颗 新星";
//		DepTree dependencyTree = Depparser.depparse(segStr);
//		System.out.println(dependencyTree.toString());
		
//		try {
//			Segment.init();
//			System.out.println(Segment.segmentQuestion("福建农林大学是农业部、国家林业局与福建省政府共建大学"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	public double[] medianSlidingWindow(int[] nums, int k) {
        List<Integer> list = new ArrayList<>();
        for(int i=0;i<k-1;i++) list.add(nums[i]);
        Collections.sort(list);
        double[] r = new double[nums.length-k+1];
        for(int i=k-1;i<nums.length;i++){
            int index = get(list, nums[i]);
            list.add(index, nums[i]);
            if((k & 1)==1)
                r[i-k+1] = (double)list.get(k/2);
            else
                r[i-k+1] = ((double)list.get(k/2-1) + (double)list.get(k/2))/2;
            list.remove((Integer)nums[i-k+1]);
        }
        return r;
    }
    
    public int get(List<Integer> list, int num){
        int b=0,e=list.size()-1,mid = b;
        while(b<=e){
            mid = (b+e)/2;
            if(list.get(mid)>=num)
                e = mid-1;
            else
                b = mid+1;
        }
        return b<list.size()?b:list.size()-1;
    }
	
	public static int firstGreatOrEqual(int[] a, int n, int key){  
        //n + 1 个数  
        int low = 0;  
        int high = n;  
        int mid = 0;  
        while(low <= high) {  
            mid = low + ((high-low) >> 1);  
            if(key <= a[mid]) {  
                high = mid - 1;  
            } else {  
                low = mid + 1;  
            }  
        }  
        return low <= n ? low : -1;  
    }  

}
