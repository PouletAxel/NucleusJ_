package gred.nucleus.utils;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import java.util.ArrayList;

public class ConnectedComponent
{
	private double[][] _image2D;
	private ArrayList<Double> _listLabel = new ArrayList<Double>();
	private ArrayList<ComponentInfo> m_tComponentInfo = new ArrayList<ComponentInfo>();
	private double[][][] _image3D;
	private ImagePlus _ip;
	private ImagePlus _ipLabel;
	private boolean _border = false;
	public ConnectedComponent(double[][] image2D)	{ _image2D = image2D; }
	public ConnectedComponent(double[][][] image3D)	{ _image3D = image3D; }
	public ConnectedComponent(ImagePlus ip)
	{
		_ip = ip;
		_ipLabel = _ip.duplicate();
		
		if (ip.getStackSize() == 1)
		{
			ImageProcessor iProc = _ip.getProcessor();
			ImageProcessor iLabel= _ipLabel.getProcessor();
			int width = _ip.getWidth();
			int height = _ip.getHeight();
			_image2D = new double [width][height]; 
			for (int i = 0; i < width; ++i )
				for (int j = 0; j < height; ++j)
				{
					_image2D[i][j] = iProc.getPixel(i, j);
					iLabel.putPixelValue(i, j, 0);
				}
			computeLabel2D(255);
		}
		else
		{
			ImageStack iStack = _ip.getStack();
			ImageStack ilabele = _ipLabel.getStack();
			int width = _ip.getWidth();
			int height = _ip.getHeight();
			int depth = _ip.getNSlices();
			_image3D = new double [width+1][height+1][depth+1]; 
			for (int i = 0; i < width; ++i )
				for (int j = 0; j < height; ++j)
					for (int k = 0; k < depth; ++k)
					{
						_image3D[i][j][k] = iStack.getVoxel(i,j,k);
						ilabele.setVoxel(i, j, k, 0);
					}
			computeLabel3D(255);
		}
	}
	
	public void labelImage(boolean removeBorderComponent, double thresholdSize)
	{
		int width = _ip.getWidth();
		int height = _ip.getHeight();
		if (_ip.getStackSize() == 1)
		{
			ImageProcessor iLabel= _ipLabel.getProcessor();
			double voxelVolume =  _ip.getCalibration().pixelHeight*_ip.getCalibration().pixelWidth;
			for (int l =0; l <  m_tComponentInfo.size(); ++l)
			{
				double volume = voxelVolume*m_tComponentInfo.get(l).getnumberOfPoints();
				if(volume>thresholdSize)
				{
					if (removeBorderComponent==true )
					{
						if ( m_tComponentInfo.get(l).IsOnTheeBorder()==false)
						{
							for (int i = 0; i < width; ++i )
								for (int j = 0; j < height; ++j)
									if (_image2D[i][j] == m_tComponentInfo.get(l).getLabel())
										iLabel.putPixelValue(i, j, m_tComponentInfo.get(l).getLabel());
						}
					}
					else
					{
						IJ.log("plop");
						for (int i = 0; i < width; ++i )
							for (int j = 0; j < height; ++j)
								if (_image2D[i][j] == (double)m_tComponentInfo.get(l).getLabel())
								{	
									iLabel.putPixelValue(i, j, m_tComponentInfo.get(l).getLabel());
								}
					}
						
				}
			}
		}
		else
		{
			int depth = _ip.getNSlices();
			ImageStack iLabel= _ipLabel.getStack();
			double voxelVolume =  _ip.getCalibration().pixelHeight*_ip.getCalibration().pixelWidth*_ip.getCalibration().pixelDepth;
			for (int l =0; l <  m_tComponentInfo.size(); ++l)
			{
				double volume = voxelVolume*m_tComponentInfo.get(l).getnumberOfPoints();
				if(volume>thresholdSize)
				{
					if (removeBorderComponent==true )
					{
						if ( m_tComponentInfo.get(l).IsOnTheeBorder()==false)
						{
							for (int i = 0; i < width; ++i )
								for (int j = 0; j < height; ++j)
									for (int k = 0; k < depth; ++k)
									if (_image3D[i][j][k] == m_tComponentInfo.get(l).getLabel())
										iLabel.setVoxel(i, j,k, m_tComponentInfo.get(l).getLabel());
						}
					}
					else
					{
						
						for (int i = 0; i < width; ++i )
							for (int j = 0; j < height; ++j)
								for (int k = 0; k < depth; ++k)
								if (_image3D[i][j][k] == m_tComponentInfo.get(l).getLabel())
									iLabel.setVoxel(i, j,k, m_tComponentInfo.get(l).getLabel());
					}
						
				}
			}
		}
	}
	
	/**
	 * 
	 * @param labelIni
	 */
		
	void computeLabel2D(double labelIni)
	{
		int currentLabel=2;
		for(int i = 0; i < _image2D.length; ++i )
			for(int j = 0; j < _image2D[i].length; ++j )
				if (_image2D[i][j] == labelIni)
				{
					_image2D[i][j] = currentLabel;
					VoxelRecord voxelRecord = new VoxelRecord();
					voxelRecord.setLocation(i, j, 0);
					breadthFirstSearch2D(labelIni,voxelRecord,currentLabel);
					_listLabel.add((double)currentLabel);
					currentLabel++;	
					_border = false;
				}
		}

	/**
	 * 
	 * @param labelIni
	 * @param voxelRecord
	 * @param currentLabel
	 */
	private ArrayList<VoxelRecord> detectVoxelBoudary2D (double label)
	{
		ArrayList<VoxelRecord> lVoxelBoundary = new ArrayList<VoxelRecord>();
		for(int i = 0; i < _image2D.length; ++i )
			for(int j = 0; j < _image2D[i].length; ++j )
			{     
				if (_image2D[i][j] == label)
				{
					if (i > 0 && i < _image2D.length-1 && j > 0 && j < _image2D[i].length-1)
					{
						if ( _image2D[i-1][j] == 0 || _image2D[i+1][j] == 0|| _image2D[i][j-1] == 0|| _image2D[i][j+1] == 0)
						{
							VoxelRecord voxelTest = new VoxelRecord();
							voxelTest.setLocation(i, j,0);
							lVoxelBoundary.add(voxelTest);	
						}
					}
					else if(i == 0 || j == 0 ||i ==_image2D.length || j == _image2D[0].length )
					{
						_border = true;
						VoxelRecord voxelTest = new VoxelRecord();
						voxelTest.setLocation(i, j,0);
						lVoxelBoundary.add(voxelTest);	
					}
				}
			}
		return lVoxelBoundary;
	}
	
	
	/**
	 * 
	 * @param labelIni
	 * @param voxelRecord
	 * @param currentLabel
	 */
	
	private void breadthFirstSearch2D( double labelIni,VoxelRecord voxelRecord, int currentLabel)
	{
		ArrayList<VoxelRecord> voxelBoundary = detectVoxelBoudary2D(labelIni);
		IJ.log("je detect les voxel frontiere");
		voxelBoundary.add(0, voxelRecord);
		IJ.log("je finis la detection les voxel frontiere");
		_image2D[(int)voxelRecord._i][(int)voxelRecord._j] = currentLabel;
		int nbOfPoint = 1;
		IJ.log("debut du while");
		while (! voxelBoundary.isEmpty())
		{
			VoxelRecord voxelRemove= voxelBoundary.remove(0);
			for (int ii = (int)voxelRemove._i-1;ii <= (int)voxelRemove._i+1; ++ii)
				for (int jj = (int)voxelRemove._j-1; jj <= (int)voxelRemove._j+1; ++jj)
					if (ii >= 0 && ii < _image2D.length && jj >= 0 && jj < _image2D[0].length)
						if (_image2D[ii][jj]==labelIni)
						{
							boolean testBreak = false;
							for (int l = ii-1;l <= ii+1; ++l)
							{
								for (int m = jj-1; m <= jj+1; ++m)
								{
									if (l >= 0 && l < _image2D.length && m >= 0 && m < _image2D[0].length)
									{
										
										if (_image2D[l][m] == currentLabel)
										{
											_image2D[ii][jj] = currentLabel;
											VoxelRecord voxel = new VoxelRecord();
											voxel.setLocation(ii, jj,0);
											voxelBoundary.add(0,voxel);
											++nbOfPoint;
											testBreak = true;
											break;
										}
									}
								}
								if (testBreak){break;}	
							}
						}
		}
		ComponentInfo ci = new ComponentInfo (currentLabel,nbOfPoint,voxelRecord,_border);
		m_tComponentInfo.add(ci);
		
	}

	/**
	 * 
	 * @param labelIni
	 */
	void computeLabel3D(double labelIni)
	{
		int currentLabel=2;
		IJ.log("debut label");
		for(int i = 0; i < _image3D.length; ++i )
			for(int j = 0; j < _image3D[i].length; ++j )
				for(int k = 0; k < _image3D[i][j].length; ++k )
				if (_image3D[i][j][k] == labelIni)
				{
					_image3D[i][j][k] = currentLabel;
					VoxelRecord voxelRecord = new VoxelRecord();
					voxelRecord.setLocation(i, j, k);
					breadthFirstSearch3D(labelIni,voxelRecord,currentLabel);
					_listLabel.add((double)currentLabel);
					currentLabel++;	
					_border = false;
				}
		IJ.log("fin label");
	}
	/**
	 * 
	 * @param label
	 * @return
	 */
	private ArrayList<VoxelRecord> detectVoxelBoudary3D (double label)
	{
		ArrayList<VoxelRecord> lVoxelBoundary = new ArrayList<VoxelRecord>();
		for(int i = 0; i < _image3D.length; ++i )
			for(int j = 0; j < _image3D[i].length; ++j )
				for(int k = 0; k < _image3D[i][j].length; ++k )
				{     
					if (_image3D[i][j][k] == label)
					{
						if (i > 0 && i < _image3D.length && j > 0 && j < _image3D[i].length &&  k > 0 && k < _image3D[i][j].length )
						{
							if ( _image3D[i-1][j][k] == 0 || _image3D[i+1][j][k] == 0|| _image3D[i][j-1][k] == 0|| _image3D[i][j+1][k] == 0)
							{
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j,k);
								lVoxelBoundary.add(voxelTest);	
							}
						}
						else if(i == 0 || j == 0 || k == 0||i ==_image3D.length || j == _image3D[0].length || k == _image3D[0][0].length )
						{
							_border = true;
							VoxelRecord voxelTest = new VoxelRecord();
							voxelTest.setLocation(i, j,k);
							lVoxelBoundary.add(voxelTest);	
						}
					}
				}
		
		return lVoxelBoundary;
	}

	
	/**
	 * 
	 * @param labelIni
	 * @param voxelRecord
	 * @param currentLabel
	 */
	
	private void breadthFirstSearch3D( double labelIni,VoxelRecord voxelRecord, int currentLabel)
	{
		IJ.log("debut boundary");
		ArrayList<VoxelRecord> voxelBoundary = detectVoxelBoudary3D(labelIni);
		IJ.log("fin boundary");
		voxelBoundary.add(0, voxelRecord);
		_image3D[(int)voxelRecord._i][(int)voxelRecord._j][(int)voxelRecord._k] = currentLabel;
		int nbOfPoint = 1;
		while (! voxelBoundary.isEmpty())
		{
			VoxelRecord voxelRemove= voxelBoundary.remove(0);
			for (int ii = (int)voxelRemove._i-1;ii <= (int)voxelRemove._i+1; ++ii)
				for (int jj = (int)voxelRemove._j-1; jj <= (int)voxelRemove._j+1; ++jj)
					for (int kk = (int)voxelRemove._k-1; kk <= (int)voxelRemove._k+1; ++kk)
						if (ii >= 0 && ii <= _image3D.length-1 && jj >= 0 && jj <= _image3D[0].length-1 && kk >= 0 && kk <= _image3D[0][0].length-1)
							if (_image3D[ii][jj][kk]==labelIni)
							{
								boolean testBreak = false;
								for (int l = ii-1;l <= ii+1; ++l)
								{	
									for (int m = jj-1; m <= jj+1; ++m)
									{
										for (int n = kk-1; n <= kk+1; ++n)
										{
											if (l >= 0 && l <= _image3D.length-1 && m >= 0 && m <= _image3D[0].length-1 && n >= 0 && n <= _image3D[0][0].length-1)
												if (_image3D[l][m][n] == currentLabel)
												{
													_image3D[ii][jj][kk] = currentLabel;
													VoxelRecord voxel = new VoxelRecord();
													voxel.setLocation(ii, jj, kk);
													voxelBoundary.add(0,voxel);
													++nbOfPoint;
													testBreak = true;
													break;
												}
										}
										if (testBreak){break;}	
									}
									if (testBreak){break;}	
								}
							}
		}
		IJ.log("label: "+currentLabel+" nbOfPoint: "+nbOfPoint+" Au bord "+_border);
		ComponentInfo ci = new ComponentInfo (currentLabel,nbOfPoint,voxelRecord,_border);
		m_tComponentInfo.add(ci);
	}

	
	/**
	 * 
	 * @param labelIni
	 * @return
	 */
	public ArrayList<Double> getListLabel(double labelIni) {
		
		
		computeLabel2D(labelIni);
		return _listLabel;
	}


	
	/**
	 *  
	 * @return
	 */
	public ImagePlus getImage() {	return _ip; }
	
	/**
	 *  
	 * @return
	 */
	public ImagePlus getLabelImage() {	return _ipLabel; }
	/**
	 * 
	 * @return
	 */
	public double[][] getImageTable2D() {	return _image2D; }

	/**
	 * 
	 * @param labelInitial
	 * @param voxelRecord
	 * @return
	 */
	double[][] computeLabelOfOneObject2D(int labelInitial, VoxelRecord voxelRecord)
	{
		int currentLabel=2;
		breadthFirstSearch2D(labelInitial,voxelRecord,currentLabel);
		return _image2D;		
	}
	
	/**
	 * 
	 * @param label
	 * @return
	 */
	public ArrayList<VoxelRecord> getBoudaryVoxel (int label)	{return detectVoxelBoudary2D(label);}
	
	/**
	 * 
	 * @param _image
	 */
	public void setImageTable(double[][] _image) {this._image2D = _image; }

}