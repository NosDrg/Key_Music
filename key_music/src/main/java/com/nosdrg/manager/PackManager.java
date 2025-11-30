package com.nosdrg.manager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nosdrg.model.KeyConfig;
import com.nosdrg.model.KeySound;

import javafx.collections.ObservableList;

public class PackManager {
    private static final String PACKS_DIR = "sound_packs";
    private final Gson gson = new Gson();

    public PackManager() {
        // Tự động tạo thư mục sound_packs nếu chưa có
        new File(PACKS_DIR).mkdirs();
    }

    // 1. Lấy danh sách tên các gói đang có (để hiện lên ComboBox)
    public List<String> getAvailablePacks() {
        List<String> packs = new ArrayList<>();
        File folder = new File(PACKS_DIR);
        File[] subDirs = folder.listFiles(File::isDirectory); // Chỉ lấy thư mục
        
        if (subDirs != null) {
            for (File dir : subDirs) {
                packs.add(dir.getName());
            }
        }
        return packs;
    }

    // 2. Load cấu hình của một gói cụ thể
    public List<KeyConfig> loadPack(String packName) {
        File configFile = new File(PACKS_DIR + "/" + packName + "/config.json");
        
        if (!configFile.exists()) return new ArrayList<>();

        try (FileReader reader = new FileReader(configFile)) {
            Type listType = new TypeToken<ArrayList<KeyConfig>>(){}.getType();
            List<KeyConfig> configs = gson.fromJson(reader, listType);
            
            // QUAN TRỌNG: Cập nhật lại đường dẫn file wav
            for (KeyConfig cfg : configs) {
                String relativePath = cfg.getPath();
                // Nếu đường dẫn chưa trỏ vào thư mục pack thì nối thêm vào
                if (!relativePath.contains(File.separator)) {
                     String fullPath = PACKS_DIR + "/" + packName + "/" + relativePath;
                     cfg.setPath(fullPath);
                }
            }

            boolean hasDefault = configs.stream()
                .anyMatch(cfg -> cfg.getKey().equals("Default"));

            if (!hasDefault) {
                configs.add(0, new KeyConfig("Default", "Don't set")); 
                System.out.println("Đã tự động thêm Default key cho gói: " + packName);
            }

            return configs;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 3. Lưu gói mới (Save As...)
    public void saveAsPack(String packName, ObservableList<KeySound> tableData) {
        // Tạo thư mục cho gói mới
        File packDir = new File(PACKS_DIR + "/" + packName);
        packDir.mkdirs();

        List<KeyConfig> configList = new ArrayList<>();
        
        for (KeySound item : tableData) {
            String originalPath = item.getSoundFilePath();
            if (originalPath == null || originalPath.isEmpty()) continue;

            File sourceFile = new File(originalPath);
            String fileName = sourceFile.getName(); // Lấy tên file (vd: boom.wav)
            
            // Copy file wav vào thư mục gói (để gói mang đi đâu cũng chạy được)
            File destFile = new File(packDir, fileName);
            copyFile(sourceFile, destFile);

            // Lưu vào config chỉ tên file thôi cho gọn
            configList.add(new KeyConfig(item.getKeyName(), fileName));
        }

        // Ghi file config.json vào thư mục gói
        try (FileWriter writer = new FileWriter(new File(packDir, "config.json"))) {
            gson.toJson(configList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hàm copy file đơn giản (Java NIO)
    private void copyFile(File source, File dest) {
        try {
            java.nio.file.Files.copy(source.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}