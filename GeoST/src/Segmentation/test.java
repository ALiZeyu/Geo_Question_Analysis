package Segmentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Evaluate.Evaluate;

public class test {
	
	public static void main(String[] args) {
		WordSegment feature = new WordSegment();
		feature.train();
		feature.seg();
		//Evaluate.calculate("E:/PyWorkspace/Crawler/data/output.txt", "F://final_test/data/seg/eva.txt");
	}

}
