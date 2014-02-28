package gred.nucleus.multiThread;
import gred.nucleus.core.NucleusAnalysis;
import gred.nucleus.core.NucleusSegmentation;
import ij.ImagePlus;
import ij.io.FileSaver;
import java.io.File;
import java.io.IOException;




public class RunnableProcessImage extends Thread implements Runnable
{

	String _workDir;
	ImagePlus _imagePlus;
	double _vmin, _vmax;
	boolean _isanalysis3D, _isanalysis2D3D;

	public RunnableProcessImage (ImagePlus imagePlusInput, double vmin, double vmax, String workDir,boolean analysis3D2D
			,boolean analysis3D)
	{
		_imagePlus = imagePlusInput;
		_vmin = vmin;
		_vmax = vmax;
		_workDir = workDir;
		_isanalysis3D = analysis3D;
		_isanalysis2D3D = analysis3D2D;
	}
	
	public void run()
	{
		ProcessImage._nbLance++;
		ProcessImage._continuer = true;
		NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
		nucleusSegmentation.setLogErrorSegmentationFile(_workDir+File.separator+"logErrorSeg.txt");
		nucleusSegmentation.setVolumeRange(_vmin, _vmax);
		ImagePlus impagePlusSegmented = nucleusSegmentation.run(_imagePlus);
		saveFile (impagePlusSegmented,_workDir+File.separator+"SegmentedDataNucleus");
		NucleusAnalysis nucleusAnalysis = new NucleusAnalysis();
		try
		{
			if (_isanalysis2D3D)
			{
				nucleusAnalysis.nucleusParameter3D(_workDir+File.separator+"3DNucleiParameters.tab",impagePlusSegmented);
				nucleusAnalysis.nucleusParameter2D(_workDir+File.separator+"2DNucleiParameters.tab",impagePlusSegmented);
			}
			else if(_isanalysis3D)  nucleusAnalysis.nucleusParameter3D(_workDir+File.separator+"3DNucleiParameters.tab",impagePlusSegmented);
			else nucleusAnalysis.nucleusParameter2D(_workDir+File.separator+"2DNucleiParameters.tab",impagePlusSegmented);
		}
		catch (IOException e) {	e.printStackTrace();	}
		ProcessImage._nbLance--;
}
	
	 /**
	   *
	   * Method which save the image in the directory.
	   *
	   * @param imagePlusInput Image to be save
	   * @param pathFile path of directory
	   */
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
