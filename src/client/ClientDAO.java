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
import java.text.SimpleDateFormat;
import java.util.Date;

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

    // Méthode pour récupérer les habitudes de consommation
    public List<String> getHabitudesConsos(int idClient) {
        List<String> habitudes = new ArrayList<>();
        List<Commande> commandes = getCommandes(idClient);
    
        if (commandes.isEmpty()) {
            habitudes.add("Aucune commande trouvée pour ce client.");
            return habitudes;
        }
    
        Map<String, Integer> categorieCounts = new HashMap<>();
        Map<String, Integer> marqueCounts = new HashMap<>();
        Map<String, Integer> nutriscoreCounts = new HashMap<>();
    
        String query = """
        SELECT p.marqueProduit, cat.nomCategorie, p.nutriscore, ppm.quantiteVoulue
        FROM panier_produit_magasin ppm, produit p, Appartenir a, categorie cat
        WHERE ppm.idProduit = p.idProduit
        AND p.idProduit = a.idProduit
        AND a.idCategorie = cat.idCategorie
        AND ppm.idPanier = ?
        """;

    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            for (Commande commande : commandes) {
                statement.setInt(1, commande.getIdPanier());
    
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String categorie = resultSet.getString("nomCategorie");
                        String marque = resultSet.getString("marqueProduit");
                        String nutriscore = resultSet.getString("nutriscore");
                        int quantite = resultSet.getInt("quantiteVoulue");
    
                        categorieCounts.put(categorie, categorieCounts.getOrDefault(categorie, 0) + quantite);
                        marqueCounts.put(marque, marqueCounts.getOrDefault(marque, 0) + quantite);
                        nutriscoreCounts.put(nutriscore, nutriscoreCounts.getOrDefault(nutriscore, 0) + quantite);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        habitudes.add("\nCatégories les plus commandées :");
        categorieCounts.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .forEach(entry -> habitudes.add(entry.getKey() + " - Commandé " + entry.getValue() + " fois"));
    
        habitudes.add("\nMarques les plus commandées :");
        marqueCounts.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .forEach(entry -> habitudes.add(entry.getKey() + " - Commandé " + entry.getValue() + " fois"));
    
        habitudes.add("\nNutriscores les plus commandés :");
        nutriscoreCounts.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .forEach(entry -> habitudes.add(entry.getKey() + " - Commandé " + entry.getValue() + " fois"));
    
        return habitudes;
    }

    public void afficherCommandes(List<Commande> commandes) {
        if (commandes.isEmpty()) {
            System.out.println("Aucune commande trouvée.");
            return;
        }
    
        for (Commande commande : commandes) {
            String dateStr = "Non disponible";
            if (commande.getDateReception() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                dateStr = sdf.format(new Date(commande.getDateReception().getTime()));
            }
    
            String commandeString = String.format(
                "ID: %d | Statut: %s | Mode: %s | Date de la commande: %s",
                commande.getIdCommande(),
                commande.getStatutCommande(),
                commande.getTypeCommande(),
                dateStr
            );
    
            System.out.println(commandeString);
        }
    }
    
    public List<Commande> getCommandes(int idClient) {
        List<Commande> commandes = new ArrayList<>();
    
        String query = """
            SELECT c.idCommande, c.idPanier, c.typeCommande, c.statutCommande, c.dateReception, c.datePreparation, c.dateFinalisation,
                SUM(ppm.quantiteVoulue) AS nbProduits
            FROM commande c, panier p, panier_produit_magasin ppm
            WHERE c.idPanier = p.idPanier
            AND p.idClient = ?
            AND p.idPanier = ppm.idPanier
            GROUP BY c.idCommande, c.typeCommande, c.statutCommande, c.dateReception, c.datePreparation, c.dateFinalisation
        """;

    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
    
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
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }
    
        return commandes;
    }

    // Méthode pour récupérer les 5 produits les plus commandés avec classement numéroté
    public List<String> getProduitsPlusCommandes(int idClient) {
        List<String> produits = new ArrayList<>();
        List<Commande> commandes = getCommandes(idClient);
    
        if (commandes.isEmpty()) {
            produits.add("Aucune commande trouvée pour ce client.");
            return produits;
        }
    
        Map<Integer, Integer> produitCounts = new HashMap<>();
        String query = """
            SELECT p.idProduit, ppm.quantiteVoulue
            FROM panier_produit_magasin ppm, produit p
            WHERE ppm.idPanier = ?
            AND p.idProduit = ppm.idProduit        
        """;
    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            for (Commande commande : commandes) {
                statement.setInt(1, commande.getIdPanier());
    
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idProduit = resultSet.getInt("idProduit");
                        int quantiteVoulue = resultSet.getInt("quantiteVoulue");
    
                        produitCounts.put(idProduit, produitCounts.getOrDefault(idProduit, 0) + quantiteVoulue);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        final int[] rank = {1};
        produitCounts.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .limit(5)
            .forEach(entry -> {
                int idProduit = entry.getKey();
                int quantite = entry.getValue();
    
                ProduitDAO produitDAO = new ProduitDAO();
                Produit produit = produitDAO.getProduitById(idProduit);
                if (produit != null) {
                    produits.add(rank[0] + ". " + produit.getLibelleProduit() + " - " + produit.getMarqueProduit() + " (Commandé " + quantite + " fois)");
                    rank[0]++;
                }
            });
    
        return produits;
    }
}