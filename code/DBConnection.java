package code;
import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/projet_dev_m1_miage_s1";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
            return null;
        }
    }

    // public static void main(String[] args) {
    //     try (Connection connection = getConnection()) {
    //         if (connection != null) {
    //             String query = "SELECT * FROM client";
    //             Statement stmt = connection.createStatement();
    //             ResultSet rs = stmt.executeQuery(query);

    //             while (rs.next()) {
    //                 System.out.println("ID: " + rs.getInt("idClient"));
    //                 System.out.println("Nom: " + rs.getString("nomClient"));
    //             }
    //         }
    //     } catch (SQLException e) {
    //         System.out.println("Erreur : " + e.getMessage());
    //     }

    // }           
}