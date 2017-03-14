import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

public class JavaClient {
    private static DatagramSocket UDPSocket = null;
    private static int ServerPortNumber = 12345;
    private static Socket TCPSocket = null;
    private static InetAddress address = null;
    private static String clientId;
    
    public static void sendMessageOverTCP(String msg){
        try{
            PrintWriter out = new PrintWriter(TCPSocket.getOutputStream(), true);
            out.println(msg);
        } catch(Exception ex){
            Logger.getLogger(ThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void sendMessageOverUDP(String msg){
        byte[] sendBuffer = msg.getBytes();
            
        try{ 
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, ServerPortNumber);
            UDPSocket.send(sendPacket);
        } catch(Exception ex){
            Logger.getLogger(ThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void openMultiCast() {
        new Thread(() -> {
            try {
                MulticastSocket multicastSocket = new MulticastSocket(JavaServer.multicastPort);
                multicastSocket.joinGroup(InetAddress.getByName(JavaServer.multicastIP));
                while (!multicastSocket.isClosed()) {
                    byte[] buff = new byte[1024];
                    DatagramPacket datagramPacket = new DatagramPacket(buff, buff.length);
                    multicastSocket.receive(datagramPacket);
                    String msg = new String(buff);
                    
                    String[] parts = msg.split(":");
                    String option = parts[0];
                    
                    String id = parts[1];
                    if (!id.equals(clientId))
                        System.out.println("Multicast UDP " + msg);
                    
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
                
        System.out.println("JAVA CLIENT");
        String hostName = "localhost";
        
        try {
            TCPSocket = new Socket();
            TCPSocket.connect(new InetSocketAddress(ServerPortNumber));
            UDPSocket = new DatagramSocket(TCPSocket.getLocalPort());  
            address = InetAddress.getByName("localhost"); 
            
            // send TCP message
            PrintWriter out = new PrintWriter(TCPSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));  
            out.println("First ping Java Tcp");
                  
            clientId = in.readLine();
            System.out.println("received response from Server: " + clientId);
                         
            // send UDP 
            byte[] sendBuffer = new String("M:" + String.valueOf(clientId) + ":First ping Java Udp").getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, ServerPortNumber);
            UDPSocket.send(sendPacket);
            
            ThreadTCP c = new ThreadTCP(in, clientId, false);
            ThreadUDP u = new ThreadUDP(UDPSocket, false);
            c.start();
            u.start();
            
            openMultiCast();
            
            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter Option");
                String option = br.readLine();
                System.out.print("Enter Message:");
                String message = br.readLine();
                if (option.contentEquals("M") || option.contentEquals("N")){
                    sendMessageOverUDP(option + ":" + String.valueOf(clientId) + ":" + message);
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
