package univ_lorraine.iut.java.privatechat.controller;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*public class Serveur {
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
                ois.close();
                oos.close();
                clientSocket.close();

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

    /*public static void main(String[] args) {
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
                ois.close();
                oos.close();
                clientSocket.close();

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
}*/

public class Serveur {
    private static final int PORT = 12345;
    private static Socket socket;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private static final String ALGO_DH = "DH";
    private static final String ALGO_SYM = "AES";
    private static final String ALGO_TRANS = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur en attente de connexion sur le port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Bloque jusqu'à ce qu'un client se connecte
                System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress().getHostAddress());

                // Étape 1 : Génération de la paire de clés du serveur
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGO_DH);
                keyPairGenerator.initialize(1024);
                KeyPair serverKeyPair = keyPairGenerator.generateKeyPair();

                // Étape 2 : Envoi de la clé publique du serveur au client
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                PublicKey serverPublicKey = serverKeyPair.getPublic();
                oos.writeObject(serverPublicKey);
                oos.flush();

                // Étape 3 : Réception de la clé publique du client et génération de la clé secrète
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                PublicKey clientPublicKey = (PublicKey) ois.readObject();
                KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGO_DH);
                keyAgreement.init(serverKeyPair.getPrivate());
                keyAgreement.doPhase(clientPublicKey, true);
                byte[] secret = keyAgreement.generateSecret();

                // Étape 4 : Génération des clés de chiffrement et de déchiffrement
                MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                byte[] key = sha256.digest(secret);
                SecretKeySpec secretKey = new SecretKeySpec(key, ALGO_SYM);
                byte[] ivBytes = new byte[16];
                new Random().nextBytes(ivBytes);
                IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

                // Étape 5 : Envoi de l'IV au client
                oos.writeObject(ivBytes);
                oos.flush();

                // Étape 6 : Communication sécurisée avec le client
                Cipher cipher = Cipher.getInstance(ALGO_TRANS);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
                String message = (String) ois.readObject();
                System.out.println("Message reçu du client : " + message);

                // Réponse du serveur
                String response = "Pong";
                byte[] responseMessageBytes = response.getBytes();
                byte[] encryptedResponseMessageBytes = cipher.doFinal(responseMessageBytes);
                String encryptedResponseMessage = Base64.getEncoder().encodeToString(encryptedResponseMessageBytes);
                //cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
                sendMessage(oos, response, cipher);

                // Fermeture des flux et de la connexion
                ois.close();
                oos.close();
                clientSocket.close();

            }} catch (IOException e) {
                System.err.println("Erreur lors de la communication avec le client : " + e.getMessage());
                } catch (ClassNotFoundException e) {
                System.err.println("Erreur lors de la lecture de l'objet : " + e.getMessage());
                } catch (NoSuchAlgorithmException e) {
                    System.err.println("Algorithme de chiffrement non disponible : " + e.getMessage());
                } catch (NoSuchPaddingException e) {
                    System.err.println("Rembourrage de bloc non disponible : " + e.getMessage());
                } catch (InvalidKeyException e) {
                    System.err.println("Clé de chiffrement invalide : " + e.getMessage());
                } catch (InvalidAlgorithmParameterException e) {
                    System.err.println("Paramètre de chiffrement invalide : " + e.getMessage());
                } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Erreur lors de la fermeture du socket serveur : " + e.getMessage());

                }
        }
    }

    private static void sendMessage(ObjectOutputStream oos, String encryptedResponseMessage, Cipher cipher) throws IOException, IllegalBlockSizeException, BadPaddingException {
        if (oos != null) {
            byte[] encryptedBytes = cipher.doFinal(encryptedResponseMessage.getBytes());
            oos.writeInt(encryptedBytes.length);
            oos.write(encryptedBytes);
            oos.flush();
            System.out.println("Message envoyé au client : " + encryptedResponseMessage);
        }
    }
}