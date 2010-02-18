package com.cleverua.bb.example;

import com.cleverua.bb.settings.SettingsDelegate;
import com.cleverua.bb.settings.Utils;

public class AppSettingsDelegate extends SettingsDelegate {
    private static final int VERSION = 1;
    public static final String SETTINGS_FILE = 
        "file:///SDCard/your_application_dir/your_application.settings";
    
    public static final String USER_TEXT_KEY = "userText";
    public static final String USER_CHOICE_KEY = "userChoice";
    
    public int version() {
        return VERSION;
    }
    
    public String filename() {
        return SETTINGS_FILE;
    }
    
    public boolean getUserChoice() {
        return Utils.stringToBoolean(getData().get(USER_CHOICE_KEY).toString());
    }
    
    public void setUserChoice(boolean userChoice) {
        getData().put(USER_CHOICE_KEY, new Boolean(userChoice));
    }
    
    public String getUserText() {
        return getData().get(USER_TEXT_KEY).toString();
    }
    
    public void setUserText(String userText) {
        getData().put(USER_TEXT_KEY, userText);
    }

}
