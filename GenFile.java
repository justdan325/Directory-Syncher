/**
*Class represents a generic file and its attributes that pretain to File Syncher program
*
*@author Dan Martineau
*@version 2.1
*/

import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenFile
{
	/*FIELDS*/
	private String canonicalPath;		//canonical path of file
	private String modStamp;			//holds time stamp of last modiifiication time
	private FileTime lastMod;			//holds most recent modification time
	private boolean canUpdate;			//true if the file can be updated
	private boolean canDel;				//true if the file can be deleted
	private boolean canRead;			//true if file can be read (and thus copied)
	private String id;					//file id code
	private String name;				//name and extension of the file
	
	/*CONSTANTS*/
	private final char DELIM = '|';	//delimiter for file strings
	
	/**
	*Constructor--Default all permissions to false
	*@param canonical path of file as a String
	*@param id of file
	*/
	public GenFile(String path, String newID)
	{
		canonicalPath = path;
		id = newID;
		canUpdate = false;
		canDel = false;
		canRead = false;
		refreshRecord();
		name = FileCMD.getName(path);
	}
	
	/**
	*Constructor--Re-instantiates a GenFIle by passing in a toString String
	*@param toString String--file attributes
	*/
	public GenFile(String fileStr)
	{
		//decode the string and fetch attributes
		String[] attributes = decodeStr(fileStr);
		
		//legacy--kept for compatibility/future use
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
		
		modStamp = attributes[6];
		name = attributes[5];
	}
	
	/**
	*Constructor--Allow permissions to be set initially
	*@param canonical path of file as a String
	*@param update permissions
	*@param delete permissions
	*@param read permissions
	*/
	public GenFile(String path, boolean up, boolean del, boolean re)
	{
		canonicalPath = path;
		
		//legacy--kept for compatibility/future use
		id = "0";
		
		canUpdate = up;
		canDel = del;
		canRead = re;
		refreshRecord();
		this.name = FileCMD.getName(path);
	}

	/**
	*Refreshes file attributes
	*@return true if attributes were able to be refreshed
	*/
	protected boolean refreshRecord()
	{
		boolean refreshed = false;
		boolean exists = false;

		//ensure file still exists
		exists = FileCMD.existFile(canonicalPath);

		if(exists)
		{
			//update last modification time
			lastMod = FileCMD.getFileTime(canonicalPath);
			//update time stamp
			modStamp = FileCMD.getModStamp(canonicalPath);

			//make sure that flag is true since things have been refreshed
			refreshed = true;
		}

		return refreshed;
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

	/*ACCESSORS*/

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
	*Returns FileTime
	*@return lastMod
	*/
	public FileTime getFileTime()
	{
		return lastMod;
	}
	
	/**
	*Returns time stamp of last mod
	*@return modStamp
	*/
	public String getModStamp()
	{
		return modStamp;
	}
	
	/**
	*Returns file name
	*@return name
	*/
	public String getFileName()
	{
		return name;
	}
	
	/**
	*Returns path of file
	*@return canonicalPath
	*/
	public String getPath()
	{
		return canonicalPath;
	}
	
	/**
	*Returns true if the file exists
	*@return true/false
	*/
	public boolean exist()
	{
		return FileCMD.existFile(canonicalPath);
	}
	
	/***************************/
	
	/**
	*Compares the mod stamp saved locally with the file's actual mod stamp
	*@return 1 is newer, 0 if equal, -1 if older
	*/
	public int current() 
	{
		//2018-09-06T04:40:07.185208Z
		
		String newStamp = FileCMD.getModStamp(canonicalPath);
		newStamp = newStamp.substring(0,10) + " " + newStamp.substring(11,19);
		
		String oldStamp = modStamp;
		oldStamp = oldStamp.substring(0,10) + " " + oldStamp.substring(11,19);
		
		SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date dateOne = null;
		Date dateTwo = null;
		
		try
		{
			dateOne = s1.parse(newStamp);
			dateTwo = s1.parse(oldStamp);
		}
		catch(NullPointerException e)
		{
			Prin.tln("One of the dates was null, you moron!\n" + Prin.getStackTrace(e));
		}
		catch(Exception f)
		{
			Prin.tln(Prin.getStackTrace(f));
		}
	
		return dateTwo.compareTo(dateOne);
	}
	
	/**
	*Decodes a file string and returns file attributes in a String[]
	*@param file String
	*@return attributes
	*/
	private String[] decodeStr(String fileStr)
	{	
		int end = 2;							//index of next delimiter
		int beg = 1;							//index of beginning of current substring
		String[] attributes = new String[7];	//holds attributes
		
		//get id
		end = nextDelim(fileStr);
		attributes[0] = fileStr.substring(beg, end);
		beg = end + 1;
		
		//get path
		end += nextDelim(fileStr.substring(beg)) + 1;
		attributes[1] = fileStr.substring(beg, end);
		beg = end + 1;
		
		//skip name
		end += nextDelim(fileStr.substring(beg)) + 1;
		attributes[5] = fileStr.substring(beg, end);
		beg = end + 1;
		
		//get mod stamp
		end += nextDelim(fileStr.substring(beg)) + 1;
		attributes[6] =  fileStr.substring(beg, end);
		beg = end + 1;
	
		//get update permissions
		end += nextDelim(fileStr.substring(beg)) + 1;
		attributes[2] = fileStr.substring(beg, end);
		beg = end + 1;
		
		//get delete permissions
		end += nextDelim(fileStr.substring(beg)) + 1;
		attributes[3] = fileStr.substring(beg, end);
		beg = end + 1;
		
		//get read permissions
		end += nextDelim(fileStr.substring(beg)) + 1;
		attributes[4] = fileStr.substring(beg);
		
		return attributes;
	}
	
	/**
	*Helper method for decodeStr--finds the next delimiter in a file string
	*@param substring of file string
	*@return index of next delimiter or -1 if there is none
	*/
	private int nextDelim(String str)
	{
		int index = -1;
		
		//find next delimiter
		for(int i = 0; i < str.length(); i++)
		{
			//if current char is delimiter, set the index and end loop
			if(str.charAt(i) == DELIM)
			{
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	/**
	*ToString Method--Returns String of file attributes
	*@return String
	*/
	public String toString()
	{
		String str = ("<" + id + DELIM + canonicalPath + DELIM + name + DELIM + modStamp + DELIM + canUpdate + DELIM + canDel + DELIM + canRead + ">");
		
		return str;
	}
}