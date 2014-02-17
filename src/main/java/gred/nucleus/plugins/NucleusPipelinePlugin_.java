package gred.nucleus.plugins;


import java.util.ArrayList;

import gred.nucleus.core.*;
import gred.nucleus.dialogs.NucleusPipelineDialog;
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
//enlever les données membres inutiles
public class NucleusPipelinePlugin_ implements PlugIn
{
	 /** image to process*/
	ImagePlus _imagePlus;


	
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
		NucleusPipelineDialog nucleusPipelineDialog = new NucleusPipelineDialog();
		while( nucleusPipelineDialog.isShowing())
		{
	    	 try {Thread.sleep(1);}
	    	 catch (InterruptedException e) {e.printStackTrace();}
	    }
	   
		if (nucleusPipelineDialog.isStart())
		{
			double dimX =nucleusPipelineDialog.getx();
			double dimY = nucleusPipelineDialog.gety();
			double dimZ = nucleusPipelineDialog.getz();
			String unit = nucleusPipelineDialog.getUnit();
			double vmin = nucleusPipelineDialog.getMinSeg();
			double vmax = nucleusPipelineDialog.getMaxSeg();
			Calibration cal = new Calibration();
			cal.pixelDepth = dimZ;
			cal.pixelWidth = dimX;
			cal.pixelHeight = dimY;
			cal.setUnit(unit);
			_imagePlus.setCalibration(cal);
			IJ.log("Begin image processing "+_imagePlus.getTitle());
			NucleusPipeline nucleusPipeline = new NucleusPipeline();
			nucleusPipeline.setVMinAndMax(vmin, vmax);
			ArrayList<ImagePlus> arrayList = nucleusPipeline.run(_imagePlus);
			if (nucleusPipeline.getIndiceMax() > 0)
			{
				ImagePlus binaire = arrayList.get(0);
				binaire.setTitle("Binary_"+_imagePlus.getTitle());
				binaire.show();
				ImagePlus contrast = arrayList.get(1);
				contrast.setTitle("Contrast_"+_imagePlus.getTitle());
				contrast.show();
				NucleusAnalysis nucleusAnalysis = new NucleusAnalysis();
				if (nucleusPipelineDialog.isTheBoth())
				{
					nucleusAnalysis.nucleusParameter3D(binaire);
					nucleusAnalysis.nucleusParameter2D(binaire);
				}
				else if(nucleusPipelineDialog.is3D())  nucleusAnalysis.nucleusParameter3D(binaire);
				else nucleusAnalysis.nucleusParameter2D(binaire);
			}
		}
	}
}