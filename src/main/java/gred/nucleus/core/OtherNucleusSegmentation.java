package gred.nucleus.core;

import java.util.ArrayList;
import java.util.Map.Entry;

import gred.nucleus.utils.FillingHoles;
import gred.nucleus.utils.Histogram;
import ij.*;
import ij.plugin.Filters3D;
import ij.plugin.Resizer;
import ij.process.*;
import ij.measure.*;
import ij.process.AutoThresholder.Method;
import inra.ijpb.binary.ConnectedComponents;


public class OtherNucleusSegmentation
{
	public OtherNucleusSegmentation (){	}

	/**
	 * 
	 * @param imagePlusInput
	 * @return
	 */
	public ImagePlus run (ImagePlus imagePlusInput)
	{
		 /* 2 rescale image and do distance Map => obtain image with istrope voxel
				 * 3 thresholded the distance Map image to creat the "deep kernel". The threshold value is inferior or equal at the "rayon de courbure "????
				 *     => comment j estime un rayon de courbure?
				 * 4 en chaque voxel du deep kernel (peut etre prendre que les voxel exterieur gain de temps?), parcourir tout les voxels appartenant
				 *  a la boule (v,s)
				 *    s => threshold de la distance map 
				 *    v => ??
				 *    si le voxel sur l'image binaire et a 0 le passer a un sinon rien faire :
				 *    		travailler sur une image binaire rescale
				 *    	=> repasse en voxel anistrope l'image final et retourner cette image
				 * 5 eliminer les objer surnumreraire
				 */
				Calibration calibration = imagePlusInput.getCalibration();
				final double xCalibration = calibration.pixelWidth;
				final double yCalibration = calibration.pixelHeight;
				final double zCalibration = calibration.pixelDepth;
			
				IJ.log(imagePlusInput.getTitle());
				ImageStack imageStackInput = imagePlusInput.getImageStack();
				ImagePlus imagePlusSegmented = generateSegmentedImage (imagePlusInput, computeThreshold(imagePlusInput));
				morphologicalCorrection (imagePlusSegmented);
				imagePlusSegmented.setCalibration(calibration);
				double min = computeMin(imagePlusInput);	
				//2 DistanceMap
				RadialDistance radialDistance = new RadialDistance();
				ImagePlus imagePlusDistanceMap = radialDistance.computeDistanceMap(resizeImage(imagePlusSegmented));
				Histogram histogram = new Histogram();
				histogram.run(imagePlusDistanceMap);
				double s_threshold = histogram.getLabelMax();
				double  s_thresholdInitial = s_threshold;
				double compteur = 1;
				while (s_threshold >= 0.103)
				{
					if (s_threshold != s_thresholdInitial )
						imagePlusDistanceMap = radialDistance.computeDistanceMap(resizeImage(imagePlusSegmented));
					ImageStack imageStackDistanceMap = imagePlusDistanceMap.getStack();
					ImageStack imageStackOutput = imagePlusSegmented.getStack();
					for (int k = 0; k < imagePlusDistanceMap.getNSlices(); ++k)
						for (int i = 0; i < imagePlusDistanceMap.getWidth(); ++i)
							for (int j = 0; j < imagePlusDistanceMap.getHeight(); ++j)
							{
								if (imageStackDistanceMap.getVoxel(i, j, k) >= s_threshold)
								{
									int inf_k = (int)(float)((k-s_threshold)*(xCalibration/zCalibration));
									if (inf_k < 0) inf_k=0; 
									int sup_k = (int)(float)((k+s_threshold)*(xCalibration/zCalibration));
									if (sup_k > imagePlusSegmented.getNSlices()) sup_k = imagePlusSegmented.getNSlices();
									int inf_i = (int)(float)(i-s_threshold);
									if (inf_i < 0) inf_i=0; 
									int sup_i = (int)(float)(i+s_threshold);
									if (sup_i > imagePlusSegmented.getWidth()) sup_i = imagePlusSegmented.getWidth();
									int inf_j = (int)(float)(j-s_threshold);
									if (inf_j < 0) inf_j=0; 
									int sup_j = (int)(float)(j+s_threshold);
									if (sup_j > imagePlusSegmented.getHeight()) sup_j = imagePlusSegmented.getHeight();
									for (int kk = inf_k; kk < sup_k ; ++kk)
										for (int ii =  inf_i; ii < sup_i; ++ii)
											for (int jj =  inf_j; jj < sup_j; ++jj)
											{
												double plop = (ii-i)*(ii-i)+(jj-j)*(jj-j)+(kk-(k*(xCalibration/zCalibration)))*(kk-(k*(xCalibration/zCalibration)));
												if (imageStackInput.getVoxel(ii,jj,kk) >= min
														&& plop <= s_threshold*s_threshold)
													imageStackOutput.setVoxel(ii,jj,kk,255);	
											}
								}
							}
					if (compteur > 1 ) s_threshold--;
					compteur++;
				}
				
				imagePlusSegmented = ConnectedComponents.computeLabels(imagePlusSegmented, 6, 32);
				deleteArtefact(imagePlusSegmented);
				morphologicalCorrection (imagePlusSegmented);
				imagePlusSegmented.setCalibration(calibration);
				return imagePlusSegmented;
	}
	
	
	/**
	 * 
	 * @param imagePlusInput
	 * @return
	 */
	private int computeThreshold (ImagePlus imagePlusInput)
	{
		AutoThresholder autoThresholder = new AutoThresholder();
		ImageStatistics imageStatistics = new StackStatistics(imagePlusInput);
		int [] tHisto = imageStatistics.histogram;
		return autoThresholder.getThreshold(Method.Otsu,tHisto);
	}
	/**
	 * 
	 * @param imagePlus
	 * @return
	 */
	private ImagePlus resizeImage (ImagePlus imagePlus)
	{
		Resizer resizer = new Resizer();
		Calibration calibration = imagePlus.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double zCalibration = calibration.pixelDepth;
		double rescaleZFactor = zCalibration/xCalibration;
		ImagePlus imagePlusRescale = resizer.zScale(imagePlus,(int)(imagePlus.getNSlices()*rescaleZFactor), 0);
		return imagePlusRescale;
	}
	/**
	 * 
	 * @param imagePlusInput
	 * @param threshold
	 * @return
	 */
	private ImagePlus generateSegmentedImage (ImagePlus imagePlusInput, int threshold)
	{
		ImageStack imageStackInput = imagePlusInput.getStack();
		ImagePlus imagePlusSegmented = imagePlusInput.duplicate();
		ImageStack imageStackSegmented = imagePlusSegmented.getStack();
		for(int k = 0; k < imagePlusInput.getStackSize(); ++k)
			for (int i = 0; i < imagePlusInput.getWidth(); ++i )
				for (int j = 0; j < imagePlusInput.getHeight(); ++j )
				{
					double voxelValue = imageStackInput.getVoxel(i,j,k);
					if (voxelValue >= threshold) imageStackSegmented.setVoxel(i,j,k,255);
					else imageStackSegmented.setVoxel(i,j,k,0);
				}
		return imagePlusSegmented;
	}
	
	/**
	 * 
	 * 
	 */
	private void morphologicalCorrection (ImagePlus imagePlusSegmented)
	{
		FillingHoles holesFilling = new FillingHoles();
		computeOpening(imagePlusSegmented);
		computeClosing(imagePlusSegmented);
		imagePlusSegmented = holesFilling.apply2D(imagePlusSegmented);
	}


	/**
	 * 
	 * @param imagePlusInput
	 */
	private void computeClosing (ImagePlus imagePlusInput)
	{
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MAX,1,1,(float)0.5);
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MIN,1,1,(float)0.5);
		imagePlusInput.setStack(imageStackInput);
	}

	/**
	 * 
	 * @param imagePlusInput
	 */
	private void computeOpening (ImagePlus imagePlusInput)
	{
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MIN,1,1,(float)0.5);
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MAX,1,1,(float)0.5);
		imagePlusInput.setStack(imageStackInput);
	}
	
	 
	
	/**
	 * Preserve the larger object and remove the other
	 *
	 * @param imagePluslab Image labeled
	 */

	public void deleteArtefact (ImagePlus imagePlusInput)
	{
	    double voxelValue;
	    double mode = getLabelOfLargestObject(imagePlusInput);
	    ImageStack imageStackInput = imagePlusInput.getStack();
	    for(int k = 0; k < imagePlusInput.getNSlices(); ++k)
	    	for (int i = 0; i < imagePlusInput.getWidth(); ++i)
	    		for (int j = 0; j < imagePlusInput.getHeight(); ++j)
	    		{
	    			voxelValue = imageStackInput.getVoxel(i,j,k);
	    			if (voxelValue == mode) imageStackInput.setVoxel(i,j,k,255);
	    			else imageStackInput.setVoxel(i,j,k,0);
	    		}
	}

	private double computeMin(ImagePlus imagePlusInput)
	{
		ArrayList<Integer> arrayListMinMaxThreshold = new ArrayList<Integer>();
		int threshold = computeThreshold (imagePlusInput);
		StackStatistics stackStatistics = new StackStatistics(imagePlusInput);
		double stdDev =stackStatistics.stdDev ;
		double min = threshold - stdDev*2;
		double max = threshold + stdDev;
		if ( min < 0) arrayListMinMaxThreshold.add(1);
		return min;
	}
	/**
	 * Browse each object of image and return the label of the larger object
	 * @param imagePluslab Image labeled
	 * @return Label of the larger object
	 */

	public double getLabelOfLargestObject(ImagePlus imagePlusInput)
	{
		Histogram histogram = new Histogram();
		histogram.run(imagePlusInput);
	    double indiceNbVoxelMax = 0;
	    double nbVoxelMax = -1;
	    for(Entry<Double, Integer> entry : histogram.getHistogram().entrySet())
	    {
	    	double label = entry.getKey();
	        int nbVoxel = entry.getValue();
	        IJ.log("label"+label+" nb "+nbVoxel);
	        if (nbVoxel > nbVoxelMax)
	        {
	        	nbVoxelMax = nbVoxel;
	        	indiceNbVoxelMax = label;
	        }
	    }
	    return indiceNbVoxelMax;
	}
}
