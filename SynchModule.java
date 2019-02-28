/**
*This is the mechanisim by which everything ultimatley gets synched
*This utility only synchs one way at a time i.e. from directory A to directory B
*It must be run twice to synch both ways
*
*@author Dan Martineau
*@version 1.0
*/


public class SynchModule
{
	/*FIELDS*/
	private boolean read;		//read (copy)  files in dir1 not in dir2 by default
	private boolean modify;	//modify files in dir2 not in dir1 by default
	private boolean delete;	//delete files in dir2 not in dir1 by default
	
	//private <DataType> synchrc
	private String log;			//log of synch
	private String dir1;			//canonical path of dir1
	private String dir2;			//canonical path of dir2
	
	/*CONSTANTS*/
	public String DEFAULT_SYNCHRC = "synchrc";
	
	/**
	*Constructor for a default synch job
	*@param dir1 path
	*@param dir2 path
	*@param synchrc file path
	*/
	public SynchModule(String path1, String path2, String rcname)
	{
		//assign parameters to their instance varibales and assert paths are valid
		dir1 = path1;
		assert FileCMD.isDir(path1) : "Path 1 is not a directory! This should be handled before SynchModule is called.";
		
		dir2 = path2;
		assert FileCMD.isDir(path1) : "Path 1 is not a directory! This should be handled before SynchModule is called.";
		
		read = true;
		modify = false;
		delete = false;
		
		log = "";
		
		//create synchrc object
		createSynch(rcname);
	}
	
	/**
	*Constructor for custom synch job
	*@param dir1 path
	*@param dir2 path
	*@param synchrc file path
	*@param read
	*@param modify
	*@param delete
	*/
	public SynchModule(String path1, String path2, String rcname, boolean read, boolean modify, boolean delete)
	{
		//assign parameters to their instance varibales and assert paths are valid
		dir1 = path1;
		assert FileCMD.isDir(path1) : "Path 1 is not a directory! This should be handled before SynchModule is called.";
		
		dir2 = path2;
		assert FileCMD.isDir(path1) : "Path 1 is not a directory! This should be handled before SynchModule is called.";
		
		this.read = read;
		this.modify = modify;
		this.delete = delete;
		
		log = "";
		
		//create synchrc object
		createSynch(rcname);
	}
	
	/*ACCESSORS*/
	
	/**
	*Returns copy of log
	*@return String copy of log
	*/
	public String getLog()
	{
		return new String(log);
	}
	
	/*******************/
	
	/**
	*Creates synch object
	*@param Synch file name
	*/
	private void createSynch(String name)
	{
		String filePath;
		
		//parse path to synchrc file
		if(path.charAt(path.size()-1) == File.pathSeparatorChar)
			filePath = dir1 + name;
		else
			filePath = dir1 + File.pathSeparatorChar + name;
		
		//assert file exisits
		assert FileCMD.existFile(filePath) : "Synchrc file does not exisit! Should be handled outside of SynchModule.";
		
//IMPLEMENT
		Prin.tln("Implement synchrc onject in createSynch() within SynchModule.");
		log += ("Synching " + dir1 + " --> " + dir2 " using \"" + name + "\"\n");
//***************
	}
	
	/**
	*Preform a synch job
	*@param primary directory
	*@param directory to synch to
	*/
	private void synchJob(String origin, String destination)
	{
		
	}
}