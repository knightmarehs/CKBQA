package structs;

import java.util.ArrayList;

public class StructuredQuery implements Comparable<StructuredQuery>
{
	public ArrayList<Triple> tripleList = new ArrayList<Triple>();
	public int tripleNum = 0;
	public double score = 0.0;
	
	public void addTriple(Triple triple)
	{
		tripleList.add(triple);
		tripleNum++;
		score+=triple.score;
	}
	
	public void deleteTriple()
	{
		if (tripleList!=null && !tripleList.isEmpty())		
			tripleList.remove(--tripleNum);		
	}
	
	public void typeTripleComesFirst() 
	{
		int cur = 0;
		for (int i=0; i<tripleList.size(); i++) 		
			if (tripleList.get(i).predicate.equals("ÀàÐÍ") && i > cur) 
			{
				// swap
				Triple temp = tripleList.get(i);
				tripleList.set(i, tripleList.get(cur));
				tripleList.set(cur, temp);
				cur ++;
			}		
	}
	
	public void normalize()
	{
		this.typeTripleComesFirst();
		this.score /= this.tripleNum;
	}
	
	public String toString() 
	{
		String ret = "";
		for (Triple t : tripleList) 
		{
			ret += t.toString();
			ret += '.';
			ret += '\n';
		}
		return ret;
	}
	
	//the structured query with higher score is bigger!
	public int compareTo (StructuredQuery o)
	{
		double diff = score-o.score;
		if (diff > 0) return -1;
		else if (diff < 0) return 1;
		else return 0;
	}
	
	public int hashCode()
	{
		int ret=0;
		for (Triple t : tripleList)
			ret += t.hashCode();
		return ret;
	}
	
	public boolean equals(Object o)
	{
		if (!(o instanceof StructuredQuery))
			return false;
		StructuredQuery sq = (StructuredQuery)o;
		if (tripleNum != sq.tripleNum)
			return false;
		for (int i=0;i<tripleNum;i++)
			if (!tripleList.get(i).equals(sq.tripleList.get(i)))
				return false;
		return true;
	}
	
	public boolean templateEquals(Object o)
	{
		if (!(o instanceof StructuredQuery))
			return false;
		StructuredQuery sq = (StructuredQuery)o;
		if (tripleNum != sq.tripleNum)
			return false;
		for (int i=0;i<tripleNum;i++)
			if (!tripleList.get(i).templateEquals(sq.tripleList.get(i)))
				return false;
		return true;
	}

}
