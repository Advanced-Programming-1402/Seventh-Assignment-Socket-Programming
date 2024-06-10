package main.Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private static final List<String> allMessages = new ArrayList<>();
    private Socket socket;
    private String clientUsername;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.clientUsername = in.readUTF();
            clientHandlers.add(this);

            // Send all previous messages to the new client
            for (String message : allMessages) {
                out.writeUTF(message);
                out.flush();
            }

            broadcastMsg(" > Server: " + clientUsername + " has joined the chat!");
        } catch (IOException e) {
            closeAll();
        }
    }

    @Override
    public void run() {
        try {
            String choice = in.readUTF();
            switch (choice) {
                case "sendmsg" -> messageMenu();
                case "downfl" -> downloadMenu();
                default -> System.out.println("> Invalid choice.");
            }
        } catch (IOException e) {
            closeAll();
        }
    }

    // message handler:
    public void messageMenu() {
        String msgFromClient;
        while (socket.isConnected()) {
            try {
                msgFromClient = in.readUTF();
                synchronized (allMessages) {
                    allMessages.add(msgFromClient);
                }
                broadcastMsg(msgFromClient);
            } catch (IOException e) {
                closeAll();
                break;
            }
        }
    }

    public void broadcastMsg(String msg) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.out.writeUTF(msg);
                    clientHandler.out.flush();
                }
            } catch (IOException e) {
                closeAll();
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMsg(" > Server: " + clientUsername + " has left the chat!");
    }

    public void closeAll() {
        removeClientHandler();
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendFile(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            long fileSize = file.length();
            out.writeLong(fileSize);
            out.flush();

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            out.writeUTF("done");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadMenu() {
        File folder = new File("C:\\CS SBU AP\\Seventh-Assignment-Socket-Programming\\seventh_assignment\\src\\main\\resources\\data");
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        try {
            StringBuilder fileList = new StringBuilder();
            for (int i = 0; i < listOfFiles.length; i++) {
                fileList.append((i + 1)).append(": ").append(listOfFiles[i].getName()).append("\n");
            }

            out.writeUTF(fileList.toString());
            out.flush();

            int fileChoice = Integer.parseInt(in.readUTF()) - 1;
            if (fileChoice >= 0 && fileChoice < listOfFiles.length) {
                sendFile(listOfFiles[fileChoice]);
            } else {
                out.writeUTF("Invalid file choice");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
