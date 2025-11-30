package com.nosdrg.model;

public class GlobalConfig {
    private String currentPackName; // Ví dụ: "Mechanical", "Mario"
    private double volume = 1.0;
    private double pitchRange = 0.0;
    private double balanceValue = 0.0;

    public GlobalConfig() {}

    // Getters & Setters
    public String getCurrentPackName() { return currentPackName; }
    public void setCurrentPackName(String name) { this.currentPackName = name; }
    
    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
    
    public double getPitchRange() { return pitchRange; }
    public void setPitchRange(double pitchRange) { this.pitchRange = pitchRange; }

    public double getBalanceValue() {
        return balanceValue;
    }

    public void setBalanceValue(double balanceValue) {
        this.balanceValue = balanceValue;
    }
}