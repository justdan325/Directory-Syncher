import java.io.*;
import java.util.ArrayList;

public class UnitTest
{
	public static void main(String[] args)
	{
		addWildcardMatches(args[0], true, true, true);
		//Prin.tln("" + match(args[0], args[1]));
	}
	
	//WILL HAVE TO BE CALLED EXCLUSIVLEY IN CONSTRUCTOR OF NODE
	public static void addWildcardMatches(String wild, boolean read, boolean modify, boolean delete) 
	{
		String rootPath;
		String restOfWild;
		boolean recursive = false;
		String[] filesAndDirs;
		String[] subDirs;
		
		if(wild.contains("*"))
			rootPath = wild.substring(0, wild.indexOf("*"));
		else
		{
			rootPath = wild;
			Prin.tln("Add: " + rootPath); //addNode(new Node(rootPath, read, modify, delete);
		}
		
		//Do not continue if the rootPath does not exist
		if(FileCMD.isDir(rootPath))
		{
			restOfWild = wild.substring(rootPath.length());
			
			filesAndDirs = null;
			
			//NullPointerException here means that user does not have access to a subdirectory. The error will be given in Synchmodule, so ignore here.
			try{filesAndDirs = FileCMD.listAll(rootPath);}
			catch(NullPointerException e){return;}
			
			if(wild.length() > wild.indexOf("*")+1)
				recursive = wild.charAt(wild.indexOf("*")+1) == '*';
			
			//get all files and subdirs in rootPath
			//run them through match
			//if they match, make them new Nodes and add them
			for(int i = 0; i < filesAndDirs.length; i++)
			{
				//Prin.tln(wild + " == " + filesAndDirs[i] + "?");
				//Prin.tln(match(wild, filesAndDirs[i]) + "");
				if(match(wild, filesAndDirs[i]))
					Prin.tln("Add: " + filesAndDirs[i]); //addNode(new Node(filesAndDirs[i], read, modify, delete);
			}
			
			//if recursive, search through all subdirectories
			if(recursive || restOfWild.contains(File.separatorChar + ""))
			{
				subDirs = FileCMD.listDirs(rootPath);
				
				for(int i = 0; i < subDirs.length; i++)
				{
					wild = subDirs[i] + restOfWild;
					/*if(restOfWild.equals(""))
						wild = subDirs[i] + "*";*/
					//Prin.tln("wild: " + wild);
					//IF restOfWild HAS NO WILD CARDS, DO STUFF AND ADD RESULT
					addWildcardMatches(wild, read, modify, delete);
				}
			}
		}
	}
	
	public static boolean match(String wild, String path)
	{
		ArrayList<Integer> locations = new ArrayList<Integer>();
		ArrayList<String> pieces = new ArrayList<String>();
		String subStr = wild;
		int index = -1;
		int prev = 0;
		int curr = -1;
		boolean match = false;
		
		if(wild.contains("*"))
		{
			wild.replace("**", "*");
			
			//find locations of all wild cards
			while(index < wild.length()-1)
			{
				subStr = wild.substring(index+1);
				index = subStr.indexOf("*");
				
				if(index == -1)
					break;
				
				index = (wild.length() - subStr.length()) + index;
				
				locations.add(index);
			}
			
			//chop wild into pieces
			for(int i = 0; i < locations.size(); i++)
			{
				prev = curr+1;
				curr = locations.get(i);
				
				if(!wild.substring(prev, curr).equals(""))
					pieces.add(wild.substring(prev, curr));
			}
			//add last piece
			if(curr < wild.length()-1)
			{
				pieces.add(wild.substring(curr+1));
			}
			
			//see if all of the pieces are contained within the path in the correct order
			curr = 0;
			for(int i = 0; i < pieces.size(); i++)
			{
				prev = curr;
				curr = prev + pieces.get(i).length();
				
				match = false;
				
				while(curr <= path.length() && !match)
				{
					if(curr == path.length() || (i == pieces.size()-1 && wild.charAt(wild.length()-1) != '*'))
					{
						//Prin.tln("|" + path.substring(prev) + "|");
						//Prin.tln("|" + pieces.get(i) + "|");
						if(path.substring(prev).equals(pieces.get(i)))
							match = true;
						else
							match = false;
					}
					else
					{
						//Prin.tln("|" + path.substring(prev, curr) + "|");
						//Prin.tln("|" + pieces.get(i) + "|");
						if(path.substring(prev, curr).equals(pieces.get(i)))
							match = true;
						else
							match = false;
					}
					
					//Prin.tln("" + match);
					
					prev++;
					curr++;
				}
				
				curr--;
				
				if(!match)
					break;
			}
		}
		else
		{
			match = wild.equals(path);
		}
		
		return match;
	}
}
