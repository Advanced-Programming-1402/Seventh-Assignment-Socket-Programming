package main.Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private ArrayList<String> allMesagges;
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
                case "sendmsg" ->  messageMenu();
                case "downfl" -> downloadMenu();
                default ->  System.out.println("> Invalid choice.");
            }
        }catch (IOException e) {
            closeAll();
        }
    }
    // message handler:
    public void messageMenu(){
        // write previous messages in chat
        for (String msg : allMesagges){
            try {
                out.writeUTF(msg);
                out.flush();
            }catch (IOException e){
                closeAll();
            }
        }
        // write message
        String msgFromClient;
        while (socket.isConnected()){
            try {
                msgFromClient = in.readUTF();
                broadcastMsg(msgFromClient);
            }catch (IOException e){
                closeAll();
                break;
            }
        }
    }

    public void broadcastMsg(String msg){
        for (ClientHandler clientHandler: clientHandlers){
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.out.writeUTF(msg);
                    clientHandler.out.flush();
                }
            } catch (IOException e) {
                closeAll();
            }
        }
    }

    public  void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMsg(" > Server: " + clientUsername + " has left the chat!");
    }

    public void closeAll() {
        removeClientHandler();
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    // download handler:
    public void downloadMenu() {
        File[] files = new File("data").listFiles();
        if (files == null) {
            System.out.println("no files found");
            return;
        }

        try {
            out.writeInt(files.length);
        } catch (IOException e) {
            closeAll();
        }
        int i = 1;
        for (File file : files) {
            try {
                out.writeUTF(i + "_ " + file.getName());
                out.flush();
                i++;
            } catch (IOException e) {
                closeAll();
            }
        }

        int index;
        while (socket.isConnected()) {
            try {
                index = in.readInt();
                downloadFile(index);
            } catch (IOException e) {
                closeAll();
                break;
            }
        }
    }

    public void downloadFile(int fileIndex) throws IOException {
        File[] files = new File("data").listFiles();
        assert files != null;

        int bytes;
        File file = files[fileIndex - 1];
        FileInputStream fileInputStream = new FileInputStream(file);

        out.writeUTF(file.getName());
        out.writeInt((int) file.length());
        out.flush();

        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInputStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);
            out.flush();
        }

        fileInputStream.close();
    }

}
