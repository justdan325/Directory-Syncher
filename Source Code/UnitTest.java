import java.io.*;
import java.util.ArrayList;

public class UnitTest
{
	public static void main(String[] args)
	{
		Status status = Status.getStatus();
		String[] files = FileCMD.listAll(args[0]);
		

		status.setTotal(files.length+1);
		status.setDir(args[0], files.length);
		status.setPrintOnUpdate(true);

		for(String file: files)
		{
			status.setFile(file);
			status.setMode(Status.MODE_READ);
			Prin.pause(20);
		}
	}
}
