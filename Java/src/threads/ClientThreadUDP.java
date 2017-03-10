package threads;

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
import javaserver.JavaServer;

public class ClientThreadUDP extends Thread {
        
    DatagramSocket UDPsocket;
    int id;
    InetAddress ad = null;

    public ClientThreadUDP(DatagramSocket UDPsocket, int id) {
        this.UDPsocket = UDPsocket;
        this.id = id;
    }

    public void run() {
        System.out.println("client " + String.valueOf(id) + " connected through UDP");
        
        byte[] receiveBuffer;
        while(true){
            try {
                receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                UDPsocket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                if (msg != null){
                    msg += " from " + String.valueOf(id);
                    System.out.println("received msg: " + msg);
                    
                    if (ad == null){
                        ad = receivePacket.getAddress();
                        JavaServer.updateUDPClientAddress(id, ad);
                    }
                    JavaServer.sendMessageOverUDP(ad, msg);
                    
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
