package main.Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private String username;
    private DataInputStream in;
    private DataOutputStream out;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.username = username;
        } catch (IOException e) {
            closeAll();
        }
    }

    public void sendMessage() {
        try {
            out.writeUTF(username);
            out.flush();

            Scanner scanner = new Scanner(System.in);
            System.out.println("> Choose an option:\n(1) Send message\n(2) Download file");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    out.writeUTF("sendmsg");
                    out.flush();

                    new Thread(this::listenForMessage).start();

                    while (socket.isConnected()) {
                        String msgToSend = scanner.nextLine();
                        out.writeUTF(username + ": " + msgToSend);
                        out.flush();
                    }
                    break;
                case 2:
                    downloadFile();
                    break;
                default:
                    System.out.println("> Invalid choice");
            }
        } catch (IOException e) {
            closeAll();
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String msgFromGP;
            while (socket.isConnected()) {
                try {
                    msgFromGP = in.readUTF();
                    System.out.println(msgFromGP);
                } catch (IOException e) {
                    closeAll();
                }
            }
        }).start();
    }

    public void downloadFile() {
        try {
            out.writeUTF("downfl");
            out.flush();

            String response = in.readUTF();
            System.out.println(response);

            Scanner scanner = new Scanner(System.in);
            System.out.println("> Enter the number of the file to download:");
            int fileNumber = scanner.nextInt();
            out.writeUTF(String.valueOf(fileNumber));

            long fileSize = in.readLong();
            FileOutputStream fileOutputStream = new FileOutputStream("downloaded_file.txt");
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesRead = 0;

            while (totalBytesRead < fileSize) {
                bytesRead = in.read(buffer);
                if (bytesRead == -1) break;
                fileOutputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            String endMessage = in.readUTF();
            if (endMessage.equals("done")) {
                System.out.println("File downloaded successfully.");
            }

            fileOutputStream.close();
        } catch (IOException e) {
            closeAll();
        }
    }

    public void closeAll() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("> Enter your username: ");
        String username = scanner.nextLine();
        Socket socket1 = new Socket("localhost", 1234);
        Client client = new Client(socket1, username);
        client.sendMessage();
    }
}
