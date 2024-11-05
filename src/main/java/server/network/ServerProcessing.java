package server.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import server.controller.GameCtr;

import server.controller.ServerCtr;
import server.dao.MatchDAO;
import server.dao.PlayerDAO;
import server.helper.CountDownTimer;
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

    private GameCtr gameCtr;

    private CountDownTimer timeTask;
    private Timer timer;
    private Timer checkTimer;
    private TimerTask checkTimeTask;

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
                        case ObjectWrapper.REGISTER_USER:
                            Player registerInfor = (Player) data.getData();
                            boolean checkRes = playerDAO.checkExistAccount(registerInfor);
                            if(checkRes){
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_REGISTER_USER, "false"));
                                break;
                            }else{
                                playerDAO.CreateAccount(registerInfor);
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_REGISTER_USER, "true"));
                                break;
                            }
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
                                    sp.enemy = this;
                                    System.out.println("Enemy before send play request: " + enemy);
                                    sp.sendData(new ObjectWrapper(ObjectWrapper.RECEIVE_PLAY_REQUEST, this.username));
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
                                enemy.enemy = this;
                                inGame = true;
                                enemy.inGame = true;
                                enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_SET_GAME_READY));
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SET_GAME_READY));
                                gameCtr = new GameCtr();
                                enemy.gameCtr = new GameCtr();
                                countDownSetShip(91);
                                enemy.countDownSetShip(91);
                                serverCtr.sendWaitingList();
                            } else {
//                                enemy.sendData(new ObjectWrapper(ObjectWrapper.ENEMY_IN_GAME_ERROR));
                            }
                            break;
                        case ObjectWrapper.REJECTED_PLAY_REQUEST:
                            enemy = null;
                            break;
                        case ObjectWrapper.READY_PLAY_GAME: // data là arraylist vị trí các tàu dạng: / 32 33 / 42 43 44...
                            stopAllTimers();
                            gameCtr.setSetup(true);
                            ArrayList<String> shipsLocation = (ArrayList<String>) data.getData();

                            System.out.println("Server: Có ai đó đã xếp xong");

                            System.out.println(this.username + "Bên server:");
                            for (String x : shipsLocation) {
                                System.out.print(x + " ");
                            }

                            // set ship location in game control ?
                            gameCtr.setPlayerShips(new ArrayList<>(shipsLocation));
                            enemy.gameCtr.setEnemyShips(new ArrayList<>(shipsLocation));

                            if (enemy.gameCtr.isSetup()) {
                                if ((int) Math.random() * 10 % 2 == 0) {
                                    sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_TURN));
                                    gameCtr.setPlayerTurn(true);
                                    countDownPlay(16);

                                    enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_NOT_TURN));
                                    enemy.gameCtr.setPlayerTurn(false);
                                } else {
                                    sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_NOT_TURN));
                                    gameCtr.setPlayerTurn(false);
                                    enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_RANDOM_TURN));
                                    enemy.gameCtr.setPlayerTurn(true);
                                    enemy.countDownPlay(16);
                                }
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_START_PLAY_GAME));
                                enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_START_PLAY_GAME));
                            }
                            break;
                        case ObjectWrapper.EXIT_MAIN_FORM:
                            inGame = false;
                            isOnline = false;
                            serverCtr.sendWaitingList();
                            break;
                        case ObjectWrapper.UPDATE_WAITING_LIST_REQUEST:
                            serverCtr.sendWaitingList();
                            break;
                        case ObjectWrapper.SHOOT_REQUEST:
                            gameCtr.setShot(true);
                            gameCtr.setCntMissTurn(0);
                            String location = (String) data.getData();
                            Object[] result = gameCtr.handleShot(location);
                            stopAllTimers();

                            if ((int) result[0] == 0) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_FAILTURE, location));
                                gameCtr.setPlayerTurn(false);
                                gameCtr.setShot(false);
                                enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_FAILTURE, location));
                                enemy.gameCtr.setPlayerTurn(true);
                                enemy.countDownPlay(16);
                            } else {
                                if (result[1] != null) {
                                    String[] destroyedShip = (String[]) result[1];
                                    if (!(boolean) result[2]) {
                                        sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_HIT_SHIP, destroyedShip));
                                        gameCtr.setShot(false);
                                        enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_HIT_SHIP, destroyedShip));
                                        countDownPlay(16);
                                    } else {
                                        this.result = "win";
                                        enemy.result = "loss";

                                        // update result match ở đây (DAO)
                                        Match match = new Match(this.username, enemy.username, "win", "loss", 1, 0);
                                        matchDAO.updateMatchResult(match);

                                        playerDAO.updateWin(this.username);
                                        playerDAO.updateLoss(enemy.username);
                                        sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_END_GAME, destroyedShip));
                                        gameCtr.setShot(false);
                                        enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_END_GAME, destroyedShip));
                                    }
                                } else {
                                    sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_HIT_POINT, location));
                                    gameCtr.setShot(false);
                                    enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_HIT_POINT, location));
                                    countDownPlay(16);
                                }
                            }
                            break;
                        case ObjectWrapper.GET_RESULT:
                            if (this.result.equals("win")) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_RESULT, "win||" + enemy.getUsername()));
                            } else if (this.result.equals("loss")) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_RESULT, "loss||" + enemy.getUsername()));
                            } else if (this.result.equals("cancelled")) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_RESULT, "cancelled||" + enemy.getUsername()));
                            } else if (this.result.equals("draw")) {
                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_SEND_RESULT, "draw||" + enemy.getUsername()));
                            }
                            break;
                        case ObjectWrapper.QUIT_WHEN_SET_SHIP:
                            stopAllTimers();
                            enemy.stopAllTimers();
                            inGame = false;
                            enemy.result = "cancelled";
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_QUIT_WHEN_SET_SHIP, this.username));

                            Match match1 = new Match(this.username, enemy.username, "afk", "cancelled", -1, 0);
                            matchDAO.updateMatchResult(match1);

                            playerDAO.updateAfk(this.username);

                            enemy = null;

                            serverCtr.sendWaitingList();

                            break;
                        case ObjectWrapper.QUIT_WHEN_PLAY:
                            stopAllTimers();
                            enemy.stopAllTimers();
                            inGame = false;
                            enemy.result = "cancelled";
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_QUIT_WHEN_PLAY, this.username));

                            Match match2 = new Match(this.username, enemy.username, "afk", "cancelled", -1, 0);
                            matchDAO.updateMatchResult(match2);

                            playerDAO.updateAfk(this.username);

                            enemy = null;

                            serverCtr.sendWaitingList();

                            break;
                        case ObjectWrapper.BACK_TO_MAIN_FORM:
                            enemy = null;
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
//            e.printStackTrace();
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

    private void countDownSetShip(int timeRemaining) {
        timeTask = new CountDownTimer(timeRemaining);
        timer = new Timer();
        timer.scheduleAtFixedRate(timeTask, 0, 1000);

        // TimerTask để kiểm tra thời gian và thực hiện khi đếm ngược về 0
        checkTimeTask = new TimerTask() {
            @Override
            public void run() {
                int remaining = timeTask.getTimeRemaining();
                System.out.println(username + "Server Time remaining: " + remaining);

                if (remaining <= 0) {
                    stopAllTimers(); // Hủy tất cả các timer
                    System.out.println("Countdown finished.");

                    // Hết thời gian vẫn chưa ready (trong lúc xếp)
                    if (!gameCtr.isSetup()) {
                        System.out.println(username + " Bên server: Có ai đó chưa xếp xong");
                        sendData(new ObjectWrapper(ObjectWrapper.SERVER_REQUEST_READY_GAME));
                    }
                }
            }
        };

        // Khởi chạy task kiểm tra mà không ảnh hưởng đến ServerProcessing
        checkTimer = new Timer();
        checkTimer.scheduleAtFixedRate(checkTimeTask, 0, 1000);
    }

    private void countDownPlay(int timeRemaining) {
        timeTask = new CountDownTimer(timeRemaining);
        timer = new Timer();
        timer.scheduleAtFixedRate(timeTask, 0, 1000);

        // TimerTask để kiểm tra thời gian và thực hiện khi đếm ngược về 0
        checkTimeTask = new TimerTask() {
            @Override
            public void run() {
                int remaining = timeTask.getTimeRemaining();
                System.out.println(username + " Server Time remaining: " + remaining);

                if (remaining <= 0) {
                    stopAllTimers(); // Hủy tất cả các timer
                    System.out.println(username + " Countdown finished.");

                    // Hết thời gian vẫn chưa bắn (trong lúc xếp)
                    if (!gameCtr.isShot() && gameCtr.isPlayerTurn()) {
                        gameCtr.setCntMissTurn(gameCtr.getCntMissTurn() + 1);
                        // cả 2 bỏ liên tục 3 lượt -> hoà
                        if (gameCtr.getCntMissTurn() == 3 && enemy.gameCtr.getCntMissTurn() == 3) {
                            result = "draw";
                            enemy.result = "draw";
                            // update result match ở đây (DAO)
                            Match match = new Match(username, enemy.username, "draw", "draw", 0, 0);
                            matchDAO.updateMatchResult(match);

                            playerDAO.updateDraw(username);
                            playerDAO.updateDraw(enemy.username);
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_END_GAME_DRAW));
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_END_GAME_DRAW));
                        } 
                        else {
                            System.out.println("Server, người chưa bắn là: " + username);
                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_MISS_TURN));
                            enemy.sendData(new ObjectWrapper(ObjectWrapper.SERVER_TRANSFER_SHOOT_MISS_TURN));
                            gameCtr.setShot(false);
                            gameCtr.setPlayerTurn(false);
                            enemy.gameCtr.setPlayerTurn(true);
                            enemy.countDownPlay(16);
                        }
                    }
                }
            }
        };

        // Khởi chạy task kiểm tra mà không ảnh hưởng đến ServerProcessing
        checkTimer = new Timer();
        checkTimer.scheduleAtFixedRate(checkTimeTask, 0, 1000);
    }

    // Phương thức để hủy tất cả các timer
    public void stopAllTimers() {
        if (timer != null) {
            timer.cancel();
            timeTask.cancel();
            timer = null;
        }
        if (checkTimer != null) {
            checkTimer.cancel();
            checkTimeTask.cancel();
            checkTimer = null;
        }
    }
}
