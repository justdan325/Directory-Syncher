public class UnitTest
{
	public static void main(String[] args)
	{	
		GenFile file = new GenFile("C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\database_formats.txt", "0");
		Prin.tln(file.toString());
		GenFile file2 = new GenFile(file.toString());
		
		Prin.tln(file2.toString());
		
		if(file.toString().equals(file2.toString()))
			Prin.tln("SUCCESS!");
	}
}