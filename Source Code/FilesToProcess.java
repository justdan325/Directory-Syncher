/**
*Class is a barebone version of SynchModule that exists soley to calculate the number of
*files that will be processed. Fundamental changes or rewrites of SynchModule will prompt 
*the same action here.
*
*@author Dan Martineau
*@version 1.0
*@since 2.0
*/

import java.io.File;

public class FilesToProcess
{
	/*FIELDS*/
	private boolean read;	
	private boolean modify;		
	private boolean delete;	
	private Synchrc synchrc;	
	private String dir1;		
	private String dir2;		
	private int num;
	
	/*CONSTANTS*/
	public static String EMPTY_ELEMENT = "?<>";		
	
	public FilesToProcess(String dir1, String dir2, String rcName, boolean read, boolean modify, boolean delete)
	{
		this.read = read;
		this.modify = modify;
		this.delete = delete;
		this.dir1 = dir1;
		this.dir2 = dir2;
		num = 0;
		
		if(read || modify)
			num = 1;
		if(delete)
			num = 2;
		
		createSynch(rcName);
		
		calc(dir1, dir2);
	}
	
	public int getNum()
	{
		return num;
	}
	
	private void createSynch(String name)
	{
		String filePath;
		
		//parse name to synchrc file
		if(name.equals(SynchModule.DEFAULT_SYNCHRC))
			filePath = dir1 + File.separatorChar + name;
		else
			filePath = name;
		
		//assert file exisits
		assert FileCMD.existFile(filePath) : "Synchrc file: " + filePath + " does not exist! Should be handled outside of SynchModule.";
		
		//instantiate synchrc
		synchrc = new Synchrc(filePath, dir1, (new String()), false);
	}
	
	private void calc(String origin, String destination)
	{
		String[] files1 = FileCMD.listFiles(origin);		
		String[] files2 = FileCMD.listFiles(destination);	
		String[] dirs1 = FileCMD.listDirs(origin);		
		String[] dirs2 = FileCMD.listDirs(destination);	
		
		String curr = "";					
		Node node;						
		boolean success;					
		int comp;							
		
		for(int i = 0; i < files1.length; i++)
		{
			curr = files1[i];
			node = synchrc.getNode(curr);
			
			
			if(node != null)
				readAndModHelperFile(origin, destination, curr, node.getRead(), node.getModify(), node.getDelete(), i, files1, files2);
			else
				readAndModHelperFile(origin, destination, curr, read, modify, delete, i, files1, files2);
		}
		
		for(int i = 0; i < files2.length; i++)
		{
			curr = files2[i];
			node = synchrc.getNode(curr);
			
			if(!curr.equals(EMPTY_ELEMENT))
			{	
				if(node != null)
					deleteHelperFile(origin, destination, curr, node.getRead(), node.getModify(), node.getDelete(), i, files1, files2);
				else
					deleteHelperFile(origin, destination, curr, read, modify, delete, i, files1, files2);
			}
		}
		
		for(int i = 0; i < dirs1.length; i++)
		{	
			curr = dirs1[i];
			node = synchrc.getNode(curr);
			
			if(node != null)
				readAndModHelperDir(origin, destination, curr, node.getRead(), node.getModify(), node.getDelete(), i, dirs1, dirs2);
			else
				readAndModHelperDir(origin, destination, curr, read, modify, delete, i, dirs1, dirs2);
		}
		
		for(int i = 0; i < dirs2.length; i++)
		{
			curr = dirs2[i];
			node = synchrc.getNode(curr);
			
			if(node != null)
				deleteHelperDir(origin, destination, curr, node.getRead(), node.getModify(), node.getDelete(), i, dirs1, dirs2);
			else
				deleteHelperDir(origin, destination, curr, read, modify, delete, i, dirs1, dirs2);
		}	
	}
	
	private void readAndModHelperFile(String origin, String destination, String curr, boolean localRead, boolean localModify, boolean localDelete, int index, String[] files1, String[] files2)
	{		
		String destFile = (destination + File.separatorChar +  FileCMD.getName(curr));	
		
		index = findIndexInList(index, curr, files2);
		
		if(localRead && index == -1)
		{	
			assert FileCMD.existFile(curr) : ("It seems that " + origin + File.separatorChar + curr + " does not exist.");
			assert FileCMD.existFile(destination) : ("It seems that " + destination + " does not exist.");
			num++;
		}
		else if(localModify && index >= 0)
		{	
			assert FileCMD.existFile(curr) : ("It seems that " + curr + " does not exist.");
			assert FileCMD.existFile(destination) : ("It seems that " + destination + " does not exist.");
			
			num++;
				
			files2[index] = EMPTY_ELEMENT;
		}
		
		else if(!localModify && index >= 0)
		{
			assert FileCMD.existFile(curr) : ("It seems that " + curr + " does not exist.");
			assert FileCMD.existFile(destination) : ("It seems that " + destination + " does not exist.");
			
			num++;
				
			files2[index] = EMPTY_ELEMENT;
		}
		
		else if(index >= 0)
		{
			files2[index] = EMPTY_ELEMENT;
			
			num++;
		}
	}
	
	private void deleteHelperFile(String origin, String destination, String curr, boolean localRead, boolean localModify, boolean localDelete, int index, String[] files1, String[] files2)
	{		
		boolean inOrigin = findInList(index, curr, files1);
		
		if(localDelete && !inOrigin)
		{	
			assert FileCMD.existFile(curr) : ("It seems that " + destination + " does not exist.");
			
			num++;
		}
		else if(!(localDelete || inOrigin))
		{
			num++;
		}
	}
	
	private void readAndModHelperDir(String origin, String destination, String curr, boolean localRead, boolean localModify, boolean localDelete, int index, String[] dirs1, String[] dirs2)
	{			
		boolean inDest = findInList(index, curr, dirs2);
		
		if(localRead && !inDest)
		{
			calc(curr, destination + File.separatorChar + FileCMD.getName(curr));
			num++;
			
		}
		else if(localRead && inDest)
		{
			calc(curr, destination + File.separatorChar + FileCMD.getName(curr));
			num++;
		}
	}
	
	private void deleteHelperDir(String origin, String destination, String curr, boolean localRead, boolean localModify, boolean localDelete, int index, String[] dirs1, String[] dirs2)
	{			
		boolean inOrigin = findInList(index, curr, dirs1);
		
		if(localDelete && !inOrigin)
		{
			assert FileCMD.existFile(curr) : ("It seems that " + destination + " does not exist.");
			
			num++;
		}
		else if(!(localDelete || inOrigin))
		{
			num++;
		}
	}
	
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
	
	private static int findIndexInList(int index, String fileName, String[] list)
	{
		int rearIndex = index - 1;
		int frontIndex = index + 1;
		
		fileName = FileCMD.getName(fileName);
		
		if(list.length > 0)
		{
			if(index >= 0 && index < list.length && fileName.equals(FileCMD.getName(list[index])))
				return index;
			
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
}


