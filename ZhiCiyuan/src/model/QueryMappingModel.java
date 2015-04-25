package model;

import java.util.ArrayList;

import structs.StructuredQuery;
import structs.Triple;

public class QueryMappingModel 
{
	public ArrayList<StructuredQuery> queryList;
	public ArrayList<StructuredQuery> getSparqlList(ArrayList<Triple> tripleList)
	{
		if(tripleList==null || tripleList.size()<1)
		{
			System.out.println("QueryMappingModel: "+"tripleList is empty.");
			return null;
		}
		
		StructuredQuery query = new StructuredQuery();
		queryList = new ArrayList<StructuredQuery>();
		
		for(Triple t: tripleList)
		{
			query.addTriple(t);
		}
		
		queryList.add(query);
		return queryList;
	}
	
}
