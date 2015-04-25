package nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import model.ParaphraseModel;
import edu.fudan.nlp.parser.dep.DependencyTree;
import structs.DependencyTreeNode;
import structs.Item;

public class DependencyTreeCore implements Cloneable
{
	public DependencyTreeNode root = null;
	public ArrayList<DependencyTreeNode> nodesList = null;
	public DependencyTree odt;
	
	public DependencyTreeCore clone()
	{
		DependencyTreeCore ret = null;
		try 
		{
			ret = (DependencyTreeCore)super.clone();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		//ret.nodesList = (ArrayList<DependencyTreeNode>) this.nodesList.clone();
		ret.nodesList = new ArrayList<DependencyTreeNode>();
		for(DependencyTreeNode dtn: this.nodesList)
		{
			ret.nodesList.add(dtn.clone());
		}
		ret.root = this.root.clone();
		
		return ret;
	}
	public DependencyTreeCore (String sentence, NlpTool nlptool)
	{
		odt = nlptool.getOrignalDSTree(sentence);
		//HashMap<Integer, DependencyTreeNode> map = new HashMap<Integer, DependencyTreeNode>();
		nodesList = new ArrayList<DependencyTreeNode>();
		
		String[] dsWordArray = odt.toString().split("\n");
		// generate nodes
		for(String row: dsWordArray)
		{
			String[] tmpArray = row.split(" ");
			int id = Integer.parseInt(tmpArray[0]);
			String name = tmpArray[1];
			String posTag = tmpArray[2];
			Item word = new Item(name,null);
			word.position = id;
			DependencyTreeNode newNode = new DependencyTreeNode(word,posTag);
			nodesList.add(newNode);
			if(tmpArray[3].equals("-1"))
			{
				this.root = newNode;
				newNode.levelInTree = 0;
				//System.out.println("this root: "+this.root.levelInTree + " " + this.root.posTag);
			}
		}
		// add edges
		for(String row: dsWordArray)
		{
			String[] tmpArray = row.split(" ");
			int id = Integer.parseInt(tmpArray[0]);
			int fid = Integer.parseInt(tmpArray[3]);
			String e = tmpArray[4];
			DependencyTreeNode child = nodesList.get(id);
			if(fid == -1)
			{
				child.dep_father2child = e;
				// e 为"核心词"
			}
			else
			{
				DependencyTreeNode father = nodesList.get(fid);
				child.father = father;
				father.childrenList.add(child);
				child.dep_father2child = e;
			}
		}
		// count level
		Stack<DependencyTreeNode> stack = new Stack<DependencyTreeNode>();
	    stack.push(this.root);
	    while (!stack.empty()) 
	    {
	    	DependencyTreeNode dtn = stack.pop();
	    	if (dtn.father != null) 
	    	{	    	
		    	dtn.levelInTree = dtn.father.levelInTree + 1;
		    	dtn.sortChildrenList();	//需要排序吗
	    	}
	    	for (DependencyTreeNode chd : dtn.childrenList) 
	    	{
	    		stack.push(chd);
	    	}
	    }
	}
	
	//BFS a dependency tree and return obvious predicates.
	public ArrayList<DependencyTreeNode> bfsDependencyTree(ParaphraseModel pm)
	{
		ArrayList<DependencyTreeNode> res = new ArrayList<DependencyTreeNode>();
		Queue<DependencyTreeNode> queue = new LinkedList<DependencyTreeNode>();
		if(this.root == null)
			return null;
		queue.add(this.root);
		DependencyTreeNode h,p;
		while(queue.peek()!=null)
		{
			h = queue.poll();
			System.out.print("level:"+h.levelInTree+" name:"+h.word.name+
					" posTag:"+h.posTag+" id:"+h.word.position+" relation:"+h.dep_father2child+" father:");
			String fa = null;
			if(h.father != null)
				fa = h.father.word.name;
			System.out.println(fa);
			for(DependencyTreeNode chd: h.childrenList)
			{
				queue.add(chd);
			}
			/*
			 * 找出显式谓词
			 * */
			if(pm.getRelatedRelation(h.word.name) != null)
			{
				res.add(h);
			}
		}
		return res;
	}
}
