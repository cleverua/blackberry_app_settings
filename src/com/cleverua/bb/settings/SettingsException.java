package com.cleverua.bb.settings;

public class SettingsException extends Exception {
	private Throwable cause;
	public SettingsException(Throwable cause) {
		super();
		this.cause = cause;
	}
	public Throwable getCause() {
	    return cause;
	}
}
