package gred.nucleus.core;
import gred.nucleus.myGradient.MyGradient;
import gred.nucleus.utils.RegionalExtremaFilter;
import ij.measure.*;
import ij.*;
import ij.process.*;
import inra.ijpb.binary.ConnectedComponents;
import inra.ijpb.watershed.Watershed;;;

/**
 * Class containig differents tools after the watershed. We compute the contraste
 * between neighboring region.
 *
 * @author Poulet Axel

 */
public class ChromocenterSegmentation
{

	/**
	 *Contructor
	 * @param imagePlusDeconv Image deconvolved
	 * @param imagePlusWatershed Image results of the watershed
	 */

	public ChromocenterSegmentation (){	}


	/**
	 * Run the compute and the creation of the image contrast, of the image deconvolved
	 * @return Image contrast
	 */

	public ImagePlus applyChromocentersSegmentation(ImagePlus imagePlusRaw,ImagePlus imagePlusSegmented)
	{
		MyGradient myGradient = new MyGradient (imagePlusRaw,imagePlusSegmented);
		ImagePlus imagePlusGradient = myGradient.run();
		RegionalExtremaFilter regionalExtremaFilter = new RegionalExtremaFilter();
	    regionalExtremaFilter.setMask(imagePlusSegmented);
	    ImagePlus imagePlusExtrema = regionalExtremaFilter.applyWithMask( imagePlusGradient);
	    ImagePlus imagePlusLabels = ConnectedComponents.computeLabels(imagePlusExtrema, 26, 32);
	    ImagePlus imagePlusWatershed = Watershed.computeWatershed(imagePlusGradient,imagePlusLabels,imagePlusSegmented, 26,true,false);
		double contrast [] = computeContrast (imagePlusRaw,imagePlusWatershed);
		ImagePlus imagePlusContrast = computeImage (imagePlusWatershed, contrast);
		return imagePlusContrast;
	}


	/**
	 * Compute the region adjacency graph. The aim is to detect the  neighboring region.
	 * @return Region adjacency graph (RAG)
	 */

	public double [][] getRegionAdjacencyGraph (ImagePlus imagePlusWatershed)
	{
		int i, j, k, ii, jj, kk, voxelValue, neighborVoxelValue;
		ImageStatistics imageStatistics = new StackStatistics(imagePlusWatershed);
		double regionAdjacencyGraph[][] = new double [(int)imageStatistics.histMax + 1] [(int)imageStatistics.histMax + 1];
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
						neighborVoxelValue = (int) imageStackWatershed.getVoxel(i,j,kk);
						if (neighborVoxelValue > 0 && voxelValue != neighborVoxelValue)
							regionAdjacencyGraph[voxelValue][neighborVoxelValue] += volumeVoxel;
					}
					for (jj = j - 1; jj <= j + 1; jj += 2)
					{
						neighborVoxelValue = (int) imageStackWatershed.getVoxel(i,jj,k);
						if (neighborVoxelValue > 0 && voxelValue != neighborVoxelValue)
							regionAdjacencyGraph[voxelValue][neighborVoxelValue] += volumeVoxel;
					}
					for (ii = i - 1; ii <= i + 1; ii += 2)
					{
						neighborVoxelValue = (int) imageStackWatershed.getVoxel(ii,j,k);
						if (neighborVoxelValue > 0 && voxelValue != neighborVoxelValue)
							regionAdjacencyGraph[voxelValue][neighborVoxelValue] += volumeVoxel;
					}
				}
		return regionAdjacencyGraph;
	}

	/**
	 * Compute the contrasts in function neighboring region.
	 * @return table of the contrast value for each region
	 */

	
	public double [] computeContrast (ImagePlus imagePlusRaw,ImagePlus imagePlusWatershed)
	{
		double regionAdjacencyGraph[][] = getRegionAdjacencyGraph(imagePlusWatershed);
		double mean[] = computeMeanIntensity (imagePlusRaw,imagePlusWatershed);
		double tContrast[]= new double [regionAdjacencyGraph.length+1];
		double neighborVolumeTotal;
		int i,j;
		for(i = 1; i < regionAdjacencyGraph.length; ++i)
		{
			neighborVolumeTotal = 0;
			for(j = 1; j < regionAdjacencyGraph[i].length; ++j)
			{
				if (regionAdjacencyGraph[i][j] > 0 && i != j)
				{
					tContrast[i] +=  regionAdjacencyGraph[i][j]*(mean[i]-mean[j]);
					neighborVolumeTotal += regionAdjacencyGraph[i][j];
				}
			}
			if (tContrast[i] <= 0)  tContrast[i] = 0;
			else tContrast[i] = tContrast[i] / neighborVolumeTotal;
			
		}
		return tContrast;
   }

	/**
	 * Compute the topHat on the RAG
	 * @return new value of region
	 */

	public double [] topHat (ImagePlus imagePlusRaw,ImagePlus imagePlusWatershed)
	{
		double tIntensite[] = computeMeanIntensity (imagePlusRaw,imagePlusWatershed);
		double regionAdjacencyGraph[][] = getRegionAdjacencyGraph(imagePlusWatershed);
		double tOuverture[] =  ouverture(regionAdjacencyGraph,imagePlusRaw,imagePlusWatershed);
		double tContrast[] = new double [regionAdjacencyGraph.length+1];
		for(int i = 1; i < regionAdjacencyGraph.length; ++i)
			tContrast[i] = tIntensite[i] - tOuverture[i];
		return tContrast;
	}

	/**
	 * Filter max on the RAG
	 * @param tMeanIntensity 
	 * @param rag
	 * @return new value of region
	 */

	public double [] filterMaxRegionAdjacencyGraph (double tMeanIntensity [], double regionAdjacencyGraph [][])
	{
		double tOutput[] = new double [regionAdjacencyGraph.length];
		double max;
		int i,j;
		for(i = 1; i < regionAdjacencyGraph.length; ++i)
		{
			max = tMeanIntensity[i];
			for(j = 1; j < regionAdjacencyGraph.length; ++j) if  (tMeanIntensity[j] > max && regionAdjacencyGraph[i][j]>0 )  max = tMeanIntensity[j];
			tOutput[i] = max;
		}
		return tOutput;
	}
  
	/**
	 * Filter min on the RAG
	 * @param tMeanIntensity
	 * @param rag
	 * @return new value of region
	 */
	public double [] filterMinRegionAdjacencyGraph (double tMeanIntensity [], double regionAdjacencyGraph [][])
	{
		double tOutput[] = new double [regionAdjacencyGraph.length];
		double min;
		int i,j;
		for(i = 1; i < regionAdjacencyGraph.length; ++i)
		{
			min = tMeanIntensity[i];
			for(j = 1; j < regionAdjacencyGraph.length; ++j) if (tMeanIntensity[j] > 0 && tMeanIntensity[j] < min && regionAdjacencyGraph[i][j] > 0 ) min = tMeanIntensity[j];
			tOutput[i] = (int) min;
		}
		return tOutput;
	}

	/**
	 * Run closing on the RAG
	 * @param rag 
   	* @return new value of region
   	*/

	public double [] fermeture( double regionAdjacencyGraph [][],ImagePlus imagePlusInput,ImagePlus imagePlusWatershed)
	{
		double tabIntensite[] = computeMeanIntensity (imagePlusInput,imagePlusWatershed);
		double max [] = filterMaxRegionAdjacencyGraph (tabIntensite, regionAdjacencyGraph);
		return filterMinRegionAdjacencyGraph (max, regionAdjacencyGraph);
	}

	/**
	 * Run opening on the RAG
	 * @param rag 
	 * @return new value of region
	 */
	
	public double [] ouverture (double regionAdjacencyGraph [][],ImagePlus imagePlusInput,ImagePlus imagePlusWatershed)
	{
		double tabIntensite [] = computeMeanIntensity (imagePlusInput,imagePlusWatershed);
		double min []  = filterMinRegionAdjacencyGraph (tabIntensite, regionAdjacencyGraph);
		return filterMaxRegionAdjacencyGraph (min, regionAdjacencyGraph);
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
	}

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
}