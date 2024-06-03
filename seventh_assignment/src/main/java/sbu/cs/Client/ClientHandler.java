package sbu.cs.Client;

import sbu.cs.Server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket client;
    private static ArrayList<Socket> clients;
    String name;
    static ArrayList<String> chat = new ArrayList<>();
    private static DataOutputStream out;
    private DataInputStream in;
    int n;
    public ClientHandler(Socket client) throws IOException {
        this.client = client;
        this.clients = Server.clients;
        this.in = new DataInputStream(client.getInputStream());
        this.out = new DataOutputStream(client.getOutputStream());
    }
//    File directory = File.mkdirs();

    File a_man_without_love = new File("src//main//java//sbu.cs//Server//data//a-man-without-love-ngelbert-Hmperdinck");
    File all_of_me = new File("src//main//java//sbu.cs//Server//data//all-of-me-john-legend");
    File birds_imagine = new File("src//main//java//sbu.cs//Server//data//birds-imagine-dragons");
    File blinding_lights = new File("src//main//java//sbu.cs//Server//data//blinding-lights-the-weekend");
    File dont_matter_to_me = new File("src//main//java//sbu.cs//Server//data//dont-matter-to-me-drake");
    File feeling_in_my_body = new File("src//main//java//sbu.cs//Server//data//feeling-in-my-body-elvis");
    File out_of_time = new File("src//main//java//sbu.cs//Server//data//out-of-time-the-weekend");
    File something_in_the_way = new File("src//main//java//sbu.cs//Server//data//something-in-the-way-nirvana");
    File why_you_wanna_trip_on_me = new File("src//main//java//sbu.cs//Server//data//why-you-wanna-trip-on-me-michael-jackson");
    File you_put_a_spell_on_me = new File("src//main//java//sbu.cs//Server//data//you-put-a-spell-on-me-austin-giorgio");
    @Override
    public void run() {
        try {
            name = in.readUTF();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(name + "connected");
        try {
            //menu();
            this.out.writeUTF("1-Join the chat");
            this.out.writeUTF("2-Downloads");
            this.out.writeUTF("3-Download new file");
            this.out.writeUTF("Enter (E) whenever you want to return to the menu");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        String request;
        try {
            while (true) {
                request = this.in.readUTF();
                if (request != null) {
                    if (request.equals("1")) {
                        if (chat.size() <= 20) {
                            n = 0;
                        } else {
                            n = (chat.size())-20;
                        }
                        out.writeUTF("inChat");
                        Chat();
                    } else if (request.equals("2")) {

                    } else if (request.equals("3")) {

                    }
                }
            }
        } catch (IOException e) {
            // Handle any I/O exceptions that occur during communication with the client
            System.err.println("IO Exception in client handler!!!!!!");
            e.printStackTrace();
        }
    }
    public void menu() throws IOException {
        out.writeUTF("1-Join the chat");
        out.writeUTF("2-Downloads");
        out.writeUTF("3-Download new file");
        String request;
        try {
            while (true) {
                request = this.in.readUTF();
                if (request != null) {
                    if (request.equals("1")) {
                        Chat();
                        break;
                    } else if (request.equals("2")) {

                    } else if (request.equals("3")) {

                    }
                }
            }
        } catch (IOException e) {
            // Handle any I/O exceptions that occur during communication with the client
            System.err.println("IO Exception in client handler!!!!!!");
            e.printStackTrace();
        }
    }
    public void Chat() throws IOException {
        System.out.println(name + this.in.readUTF());
        out.writeUTF(" ");
        out.writeUTF("Chat: ");
        out.writeUTF(" ");
        if (chat.size() != 0) {
            for (int i = n; i < chat.size(); i++) {
                out.writeUTF(chat.get(i));
            }
        }
        String msg;
        while (true) {
            msg = this.in.readUTF();
            if (msg != null) {
                if (msg.equals("E")) {
                    out.writeUTF("E");
                    menu();
                    break;
                } else {
                    String msgg = name + ": " + msg;
                    chat.add(msgg);
                    SendToAll(msgg);
                }
            }
        }
    }
    private void SendToAll(String msgg) throws IOException {
        for (Socket aclient : clients) {
            DataOutputStream outs = new DataOutputStream(aclient.getOutputStream());
            outs.writeUTF(msgg);
        }
    }
}