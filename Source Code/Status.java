/**
*Class is a singleton that manages the status of the state of SynchModule when running a job.
*
*@author Dan Martineau
*@version 1.6
*@since 2.0
*/

public class Status
{
	private static String job;			//Running job from dir a to dir b
	private static String mode;			//Read/Mod/Delete 
	private static String file;			//current file being processed
	private static String dir;			//current directory
	private static String progress;		//ASCII progress meter
	private static int total;			//total number of files in all subdirs beneath parent dir
	private static int curr;				//number of current file being processed
	private static boolean printOnUpdate; 	//true if status should print itself every time certain mutators are called
	private static boolean initialized;	//true if there is an instance of Status
	private static boolean print;			//true if program should be printing the status to std out continuously
	private static boolean alive;			//true if this instance should remain alive
	private static Status status;			//instance of this class
		
	/*PUBLIC CONSTANTS*/
	public static final String MODE_READ = "Searching for match";
	public static final String MODE_MOD = "Checking for modifications";
	public static final String MODE_DEL = "Assesing deletion";
	public static final String MODE_IDLE = "Idle";
	
	/*PRIVATE CONSTANTS*/
	private static final int DELAY = 15;
	private static final int PROG_LEN = 30;
	private static final String PROG_BLANK = "-";
	private static final String PROG_FILL = "=";
	
	private Status()
	{
		total = 0;
		printOnUpdate = false;
		initialized = true;
		print = false;
		alive = true;
		
		setIdle();
		
		progress();
		
		printMode();
	}
	
	/*ACCESSORS*/
	
	/**
	*Returns the single instance of this class
	*@return this
	*/
	public static Status getStatus()
	{
		if(!initialized)
			status = new Status();
		
		return status;
	}
	
	/*MUTATORS*/
	
	/**
	*Set the current directory being processed as well as the number of files/dirs inside of it.
	*@param directory
	*@param total files inside
	*/
	public void setDir(String dir)
	{
		if(!this.dir.equals(dir))
			this.dir = dir;
	}
	
	/**
	*Set current file being processed.
	*@param file name (not path)
	*/
	public void setFile(String file)
	{
		this.file = file;
		setCurr();
	}
	
	/**
	*Incriment curr
	*/
	public void setCurr()
	{
		curr++;
	}
	
	/**
	*Set the total number of files and subdirectories to be processed (include parent dir)
	*@param total files and subdirs
	*/
	public void setTotal(int total)
	{
		this.total = total;
	}
	
	/**
	*Increase/decrease total
	*@param amount to add/subtract from total
	*/
	public static void addToTotal(int amnt)
	{
		total += amnt;
	}
	
	/**
	*Set the job being run
	*@the job
	*/
	public void setJob(String job)
	{
		this.job = job;
	}
	
	/**
	*Set the processing mode.
	*@param processing mode (must be one of the constants)
	*/
	public void setMode(String newMode)
	{
		switch(newMode)
		{
			case MODE_READ:
			case MODE_MOD:
			case MODE_DEL:
			case MODE_IDLE:
				this.mode = newMode;
				break;
			default:
				throw new RuntimeException("Invalid mode passed into setMode(): " + newMode);
		}
	}
	
	/**
	*Sets the status indicator to idle and resets counters--do not call in middle of a job!
	*/
	public void setIdle()
	{
		job = "Not running. . .";
		mode = MODE_IDLE;
		file = "--";
		dir = "--";
		curr = 0;
	}
	
	private void progress()
	{
		assert total >= curr : "total < curr in Status! total: " + total + " curr: " + curr;
		
		if(total > 0)
		{
			int fillNo = (int)(PROG_LEN*(((double)curr/total)));
			
			progress = "[";
			
			for(int i = 0; i < fillNo; i++)
				progress += PROG_FILL;
			
			for(int i = 0; i < PROG_LEN-fillNo; i++)
				progress += PROG_BLANK;
			
			progress += "] " + curr + " out of " + total;
		}
		else
		{
			progress = "[";
			
			for(int i = 0; i < PROG_LEN; i++)
				progress += PROG_BLANK;
			
			progress += "] " + curr + " out of " + total;
		}
	}
	
	/**
	*Call to print the status to std out
	*@param true or false
	*/
	public void print()
	{
		Prin.clearAll();
		Prin.tln(toString());
	}
	
	/**
	*Set true to continously print the status to std out.
	*@param true or false
	*/
	public void setPrint(boolean print)
	{
		this.print = print;
	}
	
	/**
	*Call to kill the print thread and release the instance of status
	*/
	public void kill()
	{
		alive = false;
		status = null;
		System.gc();
	}
	
	private void printMode()
	{
		new Thread()
		{
			public void run() {
				String prev = "";
				
				while(alive)
				{
					while(print)
					{
						progress();
						
						if(!prev.equals(progress))
							print();
						
						prev = progress;
						Prin.pause(DELAY);
					}
					Prin.pause(50);
				}
			}
		}.start();
	}
	
	@Override
	public String toString()
	{
		String str = "";
		
		if(initialized)
		{
			str += "Job: " + job + "\n";
			str += "Directory: " + dir + "\n";
			str += "File: " + file + "\n";
			str += "Mode: " + mode + "\n\n";
			str += "Progress: " + progress + "\n";
		}
		
		return str;
	}
}