public class UnitTest
{
	public static void main(String[] args)
	{	
		GenFile file = new GenFile("C:\\Users\\Dan\\ProgrammingProjects\\FileSynch\\database_formats.txt", "01");
		Prin.tln("Can update: " + file.canUpdate());
		Prin.tln("Can delete: " + file.canDel());
		Prin.tln("Can read: " + file.canRead());
		Prin.tln("File ID: " + file.getFileID());
		Prin.tln("FileTime: " + file.getFileTime());
		Prin.tln("Last mod: " + file.getModStamp());
	}
}