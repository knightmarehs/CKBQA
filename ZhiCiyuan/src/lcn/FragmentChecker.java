package lcn;

import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import structs.Item;
import structs.Triple;
import structs.Item.ItemType;

public class FragmentChecker 
{
	public IndexSearcher searcher = null;
	public QueryParser parser = null;
	public Analyzer analyzer = null;
	public static String basePath = "E:\\Husen\\Code\\workspace\\gAnswerChinese\\";
	public static String indexPath = basePath+"data\\index\\fragment_index";
	
	public FragmentChecker()
	{
		try
		{
			searcher = new IndexSearcher(indexPath);
			analyzer = new StandardAnalyzer();
			parser = new QueryParser("FragmentIndex", analyzer);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public HashSet<String> searchFragmentInEdge(String queryString)
	{
		HashSet<String> ret = new HashSet<String>();
		String index = null, inEdges = null;
		
		try
		{
			Query query = parser.parse(queryString);
			Hits hits = searcher.search(query);		
			
			//System.out.println(hits.length());
			for (int i=0;i<hits.length() && i<10;i++)				
			{
				index = hits.doc(i).get("FragmentIndex");				
				if (index.equals(queryString))
				{					
					inEdges = hits.doc(i).get("InEdge");					
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (inEdges != null)
		{
			String[] contents = inEdges.split(",");
			for (int i=0;i<contents.length;i++)
				if (!contents[i].equals("null"))
					ret.add(contents[i]);
		}
		
		return ret;
	}
	
	public HashSet<String> searchFragmentOutEdge(String queryString)
	{
		HashSet<String> ret = new HashSet<String>();
		String index = null, outEdges = null;
		
		try
		{
			Query query = parser.parse(queryString);
			
			Hits hits = searcher.search(query);				
			for (int i=0;i<hits.length() && i<10;i++)				
			{
				index = hits.doc(i).get("FragmentIndex");
				if (index.equals(queryString))
				{
					outEdges = hits.doc(i).get("OutEdge");
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ret;
		}
		
		if (outEdges != null)
		{
			String[] contents = outEdges.split(",");
			for (int i=0;i<contents.length;i++)
				if (!contents[i].equals("null"))
					ret.add(contents[i]);
		}
		
		return ret;
	}
	
	public boolean checkTriple(Triple triple)
	{
		System.out.println("check: "+triple);
		Item subject = triple.subject, predicate = triple.predicate, object = triple.object;
		String subString = subject.toString(), preString = predicate.toString(), objString = object.toString();
		HashSet<String> candidates = new HashSet<String>();
		
		System.out.println("sub: "+subString+"  type: "+subject.type);
		System.out.println("pre: "+preString+"  type: "+predicate.type);
		System.out.println("obj: "+objString+"  type: "+object.type);
		
		//VPE
		if (subject.type == ItemType.variable &&
			predicate.type == ItemType.relation &&
			object.type == ItemType.entity)
		{
			candidates = searchFragmentInEdge(objString);
			System.out.println("candidates: " + candidates);
			if (candidates.contains(preString))
				return true;
			else
				return false;
		}
		//EPV
		else if (subject.type == ItemType.entity &&
			predicate.type == ItemType.relation &&
			object.type == ItemType.variable)
		{
			candidates = searchFragmentOutEdge(subString);
			if (candidates.contains(preString))
				return true;
			else
				return false;
		}
		//VPL
		else if (subject.type == ItemType.variable &&
			predicate.type == ItemType.relation &&
			object.type == ItemType.literal)
		{
			candidates = searchFragmentInEdge(objString);
			if (candidates.contains(preString))
				return true;
			else
				return false;		
		}
		else if (subject.type == ItemType.variable &&
			predicate.type == ItemType.relation &&
			object.type == ItemType.variable)
		{
			return true;
		}
		else
			System.out.println("error: illegal triple: "+triple);
		
		return false;
	}
	public static void main(String[] args)
	{
		FragmentChecker fc = new FragmentChecker();
		HashSet<String> res = fc.searchFragmentInEdge("<赵薇>");
		//res = fc.searchFragmentInEdge("<张韵>");
		System.out.println(res);
		
		Triple t = new Triple(new Item("?x", ItemType.variable),
							  new Item("类型", ItemType.relation),
							  new Item("电影", ItemType.entity),
							  1,2,3,0.7);
		Triple tt = new Triple(new Item("高圆圆",ItemType.entity),
								new Item("性别",ItemType.relation),
								new Item("?x",ItemType.variable));
		boolean flag = fc.checkTriple(t);
		System.out.println(flag);
		
		flag = fc.checkTriple(tt);
		System.out.println(flag);
		
	}
}
