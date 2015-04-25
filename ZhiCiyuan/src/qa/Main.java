package qa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import model.ParaphraseModel;
import model.QueryClassifyModel;
import model.QueryClassifyModel.queryType;
import model.QueryMappingModel;
import model.SemanticQueryGraph;
import nlp.DependencyTreeCore;
import nlp.NlpTool;
import structs.StructuredQuery;
import structs.Triple;
import jgsc.GstoreConnector;

public class Main 
{
	public static ArrayList<String> getAnswerFromGStore (StructuredQuery sq)
	{
		ArrayList<String> ret = new ArrayList<String>();
			
		try
		{	
			// initialize the GStore server's IP address and port.
			GstoreConnector gc = new GstoreConnector("172.31.19.13", 3304);
			
		    // build a new database by a RDF file.
		    // note that the relative path is related to gserver.
			//gc.build("db_movie", "example/rdf_triple/LUBM_10_GStore.n3");
			
			// then you can execute SPARQL query on this database.
			String sparql = "select ?x where "
					+ "{"
					+ "?x    <主演>	<高圆圆>."
					+ "?x	<主演>	<黄海波>."
					+ "}";				
			String answer;
			//= gc.query(sparql);
			//System.out.println(answer);
			
			// unload this database.
			//gc.unload("db_movie");
			
		    // also, you can load some exist database directly and then query.
		    //gc.load("db_movieC");
			
			String newSparql = "select ?x where "
					+ "{"
					+ sq.toString()
					+ "}";	
			
			System.out.print("new sparql:"+sq.toString());
			
		    answer = gc.query(newSparql);	    
			System.out.println(answer);
			
			
			//把string的answer变成 arraylist的answer
			int answerNum=0;
			String[] arrAnswer = null;
			
			//这里的answernum还需要改。
			//if(answer.indexOf("There has answer:")!=-1)
			if(answer!=null)
			{
				int blankNum = 0;
				for(int i=0;i<answer.length();i++)
				{
					char ch = answer.charAt(i);
					if(ch == '\n')
						blankNum++;
				}
				answerNum = blankNum;
			
				arrAnswer = answer.split("\n");
				
			}			
			if (answerNum > 0) 
			{
				System.out.println("find "+answerNum+".");
				for (int i=0;i<answerNum;i++)
				{
					if (arrAnswer[i]!=null) 
					{
						ret.add(arrAnswer[i]);
						System.out.println(ret.get(i));
					}
				}					
			}
			else
				System.out.println("can not find!");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;		
	}
	
	public ArrayList<StructuredQuery> getStructuredQueryList(String rawInput)
	{
		ArrayList<Triple> tripleList = null;
		ArrayList<StructuredQuery> sparqlRankedList = null;
		long t  = System.currentTimeMillis();
		
		// step 1: generate dependency tree
		DependencyTreeCore ds = new DependencyTreeCore(rawInput, nlpTool);
		
		queryType type = qcModel.QueryClassify(ds);
		System.out.println("Query classify: " + type);
	
		// step 2: generate semantic query graph
		sqgModel.buildSemanticQueryGraph(ds,type);
		tripleList = sqgModel.getTripleList();
		
		// step 3: generate SPARQL
		sparqlRankedList = qmModel.getSparqlList(tripleList);
				
		int sqNum=0;
		if(sparqlRankedList != null)
			for (StructuredQuery sq : sparqlRankedList)
			{
				System.out.println("["+ (++sqNum) + "] " + sq.score);
				System.out.println(sq);
			}	
		System.out.println("time="+(System.currentTimeMillis()-t)+"ms");
		return sparqlRankedList;
	}
		
	public static NlpTool nlpTool = null;
	public static SemanticQueryGraph sqgModel = null;
	public static QueryClassifyModel qcModel = null;
	public static QueryMappingModel qmModel = null;
	public static boolean isLoaded = false;
	public static ParaphraseModel pm = null;
	
	public void load()
	{
		if (!isLoaded)
		{
			nlpTool = new NlpTool();
			pm = new ParaphraseModel();
			sqgModel = new SemanticQueryGraph(pm);
			qcModel = new QueryClassifyModel(pm);
			qmModel = new QueryMappingModel();
			isLoaded = true;
		}
	}
	
	public Main()
	{
		load();
	}
		
		
	public static void main(String[] args)
	{
		Main facade = new Main();

		ArrayList<StructuredQuery> sparqlRankedList = null;
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		try
		{
			while (true)
			{
				System.out.println("Please input the question: ");
				String input = reader.readLine();
				sparqlRankedList = facade.getStructuredQueryList(input);
				if(sparqlRankedList != null)
					for (StructuredQuery t : sparqlRankedList)
					{
						System.out.println(t);
						System.out.println("-----");
					}
				if (sparqlRankedList != null && !sparqlRankedList.isEmpty())
					Main.getAnswerFromGStore(sparqlRankedList.get(0));
				else
					System.out.println("can not generate sparql query!");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}

}
