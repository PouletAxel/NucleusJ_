package gred.nucleus.plugins;
import gred.nucleus.core.*;
import gred.nucleus.dialogs.NucleusSegmentationAndAnalysisDialog;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.plugin.PlugIn;

/**
 * 
 * @author Poulet Axel
 *
 */
public class NucleusSegmentationAndAnalysisPlugin_ implements PlugIn
{
	 /** image to process*/
	ImagePlus _imagePlusInput;
	
	/**
	 * 
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
			IJ.error("image format", "No images in gray scale in 3D");
			return;
		}
		if (IJ.versionLessThan("1.32c"))
			return;
		NucleusSegmentationAndAnalysisDialog nucleusSegmentationAndAnalysisDialog = new NucleusSegmentationAndAnalysisDialog();
		while( nucleusSegmentationAndAnalysisDialog.isShowing())
		{
	    	 try {Thread.sleep(1);}
	    	 catch (InterruptedException e) {e.printStackTrace();}
	    }
	   
		if (nucleusSegmentationAndAnalysisDialog.isStart())
		{
			double xCalibration =nucleusSegmentationAndAnalysisDialog.getXCalibration();
			double yCalibration = nucleusSegmentationAndAnalysisDialog.getYCalibration();
			double zCalibration = nucleusSegmentationAndAnalysisDialog.getZCalibration();
			String unit = nucleusSegmentationAndAnalysisDialog.getUnit();
			double volumeMin = nucleusSegmentationAndAnalysisDialog.getMinVolume();
			double volumeMax = nucleusSegmentationAndAnalysisDialog.getMaxVolume();
			Calibration calibration = new Calibration();
			calibration.pixelDepth = zCalibration;
			calibration.pixelWidth = xCalibration;
			calibration.pixelHeight = yCalibration;
			calibration.setUnit(unit);
			_imagePlusInput.setCalibration(calibration);
			IJ.log("Begin image processing "+_imagePlusInput.getTitle());
			NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
			nucleusSegmentation.setVolumeRange(volumeMin, volumeMax);
			ImagePlus imagePlusSegmented = nucleusSegmentation.run(_imagePlusInput);
			if (nucleusSegmentation.getBestThreshold() > 0)
			{
				imagePlusSegmented.show();
				NucleusAnalysis nucleusAnalysis = new NucleusAnalysis();
				if (nucleusSegmentationAndAnalysisDialog.is2D3DAnalysis())
				{
					nucleusAnalysis.nucleusParameter3D(imagePlusSegmented);
					nucleusAnalysis.nucleusParameter2D(imagePlusSegmented);
				}
				else if(nucleusSegmentationAndAnalysisDialog.is3D())
					nucleusAnalysis.nucleusParameter3D(imagePlusSegmented);
				else
					nucleusAnalysis.nucleusParameter2D(imagePlusSegmented);
			}
		}
	}
}