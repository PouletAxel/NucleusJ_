package gred.nucleus.plugins;
import gred.nucleus.core.ChromocenterSegmentation;
import gred.nucleus.dialogs.*;
import gred.nucleus.utils.FileList;
import ij.plugin.PlugIn;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.measure.Calibration;

import java.io.File;
import java.util.ArrayList;

public class ChromocenterSegmentationBatchPlugin_ implements PlugIn
{

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
			FileList FileList = new FileList ();
			FileList.run(_chromocenterSegmentationPipelineBatchDialog.getDirRawData());
			if (FileList.isDirectoryOrFIleExist(".+RawDataNucleus.+") && FileList.isDirectoryOrFIleExist(".+SegmentedDataNucleus.+"))
			{
				double dimX =_chromocenterSegmentationPipelineBatchDialog.getx();
				double dimY = _chromocenterSegmentationPipelineBatchDialog.gety();
				double dimZ = _chromocenterSegmentationPipelineBatchDialog.getz();
				String unit = _chromocenterSegmentationPipelineBatchDialog.getUnit();
				ArrayList<String> imageSegmenetedDataNucleusList = FileList.fileSearchList(".+SegmentedDataNucleus.+");
				String workDirectory = _chromocenterSegmentationPipelineBatchDialog.getWorkDirectory();
				for (int i = 0; i < imageSegmenetedDataNucleusList.size(); ++i)
				{
					IJ.log("image"+(i+1)+" / "+(imageSegmenetedDataNucleusList.size())+"   "+imageSegmenetedDataNucleusList.get(i));
					String pathImageSegmentedNucleus = imageSegmenetedDataNucleusList.get(i);
					String pathNucleusRaw = pathImageSegmentedNucleus.replaceAll("SegmentedDataNucleus", "RawDataNucleus");
					IJ.log(pathNucleusRaw);
					if (FileList.isDirectoryOrFIleExist(pathNucleusRaw))
					{
						ImagePlus imagePlusSegmentedNucleus = IJ.openImage(pathImageSegmentedNucleus);
						ImagePlus imagePlusRaw = IJ.openImage(pathNucleusRaw);
						Calibration cal = new Calibration();
						cal.pixelDepth = dimZ;
						cal.pixelWidth = dimX;
						cal.pixelHeight = dimY;
						cal.setUnit(unit);
						imagePlusSegmentedNucleus.setCalibration(cal);
						imagePlusRaw.setCalibration(cal);
						ChromocenterSegmentation chromocenterSegmentation = new ChromocenterSegmentation();
						ImagePlus imagePlusConstraste = chromocenterSegmentation.applyChromocentersSegmentation(imagePlusRaw, imagePlusSegmentedNucleus);
						saveFile (imagePlusConstraste,workDirectory+File.separator+"ConstrastDataNucleus");
					}
					else	{	IJ.showMessage("There are no the three subdirectories or the subDirectories is empty"); }
				}
			}
		}
	}
	public void saveFile ( ImagePlus imagePlus, String pathFile)
	{
		FileSaver fileSaver = new FileSaver(imagePlus);
	    File file = new File(pathFile);
	    if (file.exists()) fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlus.getTitle());
	    else
	    {
	      file.mkdir();
	      fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlus.getTitle());
	    }
	  }
	
}
