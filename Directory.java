/**
*Class represents a directory in a file system
*
*@author Dan Martineau
*@version 1.0
*/


public class Directory
{
	/*FIELDS*/
	String canonicalPath;		//canonical path of a directory
	
	/**
	*Constructor
	*@param canonical path (as a string) of directory
	*/
	public Directory(String path)
	{
		canonicalPath = path;
	}
}