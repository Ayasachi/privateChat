package univ_lorraine.iut.java.privatechat.controller;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import univ_lorraine.iut.java.privatechat.App;


public class LoginController {

    private final File directory = new File("data");
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private static final String FILE_EXTENSION = ".pwd";

    private boolean isValidUsername(String username) {
        return username != null && !username.isEmpty();
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }


    private boolean checkPassword(String login, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(login + ".pwd"))) {
            String readLine = reader.readLine();
            String requiredLine = "password=" + hashPassword(password);
            if (requiredLine.equals(readLine)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    @FXML
    private void login() throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();


        // Vérifier si le nom d'utilisateur est valide
        if (!isValidUsername(username)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Le nom d'utilisateur est invalide");
            alert.showAndWait();
            return;
        }

        // Vérifier si le mot de passe est valide
        if (!isValidPassword(password)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Le mot de passe doit contenir au moins 8 caractères");
            alert.showAndWait();
            return;
        }

        // Vérifier si le compte existe déjà dans la base de données
        File passwordFile = new File(username + FILE_EXTENSION);
        if (passwordFile.exists()) {
            //verifier si le mot de passe est correct
            if (!checkPassword(username, password)) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText("Le mot de passe est incorrect");
                alert.showAndWait();
                return;
            }
        } else {
            // Créer le fichier contenant le mot de passe avec buffer Writer
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(passwordFile))) {
                writer.write("password=" + hashPassword(password));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Lancer le chat
        App.setUser(username);
        App.setRoot("chat");
        App.setWindowSize(765,795);

    }
}

