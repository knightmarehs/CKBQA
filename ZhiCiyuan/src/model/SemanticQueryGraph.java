package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.swing.text.html.HTMLDocument.Iterator;

import lcn.FragmentChecker;
import model.QueryClassifyModel.queryType;
import nlp.DependencyTreeCore;
import structs.DependencyTreeNode;
import structs.Item;
import structs.Item.ItemType;
import structs.StructuredQuery;
import structs.Triple;

public class SemanticQueryGraph 
{
	public DependencyTreeCore ds;
	public ArrayList<DependencyTreeNode> obviousPreNodes;
	public ArrayList<Triple> tripleList;
	public static ParaphraseModel pm;
	
	queryType type;
	FragmentChecker fc = new FragmentChecker();
	public ArrayList<DependencyTreeNode> entityMovieList;
	public ArrayList<DependencyTreeNode> entityPeopleList;
	public ArrayList<DependencyTreeNode> predicateList;
	public Map<DependencyTreeNode,ArrayList<DependencyTreeNode>> directConditionMap;
	public Map<String,ArrayList<String>> indirectConditionMap;
	
	String errMsg = "";
	boolean ready = false;
	
	static public ArrayList<String> predicatesM2P = new ArrayList<String>(Arrays.asList("导演","主演","编剧"));
	
	public void reload()
	{
		entityMovieList = new ArrayList<DependencyTreeNode>();
		entityPeopleList = new ArrayList<DependencyTreeNode>();
		predicateList = new ArrayList<DependencyTreeNode>();
		// map initial
		directConditionMap = new HashMap<DependencyTreeNode,ArrayList<DependencyTreeNode>>();
		indirectConditionMap = new HashMap<String,ArrayList<String>>();
	}
	
	public SemanticQueryGraph(ParaphraseModel pM) 
	{
		SemanticQueryGraph.pm = pM;
	}

	public ArrayList<Triple> getTripleList() 
	{
		tripleList = new ArrayList<Triple>();
		Triple triple;
		if(!ready)
		{
			errMsg = "SQG has not been built.";
			System.out.println("getSPARQL: "+errMsg);
			return null;
		}
		else if(type == queryType.askAttribute)
		{
			Item sub,pre,obj;
			ArrayList<String> entityStringList = new ArrayList<String>();
			ArrayList<String> predicateStringList = new ArrayList<String>();
			if(entityMovieList!=null && entityMovieList.size()>0)
			{
				entityStringList.add(entityMovieList.get(0).word.name);
			}
			if(entityPeopleList!=null && entityPeopleList.size()>0)
			{
				entityStringList.add(entityPeopleList.get(0).word.name);
			}
			if(predicateList!=null && predicateList.size()>0)
			{
				predicateStringList.add(predicateList.get(0).word.name);
			}
			String relation = pm.getRelatedRelation(predicateStringList.get(0)).item.name;
			System.out.println("predicate:"+predicateStringList.get(0)+"mapping relation:"+relation);
			
			sub = new Item(entityStringList.get(0),ItemType.entity);
			pre = new Item(relation,ItemType.relation);
			obj = new Item("?x",ItemType.variable);
			
			triple = new Triple(sub,pre,obj);
			tripleList.add(triple);
			
			System.out.println("Find triple: "+triple.toString());
		}
		else if(type == queryType.askMovie)
		{
			
		}
		else if(type == queryType.askPeople)
		{
			Item sub,pre,obj;
			ArrayList<DependencyTreeNode> attributeList = new ArrayList<DependencyTreeNode>();
			ArrayList<String> predicateStringList = new ArrayList<String>();
			if(directConditionMap.size()>0)
			{
				for(DependencyTreeNode dtn: directConditionMap.keySet())
				{
					attributeList = directConditionMap.get(dtn);
					String pName = dtn.word.name;
					if(predicatesM2P.contains(pName))
					{
						for(DependencyTreeNode tmp: attributeList)
						{
							sub = new Item(tmp.word.name,ItemType.entity);
							pre = new Item(pName,ItemType.relation);
							obj = new Item("?x",ItemType.variable);
							triple = new Triple(sub,pre,obj);
							tripleList.add(triple);
							
							System.out.println("Find triple: "+triple.toString());
						}
					}
					else
					{
						for(DependencyTreeNode tmp: attributeList)
						{
							sub = new Item("?x",ItemType.entity);
							pre = new Item(pName,ItemType.relation);
							obj = new Item(tmp.word.name,ItemType.literal);
							triple = new Triple(sub,pre,obj);
							tripleList.add(triple);
							
							System.out.println("Find triple: "+triple.toString());
						}
					}
				}
			}
			else if(entityPeopleList.size()>0)
			{
				//sub = new Item(entityStringList.get(0),ItemType.entity);
				//pre = new Item("简介",ItemType.relation);
				//obj = new Item("?x",ItemType.variable);
				
				sub = new Item("?x",ItemType.variable);
				pre = new Item("姓名",ItemType.relation);
				obj = new Item(entityPeopleList.get(0).word.name,ItemType.literal);
				
				triple = new Triple(sub,pre,obj);
				tripleList.add(triple);
			}
			else
			{
				System.out.println("getTripleList: "+"askPeople triple is null.");
			}
		}
		return tripleList;
	}
	
/*
	public void buildSemanticQueryGraph(DependencyTreeCore dtc) 
	{
		
		tripleList = new ArrayList<Triple>();
		this.ds = dtc.clone();
		obviousPreNodes = ds.bfsDependencyTree(pm);
		for(DependencyTreeNode dtn: obviousPreNodes)
		{
			System.out.println("obvious predicate: "+dtn.word.name);
		}
		// 直接将谓词和两个变量组成triple，没有保存posTag和节点信息。
		// 进一步可以考虑 补语等 作为 filters
		for(DependencyTreeNode preNode: obviousPreNodes)
		{
			Triple triple;
			if(preNode.father == null)
			{
				for(DependencyTreeNode chd: preNode.childrenList)
				{
					String chd2father = chd.dep_father2child;
					//定语	谓词  null
					if(chd2father.equals("定语"))
					{
						if(chd.word.name.equals("的")&&chd.childrenList.size()>0)
						{
							DependencyTreeNode a = chd.childrenList.get(0).clone();
							triple = new Triple(a.word,preNode.word,null);
							tripleList.add(triple);
						}
						else
						{
							triple = new Triple(chd.word,preNode.word,null);
							tripleList.add(triple);
						}
					}
					//主语	谓词	 null
					else if(chd2father.equals("主语"))
					{
						triple = new Triple(chd.word,preNode.word,null);
						tripleList.add(triple);
						if(chd.childrenList.size()>0)
						{
							for(DependencyTreeNode cchd: chd.childrenList)
							{
								if(cchd.dep_father2child.equals("并列"))
								{
									triple = new Triple(cchd.word,preNode.word,null);
									tripleList.add(triple);
								}
							}
						}
					}
				}
			}
			else
			{
				for(DependencyTreeNode chd: preNode.childrenList)
				{
					String chd2father = chd.dep_father2child;
					if( chd2father.equals("主语")&& (preNode.dep_father2child.equals("宾语")||preNode.dep_father2child.equals("的字结构") )|| chd2father.equals("定语")&&preNode.dep_father2child.equals("状语"))
					{
						Item sub = chd.word,pre = preNode.word,obj = preNode.father.word;
						if(chd.word.name.equals("的")&&chd.childrenList.size()>0)
						{
							DependencyTreeNode a = chd.childrenList.get(0).clone();
							sub = a.word;
						}
						if(preNode.father.word.name.equals("的") && preNode.father.father!=null)
						{
							obj = preNode.father.father.word;	
						}				
						triple = new Triple(sub,pre,obj);
						tripleList.add(triple);
						
						if(chd.childrenList.size()>0)
						{
							for(DependencyTreeNode cchd: chd.childrenList)
							{
								if(cchd.dep_father2child.equals("并列"))
								{
									triple = new Triple(cchd.word,pre,obj);
									tripleList.add(triple);
								}
							}
						}
					}
					else
					{
						System.out.println(chd.word.name+" false");
					}
				}
			}
		}
		
		for(Triple tri: tripleList)
		{
			System.out.println(tri);
		}
//		// test clone
//		ds.root.word.name = "root name modify";
//		System.out.println("ds arr0 name= "+ds.nodesList.get(0).word.name);
//		System.out.println("dtc root name= "+dtc.root.word.name);
//		System.out.println("dtc arr0 name= "+dtc.nodesList.get(0).word.name);
//		
//		ds.root = null;
//		if(dtc.root == null)
//			System.out.println("dtc root is null");
//		else
//			System.out.println("dtc root ok");
//		
//		ds.nodesList.get(1).word.name = "modify";
//		System.out.println(dtc.nodesList.get(1).word.name);
//		
//		ds.nodesList.clear();
//		System.out.println(dtc.nodesList.size());
	
	}

	*/

	public boolean buildSemanticQueryGraph(DependencyTreeCore dtc, queryType type) 
	{
		reload();
		this.type = type;
		this.ds = dtc.clone();
		
		errMsg = "Type is unresolved.";
		boolean flag = false;
		if(type == queryType.askAttribute)
			flag = buildAskAttribute();
		if(type == queryType.askMovie)
			flag = buildAskMovie();
		if(type == queryType.askPeople)
			flag = buildAskPeople();
		
		if(!flag)
			System.out.println("buildSemanticQueryGraph: "+errMsg);
		if(flag)
			ready = true;
		return flag;
	}

	private boolean buildAskPeople() 
	{
		boolean hasEntity = findEntities();
		boolean hasPredicate = findPredicates();
		ArrayList<DependencyTreeNode> neiborsList = new ArrayList<DependencyTreeNode>();
		boolean hasFind = false;
		//通过谓词找属性
		if(hasPredicate)
		{
			for(DependencyTreeNode dtn: predicateList)
			{
				DependencyTreeNode p = dtn.clone();
				String pName = p.word.name;
				if(predicatesM2P.contains(pName))
				{
					//同时包含电影实体和谓词【电影-人物】，直接认为两者匹配。
					if(hasEntity && entityMovieList.size()>0)
					{
						hasFind = true;
						directConditionMap.put(p, entityMovieList);
						System.out.println("predicate:"+pName+" movie:"+entityMovieList.get(0).word.name);
					}
					else
					{
						System.out.println("predicate[movie-people] is recognized to type[people].");
						//将 谓词【电影-人物】视为 类型词【人物】
					}
				}
				else
				{
					//找出 谓词 两步之内的邻居，并列关系的距离算作零
					//两步之内的亲密关系包含了：直接相连、的字结构、共同指向
					neiborsList = findNeibors(p,2,true);
					ArrayList<DependencyTreeNode> attributeList = new ArrayList<DependencyTreeNode>();
					for(DependencyTreeNode tmp: neiborsList)
					{
						System.out.println(pName+" neighbors: "+tmp.word.name);
						//这里的谓词不包含M2P，所以顺序为：<?x><predicate><neighbors>
						Triple t = new Triple(new Item("?x",ItemType.entity),
								new Item(pName,ItemType.relation),
								new Item(tmp.word.name,ItemType.variable));
						boolean flag = fc.checkTriple(t);
						if(flag)
						{
							System.out.println("check triple ok.");
							attributeList.add(tmp);
						}
						else
							System.out.println("check triple error.");
					}
					if(attributeList.size()>0)
					{
						hasFind = true;
						directConditionMap.put(p, attributeList);	
					}
				}
			}
		}
		//根据省略谓词的属性，还原完整条件
		
		if(!hasFind)
			return false;
		return true;
	}

	private boolean buildAskMovie() 
	{
		boolean hasEntity = findEntities();
		boolean hasPredicate = findPredicates();
		
		boolean hasCondition = findCondition();
		return false;
	}

	private boolean buildAskAttribute() 
	{
		boolean hasEntity = findEntities();
		boolean hasPredicate = false;
		if(hasEntity)
		{
			hasPredicate = findPredicates();
			if(!hasPredicate)
				errMsg = "Type is askAttribute, has entities, but no predicates.";
		}
		else
		{
			errMsg = "Type is askAttribute but has no entities.";
		}
		if(hasEntity && hasPredicate)
			return true;
		else
			return false;
	}

	private boolean findPredicates() 
	{
		boolean hasFind = false;
		Queue<DependencyTreeNode> queue = new LinkedList<DependencyTreeNode>();
		if(ds.root == null)
			return false;
		queue.add(ds.root);
		DependencyTreeNode h;
		while(queue.peek()!=null)
		{
			h = queue.poll();
			//System.out.print("level:"+h.levelInTree+" name:"+h.word.name+" posTag:"+h.posTag+" id:"+h.word.position+" relation:"+h.dep_father2child+" father:");
			for(DependencyTreeNode chd: h.childrenList)
			{
				queue.add(chd);
			}
			/*
			 * 找出显式谓词，这里用近义词词典
			 * 将谓词放入predicateList
			 * */
			if(pm.getRelatedRelation(h.word.name) != null)
			{
				hasFind = true;
				predicateList.add(h);
			}
		}
		return hasFind;
	}

	private boolean findEntities() 
	{
		boolean hasFind = false;
		Queue<DependencyTreeNode> queue = new LinkedList<DependencyTreeNode>();
		if(ds.root == null)
			return false;
		queue.add(ds.root);
		DependencyTreeNode h;
		while(queue.peek()!=null)
		{
			h = queue.poll();
			//System.out.print("level:"+h.levelInTree+" name:"+h.word.name+" posTag:"+h.posTag+" id:"+h.word.position+" relation:"+h.dep_father2child+" father:");
			for(DependencyTreeNode chd: h.childrenList)
			{
				queue.add(chd);
			}
			/*
			 * 找出电影实体和人物实体,这里直接用PosTag判断，可能出现 人名 并不在数据集中也会认作 人物实体 的情况。
			 * 将实体直接加入entityMovieList和entityPeopleList
			 * */
			if(h.posTag.equals("影视名"))
			{
				hasFind = true;
				entityMovieList.add(h);
			}
			if(h.posTag.equals("人名"))
			{
				hasFind = true;
				entityPeopleList.add(h);
			}
		}
		return hasFind;
	}
	
	private ArrayList<DependencyTreeNode> findNeibors(DependencyTreeNode r,int maxDep, boolean considerCoordinative) 
	{
		ArrayList<DependencyTreeNode> ret = new ArrayList<DependencyTreeNode>();
		
		Queue<DependencyTreeNode> queue = new LinkedList<DependencyTreeNode>();
		DependencyTreeNode h;
		if(r == null)
			return null;
		queue.add(r);
		
		int dep = 0;
		ret.add(r);
		while(queue.peek()!=null && dep<maxDep)
		{
			dep++;
			h = queue.poll();
			if(h.father != null && !ret.contains(h.father))
			{
				queue.add(h.father);
				ret.add(h.father);
			}
			for(DependencyTreeNode chd: h.childrenList)
			{
				if(!ret.contains(chd))
				{
					queue.add(chd);
					ret.add(chd);
				}
			}
			
			//考虑并列关系，并列边距离为0，填入并列节点。
			if(considerCoordinative)
			{
				//最大探索并列关系的次数设为25，语法树直径几乎不会超过20
				ArrayList<DependencyTreeNode> tmp;
				int LIFE = 25;
				while(LIFE>0)
				{
					tmp = new ArrayList<DependencyTreeNode>();
					for(DependencyTreeNode dtn: ret)
					{
						if(dtn.father!=null && dtn.dep_father2child.equals("并列") && !ret.contains(dtn.father))
						{
							tmp.add(dtn.father);
						}
						for(DependencyTreeNode chd: dtn.childrenList)
						{
							if(chd.dep_father2child.equals("并列") && !ret.contains(chd))
							{
								tmp.add(chd);
							}
						}
					}
					for(DependencyTreeNode it: tmp)
					{
						ret.add(it);
						queue.add(it);
					}
					if(tmp.size()<1)
						break;
					LIFE--;
				}
			}
		}
		return ret;
	}
	
	private boolean findCondition() 
	{
		
		return false;
	}

}
