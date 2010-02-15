package com.cleverua.bb.example;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.cleverua.bb.settings.Settings;
import com.cleverua.bb.settings.Utils;

public class AppSettingsApplication extends UiApplication {
    private static final String NOT_IMPLEMENTED_MSG = "Not yet implemented";
    private static AppSettingsApplication application;
    private static Settings settings;
    
    public static AppSettingsApplication instance() {
        return application;
    }
    
    public static void main(String[] args) {
        application = new AppSettingsApplication();
        try {
        Utils.createDirIncludingAncestors(Settings.SETTINGS_FILE);
        } catch (Exception e) {
            Dialog.alert("Exception caught: " + e);
        }
        application.pushScreen(new AppSettingsScreen());
        application.enterEventDispatcher();
    }

    public static void exit() {
        System.exit(0);
    }

    public static Settings getSettings() {
        return settings;
    }

    public void showNotImplementedAlert() {
        invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(NOT_IMPLEMENTED_MSG);
            }
        });
    }
    
    private AppSettingsApplication() {
        settings = new Settings();
    }
}