package sbu.cs.Server;

import sbu.cs.Client.ClientHandler;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Server Class
public class Server {
    // TODO: Implement the server-side operations

    // TODO: Add constructor and necessary methods

    private static final int PORT = 3000;
    //private static ArrayList<Socket> clients = new ArrayList<>();
    public static ArrayList<Socket> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws IOException {
        // TODO: Implement the main method to start the server
        ServerSocket listener = new ServerSocket(3000);
        try {
            while (true) {
                Socket client = listener.accept();
                clients.add(client);
                ClientHandler clientThread = new ClientHandler(client);
                pool.execute(clientThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the server socket when the server is shutting down
            if (listener != null) {
                try {
                    listener.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Shutdown the thread pool to release resources
            pool.shutdown();
        }
    }
}