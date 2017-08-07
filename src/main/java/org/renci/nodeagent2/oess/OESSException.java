package org.renci.nodeagent2.oess;

public class OESSException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public OESSException(String s) {
		super(s);
	}
	
	public OESSException(Exception e) {
		super(e);
	}

}
