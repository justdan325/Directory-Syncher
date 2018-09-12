/**
*This class builds a list of changes that have been made to a directory
*since the previous synch.
*
*@author Dan Martineau
*@version 1.0
*/

import java.util.ArrayList;

public class ChangeList
{
	/*FIELDS*/
	Directory oldDir;				//Directory built from database archive
	Directory newDir;				//Directory built from current system state
	ArrayList<GenFile> newFiles;	//list of newly added files
	ArrayList<GenFile> delFiles;	//list of deleted files
	ArrayList<GenFile> modFiles;	//list of modified files
	ArrayList<Directory> newDirs;	//list of newly added directories
	ArrayList<Directory> delDirs;	//list of deleted directories
	
	
	/**
	*Constructor
	*@param Directory built from current system state 
	*@param Directory object built from database archive
	*/
	public ChangeList(Directory newDir, Directory oldDir)
	{
		this.newDir = newDir;
		this.oldDir = oldDir;
		newFiles = new ArrayList<GenFile>();
		delFiles = new ArrayList<GenFile>();
		modFiles = new ArrayList<GenFile>();
		newDirs = new ArrayList<Directory>();
		delDirs = new ArrayList<Directory>();
		
		//find all changes 
		findChanges(newDir, oldDir);
	}
	
	/**
	*Method allows user to update the change lists by doing the same as findChanges().
	*The diffrence is that the lists are cleared first.
	*/
	public void refreshChanges()
	{
		newFiles.clear();
		newFiles.trimToSize();
		delFiles.clear();
		delFiles.trimToSize();
		modFiles.clear();
		modFiles.trimToSize();
		newDirs.clear();
		newDirs.trimToSize();
		delDirs.clear();
		delDirs.trimToSize();
		modDirs.clear();
		modDirs.trimToSize();
		
		findChanges(newDir, oldDir);
	}
	
	/**
	*Method adds any changed files to the appropriate list in the fields
	*@param the directory to find changes in
	*@return ArrayList containg the three first arraylists
	*/
	private ArrayList findChanges(Directory currNew, Directory currOld)
	{
		ArrayList<GenFile> newF = new ArrayList<GenFile>();		//list of newly added files
		ArrayList<GenFile> delF = new ArrayList<GenFile>();		//list of deleted files
		ArrayList<GenFile> modF = new ArrayList<GenFile>();		//list of modified files
		ArrayList<Directory> newD = new ArrayList<Directory>();	//list of newly added directories
		ArrayList<Directory> delD = new ArrayList<Directory>();	//list of deleted directories
	
		//get files in currDir (not in sub directories)
		ArrayList<GenFile> newLocalF = currNew.getFiles();
		ArrayList<GenFile> oldLocalF = currOld.getFiles();
		//get directories nested in currDir
		ArrayList<Directory> newLocalD = currNew.getSubs();
		ArrayList<Directory> oldLocalD = currOld.getSubs();
		
		//temporary holder for files
		GenFile oldTempF;
		GenFile newTempF;
		
		//examine old local files
		for(int i = 0; i < oldLocalF.size(); i++)
		{
			//get next file
			oldTempF = oldLocalF.get(i);
			
			//see if file was deleted
			if(!FileCMD.existFile(oldTempF))
				delF.add(oldTempF);
			//see if file has been modified
			else if(oldTempF.current() != 0)
				modF.add(oldTempF);
		}
		
		//examine new local files
		for(int i = 0; i < newLocalF.size(); i++)
		{
			//get next file
			oldTempF = oldLocalF.get(i);
			newTempF = newLocalF.get(i);
			//whether or not the file exists in the old directory
			boolean found = false;
			
			//see if file has been added since last synch
			for(int i = 0; i < oldLocalF.size(); i++)
			{
				if(oldLocalF.get(i).getPath().equals(newTempF.getPath()))
				{
					found = true;
					break;
				}
			}
			
			//if not found, the file must be new
			newF.add(newTempF);
		}
		
		//examine local directories to see if they're still there
		//if they're not, add them to deleted directories
		//then recursivley search through them to find all deleted files/subdirectories--add them in as well
		
		//check for new loacl subdirectories and add them to the new dir list
		//call this method on every local subdirectory and add their returned contents to the lists
	}
	
	//code accessor for the lists
	//code a toString()
	
}