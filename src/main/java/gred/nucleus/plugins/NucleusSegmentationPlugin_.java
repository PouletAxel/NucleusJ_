package gred.nucleus.plugins;
import gred.nucleus.core.*;
import gred.nucleus.dialogs.NucleusSegmentationDialog;
import ij.*;
import ij.measure.Calibration;
import ij.plugin.*;

/**
 *  Method to segment the nucleus on one image
 *  
 * @author Poulet Axel
 *
 */
public class NucleusSegmentationPlugin_ implements PlugIn{
	/** image to process*/
	ImagePlus m_img;
	
	/**
	 * This method permit to execute the ReginalExtremFilter on the selected image
	 * 
	 * @param arg 
	 */
	public void run(String arg){
		m_img = WindowManager.getCurrentImage();
	    if(null == m_img){
	       IJ.noImage();
	       return;
	    }
	    else if(m_img.getStackSize() == 1 || (m_img.getType() != ImagePlus.GRAY8 && m_img.getType() != ImagePlus.GRAY16)){
	    	IJ.error("Image format", "No image in gray scale 8bits or 16 bits in 3D");
	        return;
	    }
	    if(IJ.versionLessThan("1.32c"))
	    	return;
	    NucleusSegmentationDialog nucleusSegmentationDialog = new NucleusSegmentationDialog(m_img.getCalibration());
	    while( nucleusSegmentationDialog.isShowing()){
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
	    }	
		if (nucleusSegmentationDialog.isStart()){
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
			m_img.setCalibration(calibration);
			GaussianBlur3D.blur(m_img,0.25,0.25,1);
			ImageStack imageStack= m_img.getStack();
			int max = 0;
			for(int k = 0; k < m_img.getStackSize(); ++k)
				for (int i = 0; i < m_img.getWidth(); ++i )
					for (int j = 0; j < m_img.getHeight(); ++j){
						if (max < imageStack.getVoxel(i, j, k)){
							max = (int) imageStack.getVoxel(i, j, k);
						}
					}
			IJ.setMinAndMax(m_img, 0, max);	
			IJ.run(m_img, "Apply LUT", "stack");
			ImagePlus imagePlusSegmented= m_img;
			NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
			nucleusSegmentation.setVolumeRange(volumeMin, volumeMax);
			imagePlusSegmented = nucleusSegmentation.applySegmentation(imagePlusSegmented);
			if(nucleusSegmentation.getBestThreshold()==0)
				IJ.showMessageWithCancel( "Segmentation error","No object detected between "
					+nucleusSegmentationDialog.getMinVolume()+"and"+nucleusSegmentationDialog.getMaxVolume()+" "+unit
				);
			else{
				imagePlusSegmented.setTitle("Segmented"+m_img.getTitle());
				imagePlusSegmented.show();
			}
		}
	}
}