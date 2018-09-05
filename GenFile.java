/**
*Class represents a generic file and its attributes that pretain to File Syncher program
*
*@author Dan Martineau
*@version 1.0
*/

import java.nio.file;

public class GenFile
{
	/*FIELDS*/
	String canonicalPath;		//canonical path of file
	String modStamp;			//holds time stamp of last modiifiication time
	FileTime lastMod;			//holds most recent modification time
	boolean canUpdate;		//true if the file can be updated
	boolean canDel;				//true if the file can be deleted
	boolean canRead;			//true if file can be read (and thus copied) 
	
	/**
	*Constructor
	*@param canonical path of file as a String
	*/
	public GenFile(String path)
	{
		canonicalPath = path;
	}
}