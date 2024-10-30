/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.controller;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import shared.model.IPAddress;
import shared.model.ObjectWrapper;
import server.network.ServerListening;
import server.network.ServerProcessing;
import server.view.ServerMainFrm;

public class ServerCtr {

    private ServerMainFrm view;
    private ServerSocket myServer;
    private ServerListening myListening;
    private ArrayList<ServerProcessing> myProcess;
    private ArrayList<ServerProcessing> myWaitingProcess;
//    private IPAddress myAddress = new IPAddress("localhost", 8888);
    private IPAddress myAddress = new IPAddress("26.87.126.183", 8888);

    public ServerCtr(ServerMainFrm view) {
        myProcess = new ArrayList<ServerProcessing>();
        myWaitingProcess = new ArrayList<ServerProcessing>();
        this.view = view;
        openServer();
    }

    public ServerCtr(ServerMainFrm view, int serverPort) {
        myProcess = new ArrayList<ServerProcessing>();
        myWaitingProcess = new ArrayList<ServerProcessing>();
        this.view = view;
        myAddress.setPort(serverPort);
        openServer();
    }

    private void openServer() {
        try {
            myServer = new ServerSocket(myAddress.getPort());
            myListening = new ServerListening(this);
            myListening.start();
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            view.showServerInfor(myAddress);
            System.out.println("server started!");
            view.showMessage("TCP server is running at the port " + myAddress.getPort() + "...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            for (ServerProcessing sp : myProcess) {
                sp.stopProcessing();
            }
            myListening.stopListening();
            myServer.close();
            view.showMessage("TCP server is stopped!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addServerProcessing(ServerProcessing sp) {
        myProcess.add(sp);
        view.showMessage("Number of client connecting to the server: " + myProcess.size());
        publicClientNumber();
    }
    
    public void addWaitingProcessing(ServerProcessing sp) {
        myWaitingProcess.add(sp);
//        sendWaitingList();
    }

    public void removeServerProcessing(ServerProcessing sp) {
        myProcess.remove(sp);
        view.showMessage("Number of client connecting to the server: " + myProcess.size());
        publicClientNumber();
    }

    public ServerMainFrm getView() {
        return view;
    }

    public ServerSocket getMyServer() {
        return myServer;
    }

    public void publicClientNumber() {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER, myProcess.size());
        for (ServerProcessing sp : myProcess) {
            sp.sendData(data);
        }
    }

    public void sendWaitingList() {
        String listUsername = "";
        for (ServerProcessing sp : myWaitingProcess) {
            listUsername += sp.getUsername() + "||";
            System.out.println(sp.getUsername());
        }
        
        System.out.println("Server send waiting list:");
        System.out.println(listUsername);
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.SERVER_INFORM_CLIENT_WAITING, listUsername);
        System.out.println(data);
        for (ServerProcessing sp : myProcess) 
            sp.sendData(data);
    }

    public ArrayList<ServerProcessing> getMyWaitingProcess() {
        return myWaitingProcess;
    }
}
