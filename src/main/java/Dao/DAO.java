package Dao;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class DAO {
    public static Connection con;
     
    public DAO(){
        if(con == null){
            String dbUrl = "jdbc:mysql://localhost:3307/hotel?autoReconnect=true&useSSL=false";
            String dbClass = "com.mysql.jdbc.Driver";
 
            try {
                Class.forName(dbClass);
                con = DriverManager.getConnection (dbUrl, "sa", "123456");
            }catch(ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}