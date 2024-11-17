module org.group3.csc325project {
    requires javafx.controls;
    requires javafx.fxml;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires org.apache.commons.codec;
    requires com.google.api.apicommon;
    requires google.cloud.firestore;
    opens user to google.cloud.firestore;
    opens course to google.cloud.firestore;
    requires jbcrypt;
    requires com.google.auth;
    requires google.cloud.core;
    requires javafx.swing;
    requires java.desktop;
    requires totp;
    requires com.google.zxing;
    requires org.slf4j;

    exports user;

    exports course;

    opens org.group3.csc325project to javafx.fxml;
    exports org.group3.csc325project;
}