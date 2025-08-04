# baseCode

Data structures, math and statistics tools, and utilities that are often needed across projects.

Used
by [Gemma](https://github.com/PavlidisLab/Gemma), [ErmineJ](https://github.com/PavlidisLab/ermineJ), [GEOMMTx](https://github.com/PavlidisLab/GEOMMTx)
and others.

## R integration

The R integration relies on native packages that needs to be installed in your local Maven repository in order to build
baseCode.

[rJava](https://rforge.net/rJava/) is usually available as a system package or it can be installed from source or using
`install.packages("rJava")` in R.

[Rserve](https://www.rforge.net/Rserve/) has to be installed from source with `install.packages("Rserve")`.

```bash
# You may need to adjust the path depending on your system.
mvn install:install-file -DgroupId=org.rosuda.REngine -DartifactId=REngine -Dversion=1.0-13 -Dpackaging=jar -Dfile=/usr/lib64/R/library/rJava/jri/REngine.jar
mvn install:install-file -DgroupId=org.rosuda.REngine -DartifactId=JRI -Dversion=1.0-13 -Dpackaging=jar -Dfile=/usr/lib64/R/library/rJava/jri/JRI.jar
mvn install:install-file -DgroupId=org.rosuda.REngine -DartifactId=JRIEngine -Dversion=1.0-13 -Dpackaging=jar -Dfile=/usr/lib64/R/library/rJava/jri/JRIEngine.jar
mvn install:install-file -DgroupId=org.rosuda.REngine -DartifactId=Rserve -Dversion=1.8-14 -Dpackaging=jar -Dfile=/usr/lib64/R/library/Rserve/java/Rserve.jar
```