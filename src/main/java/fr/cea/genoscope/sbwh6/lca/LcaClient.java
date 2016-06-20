/**
 * Copyright CEA/Genoscope 10:08:37 AM
 */
package fr.cea.genoscope.sbwh6.lca;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import fr.cea.genoscope.sbwh6.lca.util.Taxonomy;
import fr.cea.ig.sbwh6.lca.p.FuzzyTopDownParralilzedLCA;

/**
 * @author Slalami
 *
 */
public class LcaClient {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(LcaClient.class);

	private CmdLineParser parser;
	private LcaOptions lcaOptions;

	public LcaClient(){
		lcaOptions = new LcaOptions();
		parser = new CmdLineParser(lcaOptions);  
		parser.setUsageWidth(80);
	}

	public static void main( String[] args ) throws IOException
	{
		logger.info("Welcome to LcaClient.");
		LcaClient lcaClient = new LcaClient();
		try{
			lcaClient.getParser().parseArgument(args);
			if (lcaClient.getLcaOptions().isHelpNeeded()){
				lcaClient.getParser().printUsage(System.out);
			}
			else{
				lcaClient.execute();
			}
		}
		catch (Exception e){//Catch exception if any
			logger.error("Exception Error: " + e.getMessage());
			lcaClient.getParser().printUsage(System.out);
		}

	}


	public void execute(LcaOptions lcaOptions) {
		setLcaOptions(lcaOptions);
		execute();

	}

	public void execute() {
		//try {
		if(lcaOptions.isTaskIntervals()){
			logger.info("Parsing NCBI taxonomy dump nodes into intervals representation Start\n");
			LcaIntervals.computeTaxonIntervals(lcaOptions.getNodesDmpFilePath(), lcaOptions.getOutputDirectory4IntervalTaxonomy());
			logger.info("Parsing NCBI taxonomy dump nodes into intervals representation End\n");
		}
		else if(lcaOptions.ismLca()){
			logger.info("MLCA Start\n");
			//Get parameters
			String inputFile = lcaOptions.getInputLcaFile();
			String outputFile = lcaOptions.getOutputFile();
			Float ratio = new Float(lcaOptions.getRatio());

			logger.info("Launch MLCA with :\n- ratio = " + lcaOptions.getRatio()+"\n- input file = "+lcaOptions.getInputLcaFile()+"\n- output file = "+lcaOptions.getOutputFile());
			logger.info("Step1 MLCA : Load interval taxonomy representation");

			Lca myLca = new Lca(lcaOptions.getInputTaxonomyFile());

			Taxonomy taxonomy = myLca.getTaxonomy();


			logger.info("Step2 MLCA : Compute LCA for each query entry ...");
			taxonomy.computeLca(inputFile, outputFile, ratio);				
			logger.info("MLCA End ");
		}
		else if(lcaOptions.isModeFuzzyTopDownParrallizedLca()){
			logger.info("Mode Fuzzy Top Down Parralilzed LCA Start\n");
			logger.info("Launch MLCA with :\n- ratio = " + lcaOptions.getRatio()+"\n- input file = "+lcaOptions.getInputLcaFile()+"\n- output file = "+lcaOptions.getOutputFile());

			//Get parameters
			String inputFile = lcaOptions.getInputLcaFile();
			String outputFile = lcaOptions.getOutputFile();
			Float ratio = new Float(lcaOptions.getRatio());
			int worker = lcaOptions.getWorkers();

			logger.info("Step1 MFTDP-LCA : Load interval taxonomy representation");
			Lca myLca = new Lca(lcaOptions.getInputTaxonomyFile());
			Taxonomy taxonomy = myLca.getTaxonomy();

			logger.info("Step2 MFTDP-LCA : Compute LCA for each query entry with workers= "+worker);
			FuzzyTopDownParralilzedLCA fuzzyTopDownParralilzedLCA = new FuzzyTopDownParralilzedLCA(taxonomy,worker);

			fuzzyTopDownParralilzedLCA.computeLCA(inputFile, ratio, outputFile);				
			logger.info("Mode Fuzzy Top Down Parralilzed LCA  End ");
		}
		else if(lcaOptions.isTaskComputeChildrenSignature()){
			logger.info("TaskComputeChildrenSignature Start\n");
			//Get parameters
			String inputTaxonomyFile = lcaOptions.getInputTaxonomyFile();
			String inputParent = lcaOptions.getInputParent();
			String inputParentFile = lcaOptions.getInputParentFile();
			String outputChildrenFile = lcaOptions.getOutputChildrenFile();
			boolean leafMode = lcaOptions.isLeafMode();



			logger.info("Load taxonomy file...");
			Lca lca = new Lca(inputTaxonomyFile);
			logger.info("Compute Children...");
			lca.computeChildren(inputParent, inputParentFile, outputChildrenFile,leafMode);
			logger.info("Succcessfully completed.");

			logger.info("Compute Children End ");


		}
		else if(lcaOptions.isModeFuzzyCombinationLca()){
			logger.info("Fuzzy Combination-LCA Start\n");
			//Get parameters
			String inputTaxonomyFile = lcaOptions.getInputTaxonomyFile();
			String inputLcaFile = lcaOptions.getInputLcaFile();
			String outputFile = lcaOptions.getOutputFile();
			Double threshold = new Double(lcaOptions.getThreshold());

			try {
				checkFile(inputTaxonomyFile);
				checkFile(inputLcaFile);

				logger.info("Load taxonomy file...");
				Lca lca = new Lca(inputTaxonomyFile);
				logger.info("Compute FC-LCA...");
				lca.computeFuzzyLcaByCombination(inputLcaFile, outputFile, threshold);
				logger.info("Succcessfully completed.");

				logger.info("FC-LCA End ");
			} catch (LcaInputFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else
		{
			logger.info("Chek input data..");
			String inputTaxonomyFile = lcaOptions.getInputTaxonomyFile();
			String inputLcaFile = lcaOptions.getInputLcaFile();
			String outputFile = lcaOptions.getOutputFile();
			// Check input file exists
			try {
				checkFile(inputTaxonomyFile);
				checkFile(inputLcaFile);

				//Load taxonomy
				logger.info("Load taxonomy file...");
				Lca lca = new Lca(inputTaxonomyFile);
				logger.info("Compute LCA...");
				lca.computeLca(inputLcaFile, outputFile);
				logger.info("Succcessfully completed.");
			} catch (LcaInputFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		//		} catch (Exception e) {
		//			logger.error("Exception Error: " + e.getMessage());
		//			e.printStackTrace();
		//			parser.setUsageWidth(80);
		//			parser.printUsage(System.out);
		//		}
	}

	//	private Taxonomy getTaxonomy() {
	//		String taxo;
	//		Taxonomy taxonomy;
	//		Lca myLca;
	//		if(lcaOptions.getInputTaxonomyFile()!=null){
	//			taxo=lcaOptions.getInputTaxonomyFile();
	//			myLca = new Lca(taxo);
	//			taxonomy = myLca.getTaxonomy();
	//		}
	//		else{
	//			taxo="20121100-ncbitaxonomy-interval.txt";
	//			myLca = new Lca(taxo);
	//			logger.info("Taking default taxonomy: "+taxo);
	//			taxonomy = myLca.getTaxonomy();
	//		}
	//		logger.info("Number of taxons in memory = "+taxonomy.getTaxons().size());
	//		return taxonomy;
	//	}

	/**
	 * Check that a file exists
	 * @param inputFile
	 * @throws Exception
	 */
	private static void checkFile(String inputFile) throws LcaInputFormatException {
		if (inputFile == null){
			throw new LcaInputFormatException("No input file");			
		}
		else{
			// Check that directory exists
			File file=new File(inputFile);
			if (file.exists() == false){
				throw new LcaInputFormatException("Input file "+inputFile+ " does not exist");	
			}
		}
	}

	public CmdLineParser getParser() {
		return parser;
	}

	public void setParser(CmdLineParser parser) {
		this.parser = parser;
	}

	public LcaOptions getLcaOptions() {
		return lcaOptions;
	}

	public void setLcaOptions(LcaOptions lcaOptions) {
		this.lcaOptions = lcaOptions;
	}
}
