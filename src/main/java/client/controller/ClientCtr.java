/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller;

import java.net.Socket;
import java.util.ArrayList;

import client.network.ClientListening;
import client.view.ConnectFrm;
import client.view.LoginFrm;
import client.view.MainFrm;
import client.view.PlayFrm;
import client.view.ResultFrm;
import client.view.SetShipFrm;
import java.io.IOException;
import java.io.ObjectOutputStream;
import shared.model.IPAddress;
import shared.model.ObjectWrapper;

public class ClientCtr {

    private Socket mySocket;
    private ClientListening myListening;
//    private ArrayList<ObjectWrapper> myFunction;
    private volatile boolean isConnected = false;
    private IPAddress serverAddress = new IPAddress("localhost", 8888);

    private ObjectOutputStream oos;

    private String username;
    private boolean opponentReady = false;

    private ConnectFrm connectFrm;
    private LoginFrm loginFrm;
    private MainFrm mainFrm;
//    private SetShipFrm setShipFrm;
//    private PlayFrm playFrm;
//    private ResultFrm resultFrm;

    private GameCtr gameCtr;
    
    public ClientCtr(ConnectFrm connectFrm) throws IOException {
        this.connectFrm = connectFrm;
//        myFunction = new ArrayList<ObjectWrapper>();
//        loginFrm = new LoginFrm(this);
//        mainFrm = new MainFrm(this);
    }

    public ClientCtr(ConnectFrm connectFrm, IPAddress serverAddr) throws IOException {
        this.connectFrm = connectFrm;
        this.serverAddress = serverAddr;
//        myFunction = new ArrayList<ObjectWrapper>();
//        loginFrm = new LoginFrm(this);
//        mainFrm = new MainFrm(this);
    }

    public boolean openConnection() {
        try {
            mySocket = new Socket(serverAddress.getHost(), serverAddress.getPort());
            this.oos = new ObjectOutputStream(mySocket.getOutputStream());
            myListening = new ClientListening(this);
            myListening.start();
            isConnected = true;
            connectFrm.showMessage("Connected to the server at host: " + serverAddress.getHost() + ", port: " + serverAddress.getPort());
        } catch (Exception e) {
            connectFrm.showMessage("Error when connecting to the server!");
            return false;
        }
        return true;
    }

    public boolean sendData(Object obj) {
        try {
//            oos = new ObjectOutputStream(mySocket.getOutputStream());
            oos.writeObject(obj);
            oos.flush();
        } catch (Exception e) {
            connectFrm.showMessage("Error when sending data to the server!");
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
//            myFunction.clear();
            connectFrm.showMessage("Disconnected from the server!");
            return true;
        } catch (Exception e) {
            connectFrm.showMessage("Error when disconnecting from the server!");
            return false;
        }
    }

//    public ArrayList<ObjectWrapper> getActiveFunction() {
//        return myFunction;
//    }
    public ConnectFrm getConnectFrm() {
        return connectFrm;
    }

    public Socket getMySocket() {
        return mySocket;
    }

//    public int getPlayerId() {
//        return playerId;
//    }
//
//    public void setPlayerId(int playerId) {
//        this.playerId = playerId;
//    }
    public LoginFrm getLoginFrm() {
        return loginFrm;
    }

    public void setLoginFrm(LoginFrm loginFrm) {
        this.loginFrm = loginFrm;
    }

    public MainFrm getMainFrm() {
        return mainFrm;
    }

    public void setMainFrm(MainFrm mainFrm) {
        this.mainFrm = mainFrm;
    }

//    public SetShipFrm getSetShipFrm() {
//        return setShipFrm;
//    }
//
//    public PlayFrm getPlayFrm() {
//        return playFrm;
//    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

//    public void setSetShipFrm(SetShipFrm setShipFrm) {
//        this.setShipFrm = setShipFrm;
//    }
//
//    public void setPlayFrm(PlayFrm playFrm) {
//        this.playFrm = playFrm;
//    }

    public GameCtr getGameCtr() {
        return gameCtr;
    }

    public void setGameCtr(GameCtr gameCtr) {
        this.gameCtr = gameCtr;
    }

}
