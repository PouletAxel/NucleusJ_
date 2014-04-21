package gred.nucleus.core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;

/**
 * 
 * @author Poulet Axel
 *
 */
public class NucleusChromocentersAnalysis
{
	public NucleusChromocentersAnalysis ()  { }

	   /**
	    * 
	    * @param rhfChoice
	    * @param imagePlusInput
	    * @param imagePlusSegmented
	    * @param imagePlusChromocenter
	    */
	public void computeParameters (String rhfChoice, ImagePlus imagePlusInput, ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter)
	{
		Calibration calibration = imagePlusInput.getCalibration();
		double voxelVolume = calibration.pixelDepth*calibration.pixelHeight*calibration.pixelWidth;
		Measure3D measure3D = new Measure3D();
		double [] tVolumesObjects =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
		RadialDistance radialDistance = new RadialDistance();
		double [] tBorderToBorderDistance = radialDistance.computeBorderToBorderDistances(imagePlusSegmented,imagePlusChromocenter);
		double [] tBarycenterToBorderDistance = radialDistance.computeBarycenterToBorderDistances (imagePlusSegmented,imagePlusChromocenter);
		IJ.log("3D PARAMETERS ");
		double volume = measure3D.computeVolumeObject(imagePlusSegmented,255);
		double surfaceArea = measure3D.computeSurfaceObject(imagePlusSegmented,255);
		double volumeCcMean = computeMeanOfTable(tVolumesObjects);
		int nbCc = measure3D.getNumberOfObject(imagePlusChromocenter);
		if (rhfChoice.equals("Volume and intensity"))
		{	
			IJ.log("ImageTitle Volume ESR SurfacicArea NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean Flatness Elongation Sphericity IntensityRHF VolumeRHF VoxelVolume");
			IJ.log
			(
					imagePlusSegmented.getTitle()+" "
					+volume+" "
					+measure3D.equivalentSphericalRadius(imagePlusSegmented,255)+" "
					+surfaceArea +" "
					+nbCc+" "
					+volumeCcMean+" "
					+volumeCcMean*nbCc+" "
					+computeMeanOfTable(tBorderToBorderDistance)+" "
					+computeMeanOfTable(tBarycenterToBorderDistance)+" "
					+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[0]+" "
		    		+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[1]+" "
					+measure3D.computeSphericity(volume,surfaceArea)+" "
					+measure3D.computeIntensityRHF(imagePlusInput,imagePlusSegmented,imagePlusChromocenter)+" "
					+measure3D.computeVolumeRHF(imagePlusSegmented, imagePlusChromocenter)+" "
					+voxelVolume
			);
		}
		else if (rhfChoice.equals("Volume"))
		{
			IJ.log("ImageTitle Volume ESR SurfacicArea NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean Flatness Elongation Sphericity VolumeRHF VoxelVolume");
			IJ.log
			(
					imagePlusSegmented.getTitle()+" "
					+volume+" "
					+measure3D.equivalentSphericalRadius(imagePlusSegmented,255)+" "
					+surfaceArea+" "
					+nbCc+" "
					+volumeCcMean+" "
					+volumeCcMean*nbCc+" "
					+computeMeanOfTable(tBorderToBorderDistance)+" "
					+computeMeanOfTable(tBarycenterToBorderDistance)+" "
					 +measure3D.computeFlatnessAndElongation(imagePlusInput,255)[0]+" "
		    		  +measure3D.computeFlatnessAndElongation(imagePlusInput,255)[1]+" "
					+measure3D.computeSphericity(volume,surfaceArea)+" "
					+measure3D.computeVolumeRHF(imagePlusSegmented, imagePlusChromocenter)+" "
					+voxelVolume
			);
		}
		else  
		{
			IJ.log("ImageTitle Volume ESR SurfacicArea NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean Flatness Elongation Sphericity IntensityRHF VoxelVolume");
			IJ.log
			(
					imagePlusSegmented.getTitle()+" "
					+volume+" "
					+measure3D.equivalentSphericalRadius(imagePlusSegmented,255)+" "
					+surfaceArea+" "
					+nbCc+" "
					+volumeCcMean+" "
					+volumeCcMean*nbCc+" "
					+computeMeanOfTable(tBorderToBorderDistance)+" "
					+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[0]+" "
		    		+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[1]+" "
					+measure3D.computeSphericity(volume,surfaceArea)+" "
					+measure3D.computeIntensityRHF(imagePlusInput,imagePlusSegmented, imagePlusChromocenter)+" "
					+voxelVolume
			);
		}
	}
	   

	/**
	 * 
	 * @param pathResultsFile
	 * @param rhfChoice
	 * @param imagePlusInput
	 * @param imagePlusSegmented
	 * @param imagePlusChromocenter
	 * @throws IOException
	 */
	public void computeParameters (String pathResultsFile ,String rhfChoice,ImagePlus imagePlusInput, ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter) throws IOException
	{
		Calibration calibration = imagePlusInput.getCalibration();
		double voxelVolume = calibration.pixelDepth*calibration.pixelHeight*calibration.pixelWidth;
		Measure3D measure3D = new Measure3D();
		Measure2D measure2D = new Measure2D();
		measure2D.run(imagePlusSegmented);
		double [] tVolumesObjects =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
		RadialDistance radialDistance = new RadialDistance ();
		double [] tBorderToBorderDistance = radialDistance.computeBorderToBorderDistances(imagePlusSegmented,imagePlusChromocenter);
		double [] tBarycenterToBorderDistance = radialDistance.computeBarycenterToBorderDistances (imagePlusSegmented,imagePlusChromocenter);
		double volume = measure3D.computeVolumeObject(imagePlusSegmented,255);
		double surfaceArea = measure3D.computeSurfaceObject(imagePlusSegmented,255);
		double volumeCcMean = computeMeanOfTable(tVolumesObjects);
		int nbCc = measure3D.getNumberOfObject(imagePlusChromocenter);
		File fileResults = new File(pathResultsFile);
		boolean exist = fileResults.exists();
		BufferedWriter bufferedWriterOutput;	
		if (exist)
		{
			FileWriter fileWriter = new FileWriter(fileResults, true);
			bufferedWriterOutput = new BufferedWriter(fileWriter);
			if (rhfChoice.equals("Volume and intensity"))
			{
				bufferedWriterOutput.write
				(
						imagePlusSegmented.getTitle()+"\t"
						+volume+"\t"
						+measure3D.equivalentSphericalRadius(imagePlusSegmented,255)+"\t"
						+surfaceArea+"\t"
						+nbCc+"\t"
						+volumeCcMean+"\t"
						+volumeCcMean*nbCc+"\t"
						+computeMeanOfTable(tBorderToBorderDistance)+"\t"
						+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"
						+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[0]+"\t"
			    		+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[1]+"\t"
						+measure3D.computeSphericity(volume,surfaceArea)+"\t"
						+measure3D.computeIntensityRHF(imagePlusInput,imagePlusSegmented, imagePlusChromocenter)+"\t"
						+measure3D.computeVolumeRHF(imagePlusSegmented, imagePlusChromocenter)+"\t"
						+measure2D.getAspectRatio()+"\t"
						+measure2D.getCirculairty()+"\t"
						+voxelVolume+"\n"
				);
			}
			else if (rhfChoice.equals("Volume"))
			{
				bufferedWriterOutput.write
				(
						imagePlusSegmented.getTitle()+"\t"
						+volume+"\t"
						+measure3D.equivalentSphericalRadius(imagePlusSegmented,255)+"\t"
						+surfaceArea+"\t"
						+nbCc+"\t"
						+volumeCcMean+"\t"
						+volumeCcMean*nbCc+"\t"
						+computeMeanOfTable(tBorderToBorderDistance)+"\t"
						+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"
						+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[0]+"\t"
			    		+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[1]+"\t"
						+measure3D.computeSphericity(volume,surfaceArea)+"\t"
						+measure3D.computeVolumeRHF(imagePlusSegmented, imagePlusChromocenter)+"\t"
						+measure2D.getAspectRatio()+"\t"
						+measure2D.getCirculairty()+"\t"
						+voxelVolume+"\n"
				);
			}
			else  
			{
				bufferedWriterOutput.write
				(
						imagePlusSegmented.getTitle()+"\t"
						+volume+"\t"
						+measure3D.equivalentSphericalRadius(imagePlusSegmented,255)+"\t"
						+surfaceArea+"\t"
						+nbCc+"\t"
						+volumeCcMean+"\t"
						+volumeCcMean*nbCc+"\t"
						+computeMeanOfTable(tBorderToBorderDistance)+"\t"
						+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"
						+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[0]+"\t"
			    		+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[1]+"\t"
						+measure3D.computeSphericity(volume,surfaceArea)+"\t"
						+measure3D.computeIntensityRHF(imagePlusInput,imagePlusSegmented, imagePlusChromocenter)+"\t"
						+measure2D.getAspectRatio()+"\t"
						+measure2D.getCirculairty()+"\t"
						+voxelVolume+"\n"
				);
			}
		}
		else
		{
			FileWriter fileWriter = new FileWriter(fileResults, true);
			bufferedWriterOutput = new BufferedWriter(fileWriter);
			if (rhfChoice.equals("Volume and intensity"))
			{
				
				bufferedWriterOutput.write
				(
						"ImageTitle\tVolume\tESR\tSurfacicArea\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tFlatness\tElongation\tSphericity\tIntensityRHF\tVolumeRHF\tAspectRatio\tCircularity\tVoxelVolume\n"
						+imagePlusSegmented.getTitle()+"\t"
						+volume+"\t"
						+measure3D.equivalentSphericalRadius(imagePlusSegmented,255)+"\t"
						+surfaceArea+"\t"
						+nbCc+"\t"
						+volumeCcMean+"\t"
						+volumeCcMean*nbCc+"\t"
						+computeMeanOfTable(tBorderToBorderDistance)+"\t"
						+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"
						+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[0]+"\t"
			    		+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[1]+"\t"
						+measure3D.computeSphericity(volume,surfaceArea)+"\t"
						+measure3D.computeIntensityRHF(imagePlusInput,imagePlusSegmented, imagePlusChromocenter)+"\t"
						+measure3D.computeVolumeRHF(imagePlusSegmented, imagePlusChromocenter)+"\t"
						+measure2D.getAspectRatio()+"\t"
						+measure2D.getCirculairty()+"\t"
						+voxelVolume+"\n"
				);
			}
			else if (rhfChoice.equals("Volume"))
			{
				bufferedWriterOutput.write
				(
						"ImageTitle\tVolume\tESR\tSurfacicArea\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tFlatness\tElongation\tSphericity\tVolumeRHF\tAspectRatio\tCircularity\tVoxelVolume\n"
						+imagePlusSegmented.getTitle()+"\t"
						+volume+"\t"
						+measure3D.equivalentSphericalRadius(imagePlusSegmented,255)+"\t"
						+surfaceArea+"\t"
						+nbCc+"\t"
						+volumeCcMean+"\t"
						+volumeCcMean*nbCc+"\t"
						+computeMeanOfTable(tBorderToBorderDistance)+"\t"
						+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"
						+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[0]+"\t"
			    		+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[1]+"\t"
						+measure3D.computeSphericity(volume,surfaceArea)+"\t"
						+measure3D.computeVolumeRHF(imagePlusSegmented, imagePlusChromocenter)+"\t"
						+measure2D.getAspectRatio()+"\t"
						+measure2D.getCirculairty()+"\t"
						+voxelVolume+"\n"
				);
			}
			else  
			{
				bufferedWriterOutput.write
				(
						"ImageTitle\tVolume\tESR\tSurfacicArea\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tFlatness\tElongation\tSphericity\tIntensityRHF\tAspectRatio\tCircularity\tVoxelVolume\n"
						+imagePlusSegmented.getTitle()+"\t"
						+volume+"\t"
						+measure3D.equivalentSphericalRadius(imagePlusSegmented,255)+"\t"
						+surfaceArea+"\t"
						+nbCc+"\t"
						+volumeCcMean+"\t"
						+volumeCcMean*nbCc+"\t"
						+computeMeanOfTable(tBorderToBorderDistance)+"\t"
						+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"
						+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[0]+"\t"
			    		+measure3D.computeFlatnessAndElongation(imagePlusInput,255)[1]+"\t"
						+measure3D.computeSphericity(volume,surfaceArea)+"\t"
						+measure3D.computeIntensityRHF(imagePlusInput,imagePlusSegmented, imagePlusChromocenter)+"\t"
						+measure2D.getAspectRatio()+"\t"
						+measure2D.getCirculairty()+"\t"
						+voxelVolume+"\n"
				);
			}
		} 
		bufferedWriterOutput.flush();
		bufferedWriterOutput.close();   
	}	 

	/**
	 * Method wich compute the mean of the value in the table
	 * @param tInput Table of value
	 * @return Mean of the table
	 */

	  
	public double computeMeanOfTable (double [] tInput)
	{
		double mean = 0;
		for (int i = 0; i < tInput.length; ++i)
			mean += tInput[i];
		mean = mean / (tInput.length);
		return mean;
	}
}