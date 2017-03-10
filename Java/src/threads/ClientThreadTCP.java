package threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaserver.JavaServer;

public class ClientThreadTCP extends Thread {
        
    BufferedReader in;
    int id;
    boolean isServer;

    public ClientThreadTCP(BufferedReader in, int id, boolean isServer) {
        this.in = in;
        this.id = id;
        this.isServer = isServer;
    }

    public void run() {
        System.out.println("client " + String.valueOf(id) + " connected through TCP");
        while(true){
            try {
                String msg = in.readLine();
                if (msg != null){
                    
                    if (isServer) {
                        msg += " from " + String.valueOf(id);
                        JavaServer.sendMessageOverTCP(id, msg);
                    }
                    System.out.println("received msg: " + msg);
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
