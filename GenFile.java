/**
*Class represents a generic file and its attributes that pretain to File Syncher program
*
*@author Dan Martineau
*@version 1.2
*/

import java.nio.file.attribute.FileTime;

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
	}
	
	/**
	*Constructor--Allow permissions to be set initially
	*@param canonical path of file as a String
	*@param id of file
	*@param update permissions
	*@param delete permissions
	*@param read permissions
	*/
	public GenFile(String path, String newID, boolean up, boolean del, boolean re)
	{
		canonicalPath = path;
		id = newID;
		canUpdate = up;
		canDel = del;
		canRead = re;
		refreshRecord();
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
}