package src.commande;
import src.DBConnection;

import java.sql.*;

public class CommandeDAO {
    
    public Commande creerCommande(int idPanier, String typeCommande, Timestamp dateReception){ 

        Commande commande = new Commande(idPanier, typeCommande, dateReception);
        try (Connection connection = DBConnection.getConnection()) {

            String queryTest = "SELECT * FROM commande WHERE idPanier = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(queryTest)) {
                pstmt.setInt(1, idPanier);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("La commande existe déjà .");
                        return null;
                    }
                }
            }

            //création d'une commande en BD
            String query = "INSERT INTO commande (idPanier, statutCommande, typeCommande, dateReception, datePreparation, dateFinalisation) VALUES (?, ?, ?,?,?,?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, idPanier);
                pstmt.setString(2, commande.getStatutCommande());
                pstmt.setString(3, typeCommande);
                pstmt.setTimestamp(4, dateReception);
                pstmt.setTimestamp(5, commande.getDatePreparation()); 
                pstmt.setTimestamp(6, commande.getDateFinalisation()); 

                int rowsAffected = pstmt.executeUpdate();

                //on récupère l'id auto increment de la ligne tout juste générée pour l'attribuer à notre commande java
                if (rowsAffected > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            commande.setIdCommande(rs.getInt(1));
                            System.out.println("Commande créé avec succès : " + commande.toString());
                        }
                    }
                } else {
                    System.out.println("Aucune commande créé.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création du commande : " + e.getMessage());
        }
        return commande;
    }
}
