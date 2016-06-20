package fr.cea.ig.sbwh6.lca.p;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import fr.cea.genoscope.sbwh6.lca.LcaException;
import fr.cea.genoscope.sbwh6.lca.LcaInputFormatException;
import fr.cea.genoscope.sbwh6.lca.util.LcaResult;
import fr.cea.genoscope.sbwh6.lca.util.Taxon;
import fr.cea.genoscope.sbwh6.lca.util.Taxonomy;

public class WorkerLCA implements Runnable {

	private static final Logger log = Logger.getLogger(WorkerLCA.class);

	private final BlockingQueue<String> inputQueue;
	private final Taxonomy taxonomy;
	private final Float ratio;
	private BlockingQueue<LcaResult> outputQueue ;

	WorkerLCA(BlockingQueue<String> q, Taxonomy taxonomy, Float threshold, BlockingQueue<LcaResult> outputQueue) {
		inputQueue = q;
		this.taxonomy= taxonomy;
		this.ratio = threshold;
		this.outputQueue = outputQueue;

	}

	public void run() {
		log.trace("Start " + Thread.currentThread().getName());

		String inputLine;
		try {
			inputLine = inputQueue.take();

			while (inputLine.compareTo("*")!=0) {    
				log.debug("dealing with " + inputLine);
				LcaResult lcaResult;
				try {
					lcaResult = taxonomy.computeMaxSharedLcaRatio(inputLine, ratio);
					outputQueue.add(lcaResult);
					log.trace("output size " + outputQueue.size());

					Collection<Taxon> taxonLcas = lcaResult.getTaxons();
					Taxon lca = lcaResult.getLca();

					log.trace(Thread.currentThread().getName()+"\t"+lcaResult.getReadid() + "\tpossible lca:  " +taxonLcas + "\ttrue LCA= "+ lca );
					inputLine = inputQueue.take();
					log.trace("new value " + inputLine);
					if(inputLine.compareTo("*")==0){
						log.trace(Thread.currentThread().getName() + "\t end s up1" );
						LcaResult lcaResult2 = new LcaResult("null",new Taxon(new Integer(1),new Integer(1),new Integer(1),new Integer(1), new Integer(1)),new ArrayList<Taxon>(), 0.8f, inputLine) ;
						log.trace(Thread.currentThread().getName() + "\t lcaResult2"+lcaResult2.getReadid() );
						outputQueue.add(lcaResult2);
						log.trace(Thread.currentThread().getName() + "\t end s up2" );
					}
					else{
						log.trace(Thread.currentThread().getName() + "\t continuing" );
					}
				} catch (LcaInputFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LcaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			log.trace(Thread.currentThread().getName() + inputLine );
			LcaResult lcaResult = new LcaResult("null",new Taxon(0),new ArrayList<Taxon>(), 0.8f, inputLine) ;
			outputQueue.add(lcaResult);
			log.trace(Thread.currentThread().getName() + "\tadding last element to stop the process" );
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}