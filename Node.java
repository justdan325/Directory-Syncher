/**
*Class represents a node for a synchrc object
*Nodes should form a binary search tree
*
*@author Dan Martineau
*@version 2.0
*/

public class Node
{
	/*FIELDS*/
	String path;			//String for canonical path of Node
	String name;			//Name of file from path
	Node left;				//Node to the left in the bin search tree
	Node right;			//Node to the right in the bin search tree
	boolean read;		//read permission
	boolean modify;		//modify permission
	boolean delete;		//delete permission
	
	/**
	*Constructor
	*@param String for canonical path of Node
	*@param read permission
	*@param modify permission
	*@param delete permission
	*/
	public Node(String path, boolean read, boolean modify, boolean delete)
	{
		this.path = path;
		this.read = read;
		this.modify = modify;
		this.delete = delete;
		
		name = FileCMD.getName(path);
		
		left = null;
		right = null;
	}
	
	/*ACCESSORS*/
	
	/**
	*Return path
	*@return String
	*/
	protected String getPath()
	{
		return path;
	}
	
	/**
	*Return name of file
	*@return String
	*/
	protected String getName()
	{
		return name;
	}
	
	/**
	*Return read permission
	*@return boolean
	*/
	protected boolean getRead()
	{
		return read;
	}
	
	/**
	*Return modify permission
	*@return boolean
	*/
	protected boolean getModify()
	{
		return modify;
	}
	
	/**
	*Return delete permission
	*@return boolean
	*/
	protected boolean getDelete()
	{
		return delete;
	}
	
	/**
	*Returns left child of this Node
	*@return Node
	*/
	protected Node getLeft()
	{
		return left;
	}
	
	/**
	*Returns right child of this Node
	*@return Node
	*/
	protected Node getRight()
	{
		return right;
	}
	
	/**
	*Searches for node by path and returns it
	*@param path of desired node
	*@return reference to node/null if doesn't exist
	*/
	protected Node getNode(String nodePath)
	{
		Node desired = null;	//holder for desired node to return
		Node curr;					//current node being examined
		String nodeName;		//name of file at given path
		int status;					//holder for value of comapreTo comparison
		
		nodeName = FileCMD.getName(nodePath);
		
		status = name.compareTo(nodeName);
		
		if(status == 0 && path.equals(nodePath))
		{
			desired = this;
		}
		else
		{
			if(status <= 0 && right != null)
			{
				if(right.getPath().equals(nodePath))
					desired = right;
				else
					desired = right.getNode(nodePath);
			}
			else if(left != null)
			{
				if(left.getPath().equals(nodePath))
					desired = left;
				else
					desired = left.getNode(nodePath);
			}
		}
		
		return desired;
	}
	
	/*MUTATORS*/
	
	/**
	*Recursive--Adds a node where appropriate in the bin search tree
	*Sorts nodes by alphabetical order of name
	*@param Node
	*/
	protected void addNode(Node toAdd)
	{
		int status;		//holder for value of comapreTo comparison
		
		status = name.compareTo(toAdd.getName());
		
		//if toAdd is after this node or equal to it
		if(status <= 0)
		{
			if(right == null)
				right = toAdd;
			else
				right.addNode(toAdd);
		}
		else
		{
			if(left == null)
				left = toAdd;
			else
				left.addNode(toAdd);
		}
	}
	
	/**************************/
	
	public String toString()
	{
		String str = "";
		
		str += read + "," + modify + "," + delete + " " + path + "\n";
		
		if(left != null)
			str += "     left : " + left.toString() + "\n";
		if(right != null)
			str += "     right: " + right.toString() + "\n";
		
		return str;
	}
}