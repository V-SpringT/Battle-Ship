package shared.model;

import java.io.Serializable;

public class Player implements Serializable {

    private String username;
    private String password;
    private int points;
    private int totalWins;
    private int totalLosses;
    private int totalAfk;

    public Player() {
        super();
    }

    public Player(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public void setTotalLosses(int totalLosses) {
        this.totalLosses = totalLosses;
    }

    public int getTotalAfk() {
        return totalAfk;
    }

    public void setTotalAfk(int totalAfk) {
        this.totalAfk = totalAfk;
    }

}
