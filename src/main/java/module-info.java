module com.linkqr {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;

    opens com.linkqr to javafx.fxml;
    opens com.linkqr.controllers to javafx.fxml;
    exports com.linkqr;
}
