package gred.nucleus.parameters;

import gred.nucleus.utilitaires.Histogram;
import ij.*;
import ij.measure.*;


import java.util.HashMap;

/**
 * Class NucleusMeasure : which compute differents parameters (shape, lenght) in
 * binary object
 * @author Poulet Axel
 */


public class GeometricParameters3D
{
	/** binary image */
	ImagePlus _inputImage;
	/** height, width, depth of image in pixel*/
	int _width, _height, _depth;
	/** Voxel calibration in µm*/
	double _dimX, _dimY, _dimZ;
	/** imageStack of binary image */
	ImageStack _imageStackBinary  = new ImageStack();
	/** Object label */
	double _dlabel;

	/**
	 * 
	 * @param imagePlusInput
	 * @param label
	 */
	public GeometricParameters3D (ImagePlus imagePlusInput, double label)
	{
		_inputImage = imagePlusInput;
		Calibration cal=_inputImage.getCalibration();
		_imageStackBinary = _inputImage.getImageStack();
		_width=_inputImage.getWidth();
		_height=_inputImage.getHeight();
		_depth=_inputImage.getStackSize();
		_dimX = cal.pixelWidth;
		_dimY = cal.pixelHeight;
		_dimZ =cal.pixelDepth;
		_dlabel = label; 
	}

	/**
	 * Scan of image and if the voxel belong to theobject of interest, looking,
	 * if in his neighborhood there are voxel value == 0 then it is a boundary voxel.
	 * Adding the surface of the face of the voxel frontier, which are in contact
	 * with the background of the image, to the surface total.
	 *
	 * @return surface
	 */

	public double computeSurfaceObject ()
	{
		int i,j,k,ii,jj,kk;
		double surface = 0, voxelValue, neigVoxelValue;
		for (k = 0; k < _depth; ++k)
			for (i = 0; i < _width; ++i)
				for (j = 0; j < _height; ++j)
				{
					voxelValue = _imageStackBinary.getVoxel(i, j, k);
					if (voxelValue >= _dlabel)
					{
						for (kk = k-1; kk <= k+1; ++kk)
						{
							neigVoxelValue = _imageStackBinary.getVoxel(i, j, kk);
							if (voxelValue != neigVoxelValue) { surface = surface + _dimX * _dimY; }
						}
						for (ii=i-1; ii<=i+1; ++ii)
						{
							neigVoxelValue =  _imageStackBinary.getVoxel(ii, j, k);
							if (voxelValue != neigVoxelValue) { surface = surface + _dimX * _dimZ; }
						}
						for (jj = j-1; jj <= j+1; ++jj)
						{
							neigVoxelValue = _imageStackBinary.getVoxel(i, jj, k);
							if (voxelValue != neigVoxelValue) { surface = surface + _dimY * _dimZ; }
						}
					}
				}
		return surface;
	}

	/**
	 * Method which compute the volume of nucleus
	 *
	 * @return volume
	 */
	public double computeVolumeObject ()
	{
		double volume = 0;
		Histogram histogram = new Histogram (_inputImage);
		HashMap<Double , Integer> hHisto = histogram.getHisto();
		volume =  hHisto.get(_dlabel) * _dimX * _dimY * _dimZ;
		return volume;
	}
    
	/**
	 * Method whiche compute the equivalent spheric radius of nucleus, corresponding
	 * at the radius of sphere which has the same volume of the nucleus
	 * @return equivalent spheric radius (µm)
	 */
	
	public double equivalentSphericalRadius ()
	{
		double radius;
		double volume = computeVolumeObject();
		radius =  (3 * volume) / (4 * Math.PI);
		radius = Math.pow(radius, 0.333333);
		return radius;
	}
}