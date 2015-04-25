package lcn;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class RelationSearcher 
{
	public IndexSearcher searcher = null;
	public QueryParser parser = null;
	public Analyzer analyzer = null;
	public static String basePath = "E:\\Husen\\Code\\workspace\\gAnswerChinese\\";
	public static String indexPath = basePath+"data/index/relation_index";
	
	public RelationSearcher()
	{
		try
		{
			searcher = new IndexSearcher(indexPath);
			analyzer = new StandardAnalyzer();
			parser = new QueryParser("Index", analyzer);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String searchRelation(String edgeDir, String entity, String type)
	{
		if (entity==null) return null;
		
		String queryString = edgeDir + "\t" + entity + "\t" + type;
		String relation = null;
		double maxScore = 0.0;
		
		try
		{
			Query query = parser.parse(queryString);
			
			Hits hits = searcher.search(query);	
			//System.out.println(hits.length());
			for (int i=0;i<hits.length() && i<20;i++)
			{
				String index = hits.doc(i).get("Index");
				System.out.println(index);
				System.out.println(hits.score(i));
				if (hits.score(i) > maxScore || index.equals(queryString))
				{
					maxScore = hits.score(i);
					if (index.equals(queryString)) maxScore=1.0;
					relation = hits.doc(i).get("Relation");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (maxScore >= 0.7)
			//return entityName + "-" + entityType;
			return relation;
		else
			return null;
	}
	
	public static void main(String[] args)
	{
		
		
		RelationSearcher rs = new RelationSearcher();
		String ans = rs.searchRelation("IN", "张国立","电视剧");
		System.out.println(ans);
		ans = rs.searchRelation("IN", "周杰伦","all");
		System.out.println(ans);	
	}
}
