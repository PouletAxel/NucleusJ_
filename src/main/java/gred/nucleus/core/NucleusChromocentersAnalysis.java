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
		double [] tbarycenterToBorderDistance = radialDistance.computeBarycenterToBorderDistances (imagePlusSegmented,imagePlusChromocenter);
		IJ.log("3D PARAMETERS ");
		double volume = measure3D.computeVolumeObject(imagePlusSegmented,255);
		double surfacicArea = measure3D.computeSurfaceObject(imagePlusSegmented,255);
		double volumeCcMean = computeMeanOfTable(tVolumesObjects);
		int nbCc = measure3D.getNumberOfObject(imagePlusChromocenter);
		if (rhfChoice.equals("Volume and intensity"))
		{	
			IJ.log("ImageTitle Volume ESR SurfacicArea NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean Flatness Elongation Sphericity IntensityRHF VolumeRHF VoxelVolume");
			IJ.log(imagePlusSegmented.getTitle()+" "+volume+" "+measure3D.equivalentSphericalRadius(imagePlusInput,255)+" "+surfacicArea  +" " +nbCc+" "+ volumeCcMean+" "+volumeCcMean*nbCc+" "+computeMeanOfTable(tBorderToBorderDistance)
			  +" "+computeMeanOfTable(tbarycenterToBorderDistance)+" "+measure3D.computeFlatnessObject(imagePlusInput,255)+" "+measure3D.computeElongationObject(imagePlusInput,255)+
			  " "+measure3D.computeSphericity(volume,surfacicArea)+" "+measure3D.computeRhfIntensite(imagePlusInput,imagePlusSegmented, imagePlusChromocenter)
			 +" "+measure3D.computeRhfVolume(imagePlusSegmented, imagePlusChromocenter)+" "+voxelVolume);
		}
		else if (rhfChoice.equals("Volume"))
		{
			IJ.log("ImageTitle Volume ESR SurfacicArea NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean Flatness Elongation Sphericity VolumeRHF VoxelVolume");
			IJ.log(imagePlusSegmented.getTitle()+" "+volume+" "+measure3D.equivalentSphericalRadius(imagePlusInput,255)+" "+surfacicArea
			+" " +nbCc+" "+ volumeCcMean+" "+volumeCcMean*nbCc+" "+computeMeanOfTable(tBorderToBorderDistance)
			+" "+computeMeanOfTable(tbarycenterToBorderDistance)+" "+measure3D.computeFlatnessObject(imagePlusInput,255)+" "+measure3D.computeElongationObject(imagePlusInput,255)+
			" "+measure3D.computeSphericity(volume,surfacicArea)+" "+measure3D.computeRhfVolume(imagePlusSegmented, imagePlusChromocenter)+" "+voxelVolume);
		}
		else  
		{
			IJ.log("ImageTitle Volume ESR SurfacicArea NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean Flatness Elongation Sphericity IntensityRHF VoxelVolume");
			IJ.log(imagePlusSegmented.getTitle()+" "+volume+" "+measure3D.equivalentSphericalRadius(imagePlusInput,255)+" "+surfacicArea
			+" " +nbCc+" "+ volumeCcMean+" "+volumeCcMean*nbCc+" "+computeMeanOfTable(tBorderToBorderDistance)
			+" "+computeMeanOfTable(tbarycenterToBorderDistance)+" "+measure3D.computeFlatnessObject(imagePlusInput,255)+" "+measure3D.computeElongationObject(imagePlusInput,255)+
			" "+measure3D.computeSphericity(volume,surfacicArea)+" "+measure3D.computeRhfIntensite(imagePlusInput,imagePlusSegmented, imagePlusChromocenter)+" "+voxelVolume);
		}
	}
	   

	/**
	 * 
	 * @param pathFile
	 * @param rhfChoice
	 * @param imagePlusInput
	 * @param imagePlusSegmented
	 * @param imagePlusChromocenter
	 * @throws IOException
	 */
	public void computeParameters (String pathFile ,String rhfChoice,ImagePlus imagePlusInput, ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter) throws IOException
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
		double surfacicArea = measure3D.computeSurfaceObject(imagePlusSegmented,255);
		double volumeCcMean = computeMeanOfTable(tVolumesObjects);
		int nbCc = measure3D.getNumberOfObject(imagePlusChromocenter);
		File fileResu = new File(pathFile);
		boolean exist = fileResu.exists();
		BufferedWriter bufferedWriterOutput;	
		if (exist)
		{
			FileWriter fileWriter = new FileWriter(fileResu, true);
			bufferedWriterOutput = new BufferedWriter(fileWriter);
			if (rhfChoice.equals("Volume and intensity"))
			{
				bufferedWriterOutput.write(imagePlusSegmented.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusInput,255)+"\t"+surfacicArea
				+"\t" +nbCc+"\t"+ volumeCcMean+"\t"+volumeCcMean*nbCc+"\t"+computeMeanOfTable(tBorderToBorderDistance)
				+"\t"+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"+measure3D.computeFlatnessObject(imagePlusInput,255)+" "+measure3D.computeElongationObject(imagePlusInput,255)+
				"\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfIntensite(imagePlusInput,imagePlusSegmented, imagePlusChromocenter)
				+"\t"+measure3D.computeRhfVolume(imagePlusSegmented, imagePlusChromocenter)+"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
			}
			else if (rhfChoice.equals("Volume"))
			{
				bufferedWriterOutput.write(imagePlusSegmented.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusInput,255)+"\t"+surfacicArea
				+"\t" +nbCc+"\t"+ volumeCcMean+"\t"+volumeCcMean*nbCc+"\t"+computeMeanOfTable(tBorderToBorderDistance)
				+"\t"+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"+measure3D.computeFlatnessObject(imagePlusInput,255)+" "+measure3D.computeElongationObject(imagePlusInput,255)+
				"\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfVolume(imagePlusSegmented, imagePlusChromocenter)
				+"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
			}
			else  
			{
				bufferedWriterOutput.write(imagePlusSegmented.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusInput,255)+"\t"+surfacicArea
				+"\t" +nbCc+"\t"+ volumeCcMean+"\t"+volumeCcMean*nbCc+"\t"+computeMeanOfTable(tBorderToBorderDistance)
				+"\t"+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"+measure3D.computeFlatnessObject(imagePlusInput,255)+" "+measure3D.computeElongationObject(imagePlusInput,255)+
				"\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfIntensite(imagePlusInput,imagePlusSegmented, imagePlusChromocenter)
				+"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
			}
		}
		else
		{
			FileWriter fileWriter = new FileWriter(fileResu, true);
			bufferedWriterOutput = new BufferedWriter(fileWriter);
			if (rhfChoice.equals("Volume and intensity"))
			{
				
				bufferedWriterOutput.write("ImageTitle\tVolume\tESR\tSurfacicArea\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tFlatness\tElongation\tSphericity\tIntensityRHF\tVolumeRHF\tAspectRatio\tCircularity\tVoxelVolume\n"
				+imagePlusSegmented.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusInput,255)+"\t"+surfacicArea
				+"\t" +nbCc+"\t"+ volumeCcMean+"\t"+volumeCcMean*nbCc+"\t"+computeMeanOfTable(tBorderToBorderDistance)
				+"\t"+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"+measure3D.computeFlatnessObject(imagePlusInput,255)+" "+measure3D.computeElongationObject(imagePlusInput,255)+
				"\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfIntensite(imagePlusInput,imagePlusSegmented, imagePlusChromocenter)
				+"\t"+measure3D.computeRhfVolume(imagePlusSegmented, imagePlusChromocenter)+"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
			}
			else if (rhfChoice.equals("Volume"))
			{
				bufferedWriterOutput.write("ImageTitle\tVolume\tESR\tSurfacicArea\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tFlatness\tElongation\tSphericity\tVolumeRHF\tAspectRatio\tCircularity\tVoxelVolume\n"
				+imagePlusSegmented.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusInput,255)+"\t"+surfacicArea
				+"\t" +nbCc+"\t"+ volumeCcMean+"\t"+volumeCcMean*nbCc+"\t"+computeMeanOfTable(tBorderToBorderDistance)
				+"\t"+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"+measure3D.computeFlatnessObject(imagePlusInput,255)+" "+measure3D.computeElongationObject(imagePlusInput,255)+
				"\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfVolume(imagePlusSegmented, imagePlusChromocenter)+"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
			}
			else  
			{
				bufferedWriterOutput.write("ImageTitle\tVolume\tESR\tSurfacicArea\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tFlatness\tElongation\tSphericity\tIntensityRHF\tAspectRatio\tCircularity\tVoxelVolume\n"
				+imagePlusSegmented.getTitle()+"\t"+volume+"\t"+measure3D.equivalentSphericalRadius(imagePlusInput,255)+"\t"+surfacicArea
				+"\t" +nbCc+"\t"+ volumeCcMean+"\t"+volumeCcMean*nbCc+"\t"+computeMeanOfTable(tBorderToBorderDistance)
				+"\t"+computeMeanOfTable(tBarycenterToBorderDistance)+"\t"+measure3D.computeFlatnessObject(imagePlusInput,255)+" "+measure3D.computeElongationObject(imagePlusInput,255)+
				"\t"+measure3D.computeSphericity(volume,surfacicArea)+"\t"+measure3D.computeRhfIntensite(imagePlusInput,imagePlusSegmented, imagePlusChromocenter)
				+"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\t"+voxelVolume+"\n");
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

	  
	public double computeMeanOfTable (double tInput[])
	{
		int i;
		double mean = 0;
		for (i = 0; i < tInput.length; ++i)	mean += tInput[i];
		mean = mean / (tInput.length);
		return mean;
	}
}
