import java.util.Scanner;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;

/**
*This is a class that abstracts file io to suit my needs
*
*@author Dan Martineau
*@version 1.4
*/

public class FileCMD
{
	/**
	*Parse a Path from a string
	*@param the path name as a String literal
	*@return the actual path
	*/
	public static Path strToPath(String strPath)
	{
		Path path = Paths.get(strPath);
		
		return path;
	}
	
	/**
	Read contents of a file into string
	@param file name
	@return file contents
	*/
	public static String readFile(String fileName)
	{
		File file;
		Scanner inputFile;


		file = new File(fileName);
		if(!file.exists())
			return "";

		try
		{
			inputFile = new Scanner(file);
		}

		catch(FileNotFoundException e)
		{
			System.out.println("\nFileNotFoundException when reading " + fileName + "\n" + Prin.getStackTrace(e));
			return "";
		}

		int count = 0;

		String str = "";

		while(inputFile.hasNext())
		{
			if(count != 0)
				str += '\n';
			str += inputFile.nextLine();
			count++;
		}
		inputFile.close();
		return str;
	}

	/**
	Write a sting to a file
	@param string to be written
	@param destination file
	*/
	public static void writeFile(String fileStr, String destination)
	{
		final long LENGTH = fileStr.length();
		File file = new File(destination);
		PrintWriter outputFile;

		try
		{
			outputFile = new PrintWriter(file);
		}

		catch(FileNotFoundException e)
		{
			return;
		}

		for(int i = 0; i < LENGTH; i++)
		{
			if(fileStr.substring(i,i+1).equals("\n"))
				outputFile.println();
			else if(fileStr.substring(i,i+1).equals("\t"))
				outputFile.write("\t");
			else
				outputFile.append(fileStr.charAt(i));
		}
		outputFile.close();
	}

	/**
	*Delete a file at the given path if it exists
	*@param path of file to delete
	*@return true if successful
	*/
	public static boolean deleteFile(String path)
	{		
		boolean deleted = false;
		
		try{deleted = Files.deleteIfExists(strToPath(path));}
		catch(SecurityException e){Prin.tln("Security exception was thrown when attempting to delete: " + path + "\n" + Prin.getStackTrace(e));}
		catch(IOException f){Prin.tln("There was an IOException when attempting to delete: " + path);}
		
		return deleted;
	}
	
	/**
	*Find out if a file exists--does not follow symbolic links
	*@param path to file
	*@return true if exists
	*/
	public static boolean existFile(String path)
	{
		boolean exists = Files.exists(strToPath(path), LinkOption.NOFOLLOW_LINKS);
		
		return exists;
	}
	
	/**
	*Copy a file and its attributes
	*@param file source location
	*@param file destination
	*@param true to overwrite original file, false to skip copying
	*@return true if successful
	*/
	public static boolean copyFile(String source, String target, boolean overwrite)
	{
		boolean copied = false;
		
		//The path returned by the copy method from Files class
		Path newPath = null;
		
		//attempt to copy with attributes
		try{newPath = Files.copy(strToPath(source), strToPath(target), StandardCopyOption.COPY_ATTRIBUTES);}
		catch(FileAlreadyExistsException e){if(overwrite) copied = copyReplace(source, target); /*overwrite file if the user desires it.*/}
		catch(SecurityException f){Prin.tln("Security exception was thrown when attempting to copy.\n" + Prin.getStackTrace(f));}
		catch(Exception g){Prin.tln("There's been an exception: \n" + Prin.getStackTrace(g));}
		
		
		//make sure the target paths are the same to verify copy process
		if(newPath != null && newPath.equals(strToPath(target)))
			copied = true;
		
		return copied;
	}
	
	/**
	*Copies a file and overwrites original if the file is already in directory--fallback to copyFile()
	*@param file source location
	*@param file destination
	*@return true if successful
	*/
	private static boolean copyReplace(String source, String target)
	{
		boolean copied = false;
		
		//The path returned by the copy method from Files class
		Path newPath = null;
		
		try{newPath = Files.copy(strToPath(source), strToPath(target), StandardCopyOption.REPLACE_EXISTING);}
		catch(Exception e){Prin.tln("There's been an exception: \n" + Prin.getStackTrace(e));}
		
		//make sure the target paths are the same to verify copy process
		if(newPath != null && newPath.equals(strToPath(target)))
			copied = true;
		
		return copied;
	}
	
	/**
	*Find out what date a file was modified on
	*@param file path
	*@return String containing datestamp or null if file does not exist/is not accessible.
	*/
	public static String getModTime(String path)
	{
		//date stamp from file time
		String dateStamp = null;
		//file time of last modification
		FileTime lastMod = null;
		
		try{lastMod = Files.getLastModifiedTime(strToPath(path), LinkOption.NOFOLLOW_LINKS);}
		catch(SecurityException e){Prin.tln("Security Exception when accessing mod date of: " + path + "\n" + Prin.getStackTrace(e));}
		catch(IOException f){Prin.tln("There was an IOException when accessing mod date of: " + path + "\n" + Prin.getStackTrace(f));}
		
		if(lastMod != null)
			dateStamp = lastMod.toString();
		//if lastMod is null, then the file was not found/was not accessible. 
		else
			dateStamp = null;
		
		return dateStamp;
	}
	
	
	/**
	*Compares two files to see which was modified last
	*@param path file 1
	*@param path file 2
	*@return int : -1 if error, 0 if same, 1 if first, 2 if second
	*/
	public static int compModTime(String file1, String file2)
	{
		//file time of last modification
		FileTime file1Mod = null;
		FileTime file2Mod = null;
		//whichever is the newest
		int comparison = 0;
		//result for user--see Javadoc
		int result = -1;
		
		try{file1Mod = Files.getLastModifiedTime(strToPath(file1), LinkOption.NOFOLLOW_LINKS);}
		catch(SecurityException e){Prin.tln("Security Exception when accessing mod date of: " + file1 + "\n" + Prin.getStackTrace(e));}
		catch(IOException f){Prin.tln("There was an IOException when accessing mod date of: " + file1 + "\n" + Prin.getStackTrace(f));}
		
		try{file2Mod = Files.getLastModifiedTime(strToPath(file2), LinkOption.NOFOLLOW_LINKS);}
		catch(SecurityException e){Prin.tln("Security Exception when accessing mod date of: " + file2 + "\n" + Prin.getStackTrace(e));}
		catch(IOException f){Prin.tln("There was an IOException when accessing mod date of: " + file2 + "\n" + Prin.getStackTrace(f));}
		
		//ensure that nothing went wrong in getting file times
		if(file1Mod != null && file2Mod != null)
		{
			comparison = file1Mod.compareTo(file2Mod);
			
			//if times are equal
			if(comparison==0)
				result = 0;
			//if file2 is newer
			else if(comparison < 0)
				result = 2;
			//if file1 is newer
			else if(comparison > 0)
				result = 1;
		}
		
		return result;
	}
	
	/**
	*Tests to see if a file is a directory
	*@param path to file/directory
	*@return true if directory
	*/
	public static boolean isDir(String path)
	{
		boolean isDir = false;
		
		try{isDir = Files.isDirectory(strToPath(path), LinkOption.NOFOLLOW_LINKS);}
		catch(SecurityException e){Prin.tln("Security Exception when testing for directory: " + path + "\n" + Prin.getStackTrace(e));}
		
		return isDir;
	}
	
	/**
	*Makes new directories
	*@param path of new directories
	*@return true if successful
	*/
	public static boolean mkdirs(String path)
	{
		//if directories successfully made
		boolean made = false;
		
		//create new directory file--does not exist yet
		File newDir = new File(path);
		
		//make the directory/directories
		try{made = newDir.mkdirs();}
		catch(Exception e){Prin.tln("There was an exception trying to create new directories: " + path + "\n" + Prin.getStackTrace(e));}
		
		return made;
	}
	
	/**
	*Lists the files and directories (paths) for a given path
	*@param path of directory
	*@return String[] of file & directory names
	*/
	public static String[] listAll(String path)
	{
		//get File object for directory
		File dir = new File(path);
		//list of files
		File[] files = dir.listFiles();
		//File names
		String[] fileNames = new String[files.length];
		
		//put file names into array
		for(int i = 0; i < files.length; i++)
			fileNames[i] = files[i].toString();
		
		return fileNames;
	}
	
	
	/**
	*Lists the files (paths) in a given directory
	*@param path of directory
	*@return String[] of file names
	*/
	public static String[] listFiles(String path)
	{
		//get File object for directory
		File dir = new File(path);
		//list of files
		File[] files = dir.listFiles();
		//temp holder
		ArrayList<String> temp = new ArrayList<String>();
		//Just File names
		String[] fileNames;
		//holder for a name
		String hold = "";
		
		//put file names into array list
		for(int i = 0; i < files.length; i++)
			temp.add(i,  files[i].toString());
		
		//weed out directories
		for(int i = 0; i < temp.size(); i++)
		{
			hold = temp.get(i);
			if(isDir(hold))
			{
				temp.remove(i);
				temp.trimToSize();
				i--;
			}
		}
		
		//place names into final list
		fileNames = new String[temp.size()];
		for(int i = 0; i < temp.size(); i++)
			fileNames[i] = temp.get(i);
		
		//release temp
		temp = null;
		
		return fileNames;
	}
	
	/**
	*Lists the subdirectories (paths) in a given  directory
	*@param path of directory
	*@return String[] of subdirectory names
	*/
	public static String[] listDirs(String path)
	{
		//get File object for directory
		File dir = new File(path);
		//list of directories
		File[] files = dir.listFiles();
		//temp holder
		ArrayList<String> temp = new ArrayList<String>();
		//Just File names
		String[] dirNames;
		//holder for a name
		String hold = "";
		
		//put file names into array list
		for(int i = 0; i < files.length; i++)
			temp.add(i,  files[i].toString());
		
		//weed out directories
		for(int i = 0; i < temp.size(); i++)
		{
			hold = temp.get(i);
			if(!isDir(hold))
			{
				temp.remove(i);
				temp.trimToSize();
				i--;
			}
		}
		
		//place names into final list
		dirNames = new String[temp.size()];
		for(int i = 0; i < temp.size(); i++)
			dirNames[i] = temp.get(i);
		
		//release temp
		temp = null;
		
		return dirNames;
	}
	
	/**
	*Returnhs the path of a file
	*@param file name
	*@return String canonical path of the file/null if exception occurs
	*/
	public static String getPath(String inputFile)
	{
		File file = new File(inputFile);
		String path = null;
		
		try{path = file.getCanonicalPath();}
		catch(SecurityException e){Prin.tln("There was a security exception when atempting to read the file path of : " + inputFile + "\n" + Prin.getStackTrace(e));}
		catch(IOException f){Prin.tln("There was an IOException when atempting to read the file path of : " + inputFile + "\n" + Prin.getStackTrace(f));}
		
		//remove second directory name if directory
		if(path != null && isDir(path))
			path = path.substring(0, (path.length() - inputFile.length()));
		
		Prin.tln("" + (path.length() - inputFile.length()));
		
		return path;
	}
}