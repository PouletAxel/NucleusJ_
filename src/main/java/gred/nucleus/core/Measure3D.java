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
		int i,j,k,ii,jj,kk;
		Calibration calibration= imagePlusInput.getCalibration();
		ImageStack imageStakInput = imagePlusInput.getStack();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		double surface = 0, voxelValue, neighborVoxelValue;
		for (k = 0; k < imagePlusInput.getStackSize(); ++k)
			for (i = 0; i < imagePlusInput.getWidth(); ++i)
				for (j = 0; j < imagePlusInput.getHeight(); ++j)
				{
					voxelValue = imageStakInput.getVoxel(i, j, k);
					if (voxelValue == label)
					{
						for (kk = k-1; kk <= k+1; ++kk)
						{
							neighborVoxelValue = imageStakInput.getVoxel(i, j, kk);
							if (voxelValue != neighborVoxelValue) { surface = surface + xCalibration * yCalibration; }
						}
						for (ii=i-1; ii<=i+1; ++ii)
						{
							neighborVoxelValue =  imageStakInput.getVoxel(ii, j, k);
							if (voxelValue != neighborVoxelValue) { surface = surface + xCalibration * zCalibration; }
						}
						for (jj = j-1; jj <= j+1; ++jj)
						{
							neighborVoxelValue = imageStakInput.getVoxel(i, jj, k);
							if (voxelValue != neighborVoxelValue) { surface = surface + yCalibration * zCalibration; }
						}
					}
				}
		return surface;
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
	 * Method whiche compute the equivalent spheric radius of nucleus, corresponding
	 * at the radius of sphere which has the same volume of the nucleus
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
		radius = Math.pow(radius, 0.333333);
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
		double xx = 0, xy = 0, xz = 0, yy = 0, yz = 0, zz = 0;
		int compteur = 0;
		int i,j,k;
		double voxelValue;
		for (k = 0; k < imagePlusInput.getStackSize(); ++k)
			for (i = 0; i < imagePlusInput.getWidth(); ++i)
				for (j = 0; j < imagePlusInput.getHeight(); ++j)
				{
					voxelValue = imageStackInput.getVoxel(i,j,k);
					if (voxelValue > 0)
					{ 
						xx+= ((xCalibration * (double) i)-barycenter.getI()) * ((xCalibration * (double) i)-barycenter.getI());
						yy+= ((yCalibration * (double) j)-barycenter.getJ()) * ((yCalibration * (double) j)-barycenter.getJ());
						zz+= ((zCalibration * (double) k)-barycenter.getK()) * ((zCalibration * (double) k)-barycenter.getK());
						xy+= ((xCalibration * (double) i)-barycenter.getI()) * ((yCalibration * (double) j)-barycenter.getJ());
						xz+= ((xCalibration * (double) i)-barycenter.getI()) * ((zCalibration * (double) k)-barycenter.getK());
						yz+= ((yCalibration * (double) j)-barycenter.getJ()) * ((zCalibration * (double) k)-barycenter.getK());
						compteur++;
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
	public double computeFlatnessObject (ImagePlus imagePlusInput, double label)
	{
		double [] tEigenValue = computeEigenValue3D (imagePlusInput,label);
		return Math.sqrt(tEigenValue[1] / tEigenValue[0]);
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
		int compteur = 0;
		double voxelValue;
		int i,j,k;
		for (k = 0; k < imagePlusInput.getStackSize(); ++k)
			for (i = 0; i < imagePlusInput.getWidth(); ++i)
				for (j = 0; j < imagePlusInput.getHeight(); ++j)
				{
					voxelValue = imageStackInput.getVoxel(i,j,k);
					if (voxelValue == label )
					{
						VoxelRecord voxelRecord = new VoxelRecord();
						voxelRecord.setLocation((double)i,(double)j,(double)k);
						voxelRecordBarycenter.shiftCoordinates(voxelRecord);
						compteur++;
					}
				}
		voxelRecordBarycenter.Multiplie(1 / (double)compteur);
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
	        VoxelRecord voxelRecord = computeBarycenter3D(unit, imagePlusInput,entry.getKey() );
			tVoxelRecord[i] = voxelRecord;
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
	public double computeRhfIntensite (ImagePlus imagePlusInput, ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter )
	  {
	    double chromocenterIntensity = 0, nucleusIntensity = 0;
	    double voxelValueChromocenter, voxelValueInput, voxelValueSegmented;
	    int i,j,k;
	    ImageStack imageStackChromocenter =  imagePlusChromocenter.getStack();
	    ImageStack imageStackSegmented = imagePlusSegmented.getStack();
	    ImageStack imageStackInput = imagePlusInput.getStack();
	    for (k = 0; k < imagePlusInput.getNSlices(); ++k)
	      for (i = 0; i < imagePlusInput.getWidth(); ++i )
	        for (j = 0; j < imagePlusInput.getHeight(); ++j )
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
	  public double computeRhfVolume (ImagePlus imagePlusSegmented, ImagePlus imagePlusChomocenters)
	  {
	    int i;
	    double volumeCc = 0;
	    double [] tVolumeChromocenter = computeVolumeofAllObjects(imagePlusChomocenters);
	    for (i = 0; i < tVolumeChromocenter.length; ++i) volumeCc += tVolumeChromocenter[i];
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