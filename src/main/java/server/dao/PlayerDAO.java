package server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import server.DAO.DAO;
import shared.dto.PlayerHistory;
import shared.model.Player;

public class PlayerDAO extends DAO {

    public PlayerDAO() {
        super();
    }

    public String checkLogin(Player player) {
        boolean result = false;
        String sql = "SELECT username, password FROM players WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, player.getUsername());
            ps.setString(2, player.getPassword());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "true";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    public boolean updateAfk(String username) {
        String sql = "UPDATE players SET total_afk = total_afk + 1, points = points - 1 WHERE username = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateWin(String username) {
        String sql = "UPDATE players SET total_wins = total_wins + 1, points = points + 1 WHERE username = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateLoss(String username) {
        String sql = "UPDATE players SET total_losses = total_losses + 1 WHERE username = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }    // Lấy thông tin người chơi

    public PlayerHistory getPlayerInfo(String username) {
        PlayerHistory playerHistory = null;
        String sql = "SELECT points, total_wins, total_losses, total_afk FROM players WHERE username = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int points = rs.getInt("points");
                int totalWins = rs.getInt("total_wins");
                int totalLosses = rs.getInt("total_losses");
                int totalAfk = rs.getInt("total_afk");
                int ranking = calculateRanking(username); 
                playerHistory = new PlayerHistory(ranking, points, totalWins, totalLosses, totalAfk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerHistory;
    }

    // Tính toán thứ hạng người chơi dựa trên điểm và số lần AFK
    private int calculateRanking(String username) {
        String sql = "SELECT username FROM players ORDER BY points DESC, total_afk ASC";
        int rank = 1;
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("username").equals(username)) {
                    break;
                }
                rank++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rank;
    }

    // Lấy danh sách bảng xếp hạng tất cả người chơi
    public List<PlayerHistory> getLeaderboard() {
        List<PlayerHistory> leaderboard = new ArrayList<>();
        String sql = "SELECT username, points, total_wins, total_losses, total_afk FROM players ORDER BY points DESC, total_afk ASC, total_wins DESC, total_losses DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                String username = rs.getString("username");
                int points = rs.getInt("points");
                int totalWins = rs.getInt("total_wins");
                int totalLosses = rs.getInt("total_losses");
                int totalAfk = rs.getInt("total_afk");
                leaderboard.add(new PlayerHistory(username, rank, points, totalWins, totalLosses, totalAfk));
                rank++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leaderboard;
    }
}
