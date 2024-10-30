/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

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

    private String username;
    private ServerProcessing enemy;
    private boolean turn;
    private boolean inGame = false;
    private String message;
    private boolean gameReady = false;

    private int result; // 1 - win, 0 - lose, -1 - afk

    public ServerProcessing(Socket s, ServerCtr serverCtr) throws IOException {
        super();
        mySocket = s;
        this.serverCtr = serverCtr;
        ois = new ObjectInputStream(mySocket.getInputStream());
        oos = new ObjectOutputStream(mySocket.getOutputStream());
    }

    public void sendData(Object obj) {
        try {
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                Object o = ois.readObject();
                if (o instanceof ObjectWrapper) {
                    ObjectWrapper data = (ObjectWrapper) o;

                    switch (data.getPerformative()) {
                        case ObjectWrapper.LOGIN_USER:
                            Player player = (Player) data.getData();
                            PlayerDAO playerDAO = new PlayerDAO();
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_LOGIN_USER, playerDAO.checkLogin(player)));
//                            oos.writeObject(new ObjectWrapper(ObjectWrapper.SERVER_LOGIN_USER, playerDAO.checkLogin(player)));
                            break;
                        case ObjectWrapper.LOGIN_SUCCESSFUL:
                            String username = (String) data.getData();
                            this.username = username;
                            serverCtr.addWaitingProcessing(this);
                            break;
                        case ObjectWrapper.SEND_PLAY_REQUEST: // data la username nguoi nhan
                            String username1 = (String) data.getData();
                            boolean canSend = false;
                            for (ServerProcessing sp : serverCtr.getMyWaitingProcess()) {
                                if (sp.getUsername().equals(username1)) {
                                    canSend = true;
                                    System.out.println(new ObjectWrapper(ObjectWrapper.RECEIVE_PLAY_REQUEST, this.username));
                                    enemy = sp;
                                    enemy.enemy = this;
                                    System.out.println("Enemy before send play request: " + enemy);
                                    enemy.sendData(new ObjectWrapper(ObjectWrapper.RECEIVE_PLAY_REQUEST, this.username));
//                                    sp.enemy.oos.writeObject(new ObjectWrapper(ObjectWrapper.RECEIVE_PLAY_REQUEST, this.username)); // gui cho nguoi kia username nguoi moi
//                                    sp.enemy.oos.flush();
                                    break;
                                }
                            }

                            System.out.println("Enemy before send play request after loop: " + enemy);

                            if (!canSend) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_PLAY_REQUEST_ERROR));
//                                oos.writeObject(new ObjectWrapper(ObjectWrapper.SERVER_SEND_PLAY_REQUEST_ERROR));
//                                oos.flush();
                            }
                            break;
                        case ObjectWrapper.ACCEPTED_PLAY_REQUEST:
//                            if (!enemy.inGame) {
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_SET_GAME_READY));
//                                enemy.oos.writeObject(new ObjectWrapper(ObjectWrapper.SERVER_SET_GAME_READY));
//                                enemy.oos.flush();
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_SET_GAME_READY));
//                                oos.writeObject(new ObjectWrapper(ObjectWrapper.SERVER_SET_GAME_READY));
//                                oos.flush();
                            serverCtr.getMyWaitingProcess().remove(this);
                            serverCtr.getMyWaitingProcess().remove(enemy);
                            serverCtr.sendWaitingList();
                            inGame = true;
                            enemy.inGame = true;
//                            } else {
//                                enemy.sendData(new ObjectWrapper(ObjectWrapper.ENEMY_IN_GAME_ERROR));
//                            }
                            break;
                        case ObjectWrapper.REJECTED_PLAY_REQUEST:
                            // ?
                            break;
                        case ObjectWrapper.READY_PLAY_GAME: // data là arraylist vị trí các tàu dạng: / 32 33 / 42 43 44...
                            gameReady = true;
                            ArrayList<String> shipsLocation = (ArrayList<String>) data.getData();

                            System.out.println("Bên server:");
                            for (String x : shipsLocation) {
                                System.out.print(x + " ");
                            }

                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_POSITION_ENEMY_SHIP, shipsLocation));

                            if (enemy.gameReady == true) {
                                if ((int) Math.random() * 10 % 2 == 0) {
                                    turn = true;
                                    sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_TURN));

                                    enemy.turn = false;
                                    enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_NOT_TURN));
                                } else {
                                    turn = false;
                                    sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_NOT_TURN));
                                    enemy.turn = true;
                                    enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_TURN));
                                }
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_START_PLAY_GAME));
                                enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_START_PLAY_GAME));
                            }
                            break;
                        case ObjectWrapper.SERVER_TRANSFER_POSITION_ENEMY_SHIP:
//                            oos.writeObject(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_POSITION_ENEMY_SHIP, (String) data.getData()));
//                            oos.flush();
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_POSITION_ENEMY_SHIP, (String) data.getData()));
                            break; // Địch gửi vị trí tàu, người chơi lưu vào startgame về sau
                        case ObjectWrapper.EXIT_MAIN_FORM:
                            if (inGame) {
                                serverCtr.getMyWaitingProcess().add(enemy);
                                enemy.inGame = false;
                                //                                    enemy.oos.writeObject(new ObjectWrapper(ObjectWrapper.SERVER_DISCONNECTED_CLIENT_ERROR));
//                                    enemy.oos.flush();
                                enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_DISCONNECTED_CLIENT_ERROR));
                                enemy.enemy = null;
                            }
                            serverCtr.getMyWaitingProcess().remove(this);
                            serverCtr.sendWaitingList();
                            break;
                        case ObjectWrapper.UPDATE_WAITING_LIST_REQUEST:
                            serverCtr.sendWaitingList();
                            break;
                        case ObjectWrapper.SHOOT_FAILTURE:
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_FAILTURE, data.getData()));
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_CHOOSE_TURN));
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_CHOOSE_NOT_TURN));
                            break;
                        case ObjectWrapper.SHOOT_HIT_POINT:
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_HIT_POINT, data.getData()));
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_CHOOSE_NOT_TURN));
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_CHOOSE_TURN));
                            break;
                        case ObjectWrapper.SHOOT_HIT_SHIP:
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_HIT_SHIP, data.getData()));
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_CHOOSE_NOT_TURN));
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_CHOOSE_TURN));
                            break;
                        case ObjectWrapper.SHOOT_MISS_TURN:
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_MISS_TURN));
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_CHOOSE_TURN));
                            break;
                        case ObjectWrapper.SHOOT_HIT_WIN:
                            result = 1;
                            enemy.result = 0;
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_LOSE, data.getData()));
                            break;
                        case ObjectWrapper.GET_RESULT:
                            if (result == 1) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_RESULT, "win||" + enemy.getUsername()));
                            } else if (result == 0) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_RESULT, "lose||" + enemy.getUsername()));
                            }
                            break;
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
            if (inGame) {
                serverCtr.getMyWaitingProcess().add(enemy);
                enemy.inGame = false;
                //                    enemy.oos.writeObject(new ObjectWrapper(ObjectWrapper.SERVER_DISCONNECTED_CLIENT_ERROR));
//                    enemy.oos.flush();
                enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_DISCONNECTED_CLIENT_ERROR));
                enemy.enemy = null;
            }
            serverCtr.getMyWaitingProcess().remove(this);
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

    public String getUsername() {
        return username;
    }

    public ServerProcessing getEnemy() {
        return enemy;
    }

    @Override
    public String toString() {
        return "ServerProcessing{" + "username=" + username + ", inGame=" + inGame + ", message=" + message + '}';
    }
}
