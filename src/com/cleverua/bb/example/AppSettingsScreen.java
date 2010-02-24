package com.cleverua.bb.example;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.MainScreen;

import com.cleverua.bb.settings.SettingsEngineException;

public class AppSettingsScreen extends MainScreen {
    private static final String SETTINGS_SUCCESSFUL_DIALOG = "Settings were saved successfully!";
    private static final String SAVE_BUTTON_LABEL = "Save settings";
//    private static final String CHECK_BOX_LABEL = "User choice";
    
//    private CheckboxField userChoice;
    private EditField userText;
    private EditField description;
    
    
    public AppSettingsScreen() {
        super();
        AppSettingsApplication.instance().invokeLater(new Runnable() {
            public void run() {
                initUI();
            }
        });
    }
    
    private void initUI() {
        try {
            AppSettingsApplication.getSettings().load();
        } catch (SettingsEngineException e) {
            Dialog.alert("Unable to load settings: " + e.getCause());
            return;
        }
        userText = new EditField(USE_ALL_WIDTH);
        String userTextSetting = AppSettingsApplication.getSettings().getUserText();
        userText.setText(userTextSetting);
        
        description = new EditField(USE_ALL_WIDTH);
        String descriptionSetting = AppSettingsApplication.getSettings().getDescription();
        description.setText(descriptionSetting);
        
        add(userText);
        add(description);
        userText.setFocus();
        
        ButtonField saveButton = new ButtonField(SAVE_BUTTON_LABEL, FIELD_HCENTER);
        saveButton.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field arg0, int arg1) {
                try {
                    AppSettingsApplication.getSettings().setUserText(userText.getText());
                    AppSettingsApplication.getSettings().setDescription(description.getText());
                    AppSettingsApplication.getSettings().save();
                    Dialog.inform(SETTINGS_SUCCESSFUL_DIALOG);
                } catch (SettingsEngineException e) {
                    Dialog.alert("Unable to save settings: " + e.getCause());
                }
            }
        });
        setStatus(saveButton);
    }
    
    protected boolean onSavePrompt() {
        return true;
    }
}
