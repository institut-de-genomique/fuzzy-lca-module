package fr.cea.genoscope.sbwh6.lca.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.cea.genoscope.sbwh6.lca.Lca;

public class FLcaTest {

	private static final Logger logger = Logger.getLogger(FLcaTest.class);

	Lca lca;

	@Before
	public void setUp() throws Exception {
		logger.debug("FLCATest :initializing test environment");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("FLCATest :destroying test environment");
	}

	@Test
	public void testLca() throws IOException {
		String inputTaxonomyFile = new String("src/test/resources/exportTaxonomy4LCA.out");
		String inputLcaFile = new String("src/test/resources/test.inlca");
		String outputTrueLcaFile = new String("src/test/resources/testfuzzy.outlca");
		String outputNewLcaResults = new String("target/fuzzylca.out");
		logger.debug("test FLca : load  taxonomy ");
		
		BufferedReader br;
		String line;
		String[] columns;
		String readId;
		Long lcaTaxid;
		Double threshold = 100.0;
		Map<String, Long> read_lca = new HashMap<String, Long>();
		//Load taxonomy
		Lca lca = new Lca(inputTaxonomyFile);
		//Compute LCA
		logger.debug("test FLca : compute FLCA");
		lca.computeFuzzyLcaByCombination(inputLcaFile, outputNewLcaResults, threshold);
		
		File outfile = new File(outputNewLcaResults) ;
		
		if (outfile.exists() == true){
			//Read true results
			br =  new BufferedReader(new FileReader(outputTrueLcaFile));
			while ((line = br.readLine()) != null){
				columns = line.split("\t");
				readId = columns[0];
				lcaTaxid = Long.parseLong(columns[1].split("-")[0]);
				read_lca.put(readId, lcaTaxid);
			}
			br.close();
			
			// Compare with find results
			br =  new BufferedReader(new FileReader(outputNewLcaResults));
			while ((line = br.readLine()) != null){
				columns = line.split("\t");
				readId = columns[0];
				lcaTaxid = Long.parseLong(columns[1].split("-")[0]);
				if (read_lca.containsKey(readId) == true){
					if (read_lca.get(readId).equals(lcaTaxid) == true){
						read_lca.remove(readId);
					}
					else{
						logger.error("Bad FLCA for read "+readId+" : found '" + lcaTaxid+ "' where as the true answer is '"+read_lca.get(readId)+"'");
						assertTrue(false);						
					}
				}
				else{
					logger.error("Read "+readId+" not found in initial file.");
					assertTrue(false);
				}
			}
			br.close();			
			if (read_lca.size() == 0){
				logger.info("All LCA found correctly.");
				assertTrue(true);
			}
			else{
				logger.info("Some reads not found in final file");
				assertTrue(false);
			}
		}
		else{
			assertTrue(false);
		}
		
	}
}
