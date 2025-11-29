package com.nosdrg.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Lớp mô hình cho dữ liệu hiển thị trong bảng (TableView)
public class KeySound {
    private final StringProperty keyName;
    private final StringProperty soundName; 
    private String soundPath;

    public KeySound(String keyName, String soundName, String soundPath) {
        this.keyName = new SimpleStringProperty(keyName);
        this.soundName = new SimpleStringProperty(soundName);
        this.soundPath = soundPath;
    }

    public String getSoundFilePath() { return soundPath; }
    public String getKeyName() { return keyName.get(); }
    public String getSoundName() { return soundName.get(); }

    public StringProperty keyNameProperty() { return keyName; }
    public StringProperty soundNameProperty() { return soundName; }
    
    public String getSoundPath() { return soundPath; }
    public void setSoundPath(String soundPath) { this.soundPath = soundPath; }
    
    public void setSoundName(String soundName, String soundPath) { 
        this.soundName.set(soundName); 
        this.soundPath = soundPath;
    }
}