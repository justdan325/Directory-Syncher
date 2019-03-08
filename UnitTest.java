import java.util.*;
import java.nio.file.attribute.FileTime;
import java.io.File;


public class UnitTest
{
	public static void main(String[] args)
	{
		String[] list = {"/home/dan/Programming Projects/FileSynch/testDir/c", "/home/dan/Programming Projects/FileSynch/testDir/a", "/home/dan/Programming Projects/FileSynch/testDir/b", "/home/dan/Programming Projects/FileSynch/testDir/1.txt"};
		
		Prin.tln("" + findIndexInList(-30, "a", list));
	}
	
	private static int findIndexInList(int index, String fileName, String[] list)
	{
		int rearIndex = index - 1;
		int frontIndex = index + 1;
		
		if(list.length > 0)
		{
			if(index >= 0 && index < list.length && fileName.equals(FileCMD.getName(list[index])))
				return index;
			
			//make sure we're not out of bounds
			if(rearIndex < 0 || rearIndex >= list.length)
				rearIndex = list.length/2;
			if(frontIndex >= list.length || frontIndex < 0)
				frontIndex = list.length/2 + 1;
			
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
