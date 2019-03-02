/**
*Class represents a synchrc file
*
*@author Dan Martineau
*@version 1.1
*/

import java.io.File;
import java.util.Scanner;

public class Synchrc
{
	/*FIELDS*/
	private String synchrcPath;	//path of synchrc file: IT IS ASSUMED THAT IT LIES IN UPPERMOST PARENT DIR
	private String pathToUpp;		//path to uppermost parent directory: DETERMINED BY synchrcPath!
	private Node root;					//the root node in the binary search tree
	
	/*CONSTANTS*/
	private static char rcDelim = ',';	//delimiter that the contextless synchrc file uses to seperate directories and files
	
	/*REGULAR EXPRESSIONS*/
	//000 ParentDir,subdir,subdir,file.extension 	//represents how a line should be formatted in the synchrc file
	
	/**
	*Constructor
	*@param path to synchrc file
	*/
	public Synchrc(String synchrcPath)
	{
		//assert that the file is valid
		assert FileCMD.existFile(synchrcPath) : "File " + synchrcPath + " is not valid! This should be handeled outside of Synchrc.";
		
		this.synchrcPath = synchrcPath;	//add path
		root = null;
		
		determinePathToUpp();
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
		
		if(nodePath.equals(root.getPath()))
			node = root;
		else
			node = root.getNode(nodePath);
		
		return node;
	}
	
	/**
	*Sets pathToUpp
	*/
	private void determinePathToUpp()
	{
		int count = 0;
		String hold = "";
		
		//set pathToUpp equal to synchrcPath minus the file and its containing directory
		hold = FileCMD.getName(synchrcPath);
		pathToUpp = synchrcPath.substring(0, synchrcPath.length()-hold.length());
		hold = FileCMD.getName(pathToUpp);
		hold = pathToUpp.substring(0, pathToUpp.length()-(hold.length()+1));
		pathToUpp = hold;
		
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
			decodeLine(fileScan.next());
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
		String canonicalPath;
		
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
				throw new RuntimeException("Line format is invalid: " + line);
			
			//read second bit
			if(line.charAt(1) == '0')
				modify = false;
			else if(line.charAt(1) == '1')
				modify = true;
			else
				throw new RuntimeException("Line format is invalid: " + line);
			
			//read third bit
			if(line.charAt(2) == '0')
				delete = false;
			else if(line.charAt(2) == '1')
				delete = true;
			else
				throw new RuntimeException("Line format is invalid: " + line);
			
			//set canonicalPath
			canonicalPath = pathToUpp + line.substring(4).replace(rcDelim, File.separatorChar);
			
			//make and add the Node to the tree
			if(root == null)
				root = new Node(canonicalPath, read, modify, delete);
			else
				root.addNode(new Node(canonicalPath, read, modify, delete));
		}
	}
}