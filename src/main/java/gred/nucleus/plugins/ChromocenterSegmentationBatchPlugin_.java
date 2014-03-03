package gred.nucleus.plugins;
import java.io.File;
import gred.nucleus.dialogs.*;
import gred.nucleus.multiThread.*;
import gred.nucleus.utils.FileList;
import ij.plugin.PlugIn;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ChromocenterSegmentationBatchPlugin_ implements PlugIn
{

		public void run(String arg)
		{
			ChromocenterSegmentationPipelineBatchDialog _chromocenterSegmentationPipelineBatchDialog = new ChromocenterSegmentationPipelineBatchDialog();
			while( _chromocenterSegmentationPipelineBatchDialog.isShowing())
			{
				try {Thread.sleep(1);}
				catch (InterruptedException e) {e.printStackTrace();}
		    }	
			if (_chromocenterSegmentationPipelineBatchDialog.isStart())
			{
				FileList FileList = new FileList ();
				FileList.run(_chromocenterSegmentationPipelineBatchDialog.getDirRawData());
				if (FileList.isDirectoryOrFIleExist(".+RawDataNucleus.+") && FileList.isDirectoryOrFIleExist(".+SegmentedDataNucleus.+"))
				{
					double dimX =_chromocenterSegmentationPipelineBatchDialog.getx();
					double dimY = _chromocenterSegmentationPipelineBatchDialog.gety();
					double dimZ = _chromocenterSegmentationPipelineBatchDialog.getz();
					String unit = _chromocenterSegmentationPipelineBatchDialog.getUnit();
			
					
					ArrayList<String> imageSegmenetedDataNucleusList = FileList.fileSearchList(".+SegmentedDataNucleus.+");
					String workDirectory = _chromocenterSegmentationPipelineBatchDialog.getWorkDirectory();
					for (int i = 0; i < imageSegmenetedDataNucleusList.size(); ++i)
					{
						IJ.log("image"+(i+1)+" / "+(imageSegmenetedDataNucleusList.size())+"   "+imageSegmenetedDataNucleusList.get(i));
						String pathImageSegmentedNucleus = imageSegmenetedDataNucleusList.get(i);
						String pathNucleusRaw = pathImageSegmentedNucleus.replaceAll("SegmentedDataNucleus", "RawDataNucleus");
						IJ.log(pathNucleusRaw);

					
						if (FileList.isDirectoryOrFIleExist(pathNucleusRaw))
						{
							ImagePlus imagePlusSegmentedNucleus = IJ.openImage(pathImageSegmentedNucleus);
							ImagePlus imagePlusRaw = IJ.openImage(pathNucleusRaw);
							Calibration cal = new Calibration();
							cal.pixelDepth = dimZ;
							cal.pixelWidth = dimX;
							cal.pixelHeight = dimY;
							cal.setUnit(unit);
							imagePlusSegmentedNucleus.setCalibration(cal);
							imagePlusRaw.setCalibration(cal);
							
							try
							{
								if (chromocentersPipelineBatchDialog.isNucAndCcAnalysis())
								{
									ChromocenterAnalysis chromocenterAnalysis = new ChromocenterAnalysis();
									chromocenterAnalysis.computeParametersChromocenter(nameFileChromocenter,imagePlusSegmentedNucleus,imagePlusChromocenter);
									IJ.log("chromocenter analysis is computing ...");
									NucleusChromocentersAnalysis nucleusChromocenterAnalysis = new NucleusChromocentersAnalysis(); 
									IJ.log("nucleusChromocenterAnalysis is computing...");
									nucleusChromocenterAnalysis.computeParameters(nameFileChromocenterAndNucleus,choiceRhf, imagePlusRaw, imagePlusSegmentedNucleus, imagePlusChromocenter);
								}
								else if (chromocentersPipelineBatchDialog.isCcAnalysis())
								{
									ChromocenterAnalysis chromocenterAnalysis = new ChromocenterAnalysis();
									chromocenterAnalysis.computeParametersChromocenter(nameFileChromocenter,imagePlusSegmentedNucleus,imagePlusChromocenter);
								}
								else
								{
									NucleusChromocentersAnalysis nucleusChromocenterAnalysis = new NucleusChromocentersAnalysis(); 
									nucleusChromocenterAnalysis.computeParameters(nameFileChromocenterAndNucleus,choiceRhf, imagePlusRaw, imagePlusSegmentedNucleus, imagePlusChromocenter);
								}
							}
							catch (IOException e) {	e.printStackTrace(); }
						}
						else
						{
							IJ.log("Image name problem :  the image "+pathImageSegmentedNucleus+" is not find in the directory SegmentedDataNucleusor or RawDataNucleus, see nameProblem.txt in "+workDirectory);
							BufferedWriter output;
						    try
						    {
						    	 output = new BufferedWriter(new FileWriter(workDirectory+File.separator+"nameProblem.txt", true));
						    	 output.write(pathImageSegmentedNucleus+"\n");
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


}
