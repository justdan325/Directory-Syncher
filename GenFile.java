/**
*Class represents a generic file and its attributes that pretain to File Syncher program
*
*@author Dan Martineau
*@version 1.0
*/

public class GenFile
{
	/*FIELDS*/
	String canonicalPath;		//canonical path of file
	
	
	
	/**
	*Constructor
	*@param canonical path of file as a String
	*/
	public GenFile(String path)
	{
		canonicalPath = path;
	}
}