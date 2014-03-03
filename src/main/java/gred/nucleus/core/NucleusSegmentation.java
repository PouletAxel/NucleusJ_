package gred.nucleus.core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import gred.nucleus.utils.FillingHoles;
import gred.nucleus.utils.Histogram;
import ij.*;
import ij.plugin.Filters3D;
import ij.process.*;
import ij.measure.*;
import ij.process.AutoThresholder.Method;
import inra.ijpb.binary.ConnectedComponents;

/**
 * Class to realise the segmention of object in the image in input. This segmentation
 * is based on the method of Otsu, execpt I maximise the sphericity (shape parameter)
 * of object.
 * 
 * @author Poulet Axel
 *
 */
public class NucleusSegmentation
{
	
	private int _bestThreshold = 0;
	/** Segmentation parameters*/
	private double _volumeMin, _volumeMax;
	/** */
	private String _logErrorSeg = "";
  
	/**
	 * Constructor
	 * @param imagePlusInput Image to be segmente
	 */

	public NucleusSegmentation (){	}

	/**
	 * Method which run the process in input image. This image will be segmented, and
	 * the binary image will be save in a directory. Then we realise the image gradient
	 * and the image contrast of the input image.
	 * The image contrast is saved in other directory. It is from this image contrast
	 * that a manual thresholding is necessary to segment chromocentre.
	 * @param arg
	 */
	
	public ImagePlus run (ImagePlus imagePlusInput)
	{
		IJ.log("Begin segmentation "+imagePlusInput.getTitle());
		ImagePlus imagePlusBinary = applySegmentation (imagePlusInput);
		IJ.log("End segmentation "+imagePlusInput.getTitle());
		
		if (_bestThreshold == 0)
		{
			if (_logErrorSeg.length()==0)
			{
				IJ.showMessage("Error Segmentation", "Bad parameter for the segmentation, any object is detected between "
    				  +_volumeMin+" and "+ _volumeMax+" "+ imagePlusInput.getCalibration().getUnit()+"^3");
			}
			else
			{
				File fileResu = new File (_logErrorSeg);
				BufferedWriter output;
				FileWriter fw;
				try
				{
					fw = new FileWriter(fileResu, true);
					output = new BufferedWriter(fw);
					output.write(imagePlusInput.getTitle()+"\n");
					output.flush();
					output.close();
				}
				catch (IOException e) { e.printStackTrace(); } 
			}
		}
		return imagePlusBinary;
	}
	/**
	 * Compute the first threshold of input image with the method of Otsu
	 * From this initial value we will seek the better segmentaion possible:
	 * for this we will take the voxels value superior at the threshold value of method of Otsu :
	 * Then we compute the standard deviation of this values voxel > threshold value
	 * determines which allows range of value we will search the better threshodl value :
	 *   thresholdOtsu-ecartType et thresholdOtsu+ecartType.
	 * For each threshold test; we realize a opening and a closing, then we use 
	 * the holesFilling. To finish we compute the sphericity.
	 * The aim of this method is to maximize the sphericity to obtain the segmented object
	 * nearest of the biological object.
	 * @return : return thresholded image.
	 */
  
	public ImagePlus applySegmentation (ImagePlus imagePlusInput)
	{
		Calibration calibration = imagePlusInput.getCalibration();
		final double dimX = calibration.pixelWidth;
		final double dimY = calibration.pixelHeight;
		final double dimZ = calibration.pixelDepth;
		final double imageVolume = dimX*imagePlusInput.getWidth()*dimY*imagePlusInput.getHeight()*dimZ*imagePlusInput.getStackSize();
		IJ.log(dimX+" "+dimY+" "+dimZ+"  volume image :"+imageVolume);
		ImagePlus imagePlusOutput = new ImagePlus();
		double sphericityMax = -1.0, sphericity, volume;
		ArrayList<Integer> al = computeMinMaxThreshold(imagePlusInput);		
		IJ.log("borne inf: "+al.get(0)+" bornSupThreshold "+al.get(1));
		for (int t = al.get(0) ; t <= al.get(1); ++t)
		{
			ImagePlus imagePlusSegmentedNucleusTemp = generateBinaryImage(imagePlusInput,t);
			morphoCorrection (imagePlusSegmentedNucleusTemp);
			imagePlusSegmentedNucleusTemp = ConnectedComponents.computeLabels(imagePlusSegmentedNucleusTemp, 26, 8);
			deleteArtefactNucleus(imagePlusSegmentedNucleusTemp);
			imagePlusSegmentedNucleusTemp.setCalibration(calibration);
			Measure3D measure3D = new Measure3D();
			volume = measure3D.computeVolumeObject(imagePlusSegmentedNucleusTemp,255);
			sphericity = measure3D.computeSphericity(volume,measure3D.computeSurfaceObject(imagePlusSegmentedNucleusTemp, 255));
			if (sphericity > sphericityMax && volume >= _volumeMin && volume <= _volumeMax && testRelativeObjectVolume(volume,imageVolume))
			{
				_bestThreshold=t;
				sphericityMax = sphericity;
				imagePlusOutput= imagePlusSegmentedNucleusTemp.duplicate();			
			}
		}
		IJ.log ("end segmentation "+imagePlusInput.getTitle()+" "+_bestThreshold);
		imagePlusOutput.setCalibration(calibration);
		return imagePlusOutput;
	}

	
	/**
	 * 
	 * @param imagePlusInput
	 * @return
	 */
	private int computeThreshold (ImagePlus imagePlusInput)
	{
		AutoThresholder thresholder = new AutoThresholder();
		ImageStatistics stats = new StackStatistics(imagePlusInput);
		int tabHisto[] = stats.histogram;
		return thresholder.getThreshold(Method.Otsu,tabHisto);
	}// computeThreshold

	
	/**
	 * 
	 * @param imagePlusInput
	 * @param threshold
	 * @return
	 */
	private double computeMean (ImagePlus imagePlusInput,int threshold)
	{
		double sum = 0, ni_xi = 0;
		Histogram histogram = new Histogram ();
		histogram.run(imagePlusInput);
		for(Entry<Double, Integer> entry : histogram.getHistogram().entrySet())
		{
		   double label = entry.getKey();
		   int nbVoxel = entry.getValue();
		   ni_xi = ni_xi+ label*nbVoxel;
			sum += nbVoxel;
		}
		return ni_xi/sum;
	}


	/**
	 * 
	 * @param imagePlusInput
	 * @param threshold
	 * @return
	 */
	private double computeStandardDeviation (ImagePlus imagePlusInput, int threshold)
	{
		double sce = 0, sum = 0;
		Histogram histogram = new Histogram ();
		histogram.run(imagePlusInput);
		double mean = computeMean(imagePlusInput,threshold);
		for(Entry<Double, Integer> entry : histogram.getHistogram().entrySet())
		{
		   double label = entry.getKey();
		   int nbVoxel = entry.getValue();
		   sum += nbVoxel;
		   sce += nbVoxel * ((label - mean) * (label - mean));
		}
		return Math.sqrt(sce / (sum - 1));
	} 

	
	/**
	 * 
	 * @param imagePlusInput
	 * @param threshold
	 * @return
	 */
	private ImagePlus generateBinaryImage (ImagePlus imagePlusInput, int threshold)
	{
		int i, j, k;
		ImageStack imageStackInput = imagePlusInput.getStack();
		ImagePlus imagePlusOutPut = imagePlusInput.duplicate();
		ImageStack imageStackOutput = imagePlusOutPut.getStack();
		for(k = 0; k < imagePlusInput.getStackSize(); ++k)
			for (i = 0; i < imagePlusInput.getWidth(); ++i )
				for (j = 0; j < imagePlusInput.getHeight(); ++j )
				{
					double voxelValue = imageStackInput.getVoxel(i,j,k);
					if (voxelValue >= threshold) imageStackOutput.setVoxel(i,j,k,255);
					else imageStackOutput.setVoxel(i,j,k,0);
				}
		return imagePlusOutPut;
	}

	/**
	 * 
	 * @param imagePlusInput
	 * @return
	 */
	private ArrayList<Integer> computeMinMaxThreshold(ImagePlus imagePlusInput)
	{
		ArrayList<Integer> arrayListminMaxThreshold = new ArrayList<Integer>();
		int threshold = computeThreshold (imagePlusInput);
		double ecartType = computeStandardDeviation(imagePlusInput,threshold);
		if (0 > threshold - (int)(ecartType)) arrayListminMaxThreshold.add(1);
		else arrayListminMaxThreshold.add(threshold - (int)(ecartType));
		arrayListminMaxThreshold.add(threshold + (int)(ecartType));
		return arrayListminMaxThreshold;
	
	}//computeBornInfThreshold


	/**
	 * compute openning et  use the HolesFilling
	 * @param imagePlusBinary image to be correct
	 */
	private void morphoCorrection (ImagePlus imagePlusBinary)
	{
		FillingHoles holesFilling = new FillingHoles();
		computeOpening(imagePlusBinary);
		computeClosing(imagePlusBinary);
		imagePlusBinary = holesFilling.apply2D(imagePlusBinary);
	}//morphoCorrection


	/**
	 * 
	 * @param imagePlusInput
	 */
	private void computeClosing (ImagePlus imagePlusInput)
	{
		ImageStack stackTemp = imagePlusInput.getImageStack();
		stackTemp = Filters3D.filter(stackTemp, Filters3D.MAX, 1, 1, (float) 0.5);
		stackTemp = Filters3D.filter(stackTemp, Filters3D.MIN, 1, 1, (float) 0.5);
		imagePlusInput.setStack(stackTemp);
	}

	/**
	 * 
	 * @param imagePlusInput
	 */
	private void computeOpening (ImagePlus imagePlusInput)
	{
		ImageStack stackTemp = imagePlusInput.getImageStack();
		stackTemp = Filters3D.filter(stackTemp, Filters3D.MIN, 1, 1, (float) 0.5);
		stackTemp = Filters3D.filter(stackTemp, Filters3D.MAX, 1, 1, (float) 0.5);
		imagePlusInput.setStack(stackTemp);
	}
  

	/**
	 * 
	 * @return
	 */
	public int getBestThreshold (){ return _bestThreshold;}
	
	/**
	 * 
	 * @param objectVolume
	 * @return
	 */
	private boolean testRelativeObjectVolume(double objectVolume,double imageVolume)
	{
		final double ratio = (objectVolume/imageVolume)*100;
		if (ratio >= 70) return false;
		else return true;
	}
	
	/**
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
	   * Preserve the larger object and remove the other objects
	   *
	   * @param imagePluslab Image labeled
	   */

	  public void deleteArtefactNucleus (ImagePlus imagePlusInput)
	  {
	    int i,j,k;
	    double voxel;
	    double mode = getLabelOfLargestObject(imagePlusInput);
	    ImageStack imageStackInput = imagePlusInput.getStack();
	    for(k = 0; k < imagePlusInput.getNSlices(); ++k)
	      for (i = 0; i < imagePlusInput.getWidth(); ++i)
	        for (j = 0; j < imagePlusInput.getHeight(); ++j)
	        {
	          voxel = imageStackInput.getVoxel(i,j,k);
	          if (voxel == mode) imageStackInput.setVoxel(i,j,k,255);
	          else imageStackInput.setVoxel(i,j,k,0);
	     }
	   }//deleteArtefactNoyau

	  /**
	   * Browse each object of image and return the label of the larger object
	   * @param imagePluslab Image labeled
	   * @return Label of the larger object
	   */

	  public double getLabelOfLargestObject(ImagePlus imagePlusInput)
	  {
		Histogram histogram = new Histogram();
		histogram.run(imagePlusInput);
	    double indiceNbVoxelMax = 0, nbVoxelMax = -1;
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
	  }//getLabelOfLargestObject
	 
	  /**
	   * 
	   * @param logErrorSeg
	   */
		public void setLogErrorSegmentationFile (String logErrorSeg)
		{
			_logErrorSeg = logErrorSeg;
		}
}