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

  /** Image to be processed*/
 //ImagePlus _imagePlusInput;
 /** Histogram of the inputImage*/
 //Histogram _hist;

 /**
  * Constructor
  * @param imagePlusInput Image to be processed
  */
  public FillingHoles() {}
 
  
 /**
  * Method which process the image in the three dimensions (x, y, z) in the same time.
  */
  
 public ImagePlus apply3D (ImagePlus imagePlusInput)
 {
 // image inversion (0 became 255 and 255 became 0)
  ImagePlus output = imagePlusInput;
  
  ImageStack imageStackOutput = output.getStack();
  int width = imageStackOutput.getWidth();
  int height = imageStackOutput.getHeight();
  int depth = imageStackOutput.getSize();
  int x, y, z;
  for (z = 0; z < depth; ++z)
    for (x = 0; x < width; ++x)
      for (y = 0; y < height; ++y)
      {
        double voxelCourrant = imageStackOutput.getVoxel(x, y, z);
        if (voxelCourrant > 0) imageStackOutput.setVoxel(x, y, z, 0);
        else imageStackOutput.setVoxel(x, y, z, 255);
        
      }
  output = ConnectedComponents.computeLabels(output, 26, 32);
  int label;
  boolean edgeFlags [] = new boolean [(int)output.getStatistics().max+1];
  imageStackOutput = output.getImageStack();
  IJ.log(""+output.getStatistics().max);
  for (int a = 0; a < edgeFlags.length;++a)  edgeFlags[a] = false;
    // Analyse of plans extreme in the dim x
    for (z = 0; z < depth; ++z)
      for (y = 0; y < height; ++y)
      {
        label = (int) imageStackOutput.getVoxel(0, y, z);
        edgeFlags[label] = true;
        label = (int) imageStackOutput.getVoxel(width-1, y, z);
        edgeFlags[label] = true;
      }

    // Analyse of plans extreme in the dim y
    for (z = 0; z < depth; ++z)
      for (x = 0; x < width; ++x)
      {
        label = (int) imageStackOutput.getVoxel(x, 0, z);
        edgeFlags[label] = true;
        label = (int) imageStackOutput.getVoxel(x, height-1, z);
        edgeFlags[label] = true;
      }

    // Analyse of plans extreme in the dim z
    for (x = 0; x < depth; ++x)
      for (y = 0; y < width; ++y)
      {
        label = (int) imageStackOutput.getVoxel(x, y, 0);
        edgeFlags[label] = true;
        label = (int) imageStackOutput.getVoxel(x, y, depth-1);
        edgeFlags[label] = true;
      }
    
     //Creation of the image results
    for (z = 0; z < depth; ++z)
      for (x = 0; x < width; ++x)
        for (y = 0; y < height; ++y)
        {
          label = (int) imageStackOutput.getVoxel(x, y, z);
          if (label == 0 || edgeFlags[label] == false)
        	  imageStackOutput.setVoxel(x, y, z, 255);
        else   imageStackOutput.setVoxel(x, y, z, 0);
        }
    
    output.setStack(imageStackOutput);
    return output;
 } //apply3D

 
 
 /**
  * Method in two dimensions which process ecah plan z independent,
  *
  */
 public ImagePlus apply2D (ImagePlus imagePlusInput)
 {
	 //Image stack a renommer correctement & modifier image d'entree
   ImagePlus output = imagePlusInput;
   ImageStack imageStack = output.getStack();
   int width = imageStack.getWidth();
   int height = imageStack.getHeight();
   int depth = imageStack.getSize();
   double voxelValue;
   int x, y, z;
   ImageStack imageStackOutput = new ImageStack(width,height);
   for (z = 1; z <= depth; ++z)
   {
    ImageProcessor imageProcessor = imageStack.getProcessor(z);
    for (x = 0; x < width; ++x)
      for (y = 0; y < height; ++y)
      {
    	  voxelValue = imageProcessor.getPixel(x, y);
        if (voxelValue > 0)      imageProcessor.putPixelValue(x, y, 0);
        else        imageProcessor.putPixelValue(x, y, 255);
      }
    
      ImagePlus imagePlusTmp = new ImagePlus();
      ImageStack imageStackTmp = new ImageStack(width, height);
      imageStackTmp.addSlice(imageProcessor);
      imagePlusTmp.setStack(imageStackTmp);
      imagePlusTmp = ConnectedComponents.computeLabels(imagePlusTmp, 26, 32);
      int label;
      imageStackTmp = imagePlusTmp.getStack();
      imageProcessor = imageStackTmp.getProcessor(1);
      boolean edgeFlags [] = new boolean [(int)imageProcessor.getMax()+1];
      for (int a = 0; a < edgeFlags.length; ++a)  edgeFlags[a] = false;

      // Analyse des plans extremes selon la dim x
      for (y = 0; y < height; ++y)
      {
        label = (int) imageProcessor.getf(0, y);
        edgeFlags[label] = true;
        label = (int) imageProcessor.getf(width-1, y);
        edgeFlags[label] = true;
      }

      // Analyse des plans extremes selon la dim y
      for (x = 0; x < width; ++x)
      {
        label = (int)imageProcessor.getf(x, 0);
        edgeFlags[label] = true;
        label = (int) imageProcessor.getf(x, height-1);
        edgeFlags[label] = true;
      }
      
      for (x = 0; x < width; ++x)
        for (y = 0; y < height; ++y)
        {
          label = (int)  imageProcessor.getf(x, y);
          if (label == 0 || edgeFlags[label] == false) imageProcessor.putPixelValue(x,y,255);
          else    imageProcessor.putPixelValue(x, y, 0);
        }
      imageStackOutput.addSlice(imageProcessor);
    }
    output.setStack(imageStackOutput);
    return output;
  }//apply2D
}//fin classe
