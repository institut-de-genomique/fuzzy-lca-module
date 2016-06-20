args<-commandArgs(TRUE)
nodesInputFile<-args[1]
edgesInputFile<-args[2]
subgraphInputFile<-args[3]


library("Rgraphviz")

#chargement fichier NODES
nodesFile<-file(nodesInputFile,'r')
dataList <- list()
ecdfList <- list()
while (length(oneLine <- readLines(nodesFile, n = 1, warn = FALSE)) > 0) {
	myVector <- (strsplit(oneLine, ",")); 
	myVector <- list(myVector[[1]]); 
	dataList <- c(dataList,myVector); 
}
close(nodesFile)

#chargement fichier EDGES
edgesFile<-file(edgesInputFile,'r')
edgesDataList <- list() 
while (length(oneLine <- readLines(edgesFile, n = 1, warn = FALSE)) > 0) {
	myVector <- (strsplit(oneLine, ",")); 
	myVector <- list(myVector[[1]]); 
	edgesDataList <- c(edgesDataList,myVector);
}
close(edgesFile)

#chargement fichier SUBGRAPH
subgraphFile<-file(subgraphInputFile,'r')
subgraphDataList <- list() 
while (length(oneLine <- readLines(subgraphFile, n = 1, warn = FALSE)) > 0) {
	myVector <- (strsplit(oneLine, ",")); 
	myVector <- list(myVector[[1]]); 
	subgraphDataList <- c(subgraphDataList,myVector);
}
close(subgraphFile)


#formation de la list ALLNODES
allNodes = vector()
for (elt in dataList[1]){ allNodes = c(allNodes, elt)}

#formation de la list SPECANODES
specAnodes = vector()
for (elt in dataList[2]){ specAnodes = c(specAnodes, elt)}

#formation de la list SPECBNODES
specBnodes = vector()
for (elt in dataList[3]){ specBnodes = c(specBnodes, elt)}

#formation de la list COMMONNODES
commonNodes = vector()
for (elt in dataList[4]){ commonNodes = c(commonNodes, elt)}

#creation du graph principal
subTrees <- new("graphNEL", nodes=allNodes, edgemode="directed")

#creation des edgs
for(edge in edgesDataList) {
	subTrees <- addEdge(edge[1],edge[2], subTrees, 1)
}

#coloration des NODES
nattrs<-list()

specAnodes = specAnodes
specAcolor = rep("yellow", length(specAnodes))
names(specAcolor) = specAnodes

specBnodes = specBnodes
specBcolor = rep("lightblue", length(specBnodes))
names(specBcolor) = specBnodes

commonNodes = commonNodes
commonColor = rep("lightgreen", length(commonNodes))
names(commonColor) = commonNodes

nattrs$fillcolor = c(specAcolor, specBcolor, commonColor)

#creation des SUBGRAPHES
for (subgraph in subgraphDataList) {
	g1 = unlist(subgraph)
	graphName = paste0(g1[1], ".pdf")
	g1 = g1[-1]
	subG = subGraph(g1, subTrees); 
	pdf( file=graphName,  width=46.81, height=33.11); 
	plot(subG, nodeAttrs=nattrs, lwd=0.01, attrs=list(node=list(shape="box", color="transparent", fixedsize="FALSE")))
}

#creation du GRAPHE COMPLET
pdf( file="CompleteGraph.pdf",  width=46.81, height=33.11); 
	plot(subTrees, nodeAttrs=nattrs,lwd=0.01, attrs=list(node=list(shape="box", color="transparent", fixedsize="FALSE")))
