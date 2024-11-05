package client.network;

import client.controller.ClientCtr;

import java.io.IOException;
import java.io.ObjectInputStream;

import shared.dto.ObjectWrapper;

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
                Object obj = ois.readObject();
                if (obj instanceof ObjectWrapper) {
                    System.out.println(obj);
                    ObjectWrapper data = (ObjectWrapper) obj;
                    if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER) {
                        clientCtr.getConnectFrm().showMessage("Number of client connecting to the server: " + data.getData());
                    } else {
                        switch(data.getPerformative()){
                            case ObjectWrapper.SERVER_REGISTER_USER:
                                clientCtr.getRegisterFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_LOGIN_USER:
                                clientCtr.getLoginFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_INFORM_CLIENT_WAITING:
                                if (clientCtr.getMainFrm() != null) {
                                    clientCtr.getMainFrm().receivedDataProcessing(data);
                                }
                                break;
                            case ObjectWrapper.SERVER_SEND_HISTORY:
                                clientCtr.getHistoryFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_SEND_RANKING:
                                clientCtr.getRankingFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.RECEIVE_PLAY_REQUEST:
                                clientCtr.getMainFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_SET_GAME_READY:
                                clientCtr.getMainFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_REQUEST_READY_GAME:
                                System.out.println("Client: Có ai đó chưa xếp xong");
                                clientCtr.getSetShipFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_RANDOM_NOT_TURN:
                                clientCtr.getSetShipFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_RANDOM_TURN:
                                clientCtr.getSetShipFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_START_PLAY_GAME:
                                clientCtr.getSetShipFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_TRANSFER_SHOOT_FAILTURE:
                                clientCtr.getPlayFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_TRANSFER_SHOOT_HIT_POINT:
                                clientCtr.getPlayFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_TRANSFER_SHOOT_HIT_SHIP:
                                clientCtr.getPlayFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_TRANSFER_SHOOT_MISS_TURN:
                                clientCtr.getPlayFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_TRANSFER_END_GAME:
                                clientCtr.getPlayFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_TRANSFER_END_GAME_DRAW:
                                clientCtr.getPlayFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_SEND_RESULT:
                                clientCtr.getResultFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_TRANSFER_QUIT_WHEN_SET_SHIP:
                                clientCtr.getSetShipFrm().receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_TRANSFER_QUIT_WHEN_PLAY:
                                clientCtr.getPlayFrm().receivedDataProcessing(data);
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
