package fr.cea.genoscope.sbwh6.lca;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.uncommons.maths.combinatorics.CombinationGenerator;

import fr.cea.genoscope.sbwh6.lca.util.Taxon;
import fr.cea.genoscope.sbwh6.lca.util.Taxonomy;
import fr.cea.genoscope.sbwh6.lca.util.WeightedTaxon;

public class Lca {

	private static final Logger logger = Logger.getLogger(Lca.class);

	public static final String LEFT=new String("left");
	public static final String RIGHT=new String("right");
	public static final Integer MAXCHILDREN = 10;


	// LCA COMPARISON
	private Map<Integer, Map<String, Integer>> inmemoryTaxidsNodes;
	private Map< Integer, Integer> sorted_rightNodes_taxids;
	private List<Integer> list;

	private Taxonomy taxonomy;

	public Lca(){
		inmemoryTaxidsNodes = new HashMap<Integer, Map<String, Integer>>(); // taxid.left/taxid.right
		sorted_rightNodes_taxids = new HashMap<Integer, Integer>(); // taxid.right/taxid
		list = new ArrayList<Integer>();

		taxonomy = loadDefaultIntervalRepresentationOfTaxonomy();
	}

	public Lca(String inputTaxonomyFile){
		inmemoryTaxidsNodes = new HashMap<Integer, Map<String, Integer>>(); // taxid.left/taxid.right
		sorted_rightNodes_taxids = new HashMap<Integer, Integer>(); // taxid.right/taxid
		list = new ArrayList<Integer>();
		if(inputTaxonomyFile!=null){
			loadIntervalRepresentationOfTaxonomyFromFile(inputTaxonomyFile);
		}
		else{
			this.taxonomy = loadDefaultIntervalRepresentationOfTaxonomy();
		}
	}


	//	public static void main( String[] args )
	//	{
	//		logger.info("Welcome to Lca.");
	//		System.out.println("debut");
	//		String inputTaxonomyFile = new String("src/test/resources/exportTaxonomy4LCA.out");
	//		String inputLcaFile = new String("src/test/resources/testfuzzyWeigthed.inlca");
	//		//String outputTrueLcaFile = new String("src/test/resources/test.outlca");
	//		String outputNewLcaResults = new String("target/fuzzylca.out");
	//		Double threshold = 80.0;
	//		Lca lca = new Lca(inputTaxonomyFile);
	//		Date startLocal=new Date();
	//		Date endLocal=new Date();
	//		lca.computeFuzzyLcaByCombination(inputLcaFile, outputNewLcaResults, threshold);
	//		endLocal=new Date();
	//		logger.info("Done in " + (endLocal.getTime() - startLocal.getTime()) + " ms");
	//
	//		//HashMap<Integer,Double> Lcas = lca.computeLocalFuzzyLca(readId, wtaxons, threshold);
	//		System.out.println("fin");
	//	}

	/**
	 * Compute the fuzzy LCA for all input line element
	 * @param wtaxons
	 * @param threshold
	 */
	public HashMap<Integer,Double> computeLocalFuzzyLcaByCombination(String line, Double threshold) {
		String[] columns = line.split("\t");
		return computeLocalFuzzyLcaByCombination(columns[0], columns[1], threshold);
	}

	/**
	 * Compute the fuzzy LCA for all input line element
	 * @param wtaxons
	 * @param threshold
	 */
	private HashMap<Integer,Double> computeLocalFuzzyLcaByCombination(String readId, String wtaxons, Double threshold) {

		HashMap<Integer,Double> Lcas = new HashMap<Integer,Double>();
		List<WeightedTaxon> wTaxonList = new ArrayList<WeightedTaxon>();
		List<WeightedTaxon> combination = new ArrayList<WeightedTaxon>();
		Double wThreshold = 0.0;
		Double wSum = 0.0;

		wTaxonList = convertInWeightedTaxons(wtaxons);

		System.out.println("Apres tri "+WeightedTaxonsListToString(wTaxonList));

		wSum = getTotalWeight(wTaxonList);		
		wThreshold = wSum * threshold / 100;

		//System.out.println("Sum "+wSum+" Thres "+threshold+" wThres "+wThreshold);

		// calculer les combinaison
		int nbrValidSets = 0;
		wSum = 0.0;
		//int upperbound = (wTaxonList.size() > 20) ? (int) (wTaxonList.size()* threshold / 100) : wTaxonList.size();

		//int upperbound = (wTaxonList.size() > 6) ? 6 : wTaxonList.size();
		
		if(wTaxonList.size() < 20) {
			System.out.println("Starting for combinations of "+wTaxonList.size()+" elements...");
			for (int k=1; (k <= wTaxonList.size()) ; k++)
			{
				CombinationGenerator<WeightedTaxon> CG = new CombinationGenerator<WeightedTaxon>(wTaxonList, k);

				while (CG.hasMore())
				{
					combination = CG.nextCombinationAsList();
					wSum = getTotalWeight(combination);

					//System.out.println("combination "+WeightedTaxonsListToString(combination)+"{"+wSum+"}");

					if (wSum.compareTo(wThreshold) != -1)
					{
						// pour chaque combinaison valide appeler computeLocalLca
						nbrValidSets++;
						String validSet = WeightedTaxonsListToString(combination);
						Integer lca = computeLocalLca(readId, validSet);
						System.out.println(readId+" => lca "+lca);
						Double d = 0.0;

						if (Lcas.containsKey(lca)) d = Lcas.get(lca);
						d++;

						Lcas.put(lca, d);
					}
					
					wSum = 0.0;
				}
			}
			System.out.println("End of combination");
			// calculer la representation de chaque Lca
			for (Integer mapKey : Lcas.keySet()) {
				Double d = Lcas.get(mapKey)/nbrValidSets*100;
				Lcas.put(mapKey, d);

			}
		}
		//retourner les Lcas

		return Lcas;
	}

	/**
	 * Compute the fuzzy LCA for all input line element
	 * @param inputLcaFile
	 * @param outputLcaFile
	 * @param threshold
	 */
	public void computeFuzzyLcaByCombination(String inputLcaFile, String outputLcaFile, Double threshold) {

		BufferedReader br;
		try 
		{
			br = new BufferedReader(new FileReader(inputLcaFile));
			FileWriter fstream = new FileWriter(outputLcaFile);
			BufferedWriter out = new BufferedWriter(fstream);
			String line = "";

			while ((line = br.readLine()) != null)
			{				
				String[] columns = line.split("\t");
				HashMap<Integer,Double> Lcas = computeLocalFuzzyLcaByCombination(columns[0], columns[1], threshold);
				String temp = new String("");
				for (Integer mapKey : Lcas.keySet()) {
					temp = temp.concat(mapKey+"-"+Lcas.get(mapKey)+",");
				}
				if (temp.length() > 0)
				{
					out.write(columns[0]+"\t"+temp.substring(0, temp.length()-1)+"\n");
				}
			}

			br.close();
			out.close();
		}
		catch (Exception e){//Catch exception if any
			logger.error("3.LCA  Error: " + e.getMessage()); // audrey
			System.exit(1);
		}
	} 


	private List<WeightedTaxon> convertInWeightedTaxons(String wtaxons)
	{
		List<WeightedTaxon> wTaxonList = new ArrayList<WeightedTaxon>();
		String [] taxons = wtaxons.split(",");


		for (int i=0; i< taxons.length; i++)
		{
			String[] temp = ((taxons[i].indexOf(':') != -1) ? taxons[i].split(":") : new String[]{taxons[i],"1.0"});
			BigDecimal w = new BigDecimal(temp[1]);
			wTaxonList.add(new WeightedTaxon(temp[0], w.doubleValue()));
		}

		Collections.sort(wTaxonList, new Comparator<WeightedTaxon>()
				{
			public int compare(WeightedTaxon o1, WeightedTaxon o2)
			{
				return o2.getWeight().compareTo(o1.getWeight());
			}
				});

		return wTaxonList;
	}

	/*private WeightedTaxon[] SortedWeightedTaxonsList(List<WeightedTaxon> wTaxonList)
	{
		WeightedTaxon[] wTaxonTab = new WeightedTaxon[wTaxonTab.length];

		for (int i=0; i< taxons.length; i++)
		{
			String[] temp = ((taxons[i].indexOf(':') != -1) ? taxons[i].split(":") : new String[]{taxons[i],"1.0"});
			BigDecimal w = new BigDecimal(temp[1]);
			wTaxonList.add(new WeightedTaxon(temp[0], w.doubleValue()));
		}

		return wTaxonTab;
	}*/

	private Double getTotalWeight(List<WeightedTaxon> wTaxonList)
	{
		Double wSum = 0.0;

		for (int i=0; i< wTaxonList.size(); i++)
		{
			wSum += wTaxonList.get(i).getWeight();	
		}
		return wSum;
	}

	private String WeightedTaxonsListToString(List<WeightedTaxon> list)
	{
		String s = "";
		int listSize = list.size();
		for (int i=0; i< listSize; i++)
		{
			s = s.concat(list.get(i).getTaxId().toString());
			if (i != listSize-1) s = s.concat(",");
		}
		return s;
	}


	/**
	 * Compute the LCA for all input line element
	 * @param inputLcaLine
	 * 
	 */
	private Integer computeLocalLca(String readId, String taxons) {
		Integer lca = 0;

		String[] taxids = taxons.split(",");

		if (taxids.length == 1)
		{
			lca = Integer.parseInt(taxids[0]);
		}
		else
		{	
			//logger.info("Convert  to list right node...");	
			int listSize = list.size();
			//logger.info("Done (size = "+listSize+")");

			int nbReads = 0;
			List<Integer> missing_taxids = new ArrayList<Integer>();
			Boolean allTaxidsFound = true; // Compute LCA even if some taxids not found in imported taxonomy
			// Search the first index to initialize parameters, 
			//ie the first taxid which exists in imported taxonomy
			int start_index = 0;
			int max_index =  taxids.length;
			Integer taxid = Integer.parseInt(taxids[start_index]);

			while ((start_index < max_index) && (inmemoryTaxidsNodes.containsKey(Integer.parseInt(taxids[start_index])) == false)){
				taxid = Integer.parseInt(taxids[start_index]);
				if (missing_taxids.contains(taxid) == false){
					logger.error("Error : Taxid " + taxid + " not found in taxonomy file.");
					missing_taxids.add(taxid);
				}
				start_index ++;
			}

			if (start_index < max_index){
				// Initialize parameter
				Integer taxiId = Integer.parseInt(taxids[start_index]);
				Integer minLeftNodes = inmemoryTaxidsNodes.get(taxiId).get(LEFT);
				Integer maxRightNodes = inmemoryTaxidsNodes.get(taxiId).get(RIGHT);	

				// Search optimal parameter
				// ie minimal left node and maximal right node
				for(int index = start_index + 1; index < max_index; index ++){
					taxid = Integer.parseInt(taxids[index]);
					if (inmemoryTaxidsNodes.containsKey(taxid)){
						if (minLeftNodes > inmemoryTaxidsNodes.get(taxid).get(LEFT)){
							minLeftNodes = inmemoryTaxidsNodes.get(taxid).get(LEFT);
						}
						if (maxRightNodes < inmemoryTaxidsNodes.get(taxid).get(RIGHT)){
							maxRightNodes = inmemoryTaxidsNodes.get(taxid).get(RIGHT);
						}
						logger.debug("\ntaxid = " + taxid + " L = " +inmemoryTaxidsNodes.get(taxid).get(LEFT)+ " and R = " + inmemoryTaxidsNodes.get(taxid).get(RIGHT));
					}
					else{
						allTaxidsFound = false;
						if (missing_taxids.contains(taxid) == false){
							logger.error("Error : Taxid " + taxid + " not found in taxonomy file.");
							missing_taxids.add(taxid);
						}
						//System.exit(1);
					}
				}
				// Search LCA, 
				// ie taxid with maximal left node < minLeftNodes AND minimal right node > maxRightNodes
				logger.debug("\nminLeftNodes = " + minLeftNodes + " and maxRightNodes = " + maxRightNodes);

				// Initialization
				lca = null;

				// firstIndex = index with right node = maxRightNodes
				// GOAL : find the first taxid where left node <= minLeftNodes
				int index = list.indexOf(maxRightNodes);
				logger.debug("index = " + index+ " listSize = "+ listSize);
				while ((index < listSize) && (lca == null)){
					Integer rightNode = list.get(index);
					taxid = sorted_rightNodes_taxids.get(rightNode);
					//logger.debug("index = " + index + " taxid = " + taxid + " left = "+inmemoryTaxidsNodes.get(taxid).get("left"));
					if (inmemoryTaxidsNodes.get(taxid).get("left") <= minLeftNodes){
						lca = taxid;

					}	
					index ++;
				}

				if (lca != null){
					if(allTaxidsFound == false){
						logger.error("Warning: some taxid(s) not found in initial file for readID " +readId + " (LCA = " + lca + ")");								
					}
				}
				else{
					logger.error("No LCA found for " + readId);
					//System.exit(1);
				}
			}
			// None taxid found
			else{
				logger.error("Error : none taxid(s) found for read " + readId);
				//System.exit(1);
			}

		}

		return lca;

	}

	/**
	 * Compute the LCA for all input line element
	 * @param inputLcaFile
	 * @param outputLcaFile
	 */
	public void computeLca(String inputLcaFile, String outputLcaFile) {

		logger.info("Read file : " + inputLcaFile);

		BufferedReader br;

		try {
			logger.info("Convert  to list right node...");	
			int listSize = list.size();
			logger.info("Done (size = "+listSize+")");	
			logger.info("Read Input LCA file and write results in " + outputLcaFile);
			br =  new BufferedReader(new FileReader(inputLcaFile));
			String line = "";
			String[] taxids;
			int nbReads = 0;
			Date startLocal=new Date();
			Date endLocal=new Date();
			List<Integer> missing_taxids = new ArrayList<Integer>();

			FileWriter fstream = new FileWriter(outputLcaFile);

			BufferedWriter out = new BufferedWriter(fstream);
			
			//Read File Line By Line
			while ((line = br.readLine()) != null)   {
				logger.debug("******\ndealing with: "+line);

				if (nbReads == 10000){
					endLocal=new Date();
					logger.info(nbReads  + " reads LCA : " + (endLocal.getTime() - startLocal.getTime()) + " ms");
					nbReads = 0;
					startLocal=new Date();
				}
				nbReads ++;

				String[] columns = line.split("\t");
				if (columns.length == 2){
					String readId =  columns[0];
					taxids = columns[1].split(",");
					//If one taxid found, LCA = taxid
					if (taxids.length == 1){
						//no lca to compute because only one item
						out.write(readId + "\t" + taxids[0]+"\n");
					}
					else{
						Boolean allTaxidsFound = true; // Compute LCA even if some taxids not found in imported taxonomy
						// Search the first index to initialize parameters, 
						//ie the first taxid which exists in imported taxonomy
						int start_index = 0;
						int max_index =  taxids.length;
						Integer taxid = Integer.parseInt(taxids[start_index]);
						while ((start_index < max_index) && (inmemoryTaxidsNodes.containsKey(Integer.parseInt(taxids[start_index])) == false)){
							taxid = Integer.parseInt(taxids[start_index]);
							if (missing_taxids.contains(taxid) == false){
								logger.error("Error : Taxid " + taxid + " not found in taxonomy file.");
								missing_taxids.add(taxid);
							}
							start_index ++;
						}
						// If a taxid was found
						if (start_index < max_index){
							// Initialize parameter
							Integer taxiId = Integer.parseInt(taxids[start_index]);
							Integer minLeftNodes = inmemoryTaxidsNodes.get(taxiId).get(LEFT);
							Integer maxRightNodes = inmemoryTaxidsNodes.get(taxiId).get(RIGHT);	

							// Search optimal parameter
							// ie minimal left node and maximal right node
							for(int index = start_index + 1; index < max_index; index ++){
								taxid = Integer.parseInt(taxids[index]);
								if (inmemoryTaxidsNodes.containsKey(taxid)){
									if (minLeftNodes > inmemoryTaxidsNodes.get(taxid).get(LEFT)){
										minLeftNodes = inmemoryTaxidsNodes.get(taxid).get(LEFT);
									}
									if (maxRightNodes < inmemoryTaxidsNodes.get(taxid).get(RIGHT)){
										maxRightNodes = inmemoryTaxidsNodes.get(taxid).get(RIGHT);
									}
									logger.debug("\ntaxid = " + taxid + " L = " +inmemoryTaxidsNodes.get(taxid).get(LEFT)+ " and R = " + inmemoryTaxidsNodes.get(taxid).get(RIGHT));
								}
								else{
									allTaxidsFound = false;
									if (missing_taxids.contains(taxid) == false){
										logger.error("Error : Taxid " + taxid + " not found in taxonomy file.");
										missing_taxids.add(taxid);
									}
									//System.exit(1);
								}
							}
							// Search LCA, 
							// ie taxid with maximal left node < minLeftNodes AND minimal right node > maxRightNodes
							logger.debug("\nminLeftNodes = " + minLeftNodes + " and maxRightNodes = " + maxRightNodes);

							Integer lca = null;
							// Initialization
							// firstIndex = index with right node = maxRightNodes
							// GOAL : find the first taxid where left node <= minLeftNodes
							int index = list.indexOf(maxRightNodes);
							logger.debug("index = " + index+ " listSize = "+ listSize);
							while ((index < listSize) && (lca == null)){
								Integer rightNode = list.get(index);
								taxid = sorted_rightNodes_taxids.get(rightNode);
								//logger.debug("index = " + index + " taxid = " + taxid + " left = "+inmemoryTaxidsNodes.get(taxid).get("left"));
								if (inmemoryTaxidsNodes.get(taxid).get("left") <= minLeftNodes){
									lca = taxid;
								}	
								index ++;
							}



							if (lca != null){

								if(allTaxidsFound == false){
									logger.error("Warning: some taxid(s) not found in initial file for readID " +readId + " (LCA = " + lca + ")");								
								}
								out.write(readId + "\t" + lca+"\n");

							}
							else{
								logger.error("No LCA found for " + line);
								//System.exit(1);
							}
						}
						// None taxid found
						else{
							logger.error("Error : none taxid(s) found for read " + readId);
							//System.exit(1);
						}
					}
				}
				else {
					logger.error("Line '"  + line + "' was ignored (2 columns were expected)");
				}
			}
			br.close();
			out.close();
			endLocal=new Date();
			logger.info(nbReads  + " reads LCA : " + (endLocal.getTime() - startLocal.getTime()) + " ms");
			// sort left node
		} catch (Exception e){//Catch exception if any
			logger.error("4.LCA  Error: " + e.getMessage());// audrey
			System.exit(1);
		}


	}


	/**
	 * Compute LCA for a given list of taxon identifiers
	 * @param taxids
	 * @return
	 */
	public Integer computeLca(Integer[] taxids) {
		Integer lca = null;

		if (taxids.length == 1){
			lca = new Integer(taxids[0]);
		}
		else{
			lca = null;
		}

		return lca;
	}


	/**
	 * Load the NCBI taxonomy preprocessed with interval representation (taxonomy have to be sorted by right node ascending)
	 * @param inputIntervalReesentationTaxonomyFile
	 */
	public void loadIntervalRepresentationOfTaxonomyFromFile(String inputIntervalReesentationTaxonomyFile) {

		logger.info("Read file : " + inputIntervalReesentationTaxonomyFile);
		Date startLocal=new Date();
		BufferedReader br;
		try {
			br =  new BufferedReader(new FileReader(inputIntervalReesentationTaxonomyFile));
			String line = "";

			//Read File Line By Line
			while ((line = br.readLine()) != null)   {
				String[] columns = line.split("\t");
				Integer taxId = Integer.parseInt(columns[0]);
				Integer rightNode = Integer.parseInt(columns[2]);
				Integer leftNode = Integer.parseInt(columns[1]);
				Map<String, Integer> taxonNodeBoundary = new HashMap<String, Integer>();
				taxonNodeBoundary.put(LEFT, leftNode); 
				taxonNodeBoundary.put(RIGHT, rightNode);
				inmemoryTaxidsNodes.put(taxId,taxonNodeBoundary);
				sorted_rightNodes_taxids.put(rightNode, taxId);
				list.add(rightNode);

			}
			br.close();


			//
			Date endLocal=new Date();
			logger.info("Done : " + (endLocal.getTime() - startLocal.getTime()) + " ms");
		} catch (Exception e){//Catch exception if any
			logger.error("1.LCA  Error: " + e.getMessage()); //audrey
		}
	}


	private Taxonomy loadIntervalRepresentationOfTaxonomy(String inputFile) {
		logger.info("Read taxonomy file:" + inputFile);
		Taxonomy taxonomy =null;
		try {
			FileInputStream fstream = new FileInputStream(inputFile);
			InputStreamReader stream = new InputStreamReader(fstream);
			taxonomy =  loadIntervalRepresentationOfTaxonomy2(stream);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("LCA  Error file path error "+inputFile+"\t" + e.getMessage()); // audrey
		}
		return taxonomy; 
	}

	private Taxonomy loadDefaultIntervalRepresentationOfTaxonomy() {
		logger.info("Read default taxonomy file:" + Taxonomy.DEFAULT_TAXONOMY);
		Taxonomy taxonomy =null;
		try {
			InputStreamReader  stream = new InputStreamReader(Lca.class.getClassLoader().getResourceAsStream(Taxonomy.DEFAULT_TAXONOMY));
			taxonomy =  loadIntervalRepresentationOfTaxonomy2(stream);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("LCA  Error file path error "+Taxonomy.DEFAULT_TAXONOMY+"\t" + e.getMessage());
		}
		return taxonomy; 
	}



	/**
	 * Load the NCBI taxonomy preprocessed with interval representation
	 * @param inputIntervalReesentationTaxonomyFile
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	private Taxonomy loadIntervalRepresentationOfTaxonomy2(InputStreamReader inputIntervalReesentationTaxonomyInputStream) throws NumberFormatException, IOException {
		Date startLocal=new Date();
		BufferedReader br;

		HashMap<Integer,Taxon> taxons = new HashMap<Integer,Taxon>();
		br =  new BufferedReader(inputIntervalReesentationTaxonomyInputStream);
		String line = "";

		//Read File Line By Line
		Taxon myTaxon;
		int i=0;
		while ((line = br.readLine()) != null)   {

			String[] columns = line.split("\t");
			Integer taxId = Integer.decode(columns[0]);
			Integer left = Integer.decode(columns[1]);
			Integer right = Integer.decode(columns[2]);
			Integer level = Integer.decode(columns[3]);
			Integer parentTaxId;
			if(columns[4].compareTo("NULL")!=0){
				parentTaxId = Integer.decode(columns[4]);
			}
			else{
				parentTaxId = new Integer(0);
			}

			myTaxon = new Taxon(taxId,left,right,level,parentTaxId);
			taxons.put(taxId, myTaxon);
			i++;
			if(i%10000==0){
				logger.debug(i+"\tmyTaxon: "+myTaxon.toString());
			}
			
		}
		br.close();

		Date endLocal=new Date();
		logger.info("Done : " + (endLocal.getTime() - startLocal.getTime()) + " ms");


		return new Taxonomy(taxons);
	}

	/**
	 * Load the NCBI taxonomy preprocessed with interval representation
	 * @param inputIntervalReesentationTaxonomyFile
	 */
	public static Taxonomy loadIntervalRepresentationOfTaxonomy2(String inputIntervalRepresentationTaxonomyFile) {
		Date startLocal=new Date();
		BufferedReader br;

		HashMap<Integer,Taxon> taxons = new HashMap<Integer,Taxon>();

		try {
			br =  new BufferedReader(new FileReader(inputIntervalRepresentationTaxonomyFile));
			String line = "";

			//Read File Line By Line
			Taxon myTaxon;
			//int i=0;
			while ((line = br.readLine()) != null)   {

				String[] columns = line.split("\t");
				Integer taxId = Integer.decode(columns[0]);
				Integer left = Integer.decode(columns[1]);
				Integer right = Integer.decode(columns[2]);
				Integer level = Integer.decode(columns[3]);
				Integer parentTaxId;
				if(columns[4].compareTo("NULL")!=0){
					parentTaxId = Integer.decode(columns[4]);
				}
				else{
					parentTaxId = new Integer(0);
				}

				myTaxon = new Taxon(taxId,left,right,level,parentTaxId);
				taxons.put(taxId, myTaxon);
				/*i++;
					if(i%1000==0){
						logger.debug(i+"\tline: "+line);
					}*/
			}
			br.close();

			Date endLocal=new Date();
			logger.info("Done : " + (endLocal.getTime() - startLocal.getTime()) + " ms");
		} catch (Exception e){//Catch exception if any
			logger.error("5.LCA  Error: " + e.getMessage()); // audrey
			e.printStackTrace();
		}

		return new Taxonomy(taxons);
	}


	public Taxonomy getTaxonomy() {
		return taxonomy;
	}

	public void computeChildren(String inputParent, String inputParentFile,
			String outputChildrenFile, boolean isLeafMode) {

		FileWriter fostream = null;
		BufferedWriter out = null;

		if(outputChildrenFile!=null && outputChildrenFile.length()!=0){
			try {
				fostream = new FileWriter(outputChildrenFile);
				out = new BufferedWriter(fostream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(inputParentFile==null || inputParentFile.length()==0){
			Set<Integer> children = new TreeSet<Integer>();
			Integer parentTaxonId = new Integer(inputParent);

			taxonomy.getChildren(parentTaxonId, children, isLeafMode);
			if(outputChildrenFile==null){
				System.out.println(inputParent+": "+children);
			}
			else{
				try {
					logger.trace("aparentTaxonId: "+inputParent+"\t"+children);
					out.write(inputParent+": "+children+"\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else{
			try{
				// Open the file that is the first 
				// command line parameter
				logger.trace("reading: "+inputParentFile);
				FileInputStream fstream = new FileInputStream(inputParentFile);
				// Get the object of DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				Set<Integer> children;
				//Read File Line By Line
				while ((strLine = br.readLine()) != null)   {


					logger.trace("looking for: "+strLine);
					children = new TreeSet<Integer>();
					taxonomy.getChildren(new Integer(strLine), children, isLeafMode);
					if(outputChildrenFile==null){
						System.out.println(strLine+": "+children);
					}
					else{
						logger.trace("found "+strLine+": "+children+"\n");
						try {
							out.write(strLine+": "+children+"\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				//Close the input stream
				in.close();
				out.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}

		}

	}
}