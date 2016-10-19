package gred.nucleus.plugins;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.plugin.PlugIn;
import gred.nucleus.core.ChromocenterAnalysis;
import gred.nucleus.core.NucleusChromocentersAnalysis;

/**
 *  Method to analyse the chromocenter
 * 
 * @author Poulet Axel
 *
 */
public class ChromocentersAnalysisPlugin_   implements PlugIn
{
	
	/**
	 * 
	 */
	public void run(String arg)
	{
		int indiceCcImage = 0;
		int indiceRawImage = 0;
		int indiceSegmentedImage = 0;
		double xCalibration = 1,yCalibration=1,zCalibration=1;
		String unit = "pixel";
		int[] wList = WindowManager.getIDList();
		if (wList == null)
		{
			IJ.noImage();
			return;
		}
		String[] titles = new String[wList.length];
		for (int i = 0; i < wList.length; i++)
		{
			ImagePlus imagePlus = WindowManager.getImage(wList[i]);
			
			if (imagePlus != null)
			{
				if (i == 0)
				{
					Calibration cal = imagePlus.getCalibration();
					xCalibration = cal.pixelWidth;
					yCalibration= cal.pixelHeight;
					zCalibration= cal.pixelDepth;
					unit = cal.getUnit();
				}
				titles[i] = imagePlus.getTitle();
			}
			else
				titles[i] = "";
		}
		GenericDialog genericDialog = new GenericDialog("Chromocenter Analysis", IJ.getInstance());
		genericDialog.addChoice("Raw image",titles,titles[indiceRawImage]);
		genericDialog.addChoice("Nucleus Segmented",titles,titles[indiceSegmentedImage]);
		genericDialog.addChoice("Chromocenters image Segmented",titles,titles[indiceCcImage]);
		genericDialog.addNumericField("x calibration", xCalibration,3);
      	genericDialog.addNumericField("y calibration", yCalibration, 3);
      	genericDialog.addNumericField("z calibration).",zCalibration, 3);
    	genericDialog.addStringField("Unit",unit,10);
       	genericDialog.addRadioButtonGroup
       	(
       		"Type of RHF ",
       		new String[]{"Volume and intensity","Volume","Intensity"}
       		, 1, 3,
       		"Volume and intensity"
       	);
     	genericDialog.addRadioButtonGroup
     	(
     		"Type of results ",
     		new String[]{"Nucleus and chromocenter parameters","Chromocenter parameters","Nucleus parameters"}
     		, 3, 1,
     		"Nucleus and chromocenter parameters"
     	);
		genericDialog.showDialog();
        if (genericDialog.wasCanceled())   return;
        ImagePlus imagePlusInput =  WindowManager.getImage(wList[genericDialog.getNextChoiceIndex()]);
        ImagePlus imagePlusSegmented =  WindowManager.getImage(wList[genericDialog.getNextChoiceIndex()]);
        ImagePlus imagePlusChromocenter =  WindowManager.getImage(wList[genericDialog.getNextChoiceIndex()]);
        xCalibration = genericDialog.getNextNumber();
        yCalibration = genericDialog.getNextNumber();
        zCalibration = genericDialog.getNextNumber();
        String rhfChoice = genericDialog.getNextRadioButton();
        String analysisChoice = genericDialog.getNextRadioButton();
        unit = genericDialog.getNextString();
        Calibration calibration = new Calibration();
        calibration.pixelDepth = zCalibration;
        calibration.pixelWidth = xCalibration;
        calibration.pixelHeight = yCalibration;
        calibration.setUnit(unit);
        imagePlusInput.setCalibration(calibration);
        imagePlusChromocenter.setCalibration(calibration);
        imagePlusSegmented.setCalibration(calibration);
        
        if (analysisChoice.equals("Nucleus and chromocenter parameters"))
        {
        	 ChromocenterAnalysis chromocenterAnalysis = new ChromocenterAnalysis();
             chromocenterAnalysis.computeParametersChromocenter(imagePlusSegmented,imagePlusChromocenter);
             NucleusChromocentersAnalysis nucleusChromocentersAnalysis = new NucleusChromocentersAnalysis(); 
             nucleusChromocentersAnalysis.computeParameters
             (
            	rhfChoice,
            	imagePlusInput,
            	imagePlusSegmented,
            	imagePlusChromocenter
             );
        }
        else if (analysisChoice.equals("Chromocenter parameters"))
        {
        	ChromocenterAnalysis chromocenterAnalysis = new ChromocenterAnalysis();
            chromocenterAnalysis.computeParametersChromocenter(imagePlusSegmented,imagePlusChromocenter);
        }
        else
        {
            NucleusChromocentersAnalysis nucleusChromocentersAnalysis = new NucleusChromocentersAnalysis(); 
            nucleusChromocentersAnalysis.computeParameters
            (
            	rhfChoice,
            	imagePlusInput,
            	imagePlusSegmented,
            	imagePlusChromocenter
            );
        }
	}
}