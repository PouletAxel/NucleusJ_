
package gred.nucleus.utils;

import gred.nucleus.core.Measure3D;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ConvexeHullImageMaker
{
	private VoxelRecord _p0 = new VoxelRecord();
	private String _axesName = "";
	private Calibration _calibration;
	ArrayList<Double> _listLabel;
	
	/**
	 * 
	 * @param imagePlusBinary
	 * @return
	 */
	public  ImagePlus giftWrapping (ImagePlus imagePlusBinary)
	{
		_calibration = imagePlusBinary.getCalibration();
		ImageStack imageStackInput = imagePlusBinary.getStack();
		Measure3D mesure3d = new Measure3D();
		double equivalentSphericalRadius = (mesure3d.equivalentSphericalRadius(imagePlusBinary)/2);
		ImagePlus imagePlusCorrected = new ImagePlus();
		int indice = 0;
		int width = 0, height = 0;
		ImagePlus imagePlusBlack = new ImagePlus();
		if (_axesName =="xy")
		{
			width = imagePlusBinary.getWidth();
			height = imagePlusBinary.getHeight();
			indice = imagePlusBinary.getNSlices();
		}
		else if (_axesName =="xz")
		{
			width = imagePlusBinary.getWidth();
			height = imagePlusBinary.getNSlices();
			indice = imagePlusBinary.getHeight();
		}
		else
		{
			width = imagePlusBinary.getHeight();
			height = imagePlusBinary.getNSlices();
			indice = imagePlusBinary.getWidth();
		}
			
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D blackImage = bufferedImage.createGraphics();
		imagePlusBlack.setImage(bufferedImage);
		ImageStack imageStackOutput = new ImageStack(width, height);
		for (int k = 0; k < indice; ++k )
		{
			ImagePlus ip = imagePlusBlack.duplicate();
			double[][] image = giveTable(imagePlusBinary, width, height, k);
			if (_listLabel.size()==1)
			{
				ArrayList<VoxelRecord> lVoxelBoundary = detectVoxelBoudary(image,_listLabel.get(0),k);
				if (lVoxelBoundary.size() > 5)
					ip = imageMaker(image,lVoxelBoundary , width, height, equivalentSphericalRadius);
				else
					ip = imagePlusBlack.duplicate() ;
			}
			else if(_listLabel.size()>1)
			{
				ImageStack imageStackIp = ip.getImageStack();
				for (int i = 0; i < _listLabel.size();++i)
				{
					ArrayList<VoxelRecord> lVoxelBoundary = detectVoxelBoudary(image,_listLabel.get(i),k);
					if (lVoxelBoundary.size() > 5)
					{						
						ImageStack imageTempStack = imageMaker(image,lVoxelBoundary , width, height, equivalentSphericalRadius).getStack();
						for (int l = 0; l < width; ++l)
							for (int m = 0; m < height; ++m)
								if (imageTempStack.getVoxel(l, m, 0)> 0)
									imageStackIp.setVoxel(l, m, 0,255);							
						}
					}
				}
			else ip = imagePlusBlack.duplicate();
			imageStackOutput.addSlice(ip.getProcessor());
		}
		imagePlusCorrected.setStack(imageStackOutput);
		return imagePlusCorrected;
	}
	
	
	

	/**
	 * 
	 * @param image
	 * @param label
	 * @param indice
	 * @return
	 */
	ArrayList<VoxelRecord> detectVoxelBoudary (double[][] image, double label, int indice)
	{
		ArrayList<VoxelRecord> lVoxelBoundary = new ArrayList<VoxelRecord>();
		_p0.setLocation(0,0,0);
		for(int i = 0; i < image.length; ++i )
		  for(int j = 0; j < image[i].length; ++j )
			  if (image[i][j] == label)
				  if ( image[i-1][j] == 0 || image[i+1][j] == 0|| image[i][j-1] == 0|| image[i][j+1]== 0)
				  {
					  VoxelRecord voxelTest = new VoxelRecord();
					  if(_axesName == "xy")	voxelTest.setLocation(i, j,indice);
					  else if(_axesName == "xz")	voxelTest.setLocation(i, indice,j);
					  else	voxelTest.setLocation(indice,i,j);
					  lVoxelBoundary.add(voxelTest);	
					  if (j > _p0._j)
					  {
						  if(_axesName == "xy")	_p0.setLocation(i, j,indice);
						  else if(_axesName == "xz")	_p0.setLocation(i, indice,j);
						  else	_p0.setLocation(indice,i,j);
					  }
					  else if (j ==_p0._j)
						  if(i > _p0._i)
						  {
							  if(_axesName == "xy")	_p0.setLocation(i, j,indice);
							  else if(_axesName == "xz")	_p0.setLocation(i, indice,j);
							  else	_p0.setLocation(indice,i,j);
						  }
				  }
		//IJ.log("voxeldepart : "+_p0._i+" "+_p0._j+" "+_p0._k);
		return lVoxelBoundary;
	}
	/**
	 * 
	 * @param lVoxelBoundary
	 * @param width
	 * @param height
	 * @param equivalentSphericalRadius
	 * @return
	 */
	public ImagePlus imageMaker (double[][] image,ArrayList<VoxelRecord> lVoxelBoundary ,int width, int height,double equivalentSphericalRadius)
	{
		ArrayList<VoxelRecord> convexHull = new ArrayList<VoxelRecord> ();
		convexHull.add(_p0);
		VoxelRecord vectorTest = new VoxelRecord();
		if(_axesName == "xy" || _axesName == "xz")	
			vectorTest.setLocation (-10, 0, 0);
		else if (_axesName == "yz")
			vectorTest.setLocation (0, -10, 0);
		
		ConvexeHullDetection convexHullDetection = new ConvexeHullDetection();
		convexHullDetection.setInitialVoxel(_p0);
		convexHullDetection.setAxes(_axesName);
		convexHull = convexHullDetection.findConvexeHull (image, convexHull,lVoxelBoundary, vectorTest, _calibration,equivalentSphericalRadius);
		ImagePlus ip =  makerPolygon ( convexHull , width, height);
		return ip;
	}
	
	/**
	 * 
	 * @param convexHull
	 * @param width
	 * @param height
	 * @return
	 */
	public ImagePlus makerPolygon (ArrayList<VoxelRecord> convexHull ,int width, int height)
	{
		ImagePlus ip = new ImagePlus();
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		int []tableWidth = new int[convexHull.size()+1];
		int []tableHeight = new int[convexHull.size()+1];
		for(int i = 0; i < convexHull.size(); ++i)
		{
			if(_axesName == "xy")
			{
				tableWidth[i] = (int) convexHull.get(i)._i;
				tableHeight[i] = (int) convexHull.get(i)._j;
			}
			else if (_axesName == "xz")
			{
				tableWidth[i] = (int) convexHull.get(i)._i;
				tableHeight[i] = (int) convexHull.get(i)._k;
			}
			else if (_axesName == "yz")
			{
				tableWidth[i] = (int) convexHull.get(i)._j;
				tableHeight[i] = (int) convexHull.get(i)._k;
			}
		}
					
		if(_axesName == "xy")
		{
			tableWidth[convexHull.size()] = (int) convexHull.get(0)._i;
			tableHeight[convexHull.size()] = (int) convexHull.get(0)._j;
		}
		else if (_axesName == "xz")
		{
			tableWidth[convexHull.size()] = (int) convexHull.get(0)._i;
			tableHeight[convexHull.size()] = (int) convexHull.get(0)._k;
		}
		else if (_axesName == "yz")
		{
			tableWidth[convexHull.size()] = (int) convexHull.get(0)._j;
			tableHeight[convexHull.size()] = (int) convexHull.get(0)._k;
		}
		Polygon p = new Polygon(tableWidth, tableHeight,tableWidth.length );
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawPolygon(p);
		g2d.fillPolygon(p);
		g2d.setColor(Color.WHITE);
		g2d.dispose();
		ip.setImage(bufferedImage);
		return ip;
	
	}
	
	/**
	 * 
	 * @param imagePlusInput
	 * @param width
	 * @param height
	 * @param indice
	 * @return
	 */
	double [][] giveTable(ImagePlus imagePlusInput, int width, int height, int indice) 
	{
		ImageStack imageStackInput =  imagePlusInput.getStack();
		double [][] image = new double [width+1][height+1]; 
		for (int i = 0; i < width; ++i )
			for (int j = 0; j < height; ++j)
			{
				if(_axesName == "xy")
					image[i][j] = imageStackInput.getVoxel(i, j, indice);
				else if(_axesName == "xz")
					image[i][j] = imageStackInput.getVoxel(i, indice,j);
				else
					image[i][j] = imageStackInput.getVoxel(indice,i,j);
			}
	
		ConnectedComponent componentConnexe = new ConnectedComponent(image);
		componentConnexe.setImageTable(image);
		_listLabel = componentConnexe.getListLabel(255);
		image = componentConnexe.getImageTable2D();
		//for (int i = 0; i < width; ++i )
			//for (int j = 0; j < height; ++j)
				//imageStackInput.setVoxel(i, j, indice, image[i][j]);
		return image;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAxes () {return _axesName;}
	
	/**
	 * 
	 * @param axes
	 */
	public void setAxes(String axes){ _axesName=axes;}
}