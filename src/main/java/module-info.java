module com.chinesecheckers.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.chinesecheckers.demo to javafx.fxml;
    exports com.chinesecheckers.demo;
}