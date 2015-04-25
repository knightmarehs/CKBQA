package structs;

public class Item implements Cloneable
{
	public String name;
	public enum ItemType{entity,literal,relation,variable};
	public ItemType type;
	public int position = -1;
	
	public Item clone()
	{
		Item item = null;
		try 
		{
			item = (Item)super.clone();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return item;
	}
	
	public Item(String _name, ItemType _type)
	{
		name = _name;
		type = _type;
	}
	
	public String toString()
	{
		if (type == ItemType.entity || type == ItemType.relation)
			return "<"+name+">";
		else if (type == ItemType.literal)
			return "\""+name+"\"";
		else if (type == ItemType.variable)
			return name;
		else
			return name;		//这里应该返回null，为调试方便先改为name
	}
	
	//exp
	public boolean equals(Object o)
	{
		if (!(o instanceof Item))
			return false;
		Item item = (Item)o;
		return this.name.equals(item.name) && this.type==item.type;
	}
	
	public boolean templateEquals(Object o)
	{
		if (!(o instanceof Item))
			return false;
		Item item = (Item)o;
		return  this.type==item.type;
	}
};
