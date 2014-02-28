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
 * @author gred
 *
 */
public class ChromocentersPipelineBatchPlugin_ implements PlugIn
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
			FileList FileList = new FileList ();
			FileList.run(chromocentersPipelineBatchDialog.getDirRawData());
			if (FileList.isDirectoryOrFIleExist(".+RawDataNucleus.+") && FileList.isDirectoryOrFIleExist(".+SegmentedDataNucleus.+")&& FileList.isDirectoryOrFIleExist(".+SegmentedDataCc.+"))
			{
				double dimX =chromocentersPipelineBatchDialog.getx();
				double dimY = chromocentersPipelineBatchDialog.gety();
				double dimZ = chromocentersPipelineBatchDialog.getz();
				String unit = chromocentersPipelineBatchDialog.getUnit();
				
				String choiceRhf;
				if (chromocentersPipelineBatchDialog.isRHFVolumeAndIntensity())	choiceRhf = "Volume and intensity";
				else if (chromocentersPipelineBatchDialog.isRhfVolume())	choiceRhf = "Volume";
				else choiceRhf = "Intensity";
				
				ArrayList<String> imageChromocenterList = FileList.fileSearchList(".+SegmentedDataCc.+");
				String workDirectory = chromocentersPipelineBatchDialog.getWorkDirectory();
				String nameFileChromocenterAndNucleus = workDirectory+File.separator+"NucAndCcParameters.tab";
				String nameFileChromocenter = workDirectory+File.separator+"CcParameters.tab";
				for (int i = 0; i < imageChromocenterList.size(); ++i)
				{
					IJ.log("image"+(i+1)+" / "+(imageChromocenterList.size())+"   "+imageChromocenterList.get(i));
					String pathImageChromocenter = imageChromocenterList.get(i);
					String pathNucleusRaw = pathImageChromocenter.replaceAll("SegmentedDataCc", "RawDataNucleus");
					String pathNucleusBinary= pathImageChromocenter.replaceAll("SegmentedDataCc", "SegmentedDataNucleus");
					IJ.log(pathNucleusRaw);
					IJ.log(pathNucleusBinary);
				
					if (FileList.isDirectoryOrFIleExist(pathNucleusRaw) && FileList.isDirectoryOrFIleExist(pathNucleusBinary))
					{
						ImagePlus imagePlusChromocenter = IJ.openImage(imageChromocenterList.get(i));
						ImagePlus imagePlusBinary = IJ.openImage(pathNucleusBinary);
						ImagePlus imagePlusRaw = IJ.openImage(pathNucleusRaw);
						Calibration cal = new Calibration();
						cal.pixelDepth = dimZ;
						cal.pixelWidth = dimX;
						cal.pixelHeight = dimY;
						cal.setUnit(unit);
						imagePlusChromocenter.setCalibration(cal);
						imagePlusBinary.setCalibration(cal);
						imagePlusRaw.setCalibration(cal);
						
						try
						{
							if (chromocentersPipelineBatchDialog.isNucAndCcAnalysis())
							{
								ChromocenterAnalysis chromocenterAnalysis = new ChromocenterAnalysis();
								chromocenterAnalysis.computeParametersChromocenter(nameFileChromocenter,imagePlusBinary,imagePlusChromocenter);
								IJ.log("chromocenter analysis is computing ...");
								NucleusChromocentersAnalysis nucleusChromocenterAnalysis = new NucleusChromocentersAnalysis(); 
								IJ.log("nucleusChromocenterAnalysis is computing...");
								nucleusChromocenterAnalysis.computeParameters(nameFileChromocenterAndNucleus,choiceRhf, imagePlusRaw, imagePlusBinary, imagePlusChromocenter);
							}
							else if (chromocentersPipelineBatchDialog.isCcAnalysis())
							{
								ChromocenterAnalysis chromocenterAnalysis = new ChromocenterAnalysis();
								chromocenterAnalysis.computeParametersChromocenter(nameFileChromocenter,imagePlusBinary,imagePlusChromocenter);
							}
							else
							{
								NucleusChromocentersAnalysis nucleusChromocenterAnalysis = new NucleusChromocentersAnalysis(); 
								nucleusChromocenterAnalysis.computeParameters(nameFileChromocenterAndNucleus,choiceRhf, imagePlusRaw, imagePlusBinary, imagePlusChromocenter);
							}
						}
						catch (IOException e) {	e.printStackTrace(); }
					}
					else
					{
						IJ.log("Image name problem :  the image "+pathImageChromocenter+" is not find in the directory SegmentedDataNucleusor or RawDataNucleus, see nameProblem.txt in "+workDirectory);
						BufferedWriter output;
					    try
					    {
					    	 output = new BufferedWriter(new FileWriter(workDirectory+File.separator+"nameProblem.txt", true));
					    	 output.write(pathImageChromocenter+"\n");
					    	 output.flush();
					    	 output.close();  
					    }
					    catch (IOException e) {	e.printStackTrace();}
					 } 	  
				}
			}
			else	{	IJ.showMessage("There are no the three subdirectories or the subDirectories is empty"); }
		}
	}
}
