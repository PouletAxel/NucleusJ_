package gred.nucleus.multiThread;
import gred.nucleus.plugins.NucleusSegmentationAndAnalysisBatchPlugin_;
import gred.nucleus.plugins.NucleusSegmentationBatchPlugin_;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;

import java.io.File;
import java.util.ArrayList;

/**
 * multi thread class for the nucleus segmentation
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
	 * method to run the segmentation and analysis with the features chosen by the user
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
		int nbCpu = nucleusSegmentationAndAnalysisBatchPlugin.getNbCpu();
		
		for (int i = 0; i < tInputFile.length; ++i)
		{
			IJ.log("Image processed "+tInputFile[i] +" "+i);
			_continuer = false;
			_indiceImage = i;
			IJ.log("image"+(i+1)+" / "+tInputFile.length);
			ImagePlus imagePlusInput = IJ.openImage(tInputFile[i].toString());
			imagePlusInput.setCalibration(calibration);
			arrayListImageThread.add
			(new RunnableImageSegmentation
				 (
						 imagePlusInput,
						 nucleusSegmentationAndAnalysisBatchPlugin.getMinVolume(),
						 nucleusSegmentationAndAnalysisBatchPlugin.getMaxVolume(),
						 nucleusSegmentationAndAnalysisBatchPlugin.getWorkDirectory(),
						 nucleusSegmentationAndAnalysisBatchPlugin.is2D3DAnalysis(),
						 nucleusSegmentationAndAnalysisBatchPlugin.is3DAnalysis(),
						 doAnalysis
				)
			);
			arrayListImageThread.get(i).start();
			
			while (_continuer == false)
				Thread.sleep(10);
			while (_nbLance >=nbCpu)
				Thread.sleep(10);
		}
		for (int i = 0; i < arrayListImageThread.size(); ++i)
			while(arrayListImageThread.get(i).isAlive())
				Thread.sleep(10);
	}

	/**
	 * method to run the segmentation with the features chosen by the user
	 * 
	 * @param nucleusSegmentationBatchPlugin
	 * @param tInputFile
	 * @param doAnalysis
	 * @throws InterruptedException
	 */
	public void go(NucleusSegmentationBatchPlugin_ nucleusSegmentationBatchPlugin, File[] tInputFile, boolean doAnalysis) throws InterruptedException
	{
		Calibration calibration = new Calibration();
		calibration.pixelDepth = nucleusSegmentationBatchPlugin.getZCalibration();
		calibration.pixelWidth = nucleusSegmentationBatchPlugin.getXCalibration();
		calibration.pixelHeight = nucleusSegmentationBatchPlugin.getYCalibration();
		calibration.setUnit(nucleusSegmentationBatchPlugin.getUnit());
		_nbLance = 0;
		ArrayList<Thread> arrayListImageThread = new ArrayList<Thread>() ;
		int nbCpu =nucleusSegmentationBatchPlugin.getNbCpu();
		IJ.log("Number processor used "+nbCpu);
		for (int i = 0; i <tInputFile.length; ++i)
		{
			IJ.log("Image processed "+tInputFile[i] +" "+i);
			_continuer = false;
			_indiceImage = i;
			IJ.log("image"+(i+1)+" / "+tInputFile.length);
			ImagePlus imagePlusInput = IJ.openImage(tInputFile[i].toString());
			imagePlusInput.setCalibration(calibration);
			arrayListImageThread.add
			(new RunnableImageSegmentation
					(
							imagePlusInput,
							nucleusSegmentationBatchPlugin.getMinVolume(),
							nucleusSegmentationBatchPlugin.getMaxVolume(),
							nucleusSegmentationBatchPlugin.getWorkDirectory(),
							false,
							false,
							doAnalysis
					)
			);
			arrayListImageThread.get(i).start();
			while (_continuer == false)
				Thread.sleep(10);
			while (_nbLance > nbCpu)
				Thread.sleep(10);
		}
		for (int i = 0; i < arrayListImageThread.size(); ++i)
			while(arrayListImageThread.get(i).isAlive())
				Thread.sleep(10);
	}
}