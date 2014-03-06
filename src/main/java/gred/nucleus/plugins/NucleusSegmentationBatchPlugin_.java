package gred.nucleus.plugins;
import java.io.File;
import gred.nucleus.dialogs.NucleusSegmentationBatchDialog;
import gred.nucleus.multiThread.*;
import gred.nucleus.utils.FileList;
import ij.plugin.PlugIn;
/**
 * 
 * @author Poulet Axel
 *
 */
public class NucleusSegmentationBatchPlugin_ implements PlugIn
{

	private NucleusSegmentationBatchDialog _nucleusSegmentationBatchDialog = new NucleusSegmentationBatchDialog();
	
	/**
	 * 
	 */
	public void run(String arg)
	{
		while( _nucleusSegmentationBatchDialog.isShowing())
		{
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
		}	
		if (_nucleusSegmentationBatchDialog.isStart())
		{
			FileList fileList = new FileList ();
			File[] tRawImageFile = fileList.run(_nucleusSegmentationBatchDialog.getRawDataDirectory());
			ProcessImageSegmentaion processImageSegmentation = new ProcessImageSegmentaion();
			try {	processImageSegmentation.go(this, tRawImageFile,false); } 
			catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	public int getNbCpu(){return _nucleusSegmentationBatchDialog.getNbCpu();}
	public double getZCalibration(){return _nucleusSegmentationBatchDialog.getZCalibration();}
	public double getXCalibration(){return _nucleusSegmentationBatchDialog.getXCalibration();}
	public double getYCalibration(){return _nucleusSegmentationBatchDialog.getYCalibration();}
	public String getUnit(){return _nucleusSegmentationBatchDialog.getUnit();}
	public double getMinVolume(){return _nucleusSegmentationBatchDialog.getMinVolume();}
	public double getMaxVolume(){return _nucleusSegmentationBatchDialog.getMaxVolume();}
	public String getWorkDirectory() {return _nucleusSegmentationBatchDialog.getWorkDirectory();}
}