package com.example.pnu_fx;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

public class PrintResult {
    @FXML
    private Label uploadText;

    @FXML
    protected void onUploadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Documents", "*.docx"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));

        Stage stage = (Stage) uploadText.getScene().getWindow();

        File selectedFile1 = fileChooser.showOpenDialog(stage);
        File selectedFile2 = fileChooser.showOpenDialog(stage);

        if (selectedFile1 != null && selectedFile2 != null) {
            uploadText.setText("Файл 1 вибрано: " + selectedFile1.getName() + "\nФайл 2 вибрано: " + selectedFile2.getName());

            String firstText = readFileContent(selectedFile1);
            String secondText = readFileContent(selectedFile2);
            LevensteinDistance levenstein = new LevensteinDistance();
            List<LevensteinDistance.EditOperation> differences = levenstein.findDifferences(firstText, secondText);
            int totalChanges = differences.size();
            List<String> firstParagraphs = splitTextIntoParagraphs(firstText);
            List<String> secondParagraphs = splitTextIntoParagraphs(secondText);
            compareParagraphs(firstParagraphs, secondParagraphs); // Тут порівнюються абзаци

        } else {
            uploadText.setText("Файли не вибрано або один із файлів не вибрано.");
        }
    }

    private void compareParagraphs(List<String> firstParagraphs, List<String> secondParagraphs) {
        StringBuilder info = new StringBuilder();
        int significantChangeThreshold = 10;
        LevensteinDistance levenstein = new LevensteinDistance();
        int totalDistance = 0;
        for (int i = 0; i < firstParagraphs.size(); i++) {
            String firstParagraph = firstParagraphs.get(i);
            if (firstParagraph.trim().isEmpty()) continue;
            int maxMatches = 0;
            String bestMatchParagraph = null;
            for (int j = 0; j < secondParagraphs.size(); j++) {
                String secondParagraph = secondParagraphs.get(j);
                if (secondParagraph.trim().isEmpty()) continue;
                int matchCount = countMatches(firstParagraph, secondParagraph);
                if (matchCount > maxMatches) {
                    maxMatches = matchCount;
                    bestMatchParagraph = secondParagraph;
                }
            }
            if (bestMatchParagraph != null) {
                List<LevensteinDistance.EditOperation> paragraphDifferences = levenstein.findDifferences(firstParagraph, bestMatchParagraph);
                int paragraphDistance = paragraphDifferences.size();
                totalDistance += paragraphDistance;  // Додаємо до загальної відстані
                if (paragraphDistance >= significantChangeThreshold) {
                    info.append("Абзац ").append(i + 1).append(":\n")
                            .append("Зміни в абзаці: ").append(paragraphDistance).append("\n");
                }
            }
        }
        info.append("\nЗагальна відстань між файлами: ").append(totalDistance).append("\n");
        showDistanceWindow(info.toString());
    }


    private int countMatches(String s1, String s2) {
        int matchCount = 0;
        int length = Math.min(s1.length(), s2.length());
        for (int i = 0; i < length; i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                matchCount++;
            }
        }
        return matchCount;
    }


    private String readFileContent(File file) {
        String content = "";
        try {
            if (file.getName().endsWith(".txt")) {
                content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            } else if (file.getName().endsWith(".docx")) {
                XWPFDocument doc = new XWPFDocument(new FileInputStream(file));
                StringBuilder sb = new StringBuilder();
                for (XWPFParagraph para : doc.getParagraphs()) {
                    sb.append(para.getText()).append("\n");
                }
                doc.close();
                content = sb.toString();
            } else if (file.getName().endsWith(".pdf")) {
                PDDocument document = PDDocument.load(file);
                PDFTextStripper stripper = new PDFTextStripper();
                content = stripper.getText(document);
                document.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            content = "Помилка читання файлу.";
        }
        return content;
    }

    private List<String> splitTextIntoParagraphs(String text) {
        String[] paragraphs = text.split("\n");
        List<String> paragraphList = new ArrayList<>();
        for (String paragraph : paragraphs) {
            if (!paragraph.trim().isEmpty()) {
                paragraphList.add(paragraph.trim());
            }
        }
        return paragraphList;
    }

    private void showDistanceWindow(String info) {
        Stage fileInfoStage = new Stage();
        fileInfoStage.setTitle("Відстань Левенштейна");
        TextArea textArea = new TextArea(info);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox root = new VBox(10, scrollPane);
        Scene scene = new Scene(root, 1200, 800);
        scrollPane.setPrefHeight(800);
        fileInfoStage.setScene(scene);
        fileInfoStage.show();
    }
}
