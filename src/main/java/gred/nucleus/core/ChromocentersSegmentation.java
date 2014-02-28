package gred.nucleus.core;

import gred.nucleus.myGradient.MyGradient;
import gred.nucleus.utils.RegionalExtremaFilter;
import ij.measure.*;
import ij.*;
import ij.process.*;
import inra.ijpb.binary.ConnectedComponents;
import inra.ijpb.watershed.Watershed;


/**
 * Class containig differents tools after the watershed. We compute the contraste
 * between neighboring region.
 *
 * @author Poulet Axel

 */
public class ChromocentersSegmentation
{

	/**
	 *Contructor
	 * @param imagePlusDeconv Image deconvolved
	 * @param imagePlusWatershed Image results of the watershed
	 */

	public ChromocentersSegmentation (){	}


	/**
	 * Run the compute and the creation of the image contrast, of the image deconvolved
	 * @return Image contrast
	 */

	public ImagePlus applyChromocentersSegmentation(ImagePlus imagePlusInput,ImagePlus imagePlusBinary)
	{
		MyGradient myGradient = new MyGradient (imagePlusInput,imagePlusBinary);
		ImagePlus imagePlusGradient = myGradient.run();
		RegionalExtremaFilter regionalExtremaFilter = new RegionalExtremaFilter();
	    regionalExtremaFilter.setMask(imagePlusBinary);
	    ImagePlus extrema = regionalExtremaFilter.applyWithMask( imagePlusGradient);
	    ImagePlus ImagePlusLabels = ConnectedComponents.computeLabels(extrema, 26, 32);
	    ImagePlus imagePlusWatershed = Watershed.computeWatershed(imagePlusInput,ImagePlusLabels,imagePlusBinary, 26,true);
		double contrast [] = computeContrast (imagePlusInput,imagePlusWatershed);
		ImagePlus imagePlusOutput = computeImage (imagePlusWatershed, contrast);
		return imagePlusOutput;
	}


	/**
	 * Compute the region adjacency graph. The aim is to detect the  neighboring region.
	 * @return Region adjacency graph (RAG)
	 */

	public double [][] getRag (ImagePlus imagePlusWatershed)
	{
		int i, j, k, ii, jj, kk, voxelValue, neigVoxelValue;
		ImageStatistics statistics = new StackStatistics(imagePlusWatershed);
		double rag[][] = new double [(int)statistics.histMax + 1] [(int)statistics.histMax + 1];
		Calibration calibration = imagePlusWatershed.getCalibration();
		double volumeVoxel = calibration.pixelWidth * calibration.pixelHeight * calibration.pixelDepth;
		ImageStack imageStackWatershed = imagePlusWatershed.getStack();
		for (k = 0; k < imagePlusWatershed.getNSlices(); ++k)
			for (i = 0; i < imagePlusWatershed.getWidth(); ++i)
				for (j = 0; j < imagePlusWatershed.getHeight(); ++j)
				{
					voxelValue = (int)imageStackWatershed.getVoxel(i,j,k);
					for (kk = k - 1; kk <= k + 1; kk += 2)
					{
						neigVoxelValue = (int) imageStackWatershed.getVoxel(i,j,kk);
						if (neigVoxelValue > 0 && voxelValue != neigVoxelValue)
							rag[voxelValue][neigVoxelValue] += volumeVoxel;
					}
					for (jj = j - 1; jj <= j + 1; jj += 2)
					{
						neigVoxelValue = (int) imageStackWatershed.getVoxel(i,jj,k);
						if (neigVoxelValue > 0 && voxelValue != neigVoxelValue)
							rag[voxelValue][neigVoxelValue] += volumeVoxel;
					}
					for (ii = i - 1; ii <= i + 1; ii += 2)
					{
						neigVoxelValue = (int) imageStackWatershed.getVoxel(ii,j,k);
						if (neigVoxelValue > 0 && voxelValue != neigVoxelValue)
							rag[voxelValue][neigVoxelValue] += volumeVoxel;
					}
				}
		return rag;
	}

	/**
	 * Compute the contrasts in function neighboring region.
	 * @return table of the contrast value for each region
	 */

	
	public double [] computeContrast (ImagePlus imagePlusInput,ImagePlus imagePlusWatershed)
	{
		double rag[][] = getRag(imagePlusWatershed);
		double mean[] = computeMeanIntensity (imagePlusInput,imagePlusWatershed);
		double tabContrast[]= new double [rag.length+1];
		double neigbVolumeTotal;
		int i,j;
		for(i = 1; i < rag.length; ++i)
		{
			neigbVolumeTotal = 0;
			for(j = 1; j < rag[i].length; ++j)
			{
				if (rag[i][j] > 0 && i != j)
				{
					tabContrast[i] +=  rag[i][j]*(mean[i]-mean[j]);
					neigbVolumeTotal += rag[i][j];
				}
			}
			if (tabContrast[i] <= 0)  tabContrast[i] = 0;
			else tabContrast[i] = tabContrast[i] / neigbVolumeTotal;
			
		}
		return tabContrast;
   }

	/**
	 * Compute the topHat on the RAG
	 * @return new value of region
	 */

	public double [] topHat (ImagePlus imagePlusInput,ImagePlus imagePlusWatershed)
	{
		double tabIntensite[] = computeMeanIntensity (imagePlusInput,imagePlusWatershed);
		double rag[][] = getRag(imagePlusWatershed);
		double tabOuverture[] =  ouverture(rag,imagePlusInput,imagePlusWatershed);
		double tabContrast[] = new double [rag.length+1];
		for(int i = 1; i < rag.length; ++i)
			tabContrast[i] = tabIntensite[i] - tabOuverture[i];
		return tabContrast;
	}

	/**
	 * Filter max on the RAG
	 * @param tabMeanIntensity 
	 * @param rag
	 * @return new value of region
	 */

	public double [] FilterMaxRag (double tabMeanIntensity [], double rag [][])
	{
		double tabSortie[] = new double [rag.length];
		double max;
		int i,j;
		for(i = 1; i < rag.length; ++i)
		{
			max = tabMeanIntensity[i];
			for(j = 1; j < rag.length; ++j) if  (tabMeanIntensity[j] > max && rag[i][j]>0 )  max = tabMeanIntensity[j];
			tabSortie[i] = max;
		}
		return tabSortie;
	}
  
	/**
	 * Filter min on the RAG
	 * @param tabMeanIntensity
	 * @param rag
	 * @return new value of region
	 */
	public double [] FilterMinRag (double tabMeanIntensity [], double rag [][])
	{
		double tabSortie[] = new double [rag.length];
		double min;
		int i,j;
		for(i = 1; i < rag.length; ++i)
		{
			min = tabMeanIntensity[i];
			for(j = 1; j < rag.length; ++j) if (tabMeanIntensity[j] > 0 && tabMeanIntensity[j] < min && rag[i][j] > 0 ) min = tabMeanIntensity[j];
			tabSortie[i] = (int) min;
		}
		return tabSortie;
	}

	/**
	 * Run closing on the RAG
	 * @param rag 
   	* @return new value of region
   	*/

	public double [] fermeture( double rag [][],ImagePlus imagePlusInput,ImagePlus imagePlusWatershed)
	{
		double tabIntensite[] = computeMeanIntensity (imagePlusInput,imagePlusWatershed);
		double max [] = FilterMaxRag (tabIntensite, rag);
		return FilterMinRag (max, rag);
	}

	/**
	 * Run opening on the RAG
	 * @param rag 
	 * @return new value of region
	 */
	
	public double [] ouverture (double rag [][],ImagePlus imagePlusInput,ImagePlus imagePlusWatershed)
	{
		double tabIntensite [] = computeMeanIntensity (imagePlusInput,imagePlusWatershed);
		double min []  = FilterMinRag (tabIntensite, rag);
		return FilterMaxRag (min, rag);
	}

	/**
	 * Compute the mean of value voxel for each region
	 * @return Table of the mean intensity for each region
	 */
	public double [] computeMeanIntensity (ImagePlus imagePlusInput,ImagePlus imagePlusWatershed)
	{
		ImageStatistics stats = new StackStatistics(imagePlusWatershed);
		ImageStack imageStackWatershed = imagePlusWatershed.getStack();
		ImageStack imageStackInput = imagePlusInput.getStack();
		double tabIntensiteTotal[] = new double [(int)stats.histMax + 1];
		double tabIntensiteMoy[] = new double [(int)stats.histMax + 1];
		int tabNbVoxelInEachRegion[] = new int [(int)stats.histMax + 1];
		int i, j, k, voxelValue;

		for (k = 0; k < imagePlusWatershed.getNSlices(); ++k)
			for (i = 0; i < imagePlusWatershed.getWidth(); ++i)
				for (j = 0; j < imagePlusWatershed.getHeight(); ++j)
				{
					voxelValue = (int) imageStackWatershed.getVoxel(i,j,k);
					if (voxelValue > 0)
					{
						tabIntensiteTotal [voxelValue] += imageStackInput.getVoxel(i,j,k);
						++tabNbVoxelInEachRegion [voxelValue];
					}
				}
		for (i = 1; i < tabIntensiteTotal.length; ++i)
			tabIntensiteMoy[i] = tabIntensiteTotal[i] / tabNbVoxelInEachRegion [i];
		return tabIntensiteMoy;
	}//getIntensiteMoyenne

	/**
	 * With the image results of watershed and a table of regions value, create a
	 * new image.
	 * @param imagePlusLabellise
	 * @param tab table of a new value
	 * @return a new image
	 */
	public ImagePlus computeImage (ImagePlus imagePlusContrast, double tab [])
	{
		int i, j, k;
		double voxelValue;
		ImagePlus imagePlusOutput = imagePlusContrast.duplicate();
		ImageStack imageStackOutput = imagePlusOutput.getStack();
		for (k = 0; k < imagePlusOutput.getNSlices(); ++k)
			for (i = 0; i < imagePlusOutput.getWidth(); ++i)
				for (j = 0; j < imagePlusOutput.getHeight(); ++j)
				{
					voxelValue = imageStackOutput.getVoxel(i, j, k);
					if (voxelValue > 0) imageStackOutput.setVoxel(i, j, k, tab[(int)voxelValue]);
				}
		return imagePlusOutput;
	}
}//fin de classe