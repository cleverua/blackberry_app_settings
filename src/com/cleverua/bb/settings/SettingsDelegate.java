package com.cleverua.bb.settings;

import java.util.Enumeration;
import java.util.Hashtable;

public abstract class SettingsDelegate {

	private Hashtable data;
	
	public abstract int version();
	public abstract String filename();
	
	public SettingsDelegate() {
	    data = new Hashtable();
	}
	
	public void setData(Hashtable data) {
		this.data = data;
	}

	public Hashtable getData() {
		return data;
	}

	public void put(String key, Object value) {
		data.put(key, value);
	}

	public Object get(String key) {
		return data.get(key);
	}
	
	public Enumeration getKeys() {
	    return data.keys();
	}

}
