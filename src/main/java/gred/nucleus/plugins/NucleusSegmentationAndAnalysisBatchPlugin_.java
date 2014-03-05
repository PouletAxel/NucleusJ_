package gred.nucleus.plugins;
import java.io.File;

import gred.nucleus.dialogs.NucleusSegmentationAndAnalysisBatchDialog;
import gred.nucleus.multiThread.*;
import gred.nucleus.utils.FileList;
import ij.plugin.PlugIn;



/**
 * 
 * @author gred
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
			FileList fileList = new FileList ();
			File[] rawImage = fileList.run(_nucleusPipelineBatchDialog.getDirRawData());
			ProcessImageSegmentaion processImage = new ProcessImageSegmentaion(this, rawImage);
			try {	processImage.go(); } 
			catch (InterruptedException e) { e.printStackTrace(); }
			
		}
	}
	public int getNbProcessor(){return _nucleusPipelineBatchDialog.getNbProcessor();}
	public double getPixelDepth(){return _nucleusPipelineBatchDialog.getz();}
	public double getPixelWidth(){return _nucleusPipelineBatchDialog.getx();}
	public double getPixelHeight(){return _nucleusPipelineBatchDialog.gety();}
	public String getUnit(){return _nucleusPipelineBatchDialog.getUnit();}
	public double getSegMinValue(){return _nucleusPipelineBatchDialog.getMinSeg();}
	public double getSegMaxValue(){return _nucleusPipelineBatchDialog.getMaxSeg();}
	public String getWorkDir() {return _nucleusPipelineBatchDialog.getWorkDirectory();}
	public boolean is2D3DAnalysis(){return _nucleusPipelineBatchDialog.isTheBoth();}
	public boolean is3DAnalysis(){return _nucleusPipelineBatchDialog.is3D();}

}
