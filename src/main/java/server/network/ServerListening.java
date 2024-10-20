/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.network;

import java.net.Socket;
import java.net.SocketException;
import server.controller.ServerCtr;

public class ServerListening extends Thread {

    private ServerCtr serverCtr;
    private boolean isListening = true;

    public ServerListening(ServerCtr serverCtr) {
        this.serverCtr = serverCtr;
    }

    @Override
    public void run() {
        serverCtr.getView().showMessage("server is listening... ");
        try {
            while (isListening) {
                try {
                    Socket clientSocket = serverCtr.getMyServer().accept();
                    ServerProcessing sp = new ServerProcessing(clientSocket, serverCtr);
                    sp.start();
                    serverCtr.addServerProcessing(sp);
                } catch (SocketException se) {
                    if (!isListening) {
                        break;
                    } else {
                        se.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeServerSocket();
        }
    }

    public void stopListening() {
        isListening = false;
        closeServerSocket();
    }

    private void closeServerSocket() {
        if (serverCtr.getMyServer() != null && !serverCtr.getMyServer().isClosed()) {
            try {
                serverCtr.getMyServer().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
