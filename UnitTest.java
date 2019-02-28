import java.util.*;
import java.nio.file.attribute.FileTime;
import java.io.File;


public class UnitTest
{
	public static void main(String[] args)
	{
		SynchModule test = new SynchModule("/home/dan/Programming Projects/FileSynch/testDir", "/home/dan/Programming Projects/FileSynch/newDir", SynchModule.DEFAULT_SYNCHRC, true, true, true);
		
		Prin.tln("\n\n\n" + test.getLog());
	}
}
