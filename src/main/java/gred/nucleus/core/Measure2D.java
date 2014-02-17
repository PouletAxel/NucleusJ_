package gred.nucleus.core;

import ij.*;
import ij.plugin.filter.*;
import ij.process.StackConverter;
import ij.measure.*;

/**
 * This class allow the determination of two shapes parameter in 2D, Circularity and AspectRation. This class detect the stack with the greatest Area
 *  and after compute the two shape parameters in this stack 
 *  
 * @author gred
 */

public class Measure2D
{
    /** Object to stock the two parameters computed*/
    ResultsTable _rt;
    
    /**
     *  Constructor
     * @param imagePlus
     */
    public Measure2D ()
    {

    }
    
    public void run (ImagePlus imagePlus)
    {
    	 StackConverter stackConverter = new StackConverter( imagePlus );
         if (imagePlus.getType() != ImagePlus.GRAY8)	stackConverter.convertToGray8();
         _rt = computePrameters(imagePlus,searchMaxArea(imagePlus));  
    }
    /**
     * Compute the area on each stack of the image and select the stack with the greatest area
     * @return
     */
    private int searchMaxArea(ImagePlus imagePlus)
    {
    	Calibration calibration= imagePlus.getCalibration();
    	int stackMaxArea = -1;
    	double dimX = calibration.pixelWidth;
    	double dimY = calibration.pixelHeight;
        ImageStack imageStack = imagePlus.getStack();
        double areaMax = 0, area = 0;
        for (int k = 0; k < imagePlus.getNSlices(); ++k)
        {
            int nbVoxel = 0;
            for (int i = 1; i < imagePlus.getWidth(); ++i)
            {
                for (int j = 1;  j < imagePlus.getHeight(); ++j)
                	if (imageStack.getVoxel(i, j, k)>0)
                		++nbVoxel ;
            }
            area = dimX*dimY*nbVoxel;
            if (area > areaMax)
            {
                areaMax = area ;
                stackMaxArea = k;
            }
        }
        return stackMaxArea;
    }

    /**
     * Method to compute the two parameters of interest with help of imageJ class
     * @param stackMaxArea
     * @return
     */
    private ResultsTable computePrameters(ImagePlus imagePlus, int stackMaxArea)
    {
    	ImagePlus imagePlusTmp = new ImagePlus();
    	ImageStack imageStack = imagePlus.getStack();
        ImageStack imageStackTmp = new ImageStack(imagePlus.getWidth(),imagePlus.getHeight());
        imageStackTmp.addSlice(imageStack.getProcessor(stackMaxArea));
        imagePlusTmp.setStack(imageStackTmp);
        Calibration calibrationImagePlus= imagePlus.getCalibration();
    	double dimX = calibrationImagePlus.pixelWidth;
    	double dimY = calibrationImagePlus.pixelHeight;
        Calibration calibration = new Calibration();
        calibration.pixelHeight = dimY;
        calibration.pixelWidth = dimX;
        imagePlusTmp.setCalibration(calibration);
        ResultsTable rt = new ResultsTable(); 
        ParticleAnalyzer analyser = new ParticleAnalyzer
        (ParticleAnalyzer.SHOW_NONE,Measurements.AREA+Measurements.CIRCULARITY, rt, 10, Double.MAX_VALUE, 0,1); 
        analyser.analyze(imagePlusTmp); 
        return rt;
       
    }
    
    /**
     * Circularity Particles with size circularity values outside the range specified in this field are also 
     * ignored. Circularity = (4π × [Area ] / [P erimeter]2 ) , see Set Measurements. . . ) ranges from 0 (infinitely
     * elongated polygon) to 1 (perfect circle).
     * 
     * @return
     */
   public double getAspectRatio () {	return _rt.getValue("AR", 0);	} 
    
    
    /**
     * Aspect ratio The aspect ratio of the particle’s fitted ellipse, i.e., [M ajor Axis]/Minor Axis] . If Fit
     * Ellipse is selected the Major and Minor axis are displayed. Uses the heading AR.
     * 
     * @return
     */
    public double getCirculairty() {   return _rt.getValue("Circ.", 0); }
}
