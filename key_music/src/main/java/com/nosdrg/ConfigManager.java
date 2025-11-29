package com.nosdrg;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nosdrg.model.KeyConfig;
import com.nosdrg.model.KeySound;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final String CONFIG_FILE = "sound_config.json";
    private final Gson gson = new Gson();

    // HÀM LƯU: Chuyển từ dữ liệu bảng (ObservableList) -> JSON
    public void saveConfig(ObservableList<KeySound> tableData) {
        List<KeyConfig> configList = new ArrayList<>();
        
        // 1. Chuyển đổi dữ liệu JavaFX sang dữ liệu thuần
        for (KeySound item : tableData) {
            // Chỉ lưu những phím đã có đường dẫn file
            if (item.getSoundFilePath() != null && !item.getSoundFilePath().isEmpty()) {
                configList.add(new KeyConfig(item.getKeyName(), item.getSoundFilePath()));
            }
        }

        // 2. Ghi ra file
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(configList, writer);
            System.out.println("Đã lưu cấu hình thành công!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // HÀM ĐỌC: Đọc JSON -> Trả về List cấu hình
    // Trong ConfigManager.java
    public List<KeyConfig> loadConfig() {
        File configFile = new File(CONFIG_FILE);

        if (!configFile.exists()) {
            // SAI: return null;  <-- Nguyên nhân lỗi là đây
            // ĐÚNG: Trả về danh sách rỗng để vòng lặp không bị crash
            return new ArrayList<>(); 
        }

        try (FileReader reader = new FileReader(configFile)) {
            Type listType = new TypeToken<ArrayList<KeyConfig>>(){}.getType();
            List<KeyConfig> list = gson.fromJson(reader, listType);
            
            // Phòng trường hợp file json có nội dung "null" hoặc rỗng
            if (list == null) return new ArrayList<>();
            
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            // Nếu có lỗi đọc file, trả về danh sách rỗng
            return new ArrayList<>();
        }
    }
}