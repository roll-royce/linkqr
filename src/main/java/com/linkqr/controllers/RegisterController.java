package com.linkqr.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;
import java.util.regex.Pattern;

import com.linkqr.utils.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField usernameField, nameField, ageField, cityField, emailField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private Button registerButton;

    /**
     * Handles user registration when the register button is clicked.
     */
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String gender = genderCombo.getValue();
        String city = cityField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Validate input fields
        if (username.isEmpty() || name.isEmpty() || ageText.isEmpty() || gender == null || city.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
            return;
        }

        // Validate age
        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age < 1 || age > 120) {
                showAlert("Error", "Age must be between 1 and 120.", Alert.AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid age. Please enter a number.", Alert.AlertType.ERROR);
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showAlert("Error", "Invalid email format.", Alert.AlertType.ERROR);
            return;
        }

        // Validate password match
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        // Generate unique user ID
        String userId = "U" + UUID.randomUUID().toString().substring(0, 8);

        // Store user in the database
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (id, username, name, age, gender, city, email, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, name);
            stmt.setInt(4, age);
            stmt.setString(5, gender);
            stmt.setString(6, city);
            stmt.setString(7, email);
            stmt.setString(8, password); // TODO: Hash passwords before storing

            stmt.executeUpdate();
            showAlert("Success", "Registration successful! You can now log in.", Alert.AlertType.INFORMATION);

            // Navigate to the login page
            goToLogin();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Registration failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Redirects user to the login page.
     */
    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/linkqr/login.fxml"));
            Parent loginPage = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginPage));
            stage.setTitle("LinkQR - Login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Login page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Validates an email format.
     *
     * @param email The email to validate.
     * @return True if the email is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
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
