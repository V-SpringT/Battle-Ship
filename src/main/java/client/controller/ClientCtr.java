/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller;

import java.net.Socket;
import java.util.ArrayList;

import client.network.ClientListening;
import client.view.ConnectFrm;
import java.io.ObjectOutputStream;
import shared.model.IPAddress;
import shared.model.ObjectWrapper;

public class ClientCtr {

    private Socket mySocket;
    private ConnectFrm view;
    private ClientListening myListening;
    private ArrayList<ObjectWrapper> myFunction;
    private volatile boolean isConnected = false;
    private int playerId;
    private IPAddress serverAddress = new IPAddress("localhost", 8888);

    public ClientCtr(ConnectFrm view) {
        this.view = view;
        myFunction = new ArrayList<ObjectWrapper>();
    }

    public ClientCtr(ConnectFrm view, IPAddress serverAddr) {
        this.view = view;
        this.serverAddress = serverAddr;
        myFunction = new ArrayList<ObjectWrapper>();
    }

    public boolean openConnection() {
        try {
            mySocket = new Socket(serverAddress.getHost(), serverAddress.getPort());
            myListening = new ClientListening(this);
            myListening.start();
            isConnected = true;
            view.showMessage("Connected to the server at host: " + serverAddress.getHost() + ", port: " + serverAddress.getPort());
        } catch (Exception e) {
            view.showMessage("Error when connecting to the server!");
            return false;
        }
        return true;
    }

    public boolean sendData(Object obj) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
            oos.writeObject(obj);
        } catch (Exception e) {
            view.showMessage("Error when sending data to the server!");
            return false;
        }
        return true;
    }

    public boolean closeConnection() {
        if (!isConnected) {
            return true;
        }
        try {
            isConnected = false;
            if (myListening != null) {
                myListening.stopListening();
            }
            if (mySocket != null) {
                mySocket.close();
            }
            myFunction.clear();
            view.showMessage("Disconnected from the server!");
            return true;
        } catch (Exception e) {
            view.showMessage("Error when disconnecting from the server!");
            return false;
        }
    }

    public ArrayList<ObjectWrapper> getActiveFunction() {
        return myFunction;
    }

    public ConnectFrm getView() {
        return view;
    }

    public Socket getMySocket() {
        return mySocket;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    
}
