package fr.cea.genoscope.sbwh6.lca;


import org.apache.log4j.Logger;


public class LcaInputFormatException extends Exception {
	private static final Logger logger = Logger.getLogger(LcaInputFormatException.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LcaInputFormatException(String message) {
		super(message);
		logger.error("Error : " + message);
		System.exit(1);
	}

}
