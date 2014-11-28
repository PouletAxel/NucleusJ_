
package gred.nucleus.multiThread;
import gred.nucleus.plugins.NucleusSegmentationAndAnalysisBatchPlugin_;
import gred.nucleus.plugins.NucleusSegmentationBatchPlugin_;
import gred.nucleus.plugins.OtherBatch_;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;

import java.io.File;
import java.util.ArrayList;

public class ProcessImageOtherSeg
{
	
	/**
	 * multi thread class for the nucleus segmentation
	 * 
	 * @author Poulet Axel
	 *
	 */
		static int _nbLance = 0;
		static boolean _continuer;
		static int _indiceImage = 0;


		/**
		 * method to run the segmentation and analysis with the features chosen by the user
		 * 
		 * @param otherBatch_
		 * @param tInputFile
		 * @param doAnalysis
		 * @throws InterruptedException
		 */
		public void go(OtherBatch_ otherBatch_, File[] tInputFile, boolean doAnalysis) throws InterruptedException
		{
			Calibration calibration = new Calibration();
			calibration.pixelDepth = otherBatch_.getZCalibration();
			calibration.pixelWidth = otherBatch_.getXCalibration();
			calibration.pixelHeight = otherBatch_.getYCalibration();
			calibration.setUnit(otherBatch_.getUnit());
			_nbLance = 0;
			ArrayList<Thread> arrayListImageThread = new ArrayList<Thread>() ;
			int nbCpu = otherBatch_.getNbCpu();
			
			for (int i = 0; i < tInputFile.length; ++i)
			{
				IJ.log("Image processed "+tInputFile[i] +" "+i);
				_continuer = false;
				_indiceImage = i;
				IJ.log("image"+(i+1)+" / "+tInputFile.length);
				ImagePlus imagePlusInput = IJ.openImage(tInputFile[i].toString());
				imagePlusInput.setCalibration(calibration);
				arrayListImageThread.add
				(new RunnableImageSegmentationOther
					 (
							 imagePlusInput,
							 otherBatch_.getMinVolume(),
							 otherBatch_.getMaxVolume(),
							 otherBatch_.getWorkDirectory()
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

}
