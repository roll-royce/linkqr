package com.linkqr.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.linkqr.utils.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DashboardController {

    private static final Logger logger = Logger.getLogger(DashboardController.class.getName());

    @FXML private ImageView userPhoto;
    @FXML private TextField folderPathField;
    @FXML private ListView<String> qrListView;
    @FXML private Button logoutButton;

    private String userId;

    public void initialize(String userId) {
        this.userId = userId;
        loadUserPhoto();
        loadSavedFolder();
        loadQRList();
    }

    @FXML
    private void handleChangePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try {
                Image image = new Image(new FileInputStream(selectedFile));
                userPhoto.setImage(image);
                saveUserPhoto(selectedFile.getAbsolutePath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load image", e);
                showAlert("Error", "Failed to load image.", Alert.AlertType.ERROR);
            }
        }
    }

    private void saveUserPhoto(String imagePath) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE users SET profile_picture = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, imagePath);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to save profile picture", e);
            showAlert("Error", "Failed to save profile picture.", Alert.AlertType.ERROR);
        }
    }

    private void loadUserPhoto() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT profile_picture FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String imagePath = rs.getString("profile_picture");
                if (imagePath != null && !imagePath.isEmpty()) {
                    Image image = new Image(new FileInputStream(imagePath));
                    userPhoto.setImage(image);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load profile picture", e);
            showAlert("Error", "Failed to load profile picture.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBrowseFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select QR Code Save Location");

        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            folderPathField.setText(selectedDirectory.getAbsolutePath());
            saveFolderPath(selectedDirectory.getAbsolutePath());
        }
    }

    private void saveFolderPath(String folderPath) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE users SET qr_save_location = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, folderPath);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to save folder path", e);
            showAlert("Error", "Failed to save folder path.", Alert.AlertType.ERROR);
        }
    }

    private void loadSavedFolder() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT qr_save_location FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String folderPath = rs.getString("qr_save_location");
                if (folderPath != null && !folderPath.isEmpty()) {
                    folderPathField.setText(folderPath);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load folder path", e);
            showAlert("Error", "Failed to load folder path.", Alert.AlertType.ERROR);
        }
    }

    private void loadQRList() {
        qrListView.getItems().clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT qrid, source_text FROM qrcodes WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                qrListView.getItems().add(rs.getString("qrid") + " - " + rs.getString("source_text"));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load QR codes", e);
            showAlert("Error", "Failed to load QR codes.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/linkqr/login.fxml"));
            Parent loginPage = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loginPage));
            stage.setTitle("LinkQR - Login");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to logout", e);
            showAlert("Error", "Failed to logout.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
