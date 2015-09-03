package gred.nucleus.utils;


import java.util.ArrayList;

import ij.IJ;
import ij.measure.Calibration;

public class ConvexeHullDetection
{
	private VoxelRecord _p0 = new VoxelRecord();
	private double _pi=Math.PI;
	private String _axesName = "";
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
  	* Pour calculer l'angle alpha, on utilise : 
  	*  produit vectoriel (U^V)z = Ux*Vy-Uy*Vx
  	*  produit scalaire V.U= Vx*Ux+Vy*Uy
  	*  produit des normes = racineCarre(Vx*Vx+Vy*Vy)*racineCarre(Ux*Ux+Uy*Uy)
  	*  sin alpha = produit vectoriel / produit des normes
  	*  cos alpha = produit scalaire / produit des normes
  	*  Pour calculer l'angle alpha, on utilise
  	*  sin alpha = produit vectoriel / produit des normes
  	*  cos alpha = produit scalaire / produit des normes
  	*  On inverse avec arccos en tenant compte du signe du sinus
  	*  et on cherche la détermination + ou - 2*k*pi qui se trouve dans [0, 2*pi[
	 */
	
	/**
	 * 
	 * @param lVoxelBoundary
	 * @param vectorTest
	 * @param calibration
	 */
	
	public ArrayList <VoxelRecord> findConvexeHull(double[][] image,ArrayList<VoxelRecord> convexHull, ArrayList<VoxelRecord> lVoxelBoundary,  VoxelRecord vectorTest, Calibration calibration, double ers )
	{
		double anglesSum = 0.0;	
		int compteur = 0;
		VoxelRecord voxelTest = new VoxelRecord();
		VoxelRecord voxelPrecedent = new VoxelRecord();
		voxelTest = _p0;
		//IJ.log("plopi voxeldepart : "+_p0._i+" "+_p0._j+" "+_p0._k);
		double xcal = calibration.pixelWidth;
		double ycal = calibration.pixelHeight;
		double zcal = calibration.pixelDepth;
		while(anglesSum < 2*_pi+1)
		{
			double angleMin = 0;
			double maxLength = 0;
			double distance = 0;
			double angleMinPiSurDeux = 2*_pi;
			VoxelRecord voxelMin= new VoxelRecord();
			int iMin=0;
			int aMin = 0;
			if (compteur != 0)  vectorTest.setLocation(voxelTest._i-voxelPrecedent._i,voxelTest._j-voxelPrecedent._j, voxelTest._k-voxelPrecedent._k);
						
			for(int i=0; i<lVoxelBoundary.size(); i++)
			{		
				if(voxelTest.compareCooridnatesTo(lVoxelBoundary.get(i))==1)
				{
					VoxelRecord vectorCourant = new VoxelRecord();
					vectorCourant.setLocation(lVoxelBoundary.get(i)._i-voxelTest._i, lVoxelBoundary.get(i)._j-voxelTest._j, lVoxelBoundary.get(i)._k-voxelTest._k);
					if(_axesName == "xy")	distance = Math.sqrt(vectorCourant._i*xcal*vectorCourant._i*xcal+vectorCourant._j*ycal*vectorCourant._j*ycal);
					else if (_axesName == "xz")	distance = Math.sqrt(vectorCourant._i*xcal*vectorCourant._i*xcal+vectorCourant._k*zcal*vectorCourant._k*zcal);
					else if (_axesName == "yz")	distance = Math.sqrt(vectorCourant._k*zcal*vectorCourant._k*zcal+vectorCourant._j*ycal*vectorCourant._j*ycal);
					if (distance <= ers )
					{
						int a = orientation(voxelPrecedent,voxelTest,lVoxelBoundary.get(i));
						double angle = computeAngle(vectorTest,vectorCourant,calibration); 
						double anglePlusPiSurDeux = angle +_pi/2;
						
						if (anglePlusPiSurDeux >= _pi)
							anglePlusPiSurDeux -= 2*_pi;
						//IJ.log("orientation "+a+" "+lVoxelBoundary.get(i)._i+" "+lVoxelBoundary.get(i)._j+" "+lVoxelBoundary.get(i)._k+" angle: "+anglePlusPiSurDeux+" angleMin: "+angleMinPiSurDeux+" angle "+angle);
					  	
						angleThreshold(image, lVoxelBoundary.get(i),calibration, ers);
						if(anglePlusPiSurDeux <= angleMinPiSurDeux)
				  	  	{
				  	   		if(anglePlusPiSurDeux < angleMinPiSurDeux)
				  	  		{
				  	  			maxLength = distance;
				  	  			angleMinPiSurDeux = anglePlusPiSurDeux;
				  	  			angleMin = angle;
				  	  			voxelMin = lVoxelBoundary.get(i);
				  	  			iMin = i;
				  	  		    aMin = a;
				  	  		}
				  	  		else if (anglePlusPiSurDeux == angleMinPiSurDeux && distance > maxLength)
				  	  		{
				  	  			maxLength = distance;
				  	  			angleMinPiSurDeux = anglePlusPiSurDeux;
				  	  			angleMin = angle;
				  	  			voxelMin =lVoxelBoundary.get(i);
				  	  			iMin = i;
				  	  			aMin = a;
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
			if (voxelMin.compareCooridnatesTo(_p0) == 0)	break;
		
			if (anglesSum <= 2*_pi)
			{
				convexHull.add(voxelMin);
				//IJ.log("point num: "+compteur+" orietation "+aMin+" "+voxelMin._i+" "+voxelMin._j+" "+voxelMin._k+" angle: "+angleMinPiSurDeux+" distance: "+maxLength+" angle sum"+anglesSum);
			}
		}
		return convexHull;
	}
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
		
		if (_axesName == "xy") 
		{
			sinAlpha = ((vector1._i*xcal)*(vector2._j*ycal)-(vector1._j*ycal)*(vector2._i*xcal))/normesProduct;
			cosAlpha = ((vector1._i*xcal)*(vector2._i*xcal)+(vector1._j*ycal)*(vector2._j*ycal))/normesProduct;
		}
		else if (_axesName == "xz") 
		{
			sinAlpha = ((vector1._i*xcal)*(vector2._k*zcal)-(vector1._k*zcal)*(vector2._i*xcal))/normesProduct;
			cosAlpha = ((vector1._i*xcal)*(vector2._i*xcal)+(vector1._k*zcal)*(vector2._k*zcal))/normesProduct;
		}
		else if (_axesName == "yz")
		{
			sinAlpha = ((vector1._j*ycal)*(vector2._k*zcal)-(vector1._k*zcal)*(vector2._j*ycal))/normesProduct;
			cosAlpha = ((vector1._j*ycal)*(vector2._j*ycal)+(vector1._k*zcal)*(vector2._k*zcal))/normesProduct;
		}	
		if (cosAlpha > 1 ) cosAlpha=1;
		else if(cosAlpha < -1 ) cosAlpha=-1;
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
	
	public String getAxes () {return _axesName;}
	public void setAxes(String axes){ _axesName=axes;}
	public void setInitialVoxel (VoxelRecord voxelRecord){_p0=voxelRecord;}

	/*Soit d notre seuil de distance et C(v, D) le carré de centre v et de rayon d (dans le plan considéré).
	Copier l'image dans le carré C(v, D) dans une petite image I_c
	inverser les zéros et les uns dans I_c
	Mettre v (où plutôt le voxel qui lui correspond qui doit être le centre de I_c) à 1.
	Etiqueter à 2 la composante connexe des 1 qui contient v dans I_c (faire un parcours breadthFirstSerach comme l'autre jour)
	Pour chaque voxel  w du bord de I_c qui est à 2, calculer 
	double angleEntreZeroEt2pi = computeAngle(vectorTest,w-v,calibration) + _pi
	Calculer angleEntreZeroEt2piMax le maximum des angles obtenus.
	thresholdAngle = (angleEntreZeroEt2piMax).
	si (thresholdAngle >= _pi)
	thresholdAngle -= 2pi */
	
	
	private double angleThreshold (double[][] image, VoxelRecord voxelRecod, Calibration calibration, double distance)
	{
		double angleThreshold = 0;
		int nbPixelWidth = (int) (distance/calibration.pixelWidth);
		int nbPixelHeight = (int) (distance/calibration.pixelHeight);
		int i=(int)voxelRecod._i;
		int j=(int)voxelRecod._j;
		if (_axesName == "xz")
		{
			j=(int)voxelRecod._k;
			nbPixelHeight = (int) (distance/calibration.pixelDepth);
		}
		else if (_axesName == "yz")
		{
			i=(int)voxelRecod._j;
			j=(int)voxelRecod._k;
			nbPixelWidth = (int)(distance/calibration.pixelHeight);
			nbPixelHeight = (int)(distance/calibration.pixelDepth);
		}
		double value = image[i][j];
		int minWidth = i - nbPixelWidth;
		int maxWidth = i + nbPixelWidth;
		int minHeight = j - nbPixelHeight;
		int maxHeight = j + nbPixelHeight;
		if (minWidth < 0) minWidth = 0;
		if (maxWidth >= image.length) minWidth = image.length-1;
		if (minHeight < 0) minHeight = 0;
		if (maxHeight >= image[0].length) maxHeight = image[0].length-1;
		double[][] i_c = new double[nbPixelWidth*2][nbPixelHeight*2];
		IJ.log("width "+minWidth+" "+maxWidth+" height "+minHeight+" "+maxHeight+" ers "+distance+" plop "+ calibration.pixelWidth);
		int k=0;
		int cmpt=0;
		for (i = minWidth;i <maxWidth;++i)
		{
			int l=0;
			for (j = minHeight;j < maxHeight;++j)
			{
				if (image[i][j]== value) i_c[k][l] = 0;
				else
				{
					i_c[k][l] = 1;
					cmpt++;
				}
				++l;
			}
			++k;
		}
		image[nbPixelWidth][nbPixelHeight]=1;		
		
		ComponentConnexe componentConnexe = new ComponentConnexe();
		componentConnexe.setImageTable(i_c);
		  componentConnexe.getListLabel(1);
		i_c = componentConnexe.getImageTable();
		String plop="";
		for (int m = 0; m < 28;++ m)
			plop += m+" ";
		IJ.log(plop);
		for (i = 0; i < i_c.length; ++i)
		{
			plop="";
			for (j = 0;j < i_c[0].length; ++j)
				plop += i_c[i][j]+" ";
			IJ.log(plop);
		}
		
		
		return 	angleThreshold;	
		
	}
	
}