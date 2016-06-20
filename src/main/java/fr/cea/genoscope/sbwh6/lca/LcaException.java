package fr.cea.genoscope.sbwh6.lca;

import java.util.Set;

import org.apache.log4j.Logger;

public class LcaException extends Exception {

	private static final Logger logger = Logger.getLogger(LcaException.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LcaException(String message) {
		super(message);
	}
	
	public LcaException(String message, Set<Integer> taxids) {
		super(message+" for taxon ids: "+taxids);
		logger.error(message+" for taxon ids: "+taxids);
	}

}
