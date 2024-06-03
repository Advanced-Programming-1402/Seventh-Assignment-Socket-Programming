package sbu.cs.Client;

import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class HandleServerResponse implements Runnable{
    private DataInputStream  in;
    private DataOutputStream out;
    private Socket           client;

    public HandleServerResponse(Socket client) throws IOException {
        this.client = client;
        this.in = new DataInputStream(client.getInputStream());
        this.out = new DataOutputStream(client.getOutputStream());
    }

    @Override
    public void run() {
        for (int i=0; i<4; i++) {
            try {
                System.out.println(this.in.readUTF());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            while (true) {
                String Activity = this.in.readUTF();
                if (Activity.equals("inChat")) {
                    out.writeUTF("is in chat");
                    while (true) {
                        String msg = this.in.readUTF();
                        if (msg.equals("E")) {
//                            Console console = System.console();
//                            if (console != null) {
//                                console.clear();
//                            }
                            for (int i=0; i<50; i++) {
                                System.out.println(" ");
                            }
                            for (int i=0; i<3; i++) {
                                System.out.println(this.in.readUTF());
                            }
                            break;
                        } else {
                            System.out.println(msg);
                        }
                    }
                    continue;
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
