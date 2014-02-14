package gred.nucleus.utils;

import ij.IJ;
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
	/**Input Image*/
    private static ImagePlus _imagePlus;
    /**Image Stack of the input image*/
    private ImageStack _imageStack;
    /** height, width, depth of image in voxel*/
    private int _width, _height, _depth;
    /** HashMap which stock the diffents value voxel and the number of voxel for each value present on the image*/
    HashMap<Double , Integer> _hHisto = new HashMap <Double , Integer>() ;
    /** All the value present on the image */
    private double [] _label;
    private double _maxValue;
    
    /**
     * Constructor
     * @param imagePlus
     */
    public Histogram (ImagePlus imagePlus)
    {
        _imagePlus = imagePlus;
        _imageStack = _imagePlus.getStack();
        _width = _imageStack.getWidth();
        _height= _imageStack.getHeight();
        _depth = _imageStack.getSize();
        getLabel();
    }
   
    
    /**
     * this method return a Histogram of the image input in hashMap form
     * 
     * @return 
     */
    public HashMap<Double , Integer> getHisto()
    {
    	
        double voxelValue;
        for (int k = 0; k < _depth; ++k)
            for (int i = 0; i < _width; ++i )
                for (int j = 0; j < _height;++j )
                {
                    voxelValue = _imageStack.getVoxel(i,j,k);
                    if (voxelValue > 0 )
                    {
                        if (_hHisto.containsKey(voxelValue))
                        {
                            int a = _hHisto.get(voxelValue);
                            ++a;
                            _hHisto.put(voxelValue, a);
                        }
                        else _hHisto.put(voxelValue, 1);
                    }
                }
        return _hHisto;
    }
    
    /**
     * this method return a double table which contain the all the value voxel present on the input image 
     * @return
     */
    public double [] getLabel ()
    {
    	Object[] temp = getHisto().keySet().toArray();
    	_label = new double[temp.length];
    	for (int i = 0; i < temp.length; ++i)   _label[i] = Double.parseDouble(temp[i].toString());
    	Arrays.sort(_label);
    	_maxValue = _label[_label.length-1];
    	return _label;
    }//getLabelObject
    
    public double getMaxValue()
    {
    	return _maxValue;
    }
}
