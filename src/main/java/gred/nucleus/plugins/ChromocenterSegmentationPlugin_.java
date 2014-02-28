package gred.nucleus.plugins;

import gred.nucleus.core.ChromocentersSegmentation;
import ij.measure.Calibration;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
	
public class ChromocenterSegmentationPlugin_ implements PlugIn
{
	public void run(String arg)
	{
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
		GenericDialog gd = new GenericDialog("Chromocenter Segmentation", IJ.getInstance());
		gd.addChoice("Raw image",titles,titles[rawImage]);
		gd.addChoice("Nucleus segmeneted image",titles,titles[nucSeg]);
		gd.addNumericField("x calibartion", x,4);
		gd.addNumericField("y calibration", y, 4);
		gd.addNumericField("z calibration",z, 4);
		gd.addStringField("Unit",unit,10);
		gd.showDialog();
		if (gd.wasCanceled())   return;
		ImagePlus imagePlusRaw =  WindowManager.getImage(wList[gd.getNextChoiceIndex()]);
		ImagePlus imagePlusBinary =  WindowManager.getImage(wList[gd.getNextChoiceIndex()]);
		x = gd.getNextNumber();
		y = gd.getNextNumber();
		z = gd.getNextNumber();
		unit = gd.getNextString();
		Calibration cal = new Calibration();
		cal.pixelDepth = z;
		cal.pixelWidth = x;
		cal.pixelHeight = y;
		cal.setUnit(unit);
		imagePlusRaw.setCalibration(cal);
		imagePlusBinary.setCalibration(cal);
		ChromocentersSegmentation chromocentersSegmentation	= new ChromocentersSegmentation();
		ImagePlus imagePlusContraste = chromocentersSegmentation.applyChromocentersSegmentation(imagePlusRaw, imagePlusBinary);
		imagePlusContraste.setTitle("imageContraste");
		imagePlusContraste.show();
	}
}
