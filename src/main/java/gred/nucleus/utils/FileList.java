package gred.nucleus.utils;
import ij.IJ;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author apoulet
 *
 */

public class FileList
{
	

     /**
      * 
      * @param repertoire
      */
     public FileList() {   }
     /**
      * 
      */
	public File[]  run (String repertoire){ 	return repertoryFileList( repertoire); }
	

	
	/**
	 * 
	 * @param repertoire
	 * @return
	 */
	public File[] repertoryFileList(String repertoire)
    {    
		 File directoryToScan = new File(repertoire);
	     File[] tFile = null;
	     tFile = directoryToScan.listFiles();
        for( int i = 0; i < tFile.length; ++i)
        {
            if (tFile[i].isDirectory())
            {
                File[] tempAvElement = recupFileAv(i,tFile);
                File[] tempApElement = recupFileAp(i,tFile);
                File[] fichiersTemp = repertoryFileList(tFile[i].toString());
                //si dossier n'est pas vide :
                if (fichiersTemp.length != 0)
                    tFile=this.redim(tempAvElement, tempApElement, fichiersTemp, i);
            }
        }
         return tFile;
    }
    /**
     * 
     * @param tempAvElement
     * @param tempApElement
     * @param fichiersTemp
     * @param indiceMax
     * @return
     */
    public File[] redim (File[] tempAvElement,File[] tempApElement,File[] fichiersTemp, int indiceMax)
    {
    	File [] tFile = new File[tempAvElement.length + fichiersTemp.length + tempApElement.length - 1];
         //insertion des elements dans la liste
         for (int j = 0; j < tFile.length; ++j)
         {
             //fichiers list� avant le dossier :
             if (j < indiceMax)
                 tFile[j] = tempAvElement[j];
             //fichier list� dans le dossier :
             else
             {
            	 if (j < indiceMax + fichiersTemp.length)
            		 tFile[j] = fichiersTemp[j - indiceMax];
            	 //fichiers list� apres le dossier :
            	 else
            		 tFile[j] = tempApElement[j - indiceMax - fichiersTemp.length];
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
    public File[] recupFileAv ( int indiceMax, File[] tFile)
    {
    		File[] tempAvElement = new File[indiceMax];
    		 for (int j = 0; j < indiceMax; ++j)
                 tempAvElement[j] = tFile[j];
    		 return tempAvElement;
    }
    
    /**
     * 	
     * @param indiceMax
     * @param tFile
     * @return
     */
    
    public File[] recupFileAp (int indiceMax, File[] tFile)
    {
    	File[] tempApElement = new File[tFile.length - indiceMax ];
        int j = 0;
    	for (int k = (indiceMax + 1); k < tFile.length; ++k)
    	{
               tempApElement[j] = tFile[k];
               ++j;
    	} 
        return tempApElement;
    }
    
   /**
    *  
    * @param filePathway
    * @return
    */
   	public boolean isInDirectory (String filePathway, File[] tableFile)
   {
   		boolean testFile = false;
       	for(int i = 0; i < tableFile.length; ++i)
		{
    		if(tableFile[i].toString().equals(filePathway)) {	testFile = true; break;}
    	}
       	return testFile;
    }
    
    /**
     * 
     * @param regex
     * @return
     */
    public String fileSearch (String regex,File[] tableFile)
    {
    	String file = null;
    	for(int i = 0; i < tableFile.length; ++i)
		{
    		if(tableFile[i].toString().matches((regex)))
   			{
   				file =tableFile[i].toString();
   				break;
   			}
		}
    	return file;
    }
    
    /**
     * 
     * @param regex
     * @return
     */
 	public boolean isDirectoryOrFileExist (String regex, File[] tableFile)
    {
    	boolean testFile = false;
    	IJ.log("taille "+ tableFile.length);
        for(int i = 0; i < tableFile.length; ++i)
 		{
        	if(tableFile[i].toString().matches((regex)))	{	testFile = true; break;}
     	}
        	return testFile;
     }
 	
    /**
     * 
     * @return
     */
    public String[] getDirectoryFiles (String repertoire, File[] tableFile)
    {
    	String [] ref = repertoire.split("\\"+File.separator);
    	String [] t = new String [0];
    	ArrayList <String> al = new ArrayList <String>();
    	HashMap<String, Integer> dir = new HashMap<String, Integer>();
    	for(int i = 0; i < tableFile.length; ++i)
		{
    		String [] temp = tableFile[i].toString().split ("\\"+File.separator);
    		if (temp.length > ref.length+1)
            {
    			if (!dir.containsKey(temp[ref.length]))
    			{
    				dir.put(temp[ref.length], 1);
    				al.add(temp[ref.length]);
    			}   				
   			}
		}
    	if (al.size() > 0)
    	{
    		t = new String [al.size()];
    		for(int i = 0; i < al.size(); ++i) t[i] = al.get(i);
    	}
    	
    	return t;
    }
    
    /**
     * 
     * @param regex
     * @return
     */
    
	public ArrayList<String> fileSearchList(String regex, File[] tableFile)
	{
		ArrayList<String> file = new ArrayList<String>();
       	for(int i = 0; i < tableFile.length; ++i)
		{
    		if(tableFile[i].toString().matches((regex))) file.add(tableFile[i].toString());
   	
    	}
    	return file;
	}   	
}