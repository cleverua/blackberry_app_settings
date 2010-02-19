package com.cleverua.bb.example;

import com.cleverua.bb.settings.SettingsDelegate;
import com.cleverua.bb.settings.Utils;

public class AppSettingsDelegate extends SettingsDelegate {
    private static final int VERSION = 1;
    public static final String SETTINGS_FILE = 
        "file:///SDCard/your_application.settings";
    
    public static final String USER_TEXT_KEY = "userText";
    public static final String USER_CHOICE_KEY = "userChoice";
    
    private static final boolean USER_CHOICE_DEFAULT = true;
    
    public int version() {
        return VERSION;
    }
    
    public String filename() {
        return SETTINGS_FILE;
    }
    
    public boolean getUserChoice() {
        Object read = getData().get(USER_CHOICE_KEY);
        
        if (read == null) {
            return USER_CHOICE_DEFAULT;
        }
        
        return /*Utils.stringToBoolean*/((Boolean) read).booleanValue();
    }
    
    public void setUserChoice(boolean userChoice) {
        getData().put(USER_CHOICE_KEY, new Boolean(userChoice));
    }
    
    public String getUserText() {
        return (String) getData().get(USER_TEXT_KEY);
    }
    
    public void setUserText(String userText) {
        getData().put(USER_TEXT_KEY, userText);
    }

}
