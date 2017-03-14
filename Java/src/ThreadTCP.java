import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadTCP extends Thread {
        
    BufferedReader in;
    String id;
    boolean isServer;

    public ThreadTCP(BufferedReader in, String id, boolean isServer) {
        this.in = in;
        this.id = id;
        this.isServer = isServer;
    }

    public void run() {
        System.out.println("client " + id + " connected");
        while(true){
            try {
                String msg = in.readLine();
                if (msg != null){
                    if (isServer) {
                        msg += " from " + id;
                        JavaServer.sendMessageOverTCP(id, msg);
                    }
                    System.out.println("received msg: " + msg);
                }
            } catch (IOException ex) {
                Logger.getLogger(ThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
