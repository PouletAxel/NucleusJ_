package gred.nucleus.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;


/**
 * 
 * @author gred
 *
 */
public class NucleusAnalysis
{
	 @SuppressWarnings("unused")
	private static class IOEception {  public IOEception() { } }

	  /** binary image */
	  ImagePlus _imagePlusBinary;
	  /** height, width, depth of image in pixel*/
	  int _width, _height, _depth;
	  /** Voxel calibration in µm*/
	  double _dimX, _dimY, _dimZ;
	  /** imageStack of binary image */
	  ImageStack _imageStackBinary  = new ImageStack();

	  /**
	   * Constructor
	   *
	   * @param imagePlusInput binary image
	   */

	  public NucleusAnalysis (ImagePlus imagePlusInput)	  {	    _imagePlusBinary = imagePlusInput;  }

	 /**
	  * Method which compute diffrents parameters of shape (sphericity, flataness and
	  * elongation) and parameters of lenght (volume and equivalent spherique radius)
	  * Take in input the path of results files output.
	  *
	  * @param pathFile path of output filesortis
	  * @throws IOException
	  */

	  public void nucleusParameter3D (String pathFile) throws IOException
	  {
		  Measure3D gp3d = new Measure3D (_imagePlusBinary,255);
		  ShapeParameters3D sp3d = new ShapeParameters3D (_imagePlusBinary,255); 
		  File fileResu = new File (pathFile);
		  boolean exist = fileResu.exists();
		  BufferedWriter output;
		  if (exist)
		  {
			  FileWriter fw = new FileWriter(fileResu, true);
		      output = new BufferedWriter(fw);
		      output.write(_imagePlusBinary.getTitle()+"\t"+gp3d.computeVolumeObject()+"\t"+sp3d.computeFlatnessObject()+"\t"+
		    		  sp3d.computeElongationObject()+"\t"+sp3d.computeSphericity()+"\t"+gp3d.equivalentSphericalRadius()+"\n");
		  }
		  else
		  {
			  FileWriter fw = new FileWriter(fileResu, true);
		      output = new BufferedWriter(fw);
		      output.write("NucleusFileName\tVolume\tFlatness\tElongation\tSphericity\tEsr\n"+
		    		  _imagePlusBinary.getTitle()+"\t"+gp3d.computeVolumeObject()+"\t"+sp3d.computeFlatnessObject()+"\t"+
		    		  sp3d.computeElongationObject()+"\t"+sp3d.computeSphericity()+"\t"+gp3d.equivalentSphericalRadius()+"\n");
		  } 
		  output.flush();
		  output.close();   
	  }
	  
	  /**
	   * 
	   */
	  public void nucleusParameter3D ()
	  {
		  Measure3D gp3d = new Measure3D (_imagePlusBinary,255);
		  ShapeParameters3D sp3d = new ShapeParameters3D (_imagePlusBinary,255);
		 
		  
		  IJ.log("3D parameters");
		  IJ.log("ImageTitle Volume Flatness Elongation Sphericity Esr");
		  IJ.log(_imagePlusBinary.getTitle()+" "+gp3d.computeVolumeObject()+" "+sp3d.computeFlatnessObject()+" "+sp3d.computeElongationObject()
				  +" "+sp3d.computeSphericity()+" "+gp3d.equivalentSphericalRadius());
	  }

	  /**
	   * 
	   * @param pathFile
	   * @throws IOException
	   */
	  public void nucleusParameter2D (String pathFile) throws IOException
	  {
		  SapheParameters2D sp2d = new SapheParameters2D (_imagePlusBinary);
		  File fileResu = new File (pathFile);
		  boolean exist = fileResu.exists();
		  BufferedWriter output;
		  if (exist)
		  {
			  FileWriter fw = new FileWriter(fileResu, true);
		      output = new BufferedWriter(fw);
		      output.write(_imagePlusBinary.getTitle()+"\t"+sp2d.getAspectRatio()+"\t"+sp2d.getCirculairty()+"\n");
		  }
		  else
		  {
			  FileWriter fw = new FileWriter(fileResu, true);
		      output = new BufferedWriter(fw);
		      output.write("ImageTitle\tAspectRatio\tCircularity\n"+
		    		  _imagePlusBinary.getTitle()+"\t"+sp2d.getAspectRatio()+"\t"+sp2d.getCirculairty()+"\n");
		  } 
		  output.flush();
		  output.close();   
	  }
	  
	  /**
	   * 
	   */
	  public void nucleusParameter2D ()
	  {
		  SapheParameters2D sp2d = new SapheParameters2D (_imagePlusBinary);
		  IJ.log("2D parameters");
		  IJ.log("ImageTitle AspectRatio Circularity");
		  IJ.log(_imagePlusBinary.getTitle()+" "+sp2d.getAspectRatio()+" "+sp2d.getCirculairty());
	  }
}
