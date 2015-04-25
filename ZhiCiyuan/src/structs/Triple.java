package structs;

public class Triple 
{	
	public Item subject, object, predicate;
	public int subPosition, objPosition, prePosition;
	double score;
	
	public Triple (Item s, Item p, Item o)
	{
		subject = s; predicate = p; object = o;
	}
	
	public Triple (Item s, Item p, Item o, int idx1, int idx2, int idx3)
	{
		subject = s; predicate = p; object = o;
		subPosition = idx1; prePosition = idx2; objPosition = idx3; 
	}

	public Triple (Item s, Item p, Item o, int idx1, int idx2,int idx3, double sco)
	{
		subject = s; predicate = p; object = o;
		subPosition = idx1; prePosition = idx2; objPosition = idx3; 
		score = sco;
	}
	
	public String toString()
	{		
		return subject+"\t"+predicate+"\t"+object;
	}
	
	
	public int hashCode()
	{
		return this.toString().hashCode();
	}
	
	//exp
	public boolean equals(Object o)
	{
		if (!(o instanceof Triple))
			return false;
		Triple t = (Triple)o;
		return subject.equals(t.subject) && object.equals(t.object) && predicate.equals(t.predicate);		
	}

	public boolean templateEquals(Object o)
	{
		if (!(o instanceof Triple))
			return false;
		Triple t = (Triple)o;
		return subject.templateEquals(t.subject) && object.templateEquals(t.object) && predicate.templateEquals(t.predicate) &&
			   subPosition==t.subPosition && objPosition==t.objPosition && prePosition==t.prePosition;		
	}
};
