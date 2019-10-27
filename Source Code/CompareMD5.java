/**
*Class will make and compare the MD5 hashes of two files.
*private methods were taken from "Bill the Lizard" from stack overflow
*/

import java.security.*;
import java.io.*;

public class CompareMD5
{
	private static final int BUFFER_LEN = 1024;
	
	public static boolean compareHashes(String file1, String file2)
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
	
	private static byte[] createChecksum(String filename) throws Exception 
	{
	   InputStream fis =  new FileInputStream(filename);

	   byte[] buffer;
	   MessageDigest complete;
	   int numRead;
	   
	   buffer = new byte[BUFFER_LEN];
	   complete = MessageDigest.getInstance("MD5");

	   do 
	   {
		   numRead = fis.read(buffer);
		   if (numRead > 0) 
			   complete.update(buffer, 0, numRead);
		   
	   }while (numRead != -1);

	   fis.close();
	   return complete.digest();
	}

   private static String getMD5Checksum(String filename) throws Exception 
   {
	   byte[] b = createChecksum(filename);
	   String result = "";

	   for (int i=0; i < b.length; i++) {
		   result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
	   }
	   return result;
   }
}