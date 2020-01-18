/**
*Main class of the directory syncher
*Program is intended to be run exclusivley from commandline arguments
*
*@author Dan Martineau
*@version 3.2
*/

import java.util.regex.*;
import java.io.File;
import java.util.Date;

public class Main
{
	private static boolean read;
	private static boolean mod;
	private static boolean del;
	private static boolean verbose;
	private static boolean unidirectional;
	private static boolean saveLog;
	private static boolean safe;
	private static int initialLogLen;
	private static String dir1;
	private static String dir2;
	private static String log;
	private static String rcPrim;
	private static String rcSec;
	private static Status status;
	private static Synchrc synchrc1;
	private static Synchrc synchrc2;
	
	/*REGEXES*/
	private static final String REGEX_PERM   = "[0-1]{3}";
	private static final String REGEX_FLAG   = "[-]{1}[vuls]{1,4}";
	private static final String REGEX_CUSTRC = "[-]{2}[rcPrimSe]{5,6}";
	
	/*CONSTANTS*/
	private static final double VERSION 	= 2.0;
	private static final String PREAMBLE 	= "Directory Syncher Version " + VERSION + "\nMade and maintaned by Dan Martineau.\n\n";
	private static final String PROG_NAME 	= "java -jar synch.jar";	//what one would use to call this program via commandline
	
	/*CANNOT BE LOCAL--ALTERED WITHIN SEPERATE THREADS*/
	private static boolean aFin = false;
	private static boolean bFin = false;
	private static FilesToProcess noHoldA = null;
	private static FilesToProcess noHoldB = null;
	
	public static void main(String[] args) throws Exception
	{
		read = true;
		mod = false;
		del = false;
		verbose = false;
		unidirectional = false;
		saveLog = false;
		safe = false;
		rcPrim = Synchrc.DEFAULT_SYNCHRC;
		rcSec = Synchrc.DEFAULT_SYNCHRC;
		
		
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
		
		//parse synchrc files
		Prin.ln();
		if(verbose)
			Prin.t("\nParsing " + rcPrim + ". . .");
		synchrc1 = createSynchrc(rcPrim, dir1, synchrc1);
		
		Prin.clearCurrLine();
		if(verbose)
			Prin.t("Parsing " + rcSec + ". . .");
		
		synchrc2 = createSynchrc(rcSec, dir2, synchrc2);
		
		Prin.clearCurrLine();
		
		//begin synch job
		
		status = Status.getStatus();
		status.setJob(dir1 + " --> " + dir2);
		
		if(read || mod || del)
		{
			if(verbose)
				Prin.t("Preparing to run job . . .");
			
			//count files to process in parallel
			new Thread(){ public void run() { noHoldA = new FilesToProcess(dir1, dir2, synchrc1, read, mod, del); aFin = true; } }.start();
			new Thread(){ public void run() { noHoldB = new FilesToProcess(dir2, dir1, synchrc2, read, mod, del); bFin = true; } }.start();
			
			while(!(aFin && bFin))
				Prin.spinner();
			
			if(verbose)
				Prin.clearCurrLine();
			
			if(unidirectional)
				status.setTotal(noHoldA.getNum());
			else
				status.setTotal(noHoldA.getNum() + noHoldB.getNum());
		}
		else
			status.setTotal((FileCMD.listAll(dir1).length + FileCMD.listAll(dir2).length) * 2);
		
		//synch from primary to secondary
		try
		{
			SynchModule part1 = new SynchModule(dir1, dir2, synchrc1, read, mod, del, verbose, safe);
		
			log += part1.getLog();
			
			//synch from secondary to primary if not unidirectional
			if(!unidirectional)
			{
				if(read || mod || del)
				{	//noHoldA = new FilesToProcess(dir1, dir2, rcPrim, read, mod, del);
					noHoldB = new FilesToProcess(dir2, dir1, synchrc2, read, mod, del);
					status.setTotal(noHoldA.getNum() + noHoldB.getNum());
				}
				
				//reset some status values
				status.setJob(dir2 + " --> " + dir1);
				
				SynchModule part2 = new SynchModule(dir2, dir1, synchrc2, read, mod, del, verbose, safe);
				
				log += part2.getLog();
			}
		}
		catch(Exception e)
		{
			assert e == null : Prin.getStackTrace(e);
			Prin.err("\n" + e.toString() + "\n");
			error();
		}
		
		//kill status so that the program can eventually terminate
		status.kill();
		
		//clear verbose output
		if(verbose)
			Prin.clearAll();
		
		//if del enabled, check for files in trashes to delete
		if(del)
			checkForDelFiles();
		
		log += "\nFinished synch job!";
		
		if(verbose)
			Prin.tln(log);
		
		saveLogFile(dir1);
		saveLogFile(dir2);
	}
	
	/**
	*Set the permissions
	*@param permissions string 
	*/
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
		
		//if part2 doesn't exist and not in safe mode
		if(!(FileCMD.isDir(path2) || safe))
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
		else if(!FileCMD.isDir(path2))
		{
			Prin.err("Secondary directory does not exist!\n");
			log += "Secondary directory does not exist! Cannot create in safe mode. Ending synch...\n";
			error();
		}
		
		dir1 = FileCMD.getCanonPath(path1);
		dir2 = FileCMD.getCanonPath(path2);
		
		//Do not run if the dirs are the same.
		if(dir1.equals(dir2))
		{
			Prin.err("Primary and secondary directories are identical!\n");
			log += "Primary and secondary directories are identical! Ending synch...\n";
			error();
		}
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
					case 's':
						safe = true;
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
				rcPrim = FileCMD.getCanonPath(str2);
				break;
			case "--rcSec":
				rcSec = FileCMD.getCanonPath(str2);
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
		boolean written = false;
		final String PARENT_DIR = FileCMD.getName(path);
		final String DEFAULT_RC_CONT = "#This is your synchrc file\n\n#This line prevents your synchrc files from overwriting eachother across directories.\n000 " 
			+ PARENT_DIR + ",synchrc\n\n#The following two lines are essential when running a job on the parent directory of a drive in Windows.\n000 " 
			+ PARENT_DIR 
			+ ",System Volume Information\n000 " 
			+ PARENT_DIR 
			+ ",$RECYCLE.BIN\n\n#This line prevents deleted files from copying across directories.\n000 "
			+ PARENT_DIR + ","
			+ SynchModule.DEFAULT_TRASH;
		
		if(path.charAt(path.length()-1) == File.separatorChar)
			path += Synchrc.DEFAULT_SYNCHRC;
		else
			path += File.separatorChar + Synchrc.DEFAULT_SYNCHRC;
		
		//cehck to see if synchrc exisits
		if(!FileCMD.existFile(path))
			written = FileCMD.writeFile(DEFAULT_RC_CONT, path);
		else
			written = true;
		
		//terminate program if synchrc could not be created
		if(!written)
		{
			Prin.err("Could not create new synchrc file! Check write permissions and try again.\n");
			error();
		}
	}
	
	/**
	*Creates synchrc object
	*@param Synchrc file name
	*/
	private static Synchrc createSynchrc(String name, String rcDir, Synchrc synchrc)
	{
		String filePath;
		String err;
		
		//parse name to synchrc file
		if(name.equals(Synchrc.DEFAULT_SYNCHRC))
			filePath = rcDir + File.separatorChar + name;
		else
			filePath = name;
		
		//assert file exisits
		assert FileCMD.existFile(filePath) : "Synchrc file: " + filePath + " does not exist! Should be handled outside of SynchModule.";
		
		//instantiate synchrc
		synchrc = new Synchrc(filePath, rcDir, log, verbose);
		
		//get log from new Synchrc 
		log = synchrc.getLog();
		
		//get error
		err = synchrc.getError();
		
		if(err != null)
		{
			Prin.err("\n" + err + "\n");
			log += "\n" + err + "\n\n";
		}
		
		return synchrc;
	}
	
	private static void displayOptions()
	{
		Prin.t("Directory Syncher Version " + VERSION + "\n\t");
		Prin.t("\n\tUsage:\n\t\t" + PROG_NAME + " [primary directory] [secondary directory]\n\t\t");
		Prin.t(PROG_NAME + " [permissions] [primary directory] [secondary directory]\n\t\t");
		Prin.t(PROG_NAME + " -[flags] [primary directory] [secondary directory]\n\t\t");
		Prin.t(PROG_NAME + " -[flags] [permissions] [primary directory] [secondary directory]\n\t");
		Prin.t("Permissions:\n\t\t000 --> [<Read><Modify><Delete>] where 0 = false and 1 = true");
		Prin.t("\n\tFlags: \n\t\t-u : unidirectional (from primary to secondary)\n\t\t");
		Prin.t("-l : save log file\n\t\t-v : verbose (prints log to standard out)\n\t\t-s : safe mode\n\t");
		Prin.t("End Flags:\n\t\t--rcPrim [custom synchrc file for primary directory]\n\t\t");
		Prin.t("--rcSec [custom synchrc file for secondary directory]\n");
		Prin.tln("\nNote: The default permissions are \"100.\"");
	}
	
	private static void checkForDelFiles()
	{
		String trash1 = dir1 + File.separatorChar + SynchModule.DEFAULT_TRASH;
		String trash2 = dir2 + File.separatorChar + SynchModule.DEFAULT_TRASH;
		String[] list;
		boolean deleteTrash = false;
		
		if(FileCMD.isDir(trash1))
		{
			Prin.clearAll();
			
			list = FileCMD.listAll(trash1);
			for(int i = 0; i < list.length; i++)
				Prin.tln(FileCMD.getName(list[i]));
			
			deleteTrash = Prin.yesOrNo("\nPermanently delete the above " + list.length + " file(s) from " + trash1 + "?");
			
			if(deleteTrash)
				FileCMD.deleteDir(trash1);
			
			Prin.clearAll();
		}
		
		if(FileCMD.isDir(trash2))
		{
			Prin.clearAll();
			
			list = FileCMD.listAll(trash2);
			for(int i = 0; i < list.length; i++)
				Prin.tln(FileCMD.getName(list[i]));
			
			deleteTrash = Prin.yesOrNo("\nPermanently delete the above " + list.length + " file(s) from " + trash2 + "?");
			
			if(deleteTrash)
				FileCMD.deleteDir(trash2);
			
			Prin.clearAll();
		}
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
}
