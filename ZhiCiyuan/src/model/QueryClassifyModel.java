package model;

import java.util.ArrayList;
import java.util.Arrays;

import edu.fudan.nlp.parser.dep.DependencyTree;
import nlp.DependencyTreeCore;

public class QueryClassifyModel 
{
	public static ParaphraseModel pm;
	public enum queryType{askMovie,askPeople,askAttribute,unresolved};
	public queryType type;
	public boolean containInstanceMovie = false;
	public boolean containInstancePeople = false;
	public boolean containConceptMovie = false;
	public boolean containConceptPeople = false;
	public boolean containPredicateMovie = false;
	public boolean containPredicatePeople = false;
	public boolean containPredicateM2P = false;
	public ArrayList<String> moviePredicateList = new ArrayList<String>();
	public ArrayList<String> peoplePredicateList = new ArrayList<String>();
	public ArrayList<String> m2pPredicateList = new ArrayList<String>();
	
	public QueryClassifyModel(ParaphraseModel pM) 
	{
		QueryClassifyModel.pm = pM;
	}

	void init()
	{
		containInstanceMovie = false;
		containInstancePeople = false;
		containConceptMovie = false;
		containConceptPeople = false;
		containPredicateMovie = false;
		containPredicatePeople = false;
		containPredicateM2P = false;
	//	姓名、星座、性别、家庭成员、出生日期、出生地、职业、生卒日期、简介
	//  片名、评分、上映日期、导演、片长、地区、编剧、语言、类型、主演、简介
	//  imdbID、首播、单集片长、季数、官方网站、集数
		moviePredicateList = new ArrayList<String>(Arrays.asList("片名","评分","上映日期","导演","片长"
				,"地区","编剧","语言","类型","主演","简介","imdbID","首播","单集片长","季数","官方网站","集数"));
		peoplePredicateList = new ArrayList<String>(Arrays.asList("姓名","星座","性别","家庭成员","出生日期","出生地"
				,"职业","生卒日期","简介","imdbID"));
		m2pPredicateList = new ArrayList<String>(Arrays.asList("主演","导演","编剧"));
	}
	
	public queryType QueryClassify(DependencyTreeCore dtc)
	{
		init();
		
		DependencyTree odt = dtc.odt;
		String[] dsWordArray = odt.toString().split("\n");
		
		for(String row: dsWordArray)
		{
			String[] tmpArray = row.split(" ");
			int id = Integer.parseInt(tmpArray[0]);
			String name = tmpArray[1];
			String posTag = tmpArray[2];
			
			// 这里直接用 分词的posTag进行判断合适吗？用entity searcher和paraphraseModel判断是不是更好
			if(posTag.equals("人名"))
				containInstancePeople = true;
			if(posTag.equals("影视名"))
				containInstanceMovie = true;
			
			// 这里 ”导演“ 被分词认为是 “影视名”，事实上”导演“既是 谓词【电影-人物】也是 类型词【人物】，应该都算
			if(posTag.equals("类型词[电影]"))
				containConceptMovie = true;
			if(posTag.equals("类型词[人物]"))
				containConceptPeople = true;
			
			
			if(pm.getRelatedRelation(name) != null)
			{
				String pre = pm.getRelatedRelation(name).item.name;
				if(moviePredicateList.contains(pre))
					containPredicateMovie = true;
				if(peoplePredicateList.contains(pre))
					containPredicatePeople = true;
				if(m2pPredicateList.contains(pre))
					containPredicateM2P = true;
			}
		}
		
		System.out.println("InstanceMovie:"+containInstanceMovie);
		System.out.println("InstancePeople:"+containInstancePeople);
		System.out.println("ConceptMovie:"+containConceptMovie);
		System.out.println("ConceptPeople:"+containConceptPeople);
		System.out.println("PredicateMovie:"+containPredicateMovie);
		System.out.println("PredicatePeople:"+containPredicatePeople);
		System.out.println("PredicateM2P:"+containPredicateM2P);
		
		//如果电影实例有到达人物实例的边，可以认为是askMovie
		if(containInstanceMovie && containInstancePeople)
			type = queryType.unresolved;
		else if(containInstanceMovie)
		{
			if(containConceptPeople || containPredicateM2P)
				type = queryType.askPeople;
			else if(containPredicateMovie)
				type = queryType.askAttribute;
			else
				type = queryType.askMovie;
		}
		else if(containInstancePeople)// 这里考虑加入虚拟谓词[人物-电影],如<出演>
		{
			if(containConceptMovie)
				type = queryType.askMovie;
			else if(containPredicatePeople)
				type = queryType.askAttribute;
			else
				type = queryType.askPeople;
		}
		else if(containConceptMovie && containConceptPeople)
			type = queryType.unresolved;
		else if(containConceptMovie)
			type = queryType.askMovie;
		else if(containConceptPeople)
			type = queryType.askPeople;
		else
			type = queryType.unresolved;
		
		return type;
	}
}
