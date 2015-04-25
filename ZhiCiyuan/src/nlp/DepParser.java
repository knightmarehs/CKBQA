package nlp;
import edu.fudan.ml.types.Dictionary;
import edu.fudan.nlp.cn.tag.POSTagger;
import edu.fudan.nlp.parser.dep.DependencyTree;
import edu.fudan.nlp.parser.dep.JointParser;
/**
 * 依存句法分析使用示例
 * @author xpqiu
 *
 */
public class DepParser {

	private static JointParser parser;
	
	public String basePath = "";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		parser = new JointParser("E:/Husen/FudanNLP-1.6.1/models/dep.m");
		
		//System.out.println("得到支持的依存关系类型集合");
		//System.out.println(parser.getSupportedTypes());
		
		String word = "最近黄渤和赵薇一起演的那个找孩子的电影叫什么来着？";
		String word1 = "行尸走肉的主演是谁？";
		test(word1);

	}

	/**
	 * 只输入句子，不带词性
	 * @throws Exception 
	 */
	private static void test(String word) throws Exception {		
		POSTagger tag = new POSTagger("E:/Husen/FudanNLP-1.6.1/models/seg.m","E:/Husen/FudanNLP-1.6.1/models/pos.m",new Dictionary("E:/Husen/FudanNLP-1.6.1/models/dictCh.txt"));
		String[][] s = tag.tag2Array(word);
		try {
			for(String[] tmpSarr: s)
			{
				for(String tmpS: tmpSarr)
				{
					System.out.println(tmpS);
				}
			}
			DependencyTree tree = parser.parse2T(s[0],s[1]);
			System.out.println(tree.toString());
			String stree = parser.parse2String(s[0],s[1],true);
			System.out.println(stree);
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
	
	public DependencyTree getDependencyTree(String question) throws Exception
	{
		parser = new JointParser(basePath+"models/dep.m");
		POSTagger tag = new POSTagger(basePath+"models/seg.m",basePath+"models/pos.m");
		
		String[][] s = tag.tag2Array(question);
		DependencyTree tree = parser.parse2T(s[0],s[1]);
		
		return tree;
	}

}
