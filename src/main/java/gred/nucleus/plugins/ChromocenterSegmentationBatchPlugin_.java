package gred.nucleus.plugins;
import gred.nucleus.core.ChromocentersEnhancement;
import gred.nucleus.dialogs.*;
import gred.nucleus.utils.FileList;
import ij.plugin.PlugIn;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.measure.Calibration;

import java.io.File;
import java.util.ArrayList;
/**
 * 
 * @author Poulet Axel
 *
 */
public class ChromocenterSegmentationBatchPlugin_ implements PlugIn
{

	/**
	 * 
	 */
	public void run(String arg)
	{
		ChromocenterSegmentationPipelineBatchDialog _chromocenterSegmentationPipelineBatchDialog = new ChromocenterSegmentationPipelineBatchDialog();
		while( _chromocenterSegmentationPipelineBatchDialog.isShowing())
		{
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
		}	
		if (_chromocenterSegmentationPipelineBatchDialog.isStart())
		{
			FileList fileList = new FileList ();
			File[] tFileRawData =fileList.run(_chromocenterSegmentationPipelineBatchDialog.getRawDataDirectory());
			if (fileList.isDirectoryOrFileExist(".+RawDataNucleus.+",tFileRawData) &&
				fileList.isDirectoryOrFileExist(".+SegmentedDataNucleus.+",tFileRawData))
			{
				double xCalibration =_chromocenterSegmentationPipelineBatchDialog.getXCalibration();
				double yCalibration = _chromocenterSegmentationPipelineBatchDialog.getYCalibration();
				double zCalibration = _chromocenterSegmentationPipelineBatchDialog.getZCalibration();
				String unit = _chromocenterSegmentationPipelineBatchDialog.getUnit();
				ArrayList<String> arrayListImageSegmenetedDataNucleus = fileList.fileSearchList(".+SegmentedDataNucleus.+",tFileRawData);
				String workDirectory = _chromocenterSegmentationPipelineBatchDialog.getWorkDirectory();
				for (int i = 0; i < arrayListImageSegmenetedDataNucleus.size(); ++i)
				{
					IJ.log("image"+(i+1)+" / "+arrayListImageSegmenetedDataNucleus.size());
					String pathImageSegmentedNucleus = arrayListImageSegmenetedDataNucleus.get(i);
					String pathNucleusRaw = pathImageSegmentedNucleus.replaceAll("SegmentedDataNucleus", "RawDataNucleus");
					IJ.log(pathNucleusRaw);
					if (fileList.isDirectoryOrFileExist(pathNucleusRaw,tFileRawData))
					{
						ImagePlus imagePlusSegmented = IJ.openImage(pathImageSegmentedNucleus);
						ImagePlus imagePlusInput = IJ.openImage(pathNucleusRaw);
						Calibration calibration = new Calibration();
						calibration.pixelDepth = zCalibration;
						calibration.pixelWidth = xCalibration;
						calibration.pixelHeight = yCalibration;
						calibration.setUnit(unit);
						imagePlusSegmented.setCalibration(calibration);
						imagePlusInput.setCalibration(calibration);
						ChromocentersEnhancement chromocenterSegmentation = new ChromocentersEnhancement();
						ImagePlus imagePlusConstraste = chromocenterSegmentation.applyEnhanceChromocenters(imagePlusInput, imagePlusSegmented);
						imagePlusConstraste.setTitle(imagePlusInput.getTitle());
						saveFile (imagePlusConstraste,workDirectory+File.separator+"ConstrastDataNucleus");
					}
				}
			}
			else	{	IJ.showMessage("There are no the two subdirectories (See the directory name) or subDirectories are empty"); }		
			
		}
	}
	
	/**
	 * 
	 * @param imagePlus
	 * @param pathFile
	 */
	public void saveFile ( ImagePlus imagePlus, String pathFile)
	{
		FileSaver fileSaver = new FileSaver(imagePlus);
	    File file = new File(pathFile);
	    if (file.exists())
	    	fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlus.getTitle());
	    else
	    {
	    	file.mkdir();
	    	fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlus.getTitle());
	    }
	}
}