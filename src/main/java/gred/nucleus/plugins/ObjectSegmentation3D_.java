package gred.nucleus.plugins;
import gred.nucleus.graphicInterface.JFSegmentation;
import gred.nucleus.nucleusSegmentation.*;
import ij.*;
import ij.measure.Calibration;
import ij.plugin.*;

/**
 * 
 * @author gred
 *
 */
public class ObjectSegmentation3D_ implements PlugIn
{
	/** image to process*/
	ImagePlus _imagePlus;
	/** output image*/
	ImagePlus _imagePlusOut;
	
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
	    JFSegmentation jfSeg = new JFSegmentation(cal.getUnit());
	    while( jfSeg.isShowing())
		{
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
	    }	
		if (jfSeg.isStart())
		{
			_imagePlusOut= _imagePlus;
			MySegmentation seg = new MySegmentation(_imagePlusOut,jfSeg.getMinSeg(),jfSeg.getMaxSeg());
			_imagePlusOut = seg.computeSegmentation();
			if(seg.getIndiceMax()==0)
				IJ.showMessageWithCancel("Segmentation error", "Any object is detected between "+jfSeg.getMinSeg()
					+"and"+jfSeg.getMaxSeg()+" "+cal.getUnit());
			else
			{
				_imagePlusOut.setTitle("Seg"+_imagePlus.getTitle());
				_imagePlusOut.show();
			}
		}
	}
}
