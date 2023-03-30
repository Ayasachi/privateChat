package univ_lorraine.iut.java.privatechat.controller;

import java.io.*;
import java.net.*;

public class Client {
    InetAddress host = InetAddress.getLocalHost();
    Socket socket = null;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;
public Client() throws UnknownHostException, IOException {
        socket = new Socket(host.getHostName(), 12345);
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Envoie d'une requête au serveur");
        if (socket != null && oos != null) {
            try {
                oos.writeObject("Hello Server");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
