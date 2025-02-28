module com.linkqr {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.linkqr to javafx.fxml;
    exports com.linkqr;
}
