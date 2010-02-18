package com.cleverua.bb.example;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.cleverua.bb.settings.Settings;

public class AppSettingsApplication extends UiApplication {
    private static final String NOT_IMPLEMENTED_MSG = "Not yet implemented";
    private static AppSettingsApplication application;
    private static Settings settings;
    private static AppSettingsDelegate delegate;
    
    public static AppSettingsApplication instance() {
        return application;
    }
    
    public static void main(String[] args) {
        application = new AppSettingsApplication();
        application.pushScreen(new AppSettingsScreen());
        application.enterEventDispatcher();
    }

    public static void exit() {
        System.exit(0);
    }

    public static Settings getSettings() {
        return settings;
    }
    
    public static AppSettingsDelegate getSettingsDelegate() {
        return delegate;
    }

    public void showNotImplementedAlert() {
        invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(NOT_IMPLEMENTED_MSG);
            }
        });
    }
    
    private AppSettingsApplication() {
        delegate = new AppSettingsDelegate();
        settings = new Settings(delegate);
    }
}