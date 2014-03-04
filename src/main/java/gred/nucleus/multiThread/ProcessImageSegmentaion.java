package gred.nucleus.multiThread;

import gred.nucleus.plugins.NucleusPipelineBatchPlugin_;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;


public class ProcessImageSegmentaion
{
	static int _nbLance = 0;
	static boolean _continuer;
	static int _indiceImage = 0;
	private File [] _rawImage;
	private Calibration _cal = new Calibration();
	private String _workDir;
	private double _vmin, _vmax;
	private boolean _2D3DAnalysis, _3DAnalysis;
	
	public ProcessImageSegmentaion(NucleusPipelineBatchPlugin_ nucleusPipelineBatchPlugin, File[] inputFile)
	{
		_rawImage = inputFile;
		_vmin = nucleusPipelineBatchPlugin.getSegMinValue();
		_vmax = nucleusPipelineBatchPlugin.getSegMaxValue();
		_workDir = nucleusPipelineBatchPlugin.getWorkDir();
		_cal.pixelDepth = nucleusPipelineBatchPlugin.getPixelDepth();
		_cal.pixelWidth = nucleusPipelineBatchPlugin.getPixelWidth();
		_cal.pixelHeight = nucleusPipelineBatchPlugin.getPixelHeight();
		_cal.setUnit(nucleusPipelineBatchPlugin.getUnit());
		_2D3DAnalysis = nucleusPipelineBatchPlugin.is2D3DAnalysis();
		_3DAnalysis = nucleusPipelineBatchPlugin.is3DAnalysis();
		
	}

	public void go() throws InterruptedException
	{
		_nbLance = 0;
		ArrayList<Thread> imageThread = new ArrayList<Thread>() ;
		int j = 0;
		OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
		int nbProc = bean.getAvailableProcessors();
		IJ.log("nb processeur "+nbProc);
		if (nbProc!=1) nbProc = nbProc/2; 
		IJ.log("nb processeur utilisé "+nbProc);
		for (int i = 0; i <_rawImage.length; ++i)
		{
			IJ.log("Image processed "+_rawImage[i] +" "+i);
			_continuer = false;
			_indiceImage = i;
			IJ.log("image"+(i+1)+" / "+_rawImage.length);
			ImagePlus imagePlus = IJ.openImage(_rawImage[i].toString());
			imagePlus.setCalibration(_cal);
			imageThread.add(new RunnableImageSegmentation(imagePlus,_vmin,_vmax,_workDir,_2D3DAnalysis,_3DAnalysis));
			imageThread.get(j).start();
			while (_continuer == false)
				Thread.sleep(10);
			while (_nbLance >= 6)
				Thread.sleep(10);
			++j;
		}
		
		for (int i = 0; i < imageThread.size(); ++i)
			while(imageThread.get(i).isAlive())
				Thread.sleep(10);
	}
}
