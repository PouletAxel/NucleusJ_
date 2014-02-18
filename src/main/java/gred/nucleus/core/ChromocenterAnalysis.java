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
		  double [] tVolume =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
		  RadialDistance radialDistance = new RadialDistance();
		  IJ.log("CHROMOCENTER PARAMETERS");
		  double tBorderToBorderDistanceTable [] = radialDistance.computeBorderToBorderDistances(imagePlusBinary,imagePlusChromocenter);
		  double tBarycenterToBorderDistanceTable [] = radialDistance.computeBarycenterToBorderDistances (imagePlusBinary,imagePlusChromocenter);
		  for (int i = 0; i < tBorderToBorderDistanceTable.length;++i )
		  {
			  IJ.log("Titre Volume BorderToBorderDistance BarycenterToBorderDistanceTable");
			  IJ.log(imagePlusChromocenter.getTitle()+"_"+i+" "+tVolume[i]+" "+tBorderToBorderDistanceTable[i]+" "+tBarycenterToBorderDistanceTable[i]);
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
		    double [] tVolume =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
		    RadialDistance radialDistance = new RadialDistance();
		    double tBorderToBorderDistanceTable [] = radialDistance.computeBorderToBorderDistances(imagePlusBinary,imagePlusChromocenter);
			double tBarycenterToBorderDistanceTable [] = radialDistance.computeBarycenterToBorderDistances (imagePlusBinary,imagePlusChromocenter);
		    File fileResu = new File (pathFile);
		    FileWriter fw = new FileWriter(fileResu, true);
		    boolean exist = fileResu.exists();
		    BufferedWriter output;	
		    output = new BufferedWriter(fw);
		    if (exist == false) 
		    {
		    	output.write("Titre\tVolume\tBorderToBorderDistance\tBarycenterToBorderDistanceTable\n");
		    	for (int i = 0; i < tBorderToBorderDistanceTable.length;++i )
		    	{
		    		output.write(imagePlusChromocenter.getTitle()+"_"+i+"\t"+tVolume[i]+"\t"+tBorderToBorderDistanceTable[i]+"\t"+tBarycenterToBorderDistanceTable[i]+"\n");
		    	}
		    }
		    else
		    {
		    	for (int i = 0; i < tBorderToBorderDistanceTable.length;++i )
		    	{
		    		output.write(imagePlusChromocenter.getTitle()+"_"+i+"\t"+tVolume[i]+"\t"+tBorderToBorderDistanceTable[i]+"\t"+tBarycenterToBorderDistanceTable[i]+"\n");
		    	}
		    }
		    output.flush();
		    output.close();
	   }
}