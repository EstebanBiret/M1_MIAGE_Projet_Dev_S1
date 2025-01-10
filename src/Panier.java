package src;

import java.sql.*;
import java.util.Map;

public class Panier {
    
    // Propriétés
    private int idPanier;
    private int idClient;

    private boolean panierTermine;
    private Timestamp dateDebutPanier;
    private Timestamp dateFinPanier;
    //private List<ArrayList<Integer, Integer, String>> produits;

    // Constructeur pour créer un nouveau panier
    public Panier(int idClient) {
        this.idClient = idClient;
        this.panierTermine = false;
        this.dateDebutPanier = new Timestamp(System.currentTimeMillis());
        this.dateFinPanier = null;

        // Utilisation d'une seule connexion
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(true); // Active le mode auto-commit

            // Vérification du panier en cours
            String queryTest = "SELECT * FROM panier WHERE idClient = ? AND panierTermine = false;";
            try (PreparedStatement pstmt = connection.prepareStatement(queryTest)) {
                pstmt.setInt(1, idClient);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Le client a déjà un panier en cours.");
                        return;
                    }
                }
            }

            // Création du panier
            String query = "INSERT INTO panier (idClient, panierTermine, dateDebutPanier, dateFinPanier) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, idClient);
                pstmt.setBoolean(2, this.panierTermine);
                pstmt.setTimestamp(3, this.dateDebutPanier);
                pstmt.setTimestamp(4, this.dateFinPanier);

                int rowsAffected = pstmt.executeUpdate();

                // Récupérer l'ID généré
                if (rowsAffected > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            this.idPanier = rs.getInt(1);
                            System.out.println("Panier créé avec succès : " + this.toString());
                        }
                    }
                } else {
                    System.out.println("Aucun panier créé.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création du panier : " + e.getMessage());
        }
    }
    

    //construire un panier en cours d'un client
    public Panier(int idPanier, int idClient, Timestamp dateDebutPanier) {
        this.idPanier = idPanier;
        this.idClient = idClient;
        this.panierTermine = false;
        this.dateDebutPanier = dateDebutPanier;
        this.dateFinPanier = null;
    }

    //getters & setters
    public int getIdPanier() {return idPanier;}
    public int getIdClient() {return idClient;}
    public boolean isPanierTermine() {return panierTermine;}
    public void setPanierTermine(boolean panierTermine) {this.panierTermine = panierTermine;}
    public Timestamp getDateDebutPanier() {return dateDebutPanier;}
    public Timestamp getDateFinPanier() {return dateFinPanier;}

    // Savoir si le panier recherché existe bien en BD
    public boolean exists() {return this.idPanier != 0;}

    @Override
    public String toString() {
        return "Panier [id=" + idPanier + ", idClient=" + idClient
                + ", statut_panier=" + panierTermine + ", date_début=" + dateDebutPanier + ", date_fin=" + dateFinPanier
                + "]";
    }

    //US 1.3
    public void validerPanier() {

        if(this.panierTermine) {
            System.out.println("Le panier a déjà été annulé/validé.");
            return;
        }

        //transformer le panier en cours en commande
        //TODO

        //puis supprimer le panier en cours du client
        this.panierTermine = true;
    }

    //US 1.4
    public void annulerPanier() {
        if(this.panierTermine) {
            System.out.println("Le panier a déjà été annulé/validé.");
            return;
        }

        //supprimer dans la BD le panier en cours du client
        try (Connection connection = DBConnection.getConnection()){

            //on supprime les produits du panier
            String supprPanierProduits = "DELETE FROM panier_produit_magasin WHERE idPanier = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(supprPanierProduits)) {
                pstmt.setInt(1, idPanier);

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Les produits du panier ont bien été supprimés.");
                } else {
                    System.out.println("Ce client n'a pas de panier en cours, ou aucun produit dans ce panier.");
                }
                //connection.commit();
            } catch (SQLException e) {
                //rollback si erreur
                connection.rollback();
                System.out.println("Erreur lors de la suppression : " + e.getMessage());
            }

            //et on supprime le panier en cours
            String supprPanier = "DELETE FROM panier WHERE idClient = ? AND panierTermine = false;";

            try (PreparedStatement pstmt = connection.prepareStatement(supprPanier)) {
                pstmt.setInt(1, idClient);

                //exécution de la requête
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Le panier en cours du client a bien été annulé.");
                } else {
                    System.out.println("Ce client n'a pas de panier en cours.");
                }
                //connection.commit();
            } catch (SQLException e) {
                //rollback si erreur
                connection.rollback();
                System.out.println("Erreur lors de la suppression : " + e.getMessage());
            }
            connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }

        this.panierTermine = true;
    }

    //US 1.2
    public void afficherProduitsPanier(int idPanier) {
        String query1 = """
            SELECT p.idPanier, c.nomClient, c.prenomClient
            FROM panier p
            INNER JOIN client c ON p.idClient = c.idClient
            WHERE p.idPanier = ?;
        """;
        
        String query2 = """
            SELECT pr.idProduit, pr.libelleProduit, pr.prixUnitaire, ppm.quantiteVoulue, ppm.modeLivraison
            FROM ppanier_produit_magasin ppm "
            INNER JOIN produit pr ON pp.idProduit = pr.idProduit
            WHERE ppm.idPanier = ?;
        """;
    
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt1 = connection.prepareStatement(query1);
             PreparedStatement pstmt2 = connection.prepareStatement(query2)) {

            pstmt1.setInt(1, idPanier);
    
            // Exécuter première requête
            try (ResultSet rs1 = pstmt1.executeQuery()) {
                if (rs1.next()) {
                    System.out.println("Détails du panier " + idPanier);
                    System.out.println("Client: " + rs1.getString("nomClient") + " " + rs1.getString("prenomClient"));
                } else {
                    System.out.println("Aucun panier trouvé pour l'ID " + idPanier);
                }
            }

            pstmt2.setInt(1, idPanier);
    
            // Exécuter deuxième requête
            try (ResultSet rs2 = pstmt2.executeQuery()) {
                if (!rs2.isBeforeFirst()) {
                    System.out.println("Le panier ID " + idPanier + " est vide.");
                } else {
                    System.out.println("Produits dans le panier ID " + idPanier + ":");
                    while (rs2.next()) {
                        int idProduit = rs2.getInt("idProduit");
                        String libelleProduit = rs2.getString("libelleProduit");
                        double prixUnitaire = rs2.getDouble("prixUnitaire");
                        int quantite = rs2.getInt("quantiteVoulue");
                        String modeLivraison = rs2.getString("modeLivraison");
    
                        System.out.printf(
                            "ID Produit: %d, Nom: %s, Prix: %.2f, Quantité: %d, Mode de Livraison: %s%n",
                            idProduit, libelleProduit, prixUnitaire, quantite, modeLivraison
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage du panier : " + e.getMessage());
        }
    }
    
    

    /*public void ajouterProduit(int idProduit, int qte, int magasin, String modeLivraison) {
        //vérifier que le produit est disponible dans le magasin
        String queryTest = "SELECT * FROM stocker WHERE idProduit = ? AND idMagasin = ? AND quantiteEnStock >= ?;";
        try (Connection connection = DBConnection.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(queryTest);
            
            pstmt.setInt(1, idProduit);
            pstmt.setInt(2, magasin);
            pstmt.setInt(2, magasin);

            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Le produit n'est pas disponible dans ce magasin.");
                return;
            }
            connection.close();

        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification du stock : " + e.getMessage());
        }

        //ajouter le produit au panier
        String query = "INSERT INTO panier_produit (idPanier, idProduit, quantiteVoulue, modeLivraison) VALUES (?, ?, ?, ?);";
        try (Connection connection = DBConnection.getConnection()){
            PreparedStatement pstmt = connection.prepareStatement(query);
            
            pstmt.setInt(1, this.idPanier);
            pstmt.setInt(2, idProduit);
            pstmt.setInt(3, qte);
            pstmt.setString(4, modeLivraison);

            pstmt.executeUpdate();
            connection.close();
        } 

        catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du produit au panier : " + e.getMessage());
        }
    }

    //permet de récupérer les produits du panier
    public Map<Integer, Integer> getProduits() {
        return null;
    }*/




    
    

    public void ajouterProduit(int idProduit, int idPanier, int qte, int magasin, String modeLivraison) {
       
    
    
        String queryTest = "SELECT quantiteEnStock FROM stocker WHERE idProduit = ? AND idMagasin = ?;";
        String queryInsert = "INSERT INTO panier_produit_magasin (idPanier, idProduit, idMagasin, quantiteVoulue, modeLivraison) VALUES (?, ?, ?, ?, ?);";
    
        try (Connection connection = DBConnection.getConnection()) {
            // Vérification du stock
            try (PreparedStatement pstmtTest = connection.prepareStatement(queryTest)) {
                pstmtTest.setInt(1, idProduit);
                pstmtTest.setInt(2, magasin);
    
                try (ResultSet rs = pstmtTest.executeQuery()) {
                    if (rs.next()) {
                        int stockDisponible = rs.getInt("quantiteEnStock");
    
                        if (stockDisponible < qte) {
                            System.out.println("Quantité insuffisante en stock pour le produit ID: " + idProduit);
                            return;
                        }
                    } else {
                        System.out.println("Le produit ID: " + idProduit + " n'est pas disponible dans le magasin ID: " + magasin);
                        return;
                    }
                }
            }
    
            // Ajout du produit au panier
            try (PreparedStatement pstmtInsert = connection.prepareStatement(queryInsert)) {
                pstmtInsert.setInt(1, idPanier);
                pstmtInsert.setInt(2, idProduit);
                pstmtInsert.setInt(3, magasin);
                pstmtInsert.setInt(4, qte);
                pstmtInsert.setString(5, modeLivraison);
    
                int rowsAffected = pstmtInsert.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Produit ID: " + idProduit + " ajouté au panier avec succès.");
                } else {
                    System.out.println("Échec de l'ajout du produit au panier.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du produit au panier : " + e.getMessage());
        }
    }
    


//US 1.2
    public void afficherPanier() {
        String query = "SELECT ppm.idProduit, ppm.quantiteVoulue, ppm.modeLivraison, p.libelleProduit, p.prixUnitaire " +
                       "FROM panier_produit_magasin ppm " +
                       "JOIN produit p ON ppm.idProduit = p.idProduit " +
                       "WHERE ppm.idPanier = ?;";
    
        System.out.println("\n=== Contenu du panier ID " + this.idPanier + " ===");
    
        try (Connection connection = DBConnection.getConnection()) {
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, this.idPanier);
    
               // System.out.println("Exécution de la requête : " + query);
                //System.out.println("  - Paramètre 1 (idPanier) : " + this.idPanier);
    
                try (ResultSet rs = pstmt.executeQuery()) {
                    boolean panierVide = true;
    
                    while (rs.next()) {
                        panierVide = false;
                        int idProduit = rs.getInt("idProduit");
                        int quantite = rs.getInt("quantiteVoulue");
                        String modeLivraison = rs.getString("modeLivraison");
                        String nomProduit = rs.getString("libelleProduit");
                        double prixUnitaire = rs.getDouble("prixUnitaire");
                        double totalPrix = quantite * prixUnitaire;
    
                        System.out.println("Produit : " + nomProduit);
                        System.out.println("  - ID Produit : " + idProduit);
                        System.out.println("  - Quantité : " + quantite);
                        System.out.println("  - Mode de livraison : " + modeLivraison);
                        System.out.println("  - Prix unitaire : " + prixUnitaire + " €");
                        System.out.println("  - Total : " + totalPrix + " €");
                        System.out.println("-----------------------------------");
                    }
    
                    if (panierVide) {
                        System.out.println("Votre panier est vide.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("ERREUR : Une exception SQL est survenue : " + e.getMessage());
        }
    
        System.out.println("=== Fin de l'affichage du panier ===\n");
    }
    
}
