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


    /*
 * Retourne une liste des produits les plus commandés du client actuel
 * avec des détails regroupés par marque et catégorie.
 */

    public List<String> getHabitudesCommandes(int idClient) {
        List<String> habitudes = new ArrayList<>();
    
        // Récupérer les commandes de ce client
        List<Commande> commandes = getCommandes(idClient);
    
        if (commandes.isEmpty()) {
            System.out.println("Aucune commande trouvée pour ce client.");
            return habitudes;
        }
    
        // Map pour compter les occurrences des produits, regroupés par marque, catégorie, bio et nutriscore
        Map<String, Integer> groupCounts = new HashMap<>();
    
        // Query updated to include bio, nutriscore, and other relevant details
        String query = """
        SELECT p.idProduit, p.libelleProduit, p.marqueProduit, cat.nomCategorie, p.nutriscore, ppm.quantiteVoulue
        FROM panier_produit_magasin ppm
        JOIN produit p ON ppm.idProduit = p.idProduit
        JOIN Appartenir a ON p.idProduit = a.idProduit
        JOIN categorie cat ON a.idCategorie = cat.idCategorie
        JOIN panier pa ON ppm.idPanier = pa.idPanier
        JOIN client c ON pa.idClient = c.idClient
        JOIN client_profil cp ON c.idClient = cp.idClient
        JOIN profil pr ON cp.idProfil = pr.idProfil
        WHERE ppm.idPanier = ? AND pr.nomProfil = ?
        """;
    
        // Get the client's profile
        String clientProfile = getClientProfile(idClient);  // Fetch the profile name for the client
    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            // Parcourir chaque commande pour récupérer ses produits
            for (Commande commande : commandes) {
                statement.setInt(1, commande.getIdPanier()); // Set the first parameter (idPanier)
                statement.setString(2, clientProfile); // Set the second parameter (nomProfil)
    
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String marque = resultSet.getString("marqueProduit");
                        String categorie = resultSet.getString("nomCategorie");
                        String nutriscore = resultSet.getString("nutriscore");
                        int quantite = resultSet.getInt("quantiteVoulue");
    
                        // Creating a key with brand, category, bio, and Nutriscore
                        String key = String.format("Marque: %s, Catégorie: %s, Nutriscore: %s", 
                            marque, categorie, nutriscore);
    
                        // Add or update the count for this combination
                        groupCounts.put(key, groupCounts.getOrDefault(key, 0) + quantite);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Sort by the number of times ordered in descending order
        groupCounts.entrySet().stream()
            .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))
            .forEach(entry -> habitudes.add(entry.getKey() + " - Commandé " + entry.getValue() + " fois"));
    
        return habitudes;
    }
    

public String getClientProfile(int idClient) {
    String profileName = null;
    String query = """
        SELECT pr.nomProfil
        FROM client_profil cp
        JOIN profil pr ON cp.idProfil = pr.idProfil
        WHERE cp.idClient = ?
    """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, idClient);
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                profileName = resultSet.getString("nomProfil");
            }
        }
    } catch (SQLException e) {
        System.out.println("Erreur lors de la récupération du profil du client : " + e.getMessage());
    }

    return profileName;
}

    
    

}