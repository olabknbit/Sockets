package javaserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import threads.ClientThreadTCP;
import threads.ClientThreadUDP;

public class JavaServer {

    private static int portNumber = 12345;
    private static ServerSocket TCPSocket = null;
    private static DatagramSocket UDPSocket = null;
    private static Map<Integer, Pair<Socket, InetAddress>> clients = new HashMap<>();
    private static int clientIds = 0;
    
    public static void sendMessageOverTCP(int id, String msg){
        Socket authorSocket = clients.get(id).getKey();
        try{
            for (Pair<Socket, InetAddress> pair : clients.values()){
                Socket clientSocket = pair.getKey();
                if (clientSocket != authorSocket){
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println(msg);
                }
            }
        } catch(Exception ex){
            Logger.getLogger(ClientThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void sendMessageOverUDP(InetAddress authorAddress, String msg){
        byte[] sendBuffer = msg.getBytes();
        try{
            for (Pair<Socket, InetAddress> pair : clients.values()){
                InetAddress address = pair.getValue();
                if (address != null && address != authorAddress){
                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
                    UDPSocket.send(sendPacket);
                }
            }
        } catch(Exception ex){
            Logger.getLogger(ClientThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public static void updateUDPClientAddress(int clientId, InetAddress address){
        Socket socket = clients.get(clientId).getKey();
        clients.put(clientId, new Pair<>(socket, address));
    }
    

    public static void main(String[] args)throws IOException {
        System.out.println("JAVA SERVER");  
                               
        try {
            TCPSocket = new ServerSocket(portNumber);
            UDPSocket = new DatagramSocket(portNumber);

            while(true){
                Socket clientSocket = TCPSocket.accept();
                int clientId = getNewClientId();
                clients.put(clientId, new Pair<>(clientSocket, null));
                sendClientId(clientSocket, clientId);

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ClientThreadTCP c = new ClientThreadTCP(in, clientId, true);
                ClientThreadUDP u = new ClientThreadUDP(UDPSocket, clientId);
                c.start();
                u.start();

            }
        } catch (IOException e) {            
            e.printStackTrace();
        } finally{
            if (TCPSocket != null){
                TCPSocket.close();
            }
            if (UDPSocket != null) {
                UDPSocket.close();
            }
        }
    }

    private static void sendClientId(Socket clientSocket, int clientId) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(String.valueOf(clientId));
        } catch (IOException ex) {
            Logger.getLogger(JavaServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private static int getNewClientId() {
        int id = clientIds;
        clientIds++;
        return id;
    }
    
}
