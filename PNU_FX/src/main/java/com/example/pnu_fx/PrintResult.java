package com.example.pnu_fx;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
public class PrintResult {
    @FXML
    private Label uploadText;
    @FXML
    protected void onUploadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        String file1Path = null;
        String file2Path = null;
        Stage stage = (Stage) uploadText.getScene().getWindow();
        File selectedFile1 = fileChooser.showOpenDialog(stage);
        if (selectedFile1 != null) {
             file1Path = selectedFile1.getAbsolutePath();
            uploadText.setText( "Файл 1 вибрано: " + selectedFile1.getName());
        } else {
            uploadText.setText("Файл 1 не вибрано");
        }
        File selectedFile2 = fileChooser.showOpenDialog(stage);
        if (selectedFile2 != null) {
            file2Path = selectedFile2.getAbsolutePath();
            uploadText.setText(uploadText.getText() + "\nФайл 2 вибрано: " + selectedFile2.getName());
        } else {
            uploadText.setText(uploadText.getText() + "\nФайл 2 не вибрано");
        }
        if (file1Path != null && file2Path != null) {
            LevensteinDistance levenstein = new LevensteinDistance();
            String firstWord = levenstein.readFileContent(file1Path);
            String secondWord = levenstein.readFileContent(file2Path);
            // Отримання списку змін
            List<LevensteinDistance.EditOperation> differences = levenstein.findDifferences(firstWord, secondWord);
            // Кількість змін - це фактична відстань Левенштейна
            int distance = differences.size();
            uploadText.setText(uploadText.getText() + "\nВідстань Левенштейна: " + distance);

            for (LevensteinDistance.EditOperation edit : differences) {
                uploadText.setText(uploadText.getText() + "\n" + edit);
        }
    }
}}