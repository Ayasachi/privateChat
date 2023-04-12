package univ_lorraine.iut.java.privatechat.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import univ_lorraine.iut.java.privatechat.App;

import java.io.IOException;

public class ChatController {

    @FXML
    private void logout() throws IOException {
        //faire la deconnexion en fermant la fenetre
        App.setRoot("login");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Vous êtes déconnecté");
        alert.showAndWait();
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        //envoyer le message

    }

    @FXML
    private void addContact() throws IOException {
        //ajouter un contact
        App.setRoot("addContact");
    }
}