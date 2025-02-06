package com.example.pnu_fx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
public class PrintResult {

    @FXML
    private Label uploadText;



    @FXML
    protected void onUploadButtonClick() {
        // Створюємо вікно для вибору файлу
        FileChooser fileChooser = new FileChooser();

        // Фільтри для типів файлів (за бажанням)
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        String file1Path = null;
        String file2Path = null;
        // Отримуємо поточне вікно Stage
        Stage stage = (Stage) uploadText.getScene().getWindow();

        File selectedFile1 = fileChooser.showOpenDialog(stage);
        if (selectedFile1 != null) {
             file1Path = selectedFile1.getAbsolutePath();  // Зберігаємо шлях до першого файлу
            uploadText.setText("Файл 1 вибрано: " + selectedFile1.getName());
        } else {
            uploadText.setText("Файл 1 не вибрано");

        }

        // Вибір другого файлу
        File selectedFile2 = fileChooser.showOpenDialog(stage);
        if (selectedFile2 != null) {
            file2Path = selectedFile2.getAbsolutePath();  // Зберігаємо шлях до другого файлу
            uploadText.setText(uploadText.getText() + "\nФайл 2 вибрано: " + selectedFile2.getName());
        } else {
            uploadText.setText(uploadText.getText() + "\nФайл 2 не вибрано");

        }
        if (file1Path != null && file2Path != null) {
            LevensteinDistance levenstein = new LevensteinDistance();
            String firstWord = levenstein.readFileContent(file1Path);
            String secondWord = levenstein.readFileContent(file2Path);
            int distance = levenstein.calculation(firstWord, secondWord);
            uploadText.setText(uploadText.getText() + "\nВідстань Левенштейна: " + distance);
        }
    }
}