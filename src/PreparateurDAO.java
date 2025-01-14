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
        
        
        
        public static void marquerEnPreparation(int idCommande, String typeCommande) {
            // Validation du type de commande
            if (!typeCommande.equalsIgnoreCase("retrait") && !typeCommande.equalsIgnoreCase("livraison") && !typeCommande.equalsIgnoreCase("mixte")) {
                System.out.println("Type de commande invalide. Veuillez choisir entre 'retrait' ou 'livraison' ou 'mixte'.");
                return;
            }
        
            String query = """
                UPDATE commande
                SET statutCommande = ?, typeCommande = ?, datePreparation = ?
                WHERE idCommande = ?
            """;
        
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(query)) {
        
                // Paramétrer la requête
                pstmt.setString(1, "preparation");
                pstmt.setString(2, typeCommande.toLowerCase());
                pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                pstmt.setInt(4, idCommande);
        
                // Exécuter la mise à jour
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Commande " + idCommande + " mise à jour avec succès : statut = 'préparation', type = '" + typeCommande + "'.");
                } else {
                    System.out.println("Aucune commande trouvée avec l'ID : " + idCommande);
                }
        
            } catch (SQLException e) {
                System.out.println("Erreur lors de la mise à jour de la commande : " + e.getMessage());
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
                            String update = "UPDATE commande SET statutCommande = 'Préparation',datePreparation = now() WHERE idCommande = ?";
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

}
