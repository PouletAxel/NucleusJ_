package gred.nucleus.core;

import ij.ImagePlus;
import ij.ImageStack;
/**
 * 
 * @author gred
 *
 */
public class RhfCompute
{
	/** Title of _imagePlusSeuillageManuelOfCc*/
	 ImagePlus _imagePlusSegmentationChromocenter;
	 /** Deconvoled image of _imagePlusSegmentationChromocenter */
	 ImagePlus _imagePlusRaw;
	 /** Binary image of _imagePlusSegmentationChromocenter */
	 ImagePlus _imagePlusBinaryNucleus;
	 
	 /**
	  * 
	  * @param iPRaw
	  * @param iPSegCc
	  * @param iPBinaryNucleus
	  */
	public RhfCompute (ImagePlus iPRaw,  ImagePlus iPSegCc,ImagePlus iPBinaryNucleus)
	
	{
		_imagePlusSegmentationChromocenter = iPSegCc;
		_imagePlusRaw = iPRaw;
		_imagePlusBinaryNucleus = iPBinaryNucleus;
	}
	
	/**
	 * Method which compute the RHF (total chromocenters Intensity / nucleus intensity)
	 * @return
	 */
	public double computeRhfIntensite ()
	  {
	    double ccIntensity = 0, nucleusIntensity = 0;
	    double voxelValueLabel, voxelValueDeconv, voxelValueBinaire;
	    int i,j,k;
	    ImageStack _imageStackSegmentationChromocenter =  _imagePlusSegmentationChromocenter.getStack();
	    ImageStack imageStackBinaire = _imagePlusBinaryNucleus.getStack();
	    ImageStack imageStackDeconv = _imagePlusRaw.getStack();
	    for (k = 0; k < _imagePlusRaw.getNSlices(); ++k)
	      for (i = 0; i < _imagePlusRaw.getWidth(); ++i )
	        for (j = 0; j < _imagePlusRaw.getHeight(); ++j )
	        {
	          voxelValueBinaire = imageStackBinaire.getVoxel(i, j, k);
	          voxelValueDeconv = imageStackDeconv.getVoxel(i, j, k);
	          voxelValueLabel = _imageStackSegmentationChromocenter.getVoxel(i,j,k);
	     
	          if (voxelValueBinaire > 0)
	          {
	            if (voxelValueLabel > 0){ ccIntensity+=voxelValueDeconv;}
	            nucleusIntensity += voxelValueDeconv;
	          }
	        }
	    return ccIntensity / nucleusIntensity;
	  }//computeRhfIntensite

	  /**
	   * Method which compute the RHF (total chromocenters volume / nucleus volume)
	   * @return RHF
	   */
	  
	  public double computeRhfVolume ()
	  {
	    int i;
	    double volumeCc = 0;
	    ParametersOfSeveralObject poso =new ParametersOfSeveralObject(_imagePlusSegmentationChromocenter);
	    
	    double tabVolume[] = poso.computeVolumeofAllObjects();
	    for (i = 0; i < tabVolume.length; ++i) volumeCc += tabVolume[i];
	    Measure3D gp3D = new Measure3D(_imagePlusBinaryNucleus, 255);
	    double volumeNucleus = gp3D.computeVolumeObject();

	    return volumeCc / volumeNucleus;
	  } //computeRhfVolume

}
