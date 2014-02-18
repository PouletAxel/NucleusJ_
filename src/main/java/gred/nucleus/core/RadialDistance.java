package gred.nucleus.core;

import gred.nucleus.utils.Distance_Map;
import gred.nucleus.utils.Histogram;
import gred.nucleus.utils.VoxelRecord;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.plugin.Resizer;



/**
 * Class which determine the radial distance of chromocenters, using the binary nucleus
 * and the image of segmented chromcenters.
 *
 * @author poulet axel
 */
public class RadialDistance
{
  
  /**
   * Constructor
   *
   * @param imagePlusInput Image to be process
   */

	public RadialDistance()
	{
		
	}// fin constructeur

   
	/**
	 * Method which compute the distance map of binary nucleus
	 * Rescale the voxel to abtain cubic voxel
	 * @return distance map
	 */
	public ImagePlus computeDistanceMap (ImagePlus imagePlusBinary)
	{
		Resizer resizer = new Resizer();
		Calibration cal = imagePlusBinary.getCalibration();
		double dimX = cal.pixelWidth;
		double dimZ = cal.pixelDepth;
		double rescaleZFactor = dimZ/dimX;
		
		ImagePlus imagePlusRescale = resizer.zScale(imagePlusBinary,(int)(imagePlusBinary.getNSlices()*rescaleZFactor), 0);
		Distance_Map distanceMap = new Distance_Map();
		distanceMap.aplly(imagePlusRescale);
		return imagePlusRescale;
	} //computeDistanceMap

	/**
	 * Compute the shortest distance between the chromocenter periphery and the
	 * nuclear envelope
	 * @return Table of radial distance for each chromocenter
	 */
	public double[] computeBorderToBorderDistances (ImagePlus imagePlusBinary,ImagePlus imagePlusChromocenter)
	{
		Histogram histogram = new Histogram ();
		histogram.run(imagePlusChromocenter);
		double tabLabel[] = histogram.getLabels();
		Resizer resizer = new Resizer();
		Calibration cal = imagePlusBinary.getCalibration();
		double dimX = cal.pixelWidth;
		double dimZ = cal.pixelDepth;
		double rescaleZFactor = dimZ/dimX;
		imagePlusChromocenter = resizer.zScale(imagePlusChromocenter,(int)(imagePlusBinary.getNSlices()*rescaleZFactor), 0);
		ImageStack imageStackChromocenter = imagePlusChromocenter.getStack();
		ImagePlus imagePlusDistanceMap =  computeDistanceMap(imagePlusBinary);
		ImageStack imageStackDistanceMap = imagePlusDistanceMap.getStack();
		int i, j, k, l;
		double voxelValueMin, voxelValue;
	
		double distanceRadial [] = new double [tabLabel.length];
		for (l = 0; l < tabLabel.length; ++l)
		{
			voxelValueMin = Double.MAX_VALUE;
			for (k = 0; k < imagePlusChromocenter.getNSlices(); ++k)
				for (i = 0; i < imagePlusChromocenter.getWidth(); ++i)
					for (j = 0; j < imagePlusChromocenter.getHeight(); ++j)
					{
						voxelValue = imageStackDistanceMap.getVoxel(i, j, k);
						if (voxelValue < voxelValueMin && tabLabel[l] == imageStackChromocenter.getVoxel(i, j, k))
							voxelValueMin = voxelValue;
					}
			distanceRadial[l] = voxelValueMin*dimX;
		}
		return   distanceRadial;
	} //determinationRadialDistanceNuclearEnveloppeEdgeObject
	
	  /**
	   * Determine the radial distance of all chromocenter in the image of nucleus. We realise
	   * the distance map on the bianary nucleus. This method measure the radial distance
	   * between the barycenter of chromocenter and the nuclear envelope.
	   * @return Table of radial distance in µm (barycenter => nuclear envelope)
	   */

	  public double[] computeBarycenterToBorderDistances (ImagePlus imagePlusBinary,ImagePlus imagePlusChromocenter)
	  {
	    int i;
		Calibration cal = imagePlusBinary.getCalibration();
		double dimX = cal.pixelWidth;
	    ImagePlus imagePlusDistanceMap =  computeDistanceMap(imagePlusBinary);
	    ImageStack imageStackDistanceMap = imagePlusDistanceMap.getStack();
	    Measure3D measure3D = new Measure3D();
	    VoxelRecord tabVoxelRecord[] = measure3D.computeObjectBarycenter(imagePlusChromocenter);
	    double radialDistance[] = new double[tabVoxelRecord.length];
	    double distance;
	    for (i = 0; i < tabVoxelRecord.length; ++i)
	    {
	      VoxelRecord voxelRecord = tabVoxelRecord[i];
	      distance = imageStackDistanceMap.getVoxel((int)voxelRecord._i,(int)voxelRecord._j,(int)voxelRecord._k);
	      radialDistance[i] = dimX * distance;
	    }
	    return radialDistance;
	  } //determinationRadialDistanceForEachBarycenterObject
} //fin classe