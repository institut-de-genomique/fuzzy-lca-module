package fr.cea.genoscope.sbwh6.lca.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import fr.cea.genoscope.sbwh6.lca.LcaException;
import fr.cea.genoscope.sbwh6.lca.LcaInputFormatException;
import fr.cea.ig.sbwh6.lca.p.PrepareOutputProduction;

public class Taxonomy {

	public static final String DEFAULT_TAXONOMY= new String("1391590300970-taxointervals.txt");

	private static final Logger logger = Logger.getLogger(Taxonomy.class);


	private HashMap<Integer,Taxon> taxons;
	private TreeMap<Integer, Integer> rightValue2TaxonIds;
	private HashMap<Integer, Set<Integer>> parent2DirectChildrenTaxons;

	public Taxonomy(HashMap<Integer,Taxon> taxonis){
		this.taxons = taxonis;
		rightValue2TaxonIds = new TreeMap<Integer, Integer>();
		parent2DirectChildrenTaxons = new HashMap<Integer, Set<Integer>>();
		Integer parentTaxId;
		Set<Integer> taxonas;
		for(Taxon taxon : taxons.values()){
			rightValue2TaxonIds.put(taxon.getRight(), taxon.getTaxonId());

			parentTaxId = taxon.getParentTaxId();
			taxonas = parent2DirectChildrenTaxons.get(parentTaxId);
			if(taxonas==null){
				taxonas = new TreeSet<Integer>();
			}

			if(taxon.getTaxonId()!=1 && parentTaxId!=1){
				taxonas.add(taxon.getTaxonId());
			}


			parent2DirectChildrenTaxons.put(parentTaxId, taxonas);
		}

	}


	public Collection<Taxon> getTaxons(){
		return taxons.values();
	}

	public HashMap<Integer,Taxon> getTaxonsMap(){
		return this.taxons;
	}

	/**
	 * Compute LCA for inputfile
	 * @param taxids
	 * @return
	 */
	public void computeLca(String inputFile, String outputFile, Float ratio) {
		logger.info("LCA -- start");
		logger.info("reading inputfile: "+inputFile);
		logger.info("writing outputfile: "+outputFile);
		logger.info("ratio parameter: "+ratio);

		try {
			FileInputStream fstreami = new FileInputStream(inputFile);
			DataInputStream in = new DataInputStream(fstreami);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			FileWriter fstream = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(fstream);

			FileWriter fstreamerror = new FileWriter(outputFile+".error.txt");
			BufferedWriter outerror = new BufferedWriter(fstreamerror);

			//Read File Line By Line
			int nbReads=0;
			String line;

			while ((line = br.readLine()) != null)   {
				logger.trace(nbReads + " dealing with line: "+line);
				if (nbReads%1000==0){
					logger.info(nbReads + " dealing with line: "+line);
					out.flush();
				}
				nbReads ++;


				LcaResult lcaResult = null;

				try {
					lcaResult = computeMaxSharedLcaRatio(line, ratio);
					if(lcaResult!=null){
						StringBuffer sb = PrepareOutputProduction.prepareLine(lcaResult);
						out.write(sb.toString());
						out.flush();
					}
					else{
						logger.error("lcaResult is null for: "+line);
					}

				} catch (LcaInputFormatException e) {
					logger.error("input not valid lca computable for :"+line);
				} catch (LcaException e) {
					logger.error("no lca computable for :"+line);
				}



			}
			out.flush();
			in.close();
			out.close();
			outerror.close();
		} catch (FileNotFoundException e) {
			logger.error("here");
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error("here2");
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		logger.info("LCA -- end");
	}

	/**
	 * Compute LCA for a given list of taxon identifiers
	 * @param taxids
	 * @return
	 * @throws Exception 
	 */
	public Taxon computeLca(Set<Integer> taxids) throws LcaException {
		Taxon lca = null;
		if (taxids.size() == 1){
			Integer taxonId = taxids.iterator().next();
			lca = taxons.get(taxonId);
			if(lca==null){
				throw new LcaException("No taxon for input taxon id: "+ taxonId);
			}
		}
		else{
			int max_index =  taxids.size();

			//Check if all taxids are in the initial set
			taxids.retainAll(taxons.keySet());
			if(max_index>taxids.size()){
				logger.debug("Unknown taxons have been removed from initial query: "+ max_index + " versus "+ taxids.size());
			}
			logger.debug("Looking for taxon numbers :"+taxids.size());
			if(taxids.size()==0){
				throw new LcaException("all taxons are not defined in the input taxonomy", taxids);
			}
			// Initialize parameter
			Integer taxiId = null;
			Integer minLeftNodes = null;
			Integer maxRightNodes = null;

			//Retrieve the left and right boundaries
			Iterator<Integer> itr = taxids.iterator();
			while(itr.hasNext()) {
				Integer queryTaxId = itr.next();
				if(taxiId==null){
					// Initialize parameter
					taxiId = queryTaxId;
					minLeftNodes = taxons.get(taxiId).getLeft();
					maxRightNodes = taxons.get(taxiId).getRight();
				}
				else{
					taxiId = queryTaxId;
					// Search optimal parameter left and right
					if (minLeftNodes > taxons.get(taxiId).getLeft()){
						minLeftNodes = taxons.get(taxiId).getLeft();
					}
					if (maxRightNodes < taxons.get(taxiId).getRight()){
						maxRightNodes = taxons.get(taxiId).getRight();
					} 
				}

			}
			logger.debug("["+minLeftNodes+", "+maxRightNodes+"]\tin ["+rightValue2TaxonIds.firstKey()+", "+rightValue2TaxonIds.lastKey()+"]");
			// Get / Search LCA
			NavigableMap<Integer,Integer> potentialParentsFromRoot = rightValue2TaxonIds.tailMap(maxRightNodes, true);
			logger.debug("s["+minLeftNodes+", "+maxRightNodes+"]\tin ["+potentialParentsFromRoot.firstKey()+", "+potentialParentsFromRoot.lastKey()+"]");

			Taxon pseudoLca;
			Integer pseudoLcaId;
			for(Integer right : potentialParentsFromRoot.keySet()){
				pseudoLcaId = rightValue2TaxonIds.get(right);
				pseudoLca = taxons.get(pseudoLcaId);
				if(pseudoLca!=null && pseudoLca.getLeft()<=minLeftNodes){
					lca = pseudoLca;
					break;
				}
				else{
					//logger.error("null for: "+ pseudoLcaId + " coming from "+right);
				}
			}
		}

		if(lca==null){
			throw new LcaException("lca is null", taxids);
		}

		return lca;
	}

	//	/**
	//	 * Compute LCA for a given list of taxon identifiers
	//	 * @param taxids
	//	 * @return
	//	 */
	//	public Collection<Taxon> computeMaxSharedLca(Set<Integer> taxids, int sharedTaxonFactor) {
	//		Set<Taxon> initialResultsTaxonsCoverByChildren = new HashSet<Taxon>();
	//
	//		//Compute initial LCA
	//		Taxon lca;
	//		try {
	//			lca = computeLca(taxids);
	//			//Retrieve the Data for the queried taxons
	//			List<Taxon> queryTaxons = getTaxons(taxids);
	//
	//			//Initialize subparsing
	//			initialResultsTaxonsCoverByChildren.add(lca);
	//			extractValidChildrenTaxon(lca,  queryTaxons, sharedTaxonFactor, initialResultsTaxonsCoverByChildren);
	//
	//		} catch (Exception e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//
	//
	//		return initialResultsTaxonsCoverByChildren;
	//	}

	public LcaResult computeMaxSharedLcaRatio(String line, Float ratio) throws LcaInputFormatException, LcaException{
		logger.trace("computeMaxSharedLcaRatio: "+line);
		String[] columns = line.split("\t");
		if(columns.length!=2){
			throw new LcaInputFormatException("line non valid:"+line);
		}
		String readId = columns[0];
		String[] taxidSs;

		if(columns[1].length()==0){
			throw new LcaInputFormatException("line non valid:"+line);
		}

		int befinIndex=0;
		int lastIndex = columns[1].length();
		if(columns[1].endsWith(",")){
			lastIndex-=1;
		}

		if(columns[1].startsWith(",")){
			befinIndex+=1;
		}

		boolean isWeighted = false;
		String taxonlist = columns[1].substring(befinIndex, lastIndex);
		if(taxonlist.contains(":")){
			isWeighted=true;
		}

		taxidSs = columns[1].substring(befinIndex, lastIndex).split(",");
		if(taxidSs.length==0){
			throw new LcaInputFormatException("line non valid:"+line);
		}

		Set<Integer> taxids = new TreeSet<Integer>();
		Integer tId;
		HashMap<Integer,Float> taxon2weight=new HashMap<Integer,Float>();
		for(String t : taxidSs){

			if(isWeighted){
				String id = t.split(":")[0];
				String w = t.split(":")[1];
				tId = Integer.decode(id);

				if(tId!=null){
					if(Float.valueOf(w)!=0){
						taxids.add(tId);
						taxon2weight.put(tId, Float.valueOf(w));
					}
					else{
						logger.warn("will not take into account the following taxon due to a weight null: "+ tId);
					}
				}
				else{
					throw new LcaInputFormatException("line non valid:"+line);
				}
			}
			else{
				tId = Integer.decode(t);
				if(tId!=null){
					taxids.add(tId);
					taxon2weight.put(tId, new Float(1));
				}
				else{
					throw new LcaInputFormatException("line non valid:"+line);
				}
			}
		}

		Float maxScore = new Float(0);
		for(Float v : taxon2weight.values()){
			maxScore = maxScore+v;
		}

		return computeMaxSharedLcaRatio(readId,taxids, ratio,taxon2weight,maxScore);
	}

	/**
	 * Compute LCA for a given list of taxon identifiers and a percentage for the score
	 * @param taxids
	 * @return
	 */
	public LcaResult computeMaxSharedLcaRatio(String readId,Set<Integer> taxids, Float sharedTaxonFactor, HashMap<Integer,Float> taxon2weight, Float maxScore){
		LcaResult result = null;
		//Compute initial LCA
		logger.debug("Computing initial LCA");
		Taxon trueLCA;
		try {
			trueLCA = computeLca(taxids);

			logger.debug("Found as initial LCA:" +trueLCA.getTaxonId());

			//Retrieve the Data for the queried taxons
			List<Taxon> queryTaxons = getTaxons(taxids);

			//Initialize subparsing
			logger.debug("Looking for pseudoLCATaxons");
			Set<Taxon> initialResultsTaxonsCoverByChildren = new HashSet<Taxon>();
			initialResultsTaxonsCoverByChildren.add(trueLCA);
			Integer depht = new Integer(0);
			evaluateOfChildrenTaxonAsLocalLCA4QueryTaxons(trueLCA,  queryTaxons, sharedTaxonFactor, initialResultsTaxonsCoverByChildren, taxon2weight, maxScore, depht);


			logger.debug("Creating result object: "+ trueLCA+"\t"+initialResultsTaxonsCoverByChildren);
			result = new LcaResult(readId,trueLCA, initialResultsTaxonsCoverByChildren, sharedTaxonFactor, taxids.toString());

		} catch (LcaException e) {
			logger.error("No computable LCA due to error in readid: "+ readId +"\t"+taxids);
			Set<Taxon> initialResultsTaxonsCoverByChildren = new HashSet<Taxon>();
			result = new LcaResult(readId,taxons.get(1), initialResultsTaxonsCoverByChildren, sharedTaxonFactor, taxids.toString());
		}


		return result;
	}

	private void evaluateOfChildrenTaxonAsLocalLCA4QueryTaxons(Taxon taxon2Study, List<Taxon> queryTaxons, float threshold, 
			Set<Taxon> pseudoLCATaxons, HashMap<Integer,Float> taxon2weight, Float maxScore, Integer depht) throws LcaException{
		//Retrieve the direct chidren
		logger.debug("Evaluating "+ taxon2Study.getTaxonId()+ " for " + queryTaxons);

		if(queryTaxons.size()==2){
			logger.debug("only 2 taxons in queryTaxons: "+queryTaxons);

			Taxon taxon2=null;
			Set<Integer> taxIds = new TreeSet<Integer>();
			for(Taxon taxon : queryTaxons){
				float ratio = taxon2weight.get(taxon.getTaxonId())/maxScore;
				taxIds.add(taxon.getTaxonId());
				if(ratio>=threshold){
					taxon2=taxon;
					break;
				}
			}
			if(taxon2==null){
				//keep only the LCA of both
				//try {
				Taxon lca2 = computeLca(taxIds);
				pseudoLCATaxons.add(lca2);
				//} catch (LcaException e) {
				// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}
			}
		}
		else{
			logger.debug("More than 2 taxons in queryTaxons, exploration of children");
			Set<Integer> directChildrenTaxons = parent2DirectChildrenTaxons.get(taxon2Study.getTaxonId());
			logger.debug("DirectChildren: "+ directChildrenTaxons);
			if(directChildrenTaxons!=null){
				logger.debug("directChildren size: "+ directChildrenTaxons.size());

				boolean atLeastOneUpperScore = false;
				//Iteration over the direct children to find the One /Multiple match taxons
				Iterator<Integer> directChildrenIteraor = directChildrenTaxons.iterator();	
				int h=0;
				while(directChildrenIteraor.hasNext()){
					h++;
					//Get children data
					Taxon childrenTaxon = taxons.get(directChildrenIteraor.next());
					logger.debug("Evaluation of each child taxon as pseudo LCA: "+childrenTaxon.getTaxonId());
					Integer leftChildrenTaxon = childrenTaxon.getLeft();
					Integer rightChildrenTaxon = childrenTaxon.getRight();

					//Compute local score for each direct children
					ArrayList<Taxon> taxonsMappedAtChildrenLevel = new ArrayList<Taxon>();
					Float localScore=new Float(0);
					for(Taxon taxon : queryTaxons){
						if(taxon.getLeft()>= leftChildrenTaxon && taxon.getRight()<= rightChildrenTaxon){
							logger.debug("Children taxon capture proposed query taxon: "+taxon.getTaxonId());
							taxonsMappedAtChildrenLevel.add(taxon);	
							localScore = localScore+taxon2weight.get(taxon.getTaxonId());
						}
					}

					//Scoring facilities in case of 
					logger.debug("Computing children score ");
					float localRatio = localScore/maxScore;

					if(localRatio>=threshold){
						logger.debug("Children match scoring, removing parent taxon "+taxon2Study + " <-- by "+ childrenTaxon.getTaxonId());
						atLeastOneUpperScore = true;
						pseudoLCATaxons.remove(taxon2Study);
						pseudoLCATaxons.add(childrenTaxon);
						//Check if you can get closer
						depht=depht+1;
						logger.debug("Exploring a new down level ");
						evaluateOfChildrenTaxonAsLocalLCA4QueryTaxons(childrenTaxon, taxonsMappedAtChildrenLevel, threshold, pseudoLCATaxons,taxon2weight, maxScore,depht);
					}
					else{
						//The parent was the good one for this branch
						//finalResultsTaxonsCoverByChildren was already well filled
						logger.debug("Children does not match scoring, keeping parent taxon "+taxon2Study);
					}
				}

				if(!atLeastOneUpperScore){
					//The parent was the good one
					//finalResultsTaxonsCoverByChildren was already well filled
				}
			}
		}



	}

	//	private void extractValidChildrenTaxon(Taxon taxon2Study, List<Taxon> queryTaxons, int sharedTaxonFactor, Set<Taxon> finalResultsTaxonsCoverByChildren){
	//		//Retrieve the direct chidren
	//		Set<Integer> directChildren = parent2DirectChildrenTaxons.get(taxon2Study.getTaxonId());
	//		if(directChildren!=null){
	//			boolean atLeastOneUpperScore = false;
	//
	//			//Iteration over the direct children to find the One /Multiple match taxons
	//			Iterator<Integer> directChildrenIteraor = directChildren.iterator();	
	//			while(directChildrenIteraor.hasNext()){
	//				//Get children data
	//				Taxon childrenTaxon = taxons.get(directChildrenIteraor.next());
	//				Integer leftChildrenTaxon = childrenTaxon.getLeft();
	//				Integer rightChildrenTaxon = childrenTaxon.getRight();
	//
	//				//Compute local score for each direct children
	//				ArrayList<Taxon> taxonsMappedAtChildrenLevel = new ArrayList<Taxon>();
	//				for(Taxon taxon : queryTaxons){
	//					if(taxon.getLeft()>= leftChildrenTaxon && taxon.getRight()<= rightChildrenTaxon){
	//						taxonsMappedAtChildrenLevel.add(taxon);		
	//					}
	//				}
	//
	//				//Scoring facilities
	//				int localScore = taxonsMappedAtChildrenLevel.size();
	//
	//				if(localScore>=sharedTaxonFactor){
	//					atLeastOneUpperScore = true;
	//					finalResultsTaxonsCoverByChildren.remove(taxon2Study);
	//					finalResultsTaxonsCoverByChildren.add(childrenTaxon);
	//					//Check if you can get closer
	//					extractValidChildrenTaxon(childrenTaxon, queryTaxons, sharedTaxonFactor, finalResultsTaxonsCoverByChildren);
	//				}
	//				else{
	//					//The parent was the good one for this branch
	//					//finalResultsTaxonsCoverByChildren was already well filled
	//				}
	//
	//
	//			}
	//
	//			if(!atLeastOneUpperScore){
	//				//The parent was the good one
	//				//finalResultsTaxonsCoverByChildren was already well filled
	//			}
	//		}
	//	}


	public List<Taxon> getTaxons(Collection<Integer> taxonIds) throws LcaException{
		List<Taxon> queryTaxons = new ArrayList<Taxon>();
		for(Integer taxiId : taxonIds){
			queryTaxons.add(taxons.get(taxiId));
		}

		if(queryTaxons.size()==0){
			throw new LcaException("no taxon for input taxon ids");
		}

		return queryTaxons;
	}

	public Boolean isLeaf(Integer taxid) {
		Taxon taxon = taxons.get(taxid);
		return isLeaf(taxon);
	}

	public Boolean isLeaf(Taxon taxon) {
		Boolean b = null;
		if (taxon != null)
			b = (taxon.getRight()-taxon.getLeft() == 1);
		return b;
	}

	public Integer nbrChildren(Integer taxid) {
		Integer c = null;
		Taxon taxon = taxons.get(taxid);
		return nbrChildren(taxon);
	}

	public Integer nbrChildren(Taxon taxon) {
		Integer c = null;

		if (taxon != null)
			c = (taxon.getRight()-taxon.getLeft());
		return c;
	}

	public static HashMap<Long,ArrayList<Long>> loadData(String nodesDmpFilePath, HashMap<Long,ArrayList<Long>> parentTaxid2ChildrenTaxIdsMap, HashMap<Long,Long> childrenTaxId2parentMap){


		Date start = new Date();
		logger.info("Starting ImportIntervalClassification: "+ nodesDmpFilePath);
		logger.info("Putting in cache the taxonomy tree structure ");
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(nodesDmpFilePath);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line

			while ((strLine = br.readLine()) != null)   {

				String[] fields = strLine.split("\t");
				//logger.debug(strLine);
				Long children_tax_id= new Long(Long.decode(fields[0]));
				Long parent_tax_id = new Long(Long.decode(fields[2]));
				childrenTaxId2parentMap.put(children_tax_id, parent_tax_id);
				//logger.debug(tax_id+"@"+parent_tax_id);
				// sinon boucle infini
				if (parent_tax_id.compareTo(children_tax_id)!=0){
					//Save Parent / Children relation
					ArrayList<Long> nodes = parentTaxid2ChildrenTaxIdsMap.get(parent_tax_id);
					if(nodes==null){
						nodes = new ArrayList<Long>();
					}

					nodes.add(children_tax_id);
					parentTaxid2ChildrenTaxIdsMap.put(parent_tax_id,nodes);

				}
				else{
					// =1 = root normal
					if (children_tax_id != 1){
						logger.debug("Error with line " + strLine + " : same children and parent");
					}
					else{
						logger.debug("UNO");
					}
				}
			}
			//Close the input stream
			in.close();	
			Date end = new Date();
			logger.info("Done cache the taxonomy tree structure "+ (end.getTime()-start.getTime())+ " ms");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parentTaxid2ChildrenTaxIdsMap;
	}


	public TreeMap<Integer, Integer> getRightValue2TaxonIds() {
		return rightValue2TaxonIds;
	}

	public HashMap<Integer, Set<Integer>> getParent2DirectChildrenTaxons() {
		return parent2DirectChildrenTaxons;
	}

	public Set<Integer> getChildren(Taxon taxon, Set<Integer> children, boolean leafMode) {
		return getChildren(taxon.getTaxonId(), children, leafMode);
	}


	public Set<Integer> getChildren(Integer taxonId, Set<Integer> children, boolean leafMode) {
		Set<Integer> childs = parent2DirectChildrenTaxons.get(taxonId);
		logger.trace("query: "+ taxonId + "\t"+childs);
		if(childs!=null){
			if(!leafMode){
				children.addAll(childs);
			}
			for(Integer child : childs){
				if(leafMode && isLeaf(child)){
					children.add(child);
					getChildren(child,children,leafMode);
				}
				else{
					getChildren(child,children,leafMode);
				}

			}
		}

		return children;
	}

}
