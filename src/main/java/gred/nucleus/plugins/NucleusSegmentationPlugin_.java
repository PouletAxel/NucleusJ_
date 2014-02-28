package gred.nucleus.plugins;
import gred.nucleus.core.*;
import gred.nucleus.dialogs.NucleusSegmentationDialog;
import ij.*;
import ij.measure.Calibration;
import ij.plugin.*;

/**
 * 
 * @author gred
 *
 */
public class NucleusSegmentationPlugin_ implements PlugIn
{
	/** image to process*/
	ImagePlus _imagePlus;
	
	/**
	 * This method permit to execute the ReginalExtremFilter on the selected image
	 * @param arg 
	 */
	public void run(String arg)
	{
		_imagePlus = WindowManager.getCurrentImage();
	    if (null == _imagePlus)
	    {
	       IJ.noImage();
	       return;
	    }
	    else if (_imagePlus.getStackSize() == 1)
	    {
	    	IJ.error("image format", "No images in gray scale 8bits in 3D");
	        return;
	    }
	    if (IJ.versionLessThan("1.32c"))   return;
	    Calibration cal = _imagePlus.getCalibration();
	    NucleusSegmentationDialog nucleusSegDialog = new NucleusSegmentationDialog(cal.getUnit());
	    while( nucleusSegDialog.isShowing())
		{
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
	    }	
		if (nucleusSegDialog.isStart())
		{
			ImagePlus imagePlusOut= _imagePlus;
			NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
			nucleusSegmentation.setVolumeRange(nucleusSegDialog.getMinSeg(), nucleusSegDialog.getMaxSeg());
			imagePlusOut = nucleusSegmentation.applySegmentation(imagePlusOut);
			if(nucleusSegmentation.getBestThreshold()==0)
				IJ.showMessageWithCancel("Segmentation error", "No object is detected between "+nucleusSegDialog.getMinSeg()
					+"and"+nucleusSegDialog.getMaxSeg()+" "+cal.getUnit());
			else
			{
				imagePlusOut.setTitle("Seg"+_imagePlus.getTitle());
				imagePlusOut.show();
			}
		}
	}
}
