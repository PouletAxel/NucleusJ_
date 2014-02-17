package gred.nucleus.core;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;

public class NucleusChromocentersAnalysis
{
	  /**
	   * Constructor
	   * Recuperation of data and of images to determine the differents parameters
	   *
	   * 
	   */

	   public NucleusChromocentersAnalysis ()
	   {	   }

	   /**
	    * 
	    * @param rhfChoice
	    * @param imagePlusRaw
	    * @param imagePlusBinary
	    * @param imagePlusChromocenter
	    */
	   public void computeParameters (String rhfChoice, ImagePlus imagePlusRaw, ImagePlus imagePlusBinary, ImagePlus imagePlusChromocenter)
	   {
		   Calibration calibration = imagePlusRaw.getCalibration();
		   double voxelVolume = calibration.pixelDepth*calibration.pixelHeight*calibration.pixelWidth;
		   Measure3D measure3D = new Measure3D ();
		   double [] volumesObjects =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
		   RadialDistance radialDistance = new RadialDistance ();
		   double borderToBorderDistanceTable [] = radialDistance.computeBorderToBorderDistances(imagePlusChromocenter,imagePlusBinary);
		   double barycenterToBorderDistanceTable [] = radialDistance.computeBarycenterToBorderDistances (imagePlusBinary,imagePlusChromocenter);
		   IJ.log("3D PARAMETERS");
		   double volume = measure3D.computeVolumeObject(imagePlusBinary,255);
		   double surfacicArea = measure3D.computeSurfaceObject(imagePlusBinary,255);
		   double vCcMean = computeMeanOfTable(volumesObjects);
		   int nbCc = measure3D.getNbObject(imagePlusChromocenter);
		   if (rhfChoice.equals("Volume and intensity"))
		   {
			   IJ.log("ImageTitle Volume ESR SurfacicArea NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean Flatness Elongation Sphericity IntensityRHF VolumeRHF VoxelVolume");
			   IJ.log(imagePlusBinary.getTitle()+" "+volume+" "+measure3D.equivalentSphericalRadius(imagePlusRaw,255)+" "+surfacicArea
			  +" " +nbCc+" "+ vCcMean+" "+vCcMean*nbCc+" "+computeMeanOfTable(borderToBorderDistanceTable)
			  +" "+computeMeanOfTable(barycenterToBorderDistanceTable)+" "+measure3D.computeFlatnessObject(imagePlusRaw,255)+" "+measure3D.computeElongationObject(imagePlusRaw,255)+
			  " "+measure3D.computeSphericity(volume,surfacicArea)+" "+measure3D.computeRhfIntensite(imagePlusRaw,imagePlusBinary, imagePlusChromocenter)
			  +" "+measure3D.computeRhfVolume(imagePlusBinary, imagePlusChromocenter)+" "+voxelVolume);
		   }
	       else if (rhfChoice.equals("Volume"))
	       {
	    	   IJ.log("ImageTitle Volume ESR SurfacicArea NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean Flatness Elongation Sphericity VolumeRHF VoxelVolume");
			   IJ.log(imagePlusBinary.getTitle()+" "+volume+" "+measure3D.equivalentSphericalRadius(imagePlusRaw,255)+" "+surfacicArea
				  +" " +nbCc+" "+ vCcMean+" "+vCcMean*nbCc+" "+computeMeanOfTable(borderToBorderDistanceTable)
				  +" "+computeMeanOfTable(barycenterToBorderDistanceTable)+" "+measure3D.computeFlatnessObject(imagePlusRaw,255)+" "+measure3D.computeElongationObject(imagePlusRaw,255)+
				  " "+measure3D.computeSphericity(volume,surfacicArea)+" "+measure3D.computeRhfVolume(imagePlusBinary, imagePlusChromocenter)+" "+voxelVolume);
	       }
	       else  
	       {
	    	   IJ.log("ImageTitle Volume ESR SurfacicArea NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean Flatness Elongation Sphericity IntensityRHF VoxelVolume");
	    	   IJ.log(imagePlusBinary.getTitle()+" "+volume+" "+measure3D.equivalentSphericalRadius(imagePlusRaw,255)+" "+surfacicArea
				  +" " +nbCc+" "+ vCcMean+" "+vCcMean*nbCc+" "+computeMeanOfTable(borderToBorderDistanceTable)
				  +" "+computeMeanOfTable(barycenterToBorderDistanceTable)+" "+measure3D.computeFlatnessObject(imagePlusRaw,255)+" "+measure3D.computeElongationObject(imagePlusRaw,255)+
				  " "+measure3D.computeSphericity(volume,surfacicArea)+" "+measure3D.computeRhfIntensite(imagePlusRaw,imagePlusBinary, imagePlusChromocenter)+" "+voxelVolume);
			  }
	   }
	   

	   /**
	    * 
	    * @param pathFile
	    * @param rhfChoice
	    * @param imagePlusRaw
	    * @param imagePlusBinary
	    * @param imagePlusChromocenter
	    * @throws IOException
	    */
	   public void computeParameters (String pathFile ,String rhfChoice,ImagePlus imagePlusRaw, ImagePlus imagePlusBinary, ImagePlus imagePlusChromocenter) throws IOException
	   {
		   Calibration calibration = imagePlusRaw.getCalibration();
		   double voxelVolume = calibration.pixelDepth*calibration.pixelHeight*calibration.pixelWidth;
		   Measure3D measure3D = new Measure3D ();
		   Measure2D measure2D = new Measure2D ();
		   measure2D.run(imagePlusBinary);
		   double [] volumesObjects =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
		   RadialDistance radialDistance = new RadialDistance ();
		   double borderToBorderDistanceTable [] = radialDistance.computeBorderToBorderDistances(imagePlusChromocenter,imagePlusBinary);
		   double barycenterToBorderDistanceTable [] = radialDistance.computeBarycenterToBorderDistances (imagePlusBinary,imagePlusChromocenter);
		   double volume = measure3D.computeVolumeObject(imagePlusBinary,255);
		   double surfacicArea = measure3D.computeSurfaceObject(imagePlusBinary,255);
		   double vCcMean = computeMeanOfTable(volumesObjects);
		   int nbCc = measure3D.getNbObject(imagePlusChromocenter);
		   File fileResu = new File (pathFile);
		   boolean exist = fileResu.exists();
		   BufferedWriter output;	
		   if (exist)
		   {
			   FileWriter fw = new FileWriter(fileResu, true);
			   output = new BufferedWriter(fw);
			   if (rhfChoice.equals("Volume and intensity"))
		        {
				   output.write(imagePlusBinary.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusRaw,255)+"\t"+surfacicArea
							  +"\t" +nbCc+"\t"+ vCcMean+"\t"+vCcMean*nbCc+"\t"+computeMeanOfTable(borderToBorderDistanceTable)
							  +"\t"+computeMeanOfTable(barycenterToBorderDistanceTable)+"\t"+measure3D.computeFlatnessObject(imagePlusRaw,255)+" "+measure3D.computeElongationObject(imagePlusRaw,255)+
							  "\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfIntensite(imagePlusRaw,imagePlusBinary, imagePlusChromocenter)
							  +"\t"+measure3D.computeRhfVolume(imagePlusBinary, imagePlusChromocenter)+"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
		        }
			   else if (rhfChoice.equals("Volume"))
		        {
				   output.write(imagePlusBinary.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusRaw,255)+"\t"+surfacicArea
							  +"\t" +nbCc+"\t"+ vCcMean+"\t"+vCcMean*nbCc+"\t"+computeMeanOfTable(borderToBorderDistanceTable)
							  +"\t"+computeMeanOfTable(barycenterToBorderDistanceTable)+"\t"+measure3D.computeFlatnessObject(imagePlusRaw,255)+" "+measure3D.computeElongationObject(imagePlusRaw,255)+
							  "\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfVolume(imagePlusBinary, imagePlusChromocenter)
							  +"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
		        }
			   else  
				  {
				   output.write(imagePlusBinary.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusRaw,255)+"\t"+surfacicArea
							  +"\t" +nbCc+"\t"+ vCcMean+"\t"+vCcMean*nbCc+"\t"+computeMeanOfTable(borderToBorderDistanceTable)
							  +"\t"+computeMeanOfTable(barycenterToBorderDistanceTable)+"\t"+measure3D.computeFlatnessObject(imagePlusRaw,255)+" "+measure3D.computeElongationObject(imagePlusRaw,255)+
							  "\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfIntensite(imagePlusRaw,imagePlusBinary, imagePlusChromocenter)
							  +"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
				  }
		   }
		   
		   else
		   {
			   FileWriter fw = new FileWriter(fileResu, true);
			   output = new BufferedWriter(fw);
			   if (rhfChoice.equals("Volume and intensity"))
		        {

				   output.write("ImageTitle\tVolume\tESR\tSurfacicArea\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tFlatness\tElongation\tSphericity\tIntensityRHF\tVolumeRHF\tAspectRatio\tCircularity\tVoxelVolume\n"
						 +imagePlusBinary.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusRaw,255)+"\t"+surfacicArea
									  +"\t" +nbCc+"\t"+ vCcMean+"\t"+vCcMean*nbCc+"\t"+computeMeanOfTable(borderToBorderDistanceTable)
									  +"\t"+computeMeanOfTable(barycenterToBorderDistanceTable)+"\t"+measure3D.computeFlatnessObject(imagePlusRaw,255)+" "+measure3D.computeElongationObject(imagePlusRaw,255)+
									  "\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfIntensite(imagePlusRaw,imagePlusBinary, imagePlusChromocenter)
									  +"\t"+measure3D.computeRhfVolume(imagePlusBinary, imagePlusChromocenter)+"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
		        }
			   else if (rhfChoice.equals("Volume"))
		        {
				   output.write("ImageTitle\tVolume\tESR\tSurfacicArea\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tFlatness\tElongation\tSphericity\tVolumeRHF\tAspectRatio\tCircularity\tVoxelVolume\n"
							 +imagePlusBinary.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusRaw,255)+"\t"+surfacicArea
										  +"\t" +nbCc+"\t"+ vCcMean+"\t"+vCcMean*nbCc+"\t"+computeMeanOfTable(borderToBorderDistanceTable)
										  +"\t"+computeMeanOfTable(barycenterToBorderDistanceTable)+"\t"+measure3D.computeFlatnessObject(imagePlusRaw,255)+" "+measure3D.computeElongationObject(imagePlusRaw,255)+
										  "\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfVolume(imagePlusBinary, imagePlusChromocenter)+"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
		        }
			   else  
				  {
				   output.write("ImageTitle\tVolume\tESR\tSurfacicArea\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tFlatness\tElongation\tSphericity\tIntensityRHF\tAspectRatio\tCircularity\tVoxelVolume\n"
							 +imagePlusBinary.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusRaw,255)+"\t"+surfacicArea
										  +"\t" +nbCc+"\t"+ vCcMean+"\t"+vCcMean*nbCc+"\t"+computeMeanOfTable(borderToBorderDistanceTable)
										  +"\t"+computeMeanOfTable(barycenterToBorderDistanceTable)+"\t"+measure3D.computeFlatnessObject(imagePlusRaw,255)+" "+measure3D.computeElongationObject(imagePlusRaw,255)+
										  "\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfIntensite(imagePlusRaw,imagePlusBinary, imagePlusChromocenter)
										  +"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
				  }
		   } 
		   output.flush();
		   output.close();   
	   }
	   

		/**
		 * Method wich compute the mean of the value in the table
		 * @param tabInput Table of value
		 * @return Mean of the table
		 */

	  
		public double computeMeanOfTable (double tabInput[])
		{
			int i;
			double mean = 0;
			for (i = 0; i < tabInput.length; ++i)  mean += tabInput[i];
			mean = mean / (tabInput.length);
			return mean;
		}//computeMeanOfTable
}
