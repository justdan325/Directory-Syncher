public class UnitTest
{
	public static void main(String[] args)
	{	
		Directory dir1 = new Directory("C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\dir", "0", "1");
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