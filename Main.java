/**
*Main class of the directory syncher
*Program is intended to be run exclusivley from commandline arguments
*
*@author Dan Martineau
*@version 1.1
*/

import java.io.File;
import java.util.Date;

public class Main
{
	private static String log;
	private static boolean verbose;
	
	private static final double VERSION 	= 0.6;
	private static final String PREAMBLE 	= "Directory Syncher Version " + VERSION + "\nMade and Maintaned by Dan Martineau.\n\n";
	private static final String PROG_NAME 	= "java -jar synch.jar";	//what one would use to call this program via commandline
	
	public static void main(String[] args)
	{
		log = PREAMBLE + PROG_NAME + " ";
		
		for(int i = 0; i < args.length; i++)
			log += args[i] + " ";
		
		log += "\n\nBeginning synch...\n";
		verbose = false;
		
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
					Prin.tln("Invalid arguments! Use --help for list of options.");
				break;
			case 2:
				defaultSynch(args);
				break;
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				customSynch(args);
				break;
			default:
				Prin.tln("Invalid arguments! Use --help for list of options.");
		}
	}
	
	private static void customSynch(String[] args)
	{
		boolean read 		= true;
		boolean modify 		= false;
		boolean delete 		= false;
		boolean unidirectional 	= false;
		boolean saveLog 	= false;
		String primrc 		= SynchModule.DEFAULT_SYNCHRC;
		String secrc 		= SynchModule.DEFAULT_SYNCHRC;
		String dir1 		= null;
		String dir2 		= null;
		
		//if first has dash, then it is the flags
		if(args[0].contains("-"))
		{
			for(int i = 0; i < args[0].length(); i++)
			{
				switch(args[0].charAt(i))
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
						Prin.tln("Invalid flag series: " + args[0] + "\nRun with --help for list of flags.");
						System.exit(-1);
				}
			}
			
			//see if next of args is permissions
			if((args[1].contains("0") || args[1].contains("1")) && args[1].length() == 3 && !FileCMD.isDir(args[1]) && args.length >= 4)
			{
				if(args[1].charAt(0) == '0')
					read = false;
				else if(args[1].charAt(0) == '1')
					read = true;
				else
				{
					Prin.tln("Invalid permissions entered: " + args[1]);
					System.exit(-1);
				}
				
				if(args[1].charAt(1) == '0')
					modify = false;
				else if(args[1].charAt(1) == '1')
					modify = true;
				else
				{
					Prin.tln("Invalid permissions entered: " + args[1]);
					System.exit(-1);
				}
				
				if(args[1].charAt(2) == '0')
					delete = false;
				else if(args[1].charAt(2) == '1')
					delete = true;
				else
				{
					Prin.tln("Invalid permissions entered: " + args[1]);
					System.exit(-1);
				}
				
				//check to see if next two args are dirs
				if(!FileCMD.isDir(args[2]))
				{
					Prin.tln("Invalid primary directory: " + args[2]);
					System.exit(-1);
				}
				//test to see if secondary directory exists
				if(!FileCMD.existFile(args[3]))
				{
					log += "Creating secondary directory: " + args[3] + "\n";
					
					//attempt to make secondary directory
					boolean attempt = FileCMD.mkdirs(args[3]);
					
					if(attempt)
						log += "Directory was successfully created!\n";
					//exit if unsuccessful
					else
					{
						log += "Failed to create directory. Ending synch...\n";
						Prin.tln("Could not create secondary directory.");
						System.exit(-1);
					}
				}
				
				dir1 = args[2];
				dir2 = args[3];
			}
			else
			{
				//check to see if next two args are dirs
				if(!FileCMD.isDir(args[1]))
				{
					Prin.tln("Invalid primary directory: " + args[1]);
					System.exit(-1);
				}
				//test to see if secondary directory exists
				if(!FileCMD.existFile(args[2]))
				{
					log += "Creating secondary directory: " + args[2] + "\n";
					
					//attempt to make secondary directory
					boolean attempt = FileCMD.mkdirs(args[2]);
					
					if(attempt)
						log += "Directory was successfully created!\n";
					//exit if unsuccessful
					else
					{
						log += "Failed to create directory. Ending synch...\n";
						Prin.tln("Could not create secondary directory.");
						System.exit(-1);
					}
				}
				
				dir1 = args[1];
				dir2 = args[2];
			}
		}
		//if first has zeros or ones and a length of three, then it is permissions
		else if((args[0].contains("0") || args[0].contains("1")) && args[0].length() == 3 && !FileCMD.isDir(args[0]))
		{
			if(args[0].charAt(0) == '0')
					read = false;
			else if(args[0].charAt(0) == '1')
				read = true;
			else
			{
				Prin.tln("Invalid permissions entered: " + args[0]);
				System.exit(-1);
			}
			
			if(args[0].charAt(1) == '0')
				modify = false;
			else if(args[0].charAt(1) == '1')
				modify = true;
			else
			{
				Prin.tln("Invalid permissions entered: " + args[0]);
				System.exit(-1);
			}
			
			if(args[0].charAt(2) == '0')
				delete = false;
			else if(args[0].charAt(2) == '1')
				delete = true;
			else
			{
				Prin.tln("Invalid permissions entered: " + args[0]);
				System.exit(-1);
			}
			
			//check to see if next two args are dirs
			if(!FileCMD.isDir(args[1]))
			{
				Prin.tln("Invalid primary directory: " + args[1]);
				System.exit(-1);
			}
			//test to see if secondary directory exists
			if(!FileCMD.existFile(args[2]))
			{
				log += "Creating secondary directory: " + args[2] + "\n";
				
				//attempt to make secondary directory
				boolean attempt = FileCMD.mkdirs(args[2]);
				
				if(attempt)
					log += "Directory was successfully created!\n";
				//exit if unsuccessful
				else
				{
					log += "Failed to create directory. Ending synch...\n";
					Prin.tln("Could not create secondary directory.");
					System.exit(-1);
				}
			}
			
			dir1 = args[1];
			dir2 = args[2];
		}
		//else it must be a default synch with extra flags
		else
		{
			if(!FileCMD.isDir(args[0]))
			{
				Prin.tln("Invalid primary directory: " + args[0]);
				System.exit(-1);
			}
			//test to see if secondary directory exists
			if(!FileCMD.existFile(args[1]))
			{
				log += "Creating secondary directory: " + args[1] + "\n";
				
				//attempt to make secondary directory
				boolean attempt = FileCMD.mkdirs(args[1]);
				
				if(attempt)
					log += "Directory was successfully created!\n";
				//exit if unsuccessful
				else
				{
					log += "Failed to create directory. Ending synch...\n";
					Prin.tln("Could not create secondary directory.");
					System.exit(-1);
				}
			}
			
			dir1 = args[0];
			dir2 = args[1];
		}
		
		//handle the final args to see if they are extra flags
		if(args.length >= 4 && args[args.length-2].equals("--rcPrim"))
		{
			//make sure file exisits
			if(!FileCMD.existFile(args[args.length-1]))
			{
				Prin.tln("Custom synchrc file \"" + args[args.length-1] + "\" does not exist.");
				System.exit(-1);
			}
			else
				primrc = args[args.length-1];
		}
		else if(args.length >= 4 && args[args.length-2].equals("--rcSec"))
		{
			//make sure file exisits
			if(!FileCMD.existFile(args[args.length-1]))
			{
				Prin.tln("Custom synchrc file \"" + args[args.length-1] + "\" does not exist.");
				System.exit(-1);
			}
			else
				secrc = args[args.length-1];
		}
		
		if(args.length >= 6 && args[args.length-4].equals("--rcPrim"))
		{
			//make sure file exisits
			if(!FileCMD.existFile(args[args.length-3]))
			{
				Prin.tln("Custom synchrc file \"" + args[args.length-3] + "\" does not exist.");
				System.exit(-1);
			}
			else
				primrc = args[args.length-3];
		}
		else if(args.length >= 6 && args[args.length-4].equals("--rcSec"))
		{
			//make sure file exisits
			if(!FileCMD.existFile(args[args.length-3]))
			{
				Prin.tln("Custom synchrc file \"" + args[args.length-3] + "\" does not exist.");
				System.exit(-1);
			}
			else
				secrc = args[args.length-3];
		}
		
		//check to ensure that the rc files exist
		checkSynchrc(dir1);
		checkSynchrc(dir2);
		
		//begin synch job
		
		//synch from primary to secondary
		SynchModule part1 = new SynchModule(dir1, dir2, primrc, read, modify, delete, verbose);
		
		log += part1.getLog();
		
		//synch from secondary to primary if not unidirectional
		if(!unidirectional)
		{
			SynchModule part2 = new SynchModule(dir2, dir1, secrc, read, modify, delete, verbose);
			
			log += part2.getLog() + "\nFinished synch job!";
		}
		
		if(verbose)
			Prin.tln("\n" + log);
		
		if(saveLog)
		{
			saveLogFile(dir1);
			saveLogFile(dir2);
		}
	}
	
	private static void defaultSynch(String[] args)
	{
		//test to make sure primary exisits
		if(!FileCMD.existFile(args[0]))
		{
			Prin.tln("Primary directory \"" + args[0] + "\" does not exist.");
			System.exit(-1);
		}
		
		//test to see if secondary directory exists
		if(!FileCMD.existFile(args[1]))
		{
			log += "Creating secondary directory: " + args[1] + "\n";
			
			//attempt to make secondary directory
			boolean attempt = FileCMD.mkdirs(args[1]);
			
			if(attempt)
				log += "Directory was successfully created!\n";
			//exit if unsuccessful
			else
			{
				log += "Failed to create directory. Ending synch...\n";
				Prin.tln("Could not create secondary directory.");
				System.exit(-1);
			}
		}
		
		//ensure that both dirs have synchrc files
		checkSynchrc(args[0]);
		checkSynchrc(args[1]);
		
		//synch from primary to secondary
		SynchModule part1 = new SynchModule(args[0], args[1], SynchModule.DEFAULT_SYNCHRC);
		
		log += part1.getLog();
		
		//synch from secondary to primary
		SynchModule part2 = new SynchModule(args[1], args[0], SynchModule.DEFAULT_SYNCHRC);
		
		log += part2.getLog() + "\nFinished synch job!";
		
		Prin.tln(log);
	}
	
	private static void displayOptions()
	{
		Prin.t("Directory Syncher Version " + VERSION + " Usage:\n\t" + PROG_NAME + " [primary directory] [secondary directory]\n\t");
		Prin.t(PROG_NAME + " [permissions] [primary directory] [secondary directory]\n\t");
		Prin.t(PROG_NAME + " -[flags] [primary directory] [secondary directory]\n\t");
		Prin.t(PROG_NAME + " -[flags] [permissions] [primary directory] [secondary directory]\n\t");
		Prin.t("\n\tPermissions:\n\t\t000 --> [<Read><Modify><Delete>] where 0 = false and 1 = true");
		Prin.t("\n\tFlags: \n\t\t-u : unidirectional (from primary to secondary)\n\t\t");
		Prin.t("-l : save log file\n\t\t-v : verbose (prints log to std out)\n\t");
		Prin.t("End Flags:\n\t\t--rcPrim [custom synchrc file for primary directory]\n\t\t");
		Prin.t("--rcSec [custom synchrc file for secondary directory]\n");
		Prin.tln("Note: The default permissions are \"100\" unless specified.");
	}
	
	private static void checkSynchrc(String path)
	{
		final String DEFAULT_RC_CONT = "#This is your synchrc file\n\n#This line prevents your synchrc files from overwriting eachother across directories.\n000 " + FileCMD.getName(path) + ",synchrc";
		
		if(path.charAt(path.length()-1) == File.separatorChar)
			path += SynchModule.DEFAULT_SYNCHRC;
		else
			path += File.separatorChar + SynchModule.DEFAULT_SYNCHRC;
		
		//cehck to see if synchrc exisits
		if(!FileCMD.existFile(path))
			FileCMD.writeFile(DEFAULT_RC_CONT, path);
	}

	private static void saveLogFile(String path)
	{
		if(path.charAt(path.length()-1) == File.separatorChar)
			path = path.substring(0, path.length()-1);
		
		String fileName = "synchlog_" + (new Date(System.currentTimeMillis())).toString() + ".txt";
		String destination = path + File.separatorChar + "synchlogs" + File.separatorChar + fileName;
		
		if(!FileCMD.isDir(path + File.separatorChar + "synchlogs"))
			FileCMD.mkdirs(path + File.separatorChar + "synchlogs");
		
		FileCMD.writeFile(log, destination);
	}
}
