package fr.cea.genoscope.sbwh6.taxo.test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.cea.genoscope.sbwh6.lca.Lca;
import fr.cea.genoscope.sbwh6.lca.LcaClient;
import fr.cea.genoscope.sbwh6.lca.LcaException;
import fr.cea.genoscope.sbwh6.lca.LcaInputFormatException;
import fr.cea.genoscope.sbwh6.lca.LcaOptions;
import fr.cea.genoscope.sbwh6.lca.util.LcaResult;
import fr.cea.genoscope.sbwh6.lca.util.Taxonomy;

public class Taxo {

	private static final Logger logger = Logger.getLogger(Taxo.class);


	LcaClient myLcaClient;

	@Before
	public void setUp() throws Exception {
		logger.debug("Initializing test environment - start");
		myLcaClient = new LcaClient();
		
		logger.debug("Initializing test environment - end");
		logger.debug("************************************");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("\n************************************");
		logger.debug("LCATest :destroying test environment");
	}


	
	@Test
	public void testChildren() throws IOException {
		
		LcaOptions myLcaOptions = new LcaOptions();
		myLcaOptions.setTaskComputeChildrenSignature(true);
		myLcaOptions.setInputParent("59");
		myLcaOptions.setInputParentFile(null);
		myLcaOptions.setOutputChildrenFile(null);
		myLcaOptions.setLeafMode(true);
		
		myLcaClient.setLcaOptions(myLcaOptions);
		
		myLcaClient.execute();
		
		
		myLcaOptions = new LcaOptions();
		myLcaOptions.setTaskComputeChildrenSignature(true);
		myLcaOptions.setInputParent(null);
		myLcaOptions.setInputParentFile("/media/cosvd1/programs/workspacebirds3/sbwh6-project/sbwh6-modules/lca/src/test/resources/taxo/taxo.txt");
		myLcaOptions.setOutputChildrenFile("target/children.txt");
		myLcaOptions.setLeafMode(false);
		
		myLcaClient.setLcaOptions(myLcaOptions);
		
		myLcaClient.execute();
		
	}
}
