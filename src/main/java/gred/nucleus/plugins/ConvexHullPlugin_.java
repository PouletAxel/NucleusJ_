package gred.nucleus.plugins;
import gred.nucleus.core.*;
import gred.nucleus.utils.ConvexeHullDetection;
import ij.*;
import ij.measure.Calibration;
import ij.plugin.*;

public class ConvexHullPlugin_  implements PlugIn
{

	ImagePlus _imagePlusInput;
	
	/**
	 * This method permit to execute the ReginalExtremFilter on the selected image
	 * 
	 * @param arg 
	 */
	public void run(String arg)
	{
		_imagePlusInput = WindowManager.getCurrentImage();
	    if (null == _imagePlusInput)
	    {
	       IJ.noImage();
	       return;
	    }

		ImagePlus imagePlusSegmented= _imagePlusInput;
		ImagePlus plopi = processImage(imagePlusSegmented);
		
	   plopi.show();
	}
	
	
	
	public static ImagePlus processImage(ImagePlus inputImage)
	{
		NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
		nucleusSegmentation.setVolumeRange(8, 2000);
		inputImage = nucleusSegmentation.applySegmentation(inputImage);
	    ConvexHullSegmentation nuc = new ConvexHullSegmentation();
	    ImagePlus plopi = nuc.run(inputImage);
		plopi.setTitle("test");
    
		return plopi;
	}
}