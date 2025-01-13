package src;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private int idClient; //id en BD (auto increment)
    private String nomClient;
    private String prenomClient;
    private String adresseClient;
    private String telClient;
    private int idMagasinFavori;

    //constructeur avec un paramètre (récupérer un client de la BD avec son ID)
    public Client(int idClient) {
        try (Connection connection = DBConnection.getConnection()) {
            String selectQuery = "SELECT * FROM client WHERE idClient = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {

                pstmt.setInt(1, idClient);

                try (ResultSet rs = pstmt.executeQuery()) {

                    if (rs.next()) {
                        this.idClient = rs.getInt("idClient");
                        this.nomClient = rs.getString("nomClient");
                        this.prenomClient = rs.getString("prenomClient");
                        this.adresseClient = rs.getString("adresseClient");
                        this.telClient = rs.getString("telClient");
                        this.idMagasinFavori = rs.getInt("idMagasin");
                    } else {
                        System.out.println("Client introuvable (" + idClient + ")");
                    }
                }
            }
        connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    //getters & setters
    public int getIdClient() {return idClient;}
    public int getIdMagasinFavori() {return idMagasinFavori;}

    //retourne le panier en cours du client, null si aucun panier en cours
    public Panier getPanierEnCours() {  

        Panier p = null;

        try (Connection connection = DBConnection.getConnection()) {

        String selectPanier = "SELECT * FROM panier WHERE idClient = ? AND panierTermine = false";
        try (PreparedStatement pstmt = connection.prepareStatement(selectPanier)) {
            pstmt.setInt(1, this.idClient);
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

    public Panier creerPanier() {
        //on gère dans la classe panier le cas où le client a déjà un panier en cours
        return new Panier(this.idClient);
    }

    /*
     * Retourne une liste des produits les plus commandés du client actuel
     */
    public List<String> getProduitsPlusCommandes() {
        List<String> produits = new ArrayList<>();

        //récupérer les commandes de ce client
        List<Commande> commandes = getCommandes();

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
                Produit produit = new Produit(idProduit);
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
    public List<Commande> getCommandes() {
        List<Commande> commandes = new ArrayList<>();

        //récupérer en BD les commandes de ce client
        try (Connection connection = DBConnection.getConnection()) {

            String commandesQuery = "SELECT * FROM commande c, panier p WHERE c.idPanier = p.idPanier AND p.idClient = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(commandesQuery)) {
                pstmt.setInt(1, this.idClient);
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

    @Override
    public String toString() {
        return "Client [id=" + idClient + ", nom=" + nomClient + ", prenom=" + prenomClient + ", adresse=" + adresseClient + ", tel=" + telClient + ", magasin favori=" + idMagasinFavori + "]";
    }
}