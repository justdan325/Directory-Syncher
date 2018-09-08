/**
*Class represents a directory in a file system
*
*@author Dan Martineau
*@version 1.2
*/

import java.util.ArrayList;

public class Directory
{
	/*FIELDS*/
	private String canonicalPath;					//canonical path of a directory
	private boolean canUpdate;						//true if the directory can be updated
	private boolean canDel;							//true if the directory can be deleted
	private boolean canRead;						//true if directory can be read (and thus copied)
	private String id;								//directory id code
	private String name;							//name of the directory
	private static ArrayList<Object> contents;		//list of all files/subdirectories within directory
	private int count;								//number of files/subdirectories within directory
	
	/*CONSTANTS*/
	private static final char DELIM = '|';			//delimiter for toString
	private static final String INDENT = "     ";	//indent using spaces
	
	/**
	*Constructor--add directory and contents with default values
	*@param canonical path (as a string) of directory
	*@param Directory ID
	*@param beginning index to assign file numbers
	*/
	public Directory(String path, String assignedID, String startIndex)
	{
		String[] currentContents = FileCMD.listAll(path);
		canonicalPath = path;
		canUpdate = false;
		canDel = false;
		canRead = false;
		id = assignedID;
		name = FileCMD.getName(path);
		count = currentContents.length;
		contents = new ArrayList<Object>(count);
		
		//add files to contents
		int index = 0;
		for(int i = 0; i < count; i++)
		{
			if(FileCMD.isDir(currentContents[i]))
			{
				//create directory
				Directory temp = new Directory(currentContents[i], ("" + (index + Integer.parseInt(startIndex))), ("" + (index + Integer.parseInt(startIndex) + 1)));
				//add directory
				contents.add(temp);
				//increase indicies based on how many were taken in the new directory
				index += temp.getCount();
			}
			else
			{
				contents.add(new GenFile(currentContents[i], ("" + (index + Integer.parseInt(startIndex)))));
			}
			
			index++;
		}
		
		contents.trimToSize();
	}
	
	/**
	*Constructor--add directory and contents from directory string
	*@param directory string
	*/
	public Directory(String dirStr)
	{
		//initialize contents
		contents = new ArrayList<Object>();
		
		//get attributes
		String[] attributes = decodeStr(dirStr);
		
		//assign attributes
		id = attributes[0];
		canonicalPath = attributes[1];
		
		//set permissions
		if(attributes[2].equals("true"))
			canUpdate = true;
		else
			canUpdate = false;
		
		if(attributes[3].equals("true"))
			canDel = true;
		else
			canDel = false;
		
		if(attributes[4].equals("true"))
			canRead = true;
		else
			canRead = false;
		
		count = Integer.parseInt(attributes[5]);
		name = FileCMD.getName(attributes[1]);
	}
	
	/*MUTATORS*/

	/**
	*Allow/Disallow file updates
	*@param true or false
	*/
	protected void allowUpdate(boolean allow)
	{
		canUpdate = allow;
	}

	/**
	*Allow/Disallow file deletions
	*@param true or false
	*/
	protected void allowDel(boolean allow)
	{
		canDel = allow;
	}

	/**
	*Allow/Disallow file reading
	*@param true or false
	*/
	protected void allowRead(boolean allow)
	{
		canRead = allow;
	}
	
	/**
	*Adds new file/subdir to directory
	*@param new file/subdir
	*/
	protected void addToDir(Object obj)
	{
		contents.add(obj);
		count++;
	}
	
	/**
	*Removes file/subdir from contents by name
	*@param name of file/subdir
	*/
	protected void remByName(String name)
	{
		for(int i = 0; i < contents.size(); i++)
		{}
	}
	
	/**
	*Removes file/subdir from contents by path
	*@param path of file/subdir
	*/
	protected void remByPath(String path)
	{
		for(int i = 0; i < contents.size(); i++)
		{}
	}
	
	/**
	*Removes file/subdir from contents by number
	*@param number of file/subdir
	*/
	protected void remByNum(String num)
	{
		for(int i = 0; i < contents.size(); i++)
		{}
	}

	/*ACCESSORS*/
	
	/**
	*Returns the number of immediate files and subdirectories
	*@return count
	*/
	public int getCount()
	{
		return count;
	}

	/**
	*Returns true if file can be updated
	*@return canUpdate
	*/
	public boolean canUpdate()
	{
		return canUpdate;
	}

	/**
	*Returns true if file can be deleted
	*@return canUpdate
	*/
	public boolean canDel()
	{
		return canDel;
	}

	/**
	*Returns true if file can be read
	*@return canUpdate
	*/
	public boolean canRead()
	{
		return canRead;
	}

	/**
	*Returns file id
	*@return id
	*/
	public String getFileID()
	{
		return id;
	}
	
	/**
	*Returns directory name
	*@return name
	*/
	public String getDirName()
	{
		return name;
	}
	
	/*****************************************/
	
	/**
	*Decodes a directory string: Instantiates all files/subdirectories and returns directory attributes in a String[]
	*@param file String
	*@return attributes
	*/
	private static String[] decodeStr(String dirStr)
	{
		int end = 0;							//index of next delimiter
		int beg = nextDelim(dirStr, '(') + 1;	//index of beginning of current substring
		String[] attributes = new String[6];	//holds attributes
		
		//First get directory attributes
		
		//get id
		end = nextDelim(dirStr, DELIM);
		attributes[0] = dirStr.substring(beg, end);
		beg = end + 1;
		
		//get path
		end += nextDelim(dirStr.substring(beg), DELIM) + 1;
		attributes[1] = dirStr.substring(beg, end);
		beg = end + 1;
		
		//skip name
		end += nextDelim(dirStr.substring(beg), DELIM) + 1;
		beg = end + 1;
	
		//get update permissions
		end += nextDelim(dirStr.substring(beg), DELIM) + 1;
		attributes[2] = dirStr.substring(beg, end);
		beg = end + 1;
		
		//get delete permissions
		end += nextDelim(dirStr.substring(beg), DELIM) + 1;
		attributes[3] = dirStr.substring(beg, end);
		beg = end + 1;
		
		//get read permissions
		end += nextDelim(dirStr.substring(beg), DELIM) + 1;
		attributes[4] = dirStr.substring(beg, end);
		beg = end + 1;
		
		//get count
		end += nextDelim(dirStr.substring(beg), ')') + 1;
		attributes[5] = dirStr.substring(beg, end);
		//beg = end;
		
		
		//now Instantiate all files/subdirectories
		while(beg < dirStr.length())
		{
			//add a next file to contents
			if(dirStr.charAt(beg) == '<')
			{
				end = nextDelim(dirStr.substring(beg), '>') + beg;
				contents.add(new GenFile(dirStr.substring(beg, end)));
				beg = end + 1;
			}
			//add next directory to contents
			else if(dirStr.charAt(beg) == '[')
			{
				end = nextDelim(dirStr.substring(beg), ']') + beg;
				contents.add(new Directory(dirStr.substring(beg, end)));
				beg = end + 1;
			}
			//else if(dirStr.charAt(beg) == ']')
				//break;
			
			beg++;
		}
		
		return attributes;
	}
	
	/**
	*Helper method for decodeStr--finds the next delimiter in a string
	*@param substring of directory string
	*@param delimiter
	*@return index of next delimiter or -1 if there is none
	*/
	private static int nextDelim(String str, char delimiter)
	{
		int index = -1;
		
		//find next delimiter
		for(int i = 0; i < str.length(); i++)
		{
			//if current char is delimiter, set the index and end loop
			if(str.charAt(i) == delimiter)
			{
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	/**
	*ToString Method--Returns String of directory contents
	*@return String
	*/
	public String toString()
	{
		//declare and add directory attributes to the 
		String str = ("[(" + id + DELIM + canonicalPath + DELIM + name + DELIM + canUpdate + DELIM + canDel + DELIM + canRead + DELIM + count + ")\n");
		//holder for file strings
		Object hold = null;
		String tempStr = "";
		
		for(int i = 0; i < contents.size(); i++)
		{
			//make sure all lines of every string are indented
			hold = contents.get(i);
			tempStr = hold.toString();
			for(int j = 0; j < tempStr.length(); j++)
			{
				if(tempStr.charAt(j) == '\n')
					tempStr = tempStr.substring(0, j+1) + INDENT + tempStr.substring(j+1);
			}
			
			//add to string
			str += (INDENT + tempStr + "\n");
		}
		
		str += "]\n";
		
		return str;
	}
}