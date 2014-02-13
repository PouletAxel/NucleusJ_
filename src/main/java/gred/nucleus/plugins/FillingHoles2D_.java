package gred.nucleus.plugins;
import gred.nucleus.treatment.FillingHoles;
import ij.*;
import ij.plugin.*;

/**
 * 
 * @author gred
 *
 */
public class FillingHoles2D_ implements PlugIn
{
	/** image to process*/
	ImagePlus _imagePlus;
	/** output image*/
	ImagePlus _imagePlusRef;
	
	/**
	 * This method apply fillingHolles in 2D on the selected image
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
	    ImagePlus temp = _imagePlus;
	    FillingHoles fh = new FillingHoles(temp);
	    _imagePlusRef = fh.apply2D();
	    _imagePlusRef.setTitle("2D"+_imagePlus.getTitle());
	    _imagePlusRef.show();
	}
}
