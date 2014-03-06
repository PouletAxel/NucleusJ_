package gred.nucleus.multiThread;
import gred.nucleus.plugins.NucleusSegmentationAndAnalysisBatchPlugin_;
import gred.nucleus.plugins.NucleusSegmentationBatchPlugin_;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;

import java.io.File;
import java.util.ArrayList;
/**
 * 
 * @author Poulet Axel
 *
 */
public class ProcessImageSegmentaion
{
	static int _nbLance = 0;
	static boolean _continuer;
	static int _indiceImage = 0;

	
	public ProcessImageSegmentaion(){}
	
	/**
	 * 
	 * @param nucleusSegmentationAndAnalysisBatchPlugin
	 * @param tInputFile
	 * @param doAnalysis
	 * @throws InterruptedException
	 */
	public void go(NucleusSegmentationAndAnalysisBatchPlugin_ nucleusSegmentationAndAnalysisBatchPlugin, File[] tInputFile, boolean doAnalysis) throws InterruptedException
	{
		Calibration calibration = new Calibration();
		calibration.pixelDepth = nucleusSegmentationAndAnalysisBatchPlugin.getZCalibration();
		calibration.pixelWidth = nucleusSegmentationAndAnalysisBatchPlugin.getXCalibration();
		calibration.pixelHeight = nucleusSegmentationAndAnalysisBatchPlugin.getYCalibration();
		calibration.setUnit(nucleusSegmentationAndAnalysisBatchPlugin.getUnit());
		_nbLance = 0;
		ArrayList<Thread> arrayListImageThread = new ArrayList<Thread>() ;
		int j = 0;
		IJ.log("Number processor used "+nucleusSegmentationAndAnalysisBatchPlugin.getNbCpu());
		for (int i = 0; i <tInputFile.length; ++i)
		{
			IJ.log("Image processed "+tInputFile[i] +" "+i);
			_continuer = false;
			_indiceImage = i;
			IJ.log("image"+(i+1)+" / "+tInputFile.length);
			ImagePlus imagePlus = IJ.openImage(tInputFile[i].toString());
			imagePlus.setCalibration(calibration);
			arrayListImageThread.add(new RunnableImageSegmentation(imagePlus,nucleusSegmentationAndAnalysisBatchPlugin.getMinVolume(),nucleusSegmentationAndAnalysisBatchPlugin.getMaxVolume()
					,nucleusSegmentationAndAnalysisBatchPlugin.getWorkDirectory(),nucleusSegmentationAndAnalysisBatchPlugin.is2D3DAnalysis(),
					nucleusSegmentationAndAnalysisBatchPlugin.is3DAnalysis(),doAnalysis));
			arrayListImageThread.get(j).start();
			while (_continuer == false)
				Thread.sleep(10);
			while (_nbLance >= nucleusSegmentationAndAnalysisBatchPlugin.getNbCpu())
				Thread.sleep(10);
			++j;
		}
		for (int i = 0; i < arrayListImageThread.size(); ++i)
			while(arrayListImageThread.get(i).isAlive())
				Thread.sleep(10);
	}
	
	/**
	 * 
	 * @param nucleusSegmentationBatchPlugin
	 * @param tInputFile
	 * @param doAnalysis
	 * @throws InterruptedException
	 */
	public void go(NucleusSegmentationBatchPlugin_ nucleusSegmentationBatchPlugin, File[] tInputFile, boolean doAnalysis) throws InterruptedException
	{
		Calibration cal = new Calibration();
		cal.pixelDepth = nucleusSegmentationBatchPlugin.getZCalibration();
		cal.pixelWidth = nucleusSegmentationBatchPlugin.getXCalibration();
		cal.pixelHeight = nucleusSegmentationBatchPlugin.getYCalibration();
		cal.setUnit(nucleusSegmentationBatchPlugin.getUnit());
		_nbLance = 0;
		ArrayList<Thread> arrayListImageThread = new ArrayList<Thread>() ;
		int j = 0;
		IJ.log("Number processor used "+nucleusSegmentationBatchPlugin.getNbCpu());
		for (int i = 0; i <tInputFile.length; ++i)
		{
			IJ.log("Image processed "+tInputFile[i] +" "+i);
			_continuer = false;
			_indiceImage = i;
			IJ.log("image"+(i+1)+" / "+tInputFile.length);
			ImagePlus imagePlus = IJ.openImage(tInputFile[i].toString());
			imagePlus.setCalibration(cal);
			arrayListImageThread.add(new RunnableImageSegmentation(imagePlus,nucleusSegmentationBatchPlugin.getMinVolume(),nucleusSegmentationBatchPlugin.getMaxVolume()
					,nucleusSegmentationBatchPlugin.getWorkDirectory(),false, false ,doAnalysis));
			arrayListImageThread.get(j).start();
			while (_continuer == false)
				Thread.sleep(10);
			while (_nbLance >= nucleusSegmentationBatchPlugin.getNbCpu())
				Thread.sleep(10);
			++j;
		}
		for (int i = 0; i < arrayListImageThread.size(); ++i)
			while(arrayListImageThread.get(i).isAlive())
				Thread.sleep(10);
	}
}