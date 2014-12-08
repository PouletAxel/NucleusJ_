package gred.nucleus.plugins;
import gred.nucleus.core.*;
import gred.nucleus.dialogs.NucleusSegmentationDialog;
import ij.*;
import ij.measure.Calibration;
import ij.plugin.*;

/**
 * 
 * @author Poulet Axel
 *
 */
public class OtherNucleusSegmentationPlugin_ implements PlugIn
{
	/** image to process*/
	ImagePlus _imagePlusInput;
	
	/**
	 * This method permit to execute the ReginalExtremFilter on the selected image
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
	    else if (_imagePlusInput.getStackSize() == 1)
	    {
	    	IJ.error("image format", "No images in gray scale 8bits in 3D");
	        return;
	    }
	    if (IJ.versionLessThan("1.32c"))
	    	return;
	    NucleusSegmentationDialog nucleusSegmentationDialog = new NucleusSegmentationDialog(_imagePlusInput.getCalibration());
	    while( nucleusSegmentationDialog.isShowing())
		{
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
	    }	
		if (nucleusSegmentationDialog.isStart())
		{
			double xCalibration = nucleusSegmentationDialog.getXCalibration();
			double yCalibration = nucleusSegmentationDialog.getYCalibration();
			double zCalibration = nucleusSegmentationDialog.getZCalibration();
			String unit = nucleusSegmentationDialog.getUnit();
			double volumeMin = nucleusSegmentationDialog.getMinVolume();
			double volumeMax = nucleusSegmentationDialog.getMaxVolume();
			Calibration calibration = new Calibration();
			calibration.pixelDepth = zCalibration;
			calibration.pixelWidth = xCalibration;
			calibration.pixelHeight = yCalibration;
			calibration.setUnit(unit);
			_imagePlusInput.setCalibration(calibration);
			ImagePlus imagePlusSegmented= _imagePlusInput;
			OtherNucleusSegmentation nucleusSegmentation = new OtherNucleusSegmentation();
			nucleusSegmentation.setVolumeRange(volumeMin, volumeMax);
			imagePlusSegmented = nucleusSegmentation.run(imagePlusSegmented);
			imagePlusSegmented.setTitle("Segmented"+_imagePlusInput.getTitle());
			imagePlusSegmented.show();
		}
	}
}