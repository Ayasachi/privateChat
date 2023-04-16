package univ_lorraine.iut.java.privatechat.controller;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import univ_lorraine.iut.java.privatechat.App;
import univ_lorraine.iut.java.privatechat.model.*;

public class ChatController {

    @FXML private ListView<String> conversationListView;
    private ObservableList<Conversation> conversationList = FXCollections.observableArrayList();
    private String userLogin;
    @FXML private Button btnAddContact;
    @FXML private TextField inputField;
    @FXML private Button sendButton;
    private Thread threadClient;
    private Thread threadServeur;
    private BlockingQueue<Message> listeMessages;
    private ArrayList<Utilisateur> contactList = new ArrayList<>();

    public void initialize() {
        userLogin = App.getUser();
        listeMessages = new LinkedBlockingQueue<Message>();
        String[] args = new String[0];
        loadContacts();
        for(Utilisateur user : contactList) {
            conversationListView.getItems().add(user.toString());
        }


        /*File convDir = new File(userLogin);
        if(convDir.exists() && convDir.isDirectory()) {
            File conv[] = convDir.listFiles();
            loadConversations(conv, 0, 0);
        }*/

        // Lancement du SERVEUR
        System.out.println("Lancement du serveur");
        threadServeur = new Thread(new Serveur());
        threadServeur.start();

        // Lancement du CLIENT
        /*System.out.println("Lancement du client");
        threadClient = new Thread(new Client(listeMessages));
        threadClient.sleep(10000);
        threadClient.start();*/

        conversationListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setOnMouseClicked(event -> {
                        System.out.println("L'élément " + item + " a été cliqué.");
                        // Ajoutez ici le code que vous souhaitez exécuter lorsqu'un élément est cliqué
                        try {
                            if(threadClient != null) {
                                threadClient.interrupt();
                            }
                            System.out.println("Lancement du client");
                            threadClient = new Thread(new Client(listeMessages, item, userLogin));
                            Thread.sleep(100);
                            threadClient.start();
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    });
                }
            }
        });

        // App.setWindowTitle("SaferChat (Utilisateur : " + userLogin + ")");
        // conversationListView.setItems(conversationList);
        // conversationListView.setCellFactory(param -> new ConversationListCell());

    }

    public void addConversation(Conversation conversation) {
        conversationList.add(conversation);
    }

    @FXML
    private void sendMessage() throws IOException {
        String message = inputField.getText();
        inputField.clear();

        Message messageToSend = new Message();

        // Envoi du message au serveur
        listeMessages.add(messageToSend);
    }

    @FXML
    private void logout() throws IOException {
        if(threadClient != null) {
            listeMessages.add(new Message());
            threadClient.interrupt();
        }
        threadServeur.interrupt();
        App.setRoot("login");
    }

    @FXML
    public void addContact() throws IOException {
        App.setRoot("addContact");
    }

    public void loadContacts() {
        File contactDir = new File("data/" + userLogin);
        if(contactDir.exists() && contactDir.isDirectory()) {
            File[] contact = contactDir.listFiles();
            for(int i = 0; i < contact.length; i++) {
                if(contact[i].isFile()) {
                    try {
                        Scanner sc = new Scanner(contact[i]);
                        String[] split;
                        String login = sc.nextLine();
                        split = login.split("=");
                        login = split[1];
                        String ip = sc.nextLine();
                        split = ip.split("=");
                        ip = split[1];
                        String port = sc.nextLine();
                        split = port.split("=");
                        port = split[1];
                        contactList.add(new Utilisateur(login, ip, Integer.parseInt(port)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else { System.out.println("aa"); }
    }
}