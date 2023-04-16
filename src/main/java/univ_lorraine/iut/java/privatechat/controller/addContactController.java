package univ_lorraine.iut.java.privatechat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import univ_lorraine.iut.java.privatechat.App;

import java.io.IOException;
import java.util.Objects;
import java.util.Locale;
import java.text.NumberFormat;

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

    NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
    @FXML
    private void backToChat() throws IOException {
        //retourner au chat
        App.setWindowSize(765,795);
        App.setRoot("chat");
    }

    @FXML
    private void submit() throws IOException {
        if (!Objects.equals(nameField.getText(), "") && !Objects.equals(ipField.getText(), "") && !Objects.equals(portField.getText(), "")) {
            App.setRoot("chat", new Contact(nameField.getText(), ipField.getText(), Integer.parseInt(portField.getText())));
        }
}}
