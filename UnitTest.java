import java.util.*;
import java.nio.file.attribute.FileTime;
import java.io.File;


public class UnitTest
{
	public static void main(String[] args)
	{
		String[] list = {"/home/dan/Programming Projects/FileSynch/testDir/c", "/home/dan/Programming Projects/FileSynch/testDir/a", "/home/dan/Programming Projects/FileSynch/testDir/b", "/home/dan/Programming Projects/FileSynch/testDir/1.txt"};
		
		Prin.tln("" + findInList(-30, "a", list));
	}
	
	private static boolean findInList(int index, String fileName, String[] list)
	{
		boolean found = false;
		int rearIndex = index - 1;
		int frontIndex = index + 1;
		
		if(index >= 0 && index < list.length && fileName.equals(FileCMD.getName(list[index])))
			found = true;
		
		//make sure we're not out of bounds
		if(rearIndex < 0 || rearIndex >= list.length)
			rearIndex = 0;
		if(frontIndex >= list.length || frontIndex < 0)
			frontIndex = list.length-1;
		
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
		
		return found;
	}
}
