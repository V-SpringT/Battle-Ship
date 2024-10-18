/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import server.controller.ServerCtr;
import server.dao.PlayerDAO;
import shared.model.ObjectWrapper;
import shared.model.Player;

public class ServerProcessing extends Thread {

    private Socket mySocket;
    private ServerCtr serverCtr;
    private volatile boolean isRunning = true;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public ServerProcessing(Socket s, ServerCtr serverCtr) {
        super();
        mySocket = s;
        this.serverCtr = serverCtr;
    }

    public void sendData(Object obj) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                ois = new ObjectInputStream(mySocket.getInputStream());
                oos = new ObjectOutputStream(mySocket.getOutputStream());
                Object o = ois.readObject();
                if (o instanceof ObjectWrapper) {
                    ObjectWrapper data = (ObjectWrapper) o;

                    switch (data.getPerformative()) {
                        case ObjectWrapper.LOGIN_USER:
                            Player player = (Player) data.getData();
                            PlayerDAO playerDAO = new PlayerDAO();
                            if (playerDAO.checkLogin(player)) {
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER, "true"));
                            } else {
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER, "false"));
                            }
                            break;
//                        case ObjectWrapper.EDIT_CUSTOMER:
//                            Customer cl = (Customer) data.getData();
//                            CustomerDAO cd = new CustomerDAO();
//                            boolean ok = cd.editCustomer(cl);
//                            if (ok) {
//                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_EDIT_CUSTOMER, "ok"));
//                            } else {
//                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_EDIT_CUSTOMER, "false"));
//                            }
//                            break;
//                        case ObjectWrapper.SEARCH_CUSTOMER_BY_NAME:
//                            String key = (String) data.getData();
//                            cd = new CustomerDAO();
//                            ArrayList<Customer> result = cd.searchCustomer(key);
//                            oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_CUSTOMER, result));
//                            break;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            serverCtr.removeServerProcessing(this);
//            try {
//                mySocket.close();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            this.stop();
        } finally {
            serverCtr.removeServerProcessing(this);
            closeSocket();
        }
    }

    public void stopProcessing() {
        isRunning = false;
        closeSocket();
    }

    private void closeSocket() {
        try {
            if (ois != null) {
                ois.close();
            }
            if (oos != null) {
                oos.close();
            }
            if (mySocket != null && !mySocket.isClosed()) {
                mySocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
