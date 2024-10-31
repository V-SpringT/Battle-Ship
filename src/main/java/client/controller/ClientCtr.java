package client.controller;

import java.net.Socket;

import client.network.ClientListening;

import client.view.ConnectFrm;
import client.view.HistoryFrm;
import client.view.LoginFrm;
import client.view.MainFrm;
import client.view.RankingFrm;

import shared.dto.IPAddress;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ClientCtr {

    private Socket mySocket;
    private ClientListening myListening;
    private volatile boolean isConnected = false;
    private IPAddress serverAddress = new IPAddress("localhost", 8888);

    private ObjectOutputStream oos;

    private String username;

    private ConnectFrm connectFrm;
    private LoginFrm loginFrm;
    private MainFrm mainFrm;
    private HistoryFrm historyFrm;
    private RankingFrm rankingFrm;

    private GameCtr gameCtr;

    public ClientCtr(ConnectFrm connectFrm) throws IOException {
        this.connectFrm = connectFrm;
    }

    public ClientCtr(ConnectFrm connectFrm, IPAddress serverAddr) throws IOException {
        this.connectFrm = connectFrm;
        this.serverAddress = serverAddr;
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
            connectFrm.showMessage("Disconnected from the server!");
            return true;
        } catch (Exception e) {
            connectFrm.showMessage("Error when disconnecting from the server!");
            return false;
        }
    }

    public ConnectFrm getConnectFrm() {
        return connectFrm;
    }

    public Socket getMySocket() {
        return mySocket;
    }

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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public GameCtr getGameCtr() {
        return gameCtr;
    }

    public void setGameCtr(GameCtr gameCtr) {
        this.gameCtr = gameCtr;
    }

    public HistoryFrm getHistoryFrm() {
        return historyFrm;
    }

    public void setHistoryFrm(HistoryFrm historyFrm) {
        this.historyFrm = historyFrm;
    }

    public RankingFrm getRankingFrm() {
        return rankingFrm;
    }

    public void setRankingFrm(RankingFrm rankingFrm) {
        this.rankingFrm = rankingFrm;
    }

}
