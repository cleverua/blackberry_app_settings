package com.cleverua.bb.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.Base64OutputStream;


public class Settings {
	private static final String VERSION_KEY = "_version";
    private SettingsDelegate delegate;
    private SettingsFileEditor editor;

	public Settings(SettingsDelegate delegate) {
		this.delegate = delegate;
		this.editor = new SettingsFileEditor(delegate.filename());
	}
	
	public void initialize() throws SettingsException {
		load();
		set(VERSION_KEY, new Integer(delegate.version()));
	}

	public void flush() throws SettingsException {
		save();
	}
	
	public void set(String key, Object value) {
		delegate.put(key, value);
    }
	
	public Object get(String key) {
		return delegate.get(key);
	}
	
	public Enumeration getKeys() {
	    return delegate.getKeys();
	}
	
	public Object get(String key, Object defaultValue) {
		Object result = delegate.get(key);
		if(result == null) {
			result = defaultValue;
		}
		return result;
	}

	private void load() throws SettingsException {
        editor.read();
	}

	private void save() throws SettingsException {
        editor.save();
	}

	private class SettingsFileEditor {
	    private static final char NL = '\n';
	    private static final char EQ = '=';
	    private static final String UTF_8 = "UTF-8";
	    
	    private String fileName;
	    
	    public SettingsFileEditor(String fileName) {
	        this.fileName = fileName;
	    }
	    
	    public void read() throws SettingsException {
	        if(!Utils.isFilePresent(fileName)) {
	            try {
                    Utils.createFileIncludingDirs(fileName);
                } catch (IOException e) {
                    throw new SettingsException(e);
                }
	         } else {
	             readFile();
	         }  
	    }

	    public void save() throws SettingsException {
	        FileConnection fc = null;
	        OutputStream out = null;
	        OutputStreamWriter writer = null;
	        try {
                Utils.createFile(fileName);
	            fc = (FileConnection) Connector.open(fileName, Connector.READ_WRITE);
	            fc.truncate(0);
	            out = fc.openOutputStream();
	            writer = new OutputStreamWriter(out);

	            for(Enumeration e = getKeys(); e.hasMoreElements();) {
	                String key = (String) e.nextElement();
	                writer.write(key + EQ + encode(get(key).toString()) + NL);
	            }

	        } catch (IOException e) {
	            throw new SettingsException(e);
            } finally {
	            Utils.safelyCloseStream(writer);
	            Utils.safelyCloseStream(out);
	            Utils.safelyCloseStream(fc);
	        }
	    }

	    private void readFile() throws SettingsException {
	        synchronized (this) {
	            FileConnection fc = null;
	            InputStream in = null;

	            try {
	                fc = (FileConnection) Connector.open(delegate.filename(), Connector.READ);
	                if(fc.exists()) {
	                    in = fc.openInputStream();
	                    parse(Utils.getStringFromStream(in));
	                }
	            } catch (IOException e) {
                    throw new SettingsException(e);
                } finally {
	                Utils.safelyCloseStream(fc);
	                Utils.safelyCloseStream(in);
	            }               
	        }
	    }
	    
	    private void parse(String data) throws SettingsException {
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
	    
	    private void toProperty(String str) throws SettingsException   {
	        if(str.length() == 0) {
	            return;
	        }
	        int equalsPosition = str.indexOf(EQ);
	        String key = str.substring(0, equalsPosition).trim();
	        String value;
            try {
                value = decode(str.substring(equalsPosition + 1).trim());
                set(key, value);
            } catch (UnsupportedEncodingException e) {
                throw new SettingsException(e);
            } catch (IOException e) {
                throw new SettingsException(e);
            }
	    }
	    
	    private String encode(String stringToEncode) throws IOException {
	        byte[] bytesToEncode = stringToEncode.getBytes(UTF_8);
	        return Base64OutputStream.encodeAsString(bytesToEncode, 0, bytesToEncode.length, false, false);
	    }
	    
	    private String decode(String stringToDecode) throws UnsupportedEncodingException, IOException {
	        return new String(Base64InputStream.decode(stringToDecode), UTF_8);
	    }
	}
}
