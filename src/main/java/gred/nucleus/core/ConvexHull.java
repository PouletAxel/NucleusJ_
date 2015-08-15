package gred.nucleus.core;

import gred.nucleus.utils.ConvexeHullDetection;
import gred.nucleus.utils.VoxelRecord;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;

import java.awt.Polygon;
import java.util.ArrayList;

public class ConvexHull
{
	
	/**
	 * 
	 * @param imagePlusInput
	 */
	public void run(ImagePlus imagePlusInput)
	{
		
	    ConvexeHullDetection nuc = new ConvexeHullDetection();
	    nuc.setAxes("xy");
		nuc.giftWrapping(imagePlusInput);
		nuc.setAxes("xz");
		nuc.giftWrapping(imagePlusInput);
		nuc.setAxes("yz");
		nuc.giftWrapping(imagePlusInput);
		IJ.log("finale : "+nuc.getConvexHull().size());
	}



	/**
	 * 
	 * @param imagePlusInput
	 * @param convexHull
	 * @return
	 */
	
	public ImagePlus imageMakingUnion (ImagePlus imagePlusInput, ArrayList<VoxelRecord> convexHull)
	{
		ImagePlus output;
		for (int k = 0;  k < imagePlusInput.getNSlices(); ++k)
		{
			ArrayList<Integer> xCoordinates = new ArrayList<Integer>();
			ArrayList<Integer> yCoordinates = new ArrayList<Integer>();
			for(int i = 0; i < convexHull.size(); ++i)
			{
				if (convexHull.get(i)._k == (double) k)
				{
					xCoordinates.add((int)convexHull.get(i)._i);
					yCoordinates.add((int)convexHull.get(i)._i);
					
				}
				
			}
		}
		
		
		
		drawPolygon(java.awt.Polygon p)
		
		Polygon p = new Polygon(x, y,x.length );
		
		
		return output;
	}

}
