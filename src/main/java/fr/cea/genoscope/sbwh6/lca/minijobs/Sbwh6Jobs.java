package fr.cea.genoscope.sbwh6.lca.minijobs;

import fr.cea.genoscope.sbwh6.lca.LcaClient;
import fr.cea.genoscope.sbwh6.lca.LcaOptions;

public class Sbwh6Jobs {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		LcaClient lcaClient = new LcaClient();
//		LcaOptions lcaOptions = new LcaOptions();
//		lcaOptions.setmLca(true);
//		lcaOptions.setInputLcaFile("src/test/resources/examples/New_SpecE1Reads.fasta1.out.short");
//		lcaOptions.setOutputFile("target/lca-mlca.txt");
//		lcaOptions.setRatio("0.8f");
//		lcaClient.setLcaOptions(lcaOptions);
//		lcaClient.execute();
		
//		LcaClient lcaClient2 = new LcaClient();
//		LcaOptions lcaOptions2 = new LcaOptions();
//		lcaOptions2.setModeFuzzyTopDownParrallizedLca(true);
//		lcaOptions2.setInputLcaFile("src/test/resources/examples/New_SpecE1Reads.fasta1.out.short");
//		lcaOptions2.setOutputFile("target/lca-p1lca.txt");
//		lcaOptions2.setWorkers(1);
//		lcaOptions2.setRatio("0.8f");
//		lcaClient2.setLcaOptions(lcaOptions2);
//		lcaClient2.execute();
		
		LcaClient lcaClient3 = new LcaClient();
		LcaOptions lcaOptions3 = new LcaOptions();
		lcaOptions3.setModeFuzzyTopDownParrallizedLca(true);
		lcaOptions3.setInputLcaFile("src/test/resources/examples/New_SpecE1Reads.fasta1.out");
		lcaOptions3.setOutputFile("target/lca-p4lca.txt");
		lcaOptions3.setWorkers(4);
		lcaOptions3.setRatio("0.8f");
		lcaClient3.setLcaOptions(lcaOptions3);
		lcaClient3.execute();
	}

}
