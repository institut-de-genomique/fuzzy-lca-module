package fr.cea.ig.sbwh6.lca.p;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import fr.cea.genoscope.sbwh6.lca.Lca;
import fr.cea.genoscope.sbwh6.lca.util.LcaResult;
import fr.cea.genoscope.sbwh6.lca.util.Taxonomy;

public class FuzzyTopDownParralilzedLCA {
	private static final Logger log = Logger.getLogger(FuzzyTopDownParralilzedLCA.class);
	//private static ArrayList<ExecutorService> executors = new ArrayList<ExecutorService>();
	//private static ArrayList<BufferedWriter> outs = new ArrayList<BufferedWriter>();
	//private static ArrayList<Boolean> outStatuts = new ArrayList<Boolean>();
	//private static ArrayList<Date> starts = new ArrayList<Date>();

	private Taxonomy taxonomy;
	private int worker;
	private BufferedWriter out;
	private ExecutorService executor;
	private Date start;
	
	public FuzzyTopDownParralilzedLCA(Taxonomy taxonomy, int worker){
		log.info("Creation of LCA operator: "+ worker);
		this.taxonomy = taxonomy;
		setWorker(worker);
	}

	public void computeLCA(String inputReadFilePath, Float ratio, String outputFilePath){
		log.info("Starting computing LCA with worker: "+ worker);
		start = new Date();
		//starts.add(start);
		Counter counter = new Counter();

		//input data queue to compute
		BlockingQueue<String> inputQueue = new LinkedBlockingQueue<String>();
		//output data queue to write down  
		BlockingQueue<LcaResult> outputQueue = new LinkedBlockingQueue<LcaResult>();

		//Thread Manager
		executor = Executors.newFixedThreadPool(7);
		//executors.add(executor);
		//Data to parse  
		String read2TaxonFilePath = new String(inputReadFilePath);
		Thread p1 = new Thread(new PrepareInputProduction(inputQueue,read2TaxonFilePath, counter),"in-worker");

		//opening stream for output
		FileWriter fstream;

		try {
			fstream = new FileWriter(outputFilePath);
			out = new BufferedWriter(fstream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//outs.add(out);
		//outStatuts.add(true);
		Thread p2 = new Thread(new PrepareOutputProduction(outputQueue,out,counter),"out-worker");

		//execution of input parsing
		executor.execute(p1);
		//execution of lca computer
		for (int i = 0; i < worker; i++) {
			Thread worker = new Thread(new WorkerLCA(inputQueue,taxonomy, ratio,outputQueue),"lca-worker-"+i);
			executor.execute(worker);
		}
		//execution of writer
		executor.execute(p2);

		executor.shutdown();

		while(isAlive()){
			log.trace("LCA still working");
		}
		Date end = new Date();
		log.info("End computing LCA with worker: "+ worker+"\t"+ (end.getTime()-start.getTime())+" ms for " + counter.getTotalReadsAnalyzed() + " reads analyzed");
	}



	private boolean isAlive(){
		boolean isAlive=false;
		//int i=0;
		//for(ExecutorService e : executors){
			if(executor.isTerminated() ){//&& outStatuts.get(i)){
				//BufferedWriter o = outs.get(i);
				try {
					log.info("Finished all threads ");

					out.close();
					//outStatuts.set(i, false);

					log.info("Done with LCA parralelized.");
					Date end = new Date();
					log.info("nbWorker "+worker + (end.getTime()-start.getTime())+ " ms");


				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
			}
			else{
				isAlive=true;
			}
			//i++;
		//}
		return isAlive;
	}

	public int getWorker() {
		return worker;
	}

	public void setWorker(int worker) {
		if(worker>6){
			log.warn("Max allowed worker=6");
			this.worker=6;
		}
		else{
			this.worker=worker;
		}
	}
}
