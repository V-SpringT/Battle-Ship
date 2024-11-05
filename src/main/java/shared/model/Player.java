package shared.model;

import java.io.Serializable;

public class Player implements Serializable {

    private String username;
    private String password;
    private int points;
    private int totalWins;
    private int totalLosses;
    private int totalAfk;
    private int totalDraw;
    public Player() {
        super();
        this.points = 0;
        this.totalWins=0;
        this.totalLosses=0;
        this.totalAfk=0;
        this.totalDraw=0;
    }
    
    public Player(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public int getTotalDraw() {
        return totalDraw;
    }

    public void setTotalDraw(int totalDraw) {
        this.totalDraw = totalDraw;
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
