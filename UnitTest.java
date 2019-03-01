import java.util.*;
import java.nio.file.attribute.FileTime;
import java.io.File;


public class UnitTest
{
	public static void main(String[] args)
	{
		SynchModule test = new SynchModule("/home/dan/Programming Projects/FileSynch/testDir", "/home/dan/Programming Projects/FileSynch/newDir", SynchModule.DEFAULT_SYNCHRC, true, true, true);
		
		Prin.tln("\n\n\n" + test.getLog());
		
		/*Node root = new Node("/home/dan/Programming Projects/FileSynch/testDir/b", false, false, false);
		root.addNode(new Node("/home/dan/Programming Projects/FileSynch/testDir/a", false, false, false));
		root.addNode(new Node("/home/dan/Programming Projects/FileSynch/testDir/3/b", false, false, false));
		root.addNode(new Node("/home/dan/Programming Projects/FileSynch/testDir/c", false, false, false));*/
		
		//try{Prin.tln(root.getNode("/home/dan/Programming Projects/FileSynch/testDir/3/b").getPath());}
		//catch(Exception e){e.printStackTrace();}
		
		//Prin.tln(root.toString());
		
		//Synchrc test = new Synchrc("/home/dan/Programming Projects/FileSynch/testDir/synchrc");
		
		//Prin.tln("" + test.getNode("/home/dan/Programming Projects/FileSynch/testDir/synchrc"));
	}
}
