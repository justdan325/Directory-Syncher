public class UnitTest
{
	public static void main(String[] args)
	{	
		String[] names = FileCMD.listDirs("/home/dan/Programming Projects/FileSynch/");
		
		for(int i = 0; i < names.length; i++)
			Prin.tln("List dirs: " + names[i] + "\n");
	}
}