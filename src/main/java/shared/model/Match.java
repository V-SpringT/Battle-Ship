package shared.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Match implements Serializable {

    private int matchId;
    private String user1Username;
    private String user2Username;
    private LocalDateTime timestamp;
    private String resultUser1;
    private String resultUser2;
    private int pointsChangeUser1;
    private int pointsChangeUser2;

    public Match() {
        super();
    }

    public Match(String user1Username, String user2Username, String resultUser1, String resultUser2, int pointsChangeUser1, int pointsChangeUser2) {
        this.user1Username = user1Username;
        this.user2Username = user2Username;
        this.resultUser1 = resultUser1;
        this.resultUser2 = resultUser2;
        this.pointsChangeUser1 = pointsChangeUser1;
        this.pointsChangeUser2 = pointsChangeUser2;
    }

    public Match(String user2Username, LocalDateTime timestamp, String resultUser1, int pointsChangeUser1) {
        this.user2Username = user2Username;
        this.timestamp = timestamp;
        this.resultUser1 = resultUser1;
        this.pointsChangeUser1 = pointsChangeUser1;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public String getUser1Username() {
        return user1Username;
    }

    public void setUser1Username(String user1Username) {
        this.user1Username = user1Username;
    }

    public String getUser2Username() {
        return user2Username;
    }

    public void setUser2Username(String user2Username) {
        this.user2Username = user2Username;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getResultUser1() {
        return resultUser1;
    }

    public void setResultUser1(String resultUser1) {
        this.resultUser1 = resultUser1;
    }

    public String getResultUser2() {
        return resultUser2;
    }

    public void setResultUser2(String resultUser2) {
        this.resultUser2 = resultUser2;
    }

    public int getPointsChangeUser1() {
        return pointsChangeUser1;
    }

    public void setPointsChangeUser1(int pointsChangeUser1) {
        this.pointsChangeUser1 = pointsChangeUser1;
    }

    public int getPointsChangeUser2() {
        return pointsChangeUser2;
    }

    public void setPointsChangeUser2(int pointsChangeUser2) {
        this.pointsChangeUser2 = pointsChangeUser2;
    }

    @Override
    public String toString() {
        return "Match{" + "user1Username=" + user1Username + ", user2Username=" + user2Username + ", timestamp=" + timestamp + ", resultUser1=" + resultUser1 + ", resultUser2=" + resultUser2 + ", pointsChangeUser1=" + pointsChangeUser1 + ", pointsChangeUser2=" + pointsChangeUser2 + '}';
    }

}
