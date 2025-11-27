package com.nosdrg.listener;

import java.util.HashSet;
import java.util.Set;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.nosdrg.manager.SoundManager;

import javafx.scene.media.AudioClip;

public class GlobalKeyListener implements NativeKeyListener {
    private final Set<Integer> pressedKeys = new HashSet<>();

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        AudioClip clipToPlay = SoundManager.getInstance().getClip(keyText);
        
        if (pressedKeys.contains(e.getKeyCode())) {
            return;
        }

        pressedKeys.add(e.getKeyCode());

        if (clipToPlay != null) {
            clipToPlay.play(SoundManager.getInstance().getVolumeValue() / 100.0);
            System.out.println("Playing sound for key: " + keyText);
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