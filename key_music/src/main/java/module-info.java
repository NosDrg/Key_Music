module com.nosdrg {
    // 1. JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.logging;

    // 2. Java Core (Cho SystemTray)
    requires java.desktop;
    
    // 3. Thư viện ngoài
    requires com.google.gson;
    requires jnativehook; 

    // 4. Mở quyền truy cập
    // Cho phép FXML đọc Controller
    opens com.nosdrg.scene to javafx.fxml;
    
    // Cho phép Gson đọc Model (KeyConfig)
    opens com.nosdrg.model to com.google.gson, javafx.base;

    // Xuất module chính để JavaFX chạy được
    exports com.nosdrg;
}