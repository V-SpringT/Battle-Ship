/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller;

import client.view.PlayFrm;
import client.view.ResultFrm;
import client.view.SetShipFrm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameCtr {

    private ClientCtr mySocket;

    private ArrayList<String> playerShips;
    private ArrayList<String> enemyShips;
    private boolean playerTurn = true;
    private int result;
    private String usernameEnemy;

    // Thêm các biến để theo dõi trạng thái tàu
    private Map<String, Set<String>> shipGroups = new HashMap<>(); // Map để lưu các nhóm tàu
    private Set<String> hitPositions = new HashSet<>();  // Set lưu các vị trí đã bắn trúng

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
        initializeShipEnemyGroups();
    }

//    public boolean isOpponentReady() {
//        return opponentReady;
//    }
//
//    public void setOpponentReady(boolean opponentReady) {
//        this.opponentReady = opponentReady;
//    }
    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    public GameCtr() {
    }

    private void initializeShipEnemyGroups() {
        if (enemyShips == null || enemyShips.isEmpty()) {
            return;
        }

        // Khởi tạo các nhóm tàu từ enemyShips
        Set<String> currentShip = new HashSet<>();
        int groupId = 0;
        for (String loc : enemyShips) {
            if (loc.equals("/")) {
                if (!currentShip.isEmpty()) {
                    shipGroups.put("ship" + groupId, currentShip);
                    groupId++;
                    currentShip = new HashSet<>();
                }
                currentShip = new HashSet<>();
            } else {
                currentShip.add(loc);
            }
        }

        if (!currentShip.isEmpty()) {
            shipGroups.put("ship" + groupId, currentShip);
        }
    }

//    Xử lý logic khi bắn vào một ô
//    @param position Vị trí bắn (dạng String như "49")
//    @return Object[] với 3 phần tử:
//        - phần tử 0: Integer (0: bắn hụt, 1: bắn trúng)
//        - phần tử 1: String[] (null nếu không có tàu bị hủy, 
//                hoặc mảng các vị trí của tàu bị hủy)
//        - phần tử 2: Boolean (true nếu đã thắng game - tất cả tàu đã bị hủy)
    public Object[] handleShot(String position) {
        // Kiểm tra xem có tàu nào ở vị trí này không
        boolean isHit = false;
        String hitShipKey = null;

        // Duyệt qua từng nhóm tàu
        for (Map.Entry<String, Set<String>> entry : shipGroups.entrySet()) {
            if (entry.getValue().contains(position)) {
                isHit = true;
                hitShipKey = entry.getKey();
                hitPositions.add(position);
                break;
            }
        }

        System.out.println(hitPositions);

        if (!isHit) {
            return new Object[]{0, null, false}; // Bắn hụt
        }

        // Kiểm tra xem tàu có bị hủy hoàn toàn không
        Set<String> hitShip = shipGroups.get(hitShipKey);
        if (hitPositions.containsAll(hitShip)) {
            // Tàu bị hủy hoàn toàn
            String[] shipPositions = hitShip.toArray(new String[0]);
            Arrays.sort(shipPositions); // Sắp xếp các vị trí để đảm bảo thứ tự nhất quán

            // Kiểm tra xem đã thắng chưa (tất cả tàu đã bị hủy)
            boolean isGameWon = checkGameWon();
            return new Object[]{1, shipPositions, isGameWon};
        }

        // Bắn trúng nhưng tàu chưa bị hủy
        return new Object[]{1, null, false};
    }

    private boolean checkGameWon() {
        // Đếm tổng số ô của tất cả các tàu
        int totalShipPositions = 0;
        for (Set<String> ship : shipGroups.values()) {
            totalShipPositions += ship.size();
        }

        // So sánh với số ô đã bắn trúng
        return hitPositions.size() == totalShipPositions;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getUsernameEnemy() {
        return usernameEnemy;
    }

    public void setUsernameEnemy(String usernameEnemy) {
        this.usernameEnemy = usernameEnemy;
    }

}
