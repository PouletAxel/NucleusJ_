package gred.nucleus.plugins;
import gred.nucleus.parameters.*;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gred
 */
public class ComputeParemeter3D_ implements PlugIn
{
	/** image to process*/
	ImagePlus _imagePlus;
	/** */
	String _outFile;
	/** */
	boolean _oneObject = true;
	/** */
	double _flatness;
	/** */
	double _elongation;
	/** */
	double _volume;
	/** */
	double _sphericity;
	/** */
	double _esr;
	/** */
	double _surfacicArea;  
 
	@Override
	public void run(String arg)
	{
		_imagePlus = WindowManager.getCurrentImage();
		IJ.log(_imagePlus.getTitle());
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
		try {       computeParameters(); }
		catch (IOException ex) { Logger.getLogger(ComputeParemeter3D_.class.getName()).log(Level.SEVERE, null, ex); }
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
		if (hHisto.size() >= 1 )
		{
			double [] temp = hist.getLabel();
			
			for (int i = 0; i < temp.length; ++i)
			{
				GeometricParameters3D gp3d = new GeometricParameters3D (_imagePlus,temp[i]);
				ShapeParameters3D sp3d = new ShapeParameters3D (_imagePlus,temp[i]);
				_volume = gp3d.computeVolumeObject();
				_esr = gp3d.equivalentSphericalRadius();
				_surfacicArea = gp3d.computeSurfaceObject();
				_elongation = sp3d.computeElongationObject();
				_flatness = sp3d.computeFlatnessObject();
				_sphericity = sp3d.computeSphericity();
				File fileResu = new File (_outFile);
				boolean exist = fileResu.exists();
				BufferedWriter output;
				if (exist)
				{
					FileWriter fw = new FileWriter(fileResu, true);
					output = new BufferedWriter(fw);
					if (temp.length == 1) output.write(_imagePlus.getTitle()+"\t"+_volume+"\t"+_sphericity+"\t"+_flatness+"\t"+_elongation+"\n");
					else output.write(_imagePlus.getTitle()+""+i+"\t"+_volume+"\t"+_sphericity+"\t"+_flatness+"\t"+_elongation+"\n");
 				}
				else
				{
					FileWriter fw = new FileWriter(fileResu, true);
					output = new BufferedWriter(fw);
					output.write("ImageTitle\tVolume\tsphericity\tflatness\telongation\n");
					if (temp.length == 1) output.write(_imagePlus.getTitle()+"\t"+_volume+"\t"+_sphericity+"\t"+_flatness+"\t"+_elongation+"\n");
					else output.write(_imagePlus.getTitle()+""+i+"\t"+_volume+"\t"+_sphericity+"\t"+_flatness+"\t"+_elongation+"\n");
				} 
				output.flush();
				output.close();   
			}
		}
		else {IJ.showMessage("there are no object in your image!!!!!!!!!!!!!!!");}
	} 
}