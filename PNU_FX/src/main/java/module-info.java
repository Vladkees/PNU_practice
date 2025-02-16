module com.example.pnu_fx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.ooxml;
    requires org.apache.pdfbox;


    opens com.example.pnu_fx to javafx.fxml;
    exports com.example.pnu_fx;
    exports com.example.pnu_fx.DifferentTry;
    opens com.example.pnu_fx.DifferentTry to javafx.fxml;
}