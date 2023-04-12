package univ_lorraine.iut.java.privatechat.controller;

import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.math.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Serveur {
    private static int port = 12345;
    private static Boolean running = true;

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        ServerSocket serverSocket = new ServerSocket(port);

        List<Thread> threadList = new ArrayList<>();
        while (running) {
            System.out.println("En attente du client");
            Socket socket = serverSocket.accept();
            Thread thread = new Thread(new ClientCommunication(socket));
            threadList.add(thread);
            thread.start();
        }
        serverSocket.close();
        System.out.println("Fermeture de la socket serveur");

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        serverSocket.close();
    }

    private static class ClientCommunication implements Runnable {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClientCommunication(Socket socket) throws IOException {
            this.socket = socket;
            //this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
                try {
                    // 3. Génération de la paire de clés Diffie-Hellman
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
                    kpg.initialize(1024);
                    KeyPair kp = kpg.generateKeyPair();
                    Key publicKey = kp.getPublic();
                    Key privateKey = kp.getPrivate();
                    // 5. Réception de la clé publique du client
                    Key clientPublicKey = (Key) in.readObject();
                    // 4. Envoi de la clé publique du serveur au client
                    this.out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(publicKey);
                    out.flush();



                    // 6. Génération de la clé secrète partagée
                    KeyAgreement ka = KeyAgreement.getInstance("DH");
                    ka.init(privateKey);
                    ka.doPhase(clientPublicKey, true);
                    byte[] secret = ka.generateSecret();

                    // 7. Création d'un objet Cipher pour déchiffrer les messages avec la clé secrète partagée
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    SecretKeySpec secretKeySpec = new SecretKeySpec(secret, 0, 16, "AES");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));

                    // 8. Réception des messages chiffrés et déchiffrement avec la clé secrète partagée
                    byte[] encrypted = (byte[])in.readObject();
                    byte[] iv = new byte[16];

                    if(Base64.getEncoder().withoutPadding().encodeToString(encrypted).equals(new String(encrypted))) {
                        // Décodage du message en Base64
                        byte[] decoded = Base64.getDecoder().decode(encrypted);

                        // Déchiffrement du message avec la clé secrète partagée
                        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
                        byte[] decrypted = cipher.doFinal(decoded);
                        String decryptedMessage = new String(decrypted);
                        System.out.println("Message reçu: " + decryptedMessage);

                        // 9. Chiffrement de la réponse avec la clé secrète partagée et envoi au client
                        String responseMessage = "Serveur: J'ai bien reçu votre message!";
                        byte[] responseBytes = cipher.doFinal(responseMessage.getBytes());
                        String response = Base64.getEncoder().encodeToString(responseBytes);
                        out.writeObject(response);
                        out.flush();
                    }
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

