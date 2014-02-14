package gred.nucleus.core;

import gred.nucleus.utils.Distance_Map;
import gred.nucleus.utils.Histogram;
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
	ImagePlus _imagePlusSegmentedChromocenter;
	/** Voxel calibration (µm)*/
	@SuppressWarnings("unused")
	private double _dimX, _dimY, _dimZ;
	/** Height, width, depth of image in pixel*/
	private int _height, _width,_depth;
	/** Scale factor of  _dimZ to get cubic voxels*/
	private double _rescaleZFactor;
	/** ImageStack of _imagePlusSegmentationChromocenter*/
	ImageStack _imageStackSegmentationChromocenter;
	/** Binary image of the nucleus*/
	ImagePlus _imagePlusBinaryNucleus;
	/** title of _imageStackSegmentationChromocenter*/
	final String _title;
	/** Parameters of chromocenters*/
	public ParametersOfSeveralObject _poso;
	/** */
	Histogram _hist;
  
  /**
   * Constructor
   *
   * @param imagePlusInput Image to be process
   */

	public RadialDistance(ImagePlus imagePlusInput, ImagePlus imagePlusBianry)
	{
		_imagePlusSegmentedChromocenter = imagePlusInput;
		_poso = new ParametersOfSeveralObject(imagePlusInput);
		_hist = new Histogram(imagePlusInput);
		_title = _imagePlusSegmentedChromocenter.getTitle();
		Calibration cal = _imagePlusSegmentedChromocenter.getCalibration();
		_dimX = cal.pixelWidth;
		_dimY = cal.pixelHeight;
		_dimZ = cal.pixelDepth;
		_imageStackSegmentationChromocenter = _imagePlusSegmentedChromocenter.getStack();
		_height = _imagePlusSegmentedChromocenter.getHeight();
		_width = _imagePlusSegmentedChromocenter.getWidth();
		_depth = _imagePlusSegmentedChromocenter.getNSlices();
		_rescaleZFactor = _dimZ/_dimX;
		_imagePlusBinaryNucleus = imagePlusBianry;
	}// fin constructeur

   
	/**
	 * Method which compute the distance map of binary nucleus
	 * Rescale the voxel to abtain cubic voxel
	 * @return distance map
	 */
	public ImagePlus computeDistanceMap ()
	{
		Resizer resizer = new Resizer();
		ImagePlus imagePlusRescale = resizer.zScale(_imagePlusBinaryNucleus,(int)(_depth*_rescaleZFactor), 0);
		Distance_Map distanceMap = new Distance_Map();
		distanceMap.aplly(imagePlusRescale);
		return imagePlusRescale;
	} //computeDistanceMap

	/**
	 * Compute the shortest distance between the chromocenter periphery and the
	 * nuclear envelope
	 * @return Table of radial distance for each chromocenter
	 */
	public double[] computeBorderToBorderDistances ()
	{
		Resizer resizer = new Resizer();
		ImagePlus imagePlusTmp = _imagePlusSegmentedChromocenter.duplicate();
		imagePlusTmp = resizer.zScale(imagePlusTmp,(int)(_depth*_rescaleZFactor), 0);
		ImageStack imageStackTmp = imagePlusTmp.getStack();
		ImagePlus imagePlusDistanceMap =  computeDistanceMap();
		ImageStack imageStackDistanceMap = imagePlusDistanceMap.getStack();
		int i, j, k, l;
		double voxelValueMin, voxelValue;
		double tabLabel[] = _hist.getLabel();
		double distanceRadial [] = new double [tabLabel.length];
		for (l = 0; l < tabLabel.length; ++l)
		{
			voxelValueMin = Double.MAX_VALUE;
			for (k = 0; k < imagePlusTmp.getNSlices(); ++k)
				for (i = 0; i < _width; ++i)
					for (j = 0; j < _height; ++j)
					{
						voxelValue = imageStackDistanceMap.getVoxel(i, j, k);
						if (voxelValue < voxelValueMin && tabLabel[l] == imageStackTmp.getVoxel(i, j, k))
							voxelValueMin = voxelValue;
					}
			distanceRadial[l] = voxelValueMin*_dimX;
		}
		return   distanceRadial;
	} //determinationRadialDistanceNuclearEnveloppeEdgeObject
} //fin classe