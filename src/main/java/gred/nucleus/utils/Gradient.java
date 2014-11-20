package gred.nucleus.utils;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;

import java.util.ArrayList;
/**
 * 
 * @author Poulet Axel
 *
 */
public class Gradient
{
	private ArrayList <Double> _tableGradient [][][] = null;
	private ArrayList <Double> _tableUnitaire [][][] = null;
	
	 public Gradient (ImagePlus imagePlusInput)
	 {
         _tableGradient = new ArrayList [imagePlusInput.getWidth()][imagePlusInput.getHeight()][imagePlusInput.getStackSize()];
         _tableUnitaire = new ArrayList [imagePlusInput.getWidth()][imagePlusInput.getHeight()][imagePlusInput.getStackSize()];
         computeGradient(imagePlusInput);
	
	 }

	 /**
	  * 
	  * @param imagePlusInput
	  * @return
	  */
	 
	 private void computeGradient(ImagePlus imagePlusInput)
	 {
		 Calibration calibration= imagePlusInput.getCalibration();
		 ImageStack imageStackInput = imagePlusInput.getStack();
		 double xCalibration = calibration.pixelWidth;
		 double yCalibration = calibration.pixelHeight;
		 double zCalibration = calibration.pixelDepth;
		 for (int k = 0; k < imagePlusInput.getStackSize(); ++k)
			 for (int i = 0; i < imagePlusInput.getWidth(); ++i)
				 for (int j = 0; j < imagePlusInput.getHeight(); ++j)
				 { 
					 ArrayList <Double> list = new ArrayList();
					 double dx = 0;
					 double dy = 0;
					 double dz = 0; 
					 if (k-1>0 || j-1>0 || i-1>0 || k+1 < imagePlusInput.getStackSize()|| 
							 j+1 < imagePlusInput.getHeight()|| i+1 < imagePlusInput.getWidth())
					 {
						 dx = (1/xCalibration)*((imageStackInput.getVoxel(i+1, j, k)-imageStackInput.getVoxel(i-1, j, k))/2);
						 dy = (1/yCalibration)*((imageStackInput.getVoxel(i, j+1, k)-imageStackInput.getVoxel(i, j-1, k))/2);
						 dz = (1/zCalibration)*((imageStackInput.getVoxel(i, j, k+1)-imageStackInput.getVoxel(i, j, k-1))/2);
					 }
					 list.add(dx);
					 list.add(dy);
					 list.add(dz);
					 _tableGradient[i][j][k] = list;

					 double nx = dx/ Math.sqrt(dx*dx+dy*dy+dz*dz);
					 double ny = dy/ Math.sqrt(dx*dx+dy*dy+dz*dz);
					 double nz = dz/ Math.sqrt(dx*dx+dy*dy+dz*dz);
					 ArrayList <Double> listN = new ArrayList();
					 listN.add(nx);
					 listN.add(ny);
					 listN.add(nz);
					 _tableUnitaire[i][j][k] = listN;
				 }
		 IJ.log("fin Gradient");
	 }
	 
	 public ArrayList <Double> [][][] getUnitaire (){ return _tableUnitaire;}
	
	 public ArrayList <Double> [][][] getGradient () { return _tableGradient;}
	 
}