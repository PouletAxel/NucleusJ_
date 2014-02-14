package gred.nucleus.plugins;


import gred.nucleus.graphicInterface.FenetreCalib;
import gred.nucleus.nucleusAnalysis.*;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.plugin.PlugIn;

/**
 * 
 * @author gred
 *
 */
public class NucleusProcessAndAnalysis_ implements PlugIn
{
	 /** image to process*/
	ImagePlus _imagePlus;
	/** Voxel calibration in µm*/
	double _dimX, _dimY, _dimZ;
	String _unit;
	/** */
	double _segMin, _segMax;

	
	/**
	 * 
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
			IJ.error("image format", "No images in gray scale in 3D");
			return;
		}
		if (IJ.versionLessThan("1.32c"))   return;
		FenetreCalib fc = new FenetreCalib();
		while( fc.isShowing())
		{
	    	 try {Thread.sleep(1);}
	    	 catch (InterruptedException e) {e.printStackTrace();}
	    }
	   
		if (fc.isStart())
		{
			_dimX =fc.getx();
			_dimY = fc.gety();
			_dimZ = fc.getz();
			_unit = fc.getUnit();
			_segMin = fc.getMinSeg();
			_segMax = fc.getMaxSeg();
			Calibration cal = new Calibration();
			cal.pixelDepth = _dimZ;
			cal.pixelWidth = _dimX;
			cal.pixelHeight = _dimY;
			cal.setUnit(_unit);
			_imagePlus.setCalibration(cal);
			IJ.log("Begin image processing "+_imagePlus.getTitle());
			NucleusProcess np = new NucleusProcess(_imagePlus,_segMin,_segMax);
			np.run();
			if (np.getIndiceMax() > 0)
			{
				ImagePlus binaire = np.getImagePlusBinary();
				binaire.setTitle("Binary_"+_imagePlus.getTitle());
				binaire.show();
				ImagePlus gradient = np.getImagePlusGradient();
				gradient.setTitle("Gradient_"+_imagePlus.getTitle());
				gradient.show();
				ImagePlus contrast = np.getImagePlusContrast();
				contrast.setTitle("Contrast_"+_imagePlus.getTitle());
				contrast.show();
				NucleusAnalysis na = new NucleusAnalysis(binaire);
				if (fc.isTheBoth())
				{
					na.NucleusParameter3D();
					na.NucleusParameter2D();
				}
				else if(fc.is3D())  na.NucleusParameter3D();
				else na.NucleusParameter2D();
			}
		}
	}
}