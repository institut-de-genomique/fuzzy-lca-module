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

import fr.cea.genoscope.sbwh6.lca.util.Taxonomy;

public class TaxoSubTree {

	private static final Logger logger = Logger.getLogger(TaxoSubTree.class);

	private static final String LEFT = "left";
	private static Set<Long> taxonIds;

	private static final String RIGHT = "right";
	private static final String LEVEL = "level";
	private static final String LEAF = "leaf";

	public static TreeSet<Long> extractSubTree(String nodesDmpFilePath, Long parentNode){
		HashMap<Long,ArrayList<Long>> parentTaxid2ChildrenTaxIdsMap = new HashMap<Long,ArrayList<Long>>();
		HashMap<Long,Long> childrenTaxId2ParentTaxiidMap = new HashMap<Long,Long>();
		Taxonomy.loadData(nodesDmpFilePath, parentTaxid2ChildrenTaxIdsMap,childrenTaxId2ParentTaxiidMap );
		logger.info("Taxon: "+parentTaxid2ChildrenTaxIdsMap.size());

		TreeSet<Long> taxons = new TreeSet<Long>();
		taxons.add(parentNode);

		getTaxons(taxons,parentNode,parentTaxid2ChildrenTaxIdsMap);

		return taxons;

	}

	private static void getTaxons(TreeSet<Long> taxons, Long parentNode,
			HashMap<Long, ArrayList<Long>> parentTaxid2ChildrenTaxIdsMap) {
		logger.trace("Looking for: "+parentNode);
		ArrayList<Long> children = parentTaxid2ChildrenTaxIdsMap.get(parentNode);
		if(children!=null){
			taxons.addAll(children);
			for(Long tax : children){
				getTaxons(taxons, tax,parentTaxid2ChildrenTaxIdsMap);
			}
		}
		else{
			logger.trace("No key associated with: "+parentNode+"\t it is surely a leaf.");
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String taxonDir = new String("/home/flefevre/Téléchargements/taxdump_latest/");
		String nodesDmpFilePath  = new String(taxonDir+"nodes.dmp");
		String namesDmpFilePath  = new String(taxonDir+"names.dmp");
		String outNodeTaxo  = new String("target/nodes.dmp");
		String outNamesDmpFilePath  = new String("target/names.dmp");
		Long taxonParent = new Long("10803");
		
		logger.info("Starting extracting subtree "+ nodesDmpFilePath);
		TreeSet<Long> taxons = TaxoSubTree.extractSubTree(nodesDmpFilePath, taxonParent);

		logger.info("Starting extracting subtree "+ nodesDmpFilePath);
		
		logger.info("Subtree size: "+ taxons.size());
		Date start = new Date();
		FileInputStream fstream;
		try {
			// Get the object of DataInputStream
			fstream = new FileInputStream(nodesDmpFilePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			// Create file 
			FileWriter fwstream = new FileWriter(outNodeTaxo);
			BufferedWriter out = new BufferedWriter(fwstream);
			 
			
			
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {

				String[] fields = strLine.split("\t");
				//logger.info(strLine);
				Long t = Long.decode(fields[0]);
				if(taxons.contains(t)){
					if(strLine.startsWith((""+taxonParent+"	"))){
						StringBuffer sb = new StringBuffer();
						int i=0;
						for(String f : fields){
							if(i==0){
								sb.append("1\t");
							}
							else if(i==2){
								sb.append("1\t");
							}
							else{
								sb.append(f+"\t");
							}
							i++;
							
						}
						out.write(sb.toString()+"\n");
					}
					else{
						out.write(strLine.replace("\t"+taxonParent+"\t", "\t1\t")+"\n");
					}	
				}
				
				
				
			}
			//Close the input/output streams
			in.close();	
			out.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date end = new Date();
		logger.info("Done subtree taxonomy "+ (end.getTime()-start.getTime())+ " ms");

		logger.info("Starting writing down taxonomy names in "+ outNamesDmpFilePath);
		start = new Date();
		try {
			// Get the object of DataInputStream
			fstream = new FileInputStream(namesDmpFilePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			// Create file 
			FileWriter fwstream = new FileWriter(outNamesDmpFilePath);
			BufferedWriter out = new BufferedWriter(fwstream);
			 
			
			
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {

				String[] fields = strLine.split("\t");
				//logger.info(strLine);
				Long t = Long.decode(fields[0]);
				if(taxons.contains(t)){
					if(strLine.startsWith(""+taxonParent+"	|	")){
						out.write(strLine.replace(""+taxonParent, "1")+"\n");
					}
					else{
						out.write(strLine+"\n");
					}
				}
			}
			//Close the input/output streams
			in.close();	
			out.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		end = new Date();
		logger.info("Done subtree taxonomy "+ (end.getTime()-start.getTime())+ " ms");
		
	}





}
