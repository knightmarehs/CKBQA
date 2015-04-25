package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import structs.Item;
import structs.ItemAndScore;
import structs.Item.ItemType;

public class ParaphraseModel 
{
	public HashMap<String, ArrayList<ItemAndScore> > relationParaphraseDict = new HashMap<String, ArrayList<ItemAndScore> >();
	public HashMap<String, ArrayList<ItemAndScore> > typeParaphraseDict = new HashMap<String, ArrayList<ItemAndScore> >();
	public static String basePath = "E:\\Husen\\Code\\workspace\\gAnswerChinese\\";
	
	public ParaphraseModel()
	{
		loadRelationParaphraseDict();
		loadTypeParaphraseDict();
	}
	
	public void loadRelationParaphraseDict()
	{
		System.out.println("loadRelationParaphraseDict() ...");
		File inputFile = new File(basePath+"data/paraphrase/relation_paraphrase_dictionary.txt");//put file path here...
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "utf-8"));
			String line;
			
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("#")) continue;
				
				String[] contents = line.split("\t");
				if(contents.length!=3)	//多于两个TAB或少于两个TAB的，直接抛弃。
					continue;
				
				String relation = contents[0], paraphrase = contents[1];
				Double score = Double.parseDouble(contents[2]);
				ItemAndScore ias = new ItemAndScore(new Item(relation,ItemType.relation), score);
				
				if (!relationParaphraseDict.containsKey(paraphrase))
					relationParaphraseDict.put(paraphrase, new ArrayList<ItemAndScore>());
				relationParaphraseDict.get(paraphrase).add(ias);

			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		for (String key : relationParaphraseDict.keySet())
		{
			ArrayList<ItemAndScore> al = relationParaphraseDict.get(key);
			Collections.sort(al);
		}
		System.out.println("loadRelationParaphraseDict() done.");	
	}
	
	public void loadTypeParaphraseDict()
	{
		System.out.println("loadTypeParaphraseDict() ...");
		File inputFile = new File(basePath+"data/paraphrase/type_paraphrase_dictionary.txt");//put file path here...
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "utf-8"));
			String line;
			
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("#")) continue;
				
				String[] contents = line.split("\t");
				String type = contents[0], paraphrase = contents[1];
				Double score = Double.parseDouble(contents[2]);
				ItemAndScore ias = new ItemAndScore(new Item(type,ItemType.entity), score);
				
				if (!typeParaphraseDict.containsKey(paraphrase))
					typeParaphraseDict.put(paraphrase, new ArrayList<ItemAndScore>());
				typeParaphraseDict.get(paraphrase).add(ias);
				System.out.println(type+" "+paraphrase+" "+score);

			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		for (String key : typeParaphraseDict.keySet())
		{
			ArrayList<ItemAndScore> al = typeParaphraseDict.get(key);
			Collections.sort(al);
		}
		
		System.out.println("loadTypeParaphraseDict() done.");		
	}
	
	public ItemAndScore getRelatedType(String paraphrase)
	{
		if (paraphrase!=null && 
			typeParaphraseDict.containsKey(paraphrase))	
			return typeParaphraseDict.get(paraphrase).get(0);
		else
			return null;		
	}
	
	public ItemAndScore getRelatedRelation(String paraphrase)
	{
		if (paraphrase!=null &&
			relationParaphraseDict.containsKey(paraphrase))	
			return relationParaphraseDict.get(paraphrase).get(0);
		else
			return null;			
	}
	
	public static void main(String[] args) throws IOException
	{
		ParaphraseModel pm = new ParaphraseModel();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
			System.out.println("Please input the relation: ");
			String input = reader.readLine();
			if(pm.getRelatedRelation(input)!=null)
				System.out.println(pm.getRelatedRelation(input).item.name);
			else
				System.out.println("null");
		}
	}
	

}
