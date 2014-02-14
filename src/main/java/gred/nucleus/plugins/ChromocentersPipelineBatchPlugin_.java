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
import gred.nucleus.dialogs.ChromocentersPipelineBatchDialog;
import gred.nucleus.utils.ListerFichier;

/**
 * 
 * @author gred
 *
 */
public class ChromocentersPipelineBatchPlugin_ implements PlugIn
{
	/** Voxel calibration in µm*/
	double _dimX, _dimY, _dimZ;
	/** Voxel unit*/
	String _unit, _workDirectory;
	/**	 */
	double _segMin, _segMax;
	/** */
	String[] _tFile;
	/** */
	private ListerFichier _lF;
	
	/**
	 * 
	 */
	public void run(String arg)
	{
		ChromocentersPipelineBatchDialog jfpfso = new ChromocentersPipelineBatchDialog();
	
		while( jfpfso.isShowing())
		{
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
	    }	
		if (jfpfso.isStart())
		{
			_lF = new ListerFichier (jfpfso.getDirRawData());
			_lF.run();
			if (_lF.isDirectoryOrFIleExist(".+RawDataNucleus.+") && _lF.isDirectoryOrFIleExist(".+SegmentedDataNucleus.+")&& _lF.isDirectoryOrFIleExist(".+SegmentedDataCc.+"))
			{
				_dimX =jfpfso.getx();
				_dimY = jfpfso.gety();
				_dimZ = jfpfso.getz();
				_unit = jfpfso.getUnit();
				
				String choiceRhf;
				if (jfpfso.isRHFVolumeAndIntensity())	choiceRhf = "Volume and intensity";
				else if (jfpfso.isRhfVolume())	choiceRhf = "Volume";
				else choiceRhf = "Intensity";
				
				ArrayList<String> imageCc = _lF.fileSearchList(".+SegmentedDataCc.+");
				_workDirectory = jfpfso.getWorkDirectory();
				String nameFileCcNuc = _workDirectory+File.separator+"NucAndCcParameters.tab";
				String nameFileCc = _workDirectory+File.separator+"CcParameters.tab";
				for (int i = 0; i < imageCc.size(); ++i)
				{
					IJ.log("image"+(i+1)+" / "+(imageCc.size())+"   "+imageCc.get(i));
					String plopi = imageCc.get(i);
					String nucRaw = plopi.replaceAll("SegmentedDataCc", "RawDataNucleus");
					String nucBinary= plopi.replaceAll("SegmentedDataCc", "SegmentedDataNucleus");
					IJ.log(nucRaw);
					IJ.log(nucBinary);
				
					if (_lF.isDirectoryOrFIleExist(nucRaw) && _lF.isDirectoryOrFIleExist(nucBinary))
					{
						ImagePlus ipCc = IJ.openImage(imageCc.get(i));
						ImagePlus ipNucBin = IJ.openImage(nucBinary);
						ImagePlus ipRawNuc = IJ.openImage(nucRaw);
						Calibration cal = new Calibration();
						cal.pixelDepth = _dimZ;
						cal.pixelWidth = _dimX;
						cal.pixelHeight = _dimY;
						cal.setUnit(_unit);
						ipCc.setCalibration(cal);
						ipNucBin.setCalibration(cal);
						ipRawNuc.setCalibration(cal);
						
						try
						{
							if (jfpfso.isNucAndCcAnalysis())
							{
								ChromocenterAnalysis ca = new ChromocenterAnalysis(ipCc,ipNucBin);
								IJ.log("ca compute");
								ca.computeParametersChromocenter(nameFileCc);
								NucleusChromocentersAnalysis naacc = new NucleusChromocentersAnalysis(ipRawNuc,ipCc,ipNucBin); 
								IJ.log("naac compute");
								naacc.computeParameters(nameFileCcNuc,choiceRhf);
							}
							else if (jfpfso.isCcAnalysis())
							{
								ChromocenterAnalysis ca = new ChromocenterAnalysis(ipCc,ipNucBin);
								ca.computeParametersChromocenter(nameFileCc);
							}
							else
							{
								NucleusChromocentersAnalysis naacc = new NucleusChromocentersAnalysis(ipRawNuc,ipCc,ipNucBin); 
								naacc.computeParameters(nameFileCcNuc,choiceRhf);
							}
						}
						catch (IOException e) {	e.printStackTrace(); }
					}
					else
					{
						IJ.log("Image name problem :  the image "+plopi+" is not find in the directory SegmentedDataNucleusor or RawDataNucleus, see nameProblem.txt in "+_workDirectory);
						BufferedWriter output;
					    try
					    {
					    	 output = new BufferedWriter(new FileWriter(_workDirectory+File.separator+"nameProblem.txt", true));
					    	 output.write(plopi+"\n");
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
