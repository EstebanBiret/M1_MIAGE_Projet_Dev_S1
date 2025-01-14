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

    

    
    //------US 2.2-----//
    
    public List<Produit> getProduitsDeCommande(int idPanier, int idClient) {
        List<Produit> produits = new ArrayList<>();
        
        String query = "SELECT p.idProduit, p.libelleProduit, p.prixUnitaire, p.prixKilo, p.nutriscore, " +
                       "p.poidsProduit, p.conditionnementProduit, p.marqueProduit, pr.nomProfil, " +
                       "cat.nomCategorie " +
                       "FROM panier_produit_magasin ppm " +
                       "JOIN PRODUIT p ON ppm.idProduit = p.idProduit " +
                       "JOIN PANIER pa ON pa.idPanier = ppm.idPanier " +
                       "JOIN CLIENT c ON pa.idClient = c.idClient " +
                       "JOIN client_profil cp ON c.idClient = cp.idClient " +
                       "JOIN PROFIL pr ON cp.idProfil = pr.idProfil " +
                       "JOIN Appartenir a ON p.idProduit = a.idProduit " +
                       "JOIN CATEGORIE cat ON a.idCategorie = cat.idCategorie " +
                       "WHERE ppm.idPanier = ? AND c.idClient = ?";
    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idPanier);
            pstmt.setInt(2, idClient);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produit produit = new Produit(
                        rs.getInt("idProduit"),
                        rs.getString("libelleProduit"),
                        rs.getDouble("prixUnitaire"),
                        rs.getDouble("prixKilo"),
                        rs.getString("nutriscore").charAt(0),
                        rs.getDouble("poidsProduit"),
                        rs.getString("conditionnementProduit"),
                        rs.getString("marqueProduit")
                    );
                    
                    System.out.println("Produit: " + produit.getLibelleProduit() + ", Marque: " + produit.getMarqueProduit());
    
                    produits.add(produit);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des produits : " + e.getMessage());
        }
        
        return produits;
    }
    
     

    public Map<String, Integer> calculerHabitudesConsommation(int idClient) {
        Map<String, Integer> habitudes = new HashMap<>();
        
        // Récupérer les commandes du client
        List<Commande> commandes = getCommandes(idClient);
        
        if (commandes.isEmpty()) {
            System.out.println("Aucune commande trouvée pour ce client.");
            return habitudes;
        }
        
        // Pour chaque commande, récupérer les produits associés
        for (Commande commande : commandes) {
            List<Produit> produitsCommande = getProduitsDeCommande(commande.getIdPanier(), idClient);
    
            // Ajouter des habitudes de consommation basées sur les produits
            for (Produit produit : produitsCommande) {
                ajouterHabitudes(habitudes, produit);
            }
        }
        
        // Return the habit map, no additional print statements here
        return habitudes;
    }

    
    public void ajouterHabitudes(Map<String, Integer> habitudes, Produit produit) {
        // Créer une clé unique pour chaque produit afin d'éviter les doublons
        String keyNutriscore = "nutriscore:" + produit.getNutriscore();
        String keyMarque = "marque:" + produit.getMarqueProduit();
        String keyCategorie = "categorie:" + getNomCategorie(produit.getIdProduit());
    
        // Ajouter chaque habitude, mais seulement si elle n'est pas déjà dans la map
        if (!habitudes.containsKey(keyNutriscore)) {
            incrementerHabitude(habitudes, keyNutriscore);
        }
        if (!habitudes.containsKey(keyMarque)) {
            incrementerHabitude(habitudes, keyMarque);
        }
        if (!habitudes.containsKey(keyCategorie)) {
            incrementerHabitude(habitudes, keyCategorie);
        }
    }
    
    public void incrementerHabitude(Map<String, Integer> habitudes, String habitude) {
        habitudes.put(habitude, habitudes.getOrDefault(habitude, 0) + 1);
    }
    

    public String getNomCategorie(int idProduit) {
        String categorie = "Non spécifié"; // Valeur par défaut si aucune catégorie n'est trouvée
        String query = "SELECT c.nomCategorie " +
                       "FROM Appartenir a " +
                       "JOIN CATEGORIE c ON a.idCategorie = c.idCategorie " +
                       "WHERE a.idProduit = ?";
    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idProduit);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    categorie = rs.getString("nomCategorie");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la catégorie : " + e.getMessage());
        }
    
        return categorie;
    }
}