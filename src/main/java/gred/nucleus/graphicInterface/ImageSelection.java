package gred.nucleus.graphicInterface;

import ij.*;
import ij.gui.*;

/**
 *  This class is used to select an image open with imageJ
 * 
 * @author Poulet Axel and Andrey Philippe
 */


public class ImageSelection
{
	/** this boolean is true if the user choice cancel on the window */
    private boolean _cancel = false;
    /** ImagePlus to stock the user choice*/
    private ImagePlus _imagePlusMask;

    
    /**
     * This method open a graphic windows to allow te user to select an image
     * 
     * @param titleOfWindows title of the windows 
     */
    public void run(String titleOfWindows)
    {
        int[] wList = WindowManager.getIDList();
        String[] titles = new String[wList.length];
        for (int i = 0; i < wList.length; i++)
        {
            ImagePlus imp = WindowManager.getImage(wList[i]);
            if (imp != null)        titles[i] = imp.getTitle();
            else             titles[i] = "";
        }
		
        String titleMask = Prefs.get("iterativedeconvolve3d.titleMask", titles[0]);
        int maskChoice = 0;
        for (int i = 0; i < wList.length; i++)
        {
            if(titleMask.equals(titles[i]))
            {
                maskChoice = i;
                break;
            }
        }
        GenericDialog gd = new GenericDialog(titleOfWindows, IJ.getInstance());
        gd.addChoice("Mask for the process",titles,titles[maskChoice]);
        gd.showDialog();
        if (gd.wasCanceled())           _cancel = true;
       _imagePlusMask = WindowManager.getImage(wList[gd.getNextChoiceIndex()]);
    }
    
    /**
     * getter to detect if the user click on cancel button
     * 
     * @return 
     */
    
    public boolean isCancel () { return _cancel; }
    
    /**
     * getter to return the binary mask chose by the user
     * @return 
     */
    public ImagePlus getChosenImage () { return _imagePlusMask;}
}