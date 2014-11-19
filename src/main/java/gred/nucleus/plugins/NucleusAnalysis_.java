package gred.nucleus.plugins;

import gred.nucleus.core.NucleusAnalysis;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;

public class NucleusAnalysis_
implements PlugIn
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
		else if (_imagePlusInput.getStackSize() == 1 || (_imagePlusInput.getType() != ImagePlus.GRAY8 && _imagePlusInput.getType() != ImagePlus.GRAY16))
		{
			IJ.error("image format", "No images in 8 or 16 bits gray scale  in 3D");
			return;
		}
			
			IJ.log("Begin image processing "+_imagePlusInput.getTitle());
			NucleusAnalysis nucleusAnalysis = new NucleusAnalysis();
			nucleusAnalysis.nucleusParameter3D(_imagePlusInput);
	}

}
