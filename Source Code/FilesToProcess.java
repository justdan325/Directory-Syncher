/**
*Class is a barebone version of SynchModule that exists soley to calculate the number of
*files that will be processed. Fundamental changes or rewrites of SynchModule will prompt 
*the same action here.
*
*@author Dan Martineau
*@version 1.2
*@since 2.0
*/

import java.io.File;

public class FilesToProcess
{
	/*FIELDS*/
	private boolean read;		
	private Synchrc synchrc;	
	private String dir1;		
	private String dir2;		
	private long num;
	
	/*CONSTANTS*/
	public static String EMPTY_ELEMENT = "?<>";		
	
	public FilesToProcess(String dir1, String dir2, Synchrc synchrc, boolean read, boolean modify, boolean delete)
	{
		this.read = read;
		this.dir1 = standardizePath(dir1);
		this.dir2 = standardizePath(dir2);
		num = 0;
		
		this.synchrc = synchrc;
		
		calc(dir1, dir2);
	}
	
	public long getNum()
	{
		return num;
	}
	
	private void calc(String origin, String destination)
	{
		String[] files1 = FileCMD.listFiles(origin);		
		String[] files2 = FileCMD.listFiles(destination);	
		String[] dirs1 = FileCMD.listDirs(origin);		
		String[] dirs2 = FileCMD.listDirs(destination);	
						
		Node node;															
		
		for(int i = 0; i < files1.length; i++)
			readAndModHelperFile(files1[i], i, files2);
		
		for(int i = 0; i < files2.length; i++)
		{	
			if(!files2[i].equals(EMPTY_ELEMENT))
				num++;
		}
		
		for(int i = 0; i < dirs1.length; i++)
		{	
			node = synchrc.getNode(dirs1[i]);
			
			if(node != null)
				readAndModHelperDir(destination, dirs1[i], node.getRead());
			else
				readAndModHelperDir(destination, dirs1[i], read);
		}
		
		num += dirs2.length;
	}
	
	private void readAndModHelperFile(String curr, int index, String[] files2)
	{			
		index = findIndexInList(index, curr, files2);
		
		num++;
		
		if(index >= 0)
			files2[index] = EMPTY_ELEMENT;
	}
	
	private void readAndModHelperDir(String destination, String curr, boolean localRead)
	{			
		assert FileCMD.existFile(curr) : ("It seems that " + curr + " does not exist.");
		assert FileCMD.existFile(destination) : ("It seems that " + destination + " does not exist.");
		
		num++;
		
		if(localRead)
		{
			if(FileCMD.existFile(destination + File.separatorChar + FileCMD.getName(curr)))
				destination += File.separatorChar + FileCMD.getName(curr);
				   
			calc(curr, destination);
		}
	}
	
	private String standardizePath(String str)
	{
		if(str.charAt(str.length()-1) == File.separatorChar)
			str = str.substring(0,str.length()-1);
		
		return str;
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


