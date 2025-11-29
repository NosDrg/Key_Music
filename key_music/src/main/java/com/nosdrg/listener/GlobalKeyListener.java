package com.nosdrg.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.nosdrg.manager.SoundManager;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;

// Lớp lắng nghe phím toàn cục và phát âm thanh tương ứng
public class GlobalKeyListener implements NativeKeyListener {
    private final Set<Integer> pressedKeys = new HashSet<>();

    private static Consumer<String> captureCallback = null;

    public static void setCaptureMode(Consumer<String> callback) {
        captureCallback = callback;
    }

    // Phương thức xử lý khi phím được nhấn
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int keyCode = e.getKeyCode();
        String keyText = NativeKeyEvent.getKeyText(keyCode);
        
        if (pressedKeys.contains(keyCode)) {
            return;
        }

        pressedKeys.add(keyCode);
        SoundManager.getInstance().playSound(keyText);

        if (captureCallback != null) {
            Consumer<String> tempCallback = captureCallback;

            // 2. Reset biến gốc về null ngay lập tức để ngừng chế độ bắt phím
            captureCallback = null;

            // 3. Gửi biến tạm sang JavaFX để xử lý
            Platform.runLater(() -> tempCallback.accept(keyText));
            
            return; // Dừng lại, không phát nhạc
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
                System.exit(0);
            } catch (NativeHookException nativeHookException) {
                nativeHookException.printStackTrace();
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        System.out.println("Key Typed: " + e.getKeyChar());
    }
}