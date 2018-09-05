public class UnitTest
{
	public static void main(String[] args)
	{	
		Prin.tln("Mod time file1: " + FileCMD.getModTime("C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\dir1\\file1.txt"));
		Prin.tln("Compare file2 mod times in dir1 and dir2: " + FileCMD.compModTime("C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\dir2\\file2.txt", "C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\dir1\\file2.txt"));
	}
}