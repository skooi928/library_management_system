module com.example {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    opens com.example to javafx.fxml;

    exports com.example;
}
