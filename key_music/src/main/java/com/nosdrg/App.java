package com.nosdrg;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;

import com.nosdrg.listener.GlobalKeyListener;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class App extends Application {
    private static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/nosdrg/scene/MenuScene.fxml"));
        Parent root = loader.load();
    
        Scene scene;
        if (stage.getScene() != null) {
            scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
        } else {
            scene = new Scene(root);
        }
        stage.setTitle("Key Music");
        stage.setScene(scene);
        
        stage.setResizable(false);

        setupJNativeHook();
        
        try {
            GlobalScreen.registerNativeHook();
            // Tắt log rác
            java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(java.util.logging.Level.OFF);
            logger.setUseParentHandlers(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
        
        Platform.setImplicitExit(false);

        stage.show();
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

    public static void showWindow() {
        if (stage != null) {
            Platform.runLater(() -> {
                if (!stage.isShowing()) {
                    stage.show(); // Hiện lại nếu đang ẩn
                }
                if (stage.isIconified()) {
                    stage.setIconified(false); // Phục hồi nếu đang Minimized
                }
                stage.toFront(); // Đẩy lên trên cùng
            });
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
