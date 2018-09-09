import java.util.*;

public class UnitTest
{
	public static void main(String[] args)
	{
		boolean[] temp = {true, true, false};
		ArrayList<Directory> temp2 = new ArrayList<Directory>();
		temp2.add(new Directory("/home/dan/Programming Projects/FileSynch/dir/dir", "dir", null, null, temp));
		Directory dir1 = new Directory("/home/dan/Programming Projects/FileSynch/dir", "dir", temp2, null, temp);
		Prin.tln(dir1.toString());
		
		//Directory dir2 = new Directory(dir1.toString());
		
		//if(dir1.toString().equals(dir2.toString()))
			//Prin.tln("\n\nThey match mahn!");
		
		//GenFile file = new GenFile("C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\dir", "0");
		//Prin.tln("file: " + file.toString() + "\n");
		
		//GenFile file2 = new GenFile(file.toString());
		//Prin.tln("file2: " + file2.toString() + "\n");
	}
}