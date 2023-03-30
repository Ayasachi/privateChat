package univ_lorraine.iut.java.privatechat.controller;

import java.io.*;
import java.net.*;

public class Client {
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public Client(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 12345);
        try {
            client.connect();
            client.sendMessage("Hello server!");
            Thread.sleep(3000);
            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() throws UnknownHostException, IOException {
        InetAddress host = InetAddress.getLocalHost();
        try {
            this.socket = new Socket(host.getHostName(), 12345);
            System.out.println("Connexion établie avec le serveur.");
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Impossible de trouver l'hôte " + host + ".");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Erreur d'entrée/sortie lors de la connexion au serveur " + host + ":" + 12345 + ".");
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException {
        if (socket != null && oos != null) {
            oos.writeObject(message);
            oos.flush();
            System.out.println("Message envoyé au serveur : " + message);
        }
    }

    public void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
        }
        if (ois != null) {
            ois.close();
        }
        if (oos != null) {
            oos.close();
        }
    }
}
