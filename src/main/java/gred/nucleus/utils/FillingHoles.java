package gred.nucleus.utils;

import ij.*;
import ij.process.*;
import inra.ijpb.binary.ConnectedComponents;

/**
 * Class HolesFilling
 *
 * @author Philippe Andrey et Poulet Axel
 */

public class FillingHoles{
	public FillingHoles(){}
 
	 
 	/**
	 * Method in two dimensions which process ecah plan z independent,
	 *
	 */
	@SuppressWarnings("deprecation")
	public ImagePlus apply2D (ImagePlus imagePlusInput){
		ImagePlus imagePlusCorrected = imagePlusInput;
		ImageStack imageStackCorrected = imagePlusCorrected.getStack();
		double voxelValue;
		ImageStack imageStackOutput = new ImageStack(imageStackCorrected.getWidth(),imageStackCorrected.getHeight());
		for (int k = 1; k <= imageStackCorrected.getSize(); ++k){
			ImageProcessor imageProcessorLabellised = imageStackCorrected.getProcessor(k);
			for (int i = 0; i < imageStackCorrected.getWidth(); ++i)
				for (int j = 0; j < imageStackCorrected.getHeight(); ++j){
					voxelValue = imageProcessorLabellised.getPixel(i, j);
					if(voxelValue > 0)
						imageProcessorLabellised.putPixelValue(i, j, 0);
					else
						imageProcessorLabellised.putPixelValue(i, j, 255);
				}
			imageProcessorLabellised = ConnectedComponents.computeLabels(imageProcessorLabellised, 26, 32);
			int label;
			boolean [] tEdgeFlags = new boolean [(int)imageProcessorLabellised.getMax()+1];
			for(int a = 0; a < tEdgeFlags.length; ++a)  
				tEdgeFlags[a] = false;
			// Analyse des plans extremes selon la dim x
			for(int j = 0; j < imageStackCorrected.getHeight(); ++j){
				label = (int) imageProcessorLabellised.getf(0, j);
				tEdgeFlags[label] = true;
				label = (int) imageProcessorLabellised.getf(imageStackCorrected.getWidth()-1, j);
				tEdgeFlags[label] = true;
			}
			// Analyse des plans extremes selon la dim y
			for(int i = 0; i < imageStackCorrected.getWidth(); ++i){
				label = (int)imageProcessorLabellised.getf(i, 0);
				tEdgeFlags[label] = true;
				label = (int) imageProcessorLabellised.getf(i, imageStackCorrected.getHeight()-1);
				tEdgeFlags[label] = true;
			}
      
			for(int i = 0; i < imageStackCorrected.getWidth(); ++i)
				for(int j = 0; j < imageStackCorrected.getHeight(); ++j){
					label = (int)  imageProcessorLabellised.getf(i, j);
					if (label == 0 || tEdgeFlags[label] == false)
						imageProcessorLabellised.putPixelValue(i,j,255);
					else
						imageProcessorLabellised.putPixelValue(i, j, 0);
				}
			imageStackOutput.addSlice(imageProcessorLabellised);
		}
		imagePlusCorrected.setStack(imageStackOutput);
		return imagePlusCorrected;
	}
}