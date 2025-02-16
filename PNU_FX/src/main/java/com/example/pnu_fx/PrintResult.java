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
import javafx.scene.control.TextArea;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

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
            List<String> firstLines = levenstein.readLargeFile(selectedFile1.getAbsolutePath(), 1000);
            List<String> secondLines = levenstein.readLargeFile(selectedFile2.getAbsolutePath(), 1000);

            List<String> firstParagraphs = splitTextIntoParagraphs(firstText);
            List<String> secondParagraphs = splitTextIntoParagraphs(secondText);
            compareParagraphs(firstParagraphs, secondParagraphs); // Тут порівнюються абзаци
            int totalDistance = 0;
            int minSize = Math.min(firstLines.size(), secondLines.size());
            for (int i = 0; i < minSize; i++) {
                totalDistance += levenstein.computeLevenshtein(firstLines.get(i), secondLines.get(i));
            }
        } else {
            uploadText.setText("Файли не вибрано або один із файлів не вибрано.");
        }

    }

    private void compareParagraphs(List<String> firstParagraphs, List<String> secondParagraphs) {
        StringBuilder info = new StringBuilder();
        List<String> paragraphInfos = new ArrayList<>();
        LevensteinDistance levenstein = new LevensteinDistance();
        int totalDistance = 0;
        int totalChanges = 0;

        int maxSize = Math.max(firstParagraphs.size(), secondParagraphs.size());
        for (int i = 0; i < maxSize; i++) {
            String firstParagraph = (i < firstParagraphs.size()) ? firstParagraphs.get(i) : "";
            String secondParagraph = (i < secondParagraphs.size()) ? secondParagraphs.get(i) : "";

            if (firstParagraph.equals(secondParagraph)) continue;
            List<LevensteinDistance.EditOperation> paragraphDifferences =
                    levenstein.findDifferencesOptimized(firstParagraph, secondParagraph);
            int paragraphDistance = paragraphDifferences.size();
            totalDistance += paragraphDistance;
            totalChanges += paragraphDistance;

            StringBuilder paragraphInfo = new StringBuilder();
            paragraphInfo.append("Абзац ").append(i + 1).append(":\n")
                    .append("Зміни в абзаці: ").append(paragraphDistance).append("\n")
                    .append("Оригінальний текст: ").append(firstParagraph).append("\n")
                    .append("Змінений текст: ").append(secondParagraph).append("\n");

            for (LevensteinDistance.EditOperation op : paragraphDifferences) {
                paragraphInfo.append(op.toString()).append("\n");
            }
            paragraphInfo.append("\n");

            paragraphInfos.add(paragraphInfo.toString());
        }

        info.append("\nЗагальна відстань між файлами: ").append(totalDistance).append("\n");
        info.append("Загальна кількість змін: ").append(totalChanges).append("\n"); 
        showDistanceWindow(info.toString(), paragraphInfos, totalChanges);
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


    private void showDistanceWindow(String info, List<String> paragraphInfos, int totalChanges) {
        Stage fileInfoStage = new Stage();
        fileInfoStage.setTitle("Відстань Левенштейна");

        Accordion accordion = new Accordion();

        for (String paragraphInfo : paragraphInfos) {
            String[] parts = paragraphInfo.split("\n", 3);
            if (parts.length < 3) continue;

            String title = parts[0] + " " + parts[1];
            String originalText = "Оригінальний текст:\n" + parts[2].split("Змінений текст:")[0].trim();
            String changedText = "Змінений текст:\n" + parts[2].split("Змінений текст:")[1].trim();

            TextArea contentArea = new TextArea(originalText + "\n\n" + changedText);
            contentArea.setWrapText(true);
            contentArea.setEditable(false);

            TitledPane pane = new TitledPane(title, contentArea);
            accordion.getPanes().add(pane);
        }

        Label totalChangesLabel = new Label("Загальна кількість змін: " + totalChanges);
        VBox root = new VBox(accordion, totalChangesLabel); // Add it at the bottom of the VBox
        Scene scene = new Scene(root, 1200, 800);
        fileInfoStage.setScene(scene);
        fileInfoStage.show();
    }
}
