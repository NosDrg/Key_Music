package com.nosdrg.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.nosdrg.App;
import com.nosdrg.manager.SoundManager;

import javafx.application.Platform;

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
        System.out.println("Key Pressed: " + keyText);
        
        if (pressedKeys.contains(keyCode)) {
            return;
        }

        pressedKeys.add(keyCode);
        SoundManager.getInstance().playSound(keyText);

        if (captureCallback != null) {
            Consumer<String> tempCallback = captureCallback;

            // Reset biến gốc về null ngay lập tức để ngừng chế độ bắt phím
            captureCallback = null;

            // Gửi biến tạm sang JavaFX để xử lý
            Platform.runLater(() -> tempCallback.accept(keyText));
            
            return;
        }

        // Xử lý tổ hợp phím đặc biệt
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE && 
            (e.getModifiers() & NativeInputEvent.ALT_MASK) != 0) {
            
            try {
                GlobalScreen.unregisterNativeHook();
                System.exit(0);
                System.out.println("Application Exited.");
            } catch (NativeHookException nativeHookException) {
                nativeHookException.printStackTrace();
            }
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_ENTER && 
           (e.getModifiers() & NativeInputEvent.CTRL_MASK) != 0) {
            
            System.out.println("Opening Application Window.");
            App.showWindow();
            return;
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