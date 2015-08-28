package gred.nucleus.plugins;
import gred.nucleus.core.*;
import gred.nucleus.utils.ConvexeHullDetection;
import ij.*;
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
	    ConvexHullSegmentation nuc = new ConvexHullSegmentation();
	    ImagePlus plopi = nuc.run(_imagePlusInput);
	   plopi.setTitle("test");
	   plopi.show();
	}
}