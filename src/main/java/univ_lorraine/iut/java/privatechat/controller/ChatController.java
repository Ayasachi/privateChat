package univ_lorraine.iut.java.privatechat.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import univ_lorraine.iut.java.privatechat.App;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatController {

    @FXML private ListView<Conversation> conversationListView;
    private ObservableList<Conversation> conversationList = FXCollections.observableArrayList();
    public void addConversation(Conversation conversation) {
        conversationList.add(conversation);
    }

    public void initialize() {
        String userLogin = App.getUser();
        if (conversationListView != null) {
            conversationListView.setItems(conversationList);
            conversationListView.setCellFactory(listView -> new listeConversaton());
        }

        File contactFile = new File(App.getUser() + "_contact.obj");
        FileInputStream f = null;
        ObjectInputStream s = null;
        if (contactFile.exists()) {
            try {
                f = new FileInputStream(contactFile);
                s = new ObjectInputStream(f);
                List<Conversation> List = (List)s.readObject();
                for (Conversation conversation : List) {
                    conversationList.add(conversation);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            finally {
                try {
                    s.close();
                    f.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void uninitialize() {
        FileOutputStream f = null;
        ObjectOutputStream s = null;
        try {
            // Créer une liste de conversations
            List<Conversation> conversations = new ArrayList<>(conversationList);

            // Essayer de la sérialiser
            File contactFile = new File(App.getUser() + "_contact.obj");
            f = new FileOutputStream(contactFile);
            s = new ObjectOutputStream(f);
            s.writeObject(conversations);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (s != null) {
                try {

                    s.close();
                    f.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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

    public void setData(Object data) {
        if (data instanceof Contact) {
            var contact = (Contact) data;
            conversationList.add(new Conversation(null,contact));
        }
    }
    private static Contact contact;
    public static void main(String[] args) {
        contact = new Contact(args[0], args[1], Integer.parseInt(args[2]));
    }
}