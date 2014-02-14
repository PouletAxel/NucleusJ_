package gred.nucleus.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ij.IJ;
import ij.ImagePlus;

public class ChromocenterAnalysis
{
	
	 
	  /** Segmented image of chrmocenters */
	  ImagePlus _imagePlusSegmentationChromocenter;
	  /** Deconvoled image of _imagePlusSegmentationChromocenter */
	  ImagePlus _imagePlusDeconv;
	  /** Binary image of _imagePlusSegmentationChromocenter */
	  ImagePlus _imagePlusBinaryNucleus;
	  /** Title of _imagePlusSeuillageManuelOfCc*/
	  ParametersOfSeveralObject _poso;
	 

	  /**
	   * Constructor
	   * Recuperation of data and of images to determine the differents parameters
	   *
	   * @param imagePlusInput Image of semented chromocneters
	   */

	   public ChromocenterAnalysis (ImagePlus ipCC,ImagePlus ipNucBin )
	   {
		   _imagePlusSegmentationChromocenter = ipCC;
		   _imagePlusBinaryNucleus = ipNucBin;
		   _poso = new ParametersOfSeveralObject(_imagePlusSegmentationChromocenter);
	   }
	   
	   /**
	    * 
	    */
	   public void computeParametersChromocenter ()
	   {
		  double [] volume =  _poso.computeVolumeofAllObjects();
		  RadialDistance rd = new RadialDistance (_imagePlusSegmentationChromocenter,_imagePlusBinaryNucleus);
		  IJ.log("CHROMOCENTER PARAMETERS");
		  double dr [] = rd.computeBorderToBorderDistances();
		  for (int i = 0; i < dr.length;++i )
		  {
			
			  IJ.log("Titre Volume RadialDistance");
			  IJ.log(_imagePlusSegmentationChromocenter.getTitle()+"_"+i+" "+volume[i]+" "+dr[i]);
		  }
	   }
	   
	   /**
	    * 
	    * @param pathFile
	    * @throws IOException 
	    */
	   public void computeParametersChromocenter (String pathFile) throws IOException
	   {
		  double [] volume =  _poso.computeVolumeofAllObjects();
		  RadialDistance rd = new RadialDistance (_imagePlusSegmentationChromocenter,_imagePlusBinaryNucleus);
		  double dr [] = rd.computeBorderToBorderDistances();
		  File fileResu = new File (pathFile);

		  FileWriter fw = new FileWriter(fileResu, true);
	    
		  boolean exist = fileResu.exists();
		  BufferedWriter output;	
		  output = new BufferedWriter(fw);
		  if (exist == false)      output.write("Titre\tVolume\tRadialDistance\n");
		  for (int i = 0; i < dr.length;++i )
		  {
			  output.write(_imagePlusSegmentationChromocenter.getTitle()+"_"+i+"\t"+volume[i]+"\t"+dr[i]+"\n");
		   }	
		   output.flush();
		   output.close();
	   }
}
