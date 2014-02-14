package gred.nucleus.core;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ij.IJ;
import ij.ImagePlus;

public class NucleusChromocentersAnalysis
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

	   public NucleusChromocentersAnalysis (ImagePlus ipRaw, ImagePlus ipCC,ImagePlus ipNucBin )
	   {
		   _imagePlusDeconv = ipRaw;
		   _imagePlusSegmentationChromocenter = ipCC;
		   _imagePlusBinaryNucleus = ipNucBin;
		   _poso = new ParametersOfSeveralObject(_imagePlusSegmentationChromocenter);
	   }
	  
	   /**
	    * 
	    * @param rhfChoice
	    */
	   
	   public void computeParameters (String rhfChoice)
	   {
		   Measure3D gp3d = new Measure3D (_imagePlusBinaryNucleus,255);
		   ShapeParameters3D sp3d = new ShapeParameters3D (_imagePlusBinaryNucleus,255); 
		   double [] volume =  _poso.computeVolumeofAllObjects();
		   RadialDistance rd = new RadialDistance (_imagePlusSegmentationChromocenter,_imagePlusBinaryNucleus);
		   double dr [] = rd.computeBorderToBorderDistances();
		   RhfCompute rhf = new RhfCompute(_imagePlusDeconv ,_imagePlusSegmentationChromocenter, _imagePlusBinaryNucleus);
		   IJ.log("3D PARAMETERS");
		   
		   if (rhfChoice.equals("Volume and intensity"))
	        {
			   IJ.log("ImageTitle Volume ESR NbCc VCcMean DistanceRadialeMean Flatness Elongation Sphericity IntensityRHF VolumeRHF");
			   IJ.log(_imagePlusBinaryNucleus.getTitle()+" "+gp3d.computeVolumeObject()+" "+gp3d.equivalentSphericalRadius()+" "
			   +_poso.nbObject()+" "+_poso.computeMeanOfTable(volume)+" "+_poso.computeMeanOfTable(dr)+" "+sp3d.computeFlatnessObject()
			   +" "+sp3d.computeElongationObject()+" "+sp3d.computeSphericity()+" "+rhf.computeRhfIntensite()+" "+rhf.computeRhfVolume());
	        }
	        else if (rhfChoice.equals("Volume"))
	        {
	        	 IJ.log("ImageTitle Volume ESR NbCc VCcMean DistanceRadialeMean Flatness Elongation Sphericity VolumeRHF");
	        	 IJ.log(_imagePlusBinaryNucleus.getTitle()+" "+gp3d.computeVolumeObject()+" "+gp3d.equivalentSphericalRadius()+" "
			   +_poso.nbObject()+" "+_poso.computeMeanOfTable(volume)+" "+_poso.computeMeanOfTable(dr)+" "+sp3d.computeFlatnessObject()
			   +" "+sp3d.computeElongationObject()+" "+sp3d.computeSphericity()+" "+rhf.computeRhfVolume());
	        }
			  else  
			  {
				  IJ.log("ImageTitle Volume ESR NbCc VCcMean DistanceRadialeMean Flatness Elongation Sphericity IntensityRHF ");
				  IJ.log(_imagePlusBinaryNucleus.getTitle()+" "+gp3d.computeVolumeObject()+" "+gp3d.equivalentSphericalRadius()+" "
						  +_poso.nbObject()+" "+_poso.computeMeanOfTable(volume)+" "+_poso.computeMeanOfTable(dr)+" "+sp3d.computeFlatnessObject()
						  +" "+sp3d.computeElongationObject()+" "+sp3d.computeSphericity()+" "+rhf.computeRhfIntensite());
			  }
	   }
	   
	   /**
	    * 
	    * @param pathFile
	    * @param rhfChoice
	    * @throws IOException 
	    */
	   
	   public void computeParameters (String pathFile ,String rhfChoice) throws IOException
	   {
		   SapheParameters2D sp2D = new SapheParameters2D (_imagePlusBinaryNucleus);
		   Measure3D gp3d = new Measure3D (_imagePlusBinaryNucleus,255);
		   ShapeParameters3D sp3d = new ShapeParameters3D (_imagePlusBinaryNucleus,255); 
		   double [] volume =  _poso.computeVolumeofAllObjects();
		   RadialDistance rd = new RadialDistance (_imagePlusSegmentationChromocenter,_imagePlusBinaryNucleus);
		   double dr [] = rd.computeBorderToBorderDistances();
		   RhfCompute rhf = new RhfCompute(_imagePlusDeconv ,_imagePlusSegmentationChromocenter, _imagePlusBinaryNucleus);
		   File fileResu = new File (pathFile);
		   boolean exist = fileResu.exists();
		   BufferedWriter output;	
		   if (exist)
		   {
			   FileWriter fw = new FileWriter(fileResu, true);
			   output = new BufferedWriter(fw);
			   if (rhfChoice.equals("Volume and intensity"))
		        {
				   output.write(_imagePlusBinaryNucleus.getTitle()+"\t"+gp3d.computeVolumeObject()+"\t"+gp3d.equivalentSphericalRadius()+"\t"
						   +_poso.nbObject()+"\t"+_poso.computeMeanOfTable(volume)+"\t"+_poso.computeMeanOfTable(dr)+"\t"+sp3d.computeFlatnessObject()
						   +"\t"+sp3d.computeElongationObject()+"\t"+sp3d.computeSphericity()+"\t"+rhf.computeRhfIntensite()+"\t"+rhf.computeRhfVolume()+"\t"+sp2D.getAspectRatio()+"\t"+sp2D.getCirculairty()+"\n");
		        }
			   else if (rhfChoice.equals("Volume"))
		        {
				   output.write(_imagePlusBinaryNucleus.getTitle()+"\t"+gp3d.computeVolumeObject()+"\t"+gp3d.equivalentSphericalRadius()+"\t"
						   +_poso.nbObject()+"\t"+_poso.computeMeanOfTable(volume)+"\t"+_poso.computeMeanOfTable(dr)+"\t"+sp3d.computeFlatnessObject()
						   +"\t"+sp3d.computeElongationObject()+"\t"+sp3d.computeSphericity()+"\t"+rhf.computeRhfVolume()+"\t"+sp2D.getAspectRatio()+"\t"+sp2D.getCirculairty()+"\n");
		        }
			   else  
				  {
				   output.write(_imagePlusBinaryNucleus.getTitle()+"\t"+gp3d.computeVolumeObject()+"\t"+gp3d.equivalentSphericalRadius()+"\t"
							  +_poso.nbObject()+"\t"+_poso.computeMeanOfTable(volume)+"\t"+_poso.computeMeanOfTable(dr)+"\t"+sp3d.computeFlatnessObject()
							  +"\t"+sp3d.computeElongationObject()+"\t"+sp3d.computeSphericity()+"\t"+rhf.computeRhfIntensite()+"\t"+sp2D.getAspectRatio()+"\t"+sp2D.getCirculairty()+"\n");
				  }
		   }
		   
		   else
		   {
			   FileWriter fw = new FileWriter(fileResu, true);
			   output = new BufferedWriter(fw);
			   if (rhfChoice.equals("Volume and intensity"))
		        {

				   output.write("ImageTitle\tVolume\tESR\tNbCc\tVCcMean\tDistanceRadialeMean\tFlatness\tElongation\tSphericity\tIntensityRHF\tVolumeRHF\tAspectRatio\tCircularity\n"
						   +_imagePlusBinaryNucleus.getTitle()+"\t"+gp3d.computeVolumeObject()+"\t"+gp3d.equivalentSphericalRadius()+"\t"
						   +_poso.nbObject()+"\t"+_poso.computeMeanOfTable(volume)+"\t"+_poso.computeMeanOfTable(dr)+"\t"+sp3d.computeFlatnessObject()
						   +"\t"+sp3d.computeElongationObject()+"\t"+sp3d.computeSphericity()+"\t"+rhf.computeRhfIntensite()+"\t"+rhf.computeRhfVolume()+"\t"+sp2D.getAspectRatio()+"\t"+sp2D.getCirculairty()+"\n"); 
		        }
			   else if (rhfChoice.equals("Volume"))
		        {
				   output.write("ImageTitle\tVolume\tESR\tNbCc\tVCcMean\tDistanceRadialeMean\tFlatness\tElongation\tSphericity\tVolumeRHF\n"
						   +_imagePlusBinaryNucleus.getTitle()+"\t"+gp3d.computeVolumeObject()+"\t"+gp3d.equivalentSphericalRadius()+"\t"
						   +_poso.nbObject()+"\t"+_poso.computeMeanOfTable(volume)+"\t"+_poso.computeMeanOfTable(dr)+"\t"+sp3d.computeFlatnessObject()
						   +"\t"+sp3d.computeElongationObject()+"\t"+sp3d.computeSphericity()+"\t"+rhf.computeRhfVolume()+"\t"+sp2D.getAspectRatio()+"\t"+sp2D.getCirculairty()+"\n");
		        }
			   else  
				  {
				   output.write("ImageTitle\tVolume\tESR\tNbCc\tVCcMean\tDistanceRadialeMean\tFlatness\tElongation\tSphericity\tIntensityRHF\n"
						   		+_imagePlusBinaryNucleus.getTitle()+"\t"+gp3d.computeVolumeObject()+"\t"+gp3d.equivalentSphericalRadius()+"\t"
						   		+_poso.nbObject()+"\t"+_poso.computeMeanOfTable(volume)+"\t"+_poso.computeMeanOfTable(dr)+"\t"+sp3d.computeFlatnessObject()
						   		+"\t"+sp3d.computeElongationObject()+"\t"+sp3d.computeSphericity()+"\t"+rhf.computeRhfIntensite()+"\t"+sp2D.getAspectRatio()+"\t"+sp2D.getCirculairty()+"\n");
				  }
		   } 
		   output.flush();
		   output.close();   
	   }
}
