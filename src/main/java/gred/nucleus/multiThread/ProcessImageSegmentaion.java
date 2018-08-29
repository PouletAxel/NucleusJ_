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
public class ProcessImageSegmentaion{
	static int m_nbLance = 0;
	static boolean m_continuer;
	static int m_indiceImage = 0;


	public ProcessImageSegmentaion(){}

	/**
	 * method to run the segmentation and analysis with the features chosen by the user
	 * 
	 * @param nuc
	 * @param tInputFile
	 * @param doAnalysis
	 * @throws InterruptedException
	 */
	public void go(NucleusSegmentationAndAnalysisBatchPlugin_ nuc, File[] tInputFile, boolean doAnalysis) throws InterruptedException{
		Calibration calibration = new Calibration();
		calibration.pixelDepth = nuc.getZCalibration();
		calibration.pixelWidth = nuc.getXCalibration();
		calibration.pixelHeight = nuc.getYCalibration();
		calibration.setUnit(nuc.getUnit());
		m_nbLance = 0;
		ArrayList<Thread> arrayListImageThread = new ArrayList<Thread>() ;
		int nbCpu = nuc.getNbCpu();
		
		for (int i = 0; i < tInputFile.length; ++i){
			IJ.log("Image processed "+tInputFile[i] +" "+i);
			m_continuer = false;
			m_indiceImage = i;
			IJ.log("image"+(i+1)+" / "+tInputFile.length);
			ImagePlus imagePlusInput = IJ.openImage(tInputFile[i].toString());
			imagePlusInput.setCalibration(calibration);
			arrayListImageThread.add(
				new RunnableImageSegmentation(
						 imagePlusInput, nuc.getMinVolume(),
						 nuc.getMaxVolume(), nuc.getWorkDirectory(),
						 nuc.is2D3DAnalysis(), nuc.is3DAnalysis(),
						 doAnalysis
				)
			);
			arrayListImageThread.get(i).start();
			
			while (m_continuer == false)
				Thread.sleep(10);
			while (m_nbLance >=nbCpu)
				Thread.sleep(10);
		}
		for (int i = 0; i < arrayListImageThread.size(); ++i)
			while(arrayListImageThread.get(i).isAlive())
				Thread.sleep(10);
	}

	/**
	 * method to run the segmentation with the features chosen by the user
	 * 
	 * @param nuc
	 * @param tInputFile
	 * @param doAnalysis
	 * @throws InterruptedException
	 */
	public void go(NucleusSegmentationBatchPlugin_ nuc, File[] tInputFile, boolean doAnalysis) throws InterruptedException{
		Calibration calibration = new Calibration();
		calibration.pixelDepth = nuc.getZCalibration();
		calibration.pixelWidth = nuc.getXCalibration();
		calibration.pixelHeight = nuc.getYCalibration();
		calibration.setUnit(nuc.getUnit());
		m_nbLance = 0;
		ArrayList<Thread> arrayListImageThread = new ArrayList<Thread>() ;
		int nbCpu =nuc.getNbCpu();
		IJ.log("Number processor used "+nbCpu);
		for (int i = 0; i <tInputFile.length; ++i){
			IJ.log("Image processed "+tInputFile[i] +" "+i);
			m_continuer = false;
			m_indiceImage = i;
			IJ.log("image"+(i+1)+" / "+tInputFile.length);
			ImagePlus imagePlusInput = IJ.openImage(tInputFile[i].toString());
			imagePlusInput.setCalibration(calibration);
			arrayListImageThread.add(
				new RunnableImageSegmentation(
						imagePlusInput, nuc.getMinVolume(),
						nuc.getMaxVolume(), nuc.getWorkDirectory(),
							false, false,
							doAnalysis
					)
			);
			arrayListImageThread.get(i).start();
			while (m_continuer == false)
				Thread.sleep(10);
			while (m_nbLance > nbCpu)
				Thread.sleep(10);
		}
		for (int i = 0; i < arrayListImageThread.size(); ++i)
			while(arrayListImageThread.get(i).isAlive())
				Thread.sleep(10);
	}
}