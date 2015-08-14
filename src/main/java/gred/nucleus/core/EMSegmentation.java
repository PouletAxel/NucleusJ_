package gred.nucleus.core;

/**
 * 1) Éventuellement, faire un blur de faible écart type (masque gaussien) pour réduire le bruit
         => ca je comprend, je peux utiliser celui dans imageJ ou vaut mieux t'il que je developpe?

Celui d'imagej ira bien (au moins pour le mment)

2) Créer une image avec les pixels les plus sombres (seuil t sur le niveau de gris g < t)
         ok je comprend, le seuillage je le realise comment?  

if (image[i][j][k] > threshold)
    image[i][j][k] = 255
else
    image[i][j][k] = 0

3) Enlever les petites composantes de pixels sombres
          j'enèlve tout les objet gris de top petit volume?

Ben du coup, il ne dois plus y avoir de gris (c'est manichéen comme méthode :-) )
En fait, je réalise que pour détecter les petites composantes, il faudrait faire une autre image avec les pixels noirs dilatés (soit par morpho maths, soit comme au point 7) et 8) si ça va plus vite. La dilatation éviterait d'enlever des petites composantes qui font partie de l'enveloppe nucléaire.

Normalement, au point 3, il doit te rester seulement l'enveloppe nucléaire.
Les traitements suivants visent à construire l'intérieur (donc, par composante, des brins d'ADN, si je te suis bien), puis en reprenant le bord, l'enveloppe nucléaire "propre" : qui sépare topologiquement l'intérieur de l'extérieur (au sens des surfaces de Jordan-Brouwer)

4) Créer la carte de distance aux pixels sombres restant
            ok
5) Seuiller la carte de distance aux pixels sombres > r (genre entre 5 et 20 pixels). Appelons ces pixels le "deep kernel".
             ok

6) Labelliser les composantes connexes de pixels avec distance > r
             ok ca je suis pas sur de savoir faire...
7) Creer une nouvelle carte de distance aux points du deep kernel.
8) Seuiller les points de la  nouvelle carte de distance avec distance < r
 * @author axel
 *
 */
public class EMSegmentation {

}
