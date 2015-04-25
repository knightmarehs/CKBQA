package nlp;

import java.util.HashSet;

import edu.fudan.ml.types.Dictionary;
import edu.fudan.nlp.cn.tag.CWSTagger;
import edu.fudan.nlp.cn.tag.NERTagger;
import edu.fudan.nlp.cn.tag.POSTagger;
import edu.fudan.nlp.parser.dep.DependencyTree;
import edu.fudan.nlp.parser.dep.JointParser;

public class NlpTool 
{
	public JointParser parser = null;
	public POSTagger posTagger = null;
	public CWSTagger segTagger = null;
	public NERTagger nerTagger = null;
	//public EntitySearcher entitySearcher = null;
	public HashSet<String> entityPOSTagSet = null;
	public static String basePath = "E:\\Husen\\Code\\workspace\\gAnswerChinese\\";
	public NlpTool()
	{
		System.out.println("NlpTool initialize ...");
		try
		{
			parser = new JointParser(basePath+"models/dep.m");
			//segTagger = new CWSTagger(basePath+"models/seg.m",new Dictionary(basePath+"models/dictCh.txt"));			
			posTagger = new POSTagger(basePath+"models/seg.m", basePath+"models/pos.m",new Dictionary(basePath+"models/dictCh.txt"));
			//nerTagger = new NERTagger(basePath+"models/seg.m", basePath+"models/pos.m");
			
			//entitySearcher = new EntitySearcher();		
			//initEntityPOSTagSet();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("NlpTool initialize done.");
	}

	public DependencyTree getOrignalDSTree(String sentence)
	{
		String[][] s = posTagger.tag2Array(sentence);
		DependencyTree tree = null;
		try 
		{
			tree = parser.parse2T(s[0],s[1]);
			System.out.println("DependencyTree: " + tree.toString());
		} 
		catch (Exception e) 
		{			
			e.printStackTrace();
		}
		return tree;
	}
	public void initEntityPOSTagSet()
	{
		entityPOSTagSet = new HashSet<String>();
		entityPOSTagSet.add("人名");
		entityPOSTagSet.add("地名");
		entityPOSTagSet.add("机构名");
		entityPOSTagSet.add("实体名");
		entityPOSTagSet.add("专有名");
		entityPOSTagSet.add("名词");
		entityPOSTagSet.add("形容词");
		entityPOSTagSet.add("方位词");
		entityPOSTagSet.add("邮件");
		entityPOSTagSet.add("网址");
	}
	public static void main(String[] args) 
	{
		NlpTool nlptool = new NlpTool();
		String sentence = "行尸走肉的演员是谁";
		DependencyTreeCore ds = new DependencyTreeCore(sentence, nlptool);
		//ds.bfsDependencyTree();
	}
}
