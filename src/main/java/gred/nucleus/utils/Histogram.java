package gred.nucleus.utils;

import ij.ImagePlus;
import ij.ImageStack;

import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * This class permit to obtain values who are on the Input image (8, 16 or 32 bits)
 *    
 * @author Axel Poulet
 */
public class Histogram
{
	/** HashMap which stock the diffents value voxel and the number of voxel for each value present on the image*/
	private  HashMap<Double , Integer> _hHistogram = new HashMap <Double , Integer>() ;
	/** All the value present on the image */
    private double [] _label;
    /** */
    private double _labelMax = -1;
    private int _nbLabel = 0;
    
    /**
     * 
     */
    public Histogram () {    }
   
    /**
     * 
     * @param imagePlusInput
     */
    public void run(ImagePlus imagePlusInput)
    {
    	Object[] tTemp = computeHistogram(imagePlusInput).keySet().toArray();
    	_label = new double[tTemp.length];
    	for (int i = 0; i < tTemp.length; ++i)
    		_label[i] = Double.parseDouble(tTemp[i].toString());
    	Arrays.sort(_label);
    	if (_nbLabel > 0)
    		_labelMax = _label[_label.length-1];
    }
    
    /**
     * this method return a Histogram of the image input in hashMap form
     * 
     * @param imagePlusInput
     * @return
     */
    private HashMap<Double , Integer> computeHistogram(ImagePlus imagePlusInput)
    {
    	double voxelValue;
        ImageStack imageStackInput = imagePlusInput.getImageStack();
        for (int k = 0; k < imagePlusInput.getNSlices(); ++k)
        	for (int i = 0; i < imagePlusInput.getWidth(); ++i )
                for (int j = 0; j < imagePlusInput.getHeight();++j )
                {
                    voxelValue = imageStackInput.getVoxel(i,j,k);
                    if (voxelValue > 0 )
                    {
                        if (_hHistogram.containsKey(voxelValue))
                        {
                            int nbVoxel = _hHistogram.get(voxelValue);
                            ++nbVoxel;
                            _hHistogram.put(voxelValue, nbVoxel);
                        }
                        else
                        {
                        	_hHistogram.put(voxelValue, 1);
                        	++_nbLabel;
                        }
                    }
                }
        return _hHistogram;
    }
    
    /**
     * this method return a double table which contain the all the value voxel present on the input image 
     * @return
     */
    public double [] getLabels ()    { 	return _label; }
    
    /**
     * 
     * @return
     */
    public HashMap<Double , Integer> getHistogram() { 	return _hHistogram; }
    /**
     * 
     * @return
     */
    public double getLabelMax()  {    	return _labelMax;}
    
    public int getNbLabels()   { 	return _nbLabel;}
}
