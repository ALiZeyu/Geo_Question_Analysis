package Label_tagger;

import java.util.HashSet;

public class CharType {
	static final char[] punctArray = { '!', '！', '\'', '＂', '#', '＃', '$', '＄', '%', '％', '&', '＆', '\'', '＇', '(',
			'（', ')', '）', '*', '＊', '+', '＋', ',', '，', '-', '－', '.', '．', '/', '／', ':', '：', ';', '；', '<', '＜',
			'=', '＝', '>', '＞', '?', '？', '@', '＠', '[', '［', '\\', '＼', ']', '］', '^', '＾', '_', '＿', '`', '｀', '{',
			'｛', '|', '｜', '}', '｝', '~', '～', '！', '！', '“', '“', '#', '＃', '￥', '￥', '%', '％', '&', '＆', '‘', '’',
			'（', '（', '）', '）', '*', '×', '+', '＋', '，', '，', '-', '－', '。', '。', '、', '＼', '：', '：', '；', '；', '《',
			'《', '=', '＝', '》', '》', '？', '？', '@', '＠', '【', '【', '】', '】','·', '·', '{',
			'｛', '|', '｜', '}', '｝', '~', '～', '”', '”' };

	final static char[] letterArray = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
			'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ａ', 'Ｂ', 'Ｃ', 'Ｄ', 'Ｅ',
			'Ｆ', 'Ｇ', 'Ｈ', 'Ｉ', 'Ｊ', 'Ｋ', 'Ｌ', 'Ｍ', 'Ｎ', 'Ｏ', 'Ｐ', 'Ｑ', 'Ｒ', 'Ｓ', 'Ｔ', 'Ｕ', 'Ｖ', 'Ｗ', 'Ｘ', 'Ｙ', 'Ｚ',
			'ａ', 'ｂ', 'ｃ', 'ｄ', 'ｅ', 'ｆ', 'ｇ', 'ｈ', 'ｉ', 'ｊ', 'ｋ', 'ｌ', 'ｍ', 'ｎ', 'ｏ', 'ｐ', 'ｑ', 'ｒ', 'ｓ', 'ｔ', 'ｕ',
			'ｖ', 'ｗ', 'ｘ', 'ｙ', 'ｚ' };

	final static char[] digitArray = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '０', '１', '２', '３', '４',
			'５', '６', '７', '８', '９', '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖', '拾', '佰', '仟', '万', '亿', '〇',
			'一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '百', '千', '零', '壹', '貳', '三', '肆', '伍', '陸', '柒', '捌',
			'玖', '拾', '萬' ,'两'};
	

	public static HashSet<Character> digitSet = new HashSet<Character>();
	public static HashSet<Character> punctSet = new HashSet<Character>();
	public static HashSet<Character> letterSet = new HashSet<Character>();

//	public static void loadCharType()
//	{
//		for (int i = 0; i < digitArray.length; i++)
//			digitSet.add(digitArray[i]);
//		for (int i = 0; i < punctArray.length; i++)
//			punctSet.add(punctArray[i]);
//		for (int i = 0; i < letterArray.length; i++)
//			letterSet.add(letterArray[i]);
//	}
	static{
		for (int i = 0; i < digitArray.length; i++)
			digitSet.add(digitArray[i]);
		for (int i = 0; i < punctArray.length; i++)
			punctSet.add(punctArray[i]);
		for (int i = 0; i < letterArray.length; i++)
			letterSet.add(letterArray[i]);
	}
}
