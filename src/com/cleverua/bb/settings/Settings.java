package com.cleverua.bb.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.Base64OutputStream;

public class Settings {

	public static final String SETTINGS_FILE = "file:///store/home/user/app_settings/app_settings.settings";
	
    private static final String SETTINGS_VERSION = "3";
    private static final char NL = '\n';
    private static final char EQ = '=';
    private static final String ONE = "1";
    private static final String ZERO = "0";
	private static final String UTF_8 = "UTF-8";
	
	private static final String KEY_SETTINGS_VERSION = "settingsVersion";
	private static final String KEY_SETTINGS_CHECKBOX = "boxChecked";
	private static final String KEY_USER_TEXT = "userText"; 
	
	private boolean checkBoxChecked = false;
	private String userText = "";

	public void load() throws Exception {
		if(!Utils.isFilePresent(SETTINGS_FILE)) {
			flush();
		} else {
			try {
				read();
			} catch (OutdatedSettingsVersionException e) {
				flush();
			}
		}
	}
	
    public void flush() throws IOException {
    	Hashtable settingsHash = new Hashtable();
		loadSettingsToHash(settingsHash);
    	
        FileConnection fc = null;
        OutputStream out = null;
        OutputStreamWriter writer = null;
        try {
            fc = (FileConnection) Connector.open(SETTINGS_FILE, Connector.READ_WRITE);
            if (!fc.exists()) {
                fc.create();
            }
            
            fc.truncate(0);

            out = fc.openOutputStream();
            writer = new OutputStreamWriter(out);
            
            for(Enumeration e = settingsHash.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                writer.write(key + EQ + encode(settingsHash.get(key).toString()) + NL);
            }

        } finally {
            Utils.safelyCloseStream(writer);
            Utils.safelyCloseStream(out);
            Utils.safelyCloseStream(fc);
        }
    }
	
	private void read() throws Exception {
		synchronized (this) {
			
			FileConnection fc = null;
	        InputStream in = null;
	        
	        try {
	            fc = (FileConnection) Connector.open(SETTINGS_FILE, Connector.READ);
	            if(fc.exists()) {
	                in = fc.openInputStream();
	                parse(Utils.getStringFromStream(in));
	            }
	        } finally {
	        	Utils.safelyCloseStream(fc);
	        	Utils.safelyCloseStream(in);
	        }				
		}
	}
    
    private String encode(String stringToEncode) throws IOException {
    	byte[] bytesToEncode = stringToEncode.getBytes(UTF_8);
    	return Base64OutputStream.encodeAsString(bytesToEncode, 0, bytesToEncode.length, false, false);
    }
    
    private String decode(String stringToDecode) throws UnsupportedEncodingException, IOException {
    	return new String(Base64InputStream.decode(stringToDecode), UTF_8);
    }
    
    private void loadSettingsToHash(Hashtable settingsHash) {
    	settingsHash.put(KEY_SETTINGS_VERSION, SETTINGS_VERSION); // this should always be the first item in the settings file!
    	settingsHash.put(KEY_USER_TEXT, userText);
    	settingsHash.put(KEY_SETTINGS_CHECKBOX, booleanToString(checkBoxChecked));
    }
    
    private void parse(String data) throws Exception {
        String working = data;
        int index = working.indexOf(NL);

        while (index != -1) {
            String tmp = "";
            if (index > 0) {
                tmp = working.substring(0, index);
                toProperty(tmp);
            }
            working = working.substring(index + 1);
            index = working.indexOf(NL);
        }
        toProperty(working);
    }
    
    private void toProperty(String str) throws Exception {
    	if(str.length() == 0) {
    		return;
    	}
    	
        int equalsPosition = str.indexOf(EQ);
		try {        
			String key = str.substring(0, equalsPosition).trim();

			String value = decode(str.substring(equalsPosition + 1).trim());
			
			if(key.equals(KEY_SETTINGS_VERSION)) {
	        	if(isSettingsVersionOutdated(value)) {
	        		throw new OutdatedSettingsVersionException();
	        	}
	        } else if(key.equals(KEY_SETTINGS_CHECKBOX)) {
	        	checkBoxChecked = stringToBoolean(value);
	        } else if(key.equals(KEY_USER_TEXT)) {
	        	userText = value;
	        }
	    
		} catch (OutdatedSettingsVersionException e) {
			throw e;
		} catch (Exception e) {
		    throw e;
		}
    }
    
    private boolean stringToBoolean(String value) {
        return value.equals(ONE);
    }
    
    private String booleanToString(boolean value) {
        return value ? ONE : ZERO;
    }
    
    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public boolean isCheckBoxChecked() {
        return checkBoxChecked;
    }

    public void setCheckBoxChecked(boolean checkBoxChecked) {
        this.checkBoxChecked = checkBoxChecked;
    }

    private boolean isSettingsVersionOutdated(String settingsVersion) {
		return !SETTINGS_VERSION.equals(settingsVersion);
	}
	
    private static class OutdatedSettingsVersionException extends Exception {    }
}