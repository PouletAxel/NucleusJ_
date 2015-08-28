package gred.nucleus.core;

import gred.nucleus.utils.ConvexeHullDetection;
import gred.nucleus.utils.ConvexeHullImageMaker;
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

public class ConvexHullSegmentation
{
	
	/**
	 * 
	 * @param imagePlusInput
	 */
	public ImagePlus run(ImagePlus imagePlusInput)
	{
		
		ConvexeHullImageMaker nuc = new ConvexeHullImageMaker();
	  /*  nuc.setAxes("xy");
	   	ImagePlus imagePlusXY = nuc.giftWrapping(imagePlusInput);
	   	imagePlusXY.setTitle("xy");
	   	imagePlusXY.show();*/
	   	nuc.setAxes("xz");
	   	ImagePlus imagePlusXZ = nuc.giftWrapping(imagePlusInput);
	   	imagePlusXZ.setTitle("xz");
	   	imagePlusXZ.show();
		/*nuc.setAxes("yz");
		ImagePlus imagePlusYZ = nuc.giftWrapping(imagePlusInput);
	   	imagePlusYZ.setTitle("yz");
	   	imagePlusYZ.show();*/
		
		return  imagePlusInput;//imageMakingUnion(imagePlusInput, convexHullXY, convexHullXZ, convexHullYZ);
	}



	/**
	 * 
	 * @param imagePlusInput
	 * @param convexHull
	 * @return
	 */
	
	public ImagePlus imageMakingUnion (ImagePlus imagePlusInput, ArrayList<VoxelRecord> convexHullXY,ArrayList<VoxelRecord> convexHullXZ ,ArrayList<VoxelRecord> convexHullYZ )
	{
		ImagePlus imagePlusOutput = imagePlusInput.duplicate();
		ImagePlus imagePlusXY = imageMaker(convexHullXY,"xy",imagePlusInput.getWidth(),imagePlusInput.getHeight(),imagePlusInput.getNSlices());
		imagePlusXY.show();
		ImagePlus imagePlusXZ = imageMaker(convexHullXZ,"xz",imagePlusInput.getWidth(),imagePlusInput.getNSlices(),imagePlusInput.getHeight());	
		imagePlusXZ.show();
		ImagePlus imagePlusYZ = imageMaker (convexHullYZ,"yz",imagePlusInput.getHeight(),imagePlusInput.getNSlices(),imagePlusInput.getWidth());
		imagePlusYZ.show();
		ImageStack imageStackXY= imagePlusXY.getStack();
		ImageStack imageStackXZ= imagePlusXZ.getStack();
		ImageStack imageStackYZ= imagePlusYZ.getStack();
		ImageStack imageStackOutput = imagePlusOutput.getStack();
		
		for (int k = 0; k < imagePlusXY.getNSlices();++k)
		{
			for (int i = 0; i < imagePlusXY.getWidth(); ++i)
			{
				for (int j = 0; j < imagePlusXY.getHeight();++j)
				{
					
					if (imageStackXY.getVoxel(i, j, k) != 0 )
					{	
							if(imageStackOutput.getVoxel(i, j, k) == 0)
							{
								imageStackOutput.setVoxel(i, j, k, 50);
							}
					}
					if (imageStackYZ.getVoxel(j, k, i) != 0)
					{
						if(imageStackOutput.getVoxel(i, j, k) == 0)
						{
							imageStackOutput.setVoxel(i, j, k, 100);
						}
					}
					if (imageStackXZ.getVoxel(i, k, j) != 0)	
					{
						if(imageStackOutput.getVoxel(i, j, k) == 0)
						{
							imageStackOutput.setVoxel(i, j, k, 150);
						}
					}
				}
			}
				
		}
		
		return imagePlusOutput;
	}

	
	
	
	/**
	 * 
	 * @param imagePlusInput
	 * @param convexHull
	 * @return
	 */
	public ImagePlus imageMaker (ArrayList<VoxelRecord> convexHull, String axesName ,int width, int height, int nbSlice)
	{
		ImagePlus imagePlusCorrected = new ImagePlus();
		ImageStack imageStackOutput = new ImageStack(width, height);
				
		
		for (int index = 0 ; index < nbSlice; ++index)
		{
			ImagePlus ip = new ImagePlus();
			ArrayList<VoxelRecord> plopi = new ArrayList<VoxelRecord>();
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
			for(int i = 0; i < convexHull.size(); ++i)
			{
				if(axesName == "xy")
				{
					if (convexHull.get(i)._k == (double) index)
					{
						plopi.add(convexHull.get(i));
						convexHull.remove(i);
					}
				}
				else if (axesName == "xz")
				{
					if (convexHull.get(i)._j == (double) index)
					{
						plopi.add(convexHull.get(i));
						convexHull.remove(i);
					}	
				}
				else if (axesName == "yz")
				{
					if (convexHull.get(i)._i == (double) index)
					{
						plopi.add(convexHull.get(i));
						convexHull.remove(i);
					}
				}
			}
			if (plopi.size()>0)
			{
				
				int []tableWidth = new int[plopi.size()+1];
				int []tableHeight = new int[plopi.size()+1];
				for (int i = 0; i < plopi.size();++i)
				{
					if(axesName == "xy")
					{
						tableWidth[i] = (int) plopi.get(i)._i;
						tableHeight[i] = (int) plopi.get(i)._j;
					}
					else if (axesName == "xz")
					{
						tableWidth[i] = (int) plopi.get(i)._i;
						tableHeight[i] = (int) plopi.get(i)._k;
					}
					else if (axesName == "yz")
					{
						tableWidth[i] = (int) plopi.get(i)._j;
						tableHeight[i] = (int) plopi.get(i)._k;
					}
					
				}
				if(axesName == "xy")
				{
					tableWidth[plopi.size()] = (int) plopi.get(0)._i;
					tableHeight[plopi.size()] = (int) plopi.get(0)._j;
				}
				else if (axesName == "xz")
				{
					tableWidth[plopi.size()] = (int) plopi.get(0)._i;
					tableHeight[plopi.size()] = (int) plopi.get(0)._k;
				}
				else if (axesName == "yz")
				{
					tableWidth[plopi.size()] = (int) plopi.get(0)._j;
					tableHeight[plopi.size()] = (int) plopi.get(0)._k;
				}
				Polygon p = new Polygon(tableWidth, tableHeight,tableWidth.length );
				Graphics2D g2d = bufferedImage.createGraphics();
				g2d.drawPolygon(p);
				g2d.fillPolygon(p);
				g2d.setColor(Color.WHITE);
				g2d.dispose();

				ip.setImage(bufferedImage);
			}
			else
			{
				Graphics2D g2d = bufferedImage.createGraphics();
				ip = new ImagePlus();
				ip.setImage(bufferedImage);
			}
			imageStackOutput.addSlice(ip.getProcessor());
		}
		imagePlusCorrected.setStack(imageStackOutput);

		return imagePlusCorrected;
	
	}
		
}