package com.cleverua.bb.example;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.MainScreen;

public class AppSettingsScreen extends MainScreen {
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
            AppSettingsApplication.getSettings().load();
        } catch (Exception e) {
            Dialog.alert("Exception caught: " + e);
        }
        
        userChoice = new CheckboxField(CHECK_BOX_LABEL, AppSettingsApplication.getSettings().isCheckBoxChecked(), USE_ALL_WIDTH);
        userText = new EditField(USE_ALL_WIDTH);
        userText.setText(AppSettingsApplication.getSettings().getUserText());
        
        add(userText);
        add(userChoice);
        userText.setFocus();
        
        ButtonField saveButton = new ButtonField(SAVE_BUTTON_LABEL, FIELD_HCENTER);
        saveButton.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field arg0, int arg1) {
                AppSettingsApplication.getSettings().setCheckBoxChecked(userChoice.getChecked());
                AppSettingsApplication.getSettings().setUserText(userText.getText());
                try {
                    AppSettingsApplication.getSettings().flush();
                } catch (Exception e) {
                    Dialog.alert("Exception caught: " + e);
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
