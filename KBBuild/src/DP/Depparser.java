package DP;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class Depparser {


	private static MaxentTagger tagger;
	private static DependencyParser parser;
	private static String modelPath = "edu/stanford/nlp/models/parser/nndep/CTB_CoNLL_params.txt.gz";
	private static String taggerPath = "edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger";
	
	public static void init() {
	    tagger = new MaxentTagger(taggerPath);
	    parser = DependencyParser.loadFromModelFile(modelPath);
	}
	
	public static DepTree depparse (String text) {
	    List<DepTree> list = new ArrayList<DepTree>();
	    list.add(new DepTree(0, "ROOT"));
	    DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
	    List<TypedDependency> l = null;
	    for (List<HasWord> sentence : tokenizer) {
	      List<TaggedWord> tagged = tagger.tagSentence(sentence);
	      int i = 1;
	      for (TaggedWord tw : tagged) {
	    	  list.add(new DepTree(i, tw.word()));
	    	  i++;
	      }
	      GrammaticalStructure gs = parser.predict(tagged);
	      l = (List<TypedDependency>)gs.typedDependencies();
	    }
	    if (l != null)
	    for (TypedDependency td : l) {
	    	list.get(td.gov().index()).child.add(list.get(td.dep().index()));
	    	list.get(td.gov().index()).rel.add(td.reln().getShortName());
	    }
	    return list.get(0);
	}
	
	
	public static void get_node_index(DepTree t, int index, List<String[]> result){
		if (t.child.size() > 0) {
			for (int i = 0; i < t.child.size(); i++) {
				if(t.no == index){
					String[] temp = new String[3];
					temp[0] = "head";
					temp[1] = t.rel.get(i);
					temp[2] = String.valueOf(t.child.get(i).no);
					result.add(temp);
				}
				else if (t.child.get(i).no == index) {
					String[] temp = new String[3];
					temp[0] = "tail";
					temp[1] = t.rel.get(i);
					temp[2] = String.valueOf(t.no);
					result.add(temp);
				}
			}
			for (DepTree dt : t.child) {
				get_node_index(dt, index, result);
			}
		}
	}

}
