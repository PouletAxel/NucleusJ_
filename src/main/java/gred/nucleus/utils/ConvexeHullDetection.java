package gred.nucleus.utils;

import java.util.ArrayList;
import gred.nucleus.core.Measure3D;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;

public class ConvexeHullDetection
{
	VoxelRecord _p0 = new VoxelRecord();
	double _pi=Math.PI;
	String _axes = "";
	/*
  	* 1) On calcule l'angle alpha comme précédemment :
  	* double acos = Math.acos(cosAlpha)
  	* 	double alpha;
  	* 	if (sinAlpha < 0)
  	* 		alpha = -acos;
  	* 	else
  	* 		alpha = acos;
  	* 2) L'angle alpha est l'angle entre la "tangente"  (vecteur entre v_{n-1} et v_n) à la frontière, calculée précédemment, et le vecteur vers les autres voxels.
  	* L'algo précédent doit marche pour une vrai enveloppe convexe (à mettre au point avant de coder la version avec seuillage sur la distance !).
  	* Pour une vrai enveloppe convexe, normalement, l'angle alpha doit toujours, pour tous les voxels, être entre 0 et pi (à vérifier !).
  	* Dans notre algo avec seuillage sur la distance, justement, on peut autoriser des angles alphas négatifs, car le polygone de sortie n'est pas forcément convexe.
  	* On doit alors ruser en considérant non pas l'angle entre la "tangente" à la frontière, calculée précédemment, et le vecteur vers les autres voxels, mais l'angle
  	* (ou plutôt son représentant pris entre -pi et pi) entre la "normale entrante", qui est la "tangente" (vecteur entre v_{n-1} et v_n) plus pi/2.
  	* Ça donne quelque chose comme :
  	* 	
  	* double angle = computeAngle(vectorTest,vectorCourant);
  	* double anglePlusPiSurDeux = angle - pi/2;
  	* if (anglePlusPiSurDeux < -pi)
  	*         anglePlusPiSurDeux += 2*pi;
  	*  if(anglePlusPiSurDeux <= angleMin )
  	*  angleMin = anglePlusPiSurDeux;
  	*  
  	*    etc.
  	*   Par contre, pour voir si on a "fait le tour" (somme des angles dépasse 2*pi), on peut garder l'ancien angle...
	*/	
	/**
	 * 
	 * @param imagePlusInput
	 * @return
	 */
	public  ArrayList<VoxelRecord> giftWrapping (ImagePlus imagePlusInput)
	{
		ArrayList<VoxelRecord> convexHull = new ArrayList<VoxelRecord>();
		Calibration calibration = imagePlusInput.getCalibration();
		Measure3D mesure3d = new Measure3D();
		int x = imagePlusInput.getWidth();
		int y = imagePlusInput.getHeight();
		int z =imagePlusInput.getNSlices();
		double equivalentSphericalRadius = mesure3d.equivalentSphericalRadius(imagePlusInput);
		//IJ.log("ers " +equivalentSphericalRadius);
		if (_axes =="xy")
		{
			for (int k = 0; k < z; ++k )
			{
				ArrayList<VoxelRecord> lVoxelBoundary = detectVoxelBoudary(imagePlusInput,k);
				//IJ.log("boundary "+lVoxelBoundary.size());
				if (lVoxelBoundary.size() > 10)
				{
					//IJ.log("start point "+_p0._i+" "+_p0._j+" "+_p0._k);
					convexHull.add(_p0);
					VoxelRecord vectorTest = new VoxelRecord();
					vectorTest.setLocation (-10, 0, 0);
					convexHull = findConvexeHull(convexHull,lVoxelBoundary, vectorTest, calibration,equivalentSphericalRadius);
				}
			}
		}
		else if (_axes =="xz")
		{
			for (int j = 0; j < y; ++j )
			{
				ArrayList<VoxelRecord> lVoxelBoundary = detectVoxelBoudary(imagePlusInput,j);
				//IJ.log("boundary "+lVoxelBoundary.size());
				if (lVoxelBoundary.size() > 10)
				{
					//IJ.log("start point "+_p0._i+" "+_p0._j+" "+_p0._k);
					convexHull.add(_p0);
					VoxelRecord vectorTest = new VoxelRecord();
					vectorTest.setLocation (-10, 0, 0);
					convexHull = findConvexeHull(convexHull,lVoxelBoundary, vectorTest, calibration,equivalentSphericalRadius);
				}
			}
		}
		else if (_axes =="yz")
			{
				for (int i = 0; i < x; ++i )
				{
					ArrayList<VoxelRecord> lVoxelBoundary = detectVoxelBoudary(imagePlusInput,i);
					//IJ.log("boundary "+lVoxelBoundary.size());
					if (lVoxelBoundary.size() > 10)
					{
						//IJ.log("start point "+_p0._i+" "+_p0._j+" "+_p0._k);
						convexHull.add(_p0);
						VoxelRecord vectorTest = new VoxelRecord();
						vectorTest.setLocation (0, -10, 0);
						convexHull = findConvexeHull(convexHull, lVoxelBoundary, vectorTest, calibration,equivalentSphericalRadius);
					}
				}
			}
		return convexHull;
	}
	
	/*
	 * Pour calculer l'angle alpha, on utilise : 
	 *  produit vectoriel (U^V)z = Ux*Vy-Uy*Vx
	 *  produit scalaire V.U= Vx*Ux+Vy*Uy
	 *  produit des normes = racineCarre(Vx*Vx+Vy*Vy)*racineCarre(Ux*Ux+Uy*Uy)
	 *  sin alpha = produit vectoriel / produit des normes
	 *  cos alpha = produit scalaire / produit des normes
	 *  Pour calculer l'angle alpha, on utilise
sin alpha = produit vectoriel / produit des normes
cos alpha = produit scalaire / produit des normes
On inverse avec arccos en tenant compte du signe du sinus
et on cherche la détermination + ou - 2*k*pi qui se trouve dans [0, 2*pi[
	 */
	
	/**
	 * sweetsweet sun
	 * 
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	double computeAngle(VoxelRecord vector1, VoxelRecord vector2, Calibration calibration)
	{
		double xcal = calibration.pixelWidth;
		double ycal = calibration.pixelHeight;
		double zcal = calibration.pixelDepth;
		double normeVector1 = Math.sqrt(vector1._i*xcal*vector1._i*xcal+vector1._j*ycal*vector1._j*ycal+vector1._k*zcal*vector1._k*zcal);
		double normeVector2 = Math.sqrt(vector2._i*xcal*vector2._i*xcal+vector2._j*ycal*vector2._j*ycal+vector2._k*zcal*vector2._k*zcal);
		double normesProduct = normeVector1*normeVector2;
		double sinAlpha = 0, cosAlpha=0;
		
		if (_axes == "xy") 
		{
			sinAlpha = ((vector1._i*xcal)*(vector2._j*ycal)-(vector1._j*ycal)*(vector2._i*xcal))/normesProduct;
			cosAlpha = ((vector1._i*xcal)*(vector2._i*xcal)+(vector1._j*ycal)*(vector2._j*ycal))/normesProduct;
		}
		else if (_axes == "xz") 
		{
			sinAlpha = ((vector1._i*xcal)*(vector2._k*zcal)-(vector1._k*zcal)*(vector2._i*xcal))/normesProduct;
			cosAlpha = ((vector1._i*xcal)*(vector2._i*xcal)+(vector1._k*zcal)*(vector2._k*zcal))/normesProduct;
		}
		else if (_axes == "yz")
		{
			sinAlpha = ((vector1._j*ycal)*(vector2._k*zcal)-(vector1._k*zcal)*(vector2._j*ycal))/normesProduct;
			cosAlpha = ((vector1._j*ycal)*(vector2._j*ycal)+(vector1._k*zcal)*(vector2._k*zcal))/normesProduct;
		}	
		double acos = Math.acos(cosAlpha);
		double alpha;
		if (sinAlpha < 0)
			alpha = -acos;
		else
			alpha = acos;
		return alpha;
	}
	/**
	 * 
	 * @param p
	 * @param q
	 * @param r
	 * @return
	 */
	int orientation (VoxelRecord p, VoxelRecord q, VoxelRecord r)
	{
		int turn = (int)((q._i - p._i) * (r._j - p._j) - (r._i - p._i) * (q._j - p._j));
		if (turn > 0 ) return 1;
		else if (turn < 0) return -1;
		else return 0;
	}
	
	/**
	 * 
	 * @param imagePlusInput
	 * @param indice
	 * @return
	 */
	ArrayList<VoxelRecord> detectVoxelBoudary (ImagePlus imagePlusInput, int indice)
	{
		ImageStack imageStackInput = imagePlusInput.getStack();
		ArrayList<VoxelRecord> lVoxelBoundary = new ArrayList<VoxelRecord>();
		int x = imagePlusInput.getWidth();
		int y = imagePlusInput.getHeight();
		int z =imagePlusInput.getNSlices();
		_p0.setLocation(0,0,0);
		if (_axes == "xy")
		{
			for (int i = 0; i < x; ++i )
				for (int j = 0; j < y; ++j )
					if (imageStackInput.getVoxel(i, j, indice) > 0)
						if ( imageStackInput.getVoxel(i-1, j, indice) == 0 || imageStackInput.getVoxel(i+1, j, indice) == 0||
							imageStackInput.getVoxel(i, j-1, indice) == 0||imageStackInput.getVoxel(i, j+1, indice) == 0)
						{
							VoxelRecord voxelTest = new VoxelRecord();
							voxelTest.setLocation(i, j,indice);
							lVoxelBoundary.add(voxelTest);
							if (j >= _p0._j)
								if(i > _p0._i)	_p0.setLocation(i, j,indice);
						}

		}
		else if (_axes == "xz")
		{
			for (int i = 0; i < imagePlusInput.getWidth(); ++i )
				for (int k = 0; k < z; ++k )
					if (imageStackInput.getVoxel(i, indice,k) > 0)
						if ( imageStackInput.getVoxel(i-1, indice, k) == 0 || imageStackInput.getVoxel(i+1, indice, k) == 0||
							imageStackInput.getVoxel(i, indice, k-1) == 0||imageStackInput.getVoxel(i, indice, k+1) == 0)
						{
							VoxelRecord voxelTest = new VoxelRecord();
							voxelTest.setLocation(i, indice, k);
							lVoxelBoundary.add(voxelTest);
							if (k >= _p0._k)
								if(i > _p0._i)	_p0.setLocation (i, indice, k);
						}

		}
		else if (_axes == "yz")
		{
			for (int j = 0; j < imagePlusInput.getWidth(); ++j )
				for (int k = 0; k < z; ++k )
					if (imageStackInput.getVoxel(indice, j, k) > 0)
						if ( imageStackInput.getVoxel(indice, j-1, k) == 0 || imageStackInput.getVoxel(indice, j+1, k) == 0||
							imageStackInput.getVoxel(indice, j, k-1) == 0||imageStackInput.getVoxel( indice,j , k+1) == 0)
						{
							VoxelRecord voxelTest = new VoxelRecord();
							voxelTest.setLocation(indice, j, k);
							lVoxelBoundary.add(voxelTest);
							if (k >= _p0._k)
								if(j > _p0._j)	_p0.setLocation ( indice,j , k);
						}

		}
		return lVoxelBoundary;
	}
	
	/**
	 * 
	 * @param lVoxelBoundary
	 * @param vectorTest
	 * @param calibration
	 */
	
	private ArrayList<VoxelRecord> findConvexeHull(ArrayList<VoxelRecord> convexHull, ArrayList<VoxelRecord> lVoxelBoundary,  VoxelRecord vectorTest, Calibration calibration, double ers )
	{
		double anglesSum = 0.0;	
		int compteur = 0;
		VoxelRecord voxelTest = new VoxelRecord();
		VoxelRecord voxelPrecedent = new VoxelRecord();
		voxelTest = _p0;
		double xcal = calibration.pixelWidth;
		double ycal = calibration.pixelHeight;
		double zcal = calibration.pixelDepth;
		while(anglesSum < 2*_pi)
		{
			double angleMin = 0;
			double maxLength = 0;
			double distance = 0;
			double angleMinPiSurDeux = 2*_pi;
			VoxelRecord voxelMin= new VoxelRecord();
			int iMin=0;
			if (compteur != 0)  vectorTest.setLocation(voxelTest._i-voxelPrecedent._i,voxelTest._j-voxelPrecedent._j, voxelTest._k-voxelPrecedent._k);
						
			for(int i=0; i<lVoxelBoundary.size(); i++)
			{		
				if(voxelTest.compareCooridnatesTo(lVoxelBoundary.get(i))==1)
				{
					VoxelRecord vectorCourant = new VoxelRecord();
					vectorCourant.setLocation(lVoxelBoundary.get(i)._i-voxelTest._i, lVoxelBoundary.get(i)._j-voxelTest._j, lVoxelBoundary.get(i)._k-voxelTest._k);
					if(_axes == "xy")	distance = Math.sqrt(vectorCourant._i*xcal*vectorCourant._i*xcal+vectorCourant._j*ycal*vectorCourant._j*ycal);
					else if (_axes == "xz")	distance = Math.sqrt(vectorCourant._i*xcal*vectorCourant._i*xcal+vectorCourant._k*zcal*vectorCourant._k*zcal);
					else if (_axes == "yz")	distance = Math.sqrt(vectorCourant._k*zcal*vectorCourant._k*zcal+vectorCourant._j*ycal*vectorCourant._j*ycal);
					//int sens = orientation(voxelPrecedent,voxelTest, lVoxelBoundary.get(i));
					if (distance <= ers )
					{
						double angle = computeAngle(vectorTest,vectorCourant,calibration); 
						double anglePlusPiSurDeux = angle -_pi/2;
						if (anglePlusPiSurDeux < -_pi)
							anglePlusPiSurDeux += 2*_pi;
				  	  	if(anglePlusPiSurDeux <= angleMinPiSurDeux)
				  	  	{
				  	  		if(anglePlusPiSurDeux < angleMinPiSurDeux)
				  	  		{
				  	  			maxLength = distance;
				  	  			angleMinPiSurDeux = anglePlusPiSurDeux;
				  	  			angleMin = angle;
				  	  			voxelMin = lVoxelBoundary.get(i);
				  	  			iMin = i;
				  	  		}
				  	  		else if (anglePlusPiSurDeux == angleMinPiSurDeux && distance > maxLength)
				  	  		{
				  	  			maxLength = distance;
				  	  			angleMinPiSurDeux = anglePlusPiSurDeux;
				  	  			angleMin = angle;
				  	  			voxelMin =lVoxelBoundary.get(i);
				  	  			iMin = i;
				  	  		}
				  	  	}
					}
				}
			}
			++compteur;
			voxelPrecedent = voxelTest;
			voxelTest = voxelMin;
			lVoxelBoundary.remove(iMin);
			anglesSum += angleMin;
			if (anglesSum < 2*_pi)
			{
				convexHull.add(voxelMin);
				//IJ.log("point num: "+compteur+" "+voxelMin._i+" "+voxelMin._j+" "+voxelMin._k+" angle: "+angleMinPiSurDeux+" distance: "+maxLength+" angle sum"+anglesSum);
			}
		}
		return convexHull;
	}
	
	public String getAxes () {return _axes;}
	public void setAxes(String axes){ _axes=axes;}
}