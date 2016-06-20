package fr.cea.ig.sbwh6.lca.p;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import fr.cea.genoscope.sbwh6.lca.util.LcaResult;
import fr.cea.genoscope.sbwh6.lca.util.Taxon;

public class PrepareOutputProduction implements Runnable{

	private static final Logger log = Logger.getLogger(PrepareOutputProduction.class);

	private final BlockingQueue<LcaResult> outputQueue;
	private BufferedWriter out;
	private static int nbLineOutput=0;
	//private BlockingQueue<String> inputQueue;
	private  Counter count;

	public PrepareOutputProduction(BlockingQueue<LcaResult> oq, BufferedWriter out,	 Counter count) {
		outputQueue = oq;
		this.out=out;
		//this.inputQueue = inputQueue;
		this.count=count;
	}

	public void run() {
		log.info("Start PrepareOutputProduction");

		try {
			LcaResult myLcaResult = outputQueue.take();
			while (myLcaResult.getReadid().compareTo("null")!=0 ||count.getCounter()!=0 ) {
				//while (i<2 ) {
				log.trace("outputqueue " + outputQueue.size());
				nbLineOutput++;
				
				try {
					if(myLcaResult!=null && myLcaResult.getReadid().compareTo("null")!=0){
						if(nbLineOutput%1000==0){
							log.info("reading output readId: "+nbLineOutput+"\t"+myLcaResult.getReadid());
						}
						
						log.trace("output: "+myLcaResult.getReadid());
						//						out.write(myLcaResult.getReadid()+"\t"+myLcaResult.getTaxons()+"\n");
						//						
						//						
						//						Collection<Taxon> taxonLcas = myLcaResult.getTaxons();
						//						Taxon lca = myLcaResult.getLca();
						//						String result = new String();
						//						String split=new String();
						//						Taxon t = null;
						//						for(Taxon tt : taxonLcas){
						//							t = tt;
						//							result = result.concat(split+tt.getTaxonId()+"@"+tt.getLevel());
						//							split=",";
						//						}
						//
						//						if(result.contains(",") || t.getTaxonId()!=lca.getTaxonId()){
						//							out.write("#"+myLcaResult.getMaxLevelDiff()+"\t"+myLcaResult.getReadid()+"\t"+lca.getTaxonId()+"@"+lca.getLevel()+"\t"+result+"\n");
						//						}
						//						else{
						//							out.write("-0\t"+myLcaResult.getReadid()+"\t"+lca.getTaxonId()+"@"+lca.getLevel()+"\t"+result+"\n");
						//						}
						StringBuffer sb = prepareLine(myLcaResult);
						out.write(sb.toString());
						out.flush();
						count.del();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//myLcaResult = outputQueue.peek();
				//if(myLcaResult.getReadid().compareTo("null")==0){

				//	break;
				//}
				//else{
				myLcaResult = outputQueue.take();
				log.trace("has taken " + myLcaResult.getReadid());

			}
			log.trace("has taken2 " + myLcaResult.getReadid());

		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	

	}

	public static StringBuffer prepareLine(LcaResult myLcaResult){
		StringBuffer sb = new StringBuffer();
		Collection<Taxon> taxonLcas = myLcaResult.getTaxons();
		Taxon lca = myLcaResult.getLca();
		String result = new String();
		String split=new String();
		Taxon t = null;
		for(Taxon tt : taxonLcas){
			t = tt;
			result = result.concat(split+tt.getTaxonId()+"@"+tt.getLevel());
			split=",";
		}
		if(lca!=null && t !=null){
			if(result.contains(",") || t.getTaxonId()!=lca.getTaxonId()){
				sb.append("#"+myLcaResult.getMaxLevelDiff()+"\t"+myLcaResult.getReadid()+"\t"+lca.getTaxonId()+"@"+lca.getLevel()+"\t"+result+"\t"+myLcaResult.getQuery()+"\n");
			}
			else{
				sb.append("-0\t"+myLcaResult.getReadid()+"\t"+lca.getTaxonId()+"@"+lca.getLevel()+"\t"+result+"\t"+myLcaResult.getQuery()+"\n");
			}
		}
		else{
			sb.append("-E\t"+myLcaResult.getReadid()+"\t"+"-1"+"@"+"-1"+"\t"+"-1"+"\t"+myLcaResult.getQuery()+"\n");
		}


		return sb;
	}
}
