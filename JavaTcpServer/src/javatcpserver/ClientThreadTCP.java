/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javatcpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Olka
 */
public class ClientThread extends Thread {
        
        ServerSocket serverSocket;
        Socket clientSocket;
        static int id = 0;
        int sId;
        
        ClientThread(ServerSocket serverSocket, Socket clientSocket) {
            this.serverSocket = serverSocket;
            this.clientSocket = clientSocket;
            this.sId = id;
            id++;
        }

        public void run() {
            System.out.println("client connected");
            while(true){
                // in & out streams
                
                try {
                    
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // read msg, send response
                    String msg = in.readLine();
                    if (msg != null){
                        msg += " from " + String.valueOf(sId);
                        System.out.println("received msg: " + msg);
                        JavaTcpServer.sendMessage(clientSocket, msg);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
    }
