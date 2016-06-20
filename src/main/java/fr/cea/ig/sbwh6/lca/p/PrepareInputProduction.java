package fr.cea.ig.sbwh6.lca.p;

import java.io.*;
import java.util.concurrent.*;

import org.apache.log4j.Logger;

public class PrepareInputProduction implements Runnable{

	private static final Logger log = Logger.getLogger(PrepareInputProduction.class);


	private final BlockingQueue<String> queue;
	private final String read2TaxonFilePath;

	private Counter counter;

	public PrepareInputProduction(BlockingQueue<String> q, String read2TaxonFilePath,	Counter count) {
		queue = q;
		this.read2TaxonFilePath = read2TaxonFilePath;  
		counter=count;
	}

	public void run() {
		String thisLine;
		int i =0;
		log.debug("Start PrepareProduction");
		try {
			FileInputStream fin =  new FileInputStream(read2TaxonFilePath);
			BufferedReader input = new BufferedReader
					(new InputStreamReader(fin));
			while ((thisLine = input.readLine()) != null) {
				log.trace("putting in queue inputline: "+thisLine);
				queue.put(thisLine);
				i++;
				counter.add();
				
				if(i%1000==0){
					log.info("reading input line: "+i+"\t"+thisLine);
				}
			}
			counter.setTotalReadsAnalyzed(counter.getCounter());
			//queue.put(thisLine);
			log.trace("have readen "+i);
			fin.close();
			input.close();
			// special marker for the consumer threads
			// to mark the EOF

			queue.put("*");
			queue.put("*");
			queue.put("*");
			queue.put("*");
			queue.put("*");
			queue.put("*");
			queue.put("*");
			queue.put("*");
			queue.put("*");
			log.debug("end of reading inputfile");
			// end.setEnd(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
