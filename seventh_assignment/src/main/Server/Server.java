package main.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ServerSocket server;
    private ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public Server(ServerSocket server) {
        this.server = server;
    }

    public void startServer() {
        try {
            while (!server.isClosed()) {
                Socket socket = server.accept();
                System.out.println(" > A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                threadPool.execute(thread);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            stopServer();
            threadPool.shutdown();
        }
    }

    public void stopServer() {
        try {
            if (server != null)
                server.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server1 = new Server(serverSocket);
        System.out.println(" > Server started. Waiting for client connections...");
        server1.startServer();
    }
}
