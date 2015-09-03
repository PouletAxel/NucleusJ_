package gred.nucleus.utils;

import ij.IJ;

import java.util.ArrayList;

public class ComponentConnexe
{
	private double[][] _image;
	private ArrayList<Double> _listLabel = new ArrayList<Double>();
	
	
	private void breadthFirstSearch(int imageLabel, VoxelRecord voxelRecord, int currentLabel)
	{
		ArrayList<VoxelRecord> voxelBoundary = detectVoxelBoudary(imageLabel);
		IJ.log("taille de liste "+voxelBoundary.size());
		voxelBoundary.add(0, voxelRecord);
		while (! voxelBoundary.isEmpty())
		{
			VoxelRecord voxelRemove= voxelBoundary.remove(0);
			int imin = (int)voxelRemove._i-1;
			if (imin < 0) imin = 0;
			int imax = (int)voxelRemove._i+1;
			if (imax == _image.length) imax = _image.length-1;
			int jmin = (int)voxelRemove._j-1;
			if (jmin < 0) jmin = 0;
			int jmax = (int)voxelRemove._j+1;
			if (jmax == _image[0].length) jmax = _image[0].length-1;
			for (int i = imin ;i < imax; i++)
				for (int j = jmin;j < jmax; j++)
				{
					//IJ.log(" imin "+imin+" "+i+" imax "+imax+" "+i+" jmin "+jmin+" "+j+" jmax "+jmax+" "+j);
					if (_image[i][j]==imageLabel)
					{
						if ((i > 0 && i < imax) && (j > 0 && j < jmax))
						{
							if(_image[i-1][j] == currentLabel || _image[i+1][j] == currentLabel|| _image[i][j-1] == currentLabel|| _image[i][j+1]== currentLabel)
							{
								_image[i][j] = currentLabel;
								VoxelRecord voxel = new VoxelRecord();
								voxel.setLocation(i, j, 0);
								voxelBoundary.add(0,voxel);
							}
						}
					}
				}
		}
	}
		
	
	
	void computeLabel(int imageLabel)
	{
		int currentLabel=2;
		for(int i = 0; i < _image.length; ++i )
			for(int j = 0; j < _image[i].length; ++j )
				if (_image[i][j] == imageLabel)
				{
					_image[i][j] = currentLabel;
					VoxelRecord voxelRecord = new VoxelRecord();
					voxelRecord.setLocation(i, j, 0);
					breadthFirstSearch(imageLabel,voxelRecord,currentLabel);
					_listLabel.add((double)currentLabel);
					currentLabel++;	
				}
		}

	
	private ArrayList<VoxelRecord> detectVoxelBoudary (double label)
	{
		ArrayList<VoxelRecord> lVoxelBoundary = new ArrayList<VoxelRecord>();
		for(int i = 0; i < _image.length; ++i )
			for(int j = 0; j < _image[i].length; ++j )
			{     
				if (_image[i][j] == label)
				{
					if ((i > 0 && i < _image.length-1) && (j > 0 && j < _image[i].length-1))
					{
						if( _image[i-1][j] == 0 || _image[i+1][j] == 0|| _image[i][j-1] == 0|| _image[i][j+1]== 0)
						{
						  VoxelRecord voxelTest = new VoxelRecord();
						  voxelTest.setLocation(i, j,0);
						  lVoxelBoundary.add(voxelTest);	
						}
					}
					else
					{
						if(i==0)
						{
							IJ.log("je passe ici");
							if(j==0)
							{
								if( _image[i+1][j] == 0 || _image[i][j+1]== 0)
								{
								  VoxelRecord voxelTest = new VoxelRecord();
								  voxelTest.setLocation(i, j,0);
								  lVoxelBoundary.add(voxelTest);	
								}
							}
							else if (j==_image[i].length-1)
							{
								if(_image[i+1][j] == 0|| _image[i][j-1] == 0)
								{
								  VoxelRecord voxelTest = new VoxelRecord();
								  voxelTest.setLocation(i, j,0);
								  lVoxelBoundary.add(voxelTest);	
								}
							}
							else
							{
								if(_image[i+1][j] == 0|| _image[i][j-1] == 0|| _image[i][j+1]== 0)
								{
								  VoxelRecord voxelTest = new VoxelRecord();
								  voxelTest.setLocation(i, j,0);
								  lVoxelBoundary.add(voxelTest);	
								}
							}
						}
					}
				}
			}
	
		return lVoxelBoundary;
	}


	public ArrayList<Double> getListLabel(int imageLabel) {
		
		
		computeLabel(imageLabel);
		return _listLabel;
	}


	public double[][] getImageTable() {
		return _image;
	}




	public void setImageTable(double[][] _image) {
		this._image = _image;
	}

}