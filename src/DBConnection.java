package src;
import java.sql.*;

//le jar mysql-connector-java-8.0.23.jar doit être ajouté dans le projet pour que cette classe fonctionne (dans le dossier /lib)
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