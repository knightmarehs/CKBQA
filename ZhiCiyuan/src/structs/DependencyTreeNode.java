package structs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;  
import java.util.Stack;

import nlp.DependencyTreeCore;
import edu.fudan.nlp.parser.dep.DependencyTree;

public class DependencyTreeNode implements Cloneable
{
	public Item word = null;
	public String dep_father2child = null;	// edge father <-- child
	public String posTag = null; // posTag
	
	public DependencyTreeNode father = null;
	public ArrayList<DependencyTreeNode> childrenList = null;
	
	public int levelInTree = -1;
	
	public DependencyTreeNode clone()
	{
		DependencyTreeNode ret = null;
		try 
		{
			ret = (DependencyTreeNode)super.clone();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		ret.word = this.word.clone();
		ret.dep_father2child = this.dep_father2child;
		ret.posTag = this.posTag;
		ret.levelInTree = this.levelInTree;
		//ret.childrenList = (ArrayList<DependencyTreeNode>) this.childrenList.clone();
		ret.childrenList = new ArrayList<DependencyTreeNode>();
		for(DependencyTreeNode dtn: this.childrenList)
		{
			ret.childrenList.add(dtn.clone());
		}
		if(this.father != null)
			ret.father = this.father;//这里没有完全深度clone，否则会提示stackOverflow
		
		return ret;
	}
	/**
	 * The constructor for knowing its father
	 * 
	 * @param w
	 * @param dep_father2child
	 * @param father
	 */
	public DependencyTreeNode(Item w, String dep_father2child, DependencyTreeNode father) 
	{
		word = w;
		this.dep_father2child = dep_father2child;
		this.father = father;
		this.childrenList = new ArrayList<DependencyTreeNode>();
		
		if(father==null) levelInTree = 0;
		else levelInTree = father.levelInTree+1;
	}

	/**
	 * The constructor for not knowing the father
	 * 
	 * @param word
	 */
	public DependencyTreeNode(Item w,String p)
	{
		this.word = w;
		this.posTag = p;
		this.childrenList = new ArrayList<DependencyTreeNode>();
	}
	
	public void sortChildrenList () {
		childrenList.trimToSize();
		Collections.sort(childrenList, new DependencyTreeNodeComparator());
	}
	
	@Override
	public String toString(){
		return word.name + "-" + word.type + "(" + dep_father2child + ")[" + word.position + "]";
	}
	
	public static void sortArrayList(ArrayList<DependencyTreeNode> list) {
		Collections.sort(list, new DependencyTreeNodeComparator());
	}
	
	public DependencyTreeNode containDependencyWithChildren (String dep) {
		for (DependencyTreeNode son : childrenList) {
			if (son.dep_father2child.equals(dep)) return son;
		}
		return null;
	}

	/**
		这里写遍历、搜索相关的函数
	 */

}

class DependencyTreeNodeComparator implements Comparator<DependencyTreeNode> {
  
    public int compare(DependencyTreeNode n1, DependencyTreeNode n2) { 
    	return n1.word.position - n2.word.position;
    }  
  
}  
