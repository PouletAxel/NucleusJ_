package gred.nucleus.core;
import gred.nucleus.utils.Histogram;
import gred.nucleus.utils.VoxelRecord;
import ij.*;
import ij.measure.*;

import java.util.HashMap;
import java.util.Map.Entry;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * Class NucleusMeasure : allow the compute of severals 3Dparameters (shape, lenght) in
 * binary object
 * @author Poulet Axel
 */


public class Measure3D
{
	public Measure3D () { }

	/**
	 * Scan of image and if the voxel belong to theobject of interest, looking,
	 * if in his neighborhood there are voxel value == 0 then it is a boundary voxel.
	 * Adding the surface of the face of the voxel frontier, which are in contact
	 * with the background of the image, to the surface total.
	 *
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */

	public double computeSurfaceObject (ImagePlus imagePlusInput, double label)
	{
		Calibration calibration= imagePlusInput.getCalibration();
		ImageStack imageStackInput = imagePlusInput.getStack();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		double surfaceArea = 0,voxelValue, neighborVoxelValue;
		for (int k = 0; k < imagePlusInput.getStackSize(); ++k)
			for (int i = 0; i < imagePlusInput.getWidth(); ++i)
				for (int j = 0; j < imagePlusInput.getHeight(); ++j)
				{
					voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue == label)
					{
						for (int kk = k-1; kk <= k+1; kk+=2)
						{
							neighborVoxelValue = imageStackInput.getVoxel(i, j, kk);
							if (voxelValue != neighborVoxelValue)
								surfaceArea = surfaceArea + xCalibration * yCalibration;
						}
						for (int ii=i-1; ii<=i+1; ii+=2)
						{
							neighborVoxelValue =  imageStackInput.getVoxel(ii, j, k);
							if (voxelValue != neighborVoxelValue)
								surfaceArea = surfaceArea + xCalibration * zCalibration;
						}
						for (int jj = j-1; jj <= j+1; jj+=2)
						{
							neighborVoxelValue = imageStackInput.getVoxel(i, jj, k);
							if (voxelValue != neighborVoxelValue)
								surfaceArea = surfaceArea + yCalibration * zCalibration;
						}
					}
				}
		return surfaceArea;
	}


	/**
	 * This Method compute the volume of each segmented objects
	 * in imagePlus
	 * @param imagePlusInput
	 * @return
	 */
	public double[] computeVolumeofAllObjects (ImagePlus imagePlusInput)
	{
		Calibration calibration= imagePlusInput.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		Histogram histogram = new Histogram ();
		histogram.run(imagePlusInput);
		double [] tObjectVolume = new double[histogram.getLabels().length];
		int i=0;
		for(Entry<Double, Integer> entry : histogram.getHistogram().entrySet())
	    {
	        int nbVoxel = entry.getValue(); 
			tObjectVolume[i] = nbVoxel*xCalibration*yCalibration*zCalibration;
			++i;
	    }
		return tObjectVolume;
	} 

	/**
	 * 
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */
	public double computeVolumeObject (ImagePlus imagePlusInput, double label)
	{
		Calibration calibration= imagePlusInput.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		double volume = 0;
		Histogram histogram = new Histogram ();
		histogram.run(imagePlusInput);
		HashMap<Double , Integer> hashMapHisto = histogram.getHistogram();
		volume =  hashMapHisto.get(label) *xCalibration*yCalibration*zCalibration;
		return volume;
	}
    
	/**
	 * 
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */

	public double equivalentSphericalRadius (ImagePlus imagePlusInput, double label)
	{
		double radius;
		double volume = computeVolumeObject(imagePlusInput, label);
		radius =  (3 * volume) / (4 * Math.PI);
		radius = Math.pow(radius, 1.0/3.0);
		return radius;
	}


	/**
	 * Method which compute the sphericity :
	 * 36Pi*Volume^2/Surface^3 = 1 if perfect sphere
	 * 
	 * @param volume
	 * @param surface
	 * @return
	 */
	public double computeSphericity(double volume, double surface)
	{
		return ((36 * Math.PI * (volume*volume)) / (surface*surface*surface));
	}
  
	/**
	 * Method which compute the eigen value of the matrix (differences between the
	 * coordinates of all points and the barycenter
	 * Obtaining a symmetric matrix :
	 * xx xy xz
	 * xy yy yz
	 * xz yz zz
	 * Compute the eigen value with the pakage JAMA
	 * 
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */

	public double [] computeEigenValue3D (ImagePlus imagePlusInput, double label)
	{
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		VoxelRecord barycenter = computeBarycenter3D (true,imagePlusInput,label);
		Calibration calibration= imagePlusInput.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		double xx = 0;
		double xy = 0;
		double xz = 0;
		double yy = 0;
		double yz = 0;
		double zz = 0;
		int compteur = 0;
		double voxelValue;
		for (int k = 0; k < imagePlusInput.getStackSize(); ++k)
		{
			double dz = ((zCalibration * (double) k)-barycenter.getK());
			for (int i = 0; i < imagePlusInput.getWidth(); ++i)
			{
				double dx = ((xCalibration * (double) i)-barycenter.getI());
				for (int j = 0; j < imagePlusInput.getHeight(); ++j)
				{
					voxelValue = imageStackInput.getVoxel(i,j,k);
					if (voxelValue == label)
					{ 
						double dy = ((yCalibration * (double) j)-barycenter.getJ());
						xx+= dx * dx;
						yy+= dy * dy;
						zz+= dz * dz;
						xy+= dx * dy;
						xz+= dx * dz;
						yz+= dy * dz;
						compteur++;
					}
				}
			}
		}
		double [][] tValues = {{xx / compteur, xy / compteur, xz / compteur},
                      {xy / compteur, yy / compteur, yz / compteur},
                      {xz / compteur, yz / compteur, zz / compteur}};
		Matrix matrix = new Matrix (tValues);
		EigenvalueDecomposition eigenValueDecomposition =  matrix.eig();
		return eigenValueDecomposition.getRealEigenvalues();
	}

	/**
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */
	public double computeElongationObject (ImagePlus imagePlusInput, double label)
	{
		double [] tEigenValues = computeEigenValue3D (imagePlusInput, label);
		return Math.sqrt (tEigenValues[2] / tEigenValues[1]);
	}
  
	/**
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */
	public double [] computeFlatnessAndElongation (ImagePlus imagePlusInput, double label)
	{
		double [] shapeParameters = new double[2];
		double [] tEigenValues = computeEigenValue3D (imagePlusInput,label);
		shapeParameters [0] = Math.sqrt(tEigenValues[1] / tEigenValues[0]);
		shapeParameters [1] =  Math.sqrt (tEigenValues[2] / tEigenValues[1]);
		return shapeParameters;
	}  

	/**
	 * Method which determines object barycenter
	 * 
	 * @param unit if true the coordinates of barycenter are in µm.
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */
	public VoxelRecord computeBarycenter3D (boolean unit,ImagePlus imagePlusInput, double label)
  	{
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		Calibration calibration= imagePlusInput.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		VoxelRecord voxelRecordBarycenter = new VoxelRecord ();
		int count = 0;
		int sx = 0;
		int sy = 0;
		int sz =0;
		double voxelValue;
		for (int k = 0; k < imagePlusInput.getStackSize(); ++k)
			for (int i = 0; i < imagePlusInput.getWidth(); ++i)
				for (int j = 0; j < imagePlusInput.getHeight(); ++j)
				{
					voxelValue = imageStackInput.getVoxel(i,j,k);
					if (voxelValue == label )
					{
						sx +=i;
						sy +=j;
						sz +=k;
						++count;
					}
				}
		sx /= count;
		sy /= count;
		sz /= count;
		voxelRecordBarycenter.setLocation(sx, sy, sz);
		if (unit) voxelRecordBarycenter.Multiplie(xCalibration, yCalibration,zCalibration);
		return voxelRecordBarycenter;
  	}
	
	/**
	 * Method which compute the barycenter of each objects and return the result
	 * in a table of VoxelRecord
	 * 
	 * @param imagePlusInput
	 * @param unit
	 * @return
	 */
	public VoxelRecord[] computeObjectBarycenter (ImagePlus imagePlusInput, boolean unit)
	{
		Histogram histogram = new Histogram();
		histogram.run(imagePlusInput);
		VoxelRecord [] tVoxelRecord = new VoxelRecord [ histogram.getLabels().length];
		int i = 0;
		tVoxelRecord[i] = null;
		for(Entry<Double, Integer> entry : histogram.getHistogram().entrySet())
	    {
			tVoxelRecord[i] = computeBarycenter3D(unit, imagePlusInput,entry.getKey() );
			++i;
		}
		return tVoxelRecord;
	}
	
	/**
	 * 
	 * @param imagePlusInput
	 * @param imagePlusSegmented
	 * @param imagePlusChromocenter
	 * @return
	 */
	public double computeIntensityRHF (ImagePlus imagePlusInput, ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter )
	  {
	    double chromocenterIntensity = 0;
	    double nucleusIntensity = 0;
	    double voxelValueChromocenter;
	    double voxelValueInput;
	    double voxelValueSegmented;
	    ImageStack imageStackChromocenter =  imagePlusChromocenter.getStack();
	    ImageStack imageStackSegmented = imagePlusSegmented.getStack();
	    ImageStack imageStackInput = imagePlusInput.getStack();
	    for (int k = 0; k < imagePlusInput.getNSlices(); ++k)
	      for (int i = 0; i < imagePlusInput.getWidth(); ++i )
	        for (int j = 0; j < imagePlusInput.getHeight(); ++j )
	        {
	          voxelValueSegmented = imageStackSegmented.getVoxel(i, j, k);
	          voxelValueInput = imageStackInput.getVoxel(i, j, k);
	          voxelValueChromocenter = imageStackChromocenter.getVoxel(i,j,k);
	     
	          if (voxelValueSegmented > 0)
	          {
	            if (voxelValueChromocenter > 0){ chromocenterIntensity+=voxelValueInput;}
	            nucleusIntensity += voxelValueInput;
	          }
	        }
	    return chromocenterIntensity / nucleusIntensity;
	  }

	  /**
	   * Method which compute the RHF (total chromocenters volume / nucleus volume)
	   * @return RHF
	   *
	   * @param imagePlusSegmented
	   * @param imagePlusChomocenters
	   * @return
	   */
	  public double computeVolumeRHF (ImagePlus imagePlusSegmented, ImagePlus imagePlusChomocenters)
	  {
	    double volumeCc = 0;
	    double [] tVolumeChromocenter = computeVolumeofAllObjects(imagePlusChomocenters);
	    for (int i = 0; i < tVolumeChromocenter.length; ++i) 
	    	volumeCc += tVolumeChromocenter[i];
	    double []tVolumeSegmented = computeVolumeofAllObjects(imagePlusSegmented);
	    return volumeCc / tVolumeSegmented[0];
	  } 
	  
	  /**
	   * 
	   * @param imagePlusInput
	   * @return
	   */
	  public int getNumberOfObject (ImagePlus imagePlusInput)
	  {
	    Histogram histogram = new Histogram ();
		histogram.run(imagePlusInput);
		return histogram.getNbLabels();
	  } 
}