package com.nosdrg.manager;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.media.AudioClip;

public class SoundManager {
    private static SoundManager instance;
    private double volumeValue = 50;

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private Map<String, AudioClip> fileCache = new HashMap<>();
    private Map<String, String> keyMap = new HashMap<>();

    // --- 1. HÀM CẬP NHẬT KHI NGƯỜI DÙNG CHỌN FILE TỪ UI ---
    public void updateKeySound(String keyName, String filePath) {
        // Nếu file chưa từng load, thì load vào cache
        if (!fileCache.containsKey(filePath)) {
            try {
                // Logic load file từ ổ cứng (khác với load từ Resource trong JAR)
                // Phải thêm "file:/" ở trước đường dẫn tuyệt đối
                String url = "file:///" + filePath.replace("\\", "/"); 
                AudioClip clip = new AudioClip(url);
                fileCache.put(filePath, clip);
            } catch (Exception e) {
                System.err.println("Lỗi load file: " + filePath);
                return;
            }
        }
        
        keyMap.put(keyName, filePath);
        System.out.println("Đã cập nhật phím " + keyName + " -> " + filePath);
    }

    public AudioClip getClip(String keyName) {
        String path = keyMap.get(keyName);
        
        if (path == null) path = keyMap.get("Default");
        
        if (path != null) {
            return fileCache.get(path);
        }
        return null;
    }

    public void removeKeySound(String keyName) {
        String path = keyMap.get(keyName);

        keyMap.remove(keyName);
        fileCache.remove(path);
    }

    public void clearAll() {
        keyMap.clear();
        fileCache.clear();
    }

    public double getVolumeValue() {
        return volumeValue;
    }

    public void setVolumeValue(double volumeValue) {
        this.volumeValue = volumeValue;
    }
    
}
