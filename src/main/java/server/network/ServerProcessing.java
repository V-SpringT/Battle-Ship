package server.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.controller.ServerCtr;
import server.dao.MatchDAO;
import server.dao.PlayerDAO;
import shared.model.Match;
import shared.dto.ObjectWrapper;
import shared.dto.PlayerHistory;
import shared.model.Player;

public class ServerProcessing extends Thread {

    private Socket mySocket;
    private ServerCtr serverCtr;
    private volatile boolean isRunning = true;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private String username;
    private ServerProcessing enemy;
    private boolean isOnline = false; // online
    private boolean inGame = false;   // trong game
    private boolean gameReady = false;

    private String result; // win, loss, afk, cancelled

    private PlayerDAO playerDAO = new PlayerDAO();
    private MatchDAO matchDAO = new MatchDAO();

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
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_LOGIN_USER, playerDAO.checkLogin(player)));
                            break;
                        case ObjectWrapper.LOGIN_SUCCESSFUL:
                            String username = (String) data.getData();
                            this.username = username;
                            inGame = false;
                            isOnline = true;
                            serverCtr.sendWaitingList();
                            break;
                        case ObjectWrapper.SEND_PLAY_REQUEST: // data la username nguoi nhan
                            String username1 = (String) data.getData();
                            boolean canSend = false;
                            for (ServerProcessing sp : serverCtr.getMyProcess()) {
                                if (sp.getUsername().equals(username1) && !sp.inGame) {
                                    canSend = true;
                                    System.out.println(new ObjectWrapper(ObjectWrapper.RECEIVE_PLAY_REQUEST, this.username));
                                    enemy = sp;
                                    enemy.enemy = this;
                                    System.out.println("Enemy before send play request: " + enemy);
                                    enemy.sendData(new ObjectWrapper(ObjectWrapper.RECEIVE_PLAY_REQUEST, this.username));
                                    break;
                                }
                            }

                            System.out.println("Enemy before send play request after loop: " + enemy);

                            if (!canSend) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_PLAY_REQUEST_ERROR));
                            }
                            break;
                        case ObjectWrapper.ACCEPTED_PLAY_REQUEST:
                            if (!enemy.inGame && enemy.isOnline) {
                                inGame = true;
                                enemy.inGame = true;
                                enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_SET_GAME_READY));
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SET_GAME_READY));
                                serverCtr.sendWaitingList();
                            } else {
//                                enemy.sendData(new ObjectWrapper(ObjectWrapper.ENEMY_IN_GAME_ERROR));
                            }
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
                                    sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_TURN));

                                    enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_NOT_TURN));
                                } else {
                                    sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_NOT_TURN));
                                    enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_TURN));
                                }
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_START_PLAY_GAME));
                                enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_START_PLAY_GAME));
                            }
                            break;
                        case ObjectWrapper.SERVER_TRANSFER_POSITION_ENEMY_SHIP:
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_POSITION_ENEMY_SHIP, (String) data.getData()));
                            break; // Địch gửi vị trí tàu, người chơi lưu vào startgame về sau
                        case ObjectWrapper.EXIT_MAIN_FORM:
                            inGame = false;
                            isOnline = false;
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
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_CHOOSE_NOT_TURN));
                            break;
                        case ObjectWrapper.SHOOT_HIT_WIN:
                            result = "win";
                            enemy.result = "loss";
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_LOSE, data.getData()));

                            // update result match ở đây (DAO)
                            Match match = new Match(this.username, enemy.username, "win", "loss", 1, 0);
                            matchDAO.updateMatchResult(match);

                            playerDAO.updateWin(this.username);
                            playerDAO.updateLoss(enemy.username);

                            break;
                        case ObjectWrapper.GET_RESULT:
                            if (result.equals("win")) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_RESULT, "win||" + enemy.getUsername()));
                            } else if (result.equals("loss")) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_RESULT, "loss||" + enemy.getUsername()));
                            } else if (result.equals("cancelled")) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_RESULT, "cancelled||" + enemy.getUsername()));
                            }
                            break;
                        case ObjectWrapper.QUIT_WHEN_SET_SHIP:
                            inGame = false;
                            enemy.result = "cancelled";
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_QUIT_WHEN_SET_SHIP, this.username));

                            Match match1 = new Match(this.username, enemy.username, "afk", "cancelled", -1, 0);
                            matchDAO.updateMatchResult(match1);

                            playerDAO.updateAfk(this.username);

                            enemy = null;
                            gameReady = false;

                            serverCtr.sendWaitingList();

                            break;
                        case ObjectWrapper.QUIT_WHEN_PLAY:
                            inGame = false;
                            enemy.result = "cancelled";
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_QUIT_WHEN_PLAY, this.username));

                            Match match2 = new Match(this.username, enemy.username, "afk", "cancelled", -1, 0);
                            matchDAO.updateMatchResult(match2);

                            playerDAO.updateAfk(this.username);

                            serverCtr.sendWaitingList();

                            enemy = null;
                            gameReady = false;

                            break;
                        case ObjectWrapper.BACK_TO_MAIN_FORM:
                            enemy = null;
                            gameReady = false;
                            inGame = false;
                            serverCtr.sendWaitingList();
                            break;
                        case ObjectWrapper.GET_HISTORY:
                            PlayerHistory playerHistory = playerDAO.getPlayerInfo(this.username);
                            playerHistory.setListMatch(matchDAO.getMatchHistory(this.username));
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_HISTORY, playerHistory));
                            break;
                        case ObjectWrapper.GET_RANKING:
                            List<PlayerHistory> leaderboard = playerDAO.getLeaderboard();
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_RANKING, leaderboard));
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
                enemy.inGame = false;
                enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_DISCONNECTED_CLIENT_ERROR));
                enemy.enemy = null;
            }
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

    public boolean isInGame() {
        return inGame;
    }

    public boolean isIsOnline() {
        return isOnline;
    }

    @Override
    public String toString() {
        return "ServerProcessing{" + "username=" + username + ", inGame=" + inGame + '}';
    }
}
