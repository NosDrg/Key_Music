package com.nosdrg.scene;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.nosdrg.listener.GlobalKeyListener;
import com.nosdrg.manager.ConfigManager;
import com.nosdrg.manager.PackManager;
import com.nosdrg.manager.SoundManager;
import com.nosdrg.model.GlobalConfig;
import com.nosdrg.model.KeyConfig;
import com.nosdrg.model.KeySound;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MenuSceneController implements Initializable {
    @FXML private Label textLabel;
    @FXML private Button SaveButton;
    @FXML private Button DefaultButton;
    @FXML private Button btnAddKey;
    @FXML private Button btnRemoveKey;
    @FXML private Slider volumeSlider;
    @FXML private Slider pitchSlider;
    @FXML private Slider balanceSlider;
    @FXML private TableView<KeySound> keySoundTable;
    @FXML private TableColumn<KeySound, String> keyNameColumn;
    @FXML private TableColumn<KeySound, String> soundNameColumn;
    @FXML private ComboBox<String> comboPacks;
    private final PackManager packManager = new PackManager();
    
    private ObservableList<KeySound> keySoundList;
    private GlobalConfig currentGlobalConfig;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        textLabel.setVisible(false);
        keyNameColumn.setCellValueFactory(new PropertyValueFactory<>("keyName"));
        soundNameColumn.setCellValueFactory(new PropertyValueFactory<>("soundName"));

        // Lấy tham chiếu list để thao tác sau này
        keySoundList = keySoundTable.getItems();

        // Đọc file app_settings.json
        currentGlobalConfig = configManager.loadGlobalConfig();

        
        // Volume   
        double savedVol = currentGlobalConfig.getVolume();
        volumeSlider.setValue(savedVol * 100); 
        SoundManager.getInstance().setVolumeValue(savedVol);

        // Pitch (Biến thiên cao độ)
        double savedPitch = currentGlobalConfig.getPitchRange();
        pitchSlider.setValue(savedPitch);
        SoundManager.getInstance().setPitchValue(savedPitch);

        // Balance (Cân bằng loa trái phải)
        double savedBalance = currentGlobalConfig.getBalanceValue();
        balanceSlider.setValue(savedBalance);
        SoundManager.getInstance().setBalanceValue(savedBalance);

        refreshPackList(); 

        String lastUsedPack = currentGlobalConfig.getCurrentPackName();
        
        if (lastUsedPack != null && !lastUsedPack.isEmpty() && !lastUsedPack.equals("Default")) {
            comboPacks.setValue(lastUsedPack); 
            applyPack(lastUsedPack); 
        } else {
            loadDefaultConfig(); 
            comboPacks.setValue("Default");
            currentGlobalConfig.setCurrentPackName("Default");
        }

        comboPacks.setOnAction(e -> {
            String selectedPack = comboPacks.getValue();
            if (selectedPack != null) {
                applyPack(selectedPack);
                
                currentGlobalConfig.setCurrentPackName(selectedPack);
                configManager.saveGlobalConfig(currentGlobalConfig);
            }
        });

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double vol = newVal.doubleValue() / 100.0;
            SoundManager.getInstance().setVolumeValue(vol);
            currentGlobalConfig.setVolume(vol);
            configManager.saveGlobalConfig(currentGlobalConfig);
        });

        pitchSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double val = newVal.doubleValue();
            SoundManager.getInstance().setPitchValue(val);
            currentGlobalConfig.setPitchRange(val);
            configManager.saveGlobalConfig(currentGlobalConfig);
        });

        balanceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double val = newVal.doubleValue();
            SoundManager.getInstance().setBalanceValue(val);
            currentGlobalConfig.setBalanceValue(val);
            configManager.saveGlobalConfig(currentGlobalConfig);
        });

        keySoundTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                KeySound selectedRow = keySoundTable.getSelectionModel().getSelectedItem();
                if (selectedRow != null) {
                    openFileChooser(selectedRow);
                }
            }
        });
    }

    private final ConfigManager configManager = new ConfigManager();

    private void loadDefaultConfig() {
        // 1. Reset dữ liệu cũ
        keySoundList.clear();
        SoundManager.getInstance().clearAll(); // Nhớ viết hàm clearAll trong SoundManager để xóa Map cũ

        // 2. Định nghĩa danh sách phím mặc định bạn muốn hỗ trợ
        String[] standardKeys = {"Default"};
        
        // 3. Đường dẫn file âm thanh mặc định (Nằm trong src/main/resources/sounds/...)
        String defaultSoundPath = "/sounds/click.wav"; 

        for (String key : standardKeys) {
            // Nạp vào SoundManager (Nó sẽ tự hiểu đây là file nội bộ trong JAR)
            SoundManager.getInstance().updateKeySound(key, defaultSoundPath);

            keySoundList.add(new KeySound(key, "Default (Internal)", defaultSoundPath));
        }

        // 4. Cập nhật bảng
        keySoundTable.setItems(keySoundList);
        
        System.out.println("Đã load cấu hình mặc định (Do chưa chọn Pack nào).");
    }

    // Save button clicked
    @FXML
    private void saveButtonClicked() {
        // 1. Lưu cấu hình chung (Volume, Pitch...)
        configManager.saveGlobalConfig(currentGlobalConfig);
        
        // 2. Lưu danh sách phím vào Gói hiện tại (QUAN TRỌNG)
        String currentPack = comboPacks.getValue();
        if (currentPack != null && !currentPack.equals("Default")) {
            // Lưu đè lên gói hiện tại
            packManager.saveAsPack(currentPack, keySoundList); 
            System.out.println("Đã lưu cập nhật vào gói: " + currentPack);
        }
        
        System.out.println("Đã lưu toàn bộ cấu hình!");
    }

    @FXML
    private void defaultButtonClicked() {
        SoundManager.getInstance().clearAll();
        loadDefaultConfig();
        currentGlobalConfig.setCurrentPackName("Default");
        System.out.println("Đã đặt lại cài đặt mặc định.");
    }

    private void openFileChooser(KeySound row) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose the sound for key " + row.getKeyName());
        
        // Chỉ cho chọn file .wav
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Audio Files", "*.wav")
        );

        // Lấy cửa sổ hiện tại để hiện dialog lên trên nó
        Stage stage = (Stage) keySoundTable.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // 1. Cập nhật giao diện (Model)
            row.setSoundName(selectedFile.getName(), selectedFile.getAbsolutePath());
            
            // 2. Cập nhật SoundManager (Logic)
            SoundManager.getInstance().updateKeySound(row.getKeyName(), selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void onAddKeyClicked() {
        // 1. Đổi giao diện để báo hiệu đang chờ
        textLabel.setVisible(true);
        btnAddKey.setDisable(true); // Khóa nút lại để không bấm lung tung
        btnAddKey.setText("......");

        if (btnRemoveKey != null) {
            btnRemoveKey.setDisable(true);
        }
        SaveButton.setDisable(true);
        DefaultButton.setDisable(true);
        keySoundTable.setDisable(true);

        // 2. Kích hoạt chế độ bắt phím bên KeyHandler
        GlobalKeyListener.setCaptureMode((newKeyName) -> {            
            // Kiểm tra xem phím này đã có trong bảng chưa
            boolean exists = keySoundList.stream()
                    .anyMatch(item -> item.getKeyName().equals(newKeyName));

            if (exists) {
                System.out.println("Key " + newKeyName + " already exists!");
            } else {
                // Thêm vào bảng với âm thanh mặc định (hoặc rỗng)
                KeySound newItem = new KeySound(newKeyName, "Don't set", "");
                keySoundList.add(newItem);
                
                // Cuộn bảng xuống dòng cuối cùng cho dễ nhìn
                keySoundTable.scrollTo(newItem);
                keySoundTable.getSelectionModel().select(newItem);
                
                String currentPack = comboPacks.getValue();
                if (currentPack != null && !currentPack.equals("Default")) {
                    // Lưu ngay lập tức thay đổi vào file JSON của gói
                    packManager.saveAsPack(currentPack, keySoundList);
                }            
            }

            // 3. Trả lại trạng thái cũ cho nút bấm
            textLabel.setVisible(false);
            btnAddKey.setText("Add Key");
            btnAddKey.setDisable(false);

            if (btnRemoveKey != null) {
                btnRemoveKey.setDisable(false);
            }
            SaveButton.setDisable(false);
            DefaultButton.setDisable(false);
            keySoundTable.setDisable(false);
        });
    }

    @FXML
    public void onRemoveClicked() {
        // 1. Đổi giao diện để báo hiệu đang chờ
        textLabel.setVisible(true);
        btnRemoveKey.setDisable(true); // Khóa nút lại để không bấm lung tung
        btnRemoveKey.setText("......");

        if (btnAddKey != null) {
            btnAddKey.setDisable(true);
        }
        SaveButton.setDisable(true);
        DefaultButton.setDisable(true);
        keySoundTable.setDisable(true);

        // 2. Kích hoạt chế độ bắt phím bên KeyHandler
        GlobalKeyListener.setCaptureMode((newKeyName) -> {
            // Kiểm tra xem phím này đã có trong bảng chưa
            boolean exists = keySoundList.removeIf(item -> item.getKeyName().equals(newKeyName));

            if (exists) {
                SoundManager.getInstance().removeKeySound(newKeyName);

                String currentPack = comboPacks.getValue();
                if (currentPack != null && !currentPack.equals("Default")) {
                    // Lưu ngay lập tức thay đổi vào file JSON của gói
                    packManager.saveAsPack(currentPack, keySoundList);
                }            
            } else {                
                System.out.println("Key " + newKeyName + " don't exist!");
            }

            // 3. Trả lại trạng thái cũ cho nút bấm
            textLabel.setVisible(false);
            btnRemoveKey.setText("Remove Key");
            btnRemoveKey.setDisable(false);
            
            keySoundTable.setDisable(false);
            if (btnAddKey != null) {
                btnAddKey.setDisable(false);
            }
            SaveButton.setDisable(false);
            DefaultButton.setDisable(false);
        });
    }

    private void refreshPackList() {
        comboPacks.getItems().clear();
        comboPacks.getItems().addAll(packManager.getAvailablePacks());
    }

    // Hàm áp dụng gói được chọn
    private void applyPack(String packName) {
        // 1. Dọn dẹp cũ
        SoundManager.getInstance().clearAll();
        keySoundList.clear(); // Xóa dữ liệu trên bảng

        // 2. Load config từ gói
        List<KeyConfig> packConfigs = packManager.loadPack(packName);
        
        // 3. Đổ dữ liệu vào bảng và SoundManager
        if (packConfigs != null) {
            for (KeyConfig cfg : packConfigs) {
                // Nạp vào SoundManager
                SoundManager.getInstance().updateKeySound(cfg.getKey(), cfg.getPath());

                // Giả sử KeySound có constructor (key, name, path)
                String displayName = new File(cfg.getPath()).getName();
                keySoundList.add(new KeySound(cfg.getKey(), displayName, cfg.getPath()));
            }
        }
        
        System.out.println("Đã chuyển sang gói: " + packName);
    }

    @FXML
    public void onSavePackClicked() {
        String currentPack = comboPacks.getValue();

        // 1. Kiểm tra an toàn
        if (currentPack == null || currentPack.equals("Default")) {
            showAlert("Không thể lưu", "Không thể ghi đè gói Mặc định (Default). Hãy dùng 'Tạo gói mới'.");
            return;
        }

        // 2. Lưu danh sách phím vào gói hiện tại
        // Hàm saveAsPack của PackManager thực chất là ghi đè nếu tên gói đã tồn tại -> Dùng lại được luôn
        packManager.saveAsPack(currentPack, keySoundList);
        
        // 3. Lưu cài đặt chung (Volume, Pitch...)
        configManager.saveGlobalConfig(currentGlobalConfig);

        System.out.println("Đã cập nhật gói: " + currentPack);
        showAlert("Thành công", "Đã lưu cập nhật cho gói " + currentPack);
    }

    @FXML
    public void onAddPackClicked() {
        TextInputDialog dialog = new TextInputDialog("NewPack");
        dialog.setTitle("Tạo Gói Âm Thanh Mới");
        dialog.setHeaderText("Nhập tên cho gói mới:");
        dialog.setContentText("Tên gói:");

        dialog.showAndWait().ifPresent(name -> {
            if (name.trim().isEmpty()) {
                showAlert("Lỗi", "Tên gói không được để trống!");
                return;
            }

            // 1. Lưu gói mới (Copy toàn bộ âm thanh hiện tại sang thư mục mới)
            packManager.saveAsPack(name, FXCollections.observableArrayList());
            
            // 2. Cập nhật ComboBox
            refreshPackList();
            
            // 3. Chuyển sang dùng gói mới luôn
            comboPacks.setValue(name);
            
            // 4. Lưu Global Config để nhớ gói mới này
            currentGlobalConfig.setCurrentPackName(name);
            configManager.saveGlobalConfig(currentGlobalConfig);
            
            showAlert("Thành công", "Đã tạo và chuyển sang gói: " + name);
        });
    }

    // Hàm phụ trợ để hiện thông báo (Alert)
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
