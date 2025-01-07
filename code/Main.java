package code;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {

    private static Connection getConnection() {
        return DBConnection.getConnection();
    }

    private static void insererClient(String string, String string2) {
        try (Connection connection = getConnection()) {
            String insertQuery = "INSERT INTO client (nomClient, adresseClient, telClient) VALUES (?, ?, 0000)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                pstmt.setString(1, string); 
                pstmt.setString(2, string2);
                int rowsAffected = pstmt.executeUpdate();
                System.out.println(rowsAffected + " ligne(s) insérée(s).");
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }    
    }

    private static void modifierClient(String nomClient, String newClient) {
        try (Connection connection = getConnection()) {
            String updateQuery = "UPDATE client SET nomClient = ? WHERE nomClient = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                pstmt.setString(1, newClient); 
                pstmt.setString(2, nomClient); 
                int rowsAffected = pstmt.executeUpdate();
                System.out.println(rowsAffected + " ligne(s) modifiée(s).");
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }    
    }

    private static void supprimerClient(String nomClient) {
        try (Connection connection = getConnection()) {
            String deleteQuery = "DELETE FROM client WHERE nomClient = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
                pstmt.setString(1, nomClient); 
                int rowsAffected = pstmt.executeUpdate();
                System.out.println(rowsAffected + " ligne(s) supprimée(s).");
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }    
    }


    public static void main(String[] args) {
        insererClient("test", "adr");
        modifierClient("test", "nouveau");
        supprimerClient("nouveauNom");
    }
}