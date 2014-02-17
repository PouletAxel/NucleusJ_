package gred.nucleus.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ij.IJ;
import ij.ImagePlus;

public class ChromocenterAnalysis
{
	
	  /**
	   * Constructor
	   * Recuperation of data and of images to determine the differents parameters
	   */

	   public ChromocenterAnalysis ()   {	   }
	   
	   /**
	    * 
	    * @param imagePlusBinary
	    * @param imagePlusChromocenter
	    */
	   public void computeParametersChromocenter (ImagePlus imagePlusBinary, ImagePlus imagePlusChromocenter)
	   {
		  Measure3D measure3D = new Measure3D();
		  double [] volume =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
		  RadialDistance radialDistance = new RadialDistance();
		  
		  IJ.log("CHROMOCENTER PARAMETERS");
		  double borderToBorderDistanceTable [] = radialDistance.computeBorderToBorderDistances(imagePlusChromocenter,imagePlusBinary);
		  double barycenterToBorderDistanceTable [] = radialDistance.computeBarycenterToBorderDistances (imagePlusBinary,imagePlusChromocenter);
		  for (int i = 0; i < borderToBorderDistanceTable.length;++i )
		  {
			  IJ.log("Titre Volume BorderToBorderDistance BarycenterToBorderDistanceTable");
			  IJ.log(imagePlusChromocenter.getTitle()+"_"+i+" "+volume[i]+" "+borderToBorderDistanceTable[i]+" "+barycenterToBorderDistanceTable[i]);
		  }
	   }
	   
	   /**
	    * 
	    * @param pathFile
	    * @throws IOException 
	    */
	   
	   public void computeParametersChromocenter (String pathFile,ImagePlus imagePlusBinary, ImagePlus imagePlusChromocenter) throws IOException
	   {
		   	Measure3D measure3D = new Measure3D();
		    double [] volume =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
		    RadialDistance radialDistance = new RadialDistance();
		    double borderToBorderDistanceTable [] = radialDistance.computeBorderToBorderDistances(imagePlusChromocenter,imagePlusBinary);
			double barycenterToBorderDistanceTable [] = radialDistance.computeBarycenterToBorderDistances (imagePlusBinary,imagePlusChromocenter);
		    File fileResu = new File (pathFile);
		    FileWriter fw = new FileWriter(fileResu, true);
		    boolean exist = fileResu.exists();
		    BufferedWriter output;	
		    output = new BufferedWriter(fw);
		    if (exist == false) 
		    {
		    	output.write("Titre\tVolume\tBorderToBorderDistance\tBarycenterToBorderDistanceTable\n");
		    	for (int i = 0; i < borderToBorderDistanceTable.length;++i )
		    	{
		    		output.write(imagePlusChromocenter.getTitle()+"_"+i+"\t"+volume[i]+"\t"+borderToBorderDistanceTable[i]+"\t"+barycenterToBorderDistanceTable[i]+"\n");
		    	}
		    }
		    else
		    {
		    	for (int i = 0; i < borderToBorderDistanceTable.length;++i )
		    	{
		    		output.write(imagePlusChromocenter.getTitle()+"_"+i+"\t"+volume[i]+"\t"+borderToBorderDistanceTable[i]+"\t"+barycenterToBorderDistanceTable[i]+"\n");
		    	}
		    }
		    output.flush();
		    output.close();
	   }
}
