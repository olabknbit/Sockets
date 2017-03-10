package javaclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import threads.ClientThreadTCP;

public class JavaClient {
    private static DatagramSocket UDPSocket = null;
    private static int portNumber = 12345;
    private static Socket TCPSocket = null;
    private static InetAddress address = null;
    
    public static void sendMessageOverTCP(String msg){
        try{
            PrintWriter out = new PrintWriter(TCPSocket.getOutputStream(), true);
            out.println(msg);
        } catch(Exception ex){
            Logger.getLogger(ClientThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void sendMessageOverUDP(String msg){
        byte[] sendBuffer = msg.getBytes();
        System.out.println("sending " + msg);
            
        try{ 
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
            UDPSocket.send(sendPacket);
        } catch(Exception ex){
            Logger.getLogger(ClientThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws IOException {
                
        System.out.println("JAVA CLIENT");
        String hostName = "localhost";
        
        try {
            TCPSocket = new Socket(hostName, portNumber);
            UDPSocket = new DatagramSocket();
            
            address = InetAddress.getByName("localhost");            
            
            
            // send TCP message
            PrintWriter out = new PrintWriter(TCPSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));  
            out.println("First ping Java Tcp");
            
            // send UDP 
            byte[] sendBuffer = "First ping Java Udp".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
            UDPSocket.send(sendPacket);
            
                  
            String stringId = in.readLine();
            System.out.println("received response from Server: " + stringId);
                
            int clientId = Integer.valueOf(stringId);
            
            ClientThreadTCP c = new ClientThreadTCP(in, clientId, false);
            c.start();
            
            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter Option");
                String option = br.readLine();
                System.out.print("Enter Message:");
                String message = br.readLine();
                if (option.contentEquals("N")){
                    sendMessageOverUDP(message);
                } else {
                    sendMessageOverTCP(message);
                }
                
            }
          
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (TCPSocket != null){
                TCPSocket.close();
            }
        }
    }
    
    
    
}
