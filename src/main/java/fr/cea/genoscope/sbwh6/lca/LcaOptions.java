/*******************************************************************************
 * Copyright CEA, DSV/IG/GEN/LABGEM, 91000 Evry, France. contributor(s) : Francois LE FEVRE (Jul 12, 2011)
 * e-mail of the contributor(s) flefevre at/@ genoscope.cns.fr
 * Commercial use prohibited without an agreement with CEA
 * Users are therefore encouraged to load and test the software's suitability as regards their requirements in conditions enabling the security of their systems and/or data to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 ******************************************************************************/
package fr.cea.genoscope.sbwh6.lca;

import org.kohsuke.args4j.Option;

public class LcaOptions {
	
	@Option(name="-h", aliases = {"--help"}, usage="help")
    private boolean isHelpNeeded;

	@Option(name="-it", aliases = {"--InputTaxonomyFile"}, usage="Input tabular Taxonomy File : [Taxid]\t[Left interval indice]\t[Right interval indice]")
    private String inputTaxonomyFile;

	@Option(name="-il", aliases = {"--InputLcaFile"}, usage="Input tabular Lca File : [Read ID]\t[Taxid List separated by comma]")
    private String inputLcaFile;

	@Option(name="-o", aliases = {"--outputLcaFile"}, usage="output tabular Lca File : [Read ID]\t[LCA]")
    private String outputFile;
	
	/*
	 * LCA Fuzzy
	 * 
	 */
	
	@Option(name="-fLca", aliases = {"--fuzzyLca"}, usage="the fuzzy lca")
    private boolean modeFuzzyCombinationLca;
	
	@Option(name="-t", aliases = {"--threshold"}, usage="threshold for fuzzy lca 80.0, meaning than the proposed lca federate at least 80% of the taxon id regarding there weigth")
    private String threshold;
	
	@Option(name="-mLca", aliases = {"--maxiLca"}, usage="the max lca")
    private boolean mLca;
	
	@Option(name="-ftdpLca", aliases = {"--ftdpLca"}, usage="fuzzy top down parraleized LCA")
    private boolean modeFuzzyTopDownParrallizedLca;
	
	@Option(name="-w", aliases = {"--workers"}, usage="number of thread to compute parralilized LCA")
    private int workers;
	
	@Option(name="-r", aliases = {"--ratio"}, usage="the ratio 0.3")
    private String ratio;
	
	/*
	 * intervals generation
	 */
	@Option(name="-taskIntervals", aliases = {"--taskIntervals"}, usage="taskIntervals")
    private boolean taskIntervals;
	
	@Option(name="-nodesDmpFilePath", aliases = {"--nodesDmpFilePath"}, usage="output nodesDmpFilePath")
    private String nodesDmpFilePath;
	
	@Option(name="-outputDir4IntervalTaxo", aliases = {"--outTaxo"}, usage="output directory for taxonomy intervals")
    private String outputDirectory4IntervalTaxonomy;
	
	/*
	 * children
	 */
	@Option(name="-tComputeChildrenSignature", aliases = {"--tComputeChildrenSignature"}, usage="activate the task")
    private boolean taskComputeChildrenSignature;
	
	@Option(name="-ip", aliases = {"--InputParent"}, usage="a parent taxon id")
    private String inputParent;

	@Option(name="-ipf", aliases = {"--InputParentFile"}, usage="a parent taxon id file, one parent by line")
    private String inputParentFile;

	@Option(name="-ocf", aliases = {"--outputChildrenFile"}, usage="output children file : [Parent ID]\t[Children ID separetd by comma]")
    private String outputChildrenFile;

	@Option(name="-leafMode", aliases = {"--leafMode"}, usage="activate the filtering on leaf")
    private boolean leafMode;
	
	
	
	public boolean isHelpNeeded() {
		return isHelpNeeded;
	}

	public void setHelpNeeded(boolean isHelpNeeded) {
		this.isHelpNeeded = isHelpNeeded;
	}

	public String getInputTaxonomyFile() {
		return inputTaxonomyFile;
	}

	public void setInputTaxonomyFile(String inputTaxonomyFile) {
		this.inputTaxonomyFile = inputTaxonomyFile;
	}

	public String getInputLcaFile() {
		return inputLcaFile;
	}

	public void setInputLcaFile(String inputLcaFile) {
		this.inputLcaFile = inputLcaFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getRatio() {
		return ratio;
	}

	public void setRatio(String ratio) {
		this.ratio = ratio;
	}

	public boolean ismLca() {
		return mLca;
	}

	public void setmLca(boolean mLca) {
		this.mLca = mLca;
	}

	public boolean isModeFuzzyCombinationLca() {
		return modeFuzzyCombinationLca;
	}

	public void setModeFuzzyCombinationLca(boolean modeFuzzyCombinationLca) {
		this.modeFuzzyCombinationLca = modeFuzzyCombinationLca;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public boolean isTaskIntervals() {
		return taskIntervals;
	}

	public void setTaskIntervals(boolean taskIntervals) {
		this.taskIntervals = taskIntervals;
	}

	public String getNodesDmpFilePath() {
		return nodesDmpFilePath;
	}

	public void setNodesDmpFilePath(String nodesDmpFilePath) {
		this.nodesDmpFilePath = nodesDmpFilePath;
	}

	public int getWorkers() {
		return workers;
	}

	public void setWorkers(int workers) {
		this.workers = workers;
	}

	public boolean isModeFuzzyTopDownParrallizedLca() {
		return modeFuzzyTopDownParrallizedLca;
	}

	public void setModeFuzzyTopDownParrallizedLca(
			boolean modeFuzzyTopDownParrallizedLca) {
		this.modeFuzzyTopDownParrallizedLca = modeFuzzyTopDownParrallizedLca;
	}

	public String getOutputDirectory4IntervalTaxonomy() {
		return outputDirectory4IntervalTaxonomy;
	}

	public void setOutputDirectory4IntervalTaxonomy(
			String outputDirectory4IntervalTaxonomy) {
		this.outputDirectory4IntervalTaxonomy = outputDirectory4IntervalTaxonomy;
	}

	public boolean isTaskComputeChildrenSignature() {
		return taskComputeChildrenSignature;
	}

	public void setTaskComputeChildrenSignature(boolean taskComputeChildrenSignature) {
		this.taskComputeChildrenSignature = taskComputeChildrenSignature;
	}

	public String getInputParent() {
		return inputParent;
	}

	public void setInputParent(String inputParent) {
		this.inputParent = inputParent;
	}

	public String getInputParentFile() {
		return inputParentFile;
	}

	public void setInputParentFile(String inputParentFile) {
		this.inputParentFile = inputParentFile;
	}

	public String getOutputChildrenFile() {
		return outputChildrenFile;
	}

	public void setOutputChildrenFile(String outputChildrenFile) {
		this.outputChildrenFile = outputChildrenFile;
	}

	public boolean isLeafMode() {
		return leafMode;
	}

	public void setLeafMode(boolean leafMode) {
		this.leafMode = leafMode;
	}
}
