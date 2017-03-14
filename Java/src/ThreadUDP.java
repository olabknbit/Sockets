import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadUDP extends Thread {
        
    DatagramSocket UDPSocket;
    boolean isServer;

    public ThreadUDP(DatagramSocket UDPsocket, boolean isServer) {
        this.UDPSocket = UDPsocket;
        this.isServer = isServer;
    }

    public void run() {
        
        byte[] receiveBuffer;
        while(true){
            try {
                receiveBuffer = new byte[1024];
             
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                UDPSocket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                if (msg != null){
                    
                    String[] parts = msg.split(":");
                    String option = parts[0];
                    String id = parts[1];
                    System.out.println("received msg: " + msg);
                    if (isServer){
                        if (option.equals("M")){
                            JavaServer.sendMessageOverUDP(id, msg);
                        } else if (option.equals("N")){
                           UDPSocket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(JavaServer.multicastIP), JavaServer.multicastPort));
                        }
                    }  
                }
            } catch (IOException ex) {
                Logger.getLogger(ThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
