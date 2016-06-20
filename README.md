# Module Fuzzy LCA

Module dedicated to compute the LCA for a read assigned to several taxons. It could detect the LCA, or given weight on each assigned taxon and threshold, compute the fuzzy LCAs that match the query.

It uses JDK 1.6 and Maven 3.2.x

## Compilation

```bash
git clone https://github.com/institut-de-genomique/fuzzy-lca-module.git
cd fuzzy-lca-module
mvn clean
mvn install -DskipTests
```



## Basic usage LCA topdown simple

```bash
java -jar fuzzy-lca-module-3.0-lca.jar -mLca -r 0.9 -it 1391590300970-taxointervals.txt -il ./src/test/resources/testfuzzyWeigthed.inlca -o ~/Téléchargements/result.txt
```

## Generation of input file directly from NCBI nodes.dmp file

```bash
java -jar target/fuzzy-lca-module-3.0-lca.jar -taskIntervals -nodesDmpFilePath /Téléchargements/taxdump_latest/nodes.dmp -outputDir4IntervalTaxo ~/Téléchargements/
```

## LCA topdown parralelize with 4 threads
```bash
java -jar target/fuzzy-lca-module-3.0-lca.jar -ftdpLca -w 4 -r 0.9 -it 1391590300970-taxointervals.txt -il ./src/test/resources/testfuzzyWeigthed.inlca -o ~/Téléchargements/result.txt
```

Kind of results:

```
************************************
lca for taxons: [525260, 862512]
lca found: Taxon [taxonId=38284, left=673875, right=673880, level=11, parentTaxId=1716]
***
mlca for taxons: [525260, 862512]
mlcas size: 1
mlca: Taxon [taxonId=38284, left=673875, right=673880, level=11, parentTaxId=1716]
***
lca for taxons: [144183, 525260, 862512]
lca found: Taxon [taxonId=1716, left=673748, right=674877, level=10, parentTaxId=1653]
***
lca for taxons: [144183, 351607, 525260, 862512]
lca found: Taxon [taxonId=2037, left=649049, right=724068, level=7, parentTaxId=85003]
***
mlca for taxons: [144183, 351607, 525260, 862512]
mlcas size: 1
mlca: Taxon [taxonId=38284, left=673875, right=673880, level=11, parentTaxId=1716]
```
