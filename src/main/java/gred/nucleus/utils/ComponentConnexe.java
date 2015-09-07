package gred.nucleus.utils;

import ij.IJ;

import java.util.ArrayList;

public class ComponentConnexe
{
	private double[][] _image;
	private String _axesName;
	private ArrayList<Double> _listLabel = new ArrayList<Double>();
	

	/**
	 * 
	 * @param labelIni
	 */
	void computeLabel(double labelIni)
	{
		int currentLabel=2;
		for(int i = 0; i < _image.length; ++i )
			for(int j = 0; j < _image[i].length; ++j )
				if (_image[i][j] == labelIni)
				{
					_image[i][j] = currentLabel;
					VoxelRecord voxelRecord = new VoxelRecord();
					voxelRecord.setLocation(i, j, 0);
					breadthFirstSearch(labelIni,voxelRecord,currentLabel);
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
	private void breadthFirstSearch( double labelIni,VoxelRecord voxelRecord, int currentLabel)
	{
		ArrayList<VoxelRecord> voxelBoundary = detectVoxelBoudary(labelIni);
		voxelBoundary.add(0, voxelRecord);
		_image[(int)voxelRecord._i][(int)voxelRecord._j]=currentLabel;
		while (! voxelBoundary.isEmpty())
		{
			VoxelRecord voxelRemove= voxelBoundary.remove(0);
			for (int ii = (int)voxelRemove._i-1;ii <= (int)voxelRemove._i+1; ii++)
				for (int jj = (int)voxelRemove._j-1;jj <= (int)voxelRemove._j+1; jj++)
				{
					if (ii >= 0 && ii <= _image.length-1 && jj >= 0 && jj <= _image[0].length-1)
					{
						if (ii > 0 && ii < _image.length-1 && jj > 0 && jj < _image[0].length-1)
						{
							if (_image[ii][jj]==labelIni &&(_image[ii-1][jj] == currentLabel || _image[ii+1][jj] == currentLabel|| _image[ii][jj-1] == currentLabel|| _image[ii][jj+1]== currentLabel))
							{
								_image[ii][jj] = currentLabel;
								VoxelRecord voxel = new VoxelRecord();
								voxel.setLocation(ii, jj, 0);
								voxelBoundary.add(0,voxel);
							}
						}
						else if (ii == 0)
						{
							if (jj == 0)
							{
								if (_image[ii][jj]==labelIni &&(_image[ii+1][jj] == currentLabel|| _image[ii][jj+1]== currentLabel))
								{
									_image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0,voxel);
								}
							}
							else if (jj == _image[0].length-1)
							{
								if (_image[ii][jj]==labelIni &&(_image[ii+1][jj] == currentLabel|| _image[ii][jj-1] == currentLabel))
								{
									_image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0,voxel);
								}
							}
							else
							{
								if (_image[ii][jj]==labelIni &&( _image[ii+1][jj] == currentLabel|| _image[ii][jj-1] == currentLabel|| _image[ii][jj+1]== currentLabel))
								{
									_image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0,voxel);
								}
							}
						}
						else if (ii == _image.length-1)
						{
							if (jj == 0)
							{
								if (_image[ii][jj]==labelIni &&(_image[ii-1][jj] == currentLabel || _image[ii][jj+1]== currentLabel))
								{
									_image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0,voxel);
								}
							}
							else if (jj == _image[0].length-1)
							{
								if (_image[ii][jj]==labelIni &&(_image[ii-1][jj] == currentLabel || _image[ii][jj-1] == currentLabel))
								{
									_image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0,voxel);
								}
							
							}
							else
							{
								if (_image[ii][jj]==labelIni &&(_image[ii-1][jj] == currentLabel || _image[ii][jj-1] == currentLabel|| _image[ii][jj+1]== currentLabel))
								{
									_image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0,voxel);
								}
							
							}
						}
						else if (jj==0)
						{
							if (_image[ii][jj]==labelIni &&(_image[ii-1][jj] == currentLabel || _image[ii+1][jj] == currentLabel|| _image[ii][jj+1]== currentLabel))
							{
								_image[ii][jj] = currentLabel;
								VoxelRecord voxel = new VoxelRecord();
								voxel.setLocation(ii, jj, 0);
								voxelBoundary.add(0,voxel);
							}
						
						}
						else if (jj==_image[0].length-1)
						{
							if (_image[ii][jj]==labelIni &&(_image[ii-1][jj] == currentLabel || _image[ii+1][jj] == currentLabel|| _image[ii][jj-1] == currentLabel))
							{
								_image[ii][jj] = currentLabel;
								VoxelRecord voxel = new VoxelRecord();
								voxel.setLocation(ii, jj, 0);
								voxelBoundary.add(0,voxel);
							}
						
						}
					}
				}	
		}
	}
	
	
	
	/**
	 * 
	 * @param label
	 * @return
	 */
	private ArrayList<VoxelRecord> detectVoxelBoudary (double label)
	{
		ArrayList<VoxelRecord> lVoxelBoundary = new ArrayList<VoxelRecord>();
		for(int i = 0; i < _image.length; ++i )
			for(int j = 0; j < _image[i].length; ++j )
			{     
				if (_image[i][j] == label)
					if (i > 0 && i < _image.length-1 && j > 0 && j < _image[i].length-1)
					{
						if ( _image[i-1][j] == 0 || _image[i+1][j] == 0|| _image[i][j-1] == 0|| _image[i][j+1]== 0)
						{
							VoxelRecord voxelTest = new VoxelRecord();
							voxelTest.setLocation(i, j,0);
							lVoxelBoundary.add(voxelTest);	
						}
					}
					else if(i == 0)
					{
						if(j == 0)
						{
							if ( _image[i+1][j] == 0 || _image[i][j+1]== 0)
							{
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j,0);
								lVoxelBoundary.add(voxelTest);	
							}
						}
						else if (j == _image[0].length-1)
						{
							if (  _image[i+1][j] == 0|| _image[i][j-1] == 0)
							{
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j,0);
								lVoxelBoundary.add(voxelTest);	
							}
						}
						else
						{
							if ( _image[i+1][j] == 0|| _image[i][j-1] == 0|| _image[i][j+1]== 0)
							{
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j,0);
								lVoxelBoundary.add(voxelTest);	
							}
						}						
					}
					else if (i == _image.length-1)
					{
						if(j == 0)
						{
							if ( _image[i-1][j] == 0 || _image[i][j+1]== 0)
							{
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j,0);
								lVoxelBoundary.add(voxelTest);	
							}
						}
						else if (j == _image[0].length-1)
						{
							if ( _image[i-1][j] == 0 || _image[i][j-1] == 0)
							{
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j,0);
								lVoxelBoundary.add(voxelTest);	
							}
						}
						else
						{
							if ( _image[i-1][j] == 0 || _image[i][j-1] == 0|| _image[i][j+1]== 0)
							{
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j,0);
								lVoxelBoundary.add(voxelTest);	
							}
						}
						
					}
					else if(j == 0)
					{
						if ( _image[i-1][j] == 0 || _image[i+1][j] == 0|| _image[i][j+1]== 0)
						{
							VoxelRecord voxelTest = new VoxelRecord();
							voxelTest.setLocation(i, j,0);
							lVoxelBoundary.add(voxelTest);	
						}
					}
					else if (j == _image[0].length-1)
					{
						if ( _image[i-1][j] == 0 || _image[i+1][j] == 0|| _image[i][j-1] == 0)
						{
							VoxelRecord voxelTest = new VoxelRecord();
							voxelTest.setLocation(i, j,0);
							lVoxelBoundary.add(voxelTest);	
						}
					}
			}
	
		return lVoxelBoundary;
	}


	public ArrayList<Double> getListLabel(double labelIni) {
		
		
		computeLabel(labelIni);
		return _listLabel;
	}


	public double[][] getImageTable() {
		return _image;
	}


	double[][] computeLabelOfOneObject(int labelInitial, VoxelRecord voxelRecord)
	{

		int currentLabel=2;
		breadthFirstSearch(labelInitial,voxelRecord,currentLabel);
		return _image;		
		
	}
	
	public ArrayList<VoxelRecord> getBoudaryVoxel (int label)
	{
		return detectVoxelBoudary(label);
	}
	public void setImageTable(double[][] _image) {
		this._image = _image;
	}

}