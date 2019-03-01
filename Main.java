/**
*Main class of the directory syncher
*Program is intended to be run exclusivley from commandline arguments
*
*@author Dan Martineau
*@version 1.0
*/

import java.io.File;

public class Main
{
	private static String log = "";
	
	private static final String PROG_NAME = "java synch";	//what one would use to call this program via commandline
	
	public static void main(String[] args)
	{
		log += "Beginning synch...\n";
		
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
				//customSynch();
				break;
			default:
				Prin.tln("Invalid arguments! Use --help for list of options.");
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
		Prin.t("Usage:\n\t" + PROG_NAME + " [primary directory] [secondary directory]\n\t");
		Prin.t(PROG_NAME + " [permissions] [primary directory] [secondary directory]\n\t");
		Prin.t(PROG_NAME + " -[flags] [primary directory] [secondary directory]\n\t");
		Prin.t(PROG_NAME + " -[flags] [permissions] [primary directory] [secondary directory]\n\t");
		Prin.t("\n\tPermissions:\n\t\t000 --> <Read><Modify><Delete> where 0 = false and 1 = true\n\t");
		Prin.t("\n\tFlags: \n\t\t-u : unidirectional (from primary to secondary)\n\t\t");
		Prin.t("-l : save log file\n\t");
		Prin.t("End Flags:\n\t\t--rcPrim [custom synchrc file for primary directory]\n\t\t");
		Prin.t("--rcSec [custom synchrc file for secondary directory]\n");
	}
	
	private static void checkSynchrc(String path)
	{
		final String DEFAULT_RC_CONT = "#This is your synchrc file\n\n#This line prevents your synchrc files from overwriting eachother across directories.\n000 testDir,synchrc";
		
		if(path.charAt(path.length()-1) == File.separatorChar)
			path += SynchModule.DEFAULT_SYNCHRC;
		else
			path += File.separatorChar + SynchModule.DEFAULT_SYNCHRC;
		
		//cehck to see if synchrc exisits
		if(!FileCMD.existFile(path))
			FileCMD.writeFile(DEFAULT_RC_CONT, path);
	}
}