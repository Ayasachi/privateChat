package univ_lorraine.iut.java.privatechat.controller;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Client {
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private SecretKey secretKey;

    public Client(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }

    /*public static void main(String[] args) {
        Client client = new Client("localhost", 12345);
        try {
            client.connect();
            client.sendMessage("Hello server!");
            client.listenMessage();
            client.disconnect();
        } catch (IOException e) {
            System.out.println("Erreur lors de la connexion au serveur."+e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }*/

    public static void main(String[] args) {
        Client client = new Client("localhost", 12345);
        try {
            client.connect();
            client.sendMessage("Hello server!");
            client.listenMessage();
            client.disconnect();
        } catch (IOException e) {
            System.err.println("Erreur lors de la communication avec le client : " + e.getMessage());

        } catch (ClassNotFoundException e) {
            System.err.println("Erreur lors de la lecture de l'objet : " + e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Erreur lors de la génération de la paire de clés" + e);
            e.printStackTrace();

        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (client.socket != null) {
                    client.socket.close();
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture du socket serveur : " + e.getMessage());
            }
            System.out.println("Arrêt du serveur");
        }
    }

    public void connect() throws UnknownHostException, IOException {
        InetAddress host = InetAddress.getLocalHost();
        try {
            this.socket = new Socket(host.getHostName(), serverPort);
            System.out.println("Connexion établie avec le serveur.");

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(1024);
            KeyPair kp = kpg.generateKeyPair();

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(kp.getPublic());
            oos.flush();
            System.out.println("clé publique envoyée au serveur");

            ois = new ObjectInputStream(socket.getInputStream());
            PublicKey serverPublicKey = (PublicKey) ois.readObject();
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(kp.getPrivate());
            ka.doPhase(serverPublicKey, true);
            byte[] secret = ka.generateSecret();


        } catch (UnknownHostException e) {
            System.out.println("Impossible de trouver l'hôte " + host + ".");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Erreur d'entrée/sortie lors de la connexion au serveur " + host + ":" + 12345 + ".");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) throws IOException {
        if (socket != null && oos != null) {
            oos.writeObject(message);
            oos.flush();
            System.out.println("Message envoyé au serveur : " + message);
        }
    }

    public void listenMessage() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        if(socket != null){
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            oos.writeObject(publicKey);
            oos.flush();

            byte[] encryptedKey = (byte[]) ois.readObject();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedKey = cipher.doFinal(encryptedKey);
            secretKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");

            byte[] encryptedMessage = (byte[]) ois.readObject();

            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(encryptedMessage, 0, cipher.getBlockSize());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] decryptedMessage = cipher.doFinal(encryptedMessage, cipher.getBlockSize(), encryptedMessage.length - cipher.getBlockSize());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            /*while (true) {
                String message = (String) ois.readObject();
                byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(message));
                String decryptedString = new String(decryptedMessage);
                System.out.println("Server: " + decryptedString);
            }*/
            /*String message = new String(decryptedMessage);
            System.out.println("Message recu par le client : " + message);*/
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
