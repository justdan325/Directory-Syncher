/**
*This is the mechanisim by which everything ultimatley gets synched
*This utility only synchs one way at a time i.e. from directory A to directory B
*It must be run twice to synch both ways
*
*@author Dan Martineau
*@version 1.0
*/

import java.io.File;

public class SynchModule
{
	/*FIELDS*/
	private boolean read;		//read (copy)  files in dir1 not in dir2 by default
	private boolean modify;	//modify files in dir2 not in dir1 by default
	private boolean delete;	//delete files in dir2 not in dir1 by default
	
	//private <DataType> synchrc
	private String log;			//log of synch
	private String dir1;			//canonical path of dir1
	private String dir2;			//canonical path of dir2
	
	/*CONSTANTS*/
	public String DEFAULT_SYNCHRC = "synchrc";
	
	/**
	*Constructor for a default synch job
	*@param dir1 path
	*@param dir2 path
	*@param synchrc file path
	*/
	public SynchModule(String path1, String path2, String rcname)
	{
		//assign parameters to their instance varibales and assert paths are valid
		assert FileCMD.isDir(path1) : "Path 1 is not a directory! This should be handled before SynchModule is called.";
		dir1 = standardizePath(path1);
		
		assert FileCMD.isDir(path1) : "Path 1 is not a directory! This should be handled before SynchModule is called.";
		dir2 = standardizePath(path2);
		
		read = true;
		modify = false;
		delete = false;
		
		log = "";
		
		//create synchrc object
		createSynch(rcname);
	}
	
	/**
	*Constructor for custom synch job
	*@param dir1 path
	*@param dir2 path
	*@param synchrc file path
	*@param read
	*@param modify
	*@param delete
	*/
	public SynchModule(String path1, String path2, String rcname, boolean read, boolean modify, boolean delete)
	{
		//assign parameters to their instance varibales and assert paths are valid
		assert FileCMD.isDir(path1) : "Path 1 is not a directory! This should be handled before SynchModule is called.";
		dir1 = standardizePath(path1);
		
		assert FileCMD.isDir(path1) : "Path 1 is not a directory! This should be handled before SynchModule is called.";
		dir2 = standardizePath(path2);
		
		this.read = read;
		this.modify = modify;
		this.delete = delete;
		
		log = "";
		
		//create synchrc object
		createSynch(rcname);
	}
	
	/*ACCESSORS*/
	
	/**
	*Returns copy of log
	*@return String copy of log
	*/
	public String getLog()
	{
		return new String(log);
	}
	
	/*******************/
	
	/**
	*Creates synch object
	*@param Synch file name
	*/
	private void createSynch(String name)
	{
		String filePath;
		
		//parse path to synchrc file
		if(path.charAt(path.size()-1) == File.pathSeparatorChar)
			filePath = dir1 + name;
		else
			filePath = dir1 + File.pathSeparatorChar + name;
		
		//assert file exisits
		assert FileCMD.existFile(filePath) : "Synchrc file does not exist! Should be handled outside of SynchModule.";
		
//IMPLEMENT
		Prin.tln("Implement synchrc onject in createSynch() within SynchModule.");
		log += ("Synching " + dir1 + " --> " + dir2 " using \"" + name + "\"\n");
//***************
	}
	
	/**
	*Removes the pathSeparatorChar from the end of a path if it is there
	*@param path string
	*@return String correct path string
	*/
	private String standardizePath(String raw)
	{
		String fixed;
		
		if(raw.charAt(raw.size()-1) == File.pathSeparatorChar)
			fixed = raw.substring(0,raw.size()-1);
		else 
			fixed = raw;
		
		return fixed;
	}
	
	/**
	*Preform a synch job
	*@param primary directory
	*@param directory to synch to
	*/
	private void synchJob(String origin, String destination)
	{
		String[] files1 = FileCMD.listFiles(origin);			//files in origin
		String[] files2 = FileCMD.listFiles(destination);	//files in destination
		String[] dirs1 = FileCMD.listDirs(origin);			//directories in origin
		String[] dirs2 = FileCMD.listDirs(destination);	//directories in destination
		
		String curr = "";				//current file in question
		boolean exist = false;	//whether or not a file exisits
		boolean inDest = false;	//whether or not a file exisits in destination
		boolean inOrigin = false;//whether or not a file exisits in origin
		boolean success;			//whether or not an operation was successful
		int itt;							//secondary itterator
		int comp;						//holder for compareTo methods
		//Node currNode  = null;	//current node for file from synchrc
		
		//compare and manage local files (reading and modifying)
		for(int i = 0; i < files1.length; i++)
		{
			curr = files1[i];
			//exist = synchrc.exist(curr);
			
			//if the current file is in synchrc, deal with it according to that
			if(exist)
			{
//IMPLEMENT
				Prin.tln("I exist, yo!");
			}
			//else preform default actions on file
			else
			{
				//check to see if file exisits in destination
				for(itt = 0; i < files2.length; itt++)
				{
					if(curr.equals(files2[itt]))
					{
						inDest = true;
						break;
					}
				}
				
				//if read is enabled and the file is not in destination (no point in executing if it is there already)
				if(read && !inDest)
				{
					//copy the file to where it belongs--assert the paths are valid first
					assert FileCMD.existFile(origin + File.pathSeparatorChar + curr) : ("It seems that " + origin + File.pathSeparatorChar + curr + " does not exist.");
					assert FileCMD.existFile(destination + File.pathSeparatorChar) : ("It seems that " + destination + File.pathSeparatorChar + " does not exist.");
					success = FileCMD.copyFile((origin + File.pathSeparatorChar + curr), (destination + File.pathSeparatorChar + curr), false);
					
					//write to log whether or not operation was successful
					if(success)
						log += ("Copied \"" + curr + "\" from " + origin + " to " + destination "\n");
					else
						log += ("Failed to copy \"" + curr + "\" from " + origin + " to " + destination "\n");
				}
				//if modify is enabled and the file is already in destination
				elseif(modify && inDest)
				{
					//compare the mod times to see if file should be copied--assert parths are valid first
					assert FileCMD.existFile(origin + File.pathSeparatorChar + curr) : ("It seems that " + origin + File.pathSeparatorChar + curr + " does not exist.");
					assert FileCMD.existFile(destination + File.pathSeparatorChar) : ("It seems that " + destination + File.pathSeparatorChar + " does not exist.");
					comp = FileCMD.compModTime((origin + File.pathSeparatorChar + curr), (destination + File.pathSeparatorChar + curr));
					
					//if curr is newer, overwrite the file in destination
					if(comp == 1)
						FileCMD.copyFile((origin + File.pathSeparatorChar + curr), (destination + File.pathSeparatorChar + curr), true);
					//if there is an error
					elseif(comp == -1)
						log += ("There was an error comparing the mod times of " + (origin + File.pathSeparatorChar + curr) + " and " + (destination + File.pathSeparatorChar + curr) + "\n");
				}
				//else ignore file
				else
				{
					//write to log
					log += ("Ignored \"" + curr + "\" in " + origin + "\n");
				}
			}
		}
		//compare and manage local files (deletion)
		for(int i = 0; i < files2.length; i++)
		{
			curr = files2[i];
			//exist = synchrc.exist(curr);
			
			//if the current file is in synchrc, deal with it according to that
			if(exist)
			{
//IMPLEMENT
				Prin.tln("I exist, yo!");
			}
			//else preform default actions on file
			else
			{
				//check to see if file exisits in origin
				for(itt = 0; i < files1.length; itt++)
				{
					if(curr.equals(files1[itt]))
					{
						inOrigin = true;
						break;
					}
				}
				
				//if read is enabled and the file is not in destination (no point in executing if it is there already)
				if(delete && !inOrigin)
				{
					//attempt to delete the file--assert the paths are valid first
					assert FileCMD.existFile(destination + File.pathSeparatorChar + curr) : ("It seems that " + destination + File.pathSeparatorChar + " does not exist.");
					success = FileCMD.deleteFile((destination + File.pathSeparatorChar + curr));
					
					//write to log whether or not operation was successful
					if(success)
						log += ("Deleted \"" + curr + "\" in " + destination "\n");
					else
						log += ("Failed to delete \"" + curr + "\" in " + destination "\n");
				}
				//else ignore file
				else
				{
					//write to log
					log += ("Skipped the deletion of \"" + curr + "\" in " + destination + "\n");
				}
			}
		}
		
		//compare and recursivley manage directories
	}
}