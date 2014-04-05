package gred.nucleus.multiThread;
import gred.nucleus.core.NucleusAnalysis;
import gred.nucleus.core.NucleusSegmentation;
import ij.ImagePlus;
import ij.io.FileSaver;
import java.io.File;
import java.io.IOException;

/**
 * 
 * @author Poulet Axel
 *
 */
public class RunnableImageSegmentation extends Thread implements Runnable
{
	String _workDirectory;
	ImagePlus _imagePlusInput;
	double _volumeMin, _volumeMax;
	boolean _isanalysis3D, _isanalysis2D3D, _doAnalysis;

	/**
	 * 
	 * @param imagePlusInput
	 * @param volumeMin
	 * @param volumeMax
	 * @param workDirectory
	 * @param analysis3D2D
	 * @param analysis3D
	 * @param doAnalysis
	 */
	public RunnableImageSegmentation (ImagePlus imagePlusInput, double volumeMin, double volumeMax, String workDirectory,boolean analysis3D2D
			,boolean analysis3D, boolean doAnalysis)
	{
		_doAnalysis = doAnalysis;
		_imagePlusInput = imagePlusInput;
		_volumeMin = volumeMin;
		_volumeMax = volumeMax;
		_workDirectory = workDirectory;
		_isanalysis3D = analysis3D;
		_isanalysis2D3D = analysis3D2D;
	}
	
	/**
	 * 
	 */
	public void run()
	{
		ProcessImageSegmentaion._nbLance++;
		ProcessImageSegmentaion._continuer = true;
		NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
		nucleusSegmentation.setLogErrorSegmentationFile(_workDirectory+File.separator+"logErrorSegmentation.txt");
		nucleusSegmentation.setVolumeRange(_volumeMin, _volumeMax);
		ImagePlus impagePlusSegmented = nucleusSegmentation.run(_imagePlusInput);
		if (nucleusSegmentation.getBestThreshold()> 0)
		{
			impagePlusSegmented.setTitle(_imagePlusInput.getTitle());
			saveFile(impagePlusSegmented,_workDirectory+File.separator+"SegmentedDataNucleus");
			NucleusAnalysis nucleusAnalysis = new NucleusAnalysis();
			if(_doAnalysis)
			{
				try
				{
					if(_isanalysis2D3D)
					{
						nucleusAnalysis.nucleusParameter3D(_workDirectory+File.separator+"3DNucleiParameters.tab",impagePlusSegmented);
						nucleusAnalysis.nucleusParameter2D(_workDirectory+File.separator+"2DNucleiParameters.tab",impagePlusSegmented);
					}
					else if(_isanalysis3D)  nucleusAnalysis.nucleusParameter3D(_workDirectory+File.separator+"3DNucleiParameters.tab",impagePlusSegmented);
					else nucleusAnalysis.nucleusParameter2D(_workDirectory+File.separator+"2DNucleiParameters.tab",impagePlusSegmented);
				}
				catch (IOException e) {	e.printStackTrace();	}
			}
		}
		ProcessImageSegmentaion._nbLance--;
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
	    if (file.exists()) fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlusInput.getTitle());
	    else
	    {
	      file.mkdir();
	      fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlusInput.getTitle());
	    }
	}
}
