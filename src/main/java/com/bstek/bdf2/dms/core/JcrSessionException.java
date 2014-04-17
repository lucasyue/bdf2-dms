package com.bstek.bdf2.dms.core;

public class JcrSessionException extends RuntimeException {
	private String message;

	public String getMessage() {
		return this.message;
	}

	public JcrSessionException(String message) {
		this.message = message;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
