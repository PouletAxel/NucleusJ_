package gred.nucleus.plugins;

import gred.nucleus.parameters.SapheParameters2D;
import gred.nucleus.utilitaires.Histogram;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


/**
 *
 * @author gred
 */
public class ComputeParameters2D_ implements PlugIn
{
   /** image to process*/
  ImagePlus _imagePlus;
  /** output file to stock the computed parameters*/
  String _outFile;
  /** Boolean to test if the image contains only one object*/
  boolean _oneObject = true;
  /** Aspect ratio variable*/
  double _AR;
  /** circularity variable*/
  double _circ;
 

  /**
   * Plugin charge a image on input on imagej and compute the
   *  cicularity and sphericity on the stack with the greatest area. And stock this two parameters in file
   */
  public void run(String arg)
  {
    _imagePlus = WindowManager.getCurrentImage();
    if (null == _imagePlus)
    {
       IJ.noImage();
       return;
    }
    else if (_imagePlus.getStackSize() == 1)
    {
        IJ.error("image format", "No images in 3D");
        return;
    }
    if (IJ.versionLessThan("1.32c"))   return;
    _outFile = IJ.getFilePath("fichier de sortie");
    try {	computeParameters();}
    catch (IOException e) {	e.printStackTrace(); }
  } 
    
  /**
  * Method which compute diffrents parameters of shape (sphericity, flataness and
  * elongation) and parameters of lenght (volume and equivalent spherique radius)
  * Take in input the path of results files output.
  *
  * @param pathFile path of output filesortis
  * @throws IOException
  */

  public void computeParameters() throws IOException
  {
	  Histogram hist = new Histogram(_imagePlus);
	  HashMap<Double , Integer> hHisto = hist.getHisto();
	  if (hHisto.size() == 1 )
	  {
		  SapheParameters2D gp2d = new SapheParameters2D (_imagePlus);
		  _AR = gp2d.getAspectRatio();
		  _circ = gp2d.getCirculairty();
		  File fileResu = new File (_outFile);
		  boolean exist = fileResu.exists();
		  BufferedWriter output;
		  if (exist)
		  {
			  FileWriter fw = new FileWriter(fileResu, true);
			  output = new BufferedWriter(fw);
			  output.write(_imagePlus.getTitle()+"\t"+_AR+"\t"+_circ+"\n");
		  }
		  else
		  {
			  FileWriter fw = new FileWriter(fileResu, true);
			  output = new BufferedWriter(fw);
			  output.write("NucleusFileName\tAspectRatio\tCircularity\n"+_imagePlus.getTitle()+"\t"+_AR+"\t"+_circ+"\n");
		  } 
				output.flush();
				output.close();   
		}
		else {IJ.showMessage("there are no object in your image!!!!!!!!!!!!!!!");}
  }  
}

