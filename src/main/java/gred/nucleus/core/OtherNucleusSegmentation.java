package gred.nucleus.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import gred.nucleus.utils.FillingHoles;
import gred.nucleus.utils.Gradient;
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
	
	private int _bestThreshold = 0;
	/** Segmentation parameters*/
	private double _volumeMin;
	/** */
	private double _volumeMax;
	/** */
	private String _logErrorSeg = "";
	
	public OtherNucleusSegmentation (){	}


	/**
	 * 
	 * @param imagePlusInput
	 * @param threshold
	 * @return
	 */
	
	public ImagePlus run (ImagePlus imagePlusInput)
	{
		
		IJ.log("Begin segmentation "+imagePlusInput.getTitle());
		ArrayList<Integer> arrayListThreshold = computeMinMaxThreshold(imagePlusInput);	
		ImagePlus imagePlusSegmented = applySegmentation (imagePlusInput,arrayListThreshold);
		if(_bestThreshold!=0) 
		{
			imagePlusSegmented = correctionSegmentation(imagePlusSegmented,true);
			reverseImage(imagePlusSegmented);
			imagePlusSegmented = correctionSegmentation(imagePlusSegmented,false);
			reverseImage(imagePlusSegmented);
			morphologicalCorrection(imagePlusSegmented);
			
		}
		IJ.log("fin 1");
		IJ.log("End segmentation "+imagePlusInput.getTitle()+" "+_bestThreshold);
		if (_bestThreshold == 0)
		{
			if (_logErrorSeg.length()==0)
			{
				IJ.showMessage("Error Segmentation", "Bad parameter for the segmentation, any object is detected between "
    				  +_volumeMin+" and "+ _volumeMax+" "+ imagePlusInput.getCalibration().getUnit()+"^3");
			}
			else
			{
				File fileLogError = new File (_logErrorSeg);
				BufferedWriter bufferedWriterLogError;
				FileWriter fileWriterLogError;
				try
				{
					fileWriterLogError = new FileWriter(fileLogError, true);
					bufferedWriterLogError = new BufferedWriter(fileWriterLogError);
					bufferedWriterLogError.write(imagePlusInput.getTitle()+"\n");
					bufferedWriterLogError.flush();
					bufferedWriterLogError.close();
				}
				catch (IOException e) { e.printStackTrace(); } 
			}
		}
		return imagePlusSegmented;
	}
	
	/**
	 * 
	 * @param imagePlusInput
	 * @param arrayListThreshold
	 * @return
	 */
	public ImagePlus applySegmentation (ImagePlus imagePlusInput,ArrayList<Integer> arrayListThreshold)
	{
		double sphericityMax = -1.0;
		double sphericity;
		double volume;
		Calibration calibration = imagePlusInput.getCalibration();
		final double xCalibration = calibration.pixelWidth;
		final double yCalibration = calibration.pixelHeight;
		final double zCalibration = calibration.pixelDepth;
		Measure3D measure3D = new Measure3D();
		Gradient gradient = new Gradient(imagePlusInput);
		final double imageVolume = xCalibration*imagePlusInput.getWidth()*yCalibration*imagePlusInput.getHeight()*zCalibration*imagePlusInput.getStackSize();
		IJ.log(xCalibration+" "+yCalibration+" "+zCalibration+"  volume image :"+imageVolume);
		ImagePlus imagePlusSegmented = new ImagePlus();
			
		IJ.log("Lower limit: "+arrayListThreshold.get(0)+" Upper limit "+arrayListThreshold.get(1));
		for (int t = arrayListThreshold.get(0) ; t <= arrayListThreshold.get(1); ++t)
		{
			ImagePlus imagePlusSegmentedTemp = generateSegmentedImage(imagePlusInput,t);
			imagePlusSegmentedTemp = ConnectedComponents.computeLabels(imagePlusSegmentedTemp, 26, 32);
			deleteArtefact(imagePlusSegmentedTemp);
			imagePlusSegmentedTemp.setCalibration(calibration);
			volume = measure3D.computeVolumeObject(imagePlusSegmentedTemp,255);
			imagePlusSegmentedTemp.setCalibration(calibration);
			if (testRelativeObjectVolume(volume,imageVolume) &&
					volume >= _volumeMin &&
					volume <= _volumeMax)
			{	
				
				sphericity = measure3D.computeSphericity(volume,measure3D.computeComplexSurface(imagePlusSegmentedTemp,gradient));
				if (sphericity > sphericityMax )
				{
					_bestThreshold=t;
					sphericityMax = sphericity;
					StackConverter stackConverter = new StackConverter( imagePlusSegmentedTemp );
					stackConverter.convertToGray8();
					imagePlusSegmented= imagePlusSegmentedTemp.duplicate();			
				}
			}
		}		
		morphologicalCorrection(imagePlusSegmented);
		return imagePlusSegmented;
	}
	
	
	
	
	/**
	 *  2 rescale image and do distance Map => obtain image with istrope voxel
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
	 * @param imagePlusInput
	 * @return
	 * @throws IOException 
	 */
	public ImagePlus correctionSegmentation (ImagePlus imagePlusSegmented, boolean threshold)
	{
				Calibration calibration = imagePlusSegmented.getCalibration();
				final double xCalibration = calibration.pixelWidth;
				final double zCalibration = calibration.pixelDepth;
				RadialDistance radialDistance = new RadialDistance();
				ImagePlus imagePlusDistanceMap = radialDistance.computeDistanceMap(resizeImage(imagePlusSegmented));
				ImageStack imageStackDistanceMap = imagePlusDistanceMap.getStack();
				double s_threshold = 5;
				if (threshold)
				{
					Histogram histogram = new Histogram();
					histogram.run(imagePlusDistanceMap);
					s_threshold = histogram.getLabelMax();
				}
				ImageStack imageStackOutput = imagePlusSegmented.getStack();
				while (s_threshold >= xCalibration)
				{					
					IJ.log(s_threshold+" ");
					for (int k = 0; k < imagePlusDistanceMap.getNSlices(); ++k)
						for (int i = 0; i < imagePlusDistanceMap.getWidth(); ++i)
							for (int j = 0; j < imagePlusDistanceMap.getHeight(); ++j)
							{
								if (imageStackDistanceMap.getVoxel(i, j, k) >= s_threshold && imageStackDistanceMap.getVoxel(i, j, k) <= s_threshold+1 )
								{
									double darwin = (k*(xCalibration/zCalibration)-s_threshold);
									int inf_k = (int)(float)(darwin);
									
									if (inf_k < 0) inf_k=0; 
									int sup_k = (int)(float)((k*(xCalibration/zCalibration)+s_threshold));
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
												double plopi = s_threshold*s_threshold;
												if (imageStackOutput.getVoxel(ii, jj, kk)!=255 && plop <= plopi)																					
													imageStackOutput.setVoxel(ii,jj,kk,255);
											}
								}
							}
					s_threshold--;
				}
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
	 * @param imagePlus
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

	private boolean testRelativeObjectVolume(double objectVolume,double imageVolume)
	{
		final double ratio = (objectVolume/imageVolume)*100;
		if (ratio >= 70) return false;
		else return true;
	}
	
	/**
	 * interval of volume to detect the object
	 * 
	 * @param volumeMin
	 * @param volumeMax
	 */
	public void setVolumeRange(double volumeMin, double volumeMax)
	{
		_volumeMin = volumeMin;
		_volumeMax = volumeMax;
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

	private ArrayList<Integer> computeMinMaxThreshold(ImagePlus imagePlusInput)
	{
		ArrayList<Integer> arrayListMinMaxThreshold = new ArrayList<Integer>();
		int threshold = computeThreshold (imagePlusInput);
		StackStatistics stackStatistics = new StackStatistics(imagePlusInput);
		double stdDev =stackStatistics.stdDev ;
		double min = threshold - stdDev*2;
		double max = threshold + stdDev/2;
		if ( min < 0) arrayListMinMaxThreshold.add(6);
		else arrayListMinMaxThreshold.add((int)min);
		arrayListMinMaxThreshold.add((int)max);
		return arrayListMinMaxThreshold;
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
	        if (nbVoxel > nbVoxelMax)
	        {
	        	nbVoxelMax = nbVoxel;
	        	indiceNbVoxelMax = label;
	        }
	    }
	    return indiceNbVoxelMax;
	}
	
	/**
	 * 
	 * @param imagePlusInput
	 */
	public void reverseImage(ImagePlus imagePlusInput)
	{
	    double voxelValue;
	    ImageStack imageStackInput = imagePlusInput.getStack();
	    for(int k = 0; k < imagePlusInput.getNSlices(); ++k)
	    	for (int i = 0; i < imagePlusInput.getWidth(); ++i)
	    		for (int j = 0; j < imagePlusInput.getHeight(); ++j)
	    		{
	    			voxelValue = imageStackInput.getVoxel(i,j,k);
	    			if (voxelValue == 255) imageStackInput.setVoxel(i,j,k,0);
	    			else imageStackInput.setVoxel(i,j,k,255);
	    		}
	}
	
}
