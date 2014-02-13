package gred.nucleus.treatment;

import gred.nucleus.analysis.MyCounter3D;
import gred.nucleus.utilitaires.Histogram;
import ij.*;
import ij.process.*;

/**
 * Class HolesFilling
 *
 * @author Philippe Andrey et Poulet Axel
 */

public class FillingHoles 
{

  /** Image to be processed*/
 ImagePlus _imagePlusInput;
 /** Histogram of the inputImage*/
 Histogram _hist;

 /**
  * Constructor
  * @param imagePlusInput Image to be processed
  */
  public FillingHoles(ImagePlus imagePlusInput) {   _imagePlusInput = imagePlusInput; }
 
  
 /**
  * Method which process the image in the three dimensions (x, y, z) in the same time.
  */
  
 public ImagePlus apply3D ()
 {
 // image inversion (0 became 255 and 255 became 0)
  ImagePlus output = _imagePlusInput;
  ImageStack imageStackInput = output.getStack();
  int width = imageStackInput.getWidth();
  int height = imageStackInput.getHeight();
  int depth = imageStackInput.getSize();
  int x, y, z;
  for (z = 0; z < depth; ++z)
    for (x = 0; x < width; ++x)
      for (y = 0; y < height; ++y)
      {
        double voxelCourrant = imageStackInput.getVoxel(x, y, z);
        if (voxelCourrant > 0) imageStackInput.setVoxel(x, y, z, 0);
        else imageStackInput.setVoxel(x, y, z, 255);
      }
  MyCounter3D myCounter3D = new MyCounter3D(output);
  output = myCounter3D.getObjMap();
  imageStackInput = output.getStack();
  _hist = new Histogram(output);
  int label;
  boolean edgeFlags [] = new boolean [_hist.getLabel().length+ 1];
  for (int a = 0; a < edgeFlags.length;++a)  edgeFlags[a] = false;
    // Analyse of plans extreme in the dim x
    for (z = 0; z < depth; ++z)
      for (y = 0; y < height; ++y)
      {
        label = (int) imageStackInput.getVoxel(0, y, z);
        edgeFlags[label] = true;
        label = (int) imageStackInput.getVoxel(width-1, y, z);
        edgeFlags[label] = true;
      }

    // Analyse of plans extreme in the dim y
    for (z = 0; z < depth; ++z)
      for (x = 0; x < width; ++x)
      {
        label = (int) imageStackInput.getVoxel(x, 0, z);
        edgeFlags[label] = true;
        label = (int) imageStackInput.getVoxel(x, height-1, z);
        edgeFlags[label] = true;
      }

    // Analyse of plans extreme in the dim z
    for (x = 0; x < depth; ++x)
      for (y = 0; y < width; ++y)
      {
        label = (int) imageStackInput.getVoxel(x, y, 0);
        edgeFlags[label] = true;
        label = (int) imageStackInput.getVoxel(x, y, depth-1);
        edgeFlags[label] = true;
      }
    
     //Creation of the image results
    for (z = 0; z < depth; ++z)
      for (x = 0; x < width; ++x)
        for (y = 0; y < height; ++y)
        {
          label = (int) imageStackInput.getVoxel(x, y, z);
          if (label == 0 || edgeFlags[label] == false)
            imageStackInput.setVoxel(x, y, z, 255);
        else   imageStackInput.setVoxel(x, y, z, 0);
        }
    
    output.setStack(imageStackInput);
    return output;
 } //apply3D

 
 
 /**
  * Method in two dimensions which process ecah plan z independent,
  *
  */
 public ImagePlus apply2D ()
 {
   ImagePlus output = _imagePlusInput;
   ImageStack imageStackInput = output.getStack();
   int width = imageStackInput.getWidth();
   int height = imageStackInput.getHeight();
   int depth = imageStackInput.getSize();
   double voxelValue;
   int x, y, z;
   ImageStack imageStackOutput = new ImageStack(width,height);
   for (z = 1; z <= depth; ++z)
   {
    ImageProcessor imageProcessor = imageStackInput.getProcessor(z);
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
      MyCounter3D myCounter3D = new MyCounter3D(imagePlusTmp);
      imagePlusTmp = myCounter3D.getObjMap();
      int label;
      _hist = new Histogram(imagePlusTmp);
      boolean edgeFlags [] = new boolean [_hist.getLabel().length+ 1];
      imageStackTmp = imagePlusTmp.getStack();
      imageProcessor = imageStackTmp.getProcessor(1);
      for (int a = 0; a < edgeFlags.length; ++a)  edgeFlags[a] = false;

      // Analyse des plans extremes selon la dim x
      for (y = 0; y < height; ++y)
      {
        label = (int) imageProcessor.getPixel(0, y);
        edgeFlags[label] = true;
        label = (int) imageProcessor.getPixel(width-1, y);
        edgeFlags[label] = true;
      }

      // Analyse des plans extremes selon la dim y
      for (x = 0; x < width; ++x)
      {
        label = (int)imageProcessor.getPixel(x, 0);
        edgeFlags[label] = true;
        label = (int) imageProcessor.getPixel(x, height-1);
        edgeFlags[label] = true;
      }
      
      for (x = 0; x < width; ++x)
        for (y = 0; y < height; ++y)
        {
          label = (int)  imageProcessor.getPixel(x, y);
          if (label == 0 || edgeFlags[label] == false) imageProcessor.putPixelValue(x,y,255);
          else    imageProcessor.putPixelValue(x, y, 0);
        }
      imageStackOutput.addSlice(imageProcessor);
    }
    output.setStack(imageStackOutput);
    return output;
  }//apply2D
}//fin classe
