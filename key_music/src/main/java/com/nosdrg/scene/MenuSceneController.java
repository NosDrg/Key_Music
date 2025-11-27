package com.nosdrg.scene;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.nosdrg.ConfigManager;
import com.nosdrg.manager.SoundManager;
import com.nosdrg.model.KeyConfig;
import com.nosdrg.model.KeySound;

import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MenuSceneController implements Initializable {
    @FXML private Button SaveButton;
    @FXML private Button DefaultButton;
    @FXML private Slider volumeSlider;
    @FXML private TableView<KeySound> keySoundTable;
    @FXML private TableColumn<KeySound, String> keyNameColumn;
    @FXML private TableColumn<KeySound, String> soundNameColumn;
    
    private ObservableList<KeySound> keySoundList;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        keyNameColumn.setCellValueFactory(cellData -> cellData.getValue().keyNameProperty());
        soundNameColumn.setCellValueFactory(cellData -> cellData.getValue().soundNameProperty());

        loadAndApplyConfig();

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

    private void loadAndApplyConfig() {
        keySoundList = FXCollections.observableArrayList();
        
        // 1. Tạo danh sách phím mặc định (Khung sườn)
        String[] defaultKeys = {"Space", "Enter", "Backspace", "A", "S", "D", "W", "Default"};
        
        // 2. Load cấu hình đã lưu từ file
        List<KeyConfig> savedConfigs = configManager.loadConfig();

        if (savedConfigs == null) {
            savedConfigs = new ArrayList<>(); // Nếu null thì biến nó thành rỗng
        }

        for (String key : defaultKeys) {
            String path = "";
            String name = "Chưa đặt";

            // Kiểm tra xem phím này có trong file save không
            for (KeyConfig cfg : savedConfigs) {
                if (cfg.getKey().equals(key)) {
                    path = cfg.getPath();
                    name = new java.io.File(path).getName(); // Lấy tên file cho đẹp
                    
                    // QUAN TRỌNG: Nạp ngay vào SoundManager để gõ là có tiếng luôn
                    SoundManager.getInstance().updateKeySound(key, path);
                    break;
                }
            }
            keySoundList.add(new KeySound(key, name, path));
        }

        keySoundTable.setItems(keySoundList);
    }

    @FXML
    private void saveButtonClicked() {
        configManager.saveConfig(keySoundList);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.getInstance().setVolumeValue(newVal.intValue());
        });
    }

    @FXML
    private void defaultButtonClicked() {
        SoundManager.getInstance().clearAll();
        loadAndApplyConfig();
        System.out.println("Đã đặt lại cài đặt mặc định.");
    }

    private void openFileChooser(KeySound row) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn âm thanh cho phím " + row.getKeyName());
        
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
}
