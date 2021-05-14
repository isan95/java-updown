package com.polanco.updown.exception;

public class ResourceNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7440541997335411735L;
	
	public ResourceNotFoundException () {
		super();
	}
	
	public ResourceNotFoundException(final String message) {
		super(message);
	}

}
