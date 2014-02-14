package gred.nucleus.myGradient;
import ij.ImagePlus;
import imagescience.image.Aspects;
import imagescience.image.FloatImage;
import imagescience.image.Image;
import imagescience.utility.Progressor;


/**
 * Modification of plugin featureJ to integrate of this work,
 *
 * => Use to imagescience.jar library
 * @author poulet axel
 * 
 */

public class MyGradient
{

	private static boolean compute = true;
	private static boolean suppress = false;
	private boolean mask;
	private static String scale = "1.0";
	private static String lower = "";
	private static String higher = "";
	ImagePlus _imagePlus;
	ImagePlus _imagePlusBinaire;
  
		public MyGradient ( ImagePlus imp, ImagePlus imagePlusBinaire)
		{
			_imagePlus = imp;
			_imagePlusBinaire =  imagePlusBinaire;
			mask = true;
		}
  
		public MyGradient ( ImagePlus imp)
		{
			_imagePlus = imp;
			mask = false;
		}

		@SuppressWarnings("unused")
		public ImagePlus run()
		{
			ImagePlus newimp = new ImagePlus();
			try
			{
				double scaleval, lowval=0, highval=0;
				boolean lowthres = true, highthres = true;
				try { scaleval = Double.parseDouble(scale); }
				catch (Exception e) { throw new IllegalArgumentException("Invalid smoothing scale value"); }
				try { if (lower.equals("")) lowthres = false; else lowval = Double.parseDouble(lower); }
				catch (Exception e) { throw new IllegalArgumentException("Invalid lower threshold value"); }
				try { if (higher.equals("")) highthres = false; else highval = Double.parseDouble(higher); }
				catch (Exception e) { throw new IllegalArgumentException("Invalid higher threshold value"); }
				final int thresmode = (lowthres ? 10 : 0) + (highthres ? 1 : 0);
				final Image img = Image.wrap(_imagePlus);
				Image newimg = new FloatImage(img);
				double[] pls = {0, 1}; int pl = 0;
				if ((compute || suppress) && thresmode > 0)
					pls = new double[] {0, 0.9, 1};
				final Progressor progressor = new Progressor();
				progressor.display(FJ_Options.pgs);
				if (compute || suppress)
				{
					final Aspects aspects = newimg.aspects();
					if (!FJ_Options.isotropic) newimg.aspects(new Aspects());
					final MyEdges myEdges = new MyEdges();
					if (mask) myEdges.setMask(_imagePlusBinaire);
					progressor.range(pls[pl],pls[++pl]);
					myEdges.progressor.parent(progressor);
					myEdges.messenger.log(FJ_Options.log);
					myEdges.messenger.status(FJ_Options.pgs);
					newimg = myEdges.run(newimg,scaleval,suppress);
					newimg.aspects(aspects);
				}
				newimp = newimg.imageplus();
				_imagePlus.setCalibration(newimp.getCalibration());
				final double[] minmax = newimg.extrema();
				final double min = minmax[0], max = minmax[1];
				newimp.setDisplayRange(min,max);
        }
		catch (OutOfMemoryError e) {	FJ.error("Not enough memory for this operation");}
		catch (IllegalArgumentException e) {	FJ.error(e.getMessage()); }
		catch (IllegalStateException e) {	FJ.error(e.getMessage());}
		//catch (Throwable e) {	FJ.error("An unidentified error occurred while running the plugin");	}
		return newimp;
	}
}
