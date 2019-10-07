/**
*Class represents a node for a synchrc object
*Nodes should form a binary search tree
*
*@author Dan Martineau
*@version 2.2
*/

import java.io.*;
import java.util.ArrayList;

public class Node
{
	/*FIELDS*/
	private String path;			//String for canonical path of Node
	private String name;			//Name of file from path
	private Node left;				//Node to the left in the bin search tree
	private Node right;				//Node to the right in the bin search tree
	private boolean read;			//read permission
	private boolean modify;		//modify permission
	private boolean delete;		//delete permission
	
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
		
		left = null;
		right = null;
		
		//if path contains wildcards, it must be passed to addWildcardMatches to find matches to add
		if(path.contains("*"))
			addWildcardMatches(path, read, modify, delete);
		else
			name = FileCMD.getName(path);
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
		
		//if path contains wildcards, then toAdd must be the first match found. 
		//It will therefore be set as this Node
		if(path.contains("*"))
		{
			path    = toAdd.getPath();			
			name  = toAdd.getName();			
			left      = toAdd.getLeft();				
			right     = toAdd.getRight();			
			read    = toAdd.getRead();		
			modify = toAdd.getModify();		
			delete  = toAdd.getDelete();
		}
		else
		{
			status = name.compareTo(toAdd.getName());
			
			//deal with a duplicate Node
			if(status == 0 && path.equals(toAdd.getPath()))
			{
				assert status != 0: "Duplicate Node inserted: " + toAdd.getPath() + " " + toAdd.getName();
			}
			//if toAdd is after this node or equal to it
			else if(status < 0)
			{
				if(right == null)
				{
					right = toAdd;
				}
				else
					right.addNode(toAdd);
			}
			else if(status > 0)
			{
				if(left == null)
					left = toAdd;
				else
					left.addNode(toAdd);
			}
		}
	}
	
	/***************************/
	
	private void addWildcardMatches(String wild, boolean read, boolean modify, boolean delete)
	{
		addWildcardMatchesHelper(wild, wild, read, modify, delete);
	}
	
	private void addWildcardMatchesHelper(String wild, String orig, boolean read, boolean modify, boolean delete) 
	{
		String rootPath;
		String restOfWild;
		boolean recursive = false;
		String[] filesAndDirs;
		String[] subDirs;
		
		if(wild.contains("*"))
		{
			rootPath = wild.substring(0, wild.indexOf("*"));
			if(!FileCMD.existFile(rootPath) && rootPath.contains(File.separatorChar + ""))
				rootPath = rootPath.substring(0, rootPath.lastIndexOf(File.separatorChar + "")+1);
		}
		else
		{
			rootPath = wild;
			addNode(new Node(rootPath, read, modify, delete));
		}
		
		//Do not continue if the rootPath does not exist
		if(FileCMD.isDir(rootPath))
		{
			if(wild.charAt(rootPath.length()) == '*')
				restOfWild = wild.substring(rootPath.length());
			else
				restOfWild = wild.substring(wild.indexOf("*"));
			
			filesAndDirs = null;
			
			//NullPointerException here means that user does not have access to a subdirectory. The error will be given in Synchmodule, so ignore here.
			try{filesAndDirs = FileCMD.listAll(rootPath);}
			catch(NullPointerException e){return;}
			
			if(wild.length() > wild.indexOf("*")+1)
				recursive = wild.charAt(wild.indexOf("*")+1) == '*';
			
			//get all files and subdirs in rootPath
			//run them through match
			//if they match, make them new Nodes and add them
			for(int i = 0; i < filesAndDirs.length; i++)
			{
				if(match(orig, filesAndDirs[i]))
				{
					addNode(new Node(filesAndDirs[i], read, modify, delete));
				}
			}
			
			//if recursive, search through all subdirectories
			if(recursive || restOfWild.contains(File.separatorChar + ""))
			{
				subDirs = FileCMD.listDirs(rootPath);
				
				for(int i = 0; i < subDirs.length; i++)
				{
					wild = subDirs[i] + restOfWild;
					addWildcardMatchesHelper(wild, orig, read, modify, delete);
				}
			}
		}
	}
	
	private boolean match(String wild, String path)
	{
		ArrayList<Integer> locations = new ArrayList<Integer>();
		ArrayList<String> pieces = new ArrayList<String>();
		String subStr = wild;
		int index = -1;
		int prev = 0;
		int curr = -1;
		boolean match = false;
		
		if(wild.contains("*"))
		{
			while(wild.contains("**"))
			{
				index = wild.indexOf("**");
				wild = wild.substring(0,index+1) + wild.substring(index+2);
			}
			index = -1;
			
			//find locations of all wild cards
			while(index < wild.length()-1)
			{
				subStr = wild.substring(index+1);
				index = subStr.indexOf("*");
				
				if(index == -1)
					break;
				
				index = (wild.length() - subStr.length()) + index;
				
				locations.add(index);
			}
			
			//chop wild into pieces
			for(int i = 0; i < locations.size(); i++)
			{
				prev = curr+1;
				curr = locations.get(i);
				
				if(!wild.substring(prev, curr).equals(""))
					pieces.add(wild.substring(prev, curr));
			}
			//add last piece
			if(curr < wild.length()-1)
			{
				pieces.add(wild.substring(curr+1));
			}
			
			//see if all of the pieces are contained within the path in the correct order
			curr = 0;
			for(int i = 0; i < pieces.size(); i++)
			{
				prev = curr;
				curr = prev + pieces.get(i).length();
				
				match = false;

				while(curr <= path.length() && !match)
				{
					if(curr == path.length() || (i == pieces.size()-1 && wild.charAt(wild.length()-1) != '*'))
					{
						if(path.substring(prev).equals(pieces.get(i)))
							match = true;
						else
							match = false;
					}
					else
					{
						if(path.substring(prev, curr).equals(pieces.get(i)))
							match = true;
						else
							match = false;
					}
					
					prev++;
					curr++;
				}
				
				curr--;
				
				if(!match)
					break;
			}
		}
		else
		{
			match = wild.equals(path);
		}
		
		return match;
	}
	
	/**************************/
	
	public String toString()
	{
		String str = "";
		String hold = "";
		
		str += read + "," + modify + "," + delete + " " + path + "\n";
		
		if(left != null)
		{
			hold = "";
			hold += "     left : " + left.toString();

			for(int j = 0; j < hold.length(); j++)
			{
				if(hold.charAt(j) == '\n' && j+1 < hold.length())
					hold = hold.substring(0, j+1) + "     " + hold.substring(j+1);
			}

			//add to string
			str += ("     " + hold);
		}
		if(right != null)
		{
			hold = "";
			hold += "     right: " + right.toString() + "\n";

			for(int j = 0; j < hold.length(); j++)
			{
				if(hold.charAt(j) == '\n' && j+1 < hold.length())
					hold = hold.substring(0, j+1) + "     " + hold.substring(j+1);
			}

			//add to string
			str += ("     " + hold + "\n");
		}
		
		return str;
	}
}