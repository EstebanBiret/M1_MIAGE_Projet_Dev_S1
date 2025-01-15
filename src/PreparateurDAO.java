package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PreparateurDAO {
 //classe pour les US 4, Adam - préparer
 //US 4.1 Consulter la liste des commandes à preparer par ordre de priorité
 public void afficherCommandesEnAttente() {
 // Requête SQL pour récupérer les commandes avec statut "en attente" triées par dateReception (la plus ancienne en premier)
            
            String query = """
                SELECT idCommande, statutCommande, dateReception
                FROM commande
                WHERE statutCommande = 'en attente'
                ORDER BY dateReception ASC;
            """;
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
        
                System.out.println("Liste des commandes en attente (par date de réception la plus ancienne) :");
                boolean hasResults = false; // Pour vérifier si des résultats existent
                while (rs.next()) {
                    hasResults = true;
                    int idCommande = rs.getInt("idCommande");
                    String statutCommande = rs.getString("statutCommande");
                    Timestamp dateReception = rs.getTimestamp("dateReception");
        
                    // Affichage des propriétés demandées
                    System.out.printf(
                        "ID Commande: %d | Statut: %s | Date Réception: %s%n",
                        idCommande,
                        statutCommande,
                        dateReception != null ? dateReception.toString() : "N/A"
                    );
                }
        
                if (!hasResults) {
                    System.out.println("Aucune commande en attente trouvée.");
                }
        
            } catch (SQLException e) {
                System.out.println("Erreur lors de la récupération des commandes : " + e.getMessage());
            }
        }
        
    //US 4.2
    public void commencerAPreparer(int idCommande) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT statutCommande FROM commande WHERE idCommande = ? AND datePreparation IS NULL";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, idCommande);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {  // Vérifie si la commande existe
                        String statutCommande = rs.getString("statutCommande");
                        if ("en attente".equalsIgnoreCase(statutCommande)) {
                            String update = "UPDATE commande SET statutCommande = 'Préparation', datePreparation = now() WHERE idCommande = ?";
                            try (PreparedStatement pstmtUpdate = connection.prepareStatement(update)) {
                                pstmtUpdate.setInt(1, idCommande);
                                int rowsAffected = pstmtUpdate.executeUpdate();
                                if (rowsAffected > 0) {
                                    System.out.println("Statut de la commande mis à jour avec succès.");
                                } else {
                                    System.out.println("Aucune commande mise à jour.");
                                }
                            }
                        } else {
                            System.out.println("La commande n'est pas en attente. Statut actuel : " + statutCommande);
                        }
                    } else {
                        System.out.println("Commande introuvable pour l'ID : " + idCommande);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }

    // US 4.3 finaliser la préparation d'une commande
    public void finaliserCommande(int idCommande) {
        try (Connection connection = DBConnection.getConnection()) {
            // Requête pour mettre à jour le statut de la commande et la date de finalisation,
            // uniquement si le statut actuel est 'preparation'
            String queryUpdateCommande = "UPDATE commande " +
                                         "SET statutCommande = 'terminee',dateFinalisation = NOW()"+
                                         "WHERE idCommande = ? AND statutCommande = 'preparation' AND dateFinalisation IS NULL";
            try (PreparedStatement pstmtUpdate = connection.prepareStatement(queryUpdateCommande)) {
                pstmtUpdate.setInt(1, idCommande);
               
                
                int rowsUpdated = pstmtUpdate.executeUpdate();
    
                if (rowsUpdated > 0) {
                    System.out.println("La commande #" + idCommande + " a été finalisée avec succès.");
                } else {
                    System.out.println("La commande #" + idCommande + " ne peut pas être finalisée. Vérifiez que son statut est bien 'preparation'.");
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la finalisation de la commande #" + idCommande + " : " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }
    }

}