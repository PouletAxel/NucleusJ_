package gred.nucleus.core;

import gred.nucleus.utils.ConvexeHullImageMaker;
import gred.nucleus.utils.VoxelRecord;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ConvexHullSegmentation
{
	
	/**
	 * 
	 * @param imagePlusInput
	 */
	public ImagePlus run(ImagePlus imagePlusInput)
	{
		IJ.log(imagePlusInput.getTitle()+" xy ");
		ConvexeHullImageMaker nuc = new ConvexeHullImageMaker();
		nuc.setAxes("xy");
	   	ImagePlus imagePlusXY = nuc.giftWrapping(imagePlusInput);
	   	imagePlusXY.show();
	   /*	IJ.log(imagePlusInput.getTitle()+" xz ");
	   	nuc.setAxes("xz");
	   	ImagePlus imagePlusXZ = nuc.giftWrapping(imagePlusInput);
	   	IJ.log(imagePlusInput.getTitle()+" yz ");
	   	nuc.setAxes("yz");
		ImagePlus imagePlusYZ = nuc.giftWrapping(imagePlusInput);*/
		
		return imagePlusInput;//imageMakingUnion(imagePlusInput, imagePlusXY, imagePlusXZ, imagePlusYZ);
	}



	/**
	 * 
	 * @param imagePlusInput
	 * @param convexHull
	 * @return
	 */
	
	public ImagePlus imageMakingUnion (ImagePlus imagePlusInput,ImagePlus  imagePlusXY,ImagePlus imagePlusXZ,ImagePlus imagePlusYZ)
	{
		ImagePlus imagePlusOutput = imagePlusInput.duplicate();
		ImageStack imageStackXY= imagePlusXY.getStack();
		ImageStack imageStackXZ= imagePlusXZ.getStack();
		ImageStack imageStackYZ= imagePlusYZ.getStack();
		ImageStack imageStackOutput = imagePlusOutput.getStack();
		
		for (int k = 0; k < imagePlusXY.getNSlices();++k)
			for (int i = 0; i < imagePlusXY.getWidth(); ++i)
				for (int j = 0; j < imagePlusXY.getHeight();++j)
					if (imageStackXY.getVoxel(i, j, k) != 0 || imageStackYZ.getVoxel(j, k, i) != 0 || imageStackXZ.getVoxel(i, k, j) != 0)	
						if(imageStackOutput.getVoxel(i, j, k) == 0)
							imageStackOutput.setVoxel(i, j, k, 255);
		return imagePlusOutput;
	}

}
