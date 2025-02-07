module com.example.pnu_fx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pnu_fx to javafx.fxml;
    exports com.example.pnu_fx;
}