package gred.nucleus.parameters;
import ij.ImagePlus;

import java.util.HashMap;

import gred.nucleus.utilitaires.*;



/**
 * Class allow the determination of different parameters for several object detect- in the input image
 * @author Poulet Axel
 */

public class ParametersOfSeveralObject
{
	/** Image input */
	ImagePlus _inputImage;
	/** HashMap with value of voxel in keys and the number of voxel with value on value*/
	HashMap<Double , Integer> _hHisto = new HashMap<Double , Integer>() ;
	/** Object Histogram*/
	Histogram _histogram;
	
	/**
	 * Constructor
	 * @param imagePlusSeveralObject 
	 */
	public ParametersOfSeveralObject (ImagePlus imagePlusSeveralObject)
	{
		_histogram = new Histogram (imagePlusSeveralObject);
		_hHisto = _histogram.getHisto();
		_inputImage = imagePlusSeveralObject;
	}

	/**
	 * Method which compute the volume of each segmented objects
	 * in _imagePlus
	 * @return Table of Object volume
	 */

	public double[] computeVolumeofAllObjects ()
	{
		double tabLabel[] = _histogram.getLabel();
		double objectVolume[] = new double[tabLabel.length];
		objectVolume[0] = 0;
		for (int i = 0; i < tabLabel.length; ++i)
		{
			GeometricParameters3D gp3D = new  GeometricParameters3D (_inputImage, tabLabel[i]);
			objectVolume[i] = gp3D.computeVolumeObject();
		}
		return objectVolume;
	} //computeVolumeObject

	/**
	 * Method which return number the object in _imagePlusInput
	 * @return number of objects in the image
	 */

	public int nbObject() {  return _hHisto.size(); }

 
	/**
	 * Method which compute the barycenter of each objects and return the result
	 * in a table of VoxelRecord
	 * @return Table containing the barycenter of each objects
	 */

	public VoxelRecord[] computeObjectBarycenter ()
	{
		double labelObject [] = _histogram.getLabel();
		VoxelRecord tabVoxelRecord[] = new VoxelRecord [labelObject.length];
		tabVoxelRecord[0] = null;
		for (int i = 0; i < labelObject.length; ++i)
		{
			ShapeParameters3D sp3d = new ShapeParameters3D(_inputImage,labelObject[i] );
			VoxelRecord voxelRecord = sp3d.computeBarycenter3D(true);
			tabVoxelRecord[i] = voxelRecord;
		}
		return tabVoxelRecord;
	}

  
	/**
	 * Method wich compute the mean of the value in the table
	 * @param tabInput Table of value
	 * @return Mean of the table
	 */

  
	public double computeMeanOfTable (double tabInput[])
	{
		int i;
		double mean = 0;
		for (i = 0; i < tabInput.length; ++i)  mean += tabInput[i];
		mean = mean / (tabInput.length);
		return mean;
	}//computeMeanOfTable
}
