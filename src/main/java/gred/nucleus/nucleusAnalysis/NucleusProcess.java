package gred.nucleus.nucleusAnalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import gred.nucleus.analysis.MyCounter3D;
import gred.nucleus.myGradient.MyGradient;
import gred.nucleus.nucleusSegmentation.MySegmentation;
import gred.nucleus.treatment.PostTreatmentAfterWatershed;
import gred.nucleus.treatment.RegionalExtremaFilter;
import inra.ijpb.watershed.*;
import ij.IJ;
import ij.ImagePlus;
import ij.process.StackConverter;



/**
 * 
 * 
 * @author Axel Poulet
 *
 */

public class NucleusProcess
{
	/**Image deconvolved */
	private ImagePlus _imagePlusDeconv;
	/** Binary image*/
	private ImagePlus _imagePlusBinary;
	/** Image gradient*/
	private ImagePlus _imagePlusGradient;
	/** Image contrast*/
	private ImagePlus _imagePlusContrast;
	/** */
	private double _vmin, _vmax;
	/** */
	int _indiceMax;
	/** */
	String _logErrorSeg = "";
  
	/**
	 * 
	 * @param imagePlus
	 * @param volumeMin
	 * @param volumeMax
	 * @param logErrorSeg
	 */
	
	public NucleusProcess (ImagePlus imagePlus, double volumeMin, double volumeMax, String logErrorSeg)
	{
		_imagePlusDeconv = imagePlus; 
		_vmin = volumeMin;
		_vmax = volumeMax;
		_logErrorSeg = logErrorSeg;
	}
  
	/**
	 * 
	 * @param imagePlus
	 * @param volumeMin
	 * @param volumeMax
	 */
  
	public NucleusProcess (ImagePlus imagePlus, double volumeMin, double volumeMax)
	{
		_imagePlusDeconv = imagePlus; 
		_vmin = volumeMin;
		_vmax = volumeMax;
	}
  
	/**
	 * Method which run the process in input image. This image will be segmented, and
	 * the binary image will be save in a directory. Then we realise the image gradient
	 * and the image contrast of the input image.
	 * The image contrast is saved in other directory. It is from this image contrast
	 * that a manual thresholding is necessary to segment chromocentre.
	 * @param arg
	 */
	public void run()
	{
		IJ.log("Begin segmentation "+_imagePlusDeconv.getTitle());
		_imagePlusBinary = computeNucleusSegmentation ();
		if (_indiceMax > 0)
		{
			IJ.log("End segmentation "+_imagePlusDeconv.getTitle());
			IJ.log("Begin gradient "+_imagePlusDeconv.getTitle());
			_imagePlusGradient = computeImageGradient ();
			IJ.log("End gradient "+_imagePlusDeconv.getTitle());
			IJ.log("Begin watershed "+_imagePlusDeconv.getTitle());
			_imagePlusContrast = computeImageContrast ();
			IJ.log("End watershed "+_imagePlusDeconv.getTitle());
		}
		else
		{
			if (_logErrorSeg.length()==0)
			{
				IJ.showMessage("Error Segmentation", "Bad parameter for the segmentation, any object is detected between "
    				  +_vmin+" and "+ _vmax+" "+ _imagePlusDeconv.getCalibration().getUnit()+"^3");
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
					output.write(_imagePlusDeconv.getTitle()+"\n");
					output.flush();
					output.close();
				}
				catch (IOException e) { e.printStackTrace(); } 
			}
		}
	}
 
	/**
	 * 
	 * @return
	 */
	public ImagePlus getImagePlusBinary () {return _imagePlusBinary;}

	/**
	 * 
	 * @return
	 */
	public ImagePlus getImagePlusGradient () {return _imagePlusGradient;}
	/**
	 * 
	 * @return
	 */
	public ImagePlus getImagePlusContrast () {return _imagePlusContrast;}

	/**
	 * Method which realize the segmentation
	 *
	 * @return Segmented image
	 */
	public ImagePlus computeNucleusSegmentation ()
	{
		MySegmentation mySeg = new MySegmentation(_imagePlusDeconv,_vmin, _vmax);
		ImagePlus imagePlus = mySeg.computeSegmentation();  
		_indiceMax = mySeg.getIndiceMax();
		return imagePlus;
	}
	
	public int getIndiceMax(){return _indiceMax;}
	
	/**
	 * Method with compute the image gradient
	 * @return  Image Gradient
	 */
  
	public ImagePlus computeImageGradient ()
	{
		MyGradient myGradient = new MyGradient (_imagePlusDeconv,_imagePlusBinary);
		ImagePlus imagePlusGradient = myGradient.run();
		return imagePlusGradient;
	}

	/**
	 * Method which compute the image contrast
	 * @return Image contrast
	 */
	@SuppressWarnings("static-access")
	public ImagePlus computeImageContrast ()
	{
		ImagePlus labelImagePlus = _imagePlusDeconv.duplicate();
		RegionalExtremaFilter regionalExtremaFilter = new RegionalExtremaFilter();
	    regionalExtremaFilter.setMask(_imagePlusBinary);
	    labelImagePlus = regionalExtremaFilter.applyWithMask( labelImagePlus );
	    StackConverter stackConverter = new StackConverter( labelImagePlus );
	    if (labelImagePlus.getType() == labelImagePlus.GRAY32)
	    	stackConverter.convertToGray16();
	    MyCounter3D myCounter3D = new MyCounter3D( labelImagePlus );
	    labelImagePlus = myCounter3D.getObjMap();
	    stackConverter = new StackConverter( labelImagePlus );
	    stackConverter.convertToGray32();
	    WatershedTransform3D  watershedTransform = new WatershedTransform3D(_imagePlusGradient,_imagePlusBinary,labelImagePlus);
		_imagePlusGradient = watershedTransform.apply();
		PostTreatmentAfterWatershed segmentationTools = new PostTreatmentAfterWatershed(_imagePlusDeconv,_imagePlusGradient);
    	return segmentationTools.applyContrast();
	}
	
}