package gred.nucleus.core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ij.IJ;
import ij.ImagePlus;

/**
 * 
 * @author Poulet Axel
 *
 */
public class ChromocenterAnalysis
{
	   public ChromocenterAnalysis ()   {	   }
	   
	   /**
	    * 
	    * @param imagePlusSegmented
	    * @param imagePlusChromocenter
	    */
	   public void computeParametersChromocenter (ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter)
	   {
		  Measure3D measure3D = new Measure3D();
		  double [] tVolume =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
		  RadialDistance radialDistance = new RadialDistance();
		  IJ.log("CHROMOCENTER PARAMETERS");
		  double tBorderToBorderDistanceTable [] = radialDistance.computeBorderToBorderDistances(imagePlusSegmented,imagePlusChromocenter);
		  double tBarycenterToBorderDistanceTable [] = radialDistance.computeBarycenterToBorderDistances (imagePlusSegmented,imagePlusChromocenter);
		  IJ.log("Titre Volume BorderToBorderDistance BarycenterToBorderDistanceTable");
		  for (int i = 0; i < tBorderToBorderDistanceTable.length;++i )
			  IJ.log(imagePlusChromocenter.getTitle()+"_"+i+" "+tVolume[i]+" "+tBorderToBorderDistanceTable[i]+" "+tBarycenterToBorderDistanceTable[i]);
	   }
	   
	   /**
	    * 
	    * @param pathResultsFile
	    * @param imagePlusSegmented
	    * @param imagePlusChromocenter
	    * @throws IOException
	    */
	   
	   public void computeParametersChromocenter (String pathResultsFile,ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter) throws IOException
	   {
		   	Measure3D measure3D = new Measure3D();
		    double [] tVolume =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
		    RadialDistance radialDistance = new RadialDistance();
		    double [] tBorderToBorderDistanceTable = radialDistance.computeBorderToBorderDistances(imagePlusSegmented,imagePlusChromocenter);
			double [] tBarycenterToBorderDistanceTable = radialDistance.computeBarycenterToBorderDistances (imagePlusSegmented,imagePlusChromocenter);
		    File fileResults = new File (pathResultsFile);
		    boolean exist = fileResults.exists();
		    BufferedWriter bufferedWirterOutput;	
		    if (exist) 
		    {
		    	FileWriter fileWriter = new FileWriter(fileResults, true);
		    	bufferedWirterOutput = new BufferedWriter(fileWriter);
		    	for (int i = 0; i < tBorderToBorderDistanceTable.length;++i )
		    		bufferedWirterOutput.write(imagePlusChromocenter.getTitle()+"_"+i+"\t"+tVolume[i]+"\t"+tBorderToBorderDistanceTable[i]+"\t"+tBarycenterToBorderDistanceTable[i]+"\n");
		    }
		    else
		    {
		    	FileWriter fileWriter = new FileWriter(fileResults, true);
		    	bufferedWirterOutput = new BufferedWriter(fileWriter);
		    	bufferedWirterOutput.write("Titre\tVolume\tBorderToBorderDistance\tBarycenterToBorderDistanceTable\n");
		    	for (int i = 0; i < tBorderToBorderDistanceTable.length;++i )
		    		bufferedWirterOutput.write(imagePlusChromocenter.getTitle()+"_"+i+"\t"+tVolume[i]+"\t"+tBorderToBorderDistanceTable[i]+"\t"+tBarycenterToBorderDistanceTable[i]+"\n");
		    }
		    bufferedWirterOutput.flush();
		    bufferedWirterOutput.close();
	   }
}