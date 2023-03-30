package univ_lorraine.iut.java.privatechat.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import univ_lorraine.iut.java.privatechat.App;

import java.io.IOException;

public class ChatController {

    @FXML
    private void logout() throws IOException {
        App.setRoot("login");
    }

}