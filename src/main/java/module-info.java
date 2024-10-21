module org.group3.csc325project {
    requires javafx.controls;
    requires javafx.fxml;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires org.apache.commons.codec;
    requires com.google.api.apicommon;
    requires google.cloud.firestore;
    requires jbcrypt;
    requires com.google.auth;
    requires google.cloud.core;


    opens org.group3.csc325project to javafx.fxml;
    exports org.group3.csc325project;
}