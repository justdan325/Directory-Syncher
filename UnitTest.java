import java.util.*;
import java.nio.file.attribute.FileTime;

public class UnitTest
{
	public static void main(String[] args)
	{
		/*boolean[] temp = {true, true, false};
		ArrayList<Directory> temp2 = new ArrayList<Directory>();
		temp2.add(new Directory("/home/dan/Programming Projects/FileSynch/dir/dir", "dir", null, null, temp));
		Directory dir1 = new Directory("/home/dan/Programming Projects/FileSynch/dir", "dir", temp2, null, temp);
		Prin.tln(dir1.toString());*/
		
		//GenFile file = new GenFile("C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\theDir.txt", "0");
		//GenFile file = new GenFile("<0|C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\theDir.txt|theDir.txt|2019-09-11T03:17:13.176798Z|false|false|false>");
		//Prin.tln(file.current() + "\n");
		//Prin.tln(file.toString());
		
		
		Directory dir1 = new Directory("C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\", "0", "1");
		Prin.tln(dir1.toString() + "\n\n\n");
		//GenFile file = dir1.getFile("C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\testDir\\subdir1\\subdir1a\\ii.rtf");
		//Prin.tln(file.toString() + "\n");
		
		
		//Directory dir2 = new Directory(dir1.toString());
		//Prin.tln(dir2.toString() + "\n");
		
		/*if(dir1.toString().equals(dir2.toString()))
			Prin.tln("\n\nThey match mahn!");*/
		
		//GenFile file = new GenFile("C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\dir", "0");
		//Prin.tln("file: " + file.toString() + "\n");
		
		//GenFile file2 = new GenFile(file.toString());
		//Prin.tln("file2: " + file2.toString() + "\n");
	}
}