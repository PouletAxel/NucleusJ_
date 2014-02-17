package gred.nucleus.core;

import gred.nucleus.utils.Histogram;
import gred.nucleus.utils.VoxelRecord;
import ij.*;
import ij.measure.*;

import java.util.HashMap;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * Class NucleusMeasure : which compute differents parameters (shape, lenght) in
 * binary object
 * @author Poulet Axel
 */


public class Measure3D
{
	/**
	 * 
	 */
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
		Calibration cal= imagePlusInput.getCalibration();
		ImageStack imageStakInput = imagePlusInput.getStack();
		double dimX = cal.pixelWidth;
		double dimY = cal.pixelHeight;
		double dimZ = cal.pixelDepth;
		double surface = 0, voxelValue, neigVoxelValue;
		for (k = 0; k < imagePlusInput.getStackSize(); ++k)
			for (i = 0; i < imagePlusInput.getWidth(); ++i)
				for (j = 0; j < imagePlusInput.getHeight(); ++j)
				{
					voxelValue = imageStakInput.getVoxel(i, j, k);
					if (voxelValue == label)
					{
						for (kk = k-1; kk <= k+1; ++kk)
						{
							neigVoxelValue = imageStakInput.getVoxel(i, j, kk);
							if (voxelValue != neigVoxelValue) { surface = surface + dimX * dimY; }
						}
						for (ii=i-1; ii<=i+1; ++ii)
						{
							neigVoxelValue =  imageStakInput.getVoxel(ii, j, k);
							if (voxelValue != neigVoxelValue) { surface = surface + dimX * dimZ; }
						}
						for (jj = j-1; jj <= j+1; ++jj)
						{
							neigVoxelValue = imageStakInput.getVoxel(i, jj, k);
							if (voxelValue != neigVoxelValue) { surface = surface + dimY * dimZ; }
						}
					}
				}
		return surface;
	}

	/**
	 * 
	 * @param imagePlusInput
	 * @return
	 */
	
	public int getNbObject(ImagePlus imagePlusInput)
	{  
		Histogram histogram = new Histogram(imagePlusInput);
		return histogram.getHisto().size();
	}
	
	/**
	 * Method which compute the volume of each segmented objects
	 * in _imagePlus
	 * @return Table of Object volume
	 * 
	 * @param imagePlusInput
	 * @return
	 */
	public double[] computeVolumeofAllObjects (ImagePlus imagePlusInput)
	{
		Histogram histogram = new Histogram(imagePlusInput);
		double tabLabel[] = histogram.getLabel();
		double objectVolume[] = new double[tabLabel.length];
		objectVolume[0] = 0;
		for (int i = 0; i < tabLabel.length; ++i)
			objectVolume[i] = computeVolumeObject(imagePlusInput, tabLabel[i]);
		return objectVolume;
	} 

	/**
	 * 
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */
	public double computeVolumeObject (ImagePlus imagePlusInput, double label)
	{
		Calibration cal= imagePlusInput.getCalibration();
		double dimX = cal.pixelWidth;
		double dimY = cal.pixelHeight;
		double dimZ = cal.pixelDepth;
		double volume = 0;
		Histogram histogram = new Histogram (imagePlusInput);
		HashMap<Double , Integer> hHisto = histogram.getHisto();
		volume =  hHisto.get(label) *dimX *dimY *dimZ;
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
	 * @return sphericity
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
	
	public double [] ComputeEigenValue3D (ImagePlus imagePlusInput, double label)
	{
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		VoxelRecord barycenter = computeBarycenter3D (true,imagePlusInput,label);
		Calibration cal= imagePlusInput.getCalibration();
		double dimX = cal.pixelWidth;
		double dimY = cal.pixelHeight;
		double dimZ = cal.pixelDepth;
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
						xx+= ((dimX * (double) i)-barycenter.getI()) * ((dimX * (double) i)-barycenter.getI());
						yy+= ((dimY * (double) j)-barycenter.getJ()) * ((dimY * (double) j)-barycenter.getJ());
						zz+= ((dimZ * (double) k)-barycenter.getK()) * ((dimZ * (double) k)-barycenter.getK());
						xy+= ((dimX * (double) i)-barycenter.getI()) * ((dimY * (double) j)-barycenter.getJ());
						xz+= ((dimX * (double) i)-barycenter.getI()) * ((dimZ * (double) k)-barycenter.getK());
						yz+= ((dimY * (double) j)-barycenter.getJ()) * ((dimZ * (double) k)-barycenter.getK());
						compteur++;
					}
				}
		double[][] vals = {{xx / compteur, xy / compteur, xz / compteur},
                      {xy / compteur, yy / compteur, yz / compteur},
                      {xz / compteur, yz / compteur, zz / compteur}};
		Matrix matrice = new Matrix (vals);
		EigenvalueDecomposition eigen =  matrice.eig();
		return eigen.getRealEigenvalues();
	}
	
	/**
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */
	public double computeElongationObject (ImagePlus imagePlusInput, double label)
	{
		double eigen [] = ComputeEigenValue3D (imagePlusInput, label);
		return Math.sqrt (eigen[2] / eigen[1]);
	}
  
	/**
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */
	public double computeFlatnessObject (ImagePlus imagePlusInput, double label)
	{
		double eigen [] = ComputeEigenValue3D (imagePlusInput,label);
		return Math.sqrt(eigen[1] / eigen[0]);
	}  

	/**
	 * Method which determines the barycenter of nucleus
	 * 
	 * @param unit if true the coordinates of barycenter are in µm.
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */
	public VoxelRecord computeBarycenter3D (boolean unit,ImagePlus imagePlusInput, double label)
  	{
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		Calibration cal= imagePlusInput.getCalibration();
		double dimX = cal.pixelWidth;
		double dimY = cal.pixelHeight;
		double dimZ = cal.pixelDepth;
		VoxelRecord barycenter = new VoxelRecord ();
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
						barycenter.shiftCoordinates(voxelRecord);
						compteur++;
					}
				}
		barycenter.Multiplie(1 / (double)compteur);
		if (unit) barycenter.Multiplie(dimX, dimY,dimZ);
		return barycenter;
  	}
	
	/**
	 * Method which compute the barycenter of each objects and return the result
	 * in a table of VoxelRecord
	 * 
	 * @param imagePlusInput
	 * @return
	 */
	public VoxelRecord[] computeObjectBarycenter (ImagePlus imagePlusInput)
	{
		Histogram histogram = new Histogram(imagePlusInput);
		double labelObject [] = histogram.getLabel();
		VoxelRecord tabVoxelRecord[] = new VoxelRecord [labelObject.length];
		tabVoxelRecord[0] = null;
		for (int i = 0; i < labelObject.length; ++i)
		{
			VoxelRecord voxelRecord = computeBarycenter3D(true, imagePlusInput,labelObject[i] );
			tabVoxelRecord[i] = voxelRecord;
		}
		return tabVoxelRecord;
	}
	/**
	 * 
	 * @param imagePlusInput
	 * @param imagePlusBinary
	 * @param imagePlusChromocenter
	 * @return
	 */
	public double computeRhfIntensite (ImagePlus imagePlusInput, ImagePlus imagePlusBinary, ImagePlus imagePlusChromocenter )
	  {
	    double ccIntensity = 0, nucleusIntensity = 0;
	    double voxelValueLabel, voxelValueDeconv, voxelValueBinaire;
	    int i,j,k;
	    
	    ImageStack _imageStackSegmentationChromocenter =  imagePlusChromocenter.getStack();
	    ImageStack imageStackBinaire = imagePlusBinary.getStack();
	    ImageStack imageStackDeconv = imagePlusInput.getStack();
	    for (k = 0; k < imagePlusInput.getNSlices(); ++k)
	      for (i = 0; i < imagePlusInput.getWidth(); ++i )
	        for (j = 0; j < imagePlusInput.getHeight(); ++j )
	        {
	          voxelValueBinaire = imageStackBinaire.getVoxel(i, j, k);
	          voxelValueDeconv = imageStackDeconv.getVoxel(i, j, k);
	          voxelValueLabel = _imageStackSegmentationChromocenter.getVoxel(i,j,k);
	     
	          if (voxelValueBinaire > 0)
	          {
	            if (voxelValueLabel > 0){ ccIntensity+=voxelValueDeconv;}
	            nucleusIntensity += voxelValueDeconv;
	          }
	        }
	    return ccIntensity / nucleusIntensity;
	  }//computeRhfIntensite

	  /**
	   * Method which compute the RHF (total chromocenters volume / nucleus volume)
	   * @return RHF
	   *
	   * @param imagePlusBinaryNucleus
	   * @param imagePlusChomocentersLabeled
	   * @return
	   */
	  public double computeRhfVolume (ImagePlus imagePlusBinaryNucleus, ImagePlus imagePlusChomocentersLabeled)
	  {
	    int i;
	    double volumeCc = 0;
	    double tabVolume[] = computeVolumeofAllObjects(imagePlusChomocentersLabeled);
	    for (i = 0; i < tabVolume.length; ++i) volumeCc += tabVolume[i];
	    double tabVolume2[] = computeVolumeofAllObjects(imagePlusBinaryNucleus);
	    return volumeCc / tabVolume2[0];
	  } 
}