import java.util.*;
import java.nio.file.attribute.FileTime;
import java.io.File;

import java.security.*;
import java.io.*;

public class UnitTest
{
	public static void main(String[] args)
	{
		//String[] list = {, , "/home/dan/Programming Projects/FileSynch/testDir/b", "/home/dan/Programming Projects/FileSynch/testDir/1.txt"};
		
		Prin.tln("" + compareHashes("/home/dan/Programming Projects/FileSynch/testDir/c", "/home/dan/Programming Projects/FileSynch/newDir/c"));
	}
	
	/**
	*Compares the MD5 hashes of two files and returns true if they match
	*@param first file
	*@param second file
	*@return true/false
	*
	private static boolean compareHashes(String file1, String file2)
	{
		boolean same = false;
		
		MessageDigest md_1;
		MessageDigest md_2;
		InputStream is_1;
		InputStream is_2;
		
		try
		{
			md_1 = MessageDigest.getInstance("MD5");
			md_2 = MessageDigest.getInstance("MD5");
			is_1 = new FileInputStream(file1);
			is_2 = new FileInputStream(file2);
			
			try
			{
			  is_1 = new DigestInputStream(is_1, md_1);
			  is_2 = new DigestInputStream(is_2, md_2);
			}
			catch(Exception e)
			{
				return same;
			}
			finally 
			{
			  is_1.close();
			  is_2.close();
			}
		}
		catch(Exception f)
		{
			return same;
		}
		
		byte[] digest_1 = md_1.digest();
		byte[] digest_2 = md_2.digest();
		
		for(int i = 0; i < digest_1.length; i++)
			Prin.t("," + digest_1[i]);
		Prin.ln();
		for(int i = 0; i < digest_2.length; i++)
			Prin.t("," + digest_2[i]);
		
		if(digest_1.length == digest_2.length)
		{
			for(int i = 0; i < digest_1.length; i++)
			{
				if(i == digest_1.length-1 && digest_1[i] == digest_2[i])
					same = true;
				else if(digest_1[i] != digest_2[i])
					break;
			}
		}
		
		return same;
	}*/
	
	private static boolean compareHashes(String file1, String file2)
	{
		boolean same = false;
		
		try
		{
		if(getMD5Checksum(file1).equals(getMD5Checksum(file2)))
			same = true;
		}
		catch(Exception e)
		{
			same = false;
		}
		
		return same;
	}
	
	public static byte[] createChecksum(String filename) throws Exception 
	{
       InputStream fis =  new FileInputStream(filename);

       byte[] buffer = new byte[1024];
       MessageDigest complete = MessageDigest.getInstance("MD5");
       int numRead;

       do 
	   {
           numRead = fis.read(buffer);
           if (numRead > 0) 
               complete.update(buffer, 0, numRead);
		   
       } while (numRead != -1);

       fis.close();
       return complete.digest();
	}

   // see this How-to for a faster way to convert
   // a byte array to a HEX string
   public static String getMD5Checksum(String filename) throws Exception 
   {
       byte[] b = createChecksum(filename);
       String result = "";

       for (int i=0; i < b.length; i++) {
           result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
       }
       return result;
   }
}
