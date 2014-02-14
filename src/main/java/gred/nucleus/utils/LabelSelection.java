package gred.nucleus.utils;

import java.util.HashMap;

import ij.*;
import ij.measure.Calibration;


/**
 * Class for the treatment of false positive signal and false negative signal
 * after the segmentation
 *
 * @author Poulet Axel
 */

// a metttre dans nucleus Segmentation
public class LabelSelection
{
  /** Image label input */
  ImagePlus _imagePluslab ;
  /** image input Histogram*/
  Histogram _hist;
  
  /**
   * Constructor
   * @param imagePluslab 
   */

  public LabelSelection (ImagePlus imagePluslab)
  {
    _imagePluslab = imagePluslab;
    _hist =new Histogram(_imagePluslab);
  }

  
  /**
   * Preserve the larger object and remove the other objects
   *
   * @param imagePluslab Image labeled
   */

  public void deleteArtefactNucleus ()
  {
    int i,j,k;
    double voxel;
    double label = getLabelOfLargestObject();
    ImageStack imageStackLab = _imagePluslab.getStack();
    for(k = 0; k < _imagePluslab.getNSlices(); ++k)
      for (i = 0; i < imageStackLab.getWidth(); ++i)
        for (j = 0; j < imageStackLab.getHeight(); ++j)
        {
          voxel = imageStackLab.getVoxel(i,j,k);
          if (voxel == label) imageStackLab.setVoxel(i,j,k,255);
          else imageStackLab.setVoxel(i,j,k,0);
     }
   }//deleteArtefactNoyau

  /**
   * Browse each object of image and return the label of the larger object
   * @param imagePluslab Image labeled
   * @return Label of the larger object
   */

  public double getLabelOfLargestObject()
  {
	  //parcourir autrement
    double indiceVmax = 0, vmax = 0, volume=0;
    double tabHisto []= _hist.getLabel();
    HashMap<Double , Integer> hhistogram = _hist.getHisto();
    for (int i = 0; i < tabHisto.length;++i)
    {
       	volume = hhistogram.get(tabHisto[i]);
        if (volume > vmax)
        {
        	vmax = volume;
        	indiceVmax = tabHisto[i];
        }
    }
    return indiceVmax;
  }//getLabelOfLargestObject

  /**
   * Method which determines the number of object in the image
   * @param imagePlus Binary labeled
   * @return number of object
   */

  public int getNbObject() { return _hist.getHisto().size();  }
}//fin classe