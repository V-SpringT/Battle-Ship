/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import server.DAO.DAO;
import shared.model.Player;

public class PlayerDAO extends DAO {
    public PlayerDAO() {
        super();
    }

    // return player id, "0" if not have
    public String checkLogin(Player player) {
        boolean result = false;
        String sql = "SELECT  id, name FROM player WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, player.getUsername());
            ps.setString(2, player.getPassword());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                player.setId(rs.getInt("id"));
                player.setName(rs.getString("name"));
            
                return String.valueOf(player.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}
