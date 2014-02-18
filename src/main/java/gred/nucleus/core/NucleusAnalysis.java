package gred.nucleus.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ij.IJ;
import ij.ImagePlus;


/**
 * 
 * @author gred
 *
 */

public class NucleusAnalysis
{
	 @SuppressWarnings("unused")
	private static class IOEception {  public IOEception() { } }


	  /**
	   * Constructor
	   *
	   * @param imagePlusInput binary image
	   */

	  public NucleusAnalysis ()	  {}

	 /**
	  * Method which compute diffrents parameters of shape (sphericity, flataness and
	  * elongation) and parameters of lenght (volume and equivalent spherique radius)
	  * Take in input the path of results files output.
	  *
	  * @param pathFile path of output 
	  * @throws IOException
	  */

	  public void nucleusParameter3D (String pathFile, ImagePlus imagePlusInput) throws IOException
	  {
		  Measure3D measure3D = new Measure3D ();
		  File fileResu = new File (pathFile);
		  boolean exist = fileResu.exists();
		  BufferedWriter output;
		  double volume = measure3D.computeVolumeObject(imagePlusInput,255);
		  double surfacicArea = measure3D.computeSurfaceObject(imagePlusInput,255);
		  if (exist)
		  {
			  FileWriter fw = new FileWriter(fileResu, true);
		      output = new BufferedWriter(fw);
		      output.write(imagePlusInput.getTitle()+"\t"+measure3D.computeVolumeObject(imagePlusInput,255)+"\t"+measure3D.computeFlatnessObject(imagePlusInput,255)
		    		  +"\t"+ measure3D.computeElongationObject(imagePlusInput,255)+"\t"+measure3D.computeSphericity(volume, surfacicArea)
		    		  +"\t"+measure3D.equivalentSphericalRadius(imagePlusInput,255)+"\t"+surfacicArea+"\n");
		  }
		  else
		  {
			  FileWriter fw = new FileWriter(fileResu, true);
		      output = new BufferedWriter(fw);
		      output.write("NucleusFileName\tVolume\tFlatness\tElongation\tSphericity\tEsr\tSurfacicArea\t\n"+
		    		  imagePlusInput.getTitle()+"\t"+measure3D.computeVolumeObject(imagePlusInput,255)+"\t"+measure3D.computeFlatnessObject(imagePlusInput,255)+"\t"+
		    		  measure3D.computeElongationObject(imagePlusInput,255)+"\t"+measure3D.computeSphericity(volume, surfacicArea)
		    		  +"\t"+measure3D.equivalentSphericalRadius(imagePlusInput,255)+"\t"+surfacicArea+"\n");
		  } 
		  output.flush();
		  output.close();   
	  }
	  
	  /**
	   * 
	   */
	  public void nucleusParameter3D (ImagePlus imagePlusInput)
	  {
		  Measure3D measure3D = new Measure3D ();
		  double volume = measure3D.computeVolumeObject(imagePlusInput,255);
		  double surfacicArea = measure3D.computeSurfaceObject(imagePlusInput,255);
		  
		  IJ.log("3D parameters");
		  IJ.log("NucleusFileName Volume Flatness Elongation Sphericity Esr SurfacicArea");
		  IJ.log(imagePlusInput.getTitle()+" "+measure3D.computeVolumeObject(imagePlusInput,255)+" "+measure3D.computeFlatnessObject(imagePlusInput,255)
	    		  +" "+ measure3D.computeElongationObject(imagePlusInput,255)+" "+measure3D.computeSphericity(volume, surfacicArea)
	    		  +" "+measure3D.equivalentSphericalRadius(imagePlusInput,255)+" "+surfacicArea+"\n");
	  }

	  /**
	   * 
	   * @param pathFile
	   * @throws IOException
	   */
	  public void nucleusParameter2D (String pathFile,ImagePlus imagePlusInput) throws IOException
	  {
		  Measure2D measure2D = new Measure2D ();
		  measure2D.run(imagePlusInput);
		  File fileResu = new File (pathFile);
		  boolean exist = fileResu.exists();
		  BufferedWriter output;
		  if (exist)
		  {
			  FileWriter fw = new FileWriter(fileResu, true);
		      output = new BufferedWriter(fw);
		      output.write(imagePlusInput.getTitle()+"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\n");
		  }
		  else
		  {
			  FileWriter fw = new FileWriter(fileResu, true);
		      output = new BufferedWriter(fw);
		      output.write("ImageTitle\tAspectRatio\tCircularity\n"+
		    		  imagePlusInput.getTitle()+"\t"+measure2D.getAspectRatio()+"\t"+measure2D.getCirculairty()+"\n");
		  } 
		  output.flush();
		  output.close();   
	  }
	  

	  /**
	   * 
	   * @param imagePlusInput
	   */
	  public void nucleusParameter2D (ImagePlus imagePlusInput)
	  {
		  Measure2D measure2D = new Measure2D ();
		  measure2D.run(imagePlusInput);
		  IJ.log("2D parameters");
		  IJ.log("ImageTitle AspectRatio Circularity");
		  IJ.log(imagePlusInput.getTitle()+" "+measure2D.getAspectRatio()+" "+measure2D.getCirculairty());
	  }
}
