package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import src.commande.Commande;

public class PreparateurDAO {

    //récupérer toutes les commandes en attente (date de réception la plus ancienne en premier)
    public List<Commande> getCommandesEnAttente() {
        List<Commande> commandesEnAttente = new ArrayList<>();

        //récupérer les commandes avec statut "en attente" triées par dateReception (la plus ancienne en premier)
        String query = """
            SELECT *
            FROM commande
            WHERE statutCommande = 'en attente'
            ORDER BY dateReception ASC;
        """;

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                int idCommande = rs.getInt("idCommande");
                int idPanier = rs.getInt("idPanier");
                String typeCommande = rs.getString("typeCommande");
                String statutCommande = rs.getString("statutCommande");
                Timestamp dateReception = rs.getTimestamp("dateReception");
    
                Commande commande = new Commande(idCommande, idPanier, typeCommande, statutCommande, dateReception, null, null);
                commandesEnAttente.add(commande);
            }
    
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }
        return commandesEnAttente;
    }

    //récupérer les commandes en préparation (date de préparation la plus ancienne en premier)
    public List<Commande> getCommandesPreparees() {
        List<Commande> commandesPreparees = new ArrayList<>();

        //récupérer les commandes avec statut "préparée" triées par date de préparation (la plus ancienne en premier)
        String query = """
            SELECT *
            FROM commande
            WHERE statutCommande = 'preparation'
            ORDER BY datePreparation ASC;
        """;
    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                int idCommande = rs.getInt("idCommande");
                int idPanier = rs.getInt("idPanier");
                String typeCommande = rs.getString("typeCommande");
                String statutCommande = rs.getString("statutCommande");
                Timestamp dateReception = rs.getTimestamp("dateReception");
                Timestamp datePreparation = rs.getTimestamp("datePreparation");

                Commande commande = new Commande(idCommande, idPanier, typeCommande, statutCommande, dateReception, datePreparation, null);
                commandesPreparees.add(commande);
            }
    
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes préparées : " + e.getMessage());
        }
    
        return commandesPreparees;
    }
    
    //récupérer les commandes finalisées (date de finalisation la plus ancienne en premier)
    public List<Commande> getCommandesFinalisees() {
        List<Commande> commandesFinalisees = new ArrayList<>();

        // Requête SQL pour récupérer les commandes avec statut "finalisée" triées par date de réception
        String query = """
            SELECT *
            FROM commande
            WHERE statutCommande = 'terminee'
            ORDER BY dateFinalisation ASC;
        """;
    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                int idCommande = rs.getInt("idCommande");
                int idPanier = rs.getInt("idPanier");
                String typeCommande = rs.getString("typeCommande");
                String statutCommande = rs.getString("statutCommande");
                Timestamp dateReception = rs.getTimestamp("dateReception");
                Timestamp datePreparation = rs.getTimestamp("datePreparation");
                Timestamp dateFinalisation = rs.getTimestamp("dateFinalisation");

                Commande commande = new Commande(idCommande, idPanier, typeCommande, statutCommande, dateReception, datePreparation, dateFinalisation);
                commandesFinalisees.add(commande);
            }
    
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes finalisées : " + e.getMessage());
        }
        return commandesFinalisees;
    }
    
    //marquer une commande en préparation
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
                                    System.out.println("La commande " + idCommande + " a été mise en préparation.");
                                } else {
                                    System.out.println("Aucune commande mise à jour.");
                                }
                            }
                        } else {
                            System.out.println("La commande " + idCommande + " ne peut pas être mise en préparation. Vérifiez que son statut est bien 'en attente'.");
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

    //finaliser la préparation d'une commande
    public void finaliserCommande(int idCommande) {
        try (Connection connection = DBConnection.getConnection()) {
            //requête pour mettre à jour le statut de la commande et la date de finalisation,
            //uniquement si le statut actuel est 'preparation'
            String queryUpdateCommande = "UPDATE commande " +
                                         "SET statutCommande = 'terminee',dateFinalisation = NOW()"+
                                         "WHERE idCommande = ? AND statutCommande = 'preparation' AND dateFinalisation IS NULL";
            try (PreparedStatement pstmtUpdate = connection.prepareStatement(queryUpdateCommande)) {
                pstmtUpdate.setInt(1, idCommande);
               
                int rowsUpdated = pstmtUpdate.executeUpdate();
    
                if (rowsUpdated > 0) {
                    System.out.println("La commande " + idCommande + " a été finalisée.");
                } else {
                    System.out.println("La commande " + idCommande + " ne peut pas être finalisée. Vérifiez que son statut est bien 'preparation'.");
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la finalisation de la commande " + idCommande + " : " + e.getMessage());
            }
            connection.close();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }
    }
}