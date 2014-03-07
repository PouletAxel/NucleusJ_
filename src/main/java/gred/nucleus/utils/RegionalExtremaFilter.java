package gred.nucleus.utils;

import ij.*;
import ij.process.*;
import ij.plugin.filter.*;

import java.util.ArrayList;


/**
 * Class to detect the region minima from a 3D image gradient
 * with or without a binary mask
 * 
 * @author Philippe Andrey and Poulet Axel
 */

public class RegionalExtremaFilter implements PlugInFilter
{
	/** image to process */
	private ImagePlus _imagePlusInput;
	/** image of the regional Extrema*/
	/** table to stock the values of the local minima their coordinates x, y, z*/
	private double _localMinValues[][][] = null;
	/** table to stock the binary mask if is necessary */
	private double _tabMask[][][] = null;

	/**
	 *
	 * @param arg
	 * @param imagePlusInput
	 * @return
	 */

	public int setup(String arg, ImagePlus imagePlusInput)
	{
		this._imagePlusInput = imagePlusInput;
		return 0;
	}

	/**
	 * To run the plugin with imageJ
	 * 
	 * @param imageProcessor
	 */
	public void run(ImageProcessor imageProcessor){   applyWithMask( _imagePlusInput ); }

	/**
	 * Method used to scan the deconvolved image, and the image having undergone the
	 * filter min in the binary mask of nucleus. For each voxel, the user can determine if
	 * the voxel value is a minima or belong to a minima region.
	 * All the minima regions retain a specific voxel value while the others => value = 0
	 * 
	 * @param imagePlus Image to be processed
	 * @return minima regions
	 */
	public ImagePlus applyWithMask(ImagePlus imagePlusInput)
	{
		this._imagePlusInput = imagePlusInput;
		double width = _imagePlusInput.getWidth();
		double height = _imagePlusInput.getHeight();
		double depth = _imagePlusInput.getStackSize();
		int i, j, k, kcurrent, icurrent, jcurrent, ii, jj, kk;
		VoxelRecord voxelRecord = new VoxelRecord();
		ArrayList<VoxelRecord> arrayListVoxel = new ArrayList<VoxelRecord>();
		computeImageMoreOne();
		ImagePlus imageOutput = _imagePlusInput.duplicate();
		ImageStack imageStackOutput = imageOutput.getStack();
		filterMin3DWithMask();
		for (k = 0; k < depth; ++k)
			for (i = 0; i < width; ++i)
				for (j = 0; j < height; ++j)
				{
					double currentValue = imageStackOutput.getVoxel(i,j,k);
					double currentValueMin = _localMinValues[i][j][k];
					if ( currentValue > 0 &&  currentValue != currentValueMin && _tabMask[i][j][k]>0)
					{
						imageStackOutput.setVoxel(i, j, k, 0);
						voxelRecord.setLocation(i, j, k);
						arrayListVoxel.add(voxelRecord);
						while ( arrayListVoxel.size() > 0 )
						{
							voxelRecord = (VoxelRecord) arrayListVoxel.remove(0);
							icurrent = (int)voxelRecord.getI();
							jcurrent = (int)voxelRecord.getJ();
							kcurrent = (int)voxelRecord.getK();
							for (kk = kcurrent - 1; kk <= kcurrent+1; ++kk)
								for (ii = icurrent - 1; ii <= icurrent+1; ++ii)
									for (jj = jcurrent - 1; jj <= jcurrent+1; ++jj)
										if ( kk >= 0 && kk < depth && ii >= 0 && ii < width && jj >= 0 && jj < height  && _tabMask[ii][jj][kk]>0)
											if ( imageStackOutput.getVoxel(ii,jj,kk) == currentValue )
											{
												imageStackOutput.setVoxel(ii, jj, kk, 0);
												voxelRecord.setLocation(ii, jj, kk);
												arrayListVoxel.add( voxelRecord );
											}
						}
					}
				}
		imageOutput.setTitle("minima_"+imagePlusInput.getTitle());
		return imageOutput;
	}
  
    /**
     * 
     * Method used to scan the deconvolved image. For each voxel, the user can determine if
     * the voxel value is a minima or belong to a minima region.
     * All the minima regions retain a specific voxel value while the others => value = 0 
     * @param imagePlusInput
     * @return 
     */
  
	public ImagePlus apply(ImagePlus imagePlusInput)
	{
		this._imagePlusInput = imagePlusInput;
		double width = _imagePlusInput.getWidth();
		double height = _imagePlusInput.getHeight();
		double depth = _imagePlusInput.getStackSize();
		int i, j, k, kcurrent, icurrent, jcurrent, ii, jj, kk;
		VoxelRecord voxelRecord = new VoxelRecord();
		ArrayList<VoxelRecord> arraylListVoxel = new ArrayList<VoxelRecord>();
		computeImageMoreOne();
		ImagePlus imageOutput = _imagePlusInput.duplicate();
		ImageStack imageStackOutput = imageOutput.getStack();
		filterMin3D();
		for (k = 0; k < depth; ++k)
			for (i = 0; i < width; ++i)
				for (j = 0; j < height; ++j)
				{
					double currentValue = imageStackOutput.getVoxel(i,j,k);
					double currentValueMin = _localMinValues[i][j][k];
					if ( currentValue > 0 &&  currentValue != currentValueMin)
					{
						imageStackOutput.setVoxel(i, j, k, 0);
						voxelRecord.setLocation(i, j, k);
						arraylListVoxel.add(voxelRecord);
						while ( arraylListVoxel.size() > 0 )
						{
							voxelRecord = (VoxelRecord) arraylListVoxel.remove(0);
							icurrent = (int)voxelRecord.getI();
							jcurrent = (int)voxelRecord.getJ();
							kcurrent = (int)voxelRecord.getK();
							for (kk = kcurrent - 1; kk <= kcurrent+1; ++kk)
								for (ii = icurrent - 1; ii <= icurrent+1; ++ii)
									for (jj = jcurrent - 1; jj <= jcurrent+1; ++jj)
										if ( kk >= 0 && kk < depth && ii >= 0 && ii < width && jj >= 0 && jj < height)
										{
											if ( imageStackOutput.getVoxel(ii,jj,kk) == currentValue )
											{
												imageStackOutput.setVoxel(ii, jj, kk, 0);
												voxelRecord.setLocation(ii, jj, kk);
												arraylListVoxel.add( voxelRecord );
											}
										}
						}
					}
				}
		imageOutput.setTitle("minima_"+imagePlusInput.getTitle());
		return imageOutput;
	}

  
	/**
	 * Adds one at all the voxel values of a given image
	 * 
	 */
  
	public void computeImageMoreOne ()
	{
		int i, j, k;
		double width = _imagePlusInput.getWidth();
		double height = _imagePlusInput.getHeight();
		double depth = _imagePlusInput.getStackSize();
		ImageStack imageStackInput = _imagePlusInput.getStack();
		for (k = 0; k < depth; ++k)
			for (i = 0; i < width; ++i)
				for (j = 0; j < height; ++j)
					imageStackInput.setVoxel(i, j, k, imageStackInput.getVoxel(i, j, k)+1);
	}
  
	/**
	 * Filter minimum in 3D with a neighboring 3
	 */
  
	void filterMin3DWithMask()
	{
		int size1 = _imagePlusInput.getWidth();
		int size2 = _imagePlusInput.getHeight();
		int size3 = _imagePlusInput.getStackSize();
		ImageStack imageStackInput= _imagePlusInput.getStack();
		int i,j,k,ii,jj,kk;
		double minValue;
		_localMinValues = new double[size1][size2][size3];
		for (k=0; k<size3; k++)
			for (i=0; i<size1; i++)
				for (j=0; j<size2; j++)
				{
					minValue = imageStackInput.getVoxel(i, j, k);
					for (ii = i-1; ii <= i+1; ++ii) 
						if ( ii >= 0 && ii < size1 )
							for (jj = j-1; jj <= j+1; ++jj)
								if ( jj >= 0 && jj < size2 )
									for (kk = k-1; kk <= k+1; ++kk) if ( kk >= 0 && kk < size3 )
									{
										if (imageStackInput.getVoxel(ii, jj, kk)<minValue  && _tabMask[i][j][k]>0 && _tabMask[ii][jj][kk] > 0)
											minValue = imageStackInput.getVoxel(ii, jj, kk);
									}
					_localMinValues[i][j][k] = minValue;
				}
  }
	
    /**
     * Filter minimum in 3D with a neighboring 3
     */
	void filterMin3D()
	{
		int size1 = _imagePlusInput.getWidth();
		int size2 = _imagePlusInput.getHeight();
		int size3 = _imagePlusInput.getStackSize();
		ImageStack imageStack= _imagePlusInput.getStack();
		int i,j,k,ii,jj,kk;
		double minValue;
		_localMinValues = new double[size1][size2][size3];
		for (k=0; k<size3; k++)
			for (i=0; i<size1; i++)
				for (j=0; j<size2; j++)
				{
					minValue = imageStack.getVoxel(i, j, k);
					for (ii = i-1; ii <= i+1; ++ii)
						if ( ii >= 0 && ii < size1 )
							for (jj = j-1; jj <= j+1; ++jj)
								if ( jj >= 0 && jj < size2 )
									for (kk = k-1; kk <= k+1; ++kk)
										if ( kk >= 0 && kk < size3 )
										{
											if (imageStack.getVoxel(ii, jj, kk) < minValue)
												minValue = imageStack.getVoxel(ii, jj, kk);
										}
					_localMinValues[i][j][k] = minValue;
				}
  }

	/**
	 * Initialise a matrix of a binary mask to search the minima regions in the mask
	 * @param tab binary mask
	 */
  
	public void setMask (double tab[][][]) { _tabMask=tab; } 

	/**
	 * Initialise a matrix of a binary mask to search the minima regions in the mask
	 * @param imagePlusEntree Binary image 
	 */
	public void setMask (ImagePlus imagePlusEntree)
	{
		ImageStack imagePluslabel = imagePlusEntree.getStack();
		final int size1 = imagePluslabel.getWidth();
		final int size2 = imagePluslabel.getHeight();
		final int size3 = imagePluslabel.getSize();
		_tabMask = new double[size1][size2][size3];
		int i, j, k;
		for (i = 0; i < size1; ++i)
			for (j = 0; j < size2; ++j)
				for (k = 0; k < size3; ++k)
					_tabMask[i][j][k] = imagePluslabel.getVoxel(i, j, k);
	}
}// class