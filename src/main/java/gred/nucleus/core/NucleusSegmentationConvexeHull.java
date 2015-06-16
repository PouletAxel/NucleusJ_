package gred.nucleus.core;

import java.util.ArrayList;

import javax.imageio.stream.ImageInputStream;

import gred.nucleus.utils.VoxelRecord;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;

public class NucleusSegmentationConvexeHull
{
	
	/**
	 * 
	 * @param imagePlusInput
	 * @return
	 */
	public ArrayList<VoxelRecord> giftWrapping (ImagePlus imagePlusInput)
	{
		/*
		 * D'abord, l'algo est en 2D et il fait le faire par tranche (par exemple les tranches orthogonales à l'axe des z). 
		 * Éventuellement itérer pour les trois axes l'un après l'autre.
		 * 
		 * Tu initialise avec v_0=(x_0, y_0) le voxel à 1 le plus haut (y_0 maximal) 
		 * Ensuite, tu prends le point en cours (x_n,y_n)
		 * ((x_0, y_0) si c'est le premier coup) et le point précédent v_ {n-1} = (x_{n-1},y_{n-1}) (ou v_{-1} = (x_0 - 10,y_0)  si c'est le premier coup.
		 * Ensuite, tu considère le vecteur V_n = (x_n  -x_{n-1}, y_n - y_{n-1})
		 * ou V_0 = (10, 0) si n=0.
		 * angleMin = 2*pi
		 * voxelMin=v_n // ne devrait pas rester comme ça...
		 * Pour v voxel à 1 dans l'image{
		 *  calculer alpha l'angle entre 0 et 2*pi entre le vecteur V_n et le vecteur (v-v_n)
		 *   si (alpha < angleMin){
		 *   angleMin = alpha
		 *   voxelMin = v
		 *   }
		 *  }
		 *  v_ {n+1} = voxelMin
		 *  Pour calculer l'angle alpha, on utilise : 
		 *  sin alpha = produit vectoriel / produit des normes
		 *  cos alpha = produit scalaire / produit des normes
		 *  
		 *  
		 *  On inverse avec arccos en tenant compte du signe du sinus
		 *  et on cherche la détermination + ou - 2*k*pi qui se trouve dans [0, 2*pi[
		 *  
		 *  arccos( cos x ) = x + 2kπ, when k∈ℤ (k is integer)
		 * 
		 * 
		 * 
		 * Oui.

		 * 
		 */
		
		//Tu initialise avec v_0=(x_0, y_0) le voxel à 1 le plus haut (y_0 maximal) 
		 // v_0 = p0 ici
		ArrayList<VoxelRecord> lVoxelBoundary = new ArrayList<VoxelRecord>();
		ArrayList<VoxelRecord> convexeHull = new ArrayList<VoxelRecord>();
		ImageStack imageStackInput = imagePlusInput.getStack();
		VoxelRecord p0 = new VoxelRecord();
		double pi=Math.PI;
		
		p0.setLocation(0,0,0);
		
		for (int i = 0; i < imagePlusInput.getWidth(); ++i )
		{
			for (int j = 0; j < imagePlusInput.getHeight(); ++j )
			{				
				if (imageStackInput.getVoxel(i, j, 0) > 0)
				{
					if ( imageStackInput.getVoxel(i-1, j, 0) == 0 || imageStackInput.getVoxel(i+1, j, 0) == 0||
							imageStackInput.getVoxel(i, j-1, 0) == 0||imageStackInput.getVoxel(i, j+1, 0) == 0)
					{
						VoxelRecord voxelTest = new VoxelRecord();
						voxelTest.setLocation(i, j,0);
						lVoxelBoundary.add(voxelTest);
						if (j >= p0._j)
							if(i > p0._i)	p0.setLocation(i, j,0);
					}			
				}
			}
		}
		IJ.log("start point "+p0._i+" "+p0._j);
		convexeHull.add(p0);
		
		// Ensuite, tu prends le point en cours (x_n,y_n)
		// ((x_0, y_0) si c'est le premier coup) et le point précédent v_ {n-1} = (x_{n-1},y_{n-1}) (ou v_{-1} = (x_0 - 10,y_0)  si c'est le premier coup.
		// Ensuite, tu considère le vecteur V_n = (x_n  -x_{n-1}, y_n - y_{n-1})
		//(vCourant._i != p0._i && vCourant._j != p0._j)||
		int compteur = 0;
		VoxelRecord voxelTest = new VoxelRecord();
		VoxelRecord voxelPrecedent = new VoxelRecord();
		voxelTest.setLocation(0, 0, 0);
		boolean test = true;
		while(test)
		{
			double maxLength = 0;
			VoxelRecord vectorTest = new VoxelRecord();
			if (compteur == 0)
			{
				vectorTest.setLocation (10, 0, 0);
				voxelTest= p0;
			}
			else  vectorTest.setLocation(voxelTest._i-voxelPrecedent._i,voxelTest._j-voxelPrecedent._j, 0);
			
			double angleMin = 2*pi;
			VoxelRecord voxelMin= new VoxelRecord();
			for(int i=0; i<lVoxelBoundary.size(); i++)
			{
				VoxelRecord vectorCourant = new VoxelRecord();
				vectorCourant.setLocation(voxelTest._i-lVoxelBoundary.get(i)._i, voxelTest._j-lVoxelBoundary.get(i)._j, 0);
				double angle = computeAngle(vectorTest,vectorCourant);
				angle = (double)Math.round(angle* 100000000) / 100000000;
				//IJ.log("x "+lVoxelBoundary.get(i)._i+" y "+lVoxelBoundary.get(i)._j+" angle: "+angle+" angleMin: "+angleMin);	
				double distance = Math.sqrt(vectorCourant._i*vectorCourant._i+vectorCourant._j*vectorCourant._j);			
				if(angle <= angleMin)
				{
					if ((angle == 0 || angle == angleMin || angleMin==2*pi) && distance > maxLength)
					{
						//IJ.log("PUTAIN DE VOXEL COLINEAIRE x "+lVoxelBoundary.get(i)._i+" y "+lVoxelBoundary.get(i)._j+" distance "+distance+" angle: "+angle);
						maxLength = distance;
						angleMin = angle;
						voxelMin =lVoxelBoundary.get(i);
					}
					else if(angle < angleMin)
					{
						maxLength = distance;
						angleMin = angle;
						voxelMin =lVoxelBoundary.get(i);
					}
				}			
			}
			convexeHull.add(voxelMin);
			IJ.log("point num: "+compteur+" "+voxelMin._i+" "+voxelMin._j+" angle: "+angleMin);
			++compteur;
			
			voxelPrecedent = voxelTest;
			voxelTest = voxelMin;
			
			if (p0._i == voxelTest._i && p0._j == voxelTest._j ) 	test = false;
			
		}
		
		return convexeHull;
	}
	/*
	 * Pour calculer l'angle alpha, on utilise : 
	 *  produit vectoriel (U^V)z = Ux*Vy-Uy*Vx
	 *  produit scalaire V.U= Vx*Ux+Vy*Uy
	 *  produit des normes = racineCarre(Vx*Vx+Vy*Vy)*racineCarre(Ux*Ux+Uy*Uy)
	 *  sin alpha = produit vectoriel / produit des normes
	 *  cos alpha = produit scalaire / produit des normes
	 */
	
	/**
	 * 
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	double computeAngle(VoxelRecord vector1, VoxelRecord vector2)
	{
		double pi=Math.PI;
		double normesProduct = Math.sqrt(vector1._i*vector1._i+vector1._j*vector1._j)*Math.sqrt(vector2._i*vector2._i+vector2._j*vector2._j);
		double sinAlpha = (vector1._i*vector2._j-vector1._j*vector2._i)/normesProduct;
		double cosAlpha = (vector1._i*vector2._i+vector1._j*vector2._j)/normesProduct;
		//On inverse avec arccos en tenant compte du signe du sinus
		double acos = Math.acos(cosAlpha);
		//on cherche la détermination + ou - 2*k*pi qui se trouve dans [0, 2*pi[
		if (acos < 0 ) acos =+ 2*pi ;
		else if (acos > 2*pi ) acos =- 2*pi;
		if (sinAlpha < 0) acos = acos*-1;
		return acos;
	}
	
	/**
	 * 
	 * @param imagePlusInput
	 * @param label
	 * @return
	 */

	public double equivalentSphericalRadius (double volume)
	{
		double radius =  (3 * volume) / (4 * Math.PI);
		radius = Math.pow(radius, 1.0/3.0);
		return radius;
	}
}
