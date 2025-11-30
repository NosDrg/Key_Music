package com.nosdrg.manager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.nosdrg.model.GlobalConfig;

public class ConfigManager {
    private static final String CONFIG_FILE = "app_settings.json"; // Đổi tên file cho đỡ nhầm
    private final Gson gson = new Gson();

    // Lưu cài đặt toàn cục
    public void saveGlobalConfig(GlobalConfig config) {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Đọc cài đặt toàn cục
    public GlobalConfig loadGlobalConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return new GlobalConfig(); // Trả về config mặc định (trống)

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, GlobalConfig.class);
        } catch (IOException e) {
            return new GlobalConfig();
        }
    }
}