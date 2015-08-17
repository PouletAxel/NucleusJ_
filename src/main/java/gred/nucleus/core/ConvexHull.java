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

		ImagePlus imagePlusCorrected = imagePlusInput;
		ImageStack imageStackOutput = new ImageStack(imagePlusInput.getWidth(),imagePlusInput.getHeight());
				
		
		for (int k = 0 ;  k < imagePlusInput.getNSlices(); ++k)
		{
			ImagePlus ip = new ImagePlus();
			IJ.log("quel plan "+k);
			ArrayList<VoxelRecord> plopi = new ArrayList<VoxelRecord>();
			IJ.log("J'en suis a XY");
			/*for(int i = 0; i < convexHullXY.size(); ++i)
			{
				if (convexHullXY.get(i)._k == (double) k)
				{
					plopi.add(convexHullXY.get(i));
					IJ.log("x "+convexHullXY.get(i)._i+" y "+convexHullXY.get(i)._j);
					convexHullXY.remove(i);
				}
				
			}
			IJ.log("J'en suis a XZ");
			for(int i = 0; i < convexHullXZ.size(); ++i)
			{
				if (convexHullXZ.get(i)._k == (double) k)
				{
					if (testVoxel(plopi,convexHullXZ.get(i)) == -1)
					{
						plopi.add(convexHullXZ.get(i));
						IJ.log("x "+convexHullXZ.get(i)._i+" y "+convexHullXZ.get(i)._j);
					}
					convexHullXZ.remove(i);
				}
				
			}*/
			IJ.log("J'en suis a YZ");
			for(int i = 0; i < convexHullYZ.size(); ++i)
			{
				if (convexHullYZ.get(i)._k == (double) k)
				{
					if (testVoxel(plopi,convexHullYZ.get(i)) == -1)
					{
						plopi.add(convexHullYZ.get(i));
						IJ.log("x "+convexHullYZ.get(i)._i+" y "+convexHullYZ.get(i)._j);
					}
						convexHullYZ.remove(i);
				}
				
			}
			if (plopi.size()>0)
			{
				BufferedImage bufferedImage = new BufferedImage(imagePlusInput.getWidth(), imagePlusInput.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
				int []tablex = new int[plopi.size()+1];
				int []tabley = new int[plopi.size()+1];
				for (int i = 0; i < plopi.size();++i)
				{
					tablex[i] = (int) plopi.get(i)._i;
					tabley[i] = (int) plopi.get(i)._j;
				}
				tablex[plopi.size()] = (int) plopi.get(0)._i;
				tabley[plopi.size()] = (int) plopi.get(0)._j;
				IJ.log("Je suis "+k+ " et je passes par la");
				Polygon p = new Polygon(tablex, tabley,tablex.length );
				Graphics2D g2d = bufferedImage.createGraphics();
				g2d.drawPolygon(p);
				g2d.fillPolygon(p);
				g2d.setColor(Color.WHITE);
				g2d.dispose();

				ip.setImage(bufferedImage);
				//ip.setTitle("plop"+k);
				//ip.show();
			}
			else
			{
				BufferedImage bufferedImage = new BufferedImage(imagePlusInput.getWidth(), imagePlusInput.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
				Graphics2D g2d = bufferedImage.createGraphics();
				ip = new ImagePlus();
				ip.setImage(bufferedImage);
				//ip.setTitle("plop"+k);
				//ip.show();
			}
			imageStackOutput.addSlice(ip.getProcessor());
		}
		imagePlusCorrected.setStack(imageStackOutput);
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
	
	private int testVoxel(ArrayList<VoxelRecord> listVoxel, VoxelRecord voxel)
	{
		int plop = -1;
		for (int x = 0; x < listVoxel.size(); ++x )
		{
			if (voxel.compareCooridnatesTo(listVoxel.get(x)) == 0)
			{
				plop = 0;
				break;
			}
		}
		return plop;
		
	}
}
