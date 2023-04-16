package univ_lorraine.iut.java.privatechat.controller;
import javafx.scene.control.ListCell;

import java.io.IOException;


public class ConversationListCell extends ListCell<String> {
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
                });
            }
        }
}