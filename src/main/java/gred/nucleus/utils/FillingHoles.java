package gred.nucleus.utils;

import ij.*;
import ij.process.*;
import inra.ijpb.binary.ConnectedComponents;

/**
 * Class HolesFilling
 *
 * @author Philippe Andrey et Poulet Axel
 */

public class FillingHoles 
{
	public FillingHoles() {}
 
	/**
	 * Method which process the image in the three dimensions (x, y, z) in the same time.
	 */
  
	public ImagePlus apply3D (ImagePlus imagePlusInput)
	{
		// image inversion (0 became 255 and 255 became 0)
		ImagePlus imagePlusCorrected = imagePlusInput;
		ImageStack imageStackCorrected = imagePlusCorrected.getStack();
		int i, j, k;
		for (k = 0; k < imageStackCorrected.getSize(); ++k)
			for (i = 0; i < imageStackCorrected.getWidth(); ++i)
				for (j = 0; j < imageStackCorrected.getHeight(); ++j)
				{
					double voxelCurrent = imageStackCorrected.getVoxel(i, j, k);
					if (voxelCurrent > 0) imageStackCorrected.setVoxel(i, j, k, 0);
					else imageStackCorrected.setVoxel(i, j, k, 255);
        		}
		imagePlusCorrected = ConnectedComponents.computeLabels(imagePlusCorrected, 26, 32);
		int label;
		boolean[] tEdgeFlags  = new boolean [(int)imagePlusCorrected.getStatistics().max+1];
		imageStackCorrected = imagePlusCorrected.getImageStack();
		for (int a = 0; a < tEdgeFlags.length;++a)  tEdgeFlags[a] = false;
		// Analyse of plans extreme in the dim x
		for (k = 0; k < imageStackCorrected.getSize(); ++k)
			for (j = 0; j < imageStackCorrected.getHeight(); ++j)
			{
				label = (int) imageStackCorrected.getVoxel(0, j, k);
				tEdgeFlags[label] = true;
				label = (int) imageStackCorrected.getVoxel(imageStackCorrected.getWidth()-1, j, k);
				tEdgeFlags[label] = true;
			}

		// Analyse of plans extreme in the dim y
		for (k = 0; k < imageStackCorrected.getSize(); ++k)
			for (i = 0; i < imageStackCorrected.getWidth(); ++i)
			{
				label = (int) imageStackCorrected.getVoxel(i, 0, k);
				tEdgeFlags[label] = true;
				label = (int) imageStackCorrected.getVoxel(i, imageStackCorrected.getHeight()-1, k);
				tEdgeFlags[label] = true;
			}

		// Analyse of plans extreme in the dim z
		for (i = 0; i < imageStackCorrected.getSize(); ++i)
			for (j = 0; j < imageStackCorrected.getWidth(); ++j)
			{
				label = (int) imageStackCorrected.getVoxel(i, j, 0);
				tEdgeFlags[label] = true;
				label = (int) imageStackCorrected.getVoxel(i, j, imageStackCorrected.getSize()-1);
				tEdgeFlags[label] = true;
			}
    
		//Creation of the image results
		for (k = 0; k < imageStackCorrected.getSize(); ++k)
			for (i = 0; i < imageStackCorrected.getWidth(); ++i)
				for (j = 0; j < imageStackCorrected.getHeight(); ++j)
				{
					label = (int) imageStackCorrected.getVoxel(i, j, k);
					if (label == 0 || tEdgeFlags[label] == false)	imageStackCorrected.setVoxel(i, j, k, 255);
					else   imageStackCorrected.setVoxel(i, j, k, 0);
				}
    
		imagePlusCorrected.setStack(imageStackCorrected);
		return imagePlusCorrected;
	} 
 
 
	/**
	 * Method in two dimensions which process ecah plan z independent,
	 *
	 */
	public ImagePlus apply2D (ImagePlus imagePlusInput)
	{
		ImagePlus imagePlusCorrected = imagePlusInput;
		ImageStack imageStackCorrected = imagePlusCorrected.getStack();
		double voxelValue;
		int i, j, k;
		ImageStack imageStackOutput = new ImageStack(imageStackCorrected.getWidth(),imageStackCorrected.getHeight());
		for (k = 1; k <= imageStackCorrected.getSize(); ++k)
		{
			ImageProcessor imageProcessorLabellised = imageStackCorrected.getProcessor(k);
			for (i = 0; i < imageStackCorrected.getWidth(); ++i)
				for (j = 0; j < imageStackCorrected.getHeight(); ++j)
				{
					voxelValue = imageProcessorLabellised.getPixel(i, j);
					if (voxelValue > 0)      imageProcessorLabellised.putPixelValue(i, j, 0);
					else        imageProcessorLabellised.putPixelValue(i, j, 255);
				}
			imageProcessorLabellised = ConnectedComponents.computeLabels(imageProcessorLabellised, 26, 32);
			int label;
			boolean [] tEdgeFlags = new boolean [(int)imageProcessorLabellised.getMax()+1];
			for (int a = 0; a < tEdgeFlags.length; ++a)  tEdgeFlags[a] = false;
			// Analyse des plans extremes selon la dim x
			for (j = 0; j < imageStackCorrected.getHeight(); ++j)
			{
				label = (int) imageProcessorLabellised.getf(0, j);
				tEdgeFlags[label] = true;
				label = (int) imageProcessorLabellised.getf(imageStackCorrected.getWidth()-1, j);
				tEdgeFlags[label] = true;
			}
			// Analyse des plans extremes selon la dim y
			for (i = 0; i < imageStackCorrected.getWidth(); ++i)
			{
				label = (int)imageProcessorLabellised.getf(i, 0);
				tEdgeFlags[label] = true;
				label = (int) imageProcessorLabellised.getf(i, imageStackCorrected.getHeight()-1);
				tEdgeFlags[label] = true;
			}
      
			for (i = 0; i < imageStackCorrected.getWidth(); ++i)
				for (j = 0; j < imageStackCorrected.getHeight(); ++j)
				{
					label = (int)  imageProcessorLabellised.getf(i, j);
					if (label == 0 || tEdgeFlags[label] == false) imageProcessorLabellised.putPixelValue(i,j,255);
					else    imageProcessorLabellised.putPixelValue(i, j, 0);
				}
			imageStackOutput.addSlice(imageProcessorLabellised);
		}
		imagePlusCorrected.setStack(imageStackOutput);
		return imagePlusCorrected;
	}
}