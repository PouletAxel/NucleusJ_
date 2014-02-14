package gred.nucleus.nucleusSegmentation;
import java.util.Arrays;
import java.util.HashMap;

import gred.nucleus.parameters.ShapeParameters3D;
import gred.nucleus.treatment.*;
import gred.nucleus.analysis.MyCounter3D;
import gred.nucleus.utilitaires.Histogram;
import ij.*;
import ij.process.*;
import ij.measure.*;
import ij.process.AutoThresholder.Method;
import Filter3D.*;

/**
 * Class to realise the segmention of object in the image in input. This segmentation
 * is based on the method of Otsu, execpt I maximise the sphericity (shape parameter)
 * of object.
 * 
 * @author Poulet Axel
 *
 */
public class MySegmentation
{
	/** Image to be process*/
	ImagePlus _imagePlusInput;
	/** Voxel calibration in µm*/
	private double _dimX, _dimY, _dimZ;
	/** height, width, depth of image in pixel*/
	private int _height, _width,_depth;
	/** Image stack of _imagePlusInput*/
	ImageStack _imageStackInput = new ImageStack();
	/** Better Threshold value */
	private int _bestThreshold = 0;
	/** Segmentation parameters*/
	private double _vmin, _vmax;
	Calibration _cal;
	private double _imageVolume;
  
	/**
	 * Constructor
	 * @param imagePlusInput Image to be segmente
	 */

	public MySegmentation (ImagePlus imagePlusInput, double volumeMin, double volumeMax)
	{
		_imagePlusInput = imagePlusInput;
		_vmin = volumeMin;
		_vmax = volumeMax;
		_cal = _imagePlusInput.getCalibration();
		_imageStackInput = _imagePlusInput.getImageStack();
		_width = _imagePlusInput.getWidth();
		_height = _imagePlusInput.getHeight();
		_depth = _imagePlusInput.getStackSize();
		_dimX = _cal.pixelWidth;
		_dimY = _cal.pixelHeight;
		_dimZ = _cal.pixelDepth;
		_imageVolume = _width*_dimX*_dimY*_dimZ*_height*_width;
		IJ.log(_dimX+" "+_dimY+" "+_dimZ+"  volume image :"+_imageVolume);
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
  
	public ImagePlus computeSegmentation ()
	{
		ImagePlus imagePlusOutput = new ImagePlus();
		double sphericityMax = 0, sphericity = 0, volume;
		int bornInfThreshold= computeBornInfThreshold();
		int bornSupThreshold = computeBornSupThreshold();
		IJ.log("borne inf: "+bornInfThreshold+" bornSupThreshold "+bornSupThreshold);
		for (int i = bornInfThreshold ; i <= bornSupThreshold/2; ++i)
		{
			//segmentation the threshold value = i
			ImagePlus imagePlusBinTmp = generatesBinaryImage(i);
			// labeling of  differents object if they exist
			MyCounter3D myCounter3D = new MyCounter3D(imagePlusBinTmp);
			imagePlusBinTmp = myCounter3D.getObjMap();
			removeFalsePositive (imagePlusBinTmp);
		  
			morphoCorrection (imagePlusBinTmp);
		  
			myCounter3D = new MyCounter3D(imagePlusBinTmp);
			imagePlusBinTmp = myCounter3D.getObjMap();
			removeFalsePositive (imagePlusBinTmp);
		  
			//comupte the sphericity
			ShapeParameters3D sp3D = new ShapeParameters3D(imagePlusBinTmp, 255);
			sphericity = sp3D.computeSphericity();
			volume = sp3D.getVolume();
		  
			// test sphericity max, lenght of nucleus
			if (sphericity > sphericityMax && volume > _vmin && volume < _vmax && TestRatioObjectVolumeImageVolume(volume))
			{
				_bestThreshold=i;
				sphericityMax = sphericity;
				imagePlusOutput= imagePlusBinTmp.duplicate();			 
			}
		}
		IJ.log ("fin segmentation "+_imagePlusInput.getTitle()+" "+_bestThreshold);
		return imagePlusOutput;
	}//getBinaryMaxCompactness

	/**
	 * Method which compute the threshold value of Otsu method
	 * @return threshold value
	 */
  
	private int computeThreshold ()
	{
		AutoThresholder thresholder = new AutoThresholder();
		ImageStatistics stats = new StackStatistics(_imagePlusInput);
		int tabHisto[] = stats.histogram;
		return thresholder.getThreshold(Method.Otsu,tabHisto);
	}// computeThreshold

	/**
	 * Methode which compute the mean of voxel value > threshold value
	 * @param threshold
	 * @return mean of gray value
	 */

	private double computeMean (int threshold)
	{
		double sum = 0, ni_xi = 0;
		Histogram hist = new Histogram (_imagePlusInput);
		double label [] = hist.getLabel();
		Arrays.sort(label);
		HashMap<Double , Integer> hHisto = hist.getHisto();
		int i;
		for (i = threshold; i < label.length; ++i)
		{
			ni_xi = ni_xi+ label[i]*hHisto.get(label[i]);
			sum += hHisto.get(label[i]);
		}
		return ni_xi/sum;
	}//computeMean

	/**
	 * Compute the standard Deviation  of voxel value > threshold value
	 * @param threshold 
	 * @return standard Deviation
	 */
	
	private double computeStandardDeviation (int threshold)
	{
		double sce = 0, sum = 0;
		Histogram hist = new Histogram (_imagePlusInput);
		double label [] = hist.getLabel();
		Arrays.sort(label);
		HashMap<Double , Integer> hHisto = hist.getHisto();
		int i;
    
		double mean = computeMean(threshold);
		for (i = threshold; i < label.length; ++i)
		{
			sum += hHisto.get(label[i]);
			sce += hHisto.get(label[i]) * ((label[i] - mean) * (label[i] - mean));
		}
		return Math.sqrt((1 / (sum - 1)) * sce);
	} //computeStandardDeviation

	/**
	 * Method which create binary image whit a threshold value and the image input.
	 *  Voxel==255 if the value of voxel > threshold value. Else voxel == 0
	 * @param threshold 
	 * @return binary image
	 */

	private ImagePlus generatesBinaryImage (int threshold)
	{
		int i, j, k;
		ImagePlus imagePlusOutPut = _imagePlusInput.duplicate();
		ImageStack imageStackOutput = imagePlusOutPut.getStack();
		for(k = 0; k < _depth; ++k)
			for (i = 0; i < _width; ++i )
				for (j = 0; j < _height; ++j )
				{
					double voxelValue = _imageStackInput.getVoxel(i,j,k);
					if (voxelValue >= threshold) imageStackOutput.setVoxel(i,j,k,255);
					else imageStackOutput.setVoxel(i,j,k,0);
				}
		return imagePlusOutPut;
	} //generatesBinaryImage

  
	/**
	 * Compute the value max of range of value voxel to serch the better threshold
	 * value
	 * @return upper bound
	 */
	private int computeBornSupThreshold()
	{
		int threshold = computeThreshold();
		double ecartType = computeStandardDeviation(threshold);
		return threshold + (int)(ecartType);
	}// computeBornSupThreshold

	/**
	 * Compute the value min of range of value voxel to serch the better threshold
	 * @return lower bound
	 */
	private int computeBornInfThreshold()
	{
		int threshold = computeThreshold();
		double ecartType = computeStandardDeviation(threshold);
		if (0>threshold - (int)(ecartType)) return 1;
		else return threshold - (int)(ecartType);
	}//computeBornInfThreshold

	/**
	 * Use the class ArtefactTreatementNucleus to remove false positifof segmented image
	 *  
	 * @param imagePlusBinaireLabel
	 */
	private void removeFalsePositive(ImagePlus imagePlusBinaireLabel)
	{
		ArtefactTreatement artefactTreatementNucleus = new ArtefactTreatement(imagePlusBinaireLabel);//
		artefactTreatementNucleus.deleteArtefactNoyau();
	}

	/**
	 * compute openning et  use the HolesFilling
	 * @param imagePlusBinary image to be correct
	 */
	private void morphoCorrection (ImagePlus imagePlusBinary)
	{
		FillingHoles holesFilling = new FillingHoles(imagePlusBinary);
		computeOpening(imagePlusBinary, 3);
		imagePlusBinary = holesFilling.apply2D();
		computeClosing(imagePlusBinary, 3);
		imagePlusBinary = holesFilling.apply2D();
		//imagePlusBinary = holesFilling.apply3D();
		imagePlusBinary.setCalibration(_cal);
	}//morphoCorrection

	/**
	 * Realise a closing in the image to remove the false negative
	 * @param imagePlusInput image to be process
	 * @param radius radius of closing
	 */
	private void computeClosing (ImagePlus imagePlusInput, int radius)
	{
		ImageStack stackTemp = imagePlusInput.getImageStack();
		Filter3Dmax filtermax = new Filter3Dmax(imagePlusInput, stackTemp, radius);
		filtermax.filter();
		imagePlusInput.setStack(stackTemp);
		stackTemp= imagePlusInput.getImageStack();
		Filter3Dmin filtermin = new Filter3Dmin(imagePlusInput, stackTemp, radius);
		filtermin.filter();
		imagePlusInput.setStack(stackTemp);
	}//computeFermeture

	/**
	 * Realise a opening in the image to remove the false negative
	 * @param imagePlusInput image to be process
	 * @param radius radius of opening
	 */
	private void computeOpening (ImagePlus imagePlusInput, int radius)
	{
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		Filter3Dmin filtermin = new Filter3Dmin(imagePlusInput, imageStackInput, radius);
		filtermin.filter();
		imagePlusInput.setStack(imageStackInput);
		imageStackInput= imagePlusInput.getImageStack();
		Filter3Dmax filtermax = new Filter3Dmax(imagePlusInput, imageStackInput, radius);
		filtermax.filter();
		imagePlusInput.setStack(imageStackInput);
	}//computeOuverture
  
	/**
	 * getter of indiceMax
	 * @return
	 */
	public int getIndiceMax (){ return _bestThreshold;}
	
	/**
	 * 
	 * @param objectVolume
	 * @return
	 */
	private boolean TestRatioObjectVolumeImageVolume(double objectVolume)
	{
		double ratio = (objectVolume/_imageVolume)*100;
		if (ratio >= 70) return false;
		else return true;
	}
	
	
	
}