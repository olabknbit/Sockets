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

public class JavaServer {

    private static int portNumber = 12345;
    public static final String multicastIP = "230.1.1.1";
    public static final int multicastPort = 12305;
    private static ServerSocket TCPSocket = null;
    private static DatagramSocket UDPSocket = null;
    private static Map<String, Socket> clients = new HashMap<>();
    private static int clientIds = 0;
    
    public static void sendMessageOverTCP(String id, String msg){
        try{
            for (Map.Entry<String, Socket> client : clients.entrySet()){
                if (!id.equals(client.getKey())){
                    PrintWriter out = new PrintWriter(client.getValue().getOutputStream(), true);
                    out.println(msg);
                }
            }
        } catch(Exception ex){
            Logger.getLogger(ThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void sendMessageOverUDP(String id, String msg){
        byte[] sendBuffer = msg.getBytes();
        try{
            for (Map.Entry<String, Socket> client : clients.entrySet()){
                Socket socket = client.getValue();
                if (!client.getKey().equals(id)){
                    
                    InetAddress address = InetAddress.getByName("localhost");
                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, socket.getPort());
                    UDPSocket.send(sendPacket);
                    
                }
            }
        } catch(Exception ex){
            Logger.getLogger(ThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args)throws IOException {
        System.out.println("JAVA SERVER");  
                               
        try {
            TCPSocket = new ServerSocket(portNumber);
            UDPSocket = new DatagramSocket(portNumber);
            
            ThreadUDP u = new ThreadUDP(UDPSocket, true);
            u.start();

            while(true){
                Socket clientSocket = TCPSocket.accept();
                String clientId = getNewClientId();
                clients.put(clientId, clientSocket);
                sendClientId(clientSocket, clientId);

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ThreadTCP c = new ThreadTCP(in, clientId, true);
                
                c.start();

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

    private static void sendClientId(Socket clientSocket, String clientId) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(clientId);
        } catch (IOException ex) {
            Logger.getLogger(JavaServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private static String getNewClientId() {
        return String.valueOf(clientIds++);
    }
    
}
