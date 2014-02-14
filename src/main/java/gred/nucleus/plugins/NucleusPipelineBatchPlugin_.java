package gred.nucleus.plugins;
import java.io.File;

import gred.nucleus.dialogs.NucleusPipelineBatchDialog;
import gred.nucleus.multiThread.*;
import gred.nucleus.utils.ListerFichier;
import ij.plugin.PlugIn;



/**
 * 
 * @author gred
 *
 */
public class NucleusPipelineBatchPlugin_ implements PlugIn
{
	NucleusPipelineBatchDialog _jfpfso = new NucleusPipelineBatchDialog();
	
	/**
	 * 
	 */
	public void run(String arg)
	{
		while( _jfpfso.isShowing())
		{
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
	    }	
		if (_jfpfso.isStart())
		{
			ListerFichier lF = new ListerFichier (_jfpfso.getDirRawData());
			lF.run();
			File[] rawImage = lF._tFile;
			ProcessImage plop = new ProcessImage(this, rawImage);
			try {	plop.go(); } 
			catch (InterruptedException e) { e.printStackTrace(); }
			
		}
	}
	
	public double getPixelDepth(){return _jfpfso.getz();}
	public double getPixelWidth(){return _jfpfso.getx();}
	public double getPixelHeight(){return _jfpfso.gety();}
	public String getUnit(){return _jfpfso.getUnit();}
	public double getSegMinValue(){return _jfpfso.getMinSeg();}
	public double getSegMaxValue(){return _jfpfso.getMaxSeg();}
	public String getWorkDir() {return _jfpfso.getWorkDirectory();}
	public boolean is2D3DAnalysis(){return _jfpfso.isTheBoth();}
	public boolean is3DAnalysis(){return _jfpfso.is3D();}

}
