package com.cleverua.bb.settings;

public class SettingsEngineException extends Exception {
	private Throwable cause;
	public SettingsEngineException(Throwable cause) {
		super();
		this.cause = cause;
	}
	public Throwable getCause() {
	    return cause;
	}
}
