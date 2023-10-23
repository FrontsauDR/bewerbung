module com.example.marketoptimizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires com.fasterxml.jackson.databind;
    requires java.sql;


    opens com.example.marketoptimizer to javafx.fxml;
    exports com.example.marketoptimizer;
    exports com.example.marketoptimizer.database;
    opens com.example.marketoptimizer.database to javafx.fxml;
}