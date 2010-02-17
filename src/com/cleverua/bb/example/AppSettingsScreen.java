package com.cleverua.bb.example;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.MainScreen;

public class AppSettingsScreen extends MainScreen {
    private static final String USER_TEXT_KEY = "userText";
    private static final String USER_CHOICE_KEY = "userChoice";
    private static final String SETTINGS_SUCCESSFUL_DIALOG = "Settings were saved successfully!";
    private static final String SAVE_BUTTON_LABEL = "Save settings";
    private static final String CHECK_BOX_LABEL = "User choice";
    
    private CheckboxField userChoice;
    private EditField userText;
    
    
    public AppSettingsScreen() {
        super();
        initUI();
    }

    private void initUI() {
        try {
            AppSettingsApplication.getSettings().load(AppSettingsApplication.SETTINGS_FILE);
        } catch (Exception e) {
            Dialog.alert("Unable to load settings: " + e);
        }
        
        userChoice = new CheckboxField(CHECK_BOX_LABEL, AppSettingsApplication.getSettings().getValue(USER_CHOICE_KEY, false), USE_ALL_WIDTH);
        userText = new EditField(USE_ALL_WIDTH);
        userText.setText(AppSettingsApplication.getSettings().getValue(USER_TEXT_KEY, null));
        
        add(userText);
        add(userChoice);
        userText.setFocus();
        
        ButtonField saveButton = new ButtonField(SAVE_BUTTON_LABEL, FIELD_HCENTER);
        saveButton.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field arg0, int arg1) {
                AppSettingsApplication.getSettings().putValue(USER_CHOICE_KEY, userChoice.getChecked());
                AppSettingsApplication.getSettings().putValue(USER_TEXT_KEY, userText.getText());
                try {
                    AppSettingsApplication.getSettings().save(AppSettingsApplication.SETTINGS_FILE);
                } catch (Exception e) {
                    Dialog.alert("Unable to save settings: " + e);
                }
                Dialog.inform(SETTINGS_SUCCESSFUL_DIALOG);
//                AppSettingsApplication.instance().showNotImplementedAlert();
            }
        });
        setStatus(saveButton);
    }
    
    protected boolean onSavePrompt() {
        return true;
    }
}
