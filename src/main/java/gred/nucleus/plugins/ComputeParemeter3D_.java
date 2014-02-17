package gred.nucleus.plugins;
import gred.nucleus.core.Measure3D;
import gred.nucleus.utils.Histogram;
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
	private ImagePlus _imagePlus;
	/** */
	private String _outFile;

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
				Measure3D measure3D = new Measure3D ();
				double volume = measure3D.computeVolumeObject(_imagePlus,temp[i]);
				double esr = measure3D.equivalentSphericalRadius(_imagePlus,temp[i] );
				double surfacicArea = measure3D.computeSurfaceObject(_imagePlus,temp[i]);
				double elongation = measure3D.computeElongationObject(_imagePlus,temp[i]);
				double flatness = measure3D.computeFlatnessObject(_imagePlus,temp[i]);
				double sphericity = measure3D.computeSphericity(volume,temp[i]);
				File fileResu = new File (_outFile);
				boolean exist = fileResu.exists();
				BufferedWriter output;
				if (exist)
				{
					FileWriter fw = new FileWriter(fileResu, true);
					output = new BufferedWriter(fw);
					if (temp.length == 1) output.write(_imagePlus.getTitle()+"\t"+volume+"\t"+sphericity+"\t"+flatness+"\t"+"\t"+surfacicArea+"\t"+esr+"\n");
					else output.write(_imagePlus.getTitle()+""+i+"\t"+volume+"\t"+sphericity+"\t"+flatness+"\t"+"\t"+surfacicArea+"\t"+esr+"\n");
 				}
				else
				{
					FileWriter fw = new FileWriter(fileResu, true);
					output = new BufferedWriter(fw);
					output.write("ImageTitle\tVolume\tsphericity\tflatness\telongation\tSurfacic Area\tESR\n");
					if (temp.length == 1) output.write(_imagePlus.getTitle()+"\t"+volume+"\t"+sphericity+"\t"+flatness+"\t"+elongation+"\t"+surfacicArea+"\t"+esr+"\n");
					else output.write(_imagePlus.getTitle()+""+i+"\t"+volume+"\t"+sphericity+"\t"+flatness+"\t"+elongation+"\t"+surfacicArea+"\t"+esr+"\n");
				} 
				output.flush();
				output.close();   
			}
		}
		else {IJ.showMessage("there are no object in your image!!!!!!!!!!!!!!!");}
	} 
}