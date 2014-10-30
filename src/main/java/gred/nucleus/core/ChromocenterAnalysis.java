package gred.nucleus.core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import gred.nucleus.utils.Histogram;
import ij.IJ;
import ij.ImagePlus;

/**
 * Several method to realise and creat the outfile for the chromocenter Analysis
 * 
 * @author Poulet Axel
 *
 */
public class ChromocenterAnalysis
{
	public ChromocenterAnalysis ()   {	   }
	/**
	 *  Compute the several parameters to characterize the chromocenter of one image, and return the results on the IJ log windows
	 *  
	 * @param imagePlusSegmented image of the segmented nucleus
	 * @param imagePlusChromocenter image of the segmented chromocenter
	 */
	
	public void computeParametersChromocenter (ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter)
	{
		Histogram histogram = new Histogram ();
		histogram.run(imagePlusChromocenter);
		Measure3D measure3D = new Measure3D();
		double [] tVolume =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
		RadialDistance radialDistance = new RadialDistance();
		IJ.log("CHROMOCENTER PARAMETERS");
		IJ.log("Titre Volume BorderToBorderDistance BarycenterToBorderDistance BarycenterToBorderDistanceNucleus ");
		if (histogram.getNbLabels() > 0)
		{
			double [] tBorderToBorderDistanceTable = radialDistance.computeBorderToBorderDistances(imagePlusSegmented,imagePlusChromocenter);
			double [] tBarycenterToBorderDistanceTable = radialDistance.computeBarycenterToBorderDistances (imagePlusSegmented,imagePlusChromocenter);
			double [] tBarycenterToBorderDistanceTableNucleus = radialDistance.computeBarycenterToBorderDistances (imagePlusSegmented,imagePlusSegmented);
			for (int i = 0; i < tBorderToBorderDistanceTable.length;++i )
				IJ.log
				(
					imagePlusChromocenter.getTitle()+"_"+i+" "
					+tVolume[i]+" "
					+tBorderToBorderDistanceTable[i]+" "
					+tBarycenterToBorderDistanceTable[i]+" "
					+tBarycenterToBorderDistanceTableNucleus[0]
				);
		}
}
	   
	/**
	 * 
	 * Compute the several parameters to characterize the chromocenter of several images, and create 
	 * one output file for the results
	 * 
	 * @param pathResultsFile path for the output file
	 * @param imagePlusSegmented image of the segmented nucleus
	 * @param imagePlusChromocenter image of the chromocenter segmented
	 * @throws IOException 
	 */
	   
	public void computeParametersChromocenter (String pathResultsFile,ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter) throws IOException
	{
		Histogram histogram = new Histogram ();
		histogram.run(imagePlusChromocenter);
		if (histogram.getNbLabels() > 0)
		{
			File fileResults = new File (pathResultsFile);
			boolean exist = fileResults.exists();
			BufferedWriter bufferedWriterOutput;	
			FileWriter fileWriter = new FileWriter(fileResults, true);
			bufferedWriterOutput = new BufferedWriter(fileWriter);
			Measure3D measure3D = new Measure3D();
			double [] tVolume =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
			RadialDistance radialDistance = new RadialDistance();
			double [] tBorderToBorderDistanceTable = radialDistance.computeBorderToBorderDistances(imagePlusSegmented,imagePlusChromocenter);
			double [] tBarycenterToBorderDistanceTableCc = radialDistance.computeBarycenterToBorderDistances (imagePlusSegmented,imagePlusChromocenter);
			double [] tBarycenterToBorderDistanceTableNucleus = radialDistance.computeBarycenterToBorderDistances (imagePlusSegmented,imagePlusSegmented);
			if (exist == false)
				bufferedWriterOutput.write("Titre\tVolume\tBorderToBorderDistance\tBarycenterToBorderDistance\tBarycenterToBorderDistanceNucleus\n");
			for (int i = 0; i < tBorderToBorderDistanceTable.length;++i )
			{
				bufferedWriterOutput.write
				(
						imagePlusChromocenter.getTitle()+"_"+i+"\t"
						+tVolume[i]+"\t"
						+tBorderToBorderDistanceTable[i]+"\t"
						+tBarycenterToBorderDistanceTableCc[i]+"\t"
						+tBarycenterToBorderDistanceTableNucleus[0]+"\n"
				);
			}
			bufferedWriterOutput.flush();
			bufferedWriterOutput.close();
		}
	}
}