package gred.nucleus.core;

import gred.nucleus.utils.ConvexeHullDetection;
import gred.nucleus.utils.VoxelRecord;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ConvexHull
{
	
	/**
	 * 
	 * @param imagePlusInput
	 */
	public ImagePlus run(ImagePlus imagePlusInput)
	{
		
	    ConvexeHullDetection nuc = new ConvexeHullDetection();
	    nuc.setAxes("xy");
	    ArrayList<VoxelRecord> convexHullXY = nuc.giftWrapping(imagePlusInput);
		nuc.setAxes("xz");
		ArrayList<VoxelRecord> convexHullXZ = nuc.giftWrapping(imagePlusInput);
		nuc.setAxes("yz");
		ArrayList<VoxelRecord> convexHullYZ = nuc.giftWrapping(imagePlusInput);
		ImagePlus imagePlusOutput = imageMakingUnion(imagePlusInput, convexHullXY, convexHullXZ, convexHullYZ);
		return imagePlusOutput;
	}



	/**
	 * 
	 * @param imagePlusInput
	 * @param convexHull
	 * @return
	 */
	
	public ImagePlus imageMakingUnion (ImagePlus imagePlusInput, ArrayList<VoxelRecord> convexHullXY,ArrayList<VoxelRecord> convexHullYZ ,ArrayList<VoxelRecord> convexHullXZ )
	{
		ImagePlus imagePlusCorrected = imagePlusInput.duplicate();
				

		for (int k = 11;  k < 20; ++k)
		{
			IJ.log("quel plan "+k);
			ArrayList<Integer> xCoordinates = new ArrayList<Integer>();
			ArrayList<Integer> yCoordinates = new ArrayList<Integer>();
			/*for(int i = 0; i < convexHullXY.size(); ++i)
			{
				if (convexHullXY.get(i)._k == (double) k)
				{
					xCoordinates.add((int)convexHullXY.get(i)._i);
					yCoordinates.add((int)convexHullXY.get(i)._j);
					convexHullXY.remove(i);

				}
				
			}
			for(int i = 0; i < convexHullXZ.size(); ++i)
			{
				if (convexHullXZ.get(i)._k == (double) k)
				{
					xCoordinates.add((int)convexHullXZ.get(i)._i);
					yCoordinates.add((int)convexHullXZ.get(i)._j);
					convexHullXZ.remove(i);
				}
				
			}*/
			
			for(int i = 0; i < convexHullYZ.size(); ++i)
			{
				if (convexHullYZ.get(i)._k == (double) k)
				{
					xCoordinates.add((int)convexHullYZ.get(i)._i);
					yCoordinates.add((int)convexHullYZ.get(i)._j);
					convexHullYZ.remove(i);
				}
				
			}
			if (xCoordinates.size()>0)
			{
				BufferedImage bufferedImage = new BufferedImage(imagePlusInput.getWidth(), imagePlusInput.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

				IJ.log("Je suis "+k+ " et je passes par la");
				xCoordinates.add(xCoordinates.get(0));
				yCoordinates.add(yCoordinates.get(0));
				int []tablex = convertObjectInInt(xCoordinates.toArray());
				int []tabley = convertObjectInInt(yCoordinates.toArray());
				Polygon p = new Polygon(tablex, tabley,tablex.length );
				Graphics2D g2d = bufferedImage.createGraphics();
				g2d.drawPolygon(p);
				g2d.fillPolygon(p);
				g2d.setColor(Color.WHITE);
				g2d.dispose();
				ImagePlus ip = new ImagePlus();
				ip.setImage(bufferedImage);
				ip.setTitle("plop"+k);
				ip.show();
			}
			else
			{
				BufferedImage bufferedImage = new BufferedImage(imagePlusInput.getWidth(), imagePlusInput.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
				Graphics2D g2d = bufferedImage.createGraphics();
				ImagePlus ip = new ImagePlus();
				ip.setImage(bufferedImage);
				ip.setTitle("plop"+k);
				ip.show();
			}
			
		}
		return imagePlusCorrected;
	}
	
	/**
	 * 
	 * @param table
	 * @return
	 */
	private int[] convertObjectInInt(Object[]table)
	{
		int[] tableInt = new int[table.length];
		for(int i = 0; i < table.length; ++i)
		{
			tableInt[i] = Integer.parseInt(table[i].toString());
			IJ.log("point "+i+" "+table[i]);
		}
		return tableInt;
	}

}
