/**
*This is the mechanisim by which everything ultimatley gets synched
*This utility only synchs one way at a time i.e. from directory A to directory B
*It must be run twice to synch both ways
*
*@author Dan Martineau
*@version 2.9
*/

import java.io.*;

public class SynchModule
{
	/*FIELDS*/
	private boolean read;		//read (copy)  files in dir1 not in dir2 by default
	private boolean modify;		//modify files in dir2 not in dir1 by default
	private boolean delete;		//delete files in dir2 not in dir1 by default
	private boolean verbose;		//true if program running in verbose mode
	private boolean safe;		//true if program running in safe mode
	private boolean existTrash;	//true if there is a DEFAULT_TRASH directory in dir2
	private Synchrc synchrc1;	//synchrc file object for dir1
	private Synchrc synchrc2;	//synchrc file object for dir2
	private String log;			//log of synch
	private String dir1;		//canonical path of dir1
	private String dir2;		//canonical path of dir2
	private String trash;		//DEFAULT_TRASH directory located in dir2
	private Status status;		//instance of Status
	
	/*CONSTANTS*/
	public static final String DEFAULT_TRASH = "DELETEME_DirectorySyncher";	//Name of default trash directory
	public static final String EMPTY_ELEMENT = "?<>";						//Signifies an empty array element
	
	/**
	*Constructor for a default synch job
	*@param dir1 path
	*@param dir2 path
	*@param synchrc1 object for dir1
	*@param synchrc2 object for dir2
	*/
	public SynchModule(String path1, String path2, Synchrc synchrc1, Synchrc synchrc2)
	{
		//assign parameters to their instance varibales and assert paths are valid
		assert FileCMD.existFile(path1) : "Path 1 is not a directory! This should be handled before SynchModule is called.";
		dir1 = standardizePath(path1);
		
		assert FileCMD.existFile(path2) : "Path 2 is not a directory! This should be handled before SynchModule is called.";
		dir2 = standardizePath(path2);
		
		read = true;
		modify = false;
		delete = false;
		verbose = false;
		safe = false;
		existTrash = false;
		status = Status.getStatus();
		this.synchrc1 = synchrc1;
		this.synchrc2 = synchrc2;
		
		trash = dir2 + File.separatorChar + DEFAULT_TRASH;
		if(FileCMD.isDir(trash))
			existTrash = true;
		
		log = ("\nSynching " + dir1 + " --> " + dir2 + " using \"" + synchrc1.getName() + "\" and \"" + synchrc2.getName() + "\"\n");
		
		synchJob(dir1, dir2);
	}
	
	/**
	*Constructor for custom synch job
	*@param dir1 path
	*@param dir2 path
	*@param synchrc1 object for dir1
	*@param synchrc2 object for dir2
	*@param read
	*@param modify
	*@param delete
	*@param verbose
	*@param safe
	*/
	public SynchModule(String path1, String path2, Synchrc synchrc1, Synchrc synchrc2, boolean read, boolean modify, boolean delete, boolean verbose, boolean safe)
	{
		//assign parameters to their instance varibales and assert paths are valid
		assert FileCMD.existFile(path1) : "Path 1 is not a directory! This should be handled before SynchModule is called.";
		dir1 = standardizePath(path1);
		
		assert FileCMD.existFile(path1) : "Path 1 is not a directory! This should be handled before SynchModule is called.";
		dir2 = standardizePath(path2);
		
		this.read = read;
		this.modify = modify;
		this.delete = delete;
		this.verbose = verbose;
		this.safe = safe;
		existTrash = false;
		status = Status.getStatus();
		this.synchrc1 = synchrc1;
		this.synchrc2 = synchrc2;
		
		trash = dir2 + File.separatorChar + DEFAULT_TRASH;
		if(FileCMD.isDir(trash))
			existTrash = true;
		
		log = ("\nSynching " + dir1 + " --> " + dir2 + " using \"" + synchrc1.getName() + "\" and " + synchrc2.getName() + "\"\n");
		
		if(verbose)
			status.setPrint(true);
			
		synchJob(dir1, dir2);
		
		status.setPrint(false);
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
	*Removes the separatorChar from the end of a path if it is there
	*@param path string
	*@return String correct path string
	*/
	private String standardizePath(String raw)
	{
		String fixed;
		
		if(raw.charAt(raw.length()-1) == File.separatorChar)
			fixed = raw.substring(0,raw.length()-1);
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
		String[] files1 = FileCMD.listFiles(origin);		//files in origin
		String[] files2 = FileCMD.listFiles(destination);	//files in destination
		String[] dirs1 = FileCMD.listDirs(origin);		//directories in origin
		String[] dirs2 = FileCMD.listDirs(destination);	//directories in destination
		
		String curr = "";					//current file in question
		Node node;						//holder for the current Node
		boolean success;					//whether or not an operation was successful
		int comp;							//holder for compareTo methods
		
		//compare and manage local files (reading and modifying)
		for(int i = 0; i < files1.length; i++)
		{
			status.setMode(Status.MODE_READ);
			
			curr = files1[i];
			node = synchrc1.getNode(curr);
			
			status.setDir(origin);
			status.setFile(FileCMD.getName(curr));
			
			//if the current file is in synchrc, deal with it according to that
			if(node != null)
				readAndModHelperFile(origin, destination, curr, node.getRead(), node.getModify(), node.getDelete(), i, files1, files2);
			//else preform default actions on file
			else
				readAndModHelperFile(origin, destination, curr, read, modify, delete, i, files1, files2);
		}
		
		//compare and manage local files (deletion)
		for(int i = 0; i < files2.length; i++)
		{
			status.setMode(Status.MODE_DELASS);
			
			curr = files2[i];
			
			if(!curr.equals(EMPTY_ELEMENT))
			{
				status.setDir(destination);
				status.setFile(FileCMD.getName(curr));
				node = synchrc2.getNode(curr);
				
				//if the current file is in synchrc, deal with it according to that
				if(node != null)
					deleteHelperFile(origin, destination, curr, node.getRead(), node.getModify(), node.getDelete(), i, files1, files2);
				//else preform default actions on file
				else
					deleteHelperFile(origin, destination, curr, read, modify, delete, i, files1, files2);
			}
		}
		
		//compare and recursivley manage directories
		for(int i = 0; i < dirs1.length; i++)
		{	
			status.setMode(Status.MODE_READ);
			
			curr = dirs1[i];
			node = synchrc1.getNode(curr);
			
			status.setDir(origin);
			status.setFile(FileCMD.getName(curr));
			
			//if the current file is in synchrc, deal with it according to that
			if(node != null)
				readAndModHelperDir(origin, destination, curr, node.getRead(), node.getModify(), node.getDelete(), i, dirs1, dirs2);
			//else preform default actions on dir
			else
				readAndModHelperDir(origin, destination, curr, read, modify, delete, i, dirs1, dirs2);
		}
		
		for(int i = 0; i < dirs2.length; i++)
		{
			status.setMode(Status.MODE_DELASS);
			
			curr = dirs2[i];
			node = synchrc2.getNode(curr);
			
			status.setDir(destination);
			status.setFile(FileCMD.getName(curr));
			
			//if the current dir is in synchrc, deal with it according to that
			if(node != null)
				deleteHelperDir(origin, destination, curr, node.getRead(), node.getModify(), node.getDelete(), i, dirs1, dirs2);
			//else preform default actions on dir
			else
				deleteHelperDir(origin, destination, curr, read, modify, delete, i, dirs1, dirs2);
		}	
	}
	
	private void readAndModHelperFile(String origin, String destination, String curr, boolean localRead, boolean localModify, boolean localDelete, int index, String[] files1, String[] files2)
	{
		Node node;		//holder for the current Node
		String destFile;	//holder for destination file canonicalPath with name
		boolean success;	//whether or not an operation was successful
		int comp;			//holder for compareTo methods
		long time;		//holder for current timestamp
		
		//check to see if file exisits in destination
		index = findIndexInList(index, curr, files2);
		
		//set destFile
		destFile = (destination + File.separatorChar +  FileCMD.getName(curr));
		
		//if read is enabled and the file is not in destination (no point in executing if it is there already)
		if(localRead && index == -1)
		{
			status.setMode(Status.MODE_COPY);
			
			//copy the file to where it belongs--assert the paths are valid first
			assert FileCMD.existFile(curr) : ("It seems that " + origin + File.separatorChar + curr + " does not exist.");
			assert FileCMD.existFile(destination) : ("It seems that " + destination + " does not exist.");
			success = FileCMD.copyFile(curr, destFile, false);
			
			//write to log whether or not operation was successful
			if(success)
				log += ("Copied \"" +  FileCMD.getName(curr) + "\" from " + origin + " to " + destination + "\n");
			else
				log += ("Failed to copy \"" +  FileCMD.getName(curr) + "\" from " + origin + " to " + destination + "\n");
		}
		//if modify is enabled and the file is already in destination
		else if(localModify && index >= 0)
		{
			status.setMode(Status.MODE_MOD);
			
			//compare the mod times to see if file should be copied--assert parths are valid first
			assert FileCMD.existFile(curr) : ("It seems that " + curr + " does not exist.");
			assert FileCMD.existFile(destination) : ("It seems that " + destination + " does not exist.");
			
			//compare the mod times to see if one is newer
			comp = FileCMD.compModTime(curr, (destination + File.separatorChar +  FileCMD.getName(curr)));
			
			//if one is newer than the other, see if the two files are indeed different using their hashes. 
			//do not do this in safe mode
			if(comp > 0 && CompareMD5.compareHashes(curr, destFile) == true)
			{
				assert comp == 1 || comp == 2 : "comp in readAndModHelperFile does not equal 1 or 2.";
				
				if(!safe)
				{
					time = 0;
					
					//Set the mod times of the files to the same time to make future runs more effecient.
					//specifically the oldest of the two mod times so that they won't constantly be updated when synching to multiple directories
					if(comp == 1)
						time = FileCMD.getFileTime(destFile).toMillis();
					else if(comp == 2)
						time = FileCMD.getFileTime(curr).toMillis();
					
					
					FileCMD.touch(curr, time);
					FileCMD.touch(destFile, time);
				}
				
				comp = 0;
			}
				
			//replace element in files2 with EMPTY_ELEMENT to make deletion process more efficient
			files2[index] = EMPTY_ELEMENT;
			
			//if curr is newer, overwrite the file in destination
			if(comp == 1)
			{
				status.setMode(Status.MODE_UPDATE);
				
				FileCMD.copyFile(curr, destFile, true);
				log += ("Replaced \"" + FileCMD.getName(curr) + "\" in " +  destination + " with \"" + FileCMD.getName(curr) + "\" in " + origin + "\n");
			}
			//if there is an error
			else if(comp == -1)
				log += ("There was an error comparing the mod times of " + curr + " and " + destFile + "\n");
		}
		
		else if(!localModify && index >= 0)
		{
			assert FileCMD.existFile(curr) : ("It seems that " + curr + " does not exist.");
			assert FileCMD.existFile(destination) : ("It seems that " + destination + " does not exist.");
				
			//replace element in files2 with EMPTY_ELEMENT to make deletion process more efficient
			files2[index] = EMPTY_ELEMENT;
		}
		
		//if the file is in the destination whatsoever, we want to remove it from the list because there's no point in checking if it should be deleted in the deletion process
		else if(index >= 0)
		{
			//replace element in files2 with EMPTY_ELEMENT to make deletion process more efficient
			files2[index] = EMPTY_ELEMENT;
		}
	}
	
	private void deleteHelperFile(String origin, String destination, String curr, boolean localRead, boolean localModify, boolean localDelete, int index, String[] files1, String[] files2)
	{
		boolean inOrigin = false;	//whether or not a file exisits in origin
		boolean success = false;		//whether or not an operation was successful
		int comp;			//holder for compareTo methods
		
		//check to see if file exisits in origin
		inOrigin = findInList(index, curr, files1);
		
		//if delete is enabled and the file is not in destination (no point in executing if it is there already)
		if(localDelete && !inOrigin)
		{	
			//attempt to delete the file--assert the paths are valid first
			assert FileCMD.existFile(curr) : ("It seems that " + destination + " does not exist.");
			
			status.setMode(Status.MODE_DEL);
			
			if(safe)
			{
				success = moveToTrash(curr);
				
				if(success)
					log += ("Moved \"" + curr + "\" to " + trash + "\n");
				else
					log += ("Failed to move \"" + curr + "\" to " + trash + "\n");
			}
			else
			{
				success = FileCMD.deleteFile(curr);
				
				//write to log whether or not operation was successful
				if(success)
					log += ("Deleted \"" + FileCMD.getName(curr) + "\" in " + destination + "\n");
				else
					log += ("Failed to delete \"" +  FileCMD.getName(curr) + "\" in " + destination + "\n");
			}
		}
		//else ignore file
		else if(!(localDelete || inOrigin))
		{
			//write to log
			log += ("Skipped the deletion of \"" +  FileCMD.getName(curr) + "\" in " + destination + "\n");
			
		}
	}
	
	private void readAndModHelperDir(String origin, String destination, String curr, boolean localRead, boolean localModify, boolean localDelete, int index, String[] dirs1, String[] dirs2)
	{
		boolean inDest = false;		//whether or not a file exisits in destination
		boolean success = false;		//whether or not an operation was successful
		int comp;			//holder for compareTo methods
		
		//check to see if file exisits in destination
		inDest = findInList(index, curr, dirs2);
		
		status.setMode(Status.MODE_COPY);
		
		//if read enabled and directory is not in destination
		if(localRead && !inDest)
		{
			//attempt to make directory
			success = FileCMD.mkdirs(destination + File.separatorChar + FileCMD.getName(curr));
			
			if(success)
			{
				status.setFile(FileCMD.getName(curr));
				
				log += ("Added directory \"" + FileCMD.getName(curr) + "\" to " + destination + "\n");
				
				//preform synchJob on new subdir
				synchJob(curr, destination + File.separatorChar + FileCMD.getName(curr));
			}
			else
				log += ("Failed to add directory \"" + FileCMD.getName(curr) + "\" to " + destination + "\n");
		}
		//if read enabled and sub dir already exists
		else if(localRead && inDest)
		{
			//preform synchJob on new subdir
			synchJob(curr, destination + File.separatorChar + FileCMD.getName(curr));
		}
	}
	
	private void deleteHelperDir(String origin, String destination, String curr, boolean localRead, boolean localModify, boolean localDelete, int index, String[] dirs1, String[] dirs2)
	{
		boolean inOrigin = false;	//whether or not a file exisits in origin
		boolean success = false;		//whether or not an operation was successful
		int comp;			//holder for compareTo methods
		
		//check to see if dir exisits in origin
		inOrigin = findInList(index, curr, dirs1);
		
		//if read is enabled and the dir is not in destination (no point in executing if it is there already)
		if(localDelete && !inOrigin)
		{
			//attempt to delete the dir--assert the paths are valid first
			assert FileCMD.existFile(curr) : ("It seems that " + destination + " does not exist.");
			
			status.setMode(Status.MODE_DEL);
			
			if(safe)
			{
				//do not move trash into itself
				if(!curr.equals(trash))
				{
					success = moveToTrash(curr);
					
					if(success)
						log += ("Moved \"" + curr + "\" to " + trash + "\n");
					else
						log += ("Failed to move \"" + curr + "\" to " + trash + "\n");
				}
			}
			else
			{
				success = FileCMD.deleteDir(curr);
				
				if(success)
					log += ("Deleted \"" + FileCMD.getName(curr) + "\" in " + destination + "\n");
				else
					log += ("Failed to delete \"" + FileCMD.getName(curr) + "\" in " + destination + "\n");
			}
		}
		//else ignore file
		else if(!(localDelete || inOrigin))
		{
			//write to log
			log += ("Skipped the deletion of \"" +  FileCMD.getName(curr) + "\" in " + destination + "\n");
		}
	}
	
	/**
	*Searches for mathcing file in an array--greatly improves speed when searching through massive lists
	*@param index of occurance of file in first list
	*@param name of file
	*@param list to search through
	*@return true if match was found
	*/
	private static boolean findInList(int index, String fileName, String[] list)
	{
		boolean found = false;
		int rearIndex = index - 1;
		int frontIndex = index + 1;
		
		fileName = FileCMD.getName(fileName);
		
		if(list.length > 0)
		{
			if(index >= 0 && index < list.length && fileName.equals(FileCMD.getName(list[index])))
				found = true;
			
			//make sure we're not out of bounds
			if(rearIndex < 0 || rearIndex >= list.length)
				rearIndex = list.length/2;
			if(frontIndex >= list.length || frontIndex < 0)
				frontIndex = list.length/2;
			
			while(!found)
			{
				if(fileName.equals(FileCMD.getName(list[rearIndex])) || fileName.equals(FileCMD.getName(list[frontIndex])))
					found = true;
				else
				{
					if(rearIndex == 0 && frontIndex == list.length-1)
						break;
					else
					{
						if(rearIndex > 0)
							rearIndex--;
						if(frontIndex < list.length-1)
							frontIndex++;
					}
				}
			}
		}
		
		return found;
	}
	
	/**
	*Searches for mathcing file in an array--greatly improves speed when searching through massive lists
	*@param index of occurance of file in first list
	*@param name of file
	*@param list to search through
	*@return index if found, -1 if not found
	*/
	private static int findIndexInList(int index, String fileName, String[] list)
	{
		int rearIndex = index - 1;
		int frontIndex = index + 1;
		
		fileName = FileCMD.getName(fileName);
		
		if(list.length > 0)
		{
			if(index >= 0 && index < list.length && fileName.equals(FileCMD.getName(list[index])))
				return index;
			
			//make sure we're not out of bounds
			if(rearIndex < 0 || rearIndex >= list.length)
				rearIndex = list.length/2;
			if(frontIndex >= list.length || frontIndex < 0)
				frontIndex = list.length/2;
			
			while(true)
			{
				if(fileName.equals(FileCMD.getName(list[rearIndex])))
					return rearIndex;
				else if(fileName.equals(FileCMD.getName(list[frontIndex])))
					return frontIndex;
				else
				{
					if(rearIndex == 0 && frontIndex == list.length-1)
						break;
					else
					{
						if(rearIndex > 0)
							rearIndex--;
						if(frontIndex < list.length-1)
							frontIndex++;
					}
				}
			}
		}
		
		return -1;
	}
	
	/**
	*Move a file or directory to the DEFAULT_TRASH directory
	*@param name of file/directory
	*@return true if moved
	*/
	private boolean moveToTrash(String file)
	{
		boolean moved = false;
		boolean success = false;
		
		//make trash directory if it does not exisit
		if(!existTrash)
			success = FileCMD.mkdirs(trash);
		
		if(success)
			existTrash = true;
		
		if(existTrash)
		{
			assert !file.equals(trash) : "Attempted to put \"trash\" into itself...";
			
			//incriment total in status becuase new directory has just been made
			status.addToTotal(1);

			//move "file" to trash directory
			if(FileCMD.isDir(file))
				moved = FileCMD.moveDir(file, trash);
			else
				moved = FileCMD.moveFile(file, trash);
		}
		
		return moved;
	}
}


