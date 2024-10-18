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

    public boolean checkLogin(Player player) {
        boolean result = false;
        String sql = "SELECT  id, name, position FROM tblUser WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, player.getUsername());
            ps.setString(2, player.getPassword());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                player.setId(rs.getInt("id"));
                player.setName(rs.getString("name"));
            
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
