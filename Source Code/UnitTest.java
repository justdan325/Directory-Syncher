import java.io.*;
import java.util.ArrayList;

public class UnitTest
{
	public static void main(String[] args)
	{
		Prin.tln("" + match(args[0], args[1]));
		
	}
	
	//WILL HAVE TO BE CALLED EXCLUSIVLEY IN CONSTRUCTOR OF NODE
	public static void addWildcardMatches(String wild)
	{
		String rootPath = wild.substring(0, wild.indexOf("*"));
		boolean recursive = wild.charAt(wild.indexOf("*")+1) == '*';
		
		//get all files and subdirs in rootPath
		//run them through match
		//if they match, make them new Nodes and add them
		//if recursive, search through all subdirectories
		
	}
	
	public static boolean match(String wild, String path)
	{
		assert wild.contains("*") : "\"wild\" passed into match() has no wild cards. This should be handeled prior to calling match().";
		
		ArrayList<Integer> locations = new ArrayList<Integer>();
		ArrayList<String> pieces = new ArrayList<String>();
		String subStr = wild;
		int index = -1;
		int prev = 0;
		int curr = -1;
		boolean match = false;
		
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
				if(curr == path.length())
				{
					if(path.substring(prev).equals(pieces.get(i)))
						match = true;
					else
						match = false;
					
					Prin.tln("" + match);
				}
				else
				{
					if(path.substring(prev, curr).equals(pieces.get(i)))
						match = true;
					else
						match = false;
				}
				
				prev++;
				curr++;
			}
			
			if(!match)
				break;
		}
		
		return match;
	}
}
