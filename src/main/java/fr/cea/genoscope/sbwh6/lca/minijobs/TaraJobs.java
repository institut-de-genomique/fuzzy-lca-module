package fr.cea.genoscope.sbwh6.lca.minijobs;

import fr.cea.genoscope.sbwh6.lca.LcaClient;
import fr.cea.genoscope.sbwh6.lca.LcaOptions;

public class TaraJobs {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		LcaClient lcaClient = new LcaClient();
		
		LcaOptions lcaOptions = new LcaOptions();
		lcaOptions.setmLca(true);
		lcaOptions.setInputLcaFile("part.ay");
		lcaOptions.setOutputFile("target/lcatara.txt");
		lcaOptions.setRatio("0.6");
		lcaClient.setLcaOptions(lcaOptions);
		
		lcaClient.execute();
	}

}
