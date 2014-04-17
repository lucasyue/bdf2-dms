package com.bstek.bdf2.dms.dao;

public class ObjectNotExsitException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ObjectNotExsitException(String message) {
		super(message);
	}
}
