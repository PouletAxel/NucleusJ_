package gred.nucleus.utils;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import java.util.ArrayList;

public class ConnectedComponent
{
	private double[][] _image2D;
	private ArrayList<Double> _listLabel = new ArrayList<Double>();
	private double[][][] _image3D;
	private boolean _border = false;
	public ConnectedComponent(double[][] image2D)	{ _image2D = image2D; }
	public ConnectedComponent(double[][][] image3D)	{ _image3D = image3D; }
	public ConnectedComponent(ImagePlus ip)
	{
		if (ip.getStackSize() == 1)
		{
			ImageProcessor iProc = ip.getProcessor();
			int width = ip.getWidth();
			int height = ip.getHeight();
			_image2D = new double [width+1][height+1]; 
			for (int i = 0; i < width; ++i )
				for (int j = 0; j < height; ++j)
					_image2D[i][j] = iProc.getPixel(i, j);
				
		}
		else
		{
				ImageStack iStack = ip.getStack();
				int width = ip.getWidth();
				int height = ip.getHeight();
				int depth = ip.getNSlices();
				_image3D = new double [width+1][height+1][depth+1]; 
				for (int i = 0; i < width; ++i )
					for (int j = 0; j < height; ++j)
						for (int k = 0; k < depth; ++k)
						_image3D[i][j][k] = iStack.getVoxel(i,j,k);
		}
	}
	
	
	double[][] computeLabelOfOneObject2D(int labelInitial, VoxelRecord voxelRecord)
	{
		int currentLabel=2;
		breadthFirstSearch2D(labelInitial,voxelRecord,currentLabel);
		return _image2D;		
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
					else if(i == 0 || j == 0 ||i ==_image3D.length-1 || j == _image3D[0].length-1 )
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
		ArrayList<VoxelRecord> voxelBoundary = detectVoxelBoudary3D(labelIni);
		voxelBoundary.add(0, voxelRecord);
		_image2D[(int)voxelRecord._i][(int)voxelRecord._j] = currentLabel;
		while (! voxelBoundary.isEmpty())
		{
			VoxelRecord voxelRemove= voxelBoundary.remove(0);
			for (int ii = (int)voxelRemove._i-1;ii <= (int)voxelRemove._i+1; ++ii)
				for (int jj = (int)voxelRemove._j-1; jj <= (int)voxelRemove._j+1; ++jj)
					if (ii >= 0 && ii <= _image2D.length-1 && jj >= 0 && jj <= _image2D[0].length-1)
						if (_image2D[ii][jj]==labelIni)
						{
							for (int l = ii-1;l <= ii+1; ++l)
								for (int m = jj-1; jj <= jj+1; ++m)
									if (l >= 0 && l <= _image2D.length-1 && m >= 0 && m <= _image2D[0].length-1)
										if (_image2D[l][m] == currentLabel)
										{
											_image2D[ii][jj] = currentLabel;
											VoxelRecord voxel = new VoxelRecord();
											voxel.setLocation(ii, jj,0);
											voxelBoundary.add(0,voxel);
											break;
										}
						}
							
		}	
	}


	/**
	 * 
	 * @param labelIni
	 */
	void computeLabel3D(double labelIni)
	{
		int currentLabel=2;
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
				}
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
				for(int k = 0; k < _image3D[i][j].length; ++i )
				{     
					if (_image3D[i][j][k] == label)
					{
						if (i > 0 && i < _image3D.length-1 && j > 0 && j < _image3D[i].length-1 &&  k > 0 && k < _image3D[i][j].length-1 )
						{
							if ( _image3D[i-1][j][k] == 0 || _image3D[i+1][j][k] == 0|| _image3D[i][j-1][k] == 0|| _image3D[i][j+1][k] == 0)
							{
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j,k);
								lVoxelBoundary.add(voxelTest);	
							}
						}
						else if(i == 0 || j == 0 || k == 0||i ==_image3D.length-1 || j == _image3D[0].length-1 || k == _image3D[0][0].length-1 )
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
		ArrayList<VoxelRecord> voxelBoundary = detectVoxelBoudary3D(labelIni);
		voxelBoundary.add(0, voxelRecord);
		_image3D[(int)voxelRecord._i][(int)voxelRecord._j][(int)voxelRecord._k] = currentLabel;
		while (! voxelBoundary.isEmpty())
		{
			VoxelRecord voxelRemove= voxelBoundary.remove(0);
			for (int ii = (int)voxelRemove._i-1;ii <= (int)voxelRemove._i+1; ++ii)
				for (int jj = (int)voxelRemove._j-1; jj <= (int)voxelRemove._j+1; ++jj)
					for (int kk = (int)voxelRemove._k-1; kk <= (int)voxelRemove._k+1; ++kk)
						if (ii >= 0 && ii <= _image3D.length-1 && jj >= 0 && jj <= _image3D[0].length-1 && kk >= 0 && kk <= _image3D[0][0].length-1)
							if (_image3D[ii][jj][kk]==labelIni)
								for (int l = ii-1;l <= ii+1; ++l)
									for (int m = jj-1; jj <= jj+1; ++m)
										for (int n = kk-1; n <= kk+1; ++n)
										{
											if (l >= 0 && l <= _image3D.length-1 && m >= 0 && m <= _image3D[0].length-1 && n >= 0 && n <= _image3D[0][0].length-1)
												if (_image3D[l][m][n] == currentLabel)
												{
													_image3D[ii][jj][kk] = currentLabel;
													VoxelRecord voxel = new VoxelRecord();
													voxel.setLocation(ii, jj, kk);
													voxelBoundary.add(0,voxel);
													break;
												}
										}
		}	
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


	public double[][] getImageTable() {
		return _image2D;
	}


	public ArrayList<VoxelRecord> getBoudaryVoxel (int label)
	{
		return detectVoxelBoudary2D(label);
	}
	public void setImageTable(double[][] _image) {
		this._image2D = _image;
	}

}