package gred.nucleus.core;
import gred.nucleus.utils.Distance_Map;
import gred.nucleus.utils.Histogram;
import gred.nucleus.utils.VoxelRecord;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.plugin.Resizer;



/**
 * this class allows the determination of the radial distance of chromocenters, using the binary nucleus
 * and the image of segmented chromocenters.
 * 
 * @author Poulet Axel
 *
 */
public class RadialDistance
{
  	public RadialDistance(){	}

   
	/**
	 * Method which compute the distance map of binary nucleus
	 * Rescale the voxel to obtain cubic voxel
  	 * 
  	 * @param imagePlusSegmented
  	 * @return
  	 */
	public ImagePlus computeDistanceMap (ImagePlus imagePlusSegmented)
	{
		Resizer resizer = new Resizer();
		Calibration calibration = imagePlusSegmented.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double zCalibration = calibration.pixelDepth;
		double rescaleZFactor = zCalibration/xCalibration;
		ImagePlus imagePlusRescale = resizer.zScale(imagePlusSegmented,(int)(imagePlusSegmented.getNSlices()*rescaleZFactor), 0);
		Distance_Map distanceMap = new Distance_Map();
		distanceMap.aplly(imagePlusRescale);
		return imagePlusRescale;
	}

	/**
	 * Compute the shortest distance between the chromocenter periphery and the
	 * nuclear envelope
	 * @return Table of radial distance for each chromocenter
	 * 
	 * @param imagePlusSegmented
	 * @param imagePlusChromocenter
	 * @return
	 */
	public double[] computeBorderToBorderDistances (ImagePlus imagePlusSegmented,ImagePlus imagePlusChromocenter)
	{
		Histogram histogram = new Histogram ();
		histogram.run(imagePlusChromocenter);
		double [] tLabel = histogram.getLabels();
		Resizer resizer = new Resizer();
		Calibration calibration = imagePlusSegmented.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double zCalibration = calibration.pixelDepth;
		double rescaleZFactor = zCalibration/xCalibration;
		imagePlusChromocenter = resizer.zScale(imagePlusChromocenter,(int)(imagePlusSegmented.getNSlices()*rescaleZFactor), 0);
		ImageStack imageStackChromocenter = imagePlusChromocenter.getStack();
		ImagePlus imagePlusDistanceMap =  computeDistanceMap(imagePlusSegmented);
		ImageStack imageStackDistanceMap = imagePlusDistanceMap.getStack();
		int i, j, k, l;
		double voxelValueMin, voxelValue;
		double [] tDistanceRadial = new double [tLabel.length];
		for (l = 0; l < tLabel.length; ++l)
		{
			voxelValueMin = Double.MAX_VALUE;
			for (k = 0; k < imagePlusChromocenter.getNSlices(); ++k)
				for (i = 0; i < imagePlusChromocenter.getWidth(); ++i)
					for (j = 0; j < imagePlusChromocenter.getHeight(); ++j)
					{
						voxelValue = imageStackDistanceMap.getVoxel(i, j, k);
						if (voxelValue < voxelValueMin && tLabel[l] == imageStackChromocenter.getVoxel(i, j, k))
							voxelValueMin = voxelValue;
					}
			tDistanceRadial[l] = voxelValueMin*xCalibration;
		}
		return   tDistanceRadial;
	}
	
	/**
	 * Determine the radial distance of all chromocenter in the image of nucleus. We realise
	 * the distance map on the bianary nucleus. This method measure the radial distance
	 * between the barycenter of chromocenter and the nuclear envelope. 
	 * 
	 * @param imagePlusSegmented
	 * @param imagePlusChromocenter
	 * @return
	 */
	
	public double[] computeBarycenterToBorderDistances (ImagePlus imagePlusSegmented,ImagePlus imagePlusChromocenter)
	{
		int i;
		Resizer resizer = new Resizer();
		Calibration calibration = imagePlusSegmented.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double zCalibration = calibration.pixelDepth;
		double rescaleZFactor = zCalibration/xCalibration;
		ImagePlus imagePlusChromocenterRescale = resizer.zScale(imagePlusChromocenter,(int)(imagePlusChromocenter.getNSlices()*rescaleZFactor), 0);
	    ImagePlus imagePlusDistanceMap =  computeDistanceMap(imagePlusSegmented);
	    ImageStack imageStackDistanceMap = imagePlusDistanceMap.getStack();
	    Measure3D measure3D = new Measure3D();
	    VoxelRecord [] tVoxelRecord = measure3D.computeObjectBarycenter(imagePlusChromocenterRescale,false);
	    double [] tRadialDistance = new double[tVoxelRecord.length];
	    double distance =-50;
	    for (i = 0; i < tVoxelRecord.length; ++i)
	    {
	    	VoxelRecord voxelRecord = tVoxelRecord[i];
	    	distance = imageStackDistanceMap.getVoxel((int)voxelRecord._i,(int)voxelRecord._j,(int)voxelRecord._k);
	    	tRadialDistance[i] = xCalibration * distance;
	    }
	    return tRadialDistance;
	}
}	 