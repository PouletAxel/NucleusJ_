package gred.nucleus.multiThread;

import gred.nucleus.core.NucleusAnalysis;
import gred.nucleus.core.NucleusPipeline;
import ij.ImagePlus;
import ij.io.FileSaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



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
		NucleusPipeline nucleusPipeline = new NucleusPipeline();
		nucleusPipeline.setLogErrorSegmentationFile(_workDir+File.separator+"logErrorSeg.txt");
		nucleusPipeline.setVMinAndMax(_vmin, _vmax);
		ArrayList<ImagePlus> arrayList = nucleusPipeline.run(_imagePlus);
		if (nucleusPipeline.getIndiceMax() > 0)
		{
			ImagePlus binaire = arrayList.get(0);
			binaire.setTitle(_imagePlus.getTitle());
			saveFile (binaire,_workDir+File.separator+"SegmentedDataNucleus");
			ImagePlus contrast = arrayList.get(1);
			contrast.setTitle(_imagePlus.getTitle());
			saveFile (contrast,_workDir+File.separator+"Contrast");
			NucleusAnalysis na = new NucleusAnalysis(binaire);
			try
			{
				if (_isanalysis2D3D)
				{
					na.nucleusParameter3D(_workDir+File.separator+"3DNucleiParameters.tab");
					na.nucleusParameter2D(_workDir+File.separator+"2DNucleiParameters.tab");
				}
				else if(_isanalysis3D)  na.nucleusParameter3D(_workDir+File.separator+"3DNucleiParameters.tab");
				else na.nucleusParameter2D(_workDir+File.separator+"2DNucleiParameters.tab");
			}
			catch (IOException e) {	e.printStackTrace();	}
		}
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
