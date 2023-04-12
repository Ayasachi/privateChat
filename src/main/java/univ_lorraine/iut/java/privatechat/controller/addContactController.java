package univ_lorraine.iut.java.privatechat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import univ_lorraine.iut.java.privatechat.App;

import java.io.IOException;

public class addContactController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField ipField;

    @FXML
    private TextField portField;

    private boolean isValidName(String name) {
        return name != null && !name.isEmpty();
    }

    private boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty();
    }

    private boolean isValidPort(String port) {
        return port != null && !port.isEmpty();
    }


    @FXML
    private void backToChat() throws IOException {
        //retourner au chat
        App.setRoot("chat");
    }

    @FXML
    private void addContact() {

    }
}
