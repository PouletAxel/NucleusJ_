package gred.nucleus.utils;
/**
 * Class to create a voxel with its coordinates in the three dimensions and its value
 *
 * @author Philippe Andrey and Poulet Axel
 */
@SuppressWarnings("rawtypes")
public class VoxelRecord implements Comparable
{
  /** Coordinates voxel*/
  public double _i, _j, _k;
  /** Voxel value*/
  double _value;
  
  /**
   * Constructor
   *
   * @param i Coordinates x of voxel
   * @param j Coordinates y of voxel
   * @param k Coordinates z of voxel
   */
  public void setLocation(double i, double j, double k)
  {
    this._i = i;
    this._j = j;
    this._k = k;
  }
  /**
   * Return the x coordinates of a voxel
   * @return 
   */
  public double getI() {   return _i;  }

  /**
   * Return the y coordinates of a voxel
   * @return
   */
  
  public double getJ()  {  return _j; }

  /**
   * Return the z coordinates of a voxel
   * @return 
   */
  
  public double getK() {  return _k;  }

  /**
   * Initialise the voxel value
   * @param value
   */
  
  public void setValue(double value) {   this._value = value; }

  /**
   * Return the voxel value
   * @return
   */

  public double getValue() {  return _value;  }

  /**
   * Compare the values of two voxel
   * 0 if same voxel value
   * -1 if value of voxel input > value voxel
   * 1 if value of voxel input < value voxel
   * @param object a voxel
   * @return results of comparaison
   */

  public int compareTo(Object object)
  {
    VoxelRecord voxelRecord = (VoxelRecord)object;

    if ( _value == voxelRecord._value )
    	return 0;
    else if ( _value < voxelRecord._value )
    	return -1;
    else
    	return 1;
  }

  /**
   * Compare the values of two voxel
   * 0 if same voxel value
   * -1 if value of voxel input > value voxel
   * 1 if value of voxel input < value voxel
   * @param object a voxel
   * @return results of comparaison
   */

  public int compareCooridnatesTo(Object object)
  {
    VoxelRecord voxelRecord = (VoxelRecord)object;

    if ( _i == voxelRecord._i && _j == voxelRecord._j && _k == voxelRecord._k)
    	return 0;
   else
    	return 1;
  }
  /**
   * Compute a addition between the coordinates between two voxel
   * @param p a VoxelRecord
   */

  public void shiftCoordinates (VoxelRecord p) {   this.setLocation(  this._i+p._i, this._j+p._j, this._k+p._k); }

  /**
   * Multiplied the coordinates of voxel with a differnts factor for each coordinates
   * @param a
   * @param b
   * @param c
   */
  
  public void multiplie (double a, double b, double c ) {  this.setLocation(this._i*a,this._j*b, this._k*c); }

  /**
  * Multiplied the coordinates of voxel with a same factor for each coordinates
   * @param a
   */
  public void multiplie (double a)  {  this.setLocation(this._i*a,this._j*a,this._k*a);  }
}