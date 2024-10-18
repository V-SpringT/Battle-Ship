/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.network;

import client.controller.ClientCtr;
import client.view.LoginFrm;
import java.io.IOException;
import java.io.ObjectInputStream;
import shared.model.ObjectWrapper;

public class ClientListening extends Thread {

    private volatile boolean isListening = true;
    private ClientCtr clientCtr;

    public ClientListening(ClientCtr clientCtr) {
        this.clientCtr = clientCtr;
    }

    public void run() {
        try {
            while (isListening) {
                ObjectInputStream ois = new ObjectInputStream(clientCtr.getMySocket().getInputStream());
                Object obj = ois.readObject();
                if (obj instanceof ObjectWrapper) {
                    ObjectWrapper data = (ObjectWrapper) obj;
                    if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER) {
                        clientCtr.getView().showMessage("Number of client connecting to the server: " + data.getData());
                    } else {
                        for (ObjectWrapper fto : clientCtr.getActiveFunction()) {
                            if (fto.getPerformative() == data.getPerformative()) {
                                switch (data.getPerformative()) {
                                    case ObjectWrapper.REPLY_LOGIN_USER:
                                        LoginFrm loginView = (LoginFrm) fto.getData();
                                        loginView.receivedDataProcessing(data);
                                        break;
//                                    case ObjectWrapper.REPLY_EDIT_CUSTOMER:
//                                        EditCustomerFrm ecv = (EditCustomerFrm) fto.getData();
//                                        ecv.receivedDataProcessing(data);
//                                        break;
//                                    case ObjectWrapper.REPLY_SEARCH_CUSTOMER:
//                                        SearchCustomerFrm scv = (SearchCustomerFrm) fto.getData();
//                                        scv.receivedDataProcessing(data);
//                                        break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            if (isListening) {
                clientCtr.getView().showMessage("Connection to server lost!");
            }
        } catch (ClassNotFoundException e) {
            clientCtr.getView().showMessage("Data received in unknown format!");
        } finally {
            clientCtr.closeConnection();
        }
    }

    public void stopListening() {
        isListening = false;
        this.interrupt();  // Interrupt the thread if it's blocked on I/O
    }
}
