package test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import gred.nucleus.core.NucleusSegmentation;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.plugin.ContrastEnhancer;
import ij.plugin.GaussianBlur3D;
import ij.plugin.Histogram;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.frame.ContrastAdjuster;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.process.LUT;

public class TestContrast {
	public static void main(String[] args) throws IOException, InterruptedException {
		ImagePlus imagePlus = IJ.openImage("/home/plop/Bureau/ImageF/RawDataNucleus/01_cropped FM_101_63x_tile scan_z.czi - C=0.tif");
		GaussianBlur3D.blur(imagePlus,0.25,0.25,1);
		ImageStack imageStack= imagePlus.getStack();
		int max = 0;
		for(int k = 0; k < imagePlus.getStackSize(); ++k)
			for (int i = 0; i < imagePlus.getWidth(); ++i )
				for (int j = 0; j < imagePlus.getHeight(); ++j){
					if (max < imageStack.getVoxel(i, j, k)){
						max = (int) imageStack.getVoxel(i, j, k);
					}
				}
		IJ.setMinAndMax(imagePlus, 0, max);	
		IJ.run(imagePlus, "Apply LUT", "stack");
	
		System.out.println(max);
		imagePlus.show();
		FileSaver fileSaver = new FileSaver(imagePlus);
	    fileSaver.saveAsTiffStack("/home/plop/Bureau/test.tif");
		ImagePlus imagePlusSegmented= imagePlus;
		NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
		nucleusSegmentation.setVolumeRange(7, 100000000);
		imagePlusSegmented = nucleusSegmentation.applySegmentation(imagePlusSegmented);
		imagePlusSegmented.show();
	}
}
