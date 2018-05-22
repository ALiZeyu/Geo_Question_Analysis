package pos_tagger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainTest {

	public static void main(String[] args) {
		PosTag tag=new PosTag();
		List<String> a=new ArrayList<>();
		tag.train();
		tag.tagging();
		tag.eva_result(a) ;
//		List<String> train=tag.read_trainFile();
//		tag.get_evaFile(train);
//		tag.run_10cv(train);
	}
}
