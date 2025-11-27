package com.nosdrg.model;

public class KeyConfig {
    private String key;
    private String path; // Đường dẫn file âm thanh

    // Constructor mặc định (Bắt buộc cho Gson)
    public KeyConfig() {}

    public KeyConfig(String key, String path) {
        this.key = key;
        this.path = path;
    }

    // Getter & Setter
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}