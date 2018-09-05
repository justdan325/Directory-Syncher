import java.util.Scanner;
import java.io.*;

/**
*This is a class that abstracts file io to suit my needs
*
*@author Dan Martineau
*@version 1.0
*/

public class FileCMD
{
	/**
	Read contents of a file into string
	@param file name
	@return file contents
	*/
	public static String readFile(String fileName)
	{
		File file;
		Scanner inputFile;


		file = new File(fileName);
		if(!file.exists())
			return "";

		try
		{
			inputFile = new Scanner(file);
		}

		catch(FileNotFoundException e)
		{
			System.out.println("\nFileNotFoundException\n");
			return "";
		}

		int count = 0;

		String str = "";

		while(inputFile.hasNext())
		{
			if(count != 0)
				str += '\n';
			str += inputFile.nextLine();
			count++;
		}
		inputFile.close();
		return str;
	}

	/**
	Write a sting to a file
	@param string to be written
	@param destination file
	*/
	public static void writeFile(String fileStr, String destination)
	{
		final long LENGTH = fileStr.length();
		File file = new File(destination);
		PrintWriter outputFile;

		try
		{
			outputFile = new PrintWriter(file);
		}

		catch(FileNotFoundException e)
		{
			return;
		}

		for(int i = 0; i < LENGTH; i++)
		{
			if(fileStr.substring(i,i+1).equals("\n"))
				outputFile.println();
			else if(fileStr.substring(i,i+1).equals("\t"))
				outputFile.write("\t");
			else
				outputFile.append(fileStr.charAt(i));
		}
		outputFile.close();
	}

	
}