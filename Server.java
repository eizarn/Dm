import java.net.*; //Sockets
import java.util.ArrayList;
import java.io.*; //Streams
import Stuff.Player;

public class Server implements Runnable{
    public static final int PORT = 42069;

    ArrayList <DataInputStream> inputs = new ArrayList<>();
    ArrayList <DataOutputStream> outputs = new ArrayList<>();
    ArrayList <Player> players = new ArrayList<>();
    
    ServerSocket gestSock;

    int nbClients = 0;

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        System.out.println("Démarrage du serveur");
        try {
            // gestionnaire de socket
            gestSock = new ServerSocket(PORT);
            Thread th = new Thread(this);
            th.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Socket sock = gestSock.accept();
            System.out.println("Client connected.");
            nbClients++;

            DataInputStream input = new DataInputStream(sock.getInputStream());
            inputs.add(input);
            DataOutputStream output = new DataOutputStream(sock.getOutputStream());
            outputs.add(output);
            output.writeInt(nbClients);
            Player player = new Player(input.readUTF());
            players.add(player);

            System.out.println(String.format("And his name is %s! (ta ta da taaa)", player.name));

            Thread th = new Thread(this);
            th.start();
            System.out.println("nyaga"); // nice
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}