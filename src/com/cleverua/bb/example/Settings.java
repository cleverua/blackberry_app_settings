package com.cleverua.bb.example;

import com.cleverua.bb.settings.SettingsEngine;
import com.cleverua.bb.settings.SettingsEngineException;

public class Settings {
    public static final String SETTINGS_FILE = "file:///SDCard/your_application.settings";
    
    public static final String USER_TEXT_KEY = "userText";
    public static final String DESCRIPTION_KEY = "description";
    
    private SettingsEngine engine;
    private static Settings instance;
    private boolean loaded;
    
    private Settings() {
        engine = new SettingsEngine(SETTINGS_FILE);
    }
                                                                                                                                                                                               
    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
    
    public synchronized void load() throws SettingsEngineException {
        engine.load();
        loaded = true;
    }
    
    public synchronized void save() throws SettingsEngineException {
        engine.save();
    }

    public synchronized String getUserText() {
        if (!loaded) {
            throw new IllegalStateException("Should be loaded first");
        }
        return engine.getProperty(USER_TEXT_KEY, null);
    }
    
    public synchronized void setUserText(String value) {
        engine.setProperty(USER_TEXT_KEY, value);
    }
    
    public synchronized String getDescription() {
        if (!loaded) {
            throw new IllegalStateException("Should be loaded first");
        }
        return engine.getProperty(DESCRIPTION_KEY, null);
    }
    
    public synchronized void setDescription(String value) {
        engine.setProperty(DESCRIPTION_KEY, value);
    }
}
