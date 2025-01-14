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
    public static void afficherCommandesParPriorite() {
        // Requête SQL pour trier les commandes selon statutCommande et dateReception
        String query = """
            SELECT idCommande, idPanier,statutCommande,dateReception, typeCommande,   datePreparation, dateFinalisation
            FROM commande
            ORDER BY 
                CASE 
                    WHEN statutCommande = 'en attente' THEN 1
                    WHEN statutCommande = 'preparaion' THEN 2
                    WHEN statutCommande = 'retrait' THEN 3
                    WHEN statutCommande = 'en envoi' THEN 4
                    ELSE 5
                END,
                dateReception ASC;
            """;
    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
    
            System.out.println("Liste des commandes par ordre de priorité :");
            while (rs.next()) {
                int idCommande = rs.getInt("idCommande");
                int idPanier = rs.getInt("idPanier");
                String typeCommande = rs.getString("typeCommande");
                String statutCommande = rs.getString("statutCommande");
                Timestamp dateReception = rs.getTimestamp("dateReception");
                Timestamp datePreparation = rs.getTimestamp("datePreparation");
                Timestamp dateFinalisation = rs.getTimestamp("dateFinalisation");
    
                // Affichage de chaque commande
                System.out.println(new Commande(
                    idCommande, idPanier, typeCommande, statutCommande, 
                    dateReception, datePreparation, dateFinalisation
                ).toString());
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
