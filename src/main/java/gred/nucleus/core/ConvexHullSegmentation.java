package gred.nucleus.core;

import gred.nucleus.utils.ConvexeHullImageMaker;
import gred.nucleus.utils.FillingHoles;
import gred.nucleus.utils.VoxelRecord;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.Filters3D;

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
	   	//imagePlusXY.setTitle("xy");
	   	//imagePlusXY.show();
	   	IJ.log(imagePlusInput.getTitle()+" xz ");
	   	nuc.setAxes("xz");
	   	ImagePlus imagePlusXZ = nuc.giftWrapping(imagePlusInput);
	   	//imagePlusXZ.setTitle("xz");
	   	//imagePlusXZ.show();
	   	IJ.log(imagePlusInput.getTitle()+" yz ");
	   	nuc.setAxes("yz");
		ImagePlus imagePlusYZ = nuc.giftWrapping(imagePlusInput);
		//imagePlusYZ.setTitle("yz");
	   	//imagePlusYZ.show();
		ImagePlus imagePlusSegmented = union( imagePlusXY, imagePlusXZ, imagePlusYZ);
		//morphologicalCorrection(imagePlusSegmented);
		return imagePlusSegmented;
	}



	public static ImagePlus union(ImagePlus imagePlusXY, ImagePlus imagePlusYZ, ImagePlus imagePlusXZ){
		ImagePlus imagePlusResultat = imagePlusXY;
		imagePlusResultat.setTitle("Result Union Correct");
		
		for (int z = 0 ; z < imagePlusResultat.getNSlices() ; z++){
			for (int x=0 ; x<imagePlusResultat.getWidth() ; x++){
				for (int y=0 ; y<imagePlusResultat.getHeight();y++){
					imagePlusResultat.setZ(z);
					imagePlusYZ.setZ(x);
					imagePlusXZ.setZ(y);
					if (imagePlusResultat.getPixel(x, y)[0]==0){
						if (imagePlusYZ.getPixel(y, z)[0] != 0 || imagePlusXZ.getPixel(x, z)[0] != 0){
							imagePlusResultat.getPixel(x, y)[0]=255;
						}
					}
				}
			}
		}
		
		return imagePlusResultat;
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
	
	/**
	 * 	 method to realise sevral morphological correction ( filling holes and top hat)
	 * 
	 * @param imagePlusSegmented image to be correct
	 */
	private void morphologicalCorrection (ImagePlus imagePlusSegmented)
	{
		FillingHoles holesFilling = new FillingHoles();
		computeOpening(imagePlusSegmented);
		computeClosing(imagePlusSegmented);
		imagePlusSegmented = holesFilling.apply2D(imagePlusSegmented);
	}
	
	/**
	 * compute closing with the segmented image
	 * 
	 * @param imagePlusInput image segmented
	 */
	private void computeClosing (ImagePlus imagePlusInput)
	{
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MAX,1,1,(float)0.5);
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MIN,1,1,(float)0.5);
		imagePlusInput.setStack(imageStackInput);
	}

	/**
	 * compute opening with the segmented image 
	 * 
	 * @param imagePlusInput image segmented
	 */
	private void computeOpening (ImagePlus imagePlusInput)
	{
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MIN,1,1,(float)0.5);
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MAX,1,1,(float)0.5);
		imagePlusInput.setStack(imageStackInput);
	}

}
