package univ_lorraine.iut.java.privatechat.controller;

import javafx.collections.ObservableList;
import java.io.Serializable;

public class Conversation implements Serializable {

    private ObservableList<Message> messages;
    private Contact contact;

    public Conversation(ObservableList<Message> messages, Contact contact) {
        this.messages = messages;
        this.contact = contact;
    }


    public ObservableList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

}
