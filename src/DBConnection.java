package src;
import java.sql.*;

public class DBConnection {

    //propriétés de connexion à la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/projet_dev_m1_miage_s1";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    //méthode utilisée dans tout le projet pour se connecter à la base de données
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
            return null;
        }
    }    
}