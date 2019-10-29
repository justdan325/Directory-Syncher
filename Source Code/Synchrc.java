/**
*Class represents a synchrc file
*
*@author Dan Martineau
*@version 1.6
*/

import java.io.File;
import java.util.Scanner;

public class Synchrc
{
	/*FIELDS*/
	private String synchrcPath;	//path of synchrc file: IT IS ASSUMED THAT IT LIES IN UPPERMOST PARENT DIR
	private String pathToUpp;	//path to uppermost parent directory: DETERMINED BY synchrcPath!
	private Node root;			//the root node in the binary search tree
	private String log;			//log file for the synch job
	private boolean verbose;		//wether or not the program is running in verbose mode
	private boolean error;		//true if there is an error in the synchrc file
	
	/*CONSTANTS*/
	public static String DEFAULT_SYNCHRC = "synchrc";						//Name of default synchrc file
	private final static char RC_DELIM = ',';							//delimiter that the contextless synchrc file uses to seperate directories and files
	private final static String RC_ERROR_MESSAGE = "Line format is invalid:";	//display when there is an invalid line in a synchrc file
	
	/*REGULAR EXPRESSIONS*/
	//000 ParentDir,subdir,subdir,file.extension 	//represents how a line should be formatted in the synchrc file
	
	/**
	*Constructor
	*@param path to synchrc file
	*@param path to parent directory
	*@param String containing program log
	*@param flag for verbose mode
	*/
	public Synchrc(String synchrcPath, String parentDir, String log, boolean verbose)
	{
		this.log = log;
		this.verbose = verbose;
		
		//assert that the file is valid
		assert FileCMD.existFile(synchrcPath) : "File " + synchrcPath + " is not valid! This should be handeled outside of Synchrc.";
		
		this.synchrcPath = synchrcPath;	//add path
		root = null;
		
		determinePathToUpp(parentDir);
		decodeFile();
	}
	
	/**
	*Returns a Node based upon given path/null if non-existant
	*@param path of file
	*@return Node
	*/
	public Node getNode(String nodePath)
	{
		Node node = null;
		
		if(root != null)
		{
			if(nodePath.equals(root.getPath()))
				node = root;
			else
				node = root.getNode(nodePath);
		}
		
		return node;
	}
	
	/**
	*Returns the log for this job
	*@return log
	*/
	public String getLog()
	{
		return log;
	}
	
	/**
	*Returns true if there are errors in synchrc file
	*@return error
	*/
	public boolean getError()
	{
		return error;
	}
	
	/**
	*Returns the name of the actual file
	*@return synchrc name
	*/
	public String getName()
	{
		return FileCMD.getName(synchrcPath);
	}
	
	/**
	*Sets pathToUpp
	*@param parent directory path
	*/
	private void determinePathToUpp(String parentDir)
	{
		int count = 0;
		String hold = "";
		
		//set pathToUpp equal to parentDir minus the directory itself
		hold = FileCMD.getName(parentDir);
		pathToUpp = parentDir.substring(0, parentDir.length()-hold.length());
		
		//assert that the path exists
		assert FileCMD.existFile(pathToUpp) : pathToUpp + " does not exist! Prob a formatting error. See determinePathToUpp()";
	}
	
	/**
	*Itterates through file parsing lines
	*/
	private void decodeFile()
	{
		String contents = FileCMD.readFile(synchrcPath);
		Scanner fileScan = new Scanner(contents);
		
		fileScan.useDelimiter("\n");
		
	
		while(fileScan.hasNext())
		{
			try
			{
				decodeLine(fileScan.next());
			}
			catch(RuntimeException r)
			{
				if(r.getMessage().contains(RC_ERROR_MESSAGE))
				{
					if(verbose)
						Prin.err("\nERROR in " + FileCMD.getName(synchrcPath) + ": " + r.getMessage() + "\n");
					log += "\nERROR in " + synchrcPath + ": " + r.getMessage() + "\n";
					error = true;
				}
			}
		}
	}
	
	/**
	*Converts a line form the file to a Node and adds it to the tree
	*Assumes that line is valid
	*@param line 
	*/
	private void decodeLine(String line)
	{
		boolean read;
		boolean modify;
		boolean delete;
		String canonicalPath = "";
		
		//Reading short lines causes exceptions, and a short line should be ignored anyhow.
		if(line.length() > 1)
		{
			//remove new line char from line
			if(line.charAt(line.length()-1) == '\n')
				line = line.substring(line.length()-1);
			
			//read first bit
			if(line.charAt(0) == '0')
				read = false;
			else if(line.charAt(0) == '1')
				read = true;
			//if line begins with "#," it is a comment and should be overlooked
			//should also be overlooked if line is blank
			else if(line.charAt(0) == '#' || line.charAt(0) == ' ')
				return;
			else
				throw new RuntimeException(RC_ERROR_MESSAGE + " " + line);
			
			//read second bit
			if(line.charAt(1) == '0')
				modify = false;
			else if(line.charAt(1) == '1')
				modify = true;
			else
				throw new RuntimeException(RC_ERROR_MESSAGE + " " + line);
			
			//read third bit
			if(line.charAt(2) == '0')
				delete = false;
			else if(line.charAt(2) == '1')
				delete = true;
			else
				throw new RuntimeException(RC_ERROR_MESSAGE + " " + line);
			
			//set canonicalPath
			line = line.substring(4);
			canonicalPath = pathToUpp + line.replace(RC_DELIM, File.separatorChar);
			
			//make and add the Node to the tree
			if(root == null)
				root = new Node(canonicalPath, read, modify, delete);
			else
				root.addNode(new Node(canonicalPath, read, modify, delete));
		}
	}
}