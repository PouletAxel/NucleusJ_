package gred.nucleus.plugins;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.plugin.PlugIn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import gred.nucleus.core.ChromocenterAnalysis;
import gred.nucleus.core.NucleusChromocentersAnalysis;
import gred.nucleus.dialogs.ChromocentersAnalysisPipelineBatchDialog;
import gred.nucleus.utils.FileList;

/**
 * 
 * @author Poulet Axel
 *
 */
public class ChromocentersAnalysisBatchPlugin_ implements PlugIn
{
		
	/**
	 * 
	 */
	public void run(String arg)
	{
		ChromocentersAnalysisPipelineBatchDialog chromocentersPipelineBatchDialog = new ChromocentersAnalysisPipelineBatchDialog();
		while( chromocentersPipelineBatchDialog.isShowing())
		{
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
	    }	
		if (chromocentersPipelineBatchDialog.isStart())
		{
			FileList fileList = new FileList ();
			File[] tFileRawImage = fileList.run(chromocentersPipelineBatchDialog.getRawDataDirectory());
			
			if(	fileList.isDirectoryOrFileExist(".+RawDataNucleus.+",tFileRawImage) && 
				fileList.isDirectoryOrFileExist(".+SegmentedDataNucleus.+",tFileRawImage)&&
				fileList.isDirectoryOrFileExist(".+SegmentedDataCc.+",tFileRawImage))
			{
				double xCalibration = chromocentersPipelineBatchDialog.getXCalibration();
				double yCalibration = chromocentersPipelineBatchDialog.getYCalibration();
				double zCalibration = chromocentersPipelineBatchDialog.getZCalibration();
				String unit = chromocentersPipelineBatchDialog.getUnit();
				String rhfChoice;
				if (chromocentersPipelineBatchDialog.isRHFVolumeAndIntensity())
					rhfChoice = "Volume and intensity";
				else if (chromocentersPipelineBatchDialog.isRhfVolume())
					rhfChoice = "Volume";
				else
					rhfChoice = "Intensity";
				
				ArrayList<String> arrayListImageChromocenter = fileList.fileSearchList(".+SegmentedDataCc.+",tFileRawImage);
				String workDirectory = chromocentersPipelineBatchDialog.getWorkDirectory();
				String nameFileChromocenterAndNucleus = workDirectory+File.separator+"NucAndCcParameters.tab";
				String nameFileChromocenter = workDirectory+File.separator+"CcParameters.tab";
				
				for (int i = 0; i < arrayListImageChromocenter.size(); ++i)
				{
					IJ.log("image"+(i+1)+" / "+arrayListImageChromocenter.size());
					String pathImageChromocenter = arrayListImageChromocenter.get(i);
					String pathNucleusRaw = pathImageChromocenter.replaceAll("SegmentedDataCc", "RawDataNucleus");
					String pathNucleusSegmented= pathImageChromocenter.replaceAll("SegmentedDataCc", "SegmentedDataNucleus");
					IJ.log(pathNucleusRaw);
					IJ.log(pathNucleusSegmented);
					if (fileList.isDirectoryOrFileExist(pathNucleusRaw,tFileRawImage) &&
						fileList.isDirectoryOrFileExist(pathNucleusSegmented,tFileRawImage))
					{
						ImagePlus imagePlusChromocenter = IJ.openImage(arrayListImageChromocenter.get(i));
						ImagePlus imagePlusSegmented = IJ.openImage(pathNucleusSegmented);
						ImagePlus imagePlusInput = IJ.openImage(pathNucleusRaw);
						Calibration calibration = new Calibration();
						calibration.pixelDepth = zCalibration;
						calibration.pixelWidth = xCalibration;
						calibration.pixelHeight = yCalibration;
						calibration.setUnit(unit);
						imagePlusChromocenter.setCalibration(calibration);
						imagePlusSegmented.setCalibration(calibration);
						imagePlusInput.setCalibration(calibration);
						try
						{
							if (chromocentersPipelineBatchDialog.isNucAndCcAnalysis())
							{
								ChromocenterAnalysis chromocenterAnalysis = new ChromocenterAnalysis();
								chromocenterAnalysis.computeParametersChromocenter
								(
									nameFileChromocenter,
									imagePlusSegmented,
									imagePlusChromocenter
								);
								IJ.log("chromocenterAnalysis is computing ...");
								NucleusChromocentersAnalysis nucleusChromocenterAnalysis = new NucleusChromocentersAnalysis(); 
								IJ.log("nucleusChromocenterAnalysis is computing...");
								nucleusChromocenterAnalysis.computeParameters
								(
									nameFileChromocenterAndNucleus,
									rhfChoice,
									imagePlusInput,
									imagePlusSegmented,
									imagePlusChromocenter
								);
							}
							else if (chromocentersPipelineBatchDialog.isCcAnalysis())
							{
								ChromocenterAnalysis chromocenterAnalysis = new ChromocenterAnalysis();
								chromocenterAnalysis.computeParametersChromocenter
								(
									nameFileChromocenter,
									imagePlusSegmented,
									imagePlusChromocenter
								);
							}
							else
							{
								NucleusChromocentersAnalysis nucleusChromocenterAnalysis = new NucleusChromocentersAnalysis(); 
								nucleusChromocenterAnalysis.computeParameters
								(
									nameFileChromocenterAndNucleus,
									rhfChoice,
									imagePlusInput,
									imagePlusSegmented,
									imagePlusChromocenter
								);
							}
						}
						catch (IOException e) {	e.printStackTrace(); }
					}
					else
					{
						IJ.log("Image name problem :  the image "+pathImageChromocenter
							+" is not find in the directory SegmentedDataNucleus or RawDataNucleus, see nameProblem.txt in "
							+workDirectory);
						BufferedWriter bufferedWriterLogFile;
					    try
					    {
					    	 bufferedWriterLogFile = new BufferedWriter(new FileWriter(workDirectory+File.separator+"logNameProblem.log", true));
					    	 bufferedWriterLogFile.write(pathImageChromocenter+"\n");
					    	 bufferedWriterLogFile.flush();
					    	 bufferedWriterLogFile.close();  
					    }
					    catch (IOException e) {	e.printStackTrace();}
					 } 	  
				}
			}
			else	{	IJ.showMessage("There are no the three subdirectories  (See the directory name) or subDirectories are empty"); }
		}
	}
}