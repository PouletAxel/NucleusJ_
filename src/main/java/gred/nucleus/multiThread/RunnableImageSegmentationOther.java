package gred.nucleus.multiThread;

import gred.nucleus.core.NucleusAnalysis;
import gred.nucleus.core.NucleusSegmentation;
import gred.nucleus.core.OtherNucleusSegmentation;
import ij.ImagePlus;
import ij.io.FileSaver;

import java.io.File;
import java.io.IOException;

public class RunnableImageSegmentationOther  extends Thread implements Runnable
{
	String _workDirectory;
	ImagePlus _imagePlusInput;
	double _volumeMin, _volumeMax;
	boolean _isanalysis3D, _isanalysis2D3D, _doAnalysis;

	/**
	 * 
	 * Costructor which stock all the parameters of the graphical windows
	 * 
	 * @param imagePlusInput
	 * @param volumeMin
	 * @param volumeMax
	 * @param workDirectory
	 * @param analysis3D2D
	 * @param analysis3D
	 * @param doAnalysis
	 */
	public RunnableImageSegmentationOther (ImagePlus imagePlusInput, double volumeMin, double volumeMax, String workDirectory)
	{
		_imagePlusInput = imagePlusInput;
		_volumeMin = volumeMin;
		_volumeMax = volumeMax;
		_workDirectory = workDirectory;
	
	}
	
	/**
	 * Run parallel compute in function of the number of CPU chose by the user, and call the class ProcessImageSgmentation
	 */
	public void run()
	{
		ProcessImageOtherSeg._nbLance++;
		ProcessImageOtherSeg._continuer = true;
		OtherNucleusSegmentation nucleusSegmentation = new OtherNucleusSegmentation();
		ImagePlus impagePlusSegmented = nucleusSegmentation.run(_imagePlusInput);
		impagePlusSegmented.setTitle(_imagePlusInput.getTitle());
		saveFile(impagePlusSegmented,_workDirectory+File.separator+"SegmentedDataNucleus");
		ProcessImageOtherSeg._nbLance--;
	}
	
	/**
	 *
	 * Method which save the image in the directory.
	 *
	 * @param imagePlusInput Image to be save
	 * @param pathFile path of directory
	 */
	public void saveFile ( ImagePlus imagePlusInput, String pathFile)
	{
		FileSaver fileSaver = new FileSaver(imagePlusInput);
	    File file = new File(pathFile);
	    if (file.exists())
	    	fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlusInput.getTitle());
	    else
	    {
	      file.mkdir();
	      fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlusInput.getTitle());
	    }
	}
}