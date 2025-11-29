package com.nosdrg;



import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import com.nosdrg.listener.GlobalKeyListener;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/nosdrg/scene/MenuScene.fxml"));
        Parent root = loader.load();
    
        Scene scene;
        if (primaryStage.getScene() != null) {
            scene = new Scene(root, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());
        } else {
            scene = new Scene(root);
        }
        primaryStage.setTitle("Key Music");
        primaryStage.setScene(scene);
        
        primaryStage.setResizable(false);

        setupJNativeHook();
        // ... Code load SoundManager và JNativeHook giữ nguyên ...
        
        try {
            GlobalScreen.registerNativeHook();
            // Tắt log rác
            java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(java.util.logging.Level.OFF);
            logger.setUseParentHandlers(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Đăng ký class xử lý phím (Class mà mình đã hướng dẫn bạn sửa để gọi SoundManager ấy)
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());

        // 3. Ẩn hiện Tray Icon (giữ nguyên logic cũ của bạn)
        
        primaryStage.show();
    }

    private void setupJNativeHook() {
        // Tắt log spam
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            GlobalScreen.registerNativeHook();
            // Đăng ký class xử lý riêng biệt của bạn
            GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // @Override
    // public void stop() throws Exception {
    //     GlobalScreen.unregisterNativeHook();
    //     super.stop();
    // }

    public static void main(String[] args) {
        launch(args);
    }
}
