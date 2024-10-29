/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.network;

import client.controller.ClientCtr;
import client.view.LoginFrm;
import client.view.MainFrm;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import shared.model.ObjectWrapper;

public class ClientListening extends Thread {

    private volatile boolean isListening = true;
    private ClientCtr clientCtr;

    private ObjectInputStream ois;

    public ClientListening(ClientCtr clientCtr) throws IOException {
        this.clientCtr = clientCtr;
        this.ois = new ObjectInputStream(clientCtr.getMySocket().getInputStream());
    }

    @Override
    public void run() {
        try {
            while (isListening) {
//                ois = new ObjectInputStream(clientCtr.getMySocket().getInputStream());
                Object obj = ois.readObject();
                if (obj instanceof ObjectWrapper) {
                    System.out.println(obj);
                    ObjectWrapper data = (ObjectWrapper) obj;
                    if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER) {
                        clientCtr.getConnectFrm().showMessage("Number of client connecting to the server: " + data.getData());
                    } else {
                        switch (data.getPerformative()) {
                            case ObjectWrapper.SERVER_LOGIN_USER:
                                clientCtr.getLoginFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_INFORM_CLIENT_WAITING:
                                clientCtr.getMainFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.RECEIVE_PLAY_REQUEST:
                                clientCtr.getMainFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_SET_GAME_READY:
                                clientCtr.getMainFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_TRANSFER_POSITION_ENEMY_SHIP:
                                clientCtr.getGameCtr().getSetShipFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_RANDOM_NOT_TURN:
                                clientCtr.getGameCtr().getSetShipFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_RANDOM_TURN:
                                clientCtr.getGameCtr().getSetShipFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_START_PLAY_GAME:
                                clientCtr.getGameCtr().getSetShipFrm().receivedDataProcessing(data);
                                break;
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (isListening) {
                clientCtr.getConnectFrm().showMessage("Connection to server lost!");
            }
        } catch (ClassNotFoundException e) {
            clientCtr.getConnectFrm().showMessage("Data received in unknown format!");
        } finally {
//            clientCtr.closeConnection();
        }
    }

    public void stopListening() {
        isListening = false;
        this.interrupt();  // Interrupt the thread if it's blocked on I/O
    }
}
