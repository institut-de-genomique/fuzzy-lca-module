package fr.cea.genoscope.sbwh6.lca;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class LcaIntervals {

	private static final Logger logger = Logger.getLogger(LcaIntervals.class);

	private static final String LEFT = "left";
	private static Set<Long> taxonIds;

	private static final String RIGHT = "right";
	private static final String LEVEL = "level";
	private static final String LEAF = "leaf";
	
	public static void computeTaxonIntervals(String nodesDmpFilePath, String outTaxoDir){
		HashMap<Long,ArrayList<Long>> parentTaxid2ChildrenTaxIdsMap = new HashMap<Long,ArrayList<Long>>();

		HashMap<Long,Long> childrenTaxId2ParentTaxiidMap = new HashMap<Long,Long>();
		loadData(nodesDmpFilePath, parentTaxid2ChildrenTaxIdsMap,childrenTaxId2ParentTaxiidMap );
		logger.info("Taxon: "+parentTaxid2ChildrenTaxIdsMap.size());

		Date start = new Date();
		logger.info("Calculating the intervall representation...");
		HashMap<Long,HashMap<String,Long>>	myIntervals = computeIntervalsRepresentations(new Long(parentTaxid2ChildrenTaxIdsMap.size()), new Long(0), new Long(0), parentTaxid2ChildrenTaxIdsMap);


		long nbLeaves = 0;
		try{
			// Create file 
			String outTaxo;
			if(!outTaxoDir.endsWith(File.separator)){
				outTaxo = new String(outTaxoDir+File.separator+start.getTime()+"-taxointervals.txt");
			}
			else{
				outTaxo = new String(outTaxoDir+start.getTime()+"-taxointervals.txt");
			}
				
			 
			FileWriter fstream = new FileWriter(outTaxo);
			BufferedWriter out = new BufferedWriter(fstream);


			for(Long tax_id : taxonIds){

				if(myIntervals.get(tax_id)!=null){
					Long lefti = myIntervals.get(tax_id).get(LEFT);
					Long righti = myIntervals.get(tax_id).get(RIGHT);
					Long leveli = myIntervals.get(tax_id).get(LEVEL);
					Long leafTmp = myIntervals.get(tax_id).get(LEAF);
					Long parent = childrenTaxId2ParentTaxiidMap.get(tax_id);

					if(leafTmp==1){
						nbLeaves++;
					}

					//Boolean leafi = false;
					if(lefti!=null && righti!=null && leveli != null && leafTmp != null){
						if(tax_id%1000==0){
							logger.debug("Dealing with: "+leafTmp+"\t"+ tax_id + "\t" + Integer.decode(""+lefti) +"\t" + Integer.decode(""+righti) +"\t"+Integer.decode(""+leveli) + "\t" +parent);
						}

						out.write(tax_id + "\t" + Integer.decode(""+lefti) +"\t" + Integer.decode(""+righti) +"\t"+Integer.decode(""+leveli) + "\t" +parent+"\n");
					}
					else{
						logger.error(tax_id + " with no lefti/righti/level/leaf");
					}

				}
				else{
					logger.error(tax_id + " no intervall precompute");
				}


			}
			out.close();
			logger.info("nb leaves= "+nbLeaves);
			Date end = new Date();
			logger.info("End computing intervals: "+(end.getTime()-start.getTime())+" ms, see for results: "+ outTaxo);
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String nodesDmpFilePath  = new String("/home/flefevre/Téléchargements/taxdump_latest/nodes.dmp");
		String outTaxo  = new String("target/out.taxo");

		LcaIntervals.computeTaxonIntervals(nodesDmpFilePath, outTaxo);

	}

	private static HashMap<Long,ArrayList<Long>> loadData(String nodesDmpFilePath, HashMap<Long,ArrayList<Long>> parentTaxid2ChildrenTaxIdsMap, HashMap<Long,Long> childrenTaxId2parentMap){


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


			taxonIds = new TreeSet<Long>();
			while ((strLine = br.readLine()) != null)   {

				String[] fields = strLine.split("\t");
				//logger.debug(strLine);
				Long children_tax_id= new Long(Long.decode(fields[0]));
				Long parent_tax_id = new Long(Long.decode(fields[2]));
				childrenTaxId2parentMap.put(children_tax_id, parent_tax_id);
				taxonIds.add(children_tax_id);
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


	private static HashMap<Long,HashMap<String,Long>> computeIntervalsRepresentations(Long nbTaxons, Long intervalLeft, Long level, HashMap<Long,ArrayList<Long>> parentTaxid2ChildrenTaxIdsMap){
		HashMap<Long,HashMap<String,Long>> myIntervals = new HashMap<Long,HashMap<String,Long>>();
		searchInterval(nbTaxons,new Long(1), intervalLeft, level,  myIntervals, parentTaxid2ChildrenTaxIdsMap);	
		return myIntervals;
	}

	private static  Long searchInterval(Long nbTaxons,Long tax_id, Long intervalLeft, Long level, HashMap<Long, HashMap<String, Long>> taxid2IntervallsMap, 	HashMap<Long, ArrayList<Long>> parentTaxid2ChildrenTaxIdsMap) {
		logger.debug("searchInterval dealing with = "+tax_id);
		Long intervalRight = new Long(-1);
		long leaf = 0;
		//if(tax_id!=1){
		intervalLeft ++;
		level ++;
		HashMap<String,Long> intervals = taxid2IntervallsMap.get(tax_id);
		if(intervals==null){
			logger.debug("IR for taxid = "+tax_id);
			// Right interval
			intervals = new HashMap<String,Long>();
			intervals.put(LEFT, intervalLeft);
			intervals.put(LEVEL, level);
			logger.debug("IR left = "+intervalLeft+ " level = " + level);
			//Left interval
			ArrayList<Long> childrenNodes = parentTaxid2ChildrenTaxIdsMap.get(tax_id);
			// if tax_id is a leaf	
			if(childrenNodes==null){
				intervalRight = intervalLeft + 1;
				leaf = 1;
				logger.debug("IR right leaf = "+intervalRight);
			}
			// if tax_id is a node
			else{
				//recursive function : on parcourt toutes les feuilles pour connaitre la borne gauche
				for (Long childrenTaxid :  childrenNodes){
					logger.debug("looking at sublevel:"+childrenTaxid);
					intervalRight = searchInterval(nbTaxons,childrenTaxid, intervalLeft, level, taxid2IntervallsMap, parentTaxid2ChildrenTaxIdsMap);
					intervalLeft = intervalRight ;
				}
				intervalRight ++;
			}

			intervals.put(RIGHT, intervalRight);
			intervals.put(LEAF, leaf);
			taxid2IntervallsMap.put(tax_id,intervals);
		}
		else{
			logger.warn("Warning with taxid " + tax_id + " : interval already exists");
		}
		logger.debug("debug2 = "+intervals.size());
		//}
		//else{
		//	intervalRight = nbTaxons;
		//}
		return intervalRight;
	}
}
