package gred.nucleus.core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import gred.nucleus.myGradient.MyGradient;
import gred.nucleus.utils.RegionalExtremaFilter;
import inra.ijpb.binary.ConnectedComponents;
import inra.ijpb.watershed.*;
import ij.IJ;
import ij.ImagePlus;



/**
 * 
 * 
 * @author Axel Poulet
 *
 */

public class NucleusPipeline
{

	private double _volumeMin, _volumeMax;
	/** */
	int _bestThreshold;
	/** */
	String _logErrorSeg = "";
  
	
	/**
	 *   
	 */
	public NucleusPipeline ()	{	}
  
	/**
	 * Method which run the process in input image. This image will be segmented, and
	 * the binary image will be save in a directory. Then we realise the image gradient
	 * and the image contrast of the input image.
	 * The image contrast is saved in other directory. It is from this image contrast
	 * that a manual thresholding is necessary to segment chromocentre.
	 * @param arg
	 */

	public ArrayList<ImagePlus> run(ImagePlus imagePlusInput)
	{
		ArrayList<ImagePlus> outPutImageArrayList = new ArrayList<ImagePlus>();
		IJ.log("Begin segmentation "+imagePlusInput.getTitle());
		ImagePlus imagePlusBinary = computeNucleusSegmentation (imagePlusInput);
		if (_bestThreshold > 0)
		{
			IJ.log("End segmentation "+imagePlusInput.getTitle());
			IJ.log("Begin gradient "+imagePlusInput.getTitle());
			ImagePlus imagePlusGradient = computeImageGradient (imagePlusInput, imagePlusBinary);
			IJ.log("End gradient "+imagePlusInput.getTitle());
			IJ.log("Begin watershed "+imagePlusInput.getTitle());
			ImagePlus imagePlusContrast = computeImageContrast (imagePlusInput, imagePlusBinary, imagePlusGradient);
			IJ.log("End watershed "+imagePlusInput.getTitle());
			outPutImageArrayList.add(imagePlusBinary);
			outPutImageArrayList.add(imagePlusContrast);
		}
		else
		{
			if (_logErrorSeg.length()==0)
			{
				IJ.showMessage("Error Segmentation", "Bad parameter for the segmentation, any object is detected between "
    				  +_volumeMin+" and "+ _volumeMax+" "+ imagePlusInput.getCalibration().getUnit()+"^3");
			}
			else
			{
				File fileResu = new File (_logErrorSeg);
				BufferedWriter output;
				FileWriter fw;
				try
				{
					fw = new FileWriter(fileResu, true);
					output = new BufferedWriter(fw);
					output.write(imagePlusInput.getTitle()+"\n");
					output.flush();
					output.close();
				}
				catch (IOException e) { e.printStackTrace(); } 
			}
		}
		return outPutImageArrayList;
	}
 

	/**
	 * 
	 * @param imagePlusInput
	 * @return
	 */
	public ImagePlus computeNucleusSegmentation (ImagePlus imagePlusInput)
	{
		NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
		nucleusSegmentation.setVolumeRange(_volumeMin, _volumeMax);
		ImagePlus imagePlusOutput = nucleusSegmentation.apply(imagePlusInput);  
		_bestThreshold = nucleusSegmentation.getBestThreshold();
		return imagePlusOutput;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getIndiceMax(){return _bestThreshold;}
	
  
	/**
	 * 
	 * @param imagePlusInput
	 * @param imagePlusBinary
	 * @return
	 */
	public ImagePlus computeImageGradient (ImagePlus imagePlusInput, ImagePlus imagePlusBinary)
	{
		MyGradient myGradient = new MyGradient (imagePlusInput,imagePlusBinary);
		ImagePlus imagePlusGradient = myGradient.run();
		return imagePlusGradient;
	}

	

	/**
	 * 	
	 * @param imagePlusInput
	 * @param imagePlusBinary
	 * @param imagePlusGradient
	 * @return
	 */
	public ImagePlus computeImageContrast (ImagePlus imagePlusInput, ImagePlus imagePlusBinary, ImagePlus imagePlusGradient)
	{
		
		RegionalExtremaFilter regionalExtremaFilter = new RegionalExtremaFilter();
	    regionalExtremaFilter.setMask(imagePlusBinary);
	    ImagePlus extrema = regionalExtremaFilter.applyWithMask( imagePlusGradient );
	    ImagePlus ImagePlusLabels = ConnectedComponents.computeLabels(extrema, 26, 32);
	    WatershedTransform3D  watershedTransform = new WatershedTransform3D(imagePlusGradient,ImagePlusLabels,imagePlusBinary);
		imagePlusGradient = watershedTransform.apply();
		ChromocentersSegmentation segmentationTools = new ChromocentersSegmentation(imagePlusInput,imagePlusGradient);
    	return segmentationTools.applyContrast();
	}
	
	/**
	 * 
	 * @param logErrorSeg
	 */
	public void setLogErrorSegmentationFile (String logErrorSeg)
	{
		_logErrorSeg = logErrorSeg;
	}
	
	/**
	 * 
	 * @param volumeMin
	 * @param volumeMax
	 */
	public void setVMinAndMax(double volumeMin, double volumeMax)
	{
		_volumeMin = volumeMin;
		_volumeMax = volumeMax;
	}
}