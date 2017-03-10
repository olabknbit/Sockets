package javatcpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaTcpServer {

    private static int portNumber = 12345;
    private static ServerSocket serverSocket = null;
    private static List<Socket> clients = new ArrayList<>();
    
    public static void sendMessage(Socket authorSocket, String msg){
        try{
            for (Socket clientSocket : clients){
                if (clientSocket != authorSocket){
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println(msg);
                }
            }
        } catch(Exception ex){
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) throws IOException {
        
        System.out.println("JAVA TCP SERVER");  
        
        try {
            // create socket
            serverSocket = new ServerSocket(portNumber);
            
            while(true){
                
                // accept client
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                ClientThread c = new ClientThread(serverSocket, clientSocket);
                c.start();
                
                
                
            
       
            }
        } catch (IOException e) {            
            e.printStackTrace();
        }
        finally{
            if (serverSocket != null){
                serverSocket.close();
            }
        }
    }
    
}
