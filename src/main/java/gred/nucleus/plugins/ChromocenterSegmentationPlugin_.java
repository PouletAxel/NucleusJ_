package gred.nucleus.plugins;
import gred.nucleus.core.ChromocentersEnhancement;
import ij.measure.Calibration;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.GaussianBlur3D;
import ij.plugin.PlugIn;
	
/**
 * Method to detect the chromocenters on one image
 * 
 * 
 * @author Poulet Axel
 *
 */
public class ChromocenterSegmentationPlugin_ implements PlugIn{
	/**
	 * 
	 */
	public void run(String arg){
		int indiceRawImage = 0;
		int indiceSementedImage = 0;
		double xCalibration = 1,yCalibration = 1,zCalibration = 1;
		String unit = "pixel";
		int[] wList = WindowManager.getIDList();
		if(wList == null){
			IJ.noImage();
			return;
		}
		String[] titles = new String[wList.length];
		for(int i = 0; i < wList.length; i++){
			ImagePlus imagePlus = WindowManager.getImage(wList[i]);
			if(imagePlus != null){	
				if(i == 0){
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
		
		GenericDialog genericDialog = new GenericDialog("Chromocenter Segmentation", IJ.getInstance());
		genericDialog.addChoice("Raw image",titles,titles[indiceRawImage]);
		genericDialog.addChoice("Nucleus segmented image",titles,titles[indiceSementedImage]);
		genericDialog.addNumericField("x calibration", xCalibration,3);
		genericDialog.addNumericField("y calibration", yCalibration, 3);
		genericDialog.addNumericField("z calibration",zCalibration, 3);
		genericDialog.addStringField("Unit",unit,10);
		genericDialog.showDialog();
		if(genericDialog.wasCanceled()) 
			return;
		ImagePlus imagePlusInput =  WindowManager.getImage(wList[genericDialog.getNextChoiceIndex()]);
		ImagePlus imagePlusSegmented =  WindowManager.getImage(wList[genericDialog.getNextChoiceIndex()]);
		GaussianBlur3D.blur(imagePlusInput,0.25,0.25,1);
		ImageStack imageStack= imagePlusInput.getStack();
		int max = 0;
		for(int k = 0; k < imagePlusInput.getStackSize(); ++k)
			for (int i = 0; i < imagePlusInput.getWidth(); ++i )
				for (int j = 0; j < imagePlusInput.getHeight(); ++j){
					if (max < imageStack.getVoxel(i, j, k)){
						max = (int) imageStack.getVoxel(i, j, k);
					}
				}
		IJ.setMinAndMax(imagePlusInput, 0, max);	
		IJ.run(imagePlusInput, "Apply LUT", "stack");
		xCalibration = genericDialog.getNextNumber();
		yCalibration = genericDialog.getNextNumber();
		zCalibration = genericDialog.getNextNumber();
		unit = genericDialog.getNextString();
		Calibration calibration = new Calibration();
		calibration.pixelDepth = zCalibration;
		calibration.pixelWidth = xCalibration;
		calibration.pixelHeight = yCalibration;
		calibration.setUnit(unit);
		imagePlusInput.setCalibration(calibration);
		imagePlusSegmented.setCalibration(calibration);
		ChromocentersEnhancement chromocentersSegmentation	= new ChromocentersEnhancement();
		ImagePlus imagePlusContraste = chromocentersSegmentation.applyEnhanceChromocenters(imagePlusInput, imagePlusSegmented);
		imagePlusContraste.setTitle("ContrastedImage");
		imagePlusContraste.show();
	}
}