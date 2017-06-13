package gred.nucleus.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Poulet Axel
 * 
 * Several method on the file 
 *
 */
public class FileList
{
	boolean _windows = false; 
    
	/**
     * 
     */
	public FileList()
    {
    	_windows = System.getProperty("os.name").startsWith("Windows");
    }
    
    /**
     * run the methods to list all the file in one input directory
     *  
     * @param repertoire
     * @return Liste of file
    
     */
	public File[]  run (String repertoire){ 	return repertoryFileList( repertoire); }
	

	
	/**
	 * method to list all the file in one input directory
	 * 
	 * @param directory
	 * @return list file
	 */
	public File[] repertoryFileList(String directory)
    {    
		File directoryToScan = new File(directory);
		File[] tFileDirectory = null;
		tFileDirectory = directoryToScan.listFiles();
        for( int i = 0; i < tFileDirectory.length; ++i)
        {
        	if (tFileDirectory[i].isDirectory())
            {
                File[] tTempBeforeElement = stockFileBefore(i,tFileDirectory);
                File[] tTempAfterElement = stockFileAfter(i,tFileDirectory);
                File[] tTempFile = repertoryFileList(tFileDirectory[i].toString());
                if (tTempFile.length != 0) 
                	tFileDirectory=this.resize(tTempBeforeElement, tTempAfterElement, tTempFile, i);
            }
        }
         return tFileDirectory;
    }
	
    /**
     * methode to list on subdirectory
     * 
     * @param tTempBeforeElement
     * @param tTempAfterElement
     * @param tTempFile
     * @param indiceMax
     * @return
     */
    public File[] resize (File[] tTempBeforeElement,File[] tTempAfterElement,File[] tTempFile, int indiceMax)
    {
    	File [] tFile = new File[tTempBeforeElement.length + tTempFile.length + tTempAfterElement.length - 1];
         //element insertion in the file list
        for (int j = 0; j < tFile.length; ++j)
        {
        	//list file before the directory :
        	if (j < indiceMax)
        		tFile[j] = tTempBeforeElement[j];
        	//listed file in the directory :
        	else
        	{
        		if (j < indiceMax + tTempFile.length)
        			tFile[j] = tTempFile[j - indiceMax];
        		//listed files after directory :
        		else
        			tFile[j] = tTempAfterElement[j - indiceMax - tTempFile.length];
        	}
        }
	return tFile;  
    }
    
    /**
     * 
     * @param indiceMax
     * @param tFile
     * @return
     */
    public File[] stockFileBefore ( int indiceMax, File[] tFile)
    {
    		File[] tTempBeforeElement = new File[indiceMax];
    		 for (int j = 0; j < indiceMax; ++j)
    			 tTempBeforeElement[j] = tFile[j];
    		 return tTempBeforeElement;
    }
    
    /**
     * 	
     * @param indiceMax
     * @param tFile
     * @return
     */
    public File[] stockFileAfter (int indiceMax, File[] tFile)
    {
    	File[] tTempAfterElement = new File[tFile.length - indiceMax ];
        int j = 0;
    	for (int k = (indiceMax + 1); k < tFile.length; ++k)
    	{
    		tTempAfterElement[j] = tFile[k];
               ++j;
    	} 
        return tTempAfterElement;
    }
    
   /**
    * 
    * @param filePathway
    * @param tableFile
    * @return
    */
   	public boolean isInDirectory (String filePathway, File[] tableFile)
   {
   		boolean testFile = false;
       	for(int i = 0; i < tableFile.length; ++i)
		{
    		if(tableFile[i].toString().equals(filePathway))
    		{
    			testFile = true;
    			break;
    		}
    	}
       	return testFile;
    }
    
    /**
     * 
     * @param regex
     * @param tFile
     * @return
     */
    public String fileSearch (String regex,File[] tFile)
    {
    	if (_windows)
    	{
    		String as = new String ("\\"); 
    		String das = new String("\\\\"); 
    		regex =regex.replace(as, das);
    	}
    	String file = null;
    	for(int i = 0; i < tFile.length; ++i)
		{
    		if(tFile[i].toString().matches((regex)))
   			{
   				file =tFile[i].toString();
   				break;
   			}
		}
    	return file;
    }
    

    /**
     * 
     * @param regex
     * @param tFile
     * @return
     */
 	public boolean isDirectoryOrFileExist (String regex, File[] tFile)
    {
 		if (_windows)
    	{
    		String as = new String ("\\"); 
    		String das = new String("\\\\"); 
    		regex =regex.replace(as, das);
    	}
    	boolean testFile = false;
        for(int i = 0; i < tFile.length; ++i)
 		{
        	if(tFile[i].toString().matches((regex)))
        	{
        		testFile = true;
        		break;
        	}
     	}
        	return testFile;
     }
 	
    
 	/**
 	 * 
 	 * @param directory
 	 * @param tFile
 	 * @return
 	 */
    public String[] getDirectoryFiles (String directory, File[] tFile)
    {
    	String [] tRef = directory.split("\\"+File.separator);
    	String [] tTemp = new String [0];
    	ArrayList <String> arrayList = new ArrayList <String>();
    	HashMap<String, Integer> hasMapDirectory = new HashMap<String, Integer>();
    	for(int i = 0; i < tFile.length; ++i)
		{
    		String [] temp = tFile[i].toString().split ("\\"+File.separator);
    		if (temp.length > tRef.length+1)
            {
    			if (!hasMapDirectory.containsKey(temp[tRef.length]))
    			{
    				hasMapDirectory.put(temp[tRef.length], 1);
    				arrayList.add(temp[tRef.length]);
    			}   				
   			}
		}
    	if (arrayList.size() > 0)
    	{
    		tTemp = new String [arrayList.size()];
    		for(int i = 0; i < arrayList.size(); ++i)
    			tTemp[i] = arrayList.get(i);
    	}
    	return tTemp;
    }
    

    /**
     * 
     * @param regex
     * @param tFile
     * @return
     */
	public ArrayList<String> fileSearchList(String regex, File[] tFile)
	{
		if (_windows)
    	{
    		String as = new String ("\\"); 
    		String das = new String("\\\\"); 
    		regex =regex.replace(as, das);
    	}
		ArrayList<String> arrayListFile = new ArrayList<String>();
       	for(int i = 0; i < tFile.length; ++i)
    		if(tFile[i].toString().matches((regex)))
    			arrayListFile.add(tFile[i].toString());
   	
    	return arrayListFile;
	}   	
}