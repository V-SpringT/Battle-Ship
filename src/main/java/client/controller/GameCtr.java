/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller;

import client.view.PlayFrm;
import client.view.ResultFrm;
import client.view.SetShipFrm;
import java.util.ArrayList;

public class GameCtr {

    private ClientCtr mySocket;

    private ArrayList<String> playerShips;
    private ArrayList<String> enemyShips;
    private ArrayList<String> previousStrikes;
    private boolean opponentReady = false;
    private boolean playerTurn = true;
//    private OpponentTurn opponentTurn;

    private SetShipFrm setShipFrm;
    private PlayFrm playFrm;
    private ResultFrm resultFrm;

    public SetShipFrm getSetShipFrm() {
        return setShipFrm;
    }

    public void setSetShipFrm(SetShipFrm setShipFrm) {
        this.setShipFrm = setShipFrm;
    }

    public PlayFrm getPlayFrm() {
        return playFrm;
    }

    public void setPlayFrm(PlayFrm playFrm) {
        this.playFrm = playFrm;
    }

    public ResultFrm getResultFrm() {
        return resultFrm;
    }

    public void setResultFrm(ResultFrm resultFrm) {
        this.resultFrm = resultFrm;
    }

    public GameCtr(ClientCtr mySocket) {
        this.mySocket = mySocket;
    }

    public ClientCtr getMySocket() {
        return mySocket;
    }

    public ArrayList<String> getPlayerShips() {
        return playerShips;
    }

    public void setPlayerShips(ArrayList<String> playerShips) {
        this.playerShips = playerShips;
    }

    public ArrayList<String> getEnemyShips() {
        return enemyShips;
    }

    public void setEnemyShips(ArrayList<String> enemyShips) {
        this.enemyShips = enemyShips;
    }

    public ArrayList<String> getPreviousStrikes() {
        return previousStrikes;
    }

    public void setPreviousStrikes(ArrayList<String> previousStrikes) {
        this.previousStrikes = previousStrikes;
    }

    public boolean isOpponentReady() {
        return opponentReady;
    }

    public void setOpponentReady(boolean opponentReady) {
        this.opponentReady = opponentReady;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

}
