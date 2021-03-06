NucleusJ_
================

New version available here:
================

NulceusJ2.0 https://gitlab.com/DesTristus/NucleusJ2.0
================

#
NucleusJ 2.0 is a new release of NucleusJ, in which image processing is achieved more quickly using a command-line
user interface. Starting with large collection of 3D nuclei, segmentation can be performed by the previously 
developed Otsu-modified method or by a new 3D gift-wrapping method, taking better account of nuclear indentations and unmarked nucleoli.

A discrete geometric method was introduced to improve the surface area calculation, a key parameter when studying nuclear morphology, 
replacing an imageJ default tool by a new one that includes pixel context information.

To increase the number of nuclei considered in a single analysis, a method was introduced to delimit an automatic bounding volume (autocrop)
around each nucleus of a 3D wide-field stack containing ten to a hundred nuclei. Each of the collected nuclei can then
be segmented through two complementary methods, either based on the Otsu threshold method or on edge-detection through a 3D gift-wrapping method.




#

#

#

#

#

#
Ensemble of ImageJ plugins for nuclear analysis

This plugin is dedicated to researchers interested in nuclear shape and chromatin organization. Starting from image stacks, the nuclear boundary as well as nuclear bodies are segmented. As output, NucleusJ automatically measures 15 parameters quantifying shape and size of nuclei as well as intra-nuclear objects and the positioning of the objects within the nuclear volume.

The plugin contains several methods to process and analyze 8 grey level image stacks of nuclei. For each method two versions are available, one version to analyze one image at a time and another for processing in batch mode.

Documentation : http://imagejdocu.tudor.lu/doku.php?id=plugin:stacks:nuclear_analysis_plugin:start

Installing NucleusJ_ for developers
===========================

NucleusJ_ has three major dependencies as set in the pom.xml: Jama, imascience and MorphoLibJ_ (version 1.0.6).

Jama and imagescience are publicly available in Maven Central but MorphoLibJ_ is not yet there. Therefore, if you import the maven project in an IDE (for example in Eclipse) you will have some compilation errors since it cannot find the MorphoLibJ_ dependency. You have two options to fix this problem:

1) Download the jar from MorphoLibJ's site (https://github.com/ijpb/MorphoLibJ/releases/tag/v1.0.6) and manually add it to your IDE project.

2) Download the jar and install it in your maven local repository (http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html): mvn install:install-file -Dfile=<path-to-MorphoLibJ_-1.0.6.jar>

Authors
===========================
Axel Poulet

UMR CNRS 6293, INSERM U1103, Genetic, Reproduction and Development, Clermont-Ferrand, France.
Department of Biological and Medical Sciences Faculty of Health and Life Sciences, Oxford Brookes University, Headington Campus, Oxford, United Kingdom

Philippe Andrey

Modeling and Digital Imaging Group, Institut Jean-Pierre Bourgin, INRA Versailles, France.

Ignacio Arganda-Carreras 

 Modeling and Digital Imaging Group, Institut Jean-Pierre Bourgin, INRA 
 
 David Legland,
 
  Modeling and Digital Imaging Group, Institut Jean-Pierre Bourgin, INRA Versailles, France.

Contact: axel.poulet@etudiant.univ-bpclermont.fr

