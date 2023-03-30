package univ_lorraine.iut.java.privatechat.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class Serveur {
    public static void main(String[] args) {
        int port = 12345;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Serveur en attente de connexion sur le port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Bloque jusqu'à ce qu'un client se connecte
                System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress().getHostAddress());

                // Traitez la connexion ici
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                String message = (String) ois.readObject();
                System.out.println("Message reçu : " + message);
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                oos.writeObject("Pong");
                oos.flush();
                //ois.close();
                //oos.close();
                //clientSocket.close();

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la communication avec le client : " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur lors de la lecture de l'objet : " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture du socket serveur : " + e.getMessage());
            }
            System.out.println("Arrêt du serveur");
        }
    }
}