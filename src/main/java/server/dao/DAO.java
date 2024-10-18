package server.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {

    public static Connection con;
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=CoffeeShop";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "sa";

    public DAO() {
        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
