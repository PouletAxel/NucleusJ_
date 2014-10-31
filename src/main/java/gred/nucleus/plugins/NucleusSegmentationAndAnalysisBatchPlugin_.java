package gred.nucleus.plugins;
import java.io.File;

import gred.nucleus.dialogs.NucleusSegmentationAndAnalysisBatchDialog;
import gred.nucleus.multiThread.*;
import gred.nucleus.utils.FileList;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 *  Method to segment and analyse the nucleus on batch
 *  
 * @author Poulet Axel
 *
 */
public class NucleusSegmentationAndAnalysisBatchPlugin_ implements PlugIn
{
	NucleusSegmentationAndAnalysisBatchDialog _nucleusPipelineBatchDialog = new NucleusSegmentationAndAnalysisBatchDialog();
	
	/**
	 * 
	 */
	public void run(String arg)
	{
		while( _nucleusPipelineBatchDialog.isShowing())
		{
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
	    }	
		if (_nucleusPipelineBatchDialog.isStart())
		{
			IJ.log("Begining of the segmentation of nuclei, the data are in "+_nucleusPipelineBatchDialog.getRawDataDirectory());
			FileList fileList = new FileList ();
			File[] tFileRawImage = fileList.run(_nucleusPipelineBatchDialog.getRawDataDirectory());
			if(IJ.openImage(tFileRawImage[0].toString()).getType() == ImagePlus.GRAY32 )
			{
		    	IJ.error("image format", "No images in gray scale 8bits or 16 bits in 3D");
		        return;
		    }
			
			ProcessImageSegmentaion processImageSegmentation = new ProcessImageSegmentaion();
			try
			{
				processImageSegmentation.go(this, tFileRawImage,true);
			} 
			catch (InterruptedException e) { e.printStackTrace(); }
			IJ.log("End of the segmentation the nuclei, the results are in "+_nucleusPipelineBatchDialog.getWorkDirectory());
		}
	}
	
	public int getNbCpu(){return _nucleusPipelineBatchDialog.getNbCpu();}
	public double getZCalibration(){return _nucleusPipelineBatchDialog.getZCalibration();}
	public double getXCalibration(){return _nucleusPipelineBatchDialog.getXCalibration();}
	public double getYCalibration(){return _nucleusPipelineBatchDialog.getYCalibration();}
	public String getUnit(){return _nucleusPipelineBatchDialog.getUnit();}
	public double getMinVolume(){return _nucleusPipelineBatchDialog.getMinVolume();}
	public double getMaxVolume(){return _nucleusPipelineBatchDialog.getMaxVolume();}
	public String getWorkDirectory() {return _nucleusPipelineBatchDialog.getWorkDirectory();}
	public boolean is2D3DAnalysis(){return _nucleusPipelineBatchDialog.is2D3DAnalysis();}
	public boolean is3DAnalysis(){return _nucleusPipelineBatchDialog.is3D();}

}
