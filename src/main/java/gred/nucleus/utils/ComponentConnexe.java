package gred.nucleus.utils;

import java.util.ArrayList;

public class ComponentConnexe
{
	private double[][] _image;
	private ArrayList<Double> _listLabel = new ArrayList<Double>();
	
	
	private void breadthFirstSearch( VoxelRecord voxelRecord,	int currentLabel)
	{
		ArrayList<VoxelRecord> voxelBoundary = detectVoxelBoudary(255);
		voxelBoundary.add(0, voxelRecord);
		while (! voxelBoundary.isEmpty())
		{
			VoxelRecord voxelRemove= voxelBoundary.remove(0);
			for (int i = (int)voxelRemove._i-1;i <= (int)voxelRemove._i+1; i++)
				for (int j = (int)voxelRemove._j-1;j <= (int)voxelRemove._j+1; j++)
					if (_image[i][j]==255 && (_image[i-1][j] == currentLabel || _image[i+1][j] == currentLabel|| _image[i][j-1] == currentLabel|| _image[i][j+1]== currentLabel))
					{
						_image[i][j] = currentLabel;
						 VoxelRecord voxel = new VoxelRecord();
						voxel.setLocation(i, j, 0);
						voxelBoundary.add(0,voxel);
					}			
		}
	}
	
	
	
	
	void computeLabel()
	{
		int currentLabel=2;
		for(int i = 0; i < _image.length; ++i )
			for(int j = 0; j < _image[i].length; ++j )
				if (_image[i][j] == 255)
				{
					_image[i][j] = currentLabel;
					VoxelRecord voxelRecord = new VoxelRecord();
					voxelRecord.setLocation(i, j, 0);
					breadthFirstSearch(voxelRecord,currentLabel);
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
					if ( _image[i-1][j] == 0 || _image[i+1][j] == 0|| _image[i][j-1] == 0|| _image[i][j+1]== 0)
					{
						  VoxelRecord voxelTest = new VoxelRecord();
						  voxelTest.setLocation(i, j,0);
						  lVoxelBoundary.add(voxelTest);	
					}
			}
	
		return lVoxelBoundary;
	}


	public ArrayList<Double> getListLabel() {
		
		
		computeLabel();
		return _listLabel;
	}


	public double[][] getImageTable() {
		return _image;
	}




	public void setImageTable(double[][] _image) {
		this._image = _image;
	}

}
