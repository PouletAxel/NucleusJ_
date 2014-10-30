package gred.nucleus.core;

import ij.*;
import ij.plugin.filter.*;
import ij.process.StackConverter;
import ij.measure.*;

/**
 * This class allow the determination of two shapes parameter in 2D, Circularity and AspectRatio. This class detect the slice with the greatest Area
 *  and after compute the two shape parameters in this slice 
 *  
 * @author Poulet Axel
 *
 */
public class Measure2D
{
    /** Object to store the two parameters computed*/
    ResultsTable _resultsTable;

    public Measure2D ()  {   }
    
    /**
     * Compute of the 2D parameters of the nucleus 
     * 
     * @param imagePlusSegmented image of segmented nucleus
     */
    public void run (ImagePlus imagePlusSegmented)
    {
    	 StackConverter stackConverter = new StackConverter( imagePlusSegmented );
         if (imagePlusSegmented.getType() != ImagePlus.GRAY8)	stackConverter.convertToGray8();
         _resultsTable = computePrameters(imagePlusSegmented,searchSliceWithMaxArea(imagePlusSegmented));  
    }

    /**
     * Search the stack with the greatest area for the nucleus
     * 
     * @param imagePlusSegmented
     * @return the number of the stack with the greatest area
     */
    private int searchSliceWithMaxArea(ImagePlus imagePlusSegmented)
    {
    	Calibration calibration= imagePlusSegmented.getCalibration();
    	int indiceMaxArea = -1;
    	double xCalibration = calibration.pixelWidth;
    	double yCalibration = calibration.pixelHeight;
        ImageStack imageStackSegmented = imagePlusSegmented.getStack();
        double areaMax = 0;
        double area = 0;
        for (int k = 0; k < imagePlusSegmented.getNSlices(); ++k)
        {
            int nbVoxel = 0;
            for (int i = 1; i < imagePlusSegmented.getWidth(); ++i)
            {
                for (int j = 1;  j < imagePlusSegmented.getHeight(); ++j)
                	if (imageStackSegmented.getVoxel(i, j, k)>0)
                		++nbVoxel ;
            }
            area = xCalibration*yCalibration*nbVoxel;
            if (area > areaMax)
            {
                areaMax = area ;
                indiceMaxArea = k;
            }
        }
        return indiceMaxArea;
    }

    /**
     * method to compute the parameter
     * 
     * @param imagePlusSegmented
     * @param indiceMaxArea 
     * @return a resultTable object which contain the results
     */
    private ResultsTable computePrameters(ImagePlus imagePlusSegmented, int indiceMaxArea)
    {
    	ImagePlus imagePlusTemp = new ImagePlus();
    	ImageStack imageStackSegmented = imagePlusSegmented.getStack();
        ImageStack imageStackTemp = new ImageStack(imagePlusSegmented.getWidth(),imagePlusSegmented.getHeight());
        imageStackTemp.addSlice(imageStackSegmented.getProcessor(indiceMaxArea));
        imagePlusTemp.setStack(imageStackTemp);
        Calibration calibrationImagePlusSegmented= imagePlusSegmented.getCalibration();
        Calibration calibration = new Calibration();
        calibration.pixelHeight = calibrationImagePlusSegmented.pixelHeight;
        calibration.pixelWidth =  calibrationImagePlusSegmented.pixelWidth;
        imagePlusTemp.setCalibration(calibration);
        ResultsTable resultTable = new ResultsTable(); 
        ParticleAnalyzer particleAnalyser = new ParticleAnalyzer
        (ParticleAnalyzer.SHOW_NONE,Measurements.AREA+Measurements.CIRCULARITY, resultTable, 10, Double.MAX_VALUE, 0,1); 
        particleAnalyser.analyze(imagePlusTemp); 
        return resultTable;
       
    }
	/**
     * Aspect ratio The aspect ratio of the particle’s fitted ellipse, i.e., [M ajor Axis]/Minor Axis] . If Fit
     * Ellipse is selected the Major and Minor axis are displayed. Uses the heading AR.
     * 
     * @return
     */
    public double getAspectRatio () {	return _resultsTable.getValue("AR", 0);	} 
    
    /**
     * Circularity Particles with size circularity values outside the range specified in this field are also 
     * ignored. Circularity = (4π × [Area ] / [P erimeter]2 ) , see Set Measurements. . . ) ranges from 0 (infinitely
     * elongated polygon) to 1 (perfect circle).
     * 
     * @return
     */
    
    public double getCirculairty() {   return _resultsTable.getValue("Circ.", 0); }
}