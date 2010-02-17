package com.cleverua.bb.settings;

import java.io.IOException;
import java.util.Hashtable;

public class Settings {
    private Hashtable settingsHash;
    private static Settings instance;

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
    
    private Settings() {
        settingsHash = new Hashtable();
    }
    
    /*public boolean getValue(String key, boolean defaultValue) {
        Boolean result = Boolean.valueOf(settingsHash.get(key).toString());
        if (result == null) {
            return defaultValue;
        }
        return result.booleanValue();
    }*/
    
    public boolean getValue(String key) {
        return Utils.stringToBoolean(settingsHash.get(key).toString());
    }
    
    public int getValue(String key, int defaultValue) {
        /*Integer result = new Integer(settingsHash.get(key).toString());
        if (result == null) {
            return defaultValue;
        }
        return result.intValue();*/
        return Integer.parseInt(settingsHash.get(key).toString());
    }
    
    public long getValue(String key, long defaultValue) {
        /*Long result = new Long(settingsHash.get(key).toString());
        if (result == null) {
            return defaultValue;
        }
        return result.longValue();*/
        return Long.parseLong(settingsHash.get(key).toString());
    }
    
    public float getValue(String key, float defaultValue) {
        /*Float result = Float.parseFloat(settingsHash.get(key).toString());
        
        if (result == null) {
            return defaultValue;
        }
        return result.floatValue();*/
        return Float.parseFloat(settingsHash.get(key).toString());
    }
    
    public String getValue(String key, String defaultValue) {
        String result = (String) settingsHash.get(key);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }
    
    public boolean containsKey(String key) {
        return settingsHash.containsKey(key);
    }
       
    public Settings putValue(String key, boolean value) {
        settingsHash.put(key, new Boolean(value));
        return this;
    }

    public Settings putValue(String key, int value) {
        settingsHash.put(key, new Integer(value));
        return this;
    }

    public Settings putValue(String key, long value) {
        settingsHash.put(key, new Long(value));
        return this;
    }

    public Settings putValue(String key, float value) {
        settingsHash.put(key, new Float(value));
        return this;
    }
    
    public Settings putValue(String key, String value) {
        settingsHash.put(key, value);
        return this;
    }

    public Settings removeValue(String key) {
        settingsHash.remove(key);
        return this;
    }
    
    public Settings clear() {
        settingsHash.clear();
        return this;
    }
    
    public void load(String settingsFilePath) throws IOException {
        HashFileReader engine = new HashFileReader(settingsFilePath, settingsHash);
        engine.read();
    }
    
    public void save(String settingsFilePath) throws IOException  {
        HashFileReader engine = new HashFileReader(settingsFilePath, settingsHash);
        engine.save();
    }
}
