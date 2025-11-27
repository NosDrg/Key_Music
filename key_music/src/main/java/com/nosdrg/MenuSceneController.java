package com.nosdrg;

import java.io.File;
import java.net.URL;

import java.util.ResourceBundle;
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
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.getInstance().setVolumeValue(newVal.intValue());
        });

        keyNameColumn.setCellValueFactory(cellData -> cellData.getValue().keyNameProperty());
        soundNameColumn.setCellValueFactory(cellData -> cellData.getValue().soundNameProperty());

        loadInitialData();

        keySoundTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                KeySound selectedRow = keySoundTable.getSelectionModel().getSelectedItem();
                if (selectedRow != null) {
                    openFileChooser(selectedRow);
                }
            }
        });
    }

    private void loadInitialData() {
        keySoundList = FXCollections.observableArrayList();
        keySoundList.add(new KeySound("Default", "click.wav", "D:/Folder/Java/Key_Music/key_music/src/main/resources/com/nosdrg/sounds/click.wav"));        
        keySoundList.add(new KeySound("Enter", "click.wav", "D:/Folder/Java/Key_Music/key_music/src/main/resources/com/nosdrg/sounds/click.wav"));        

        keySoundTable.setItems(keySoundList);
    }

    @FXML
    private void saveButtonClicked() {
        KeySound selected = keySoundTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            SoundManager.getInstance().updateKeySound(selected.keyNameProperty().get(), selected.getSoundFilePath());
            System.out.println("Đã lưu cài đặt cho phím: " + selected.keyNameProperty().get());
        }
    }

    @FXML
    private void defaultButtonClicked() {
        SoundManager.getInstance().clearAll();
        loadInitialData();
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
