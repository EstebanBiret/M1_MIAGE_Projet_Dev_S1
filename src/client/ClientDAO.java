package src.client;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import src.DBConnection;
import src.commande.Commande;
import src.panier.Panier;
import src.produit.Produit;
import src.produit.ProduitDAO;


public class ClientDAO {
    
    public Client getClientById(int idClient) {
        Client client = null;
        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery = "SELECT * FROM client WHERE idClient = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setInt(1, idClient);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        client = new Client(
                            rs.getInt("idClient"),
                            rs.getString("nomClient"),
                            rs.getString("prenomClient"),
                            rs.getString("adresseClient"),
                            rs.getString("telClient"),
                            rs.getInt("idMagasin")
                        );
                    } else {
                        System.out.println("Client introuvable (" + idClient + ")");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return client;
    }

    //retourne le panier en cours du client, null si aucun panier en cours
    public Panier getPanierEnCours(int idClient) {  

        Panier p = null;

        try (Connection connection = DBConnection.getConnection()) {

        String selectPanier = "SELECT * FROM panier WHERE idClient = ? AND panierTermine = false";
        try (PreparedStatement pstmt = connection.prepareStatement(selectPanier)) {
            pstmt.setInt(1, idClient);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    p = new Panier(rs.getInt("idPanier"), idClient, rs.getTimestamp("dateDebutPanier"));
                }
            }
        }
        connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return p;
    }

    /*
     * Retourne une liste des produits les plus commandés du client actuel
     */
    public List<String> getProduitsPlusCommandes(int idClient) {
        List<String> produits = new ArrayList<>();

        //récupérer les commandes de ce client
        List<Commande> commandes = getCommandes(idClient);

        if (commandes.isEmpty()) {
            System.out.println("Aucune commande trouvée pour ce client.");
            return produits;
        }
        
        // Map pour compter les occurrences des produits
        Map<Integer, Integer> produitCounts = new HashMap<>();

        String query = """
            SELECT p.idProduit, p.libelleProduit, p.prixUnitaire, p.prixKilo, p.nutriscore, 
                p.poidsProduit, p.conditionnementProduit, p.marqueProduit, ppm.quantiteVoulue
            FROM panier_produit_magasin ppm, produit p
            WHERE ppm.idProduit = p.idProduit
            AND ppm.idPanier = ?
        """;

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            // Parcourir chaque commande pour récupérer ses produits
            for (Commande commande : commandes) {
                statement.setInt(1, commande.getIdPanier());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idProduit = resultSet.getInt("idProduit");
                        int quantiteVoulue = resultSet.getInt("quantiteVoulue");

                        // Ajouter au compteur ou incrémenter
                        produitCounts.put(idProduit, produitCounts.getOrDefault(idProduit, 0) + quantiteVoulue);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Trier les produits par nombre de commandes décroissant
        produitCounts.entrySet().stream()
            .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))
            .forEach(entry -> {
                int idProduit = entry.getKey();
                int quantite = entry.getValue();

                // Récupérer les informations du produit
                ProduitDAO produitDAO = new ProduitDAO();
                Produit produit = produitDAO.getProduitById(idProduit);
                if (produit != null) {
                    String description = produit + " (Commandé " + quantite + " fois)";
                    produits.add(description);
                }
            });

        return produits;
    }

    /*
     * Retourne une liste des commandes du client actuel
     */
    public List<Commande> getCommandes(int idClient) {
        List<Commande> commandes = new ArrayList<>();

        //récupérer en BD les commandes de ce client
        try (Connection connection = DBConnection.getConnection()) {

            String commandesQuery = "SELECT * FROM commande c, panier p WHERE c.idPanier = p.idPanier AND p.idClient = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(commandesQuery)) {
                pstmt.setInt(1, idClient);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Commande commande = new Commande(
                            rs.getInt("idCommande"),
                            rs.getInt("idPanier"),
                            rs.getString("typeCommande"),
                            rs.getString("statutCommande"),
                            rs.getTimestamp("dateReception"),
                            rs.getTimestamp("datePreparation"),
                            rs.getTimestamp("dateFinalisation")
                        );
                        commandes.add(commande);
                    } 
                }
            }
            connection.close();
    
            } catch (SQLException e) {
                System.out.println("Erreur : " + e.getMessage());
            }

        if(commandes.isEmpty()) {
            System.out.println("Ce client n'a pas de commandes.");
        }
        return commandes;
    }

    public String getNomClient(int idClient) {
        String nomClient = null;
        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery = "SELECT nomClient FROM client WHERE idClient = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setInt(1, idClient);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        nomClient = rs.getString("nomClient");
                    } else {
                        System.out.println("Client introuvable (" + idClient + ")");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return nomClient;
    }

    public String getPrenomClient(int idClient) {
        String prenomClient = null;
        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery = "SELECT prenomClient FROM client WHERE idClient = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setInt(1, idClient);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        prenomClient = rs.getString("prenomClient");
                    } else {
                        System.out.println("Client introuvable (" + idClient + ")");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return prenomClient;
    }

    public String getMagasinFavori(int idClient) {
        String magasinFavori = null;
        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery = "SELECT nomMagasin FROM client c, magasin m WHERE idClient = ? AND c.idMagasin = m.idMagasin";
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setInt(1, idClient);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        magasinFavori = rs.getString("nomMagasin");
                    } else {
                        System.out.println("Magasin favori introuvable  pour le client (" + idClient + ")");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return magasinFavori;
    }

    

}