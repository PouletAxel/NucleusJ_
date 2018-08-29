package gred.nucleus.plugins;
import java.io.File;
import gred.nucleus.dialogs.NucleusSegmentationBatchDialog;
import gred.nucleus.multiThread.*;
import gred.nucleus.utils.FileList;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
/**
 * 
 * Method to segment the nucleus on batch
 *  
 * @author Poulet Axel
 *
 */
public class NucleusSegmentationBatchPlugin_ implements PlugIn{

	private NucleusSegmentationBatchDialog m_nuc = new NucleusSegmentationBatchDialog();
	
	/**
	 * 
	 */
	public void run(String arg){
		while(m_nuc.isShowing()){
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
		}	
		if(m_nuc.isStart()){
			FileList fileList = new FileList ();
			File[] tRawImageFile = fileList.run(m_nuc.getRawDataDirectory());
			if (tRawImageFile.length==0)
				IJ.showMessage("There are no image in "+m_nuc.getRawDataDirectory());
			else{
				if(IJ.openImage(tRawImageFile[0].toString()).getType() == ImagePlus.GRAY32 ){
			    	IJ.error("Image format", "No image in gray scale 8bits or 16 bits in 3D");
			        return;
			    }
				ProcessImageSegmentaion processImageSegmentation = new ProcessImageSegmentaion();
				try{	
					processImageSegmentation.go(this, tRawImageFile,false);
					IJ.log("End of Nuclear segmentation. Results are in "+getWorkDirectory());
				} 
				catch (InterruptedException e) { e.printStackTrace(); }
			}
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
}