package com.linkqr.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.linkqr.utils.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    /**
     * Handles user login when the login button is clicked.
     */
    @FXML
    private void handleLogin() {
        String usernameOrEmail = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username/Email and Password cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, username FROM users WHERE (username = ? OR email = ?) AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password); // TODO: Use hashed passwords in production.

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String userId = rs.getString("id");
                String username = rs.getString("username");

                System.out.println("✅ Login Successful! User: " + username);
                showAlert("Success", "Login Successful! Welcome, " + username, Alert.AlertType.INFORMATION);

                // Load dashboard after successful login
                loadDashboard(userId, username);
            } else {
                showAlert("Error", "Invalid Username/Email or Password.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Database Connection Failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Loads the dashboard screen after successful login.
     * 
     * @param userId   The logged-in user's ID.
     * @param username The logged-in user's name.
     */
    private void loadDashboard(String userId, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/linkqr/dashboard.fxml"));
            Parent dashboardPage = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(dashboardPage));
            stage.setTitle("LinkQR - Dashboard");
            System.out.println("📌 Dashboard Loaded for User: " + username);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to Load Dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handles navigation to the registration screen.
     */
    @FXML
    private void handleGoToRegister() {
        try {
            System.out.println("🔄 Navigating to Register Page...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/linkqr/register.fxml"));
            Parent registerPage = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(registerPage));
            stage.setTitle("LinkQR - Register");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to Load Registration Page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Displays an alert dialog with the given title and message.
     * 
     * @param title   The title of the alert.
     * @param message The message to display.
     * @param type    The type of alert (ERROR, INFORMATION, WARNING).
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
