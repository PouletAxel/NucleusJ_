package gred.nucleus.plugins;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.plugin.PlugIn;
import gred.nucleus.chromocenterAnalysis.*;

/**
 * 
 * @author gred
 *
 */
public class ChromocenterAnalysis_   implements PlugIn
{
	
	public void run(String arg)
	{
		int ccSeg = 0;
		int rawImage = 0;
		int nucSeg = 0;
		double x = 1,y=1,z=1;
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
			ImagePlus imp = WindowManager.getImage(wList[i]);
			if (imp != null)          titles[i] = imp.getTitle();
			else          titles[i] = "";
		}
		GenericDialog gd = new GenericDialog("Chromocenter Analysis", IJ.getInstance());
		gd.addChoice("Raw image",titles,titles[rawImage]);
		gd.addChoice("Chromocenters image Segemented",titles,titles[ccSeg]);
		gd.addChoice("Nucleus Segmeneted",titles,titles[nucSeg]);
		gd.addNumericField("x calibartion", x,4);
      	gd.addNumericField("y calibration", y, 4);
      	gd.addNumericField("z calibration).",z, 4);
    	gd.addStringField("Unit",unit,10);
       	gd.addRadioButtonGroup("Type of RHF ", new String[]{"Volume and intensity","Volume","Intensity"}, 1, 3, "Volume and intensity");
     	gd.addRadioButtonGroup("Type of OutPut ", new String[]{"Nucleus and chromocenter parameters","Chromocenter parameters","Nucleus parameters"}, 3, 1, "Nucleus and chromocenter parameters");
		gd.showDialog();
        if (gd.wasCanceled())   return;
        ImagePlus ipRawImage =  WindowManager.getImage(wList[gd.getNextChoiceIndex()]);
        ImagePlus ipCcSeg =  WindowManager.getImage(wList[gd.getNextChoiceIndex()]);
        ImagePlus ipNucSeg =  WindowManager.getImage(wList[gd.getNextChoiceIndex()]);
        x = gd.getNextNumber();
        y = gd.getNextNumber();
        z = gd.getNextNumber();
        String choiceRhf = gd.getNextRadioButton();
        String choiceOutput = gd.getNextRadioButton();
        unit = gd.getNextString();
        Calibration cal = new Calibration();
        cal.pixelDepth = z;
        cal.pixelWidth = x;
        cal.pixelHeight = y;
        cal.setUnit(unit);
        ipRawImage.setCalibration(cal);
        ipCcSeg.setCalibration(cal);
        ipNucSeg.setCalibration(cal);
        
        if (choiceOutput.equals("Nucleus and chromocenter parameters"))
        {
        	 ChromocenterAnalysis ca = new ChromocenterAnalysis(ipCcSeg,ipNucSeg);
             ca.computeParametersChromocenter();
             NucleusAnalysisAndCC naacc = new NucleusAnalysisAndCC(ipRawImage,ipCcSeg,ipNucSeg); 
             naacc.computeParameters(choiceRhf);
        }
        else if (choiceOutput.equals("Chromocenter parameters"))
        {
        	ChromocenterAnalysis ca = new ChromocenterAnalysis(ipCcSeg,ipNucSeg);
            ca.computeParametersChromocenter();
        }
        else
        {
        	NucleusAnalysisAndCC naacc = new NucleusAnalysisAndCC(ipRawImage,ipCcSeg,ipNucSeg); 
            naacc.computeParameters(choiceRhf);
        }
	}
}