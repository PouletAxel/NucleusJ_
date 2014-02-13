package gred.nucleus.parameters;


import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import gred.nucleus.utilitaires.VoxelRecord;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;

/**
 * This class compute several shape parameters, taking on parameter one image and the label of the interest object
 * 
 * @author Axel Poulet and Philippe Andrey
 */
public class ShapeParameters3D
{
   
	/** binary image */
	ImagePlus _imagePlusInput;
	/** height, width, depth of image in pixel*/
	int _width, _height, _depth;
	/** Voxel calibration */
	double _dimX, _dimY, _dimZ;
	/** imageStack of binary image */
	ImageStack _imageStackInput  = new ImageStack();
	/** object allow the computing of geometric parameters */
	GeometricParameters3D _gp3d;
	/** label object of interest */
	double _label;
  
	/**
	 * Constructor
	 * @param imagePlusInput
	 * @param label
	 */
	public ShapeParameters3D (ImagePlus imagePlusInput, double label)
	{
		_imagePlusInput = imagePlusInput;
		_label = label;
		Calibration cal=_imagePlusInput.getCalibration();
		_imageStackInput = _imagePlusInput.getImageStack();
		_width=_imagePlusInput.getWidth();
		_height=_imagePlusInput.getHeight();
		_depth=_imagePlusInput.getStackSize();
		_dimX = cal.pixelWidth;
		_dimY = cal.pixelHeight;
		_dimZ =cal.pixelDepth;
		_gp3d = new GeometricParameters3D(_imagePlusInput,_label); 
	}	
 
	/**
	 * Method which compute the sphericity :
	 * 36Pi*Volume^2/Surface^3 = 1 if perfect sphere
	 * @return sphericity
	 */
	public double computeSphericity()
	{
		double surface = _gp3d.computeSurfaceObject();
		double volume = _gp3d.computeVolumeObject ();
		return ((36 * Math.PI * (volume*volume)) / (surface*surface*surface));
	}
  
	/**
	 * Method which determines the barycenter of nucleus
	 * @param unit if true the coordinates of barycenter are in µm.
	 * @return the barycenter
	 */
  	
	public VoxelRecord computeBarycenter3D (boolean unit)
  	{
		VoxelRecord barycenter = new VoxelRecord ();
		int compteur = 0;
		double voxelValue;
		int i,j,k;
		for (k = 0; k < _depth; ++k)
			for (i = 0; i < _width; ++i )
				for (j = 0; j < _height;++j )
				{
					voxelValue = _imageStackInput.getVoxel(i,j,k);
					if (voxelValue == _label )
					{
						VoxelRecord voxelRecord = new VoxelRecord();
						voxelRecord.setLocation((double)i,(double)j,(double)k);
						barycenter.shiftCoordinates(voxelRecord);
						compteur++;
					}
				}
		barycenter.Multiplie(1 / (double)compteur);
		if (unit) barycenter.Multiplie(_dimX, _dimY,_dimZ);
		return barycenter;
  	}

	/**
	 * Method which compute the eigen value of the matrix (differences between the
	 * coordinates of all points and the barycenter
	 * Obtaining a symmetric matrix :
	 * xx xy xz
	 * xy yy yz
	 * xz yz zz
	 * Compute the eigen value with the pakage JAMA
	 * @return table with the eigen values
	 */
	public double [] ComputeEigenValue3D ()
	{
		VoxelRecord barycenter = computeBarycenter3D (true);
		double xx = 0, xy = 0, xz = 0, yy = 0, yz = 0, zz = 0;
		int compteur = 0;
		int i,j,k;
		double voxelValue;
		for (k = 0; k < _depth; ++k)
			for (i = 0; i < _width; ++i)
				for (j = 0; j < _height; ++j)
				{
					voxelValue = _imageStackInput.getVoxel(i,j,k);
					if (voxelValue > 0)
					{ 
						xx+= ((_dimX * (double) i)-barycenter.getI()) * ((_dimX * (double) i)-barycenter.getI());
						yy+= ((_dimY * (double) j)-barycenter.getJ()) * ((_dimY * (double) j)-barycenter.getJ());
						zz+= ((_dimZ * (double) k)-barycenter.getK()) * ((_dimZ * (double) k)-barycenter.getK());
						xy+= ((_dimX * (double) i)-barycenter.getI()) * ((_dimY * (double) j)-barycenter.getJ());
						xz+= ((_dimX * (double) i)-barycenter.getI()) * ((_dimZ * (double) k)-barycenter.getK());
						yz+= ((_dimY * (double) j)-barycenter.getJ()) * ((_dimZ * (double) k)-barycenter.getK());
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
	 * Compute elongation => shape parameter :
	 *
	 * @return elongation
	 */
	
	public double computeElongationObject ()
	{
		double eigen [] = ComputeEigenValue3D ();
		return Math.sqrt (eigen[2] / eigen[1]);
	}
  
	/**
	 * Compute elongation => shape parameter :
	 * @return flatness
	 */
  
	public double computeFlatnessObject ()
	{
		double eigen [] = ComputeEigenValue3D ();
		return Math.sqrt(eigen[1] / eigen[0]);
	}  
	
	/**
	 * getter of the object volume 
	 * @return
	 */
	
	public double getVolume () {return _gp3d.computeVolumeObject();}
}
