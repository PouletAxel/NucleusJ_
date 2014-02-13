package gred.nucleus.multiThread;

import gred.nucleus.nucleusAnalysis.*;
import ij.ImagePlus;
import ij.io.FileSaver;

import java.io.File;
import java.io.IOException;



public class RunnableProcessImage extends Thread implements Runnable
{

	String _workDir;
	ImagePlus _imagePlus;
	double _segMin, _segMax;
	boolean _isanalysis3D, _isanalysis2D3D;

	public RunnableProcessImage (ImagePlus imagePlusInput, double segMin, double segMax, String workDir,boolean analysis3D2D
			,boolean analysis3D)
	{
		_imagePlus = imagePlusInput;
		_segMin = segMin;
		_segMax = segMax;
		_workDir = workDir;
		_isanalysis3D = analysis3D;
		_isanalysis2D3D = analysis3D2D;
	}
	
	public void run()
	{
		ProcessImage._nbLance++;
		ProcessImage._continuer = true;
		NucleusProcess np = new NucleusProcess(_imagePlus,_segMin,_segMax,_workDir+File.separator+"logErrorSeg.txt");
		np.run();
		if (np.getIndiceMax() > 0)
		{
			ImagePlus binaire = np.getImagePlusBinary();
			binaire.setTitle(_imagePlus.getTitle());
			saveFile (binaire,_workDir+File.separator+"SegmentedDataNucleus");
			ImagePlus contrast = np.getImagePlusContrast();
			contrast.setTitle(_imagePlus.getTitle());
			saveFile (contrast,_workDir+File.separator+"Contrast");
			NucleusAnalysis na = new NucleusAnalysis(binaire);
			try
			{
				if (_isanalysis2D3D)
				{
					na.NucleusParameter3D(_workDir+File.separator+"3DNucleiParameters.tab");
					na.NucleusParameter2D(_workDir+File.separator+"2DNucleiParameters.tab");
				}
				else if(_isanalysis3D)  na.NucleusParameter3D(_workDir+File.separator+"3DNucleiParameters.tab");
				else na.NucleusParameter2D(_workDir+File.separator+"2DNucleiParameters.tab");
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
