package com.nosdrg.manager;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.media.AudioClip;

// Quản lý âm thanh và ánh xạ phím
public class SoundManager {
    private static SoundManager instance;
    private double volumeValue = 50;
    private double pitchValue = 0;
    private double balanceValue = 0;

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private SoundManager() {}

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


    // --- 2. HÀM LẤY CLIP THEO TÊN PHÍM ---
    // Trả về AudioClip tương ứng với tên phím
    public AudioClip getClip(String keyName) {
        String path = keyMap.get(keyName);
        
        if (path == null) path = keyMap.get("Default");
        
        if (path != null) {
            return fileCache.get(path);
        }
        return null;
    }

    // --- 3. HÀM XÓA PHÍM ---
    public void removeKeySound(String keyName) {
        String path = keyMap.get(keyName);

        keyMap.remove(keyName);
    }

    // --- 4. HÀM XÓA TẤT CẢ PHÍM ---
    public void clearAll() {
        keyMap.clear();
        fileCache.clear();
    }

    // --- 5. HÀM PHÁT ÂM THANH THEO TÊN PHÍM ---
    public void playSound(String keyName) {
        AudioClip clip = getClip(keyName);
        if (clip != null) {
            double rate = 1;
            double balance = 0;
            if (pitchValue > 0) {
            rate = 1 + 2 * Math.random() * pitchValue - pitchValue;
            }
            if (balanceValue > 0) {
            balance = Math.random() * 0.5;
            }
            clip.play(volumeValue / 100.0, balance, rate, 0, 1);
        }
    }

    public double getVolumeValue() {
        return volumeValue;
    }

    public void setVolumeValue(double volumeValue) {
        this.volumeValue = volumeValue;
    }
    

    public double getPitchValue() {
        return pitchValue * 100;
    }

    public void setPitchValue(double pitchValue) {
        this.pitchValue = pitchValue / 100;
    }

    public double getBalanceValue() {
        return balanceValue * 100;
    }

    public void setBalanceValue(double balanceValue) {
        this.balanceValue = balanceValue / 100;
    }
}
