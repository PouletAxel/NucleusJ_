package gred.nucleus.utils;

public class ComponentInfo
{
	int m_label;
	int m_numberOfPoints;
	VoxelRecord m_oneVoxelIn;
	boolean m_componentOnTheBorder;
	
	public ComponentInfo(int label, int numberOfPoints,VoxelRecord oneVoxelIn, boolean componentOnTheBorder)
	{
		m_label = label;
		m_numberOfPoints = numberOfPoints;
		m_oneVoxelIn = oneVoxelIn;
		m_componentOnTheBorder = componentOnTheBorder;
	}
	
	public int getLabel() { return m_label; }
	
	public int getnumberOfPoints() { return m_numberOfPoints; }
	
	public VoxelRecord getOneVoxelIn() { return m_oneVoxelIn; }
	
	public boolean IsOnTheeBorder() { return m_componentOnTheBorder; }
	
	       
}
