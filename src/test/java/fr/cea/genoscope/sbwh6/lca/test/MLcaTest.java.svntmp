package fr.cea.genoscope.sbwh6.lca.test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.cea.genoscope.sbwh6.lca.Lca;
import fr.cea.genoscope.sbwh6.lca.LcaException;
import fr.cea.genoscope.sbwh6.lca.LcaInputFormatException;
import fr.cea.genoscope.sbwh6.lca.util.LcaResult;
import fr.cea.genoscope.sbwh6.lca.util.Taxonomy;

public class MLcaTest {

	private static final Logger logger = Logger.getLogger(MLcaTest.class);

	Taxonomy taxonomy;

	@Before
	public void setUp() throws Exception {
		logger.debug("Initializing test environment - start");
		Lca myLca = new Lca();

		taxonomy = myLca.getTaxonomy();
		logger.debug("Initializing test environment - end");
		logger.debug("************************************");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("\n************************************");
		logger.debug("LCATest :destroying test environment");
	}

//	//@Test
//	public void testLca() throws Exception {
//		Set<Integer> taxids = new TreeSet<Integer>();
//		taxids.add(new Integer(525260));
//		taxids.add(new Integer(862512));
//		logger.info("lca for taxons: "+ taxids);
//		Taxon lca = taxonomy.computeLca(taxids);
//		assertTrue(lca.getTaxonId()==38284);
//		logger.info("lca found: "+ lca);
//		
//		logger.info("***");
//		
//		logger.info("mlca for taxons: "+ taxids);
//		HashMap<Integer,Float> taxonWeight = new HashMap<Integer,Float>();
//		taxonWeight.put(new Integer(525260),new Float(1));
//		taxonWeight.put(new Integer(862512),new Float(1));
//		LcaResult lcaResult = taxonomy.computeMaxSharedLcaRatio("a",taxids,0.6f, taxonWeight,new Float(taxids.size()));
//		Collection<Taxon> taxonLcas = lcaResult.getTaxons();
//		logger.info("mlcas size: "+taxonLcas.size());
//		assertTrue(taxonLcas.size()==1);
//		for(Taxon t : taxonLcas){
//			logger.info("mlca: "+t);
//			assertTrue(t.getTaxonId()==38284);
//		}
//		
//		logger.info("***");
//		
//		taxids.add(new Integer(144183));
//		logger.info("lca for taxons: "+ taxids);
//		lca = taxonomy.computeLca(taxids);
//		logger.info("lca found: "+ lca);
//		assertTrue(lca.getTaxonId()==1716);
//		
//		logger.info("***");
//		
//		taxids.add(new Integer(351607));
//		logger.info("lca for taxons: "+ taxids);
//		lca = taxonomy.computeLca(taxids);
//		logger.info("lca found: "+ lca);
//		assertTrue(lca.getTaxonId()==2037);
//		
//		logger.info("***");
//		logger.info("mlca for taxons: "+ taxids);
//		//taxonLcas = taxonomy.computeMaxSharedLcaRatio(line, ratio)(taxids,2);
//		assertTrue(taxonLcas.size()==1);
//		assertTrue(taxonLcas.iterator().next().getTaxonId()==38284);
//		logger.info("mlcas size: "+taxonLcas.size());
//		for(Taxon t : taxonLcas){
//			logger.info("mlca: "+t);
//		}
//		
//		logger.info("***");
//		logger.info("mlca for taxons: "+ taxids);
//		//taxonLcas = taxonomy.computeMaxSharedLca(taxids,3);
//		assertTrue(taxonLcas.size()==1);
//		assertTrue(taxonLcas.iterator().next().getTaxonId()==1716);
//		logger.info("mlcas size: "+taxonLcas.size());
//		for(Taxon t : taxonLcas){
//			logger.info("mlca: "+t);
//		}
//		
//		taxids.remove(new Integer(351607));
//		taxids.add(new Integer(144184));
//		lca = taxonomy.computeLca(taxids);
//		logger.info("lca found: "+ lca);
//		assertTrue(lca.getTaxonId()==1716);
//		
//		logger.info("***");
//		logger.info("mlca for taxons: "+ taxids);
//		//taxonLcas = taxonomy.computeMaxSharedLca(taxids,2);
//		logger.info("mlcas size: "+taxonLcas.size());
//		assertTrue(taxonLcas.size()==2);
//		int found=0;
//		for(Taxon t : taxonLcas){
//			logger.info("mlca: "+t);
//			if(t.getTaxonId()==38284 || t.getTaxonId()==38286){
//				found++;
//			}
//		}
//		assertTrue(found==2);
//		
//		logger.info("***");
//		//taxonLcas = taxonomy.computeMaxSharedLca(taxids,3);
//		logger.info("mlcas size: "+taxonLcas.size());
//		assertTrue(taxonLcas.size()==1);
//		assertTrue(taxonLcas.iterator().next().getTaxonId()==1716);
//		
//		logger.info("***");
//		//taxonLcas = taxonomy.computeMaxSharedLca(taxids,1);
//		logger.info("mlcas size: "+taxonLcas.size());
//		assertTrue(taxonLcas.size()==4);
//		
//		logger.info("***");
//		lca = taxonomy.computeLca(taxids);
//		assertTrue(lca.getTaxonId()==1716);
//	}
	
	@Test
	public void testLcaWithWeightTaxons() throws IOException {
		LcaResult result; 
		try {
			Float ratio = new Float(1.0f);
			String line = new String("158816143::1	1234380,1114966,1114967,1114969,1031709,909952,553199,267747");
			
			
			logger.info(ratio+"\tquery="+line);
			result = taxonomy.computeMaxSharedLcaRatio(line, ratio);
			Integer tLCA = result.getLca().getTaxonId();
			logger.info("result: "+result);
			
			line = new String("158816143::1	1234380:1,1114966:1,1114967:1,1114969:1,1031709:1,909952:1,553199:1,267747:1");
			logger.info(ratio+"\tquery="+line);
			result = taxonomy.computeMaxSharedLcaRatio(line, ratio);
			Assert.assertEquals(tLCA, result.getLca().getTaxonId());
			logger.info("result: "+ratio+"\t"+result);
			
			line = new String("158816143::1	1234380:1,1114966:0,1114967:0,1114969:0,1031709:0,909952:0,553199:0,267747:0");
			logger.info(ratio+"\tquery="+line);
			result = taxonomy.computeMaxSharedLcaRatio(line, ratio);
			Assert.assertEquals(new Integer(1234380), result.getLca().getTaxonId());
			logger.info("result: "+ratio+"\t"+result);
			
			line = new String("158816143::1	1234380:1,1114966:1,1114967:0,1114969:0,1031709:0,909952:0,553199:0,267747:0");
			logger.info(ratio+"\tquery="+line);
			result = taxonomy.computeMaxSharedLcaRatio(line, ratio);
			logger.info("result: "+ratio+"\t"+result);
			
			ratio=0.8f;
			line = new String("158816143::1	1234380:10,1114966:10,1114967:1,1114969:1,1031709:1,909952:1,553199:1,267747:1");
			logger.info(ratio+"\tquery="+line);
			result = taxonomy.computeMaxSharedLcaRatio(line, ratio);
			Assert.assertEquals(new Integer(1747), result.getLca().getTaxonId());
			logger.info("result: "+ratio+"\t"+result);
			
			
			
			
		} catch (LcaInputFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LcaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
