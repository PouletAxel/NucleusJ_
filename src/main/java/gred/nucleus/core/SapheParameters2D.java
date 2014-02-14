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

public class SapheParameters2D
{
	/** Input image */
    ImagePlus _imagePlus;
    /** number of pixel in width height and depth. DepthMax is the stack with the greatest area */
    int _width, _height, _depth, _depthMax;
    /** Length of the voxel in x and y*/
    double _dimX,_dimY;
    /** Object to stock the two parameters computed*/
    ResultsTable _rt;
    
    /**
     *  Constructor
     * @param imagePlus
     */
    public SapheParameters2D (ImagePlus imagePlus)
    {
        Calibration calibration= imagePlus.getCalibration();
        _imagePlus = imagePlus;
        _width=_imagePlus.getWidth();
        _height=_imagePlus.getHeight();
        _dimX = calibration.pixelWidth;
        _dimY = calibration.pixelHeight;
        _depth=_imagePlus.getStackSize();
        StackConverter stackConverter = new StackConverter( _imagePlus );
        if (_imagePlus.getType() != ImagePlus.GRAY8)	stackConverter.convertToGray8();
        _rt = computePrameters(searchMaxArea());  
    }
    
    /**
     * Compute the area on each stack of the image and select the stack with the greatest area
     * @return
     */
    private int searchMaxArea()
    {
        ImageStack imageStack = _imagePlus.getStack();
        double areaMax = 0, area = 0;
        for (int k = 0; k < _depth; ++k)
        {
            int nbVoxel = 0;
            for (int i = 1; i < _width; ++i)
            {
                for (int j = 1;  j < _height; ++j)
                	if (imageStack.getVoxel(i, j, k)>0)
                		++nbVoxel ;
            }
            area = _dimX*_dimY*nbVoxel;
            if (area > areaMax)
            {
                areaMax = area ;
                _depthMax = k;
            }
        }
        return _depthMax;
    }

    /**
     * Method to compute the two parameters of interest with help of imageJ class
     * @param stackMaxArea
     * @return
     */
    private ResultsTable computePrameters(int stackMaxArea)
    {
    	ImagePlus imagePlusTmp = new ImagePlus();
    	ImageStack imageStack = _imagePlus.getStack();
        ImageStack imageStackTmp = new ImageStack(_width, _height);
        imageStackTmp.addSlice(imageStack.getProcessor(_depthMax));
        imagePlusTmp.setStack(imageStackTmp);
        Calibration calibration = new Calibration();
        calibration.pixelHeight = _dimY;
        calibration.pixelWidth = _dimX;
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
