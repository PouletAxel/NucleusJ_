NucleusJ_
================

Ensemble of ImageJ plugins for nuclear analysis

Installing NucleusJ_ for developers
===========================

NucleusJ_ has three major dependencies as set in the pom.xml: Jama, imascience and MorphoLibJ_ (version 1.0.6).

Jama and imagescience are publicly available in Maven Central but MorphoLibJ_ is not yet there. Therefore, if you import the maven project in an IDE (for example in Eclipse) you will have some compilation errors since it cannot find the MorphoLibJ_ dependency. You have two options to fix this problem:

1) Download the jar from MorphoLibJ's site (https://github.com/ijpb/MorphoLibJ/releases/tag/v1.0.6) and manually add it to your IDE project.

2) Download the jar and install it in your maven local repository (http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html): mvn install:install-file -Dfile=<path-to-MorphoLibJ_-1.0.6.jar>
