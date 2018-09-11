/**
*Class represents a directory in a file system
*
*@author Dan Martineau
*@version 2.0
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
	private ArrayList<GenFile> files;		//list of all files within directory
	private ArrayList<Directory> sub;		//list of all subdirectories
	private int count;								//number of files/subdirectories within directory

	/*CONSTANTS*/
	private final char DELIM = '|';			//delimiter for toString
	private final String INDENT = "     "; 	//indent using spaces

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
		files = new ArrayList<GenFile>();
		sub = new ArrayList<Directory>();

		int index = 0;		//number assocaited with each file/subdirectory
		int subCount = 0;	//running total of the number of subdirectories

		//temporary holder for directories
		Directory[] dirs = new Directory[count];

		//add files to files list
		for(int i = 0; i < count; i++)
		{
			if(!FileCMD.isDir(currentContents[i]))
			{
				files.add(new GenFile(currentContents[i], ("" + (index + Integer.parseInt(startIndex)))));

				index++;
			}
		}

		//add directories into holder array
		for(int i = 0; i < count; i++)
		{
			if(FileCMD.isDir(currentContents[i]))
			{
				//add directory
				dirs[subCount] = new Directory(currentContents[i], ("" + (index + Integer.parseInt(startIndex))), ("" + (index + Integer.parseInt(startIndex) + 1)));
				//increase indicies based on how many were taken in the new directory
				index += dirs[subCount].getCount();

				index++;
				subCount++;
			}
		}

		//add holder array contents to sub
		for(int i = 0; i < subCount; i++)
		{
			if(dirs[i] != null)
				sub.add(dirs[i]);
		}
	}

	/**
	*Constructor--add directory and contents with default values
	*@param canonical path (as a string) of directory
	*@param name of directory
	*@param ArrayList of subdirectories
	*@param ArrayList of files
	*@param array of permissions (update, delete, read)
	*/
	public Directory(String path, String name, ArrayList<Directory> sub, ArrayList<GenFile> files, boolean[] permissions)
	{
		//startIndex is a legacy element pretaining to id. It is left in for compatibility/future use
		String startIndex = "1";

		canonicalPath = path;
		canUpdate = permissions[0];
		canDel = permissions[1];
		canRead = permissions[2];
		id = "0";
		this.name = name;

		if(files == null)
			this.files = new ArrayList<GenFile>();
		else
			this.files = files;

		if(sub == null)
			this.sub = new ArrayList<Directory>();
		else
			this.sub = sub;

		count = this.files.size() + this.sub.size();
	}

	/**
	*Constructor--add directory and contents from directory string
	*@param directory string
	*/
	public Directory(String dirStr)
	{
		//initialize contents
		files = new ArrayList<GenFile>();
		sub = new ArrayList<Directory>();

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
		name = attributes[6];
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
	*Adds a GenFile object to files list
	*@param a file to be added
	*/
	protected void addFile(GenFile file)
	{
		files.add(file);
		count++;
	}

	/**
	*Adds a Directory object to sub list
	*@param a directory to be added
	*/
	protected void addDir(Directory dir)
	{
		sub.add(dir);
		count++;
	}

	/**
	*Deletes a GenFile object from files list
	*@param path of file
	*/
	protected void remFile(String path)
	{
		for(int i =0; i < files.size(); i++)
		{
			if(files.get(i).getPath().equals(path))
			{
				files.remove(i);
				count--;
			}
		}
	}

	/**
	*Removes a Directory object from sub list
	*@param a directory to be removed
	*/
	protected void remDir(String path)
	{
		for(int i =0; i < sub.size(); i++)
		{
			if(sub.get(i).getPath().equals(path))
			{
				sub.remove(i);
				count--;
			}
		}
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

	/**
	*Returns the path of the directory
	*@return canonicalPath
	*/
	public String getPath()
	{
		return canonicalPath;
	}

	/**
	*Returns the list of subdirectories
	*@return sub
	*/
	public ArrayList<Directory> getSubs()
	{
		return sub;
	}

	/**
	*Returns the list of files
	*@return sub
	*/
	public ArrayList<GenFile> getFiles()
	{
		return files;
	}

	/*****************************************/

	/**
	*Decodes a directory string: Instantiates all files/subdirectories and returns directory attributes in a String[]
	*@param file String
	*@return attributes
	*/
	private String[] decodeStr(String dirStr)
	{
		int end = 0;							//index of next delimiter
		int beg = nextDelim(dirStr, '(') + 1;	//index of beginning of current substring
		String[] attributes = new String[7];	//holds attributes

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
		attributes[6] = dirStr.substring(beg, end);
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
		beg = end;


		//now Instantiate all files/subdirectories
		while(beg < dirStr.length())
		{
			//add a next file to contents
			if(dirStr.charAt(beg) == '<')
			{
				end = nextDelim(dirStr.substring(beg), '>') + beg;
				files.add(new GenFile(dirStr.substring(beg, end)));
				//Prin.tln("Adding: " + dirStr.substring(beg, end) + " in " + getDirName() + ".\n\n");
				beg = end + 1;
			}
			//add next directory to contents
			else if(dirStr.charAt(beg) == '[')
			{
				end = nextDelim(dirStr.substring(beg), ']') + beg;
				
				//account for sub directories
				if(dirStr.substring(beg, end).contains("["))
				{
					//find the beginnings of all subdirectories, stop when first end if found
					int dirBeg = 1;
					int dirEnd = 1;
					int count = beg + 1;
					boolean currIsEnd = false;
					while(!currIsEnd)
					{
						if(dirStr.charAt(count) == '[')
							dirBeg++;
						else if(dirStr.charAt(count) == ']')
						{
							currIsEnd = true;
							end = count;
						}
						
						count++;
					}
					
					//continue to incriment end until the number of subdirectory ends match number of beginnings
					while(dirBeg != dirEnd)
					{
						if(dirStr.charAt(end) == ']')
						{
							dirEnd++;
							end++;
						}
					}
				}
				
				//recursivley add directory and all subdirectories
				//Prin.tln("Adding: " + dirStr.substring(beg, end) + " in " + name + ".\n\n");
				sub.add(new Directory(dirStr.substring(beg, end)));
				beg = end + 1;
			}
			
			//incriment beg by one if neither "<" nor "[" were found
			else
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
	private int nextDelim(String str, char delimiter)
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
		String tempStr = "";


		for(int i = 0; i < files.size(); i++)
		{
			tempStr = files.get(i).toString();

			for(int j = 0; j < tempStr.length(); j++)
			{
				if(tempStr.charAt(j) == '\n')
					tempStr = tempStr.substring(0, j+1) + INDENT + tempStr.substring(j+1);
			}

			//add to string
			str += (INDENT + tempStr + "\n");
		}

		for(int i = 0; i < sub.size(); i++)
		{
			//make sure all lines of every string are indented
			tempStr = sub.get(i).toString();

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