package sbu.cs.Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Client Class
public class Client {
    // TODO: Implement the client-side operations

    // TODO: Add constructor and necessary methods
    private static final String SERVER_IP   = "127.0.0.1";
    private static final int    SERVER_PORT = 3000;

    public static void main(String[] args) throws IOException {
        // TODO: Implement the main method to start the client
        Socket           client = new Socket(SERVER_IP, SERVER_PORT);
        Boolean IsInChat = false;
        DataOutputStream out    = new DataOutputStream(client.getOutputStream());
        //DataInputStream in = new DataInputStream(.getInputStream());
        System.out.println("Name:");
        Scanner scanner = new Scanner(System.in);
        String  name    = scanner.nextLine();
        out.writeUTF(name);
        HandleServerResponse Handle = new HandleServerResponse(client);
        Thread Handlethread = new Thread(Handle);
        Handlethread.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        while (true) {
            userInput = reader.readLine();
            out.writeUTF(userInput);
        }
    }
}