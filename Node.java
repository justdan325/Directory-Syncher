/**
*Class represents a node for a synchrc object
*Nodes are Immutable
*
*@author Dan Martineau
*@version 1.0
*/

public class Node
{
	/*FIELDS*/
	String path;			//String for canonical path of Node
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
}