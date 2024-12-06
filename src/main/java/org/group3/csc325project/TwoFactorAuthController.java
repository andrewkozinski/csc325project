package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import de.taimos.totp.TOTP;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;

public class TwoFactorAuthController {
    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthController.class);

    @FXML
    private ImageView qrcodeImage;
    @FXML
    private TextField totpCodeField;
    @FXML
    private ImageView twoFactor_background_image_view;
    @FXML
    private ImageView twoFactor_title_background;
    @FXML
    private VBox twoFactor_screen;
    @FXML
    private AnchorPane twoFactor_screen_anchor;
    @FXML
    private Button verifyCode;

    private String secretToken;

    @FXML
    public void initialize() {
        //-------------------------------------------------------
        // Load the background images
        twoFactor_background_image_view.setImage(new Image(getClass().getResourceAsStream("/Images/Atlantis_Background.png")));
        twoFactor_title_background.setImage(new Image(getClass().getResourceAsStream("/Images/two_fact_header.png")));

        // Scaling ImagePanes
        twoFactor_background_image_view.fitWidthProperty().bind(twoFactor_screen.widthProperty());
        twoFactor_background_image_view.fitHeightProperty().bind(twoFactor_screen.heightProperty());

        twoFactor_title_background.fitWidthProperty().bind(twoFactor_screen.widthProperty());
        twoFactor_title_background.fitHeightProperty().bind(twoFactor_screen.heightProperty());

        twoFactor_screen_anchor.prefWidthProperty().bind(twoFactor_screen.widthProperty());
        twoFactor_screen_anchor.prefHeightProperty().bind(twoFactor_screen.heightProperty());

        // Scaling other elements

        twoFactor_screen.setFillWidth(true);
        qrcodeImage.setPreserveRatio(true);
        twoFactor_background_image_view.setPreserveRatio(true);
        twoFactor_title_background.setPreserveRatio(true);

        //-------------------------------------------------------
        String username = SessionManager.getLoggedInUsername();
        if (username == null) {
            RegistrationApp.setRoot("login");
            return;
        }

        secretToken = fetchSecretToken(username, SessionManager.getLoggedInUserRole());

        if (secretToken == null) {
            secretToken = generateSecretToken();
            saveSecretToken(username, SessionManager.getLoggedInUserRole(), secretToken);
            generateQRCode(username, secretToken);
        } else if (qrcodeImage != null) {
            qrcodeImage.setVisible(false);
        }
        verifyCode.setOnAction(event -> handleVerifyButtonClick());
    }

    @FXML
    private void handleVerifyButtonClick() {
        String totpCode = totpCodeField.getText().trim();

        if (secretToken == null) {
            showAlert("Two-Factor Authentication is not set up for this account.");
            return;
        }

        if (verifyTOTP(secretToken, totpCode)) {
            logger.info("TOTP verified successfully");

            Platform.runLater(() -> {
                if (verifyCode.getScene() != null && verifyCode.getScene().getWindow() != null) {
                    ((Stage) verifyCode.getScene().getWindow()).close();
                }
            });

            redirectToUserRoleScreen();
        } else {
            showAlert("Invalid TOTP code. Please try again.");
        }
    }

    private String generateSecretToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes).replace("=", "");
    }

    private void generateQRCode(String username, String secretToken) {
        String issuer = "Atlantis University";
        String qrCodeData = "otpauth://totp/" + issuer + ":" + username + "?secret=" + secretToken + "&issuer=" + issuer;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, 200, 200);

            BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            Image qrCodeImage = SwingFXUtils.toFXImage(bufferedImage, null);
            qrcodeImage.setImage(qrCodeImage);

        } catch (WriterException e) {
            logger.error("Error generating QR code", e);
        }
    }

    private String fetchSecretToken(String username, String collectionName) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            CollectionReference collection = db.collection(collectionName);
            ApiFuture<QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
            QuerySnapshot querySnapshot = future.get();

            if (!querySnapshot.isEmpty()) {
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    return document.getString("secretToken");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error fetching secret token for user: " + username, e);
        }
        return null;
    }

    private void saveSecretToken(String username, String collectionName, String secretToken) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            CollectionReference collection = db.collection(collectionName);
            ApiFuture<QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
            QuerySnapshot querySnapshot = future.get();

            if (!querySnapshot.isEmpty()) {
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    DocumentReference docRef = collection.document(document.getId());
                    docRef.update("secretToken", secretToken).get();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving secret token for user: " + username, e);
        }
    }

    private boolean verifyTOTP(String secretKey, String totpCode) {
        if (secretKey == null) {
            return false;
        }
        try {
            Base32 base32 = new Base32();
            byte[] decodedKey = base32.decode(secretKey);
            StringBuilder hexString = new StringBuilder();
            for (byte b : decodedKey) {
                hexString.append(String.format("%02x", b & 0xFF));
            }

            String generatedCode = TOTP.getOTP(hexString.toString());
            return generatedCode.equals(totpCode);
        } catch (Exception e) {
            logger.error("Error generating TOTP code", e);
            return false;
        }
    }
    private void redirectToUserRoleScreen() {
        String role = SessionManager.getLoggedInUserRole();
        logger.info("Redirecting user with role: " + role);

        if (role == null || role.isEmpty()) {
            logger.error("Role is null or empty.");
            showAlert("Error retrieving user role. Please try again.");
            return;
        }
        switch (role) {
            case "Admin":
                logger.info("Redirecting to Admin screen.");
                RegistrationApp.setRoot("admin");
                break;
            case "Student":
                logger.info("Redirecting to Student screen.");
                RegistrationApp.setRoot("student");
                break;
            case "Professor":
                logger.info("Redirecting to Professor screen.");
                RegistrationApp.setRoot("professor");
                break;
            default:
                logger.error("Invalid role detected: " + role);
                showAlert("Invalid user role detected. Unable to proceed.");
                break;
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Two-Factor Authentication");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public String getSecretToken() {
        return secretToken;
    }
}