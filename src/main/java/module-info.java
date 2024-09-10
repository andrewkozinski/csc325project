module org.group3.csc325project {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.group3.csc325project to javafx.fxml;
    exports org.group3.csc325project;
}