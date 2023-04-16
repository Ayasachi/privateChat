package univ_lorraine.iut.java.privatechat.controller;



public class Utilisateur {

    private String username;
    private String ipaddress;
    private int port;

    public Utilisateur(String username, String ipaddress, int port) {
        this.username = username;
        this.ipaddress = ipaddress;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Utilisateur [username=" + username + ", ipaddress=" + ipaddress + ", port=" + port + "]";
    }
}