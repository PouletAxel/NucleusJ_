package gred.nucleus.plugins;
import java.io.File;

import gred.nucleus.dialogs.NucleusSegmentationAndAnalysisBatchDialog;
import gred.nucleus.multiThread.*;
import gred.nucleus.utils.FileList;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 *  Method to segment and analyse the nucleus on batch
 *  
 * @author Poulet Axel
 *
 */
public class NucleusSegmentationAndAnalysisBatchPlugin_ implements PlugIn{
	NucleusSegmentationAndAnalysisBatchDialog m_nuc = new NucleusSegmentationAndAnalysisBatchDialog();
	
	/**
	 * 
	 */
	public void run(String arg){
		while(m_nuc.isShowing()){
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
	    }	
		if(m_nuc.isStart()){
			IJ.log("Begining of the segmentation of nuclei, data are in "+m_nuc.getRawDataDirectory());
			FileList fileList = new FileList ();
			File[] tFileRawImage = fileList.run(m_nuc.getRawDataDirectory());
			if(IJ.openImage(tFileRawImage[0].toString()).getType() == ImagePlus.GRAY32 ){
		    	IJ.error("image format", "No image in gray scale 8bits or 16 bits in 3D");
		        return;
		    }
			
			ProcessImageSegmentaion processImageSegmentation = new ProcessImageSegmentaion();
			try{
				processImageSegmentation.go(this, tFileRawImage,true);
			} 
			catch (InterruptedException e) { e.printStackTrace(); }
			IJ.log("End of nuclear segmentation. Results are in "+m_nuc.getWorkDirectory());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNbCpu(){
		return m_nuc.getNbCpu();
	}
	/**
	 * 
	 * @return
	 */
	public double getZCalibration(){
		return m_nuc.getZCalibration();
	}
	/**
	 * 
	 * @return
	 */
	public double getXCalibration(){
		return m_nuc.getXCalibration();
	}
	/**
	 * 
	 * @return
	 */
	public double getYCalibration(){
		return m_nuc.getYCalibration();
	}
	/**
	 * 
	 * @return
	 */
	public String getUnit(){
		return m_nuc.getUnit();
	}
	/**
	 * 
	 * @return
	 */
	public double getMinVolume(){
		return m_nuc.getMinVolume();
	}
	/**
	 * 
	 * @return
	 */
	public double getMaxVolume(){
		return m_nuc.getMaxVolume();
	}
	/**
	 * 
	 * @return
	 */
	public String getWorkDirectory(){
		return m_nuc.getWorkDirectory();
	}
	/**
	 * 
	 * @return
	 */
	public boolean is2D3DAnalysis(){
		return m_nuc.is2D3DAnalysis();
	}
	/**
	 * 
	 * @return
	 */
	public boolean is3DAnalysis(){
		return m_nuc.is3D();
	}

}
