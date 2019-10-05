import java.io.*;


public class UnitTest
{
	public static void main(String[] args)
	{
		Prin.tln(getCanonPath(args[0]));
	}
	
	public static String getCanonPath(String inputFile)
	{
		File file = new File(inputFile);
		String path = null;
		String errMess = null;
		
		try{path = file.getCanonicalPath();}
		catch(SecurityException e){errMess = ("There was a security exception when atempting to read the file path of : " + inputFile + "\n" + Prin.getStackTrace(e));}
		catch(IOException f){errMess = ("There was an IOException when atempting to read the file path of : " + inputFile + "\n" + Prin.getStackTrace(f));}

		assert errMess == null : errMess;
		
		/*//remove second directory name if directory
		if(path != null && isDir(path))
			path = path.substring(0, (path.length() - inputFile.length()));*/
		
		return path;
	}
}


/*import java.util.*;
import java.nio.file.attribute.FileTime;


import java.security.*;

import java.util.regex.*;
import java.io.File;
import java.util.Date;

public class UnitTest
{
	private static boolean read;
	private static boolean mod;
	private static boolean del;
	private static boolean verbose;
	private static boolean unidirectional;
	private static boolean saveLog;
	private static int initialLogLen;
	private static String dir1;
	private static String dir2;
	private static String log;
	private static String rcPrim;
	private static String rcSec;
	
	private static String REGEX_PERM     = "[0-1]{3}";
	private static String REGEX_FLAG      = "[-]{1}[vul]{1,3}";
	private static String REGEX_CUSTRC = "[-]{2}[rcPrimSe]{5,6}";
	
	private static final double VERSION 	= 1.03;
	private static final String PREAMBLE 	= "Directory Syncher Version " + VERSION + "\nMade and maintaned by Dan Martineau.\n\n";
	private static final String PROG_NAME 	= "java -jar synch.jar";	//what one would use to call this program via commandline
	
	public static void main(String[] args) throws Exception
	{
		read = true;
		mod = false;
		del = false;
		verbose = false;
		unidirectional = false;
		saveLog = false;
		rcPrim = SynchModule.DEFAULT_SYNCHRC;
		rcSec = SynchModule.DEFAULT_SYNCHRC;
		
		
		log = PREAMBLE + PROG_NAME + " ";
		
		for(int i = 0; i < args.length; i++)
			log += args[i] + " ";
		
		log += "\n\nBeginning synch...\n";
		
		initialLogLen = log.length();

		//extract custom rc files if they are there
		args = findCustRc(args);
		
		decodeArgs(args);
	}
	
	private static void decodeArgs(String[] args)
	{
		switch(args.length)
		{
			case 1:
				if(args[0].equals("--help"))
					displayOptions();
				else
					Prin.err("Invalid arguments! Use --help for list of options.\n");
				break;
			case 2:
				decodeDirs(args[0], args[1]);
				startSynch();
				break;
			case 3:
				threeArgs(args[0], args[1], args[2]);
				startSynch();
				break;
			case 4:
				fourArgs(args[0], args[1], args[2], args[3]);
				startSynch();
				break;
			default:
				Prin.err("Invalid arguments! Use --help for list of options.\n");
		}
	}
	
	private static void threeArgs(String one, String two, String three)
	{
		if(match(REGEX_PERM, one))
		{
			decodePerm(one);
			decodeDirs(two, three);
		}
		else if(match(REGEX_FLAG, one))
		{
			decodeFlags(one);
			decodeDirs(two, three);
		}	
		else
		{
			Prin.err("Invalid arguments! Use --help for list of options.\n");
			error();
		}
	}
	
	private static void fourArgs(String one, String two, String three, String four)
	{
		if(match(REGEX_PERM, one) && match(REGEX_FLAG, two))
		{
			decodePerm(one);
			decodeFlags(two);
			decodeDirs(three, four);
		}
		else if(match(REGEX_FLAG, one) && match(REGEX_PERM, two))
		{
			decodeFlags(one);
			decodePerm(two);
			decodeDirs(three, four);
		}	
		else
		{
			Prin.err("Invalid arguments! Use --help for list of options.\n");
			error();
		}
	}
	
	//comb through argument array to find end flags
	private static String[] findCustRc(String[] items)
	{
		int nullCount = 0;
		int itt = 0;
		String[] newArr;
		
		for(int i = items.length-1; i >= 0; i--)
		{
			if(match(REGEX_CUSTRC, items[i]))
			{
				if(i == items.length-1)
				{
					Prin.err("Error: no directory given for: " + items[i] + "\n");
					error();
				}
				else
				{
					decodeRc(items[i], items[i+1]);
					items[i] = null;
					items[i+1] = null;
					nullCount += 2;
				}
			}
		}
		
		//create new array with found flags removed
		newArr = new String[items.length - nullCount];
		
		for(int i = 0; i < items.length; i++)
		{
			if(items[i] != null)
			{
				newArr[itt] = items[i];
				itt++;
			}
		}
		
		return newArr;
	}
	
	private static void startSynch()
	{
		assert dir1 != null : "The field \"dir1\" is null in startSynch(). This should not be possible.";
		assert dir2 != null : "The field \"dir2\" is null in startSynch(). This should not be possible.";
		
		//check to ensure that the default rc files exist--create them if they don't
		checkSynchrc(dir1);
		checkSynchrc(dir2);
		
		//begin synch job
		
		//synch from primary to secondary
		try
		{
			SynchModule part1 = new SynchModule(dir1, dir2, rcPrim, read, mod, del, verbose);
		
			log += part1.getLog();
			
			//synch from secondary to primary if not unidirectional
			if(!unidirectional)
			{
				SynchModule part2 = new SynchModule(dir2, dir1, rcSec, read, mod, del, verbose);
				
				log += part2.getLog();
			}
		}
		catch(Exception e)
		{
			//Prin.err("\n" + e.toString() + "\n");
			//error();
		}
		
		log += "\nFinished synch job!";
		
		if(verbose)
			Prin.tln("\n\n" + log);
		
		saveLogFile(dir1);
		saveLogFile(dir2);
	}
	
	private static void decodePerm(String permissions)
	{
		if(permissions.charAt(0) == '0')
			read = false;
		else if(permissions.charAt(0) == '1')
			read = true;
		
		if(permissions.charAt(1) == '0')
			mod = false;
		else if(permissions.charAt(1) == '1')
			mod = true;
		
		if(permissions.charAt(2) == '0')
			del = false;
		else if(permissions.charAt(2) == '1')
			del = true;
	}
	
	private static void decodeDirs(String path1, String path2)
	{
		if(!FileCMD.isDir(path1))
		{
			Prin.err("Invalid primary directory: " + path1 + "\n");
			error();
		}
		
		if(!FileCMD.existFile(path2))
		{
			log += "Creating secondary directory: " + path2 + "\n";
			
			//attempt to make secondary directory
			boolean attempt = FileCMD.mkdirs(path2);
			
			if(attempt)
				log += "Directory was successfully created!\n";
			//exit if unsuccessful
			else
			{
				log += "Failed to create directory. Ending synch...\n";
				Prin.err("Could not create secondary directory.\n");
				error();
			}
		}
		
		dir1 = path1;
		dir2 = path2;
	}
	
	private static void decodeFlags(String flags)
	{
		for(int i = 0; i < flags.length(); i++)
			{
				switch(flags.charAt(i))
				{
					case '-':
						break;
					case 'u':
						unidirectional = true;
						break;
					case 'l':
						saveLog = true;
						break;
					case 'v':
						verbose = true;
						break;
					default:
						Prin.err("Invalid flag series: " + flags + "\nRun with --help for list of flags.\n");
						error();
				}
			}
	}
	
	private static void decodeRc(String str1, String str2)
	{
		if(!FileCMD.existFile(str2))
		{
			Prin.err("Invalid custom synchrc file: " + str2 + "\n");
			error();
		}
		else if(FileCMD.isDir(str2))
		{
			Prin.err("Invalid custom synchrc file: " + str2 + "\n");
			error();
		}
		
		switch(str1)
		{
			case "--rcPrim":
				rcPrim = str2;
				break;
			case "--rcSec":
				rcSec = str2;
				break;
			default:
				Prin.err("Invalid flag: " + str1 + "\n");
				error();
		}
	}
	
	private static boolean match(String regex, String container)
	{
		Pattern pat = Pattern.compile(regex);
		Matcher match = pat.matcher(container);
		
		return match.find();
	}
	
	private static void error()
	{
		if(dir1 != null)
			saveLogFile(dir1);
		if(dir2 != null)
			saveLogFile(dir2);
		
		System.exit(-1);
	}
	
	private static void checkSynchrc(String path)
	{
		final String PARENT_DIR = FileCMD.getName(path);
		final String DEFAULT_RC_CONT = "#This is your synchrc file\n\n#This line prevents your synchrc files from overwriting eachother across directories.\n000 " + PARENT_DIR + ",synchrc\n\n#The following two lines are essential when running a job on the parent directory of a drive in Windows.\n000 " + PARENT_DIR + ",System Volume Information\n000 " + PARENT_DIR + ",$RECYCLE.BIN";
		
		if(path.charAt(path.length()-1) == File.separatorChar)
			path += SynchModule.DEFAULT_SYNCHRC;
		else
			path += File.separatorChar + SynchModule.DEFAULT_SYNCHRC;
		
		//cehck to see if synchrc exisits
		if(!FileCMD.existFile(path))
			FileCMD.writeFile(DEFAULT_RC_CONT, path);
	}
	
	private static void displayOptions()
	{
		Prin.t("Directory Syncher Version " + VERSION + "\n\n\t");
		Prin.t("Warning: The use of relative path names may result in undefined behavior.\n\t");
		Prin.t("\n\tUsage:\n\t\t" + PROG_NAME + " [primary directory] [secondary directory]\n\t\t");
		Prin.t(PROG_NAME + " [permissions] [primary directory] [secondary directory]\n\t\t");
		Prin.t(PROG_NAME + " -[flags] [primary directory] [secondary directory]\n\t\t");
		Prin.t(PROG_NAME + " -[flags] [permissions] [primary directory] [secondary directory]\n\t");
		Prin.t("Permissions:\n\t\t000 --> [<Read><Modify><Delete>] where 0 = false and 1 = true");
		Prin.t("\n\tFlags: \n\t\t-u : unidirectional (from primary to secondary)\n\t\t");
		Prin.t("-l : save log file\n\t\t-v : verbose (prints log to standard out)\n\t");
		Prin.t("End Flags:\n\t\t--rcPrim [custom synchrc file for primary directory]\n\t\t");
		Prin.t("--rcSec [custom synchrc file for secondary directory]\n");
		Prin.tln("\nNote: The default permissions are \"100.\"");
	}
	
	private static void saveLogFile(String path)
	{
		if(log.length() > initialLogLen && saveLog)
		{
			if(path.charAt(path.length()-1) == File.separatorChar)
				path = path.substring(0, path.length()-1);
			
			String fileName = "synchlog_" + (new Date(System.currentTimeMillis())).toString() + ".txt";
			fileName = fileName.replace(':', '_');
			String destination = path + File.separatorChar + "synchlogs" + File.separatorChar + fileName;
			
			if(!FileCMD.isDir(path + File.separatorChar + "synchlogs"))
				FileCMD.mkdirs(path + File.separatorChar + "synchlogs");
			
			FileCMD.writeFile(log, destination);
		}
	}
}*/
