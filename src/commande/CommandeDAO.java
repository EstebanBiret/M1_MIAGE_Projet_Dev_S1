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

    //Consulter la liste des commandes à preparer par ordre de priorité
    public static void afficherCommandesEnAttente() {
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
    
}
